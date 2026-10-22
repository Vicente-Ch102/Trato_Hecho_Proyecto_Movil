package com.example.tratohecho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tratohecho.data.model.IndicadorEconomico
import com.example.tratohecho.data.repository.IndicadorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class IndicadoresState(
    val dolar: IndicadorEconomico? = null,
    val uf: IndicadorEconomico? = null,
    val cargando: Boolean = false,
    val error: String? = null
)

class IndicadoresViewModel(private val repo: IndicadorRepository) : ViewModel() {
    val state = MutableStateFlow(IndicadoresState())

    fun cargar(online: Boolean) {
        if (!online) {
            state.value = state.value.copy(error = "Se requiere conexión para actualizar USD/UF")
            return
        }
        viewModelScope.launch {
            state.value = state.value.copy(cargando = true, error = null)
            val dolar = repo.obtener("dolar")
            val uf = repo.obtener("uf")
            state.value = IndicadoresState(
                dolar = dolar.getOrNull(),
                uf = uf.getOrNull(),
                cargando = false,
                error = dolar.exceptionOrNull()?.message ?: uf.exceptionOrNull()?.message
            )
        }
    }
}
