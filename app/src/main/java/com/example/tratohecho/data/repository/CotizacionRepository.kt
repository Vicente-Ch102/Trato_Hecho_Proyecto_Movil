package com.example.tratohecho.data.repository

import androidx.room.withTransaction
import com.example.tratohecho.data.database.*
import com.example.tratohecho.data.model.*
import kotlinx.coroutines.delay

class CotizacionRepository(
    private val database: AppDatabase,
    private val dao: CotizacionDao,
    private val items: ItemDao
) {
    fun observar(tenant: Long) = dao.observar(tenant)
    fun observarUna(id: Long) = dao.observarUna(id)
    fun items(id: Long) = items.observar(id)
    suspend fun obtener(id: Long) = dao.obtener(id)
    suspend fun obtenerItems(id: Long) = items.obtener(id)

    suspend fun guardarConItems(cotizacion: Cotizacion, detalle: List<ItemCotizacion>, online: Boolean): Long = database.withTransaction {
        val id = dao.guardar(cotizacion.copy(sync = if (online) EstadoSync.SINCRONIZADO else EstadoSync.PENDIENTE))
        items.eliminarDeCotizacion(id)
        detalle.forEach { items.guardar(it.copy(id = 0, cotizacionId = id)) }
        id
    }

    suspend fun cambiarEstado(id: Long, estado: EstadoCotizacion) = dao.cambiarEstado(id, estado)

    suspend fun sincronizarPendientes(tenant: Long) {
        dao.pendientes(tenant).forEach { cot ->
            try {
                dao.cambiarSync(cot.id, EstadoSync.SINCRONIZANDO)
                delay(350)
                dao.cambiarSync(cot.id, EstadoSync.SINCRONIZADO)
            } catch (_: Exception) { dao.cambiarSync(cot.id, EstadoSync.ERROR) }
        }
    }

    suspend fun reintentar(id: Long, online: Boolean) {
        if (!online) { dao.cambiarSync(id, EstadoSync.PENDIENTE); return }
        try {
            dao.cambiarSync(id, EstadoSync.SINCRONIZANDO)
            delay(350)
            dao.cambiarSync(id, EstadoSync.SINCRONIZADO)
        } catch (_: Exception) { dao.cambiarSync(id, EstadoSync.ERROR) }
    }
}
