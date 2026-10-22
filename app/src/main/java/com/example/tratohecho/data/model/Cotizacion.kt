package com.example.tratohecho.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cotizaciones")
data class Cotizacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tenantId: Long = 1,
    val propietarioCorreo: String = "",
    val clienteId: Long = 0,
    val clienteNombre: String,
    val fecha: String = "",
    val vigenciaDias: Int = 15,
    val moneda: String = "CLP",
    val tipoCambio: Double = 1.0,
    val descuentoPct: Double = 0.0,
    val impuestoPct: Double = 19.0,
    val observaciones: String = "",
    val estado: EstadoCotizacion = EstadoCotizacion.BORRADOR,
    val sync: EstadoSync = EstadoSync.SINCRONIZADO
)

@Entity(tableName = "items")
data class ItemCotizacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cotizacionId: Long,
    val descripcion: String,
    val cantidad: Double,
    val unidad: String,
    val precioUnitario: Double
) {
    val subtotal: Double get() = cantidad * precioUnitario
}

data class TotalesCotizacion(
    val subtotal: Double,
    val descuento: Double,
    val neto: Double,
    val impuesto: Double,
    val total: Double
)

fun calcularTotales(
    items: List<ItemCotizacion>,
    descuentoPct: Double,
    impuestoPct: Double
): TotalesCotizacion {
    val subtotal = items.sumOf { it.subtotal }
    val descuento = subtotal * (descuentoPct.coerceIn(0.0, 100.0) / 100.0)
    val neto = subtotal - descuento
    val impuesto = neto * (impuestoPct.coerceAtLeast(0.0) / 100.0)
    return TotalesCotizacion(subtotal, descuento, neto, impuesto, neto + impuesto)
}
