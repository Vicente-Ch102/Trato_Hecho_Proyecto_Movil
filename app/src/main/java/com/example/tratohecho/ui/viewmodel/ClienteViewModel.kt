package com.example.tratohecho.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tratohecho.data.model.Cliente
import com.example.tratohecho.data.repository.ClienteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ClienteFormState(
    val nombreError: String? = null,
    val correoError: String? = null,
    val saved: Boolean = false
) {
    val error: String? get() = nombreError ?: correoError
}

class ClienteViewModel(private val repo: ClienteRepository) : ViewModel() {
    private val tenantId = MutableStateFlow(1L)
    val clientes = tenantId.flatMapLatest(repo::observar)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val form = MutableStateFlow(ClienteFormState())
    val editando = MutableStateFlow<Cliente?>(null)
    val seleccionado = MutableStateFlow<Cliente?>(null)

    fun setTenant(id: Long) { tenantId.value = id }
    fun seleccionar(cliente: Cliente) { seleccionado.value = cliente }
    fun editar(cliente: Cliente?) { editando.value = cliente; form.value = ClienteFormState() }
    fun limpiarResultado() { form.value = ClienteFormState() }

    fun save(
        nombre: String, rut: String, contacto: String, correo: String,
        telefono: String, direccion: String, online: Boolean, propietarioCorreo: String
    ) {
        val nombreError = if (nombre.isBlank()) "El nombre o razón social es obligatorio" else null
        val correoError = if (correo.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) "Ingresa un correo válido" else null
        form.value = ClienteFormState(nombreError, correoError)
        if (nombreError != null || correoError != null) return

        viewModelScope.launch {
            val base = editando.value
            val guardado = Cliente(
                id = base?.id ?: 0,
                tenantId = tenantId.value,
                propietarioCorreo = base?.propietarioCorreo?.ifBlank { propietarioCorreo } ?: propietarioCorreo,
                nombre = nombre.trim(), rut = rut.trim(), contacto = contacto.trim(), correo = correo.trim(),
                telefono = telefono.trim(), direccion = direccion.trim()
            )
            val id = repo.guardar(guardado, online)
            seleccionado.value = guardado.copy(id = if (guardado.id == 0L) id else guardado.id)
            editando.value = null
            form.value = ClienteFormState(saved = true)
        }
    }

    fun delete(cliente: Cliente) = viewModelScope.launch { repo.eliminar(cliente) }
    fun sincronizarSiCorresponde(online: Boolean) { if (online) viewModelScope.launch { repo.sincronizarPendientes(tenantId.value) } }
    fun reintentar(id: Long, online: Boolean) = viewModelScope.launch { repo.reintentar(id, online) }
}
