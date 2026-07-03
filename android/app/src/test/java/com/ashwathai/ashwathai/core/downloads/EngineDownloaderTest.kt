package com.ashwathai.ashwathai.core.downloads

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class EngineDownloaderTest {

    @Test
    fun `downloadFile emits Complete on success`() = runTest {
        val mockHttpClient = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    respond(
                        content = ByteReadChannel("binary content"),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/octet-stream")
                    )
                }
            }
        }

        val downloader = EngineDownloader(mockHttpClient)
        val destFile = File.createTempFile("test", "bin")

        try {
            downloader.downloadFile("https://example.com/file", destFile)
            assertEquals(DownloadState.Complete, downloader.downloadState.value)
            assertEquals("binary content", destFile.readText())
        } finally {
            destFile.delete()
        }
    }

    @Test
    fun `downloadFile emits Failed on error`() = runTest {
        val mockHttpClient = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    respond(
                        content = "",
                        status = HttpStatusCode.NotFound
                    )
                }
            }
        }

        val downloader = EngineDownloader(mockHttpClient)
        val destFile = File.createTempFile("test", "bin")

        try {
            downloader.downloadFile("https://example.com/file", destFile)
            assertTrue(downloader.downloadState.value is DownloadState.Failed)
        } finally {
            destFile.delete()
        }
    }
}
