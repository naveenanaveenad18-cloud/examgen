package com.example.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.models.PaperSummaryDto
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    fun generateAndOpenPdf(
        context: Context,
        paper: PaperSummaryDto
    ) {
        try {
            val pdfDir = File(context.cacheDir, "pdfs")
            if (!pdfDir.exists()) {
                pdfDir.mkdirs()
            }
            val safeSubject = paper.subject.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val fileName = "Exam_${safeSubject}_${System.currentTimeMillis()}.pdf"
            val file = File(pdfDir, fileName)

            val pdfDocument = PdfDocument()
            val pageWidth = 595 // Standard A4 points at 72dpi
            val pageHeight = 842
            val margin = 40

            // Header paints
            val titlePaint = TextPaint().apply {
                color = Color.rgb(26, 35, 126) // Deep Indigo
                textSize = 16f
                isFakeBoldText = true
                isAntiAlias = true
            }

            val metaPaint = TextPaint().apply {
                color = Color.rgb(71, 85, 105)
                textSize = 10f
                isAntiAlias = true
            }

            val bodyPaint = TextPaint().apply {
                color = Color.rgb(15, 23, 42)
                textSize = 10.5f
                isAntiAlias = true
            }

            val linePaint = Paint().apply {
                color = Color.rgb(203, 213, 225)
                strokeWidth = 1f
            }

            // Page 1 setup
            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            // Draw Header
            var yPos = margin.toFloat() + 20f
            canvas.drawText("EXAMGEN - QUESTION PAPER", margin.toFloat(), yPos, titlePaint)
            yPos += 20f

            val subjectLine = "Subject: ${paper.subject}   |   Class/Grade: ${paper.grade}   |   Difficulty: ${paper.difficulty}"
            canvas.drawText(subjectLine, margin.toFloat(), yPos, metaPaint)
            yPos += 14f

            val marksLine = "Time Allowed: ${paper.duration} Minutes   |   Maximum Marks: ${paper.marks} Marks"
            canvas.drawText(marksLine, margin.toFloat(), yPos, metaPaint)
            yPos += 16f

            canvas.drawLine(margin.toFloat(), yPos, (pageWidth - margin).toFloat(), yPos, linePaint)
            yPos += 20f

            // Format body content lines
            val contentLines = paper.content.split("\n")
            val contentWidth = pageWidth - (margin * 2)

            for (line in contentLines) {
                val textToDraw = if (line.trim().isEmpty()) " " else line
                val isHeading = textToDraw.startsWith("SECTION") || textToDraw.startsWith("PART") || textToDraw.startsWith("Instructions:")
                val paintToUse = if (isHeading) {
                    TextPaint().apply {
                        color = Color.rgb(30, 41, 59)
                        textSize = 11.5f
                        isFakeBoldText = true
                        isAntiAlias = true
                    }
                } else {
                    bodyPaint
                }

                val staticLayout = StaticLayout.Builder
                    .obtain(textToDraw, 0, textToDraw.length, paintToUse, contentWidth)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(2f, 1.1f)
                    .build()

                if (yPos + staticLayout.height > pageHeight - margin - 30) {
                    // Draw footer on current page
                    canvas.drawText("Page $pageNumber", (pageWidth / 2 - 15).toFloat(), (pageHeight - 20).toFloat(), metaPaint)
                    pdfDocument.finishPage(page)

                    // Start new page
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = margin.toFloat() + 20f
                }

                canvas.save()
                canvas.translate(margin.toFloat(), yPos)
                staticLayout.draw(canvas)
                canvas.restore()

                yPos += staticLayout.height + 4f
            }

            // Draw final page footer
            canvas.drawText("Page $pageNumber", (pageWidth / 2 - 15).toFloat(), (pageHeight - 20).toFloat(), metaPaint)
            pdfDocument.finishPage(page)

            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            // Open the generated PDF file via FileProvider
            openPdfFile(context, file, paper.title)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Could not generate PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun openPdfFile(context: Context, file: File, title: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Open $title").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "PDF saved to ${file.name}. Install a PDF viewer or view preview in app.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun openUrlOrFile(context: Context, urlOrContent: String, paper: PaperSummaryDto) {
        if (urlOrContent.startsWith("http://") || urlOrContent.startsWith("https://")) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlOrContent)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
                return
            } catch (e: Exception) {
                // fallback to local PDF generation
            }
        }
        generateAndOpenPdf(context, paper)
    }
}
