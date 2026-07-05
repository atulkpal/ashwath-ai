package com.ashwathai.ashwathai.data.download

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
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
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 30000
            connection.connect()

            val contentLength = connection.contentLengthLong
            val input = connection.inputStream.buffered()
            val output = FileOutputStream(tempFile)

            var downloaded = 0L
            val buffer = ByteArray(32 * 1024)

            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                output.write(buffer, 0, read)
                downloaded += read

                val total = if (contentLength > 0) contentLength else downloaded
                emit(DownloadProgress(
                    bytesDownloaded = downloaded,
                    totalBytes = total,
                    fraction = if (total > 0) downloaded.toFloat() / total else 0f,
                ))
            }

            input.close()
            output.close()
            connection.disconnect()

            if (tempFile.exists() && tempFile.length() > 0) {
                tempFile.renameTo(destFile)
                emit(DownloadProgress(
                    bytesDownloaded = destFile.length(),
                    totalBytes = destFile.length(),
                    fraction = 1f,
                ))
            }
        } catch (e: Exception) {
            if (tempFile.exists()) tempFile.delete()
            throw e
        }
    }
}
