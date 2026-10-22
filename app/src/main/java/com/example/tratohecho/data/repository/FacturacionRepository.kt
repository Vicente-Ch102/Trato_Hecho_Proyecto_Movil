package com.example.tratohecho.data.repository

import com.example.tratohecho.data.database.FacturacionDao
import com.example.tratohecho.data.model.*

class FacturacionRepository(private val dao: FacturacionDao) {
    fun ordenes(t: Long) = dao.ordenes(t)
    fun facturas(t: Long) = dao.facturas(t)
    suspend fun guardarOrden(x: OrdenCompra) = dao.guardarOrden(x)
    suspend fun guardarFactura(x: Factura) = dao.guardarFactura(x)
}
