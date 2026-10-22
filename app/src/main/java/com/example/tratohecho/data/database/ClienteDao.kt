package com.example.tratohecho.data.database

import androidx.room.*
import com.example.tratohecho.data.model.Cliente
import com.example.tratohecho.data.model.EstadoSync
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes WHERE tenantId=:tenant ORDER BY nombre")
    fun observar(tenant: Long): Flow<List<Cliente>>

    @Query("SELECT COUNT(*) FROM clientes WHERE tenantId=:tenant")
    suspend fun contar(tenant: Long): Int

    @Query("SELECT * FROM clientes WHERE id=:id LIMIT 1")
    suspend fun obtener(id: Long): Cliente?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(x: Cliente): Long

    @Delete
    suspend fun eliminar(x: Cliente)

    @Query("SELECT * FROM clientes WHERE tenantId=:tenant AND sync != 'SINCRONIZADO'")
    suspend fun pendientes(tenant: Long): List<Cliente>

    @Query("UPDATE clientes SET sync=:estado WHERE id=:id")
    suspend fun cambiarSync(id: Long, estado: EstadoSync)
}
