package com.example.tratohecho.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.tratohecho.data.model.Cotizacion
import com.example.tratohecho.data.model.ItemCotizacion
import com.example.tratohecho.data.model.calcularTotales
import java.text.NumberFormat
import java.util.Locale

object CotizacionPdfExporter {
    fun exportar(context: Context, uri: Uri, cotizacion: Cotizacion, items: List<ItemCotizacion>) {
        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { textSize = 13f }
        val bold = Paint().apply { textSize = 18f; isFakeBoldText = true }
        val money = NumberFormat.getNumberInstance(Locale("es", "CL"))
        var y = 55f

        canvas.drawText("Trato Hecho - Cotización COT-${cotizacion.id}", 40f, y, bold); y += 34f
        canvas.drawText("Cliente: ${cotizacion.clienteNombre}", 40f, y, paint); y += 22f
        canvas.drawText("Fecha: ${cotizacion.fecha}   Vigencia: ${cotizacion.vigenciaDias} días", 40f, y, paint); y += 22f
        canvas.drawText("Moneda: ${cotizacion.moneda}", 40f, y, paint); y += 30f

        items.forEachIndexed { index, item ->
            if (y > 740f) return@forEachIndexed
            canvas.drawText("${index + 1}. ${item.descripcion}", 40f, y, paint); y += 18f
            canvas.drawText("${item.cantidad} ${item.unidad} x ${money.format(item.precioUnitario)} = ${money.format(item.subtotal)}", 60f, y, paint); y += 24f
        }

        val t = calcularTotales(items, cotizacion.descuentoPct, cotizacion.impuestoPct)
        y += 10f
        canvas.drawText("Subtotal: ${money.format(t.subtotal)} ${cotizacion.moneda}", 40f, y, paint); y += 20f
        canvas.drawText("Descuento (${cotizacion.descuentoPct}%): ${money.format(t.descuento)}", 40f, y, paint); y += 20f
        canvas.drawText("Impuesto (${cotizacion.impuestoPct}%): ${money.format(t.impuesto)}", 40f, y, paint); y += 24f
        canvas.drawText("TOTAL: ${money.format(t.total)} ${cotizacion.moneda}", 40f, y, bold)

        pdf.finishPage(page)
        context.contentResolver.openOutputStream(uri)?.use { pdf.writeTo(it) }
        pdf.close()
    }
}
