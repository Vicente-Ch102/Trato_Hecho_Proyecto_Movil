package com.example.tratohecho.ui.screens.facturacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tratohecho.data.model.*
import com.example.tratohecho.ui.screens.comun.EstadoChip
import com.example.tratohecho.ui.screens.comun.Page

@Composable
fun FacturacionScreen(
    cotizaciones: List<Cotizacion>,
    onSelect: (Long) -> Unit,
    onBack: () -> Unit
) {
    val aceptadas = cotizaciones.filter { it.estado == EstadoCotizacion.ACEPTADA || it.estado == EstadoCotizacion.FACTURADA }
    Page("Cotizaciones aceptadas", onBack) {
        if (aceptadas.isEmpty()) Text("No hay cotizaciones aceptadas disponibles.")
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(aceptadas, key = { it.id }) { c ->
                Card(onClick = { onSelect(c.id) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("COT-${c.id}", style = MaterialTheme.typography.titleMedium)
                            EstadoChip(c.estado)
                        }
                        Text(c.clienteNombre)
                        Text(c.fecha, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun FacturacionDetalleScreen(
    cotizacion: Cotizacion?,
    ordenes: List<OrdenCompra>,
    facturas: List<Factura>,
    goOrden: () -> Unit,
    goFactura: () -> Unit,
    onBack: () -> Unit
) {
    Page("Detalle facturación", onBack) {
        if (cotizacion == null) {
            Text("No hay cotización seleccionada")
            return@Page
        }
        Text("COT-${cotizacion.id}", style = MaterialTheme.typography.headlineSmall)
        Text(cotizacion.clienteNombre)
        EstadoChip(cotizacion.estado)
        HorizontalDivider()
        Text("Órdenes de compra", style = MaterialTheme.typography.titleMedium)
        val ocCot = ordenes.filter { it.cotizacionId == cotizacion.id }
        if (ocCot.isEmpty()) Text("Sin órdenes registradas")
        ocCot.forEach { oc ->
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(oc.numero, style = MaterialTheme.typography.titleSmall)
                    Text(oc.fecha)
                    if (oc.observaciones.isNotBlank()) Text(oc.observaciones, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        OutlinedButton(onClick = goOrden, modifier = Modifier.fillMaxWidth()) { Text("+ Registrar orden de compra") }

        Text("Facturas", style = MaterialTheme.typography.titleMedium)
        val facCot = facturas.filter { it.cotizacionId == cotizacion.id }
        if (facCot.isEmpty()) Text("Sin facturas registradas")
        facCot.forEach { fac ->
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(fac.numero, style = MaterialTheme.typography.titleSmall)
                    Text(fac.fecha)
                    if (fac.observaciones.isNotBlank()) Text(fac.observaciones, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        OutlinedButton(onClick = goFactura, modifier = Modifier.fillMaxWidth()) { Text("+ Registrar factura") }
    }
}

@Composable
fun DocumentoFacturacionScreen(
    titulo: String,
    numeroLabel: String,
    cotizacionId: Long,
    onSave: (Long, String, String, String) -> String?,
    onBack: () -> Unit
) {
    var numero by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    Page(titulo, onBack) {
        Text("Cotización: COT-$cotizacionId")
        OutlinedTextField(numero, { numero = it }, label = { Text("$numeroLabel *") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(fecha, { fecha = it }, label = { Text("Fecha (AAAA-MM-DD) *") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(observaciones, { observaciones = it }, label = { Text("Observaciones") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = {
            error = onSave(cotizacionId, numero, fecha, observaciones)
            if (error == null) onBack()
        }, modifier = Modifier.fillMaxWidth()) { Text("Guardar") }
    }
}
