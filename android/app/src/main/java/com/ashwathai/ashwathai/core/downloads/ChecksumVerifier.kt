package com.ashwathai.ashwathai.core.downloads

import java.io.File
import java.security.MessageDigest

class ChecksumVerifier {
    fun verifyChecksum(file: File, expectedChecksum: String): Boolean {
        if (!file.exists()) return false

        val sha256 = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var bytesRead = input.read(buffer)
            while (bytesRead != -1) {
                sha256.update(buffer, 0, bytesRead)
                bytesRead = input.read(buffer)
            }
        }

        val hashBytes = sha256.digest()
        val actualChecksum = hashBytes.joinToString("") { "%02x".format(it) }

        return actualChecksum.equals(expectedChecksum, ignoreCase = true)
    }

    fun parseChecksums(checksumsFileContent: String, fileName: String): String? {
        return checksumsFileContent.lines().find { it.contains(fileName) }?.split(" ")?.firstOrNull()
    }
}
