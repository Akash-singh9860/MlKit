package com.app.mlkit.domain.useCase

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import com.app.mlkit.domain.model.ScannedDocument
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CreatePdfUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(document: ScannedDocument): String {
        val pdfDocument = PdfDocument()
        val bitmap = BitmapFactory.decodeFile(document.imagePath)
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width,
            bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)
        val pdfFile = File(context.getExternalFilesDir(null),
            "document_${System.currentTimeMillis()}.pdf")
        val outputStream = FileOutputStream(pdfFile)
        pdfDocument.writeTo(outputStream)
        outputStream.close()
        pdfDocument.close()
        return pdfFile.absolutePath
    }
}