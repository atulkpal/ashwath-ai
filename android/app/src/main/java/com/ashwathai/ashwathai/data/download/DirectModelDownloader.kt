package com.ashwathai.ashwathai.data.download

import android.util.Log
import com.ashwathai.ashwathai.core.HfTokenProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DirectModelDownloader {

    data class DownloadProgress(
        val bytesDownloaded: Long,
        val totalBytes: Long,
        val fraction: Float,
    )

    fun download(url: String, destFile: File): Flow<DownloadProgress> = flow {
        val tempFile = File(destFile.parentFile, destFile.name + ".tmp")
        Log.d("Downloader", "Starting: $url -> $destFile")

        if (tempFile.exists()) tempFile.delete()
        destFile.parentFile?.mkdirs()

        val connection = URL(url).openConnection() as HttpURLConnection
        connection.setRequestProperty("User-Agent", "AshwathAI/0.1 (Android)")
        connection.setRequestProperty("Accept", "*/*")
        val token = HfTokenProvider.getToken()
        if (token.isNotBlank()) {
            connection.setRequestProperty("Authorization", "Bearer $token")
            Log.d("Downloader", "Using HF token: ${token.take(8)}...")
        }
        connection.connectTimeout = 30000
        connection.readTimeout = 120000
        connection.instanceFollowRedirects = true

        try {
            connection.connect()
            val code = connection.responseCode
            Log.d("Downloader", "HTTP $code")

            if (code == 401) {
                throw Exception("Hugging Face requires authentication.\nGet a token at huggingface.co/settings/tokens,\nthen add it in Settings → Hugging Face Token")
            }
            if (code != 200) {
                val err = connection.errorStream?.bufferedReader()?.readText() ?: ""
                Log.e("Downloader", "HTTP $code response: $err")
                throw Exception("Download failed (HTTP $code)")
            }

            val totalBytes = connection.contentLengthLong
            val input = connection.inputStream
            val output = FileOutputStream(tempFile)

            var downloaded = 0L
            val buf = ByteArray(32 * 1024)

            while (true) {
                val read = input.read(buf)
                if (read < 0) break
                output.write(buf, 0, read)
                downloaded += read
                val total = if (totalBytes > 0) totalBytes else downloaded
                emit(DownloadProgress(downloaded, total, downloaded.toFloat() / total))
            }

            input.close()
            output.close()
            connection.disconnect()
            Log.d("Downloader", "Complete: $downloaded bytes")

        } catch (e: Exception) {
            Log.e("Downloader", "Failed: ${e.message}", e)
            tempFile.delete()
            connection.disconnect()
            throw e
        }

        if (tempFile.exists() && tempFile.length() > 0) {
            tempFile.renameTo(destFile)
            emit(DownloadProgress(destFile.length(), destFile.length(), 1f))
        }
    }
}
