package com.example.tratohecho.data.database

import androidx.room.*
import com.example.tratohecho.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminDao {
    @Query("SELECT * FROM tipos_unidad WHERE tenantId=:tenant ORDER BY nombre")
    fun unidades(tenant: Long): Flow<List<TipoUnidad>>
    @Query("SELECT COUNT(*) FROM tipos_unidad WHERE tenantId=:tenant") suspend fun contarUnidades(tenant: Long): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun guardarUnidad(x: TipoUnidad): Long
    @Delete suspend fun eliminarUnidad(x: TipoUnidad)

    @Query("SELECT * FROM impuestos WHERE tenantId=:tenant ORDER BY nombre")
    fun impuestos(tenant: Long): Flow<List<Impuesto>>
    @Query("SELECT COUNT(*) FROM impuestos WHERE tenantId=:tenant") suspend fun contarImpuestos(tenant: Long): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun guardarImpuesto(x: Impuesto): Long
    @Delete suspend fun eliminarImpuesto(x: Impuesto)

    @Query("SELECT * FROM usuarios WHERE tenantId=:tenant ORDER BY nombre")
    fun usuarios(tenant: Long): Flow<List<Usuario>>
    @Query("SELECT COUNT(*) FROM usuarios WHERE tenantId=:tenant") suspend fun contarUsuarios(tenant: Long): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun guardarUsuario(x: Usuario): Long
    @Delete suspend fun eliminarUsuario(x: Usuario)
}
