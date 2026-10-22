package com.example.tratohecho.data.database

import androidx.room.*
import com.example.tratohecho.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CotizacionDao {
    @Query("SELECT * FROM cotizaciones WHERE tenantId=:tenant ORDER BY id DESC")
    fun observar(tenant: Long): Flow<List<Cotizacion>>

    @Query("SELECT COUNT(*) FROM cotizaciones WHERE tenantId=:tenant")
    suspend fun contar(tenant: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(x: Cotizacion): Long

    @Query("SELECT * FROM cotizaciones WHERE id=:id LIMIT 1")
    fun observarUna(id: Long): Flow<Cotizacion?>

    @Query("SELECT * FROM cotizaciones WHERE id=:id LIMIT 1")
    suspend fun obtener(id: Long): Cotizacion?

    @Query("UPDATE cotizaciones SET estado=:estado WHERE id=:id")
    suspend fun cambiarEstado(id: Long, estado: EstadoCotizacion)

    @Query("UPDATE cotizaciones SET sync=:estado WHERE id=:id")
    suspend fun cambiarSync(id: Long, estado: EstadoSync)

    @Query("SELECT * FROM cotizaciones WHERE tenantId=:tenant AND sync != 'SINCRONIZADO'")
    suspend fun pendientes(tenant: Long): List<Cotizacion>
}

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE cotizacionId=:id ORDER BY id")
    fun observar(id: Long): Flow<List<ItemCotizacion>>

    @Query("SELECT * FROM items WHERE cotizacionId=:id ORDER BY id")
    suspend fun obtener(id: Long): List<ItemCotizacion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(x: ItemCotizacion): Long

    @Query("DELETE FROM items WHERE cotizacionId=:cotizacionId")
    suspend fun eliminarDeCotizacion(cotizacionId: Long)
}
