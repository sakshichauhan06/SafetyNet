package com.example.safetynet.utils

import android.R.attr.textSize
import android.content.ContentValues
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.graphics.Paint
import com.example.safetynet.data.SafetyPin
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object IncidentPdfGenerator {

    suspend fun generateIncidentPdf(
        context: Context,
        pin: SafetyPin
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595  // A4 width in points (72 dpi)
            val pageHeight = 842 // A4 height

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply { color = android.graphics.Color.BLACK }
            val titlePaint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 24f
                isFakeBoldText = true
            }
            val labelPaint = Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 12f
            }

            var yPosition = 50f

            // Title
            canvas.drawText("SAFETYNET INCIDENT REPORT", 50f, yPosition, titlePaint)
            yPosition += 40f

            // Severity
            canvas.drawText("SEVERITY: ${pin.severity.displayName.uppercase()}", 50f, yPosition, paint)
            yPosition += 30f

            // Date
            val date = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
                .format(Date(pin.timestamp))
            canvas.drawText("REPORTED: $date", 50f, yPosition, paint)
            yPosition += 30f

            // Description
            canvas.drawText("DESCRIPTION:", 50f, yPosition, labelPaint)
            yPosition += 20f

            // Wrap text manually or use StaticLayout
            val textPaint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 14f
            }

            val words = pin.detailedDescription.split(" ")
            var line = ""
            for (word in words) {
                val testLine = if (line.isEmpty()) word else "$line $word"
                if (textPaint.measureText(testLine) > pageWidth - 100) {
                    canvas.drawText(line, 50f, yPosition, textPaint)
                    yPosition += 20f
                    line = word
                } else {
                    line = testLine
                }
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line, 50f, yPosition, textPaint)
            }

            pdfDocument.finishPage(page)

            // Save
            val fileName = "incident_${pin.id.take(8)}_${System.currentTimeMillis()}.pdf"
            val uri = savePdf(context, pdfDocument, fileName)

            pdfDocument.close()
            Result.success(uri ?: "Saved")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun savePdf(context: Context, pdfDocument: PdfDocument, fileName: String): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { os -> pdfDocument.writeTo(os) }
                uri.toString()
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { pdfDocument.writeTo(it) }
            file.absolutePath
        }
    }
}