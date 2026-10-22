package com.example.tratohecho.data.repository

import com.example.tratohecho.data.database.ClienteDao
import com.example.tratohecho.data.model.*
import kotlinx.coroutines.delay

class ClienteRepository(private val dao: ClienteDao) {
    fun observar(tenant: Long) = dao.observar(tenant)
    suspend fun obtener(id: Long) = dao.obtener(id)

    suspend fun guardar(x: Cliente, online: Boolean): Long = dao.guardar(
        x.copy(sync = if (online) EstadoSync.SINCRONIZADO else EstadoSync.PENDIENTE)
    )

    suspend fun eliminar(x: Cliente) = dao.eliminar(x)

    suspend fun sincronizarPendientes(tenant: Long) {
        dao.pendientes(tenant).forEach { cliente ->
            try {
                dao.cambiarSync(cliente.id, EstadoSync.SINCRONIZANDO)
                delay(350)
                // Punto de sustitución por la API privada cuando se entregue su contrato.
                dao.cambiarSync(cliente.id, EstadoSync.SINCRONIZADO)
            } catch (_: Exception) {
                dao.cambiarSync(cliente.id, EstadoSync.ERROR)
            }
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
