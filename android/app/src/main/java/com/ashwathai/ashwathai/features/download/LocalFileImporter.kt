package com.ashwathai.ashwathai.features.download

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object LocalFileImporter {

    fun importModel(context: Context, uri: Uri, modelsDir: File): File? {
        modelsDir.mkdirs()
        val filename = "imported-${System.currentTimeMillis()}.gguf"
        val dest = File(modelsDir, filename)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(dest).use { output ->
                input.copyTo(output)
            }
        }

        return if (dest.exists() && dest.length() > 0) dest else null
    }
}
