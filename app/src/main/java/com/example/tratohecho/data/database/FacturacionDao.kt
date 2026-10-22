package com.example.tratohecho.data.database

import androidx.room.*
import com.example.tratohecho.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FacturacionDao {
    @Query("SELECT * FROM ordenes WHERE tenantId=:tenant ORDER BY id DESC")
    fun ordenes(tenant: Long): Flow<List<OrdenCompra>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarOrden(x: OrdenCompra): Long

    @Query("SELECT * FROM facturas WHERE tenantId=:tenant ORDER BY id DESC")
    fun facturas(tenant: Long): Flow<List<Factura>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarFactura(x: Factura): Long
}
