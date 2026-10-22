package com.example.tratohecho.data.repository

import com.example.tratohecho.data.database.AdminDao
import com.example.tratohecho.data.model.*

class AdminRepository(private val dao: AdminDao) {
    fun unidades(t: Long) = dao.unidades(t)
    fun impuestos(t: Long) = dao.impuestos(t)
    fun usuarios(t: Long) = dao.usuarios(t)
    suspend fun guardarUnidad(x: TipoUnidad) = dao.guardarUnidad(x)
    suspend fun guardarImpuesto(x: Impuesto) = dao.guardarImpuesto(x)
    suspend fun guardarUsuario(x: Usuario) = dao.guardarUsuario(x)
    suspend fun eliminarUnidad(x: TipoUnidad) = dao.eliminarUnidad(x)
    suspend fun eliminarImpuesto(x: Impuesto) = dao.eliminarImpuesto(x)
    suspend fun eliminarUsuario(x: Usuario) = dao.eliminarUsuario(x)
}
