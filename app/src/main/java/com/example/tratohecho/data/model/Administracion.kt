package com.example.tratohecho.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tipos_unidad")
data class TipoUnidad(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tenantId: Long = 1,
    val nombre: String,
    val codigo: String,
    val activo: Boolean = true
)

@Entity(tableName = "impuestos")
data class Impuesto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tenantId: Long = 1,
    val nombre: String,
    val porcentaje: Double,
    val activo: Boolean = true
)

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tenantId: Long = 1,
    val nombre: String,
    val correo: String,
    val perfil: Perfil,
    val activo: Boolean = true,
    val supervisorCorreo: String = ""
)
