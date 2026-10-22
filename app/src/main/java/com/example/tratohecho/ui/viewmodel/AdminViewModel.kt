package com.example.tratohecho.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tratohecho.data.model.*
import com.example.tratohecho.data.repository.AdminRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel(private val repo: AdminRepository) : ViewModel() {
    private val tenantId = MutableStateFlow(1L)
    val unidades = tenantId.flatMapLatest(repo::unidades).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val impuestos = tenantId.flatMapLatest(repo::impuestos).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val usuarios = tenantId.flatMapLatest(repo::usuarios).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTenant(id: Long) { tenantId.value = id }

    fun guardarUnidad(base: TipoUnidad?, nombre: String, codigo: String, activo: Boolean): String? {
        if (nombre.isBlank()) return "El nombre es obligatorio"
        if (codigo.isBlank()) return "El código es obligatorio"
        viewModelScope.launch {
            repo.guardarUnidad(
                TipoUnidad(
                    id = base?.id ?: 0,
                    tenantId = tenantId.value,
                    nombre = nombre.trim(),
                    codigo = codigo.trim().uppercase(),
                    activo = activo
                )
            )
        }
        return null
    }

    fun alternarUnidad(x: TipoUnidad) = viewModelScope.launch { repo.guardarUnidad(x.copy(activo = !x.activo)) }

    fun guardarImpuesto(base: Impuesto?, nombre: String, porcentaje: String, activo: Boolean): String? {
        val p = porcentaje.replace(',', '.').toDoubleOrNull()
        return when {
            nombre.isBlank() -> "El nombre es obligatorio"
            p == null -> "Ingresa un porcentaje válido"
            p !in 0.0..100.0 -> "El valor debe estar entre 0 y 100"
            else -> {
                viewModelScope.launch {
                    repo.guardarImpuesto(Impuesto(id = base?.id ?: 0, tenantId = tenantId.value, nombre = nombre.trim(), porcentaje = p, activo = activo))
                }
                null
            }
        }
    }

    fun alternarImpuesto(x: Impuesto) = viewModelScope.launch { repo.guardarImpuesto(x.copy(activo = !x.activo)) }

    fun guardarUsuario(base: Usuario?, nombre: String, correo: String, perfil: Perfil, activo: Boolean, supervisorCorreo: String): String? {
        return when {
            nombre.isBlank() -> "El nombre es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> "Ingresa un correo válido"
            perfil == Perfil.USUARIO && supervisorCorreo.isBlank() -> "Selecciona un supervisor para el usuario"
            else -> {
                viewModelScope.launch {
                    repo.guardarUsuario(
                        Usuario(
                            id = base?.id ?: 0, tenantId = tenantId.value, nombre = nombre.trim(), correo = correo.trim(),
                            perfil = perfil, activo = activo,
                            supervisorCorreo = if (perfil == Perfil.USUARIO) supervisorCorreo.trim() else ""
                        )
                    )
                }
                null
            }
        }
    }

    fun alternarUsuario(x: Usuario) = viewModelScope.launch { repo.guardarUsuario(x.copy(activo = !x.activo)) }
}
