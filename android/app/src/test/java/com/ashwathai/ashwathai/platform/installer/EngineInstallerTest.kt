package com.ashwathai.ashwathai.platform.installer

import android.content.Context
import com.ashwathai.ashwathai.core.downloads.ChecksumVerifier
import com.ashwathai.ashwathai.core.downloads.DownloadState
import com.ashwathai.ashwathai.core.downloads.EngineDownloader
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class EngineInstallerTest {
    private val context = mockk<Context>(relaxed = true)
    private val downloader = mockk<EngineDownloader>()
    private val verifier = mockk<ChecksumVerifier>()

    @Test
    fun `install fails if downloader fails`() = runTest {
        val downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
        every { downloader.downloadState } returns downloadState
        every { downloader.updateState(any()) } just Runs
        coEvery { downloader.downloadFile(any(), any()) } coAnswers {
            downloadState.value = DownloadState.Failed("Error")
        }

        val mockHttpClient = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    respond("checksums", HttpStatusCode.OK)
                }
            }
        }

        every { context.filesDir } returns File("/tmp/files")
        every { context.cacheDir } returns File("/tmp/cache")

        val installer = EngineInstaller(context, downloader, verifier, mockHttpClient)
        val result = installer.install(abiOverride = "arm64-v8a")

        assertTrue(result.isFailure)
    }
}
