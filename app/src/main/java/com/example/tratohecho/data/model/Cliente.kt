package com.example.tratohecho.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class Cliente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tenantId: Long = 1,
    val propietarioCorreo: String = "",
    val nombre: String,
    val rut: String = "",
    val contacto: String = "",
    val correo: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val sync: EstadoSync = EstadoSync.SINCRONIZADO
)
