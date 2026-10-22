package com.example.tratohecho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tratohecho.data.model.*
import com.example.tratohecho.data.repository.CotizacionRepository
import com.example.tratohecho.data.repository.FacturacionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FacturacionViewModel(private val repo: FacturacionRepository, private val cotizaciones: CotizacionRepository) : ViewModel() {
    private val tenantId = MutableStateFlow(1L)
    val ordenes = tenantId.flatMapLatest(repo::ordenes).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val facturas = tenantId.flatMapLatest(repo::facturas).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun setTenant(id: Long) { tenantId.value = id }

    fun guardarOrden(cotizacionId: Long, numero: String, fecha: String, observaciones: String): String? {
        if (cotizacionId <= 0 || numero.isBlank() || !fecha.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) return "Completa número y fecha con formato AAAA-MM-DD"
        viewModelScope.launch { repo.guardarOrden(OrdenCompra(tenantId = tenantId.value, cotizacionId = cotizacionId, numero = numero.trim(), fecha = fecha.trim(), observaciones = observaciones.trim())) }
        return null
    }

    fun guardarFactura(cotizacionId: Long, numero: String, fecha: String, observaciones: String): String? {
        if (cotizacionId <= 0 || numero.isBlank() || !fecha.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) return "Completa número y fecha con formato AAAA-MM-DD"
        viewModelScope.launch {
            repo.guardarFactura(Factura(tenantId = tenantId.value, cotizacionId = cotizacionId, numero = numero.trim(), fecha = fecha.trim(), observaciones = observaciones.trim()))
            cotizaciones.cambiarEstado(cotizacionId, EstadoCotizacion.FACTURADA)
        }
        return null
    }
}
