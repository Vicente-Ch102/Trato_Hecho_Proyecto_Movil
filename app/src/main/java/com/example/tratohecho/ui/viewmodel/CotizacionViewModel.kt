package com.example.tratohecho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tratohecho.data.model.*
import com.example.tratohecho.data.repository.CotizacionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CotizacionFormState(val error: String? = null, val saved: Boolean = false)

data class CotizacionDraftMeta(
    val cliente: Cliente,
    val moneda: String,
    val descuentoPct: Double,
    val impuestoPct: Double,
    val vigenciaDias: Int,
    val observaciones: String,
    val tipoCambio: Double = 1.0
)

class CotizacionViewModel(private val repo: CotizacionRepository) : ViewModel() {
    private val tenantId = MutableStateFlow(1L)
    val cotizaciones = tenantId.flatMapLatest(repo::observar).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val draftItems = MutableStateFlow<List<ItemCotizacion>>(emptyList())
    val form = MutableStateFlow(CotizacionFormState())
    val selectedId = MutableStateFlow<Long?>(null)
    val editing = MutableStateFlow<Cotizacion?>(null)
    val preselectedClientId = MutableStateFlow<Long?>(null)
    val draftMeta = MutableStateFlow<CotizacionDraftMeta?>(null)

    val selected = selectedId.flatMapLatest { id -> if (id == null) flowOf(null) else repo.observarUna(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val selectedItems = selectedId.flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repo.items(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTenant(id: Long) { tenantId.value = id }

    fun nueva(clienteId: Long? = null) {
        draftItems.value = emptyList()
        form.value = CotizacionFormState()
        selectedId.value = null
        editing.value = null
        preselectedClientId.value = clienteId
        draftMeta.value = null
    }

    fun agregarItem(descripcion: String, cantidad: String, unidad: String, precio: String): String? {
        val cantidadNum = cantidad.replace(',', '.').toDoubleOrNull()
        val precioNum = precio.replace(',', '.').toDoubleOrNull()
        val error = when {
            descripcion.isBlank() -> "La descripción es obligatoria"
            cantidadNum == null || cantidadNum <= 0 -> "La cantidad debe ser mayor a 0"
            unidad.isBlank() -> "La unidad es obligatoria"
            precioNum == null || precioNum < 0 -> "Ingresa un precio válido"
            else -> null
        }
        if (error == null) {
            draftItems.value = draftItems.value + ItemCotizacion(
                cotizacionId = 0,
                descripcion = descripcion.trim(),
                cantidad = cantidadNum!!,
                unidad = unidad.trim(),
                precioUnitario = precioNum!!
            )
        }
        return error
    }

    fun quitarItem(index: Int) {
        draftItems.value = draftItems.value.toMutableList().also { if (index in it.indices) it.removeAt(index) }
    }

    fun prepararBorrador(
        cliente: Cliente?,
        moneda: String,
        descuento: String,
        impuesto: String,
        vigencia: String,
        observaciones: String
    ): Boolean {
        val desc = descuento.replace(',', '.').toDoubleOrNull() ?: 0.0
        val imp = impuesto.replace(',', '.').toDoubleOrNull()
        val vig = vigencia.toIntOrNull()
        val error = when {
            cliente == null -> "Selecciona un cliente"
            draftItems.value.isEmpty() -> "Agrega al menos un ítem"
            desc !in 0.0..100.0 -> "El descuento debe estar entre 0 y 100"
            imp == null || imp !in 0.0..100.0 -> "El impuesto debe estar entre 0 y 100"
            vig == null || vig <= 0 -> "Ingresa una vigencia válida"
            moneda !in listOf("CLP", "USD", "UF") -> "Selecciona una moneda válida"
            else -> null
        }
        form.value = CotizacionFormState(error = error)
        if (error != null) return false

        val cambioInicial = if (moneda == "CLP") 1.0 else editing.value?.takeIf { it.moneda == moneda }?.tipoCambio ?: 0.0
        draftMeta.value = CotizacionDraftMeta(
            cliente = cliente!!,
            moneda = moneda,
            descuentoPct = desc,
            impuestoPct = imp!!,
            vigenciaDias = vig!!,
            observaciones = observaciones.trim(),
            tipoCambio = cambioInicial
        )
        return true
    }

    fun actualizarTipoCambio(valor: Double): Boolean {
        if (valor <= 0) {
            form.value = CotizacionFormState(error = "Ingresa un valor de conversión válido")
            return false
        }
        val meta = draftMeta.value ?: return false
        draftMeta.value = meta.copy(tipoCambio = valor)
        form.value = CotizacionFormState()
        return true
    }

    fun guardarPreparado(online: Boolean, propietarioCorreo: String, onSaved: () -> Unit) {
        val meta = draftMeta.value ?: run {
            form.value = CotizacionFormState(error = "Completa la cotización antes de guardar")
            return
        }
        if (meta.moneda != "CLP" && meta.tipoCambio <= 0) {
            form.value = CotizacionFormState(error = "Falta el valor de conversión")
            return
        }

        viewModelScope.launch {
            val fecha = editing.value?.fecha ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val id = repo.guardarConItems(
                Cotizacion(
                    id = editing.value?.id ?: 0,
                    tenantId = tenantId.value,
                    propietarioCorreo = propietarioCorreo,
                    clienteId = meta.cliente.id,
                    clienteNombre = meta.cliente.nombre,
                    fecha = fecha,
                    vigenciaDias = meta.vigenciaDias,
                    moneda = meta.moneda,
                    tipoCambio = meta.tipoCambio,
                    descuentoPct = meta.descuentoPct,
                    impuestoPct = meta.impuestoPct,
                    observaciones = meta.observaciones,
                    estado = EstadoCotizacion.BORRADOR
                ),
                draftItems.value,
                online
            )
            selectedId.value = id
            editing.value = null
            preselectedClientId.value = null
            draftItems.value = emptyList()
            draftMeta.value = null
            form.value = CotizacionFormState(saved = true)
            onSaved()
        }
    }

    fun prepararEdicion(onReady: () -> Unit) {
        val id = selectedId.value ?: return
        viewModelScope.launch {
            val q = repo.obtener(id) ?: return@launch
            if (q.estado != EstadoCotizacion.BORRADOR) return@launch
            editing.value = q
            preselectedClientId.value = q.clienteId
            draftItems.value = repo.obtenerItems(id).map { it.copy(id = 0, cotizacionId = 0) }
            draftMeta.value = null
            form.value = CotizacionFormState()
            onReady()
        }
    }

    fun seleccionar(id: Long) { selectedId.value = id }

    fun marcarEnviada() {
        val id = selectedId.value ?: return
        viewModelScope.launch { repo.cambiarEstado(id, EstadoCotizacion.ENVIADA) }
    }

    fun sincronizarSiCorresponde(online: Boolean) {
        if (online) viewModelScope.launch { repo.sincronizarPendientes(tenantId.value) }
    }

    fun reintentar(id: Long, online: Boolean) = viewModelScope.launch { repo.reintentar(id, online) }
}
