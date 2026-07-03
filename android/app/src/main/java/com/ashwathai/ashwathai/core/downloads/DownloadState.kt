package com.ashwathai.ashwathai.core.downloads

sealed class DownloadState {
    object Idle : DownloadState()
    data class Downloading(val progress: Float) : DownloadState()
    object Verifying : DownloadState()
    object Complete : DownloadState()
    data class Failed(val error: String) : DownloadState()
}
