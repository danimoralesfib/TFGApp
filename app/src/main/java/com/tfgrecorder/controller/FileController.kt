package com.tfgrecorder.controller

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File


class FileControllerImpl(private val context: Context): FileController {

    override fun viewPdf(): Result<Unit> {
        val pdfFile = File("${context.getExternalFilesDir("/")?.absolutePath}/score_generated.pdf")

        val path: Uri = FileProvider.getUriForFile(context, "com.tfgrecorder.provider", pdfFile)
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(path, "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION


        return try {
            context.startActivity(pdfIntent)
            Result.success(Unit)
        } catch (e: ActivityNotFoundException) {
            Result.failure(e)
        } catch (e: java.lang.Exception) {
            Result.failure(e)
        }
    }

    override fun sharePdf(): Result<Unit> {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        val fileWithinMyDir = File("${context.getExternalFilesDir("/")?.absolutePath}/score_generated.pdf")

        return if (fileWithinMyDir.exists()) {
            intentShareFile.type = "application/pdf"
            intentShareFile.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(context, "com.tfgrecorder.provider", fileWithinMyDir)
            )
            intentShareFile.putExtra(
                Intent.EXTRA_SUBJECT,
                "Sharing File..."
            )
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...")
            context.startActivity(Intent.createChooser(intentShareFile, "Share File"))
            Result.success(Unit)
        } else {
            Result.failure(Exception())
        }
    }
}

interface FileController {
    fun viewPdf(): Result<Unit>
    fun sharePdf(): Result<Unit>
}