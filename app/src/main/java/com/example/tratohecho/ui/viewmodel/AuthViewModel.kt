package com.example.tratohecho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tratohecho.data.datastore.Session
import com.example.tratohecho.data.datastore.SessionDataStore
import com.example.tratohecho.data.model.Perfil
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CuentaDemo(val nombre: String, val correo: String, val perfil: Perfil, val tenantId: Long = 1)

class AuthViewModel(private val ds: SessionDataStore) : ViewModel() {
    val session = ds.session.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Session())

    val cuentasDemo = listOf(
        CuentaDemo("Carlos", "admin@tratohecho.cl", Perfil.ADMINISTRADOR),
        CuentaDemo("Ana", "supervisor@tratohecho.cl", Perfil.SUPERVISOR),
        CuentaDemo("Juan", "usuario@tratohecho.cl", Perfil.USUARIO),
        CuentaDemo("María", "facturacion@tratohecho.cl", Perfil.FACTURACION),
        CuentaDemo("Admin Empresa B", "admin.b@tratohecho.cl", Perfil.ADMINISTRADOR, tenantId = 2)
    )

    fun login(email: String, password: String): Boolean {
        val cuenta = cuentasDemo.firstOrNull { it.correo.equals(email.trim(), ignoreCase = true) }
        if (cuenta == null || password != "123456") return false
        viewModelScope.launch { ds.login(cuenta.nombre, cuenta.correo, cuenta.perfil, cuenta.tenantId) }
        return true
    }

    fun logout() = viewModelScope.launch { ds.logout() }
}
