package com.example.tratohecho.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.tratohecho.data.model.Perfil
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "session"
)

data class Session(
    val logged: Boolean = false,
    val nombre: String = "",
    val correo: String = "",
    val perfil: Perfil = Perfil.USUARIO,
    val tenantId: Long = 1
)

class SessionDataStore(
    private val context: Context
) {

    private val LOGGED = booleanPreferencesKey("logged")
    private val NOMBRE = stringPreferencesKey("nombre")
    private val CORREO = stringPreferencesKey("correo")
    private val PERFIL = stringPreferencesKey("perfil")
    private val TENANT = longPreferencesKey("tenant")

    val session = context.dataStore.data.map { preferences ->

        Session(
            logged = preferences[LOGGED] ?: false,
            nombre = preferences[NOMBRE] ?: "",
            correo = preferences[CORREO] ?: "",
            perfil = runCatching {
                Perfil.valueOf(
                    preferences[PERFIL] ?: "USUARIO"
                )
            }.getOrDefault(Perfil.USUARIO),
            tenantId = preferences[TENANT] ?: 1
        )
    }

    suspend fun login(
        nombre: String,
        correo: String,
        perfil: Perfil,
        tenantId: Long
    ) {
        context.dataStore.edit { preferences ->

            preferences[LOGGED] = true
            preferences[NOMBRE] = nombre
            preferences[CORREO] = correo
            preferences[PERFIL] = perfil.name
            preferences[TENANT] = tenantId
        }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}