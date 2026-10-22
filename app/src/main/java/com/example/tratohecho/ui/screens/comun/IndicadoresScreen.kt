package com.example.tratohecho.ui.screens.comun

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tratohecho.data.model.Cotizacion
import com.example.tratohecho.data.model.EstadoCotizacion
import com.example.tratohecho.ui.viewmodel.IndicadoresState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun IndicadoresScreen(
    cotizaciones: List<Cotizacion>,
    state: IndicadoresState,
    online: Boolean,
    onRefresh: () -> Unit,
    onBack: () -> Unit
) {
    val nf = NumberFormat.getNumberInstance(Locale("es", "CL"))
    Page("Indicadores", onBack) {
        Text("Resumen de cotizaciones", style = MaterialTheme.typography.titleMedium)
        val total = cotizaciones.size
        val enviadas = cotizaciones.count { it.estado == EstadoCotizacion.ENVIADA }
        val aceptadas = cotizaciones.count { it.estado == EstadoCotizacion.ACEPTADA || it.estado == EstadoCotizacion.FACTURADA }
        val rechazadas = cotizaciones.count { it.estado == EstadoCotizacion.RECHAZADA }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Total", total.toString(), Modifier.weight(1f))
            MetricCard("Enviadas", enviadas.toString(), Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Aceptadas", aceptadas.toString(), Modifier.weight(1f))
            MetricCard("Rechazadas", rechazadas.toString(), Modifier.weight(1f))
        }
        HorizontalDivider()
        Text("Indicadores económicos", style = MaterialTheme.typography.titleMedium)
        state.dolar?.let { MetricCard("USD", "$${nf.format(it.valor)} CLP · ${it.fecha}", Modifier.fillMaxWidth()) }
        state.uf?.let { MetricCard("UF", "$${nf.format(it.valor)} CLP · ${it.fecha}", Modifier.fillMaxWidth()) }
        if (state.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = onRefresh, enabled = online && !state.cargando, modifier = Modifier.fillMaxWidth()) { Text("Actualizar USD / UF") }
        Text("Fuente pública: mindicador.cl. Los indicadores de negocio se calculan únicamente con datos de cotizaciones almacenados por la app.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier) {
    OutlinedCard(modifier) { Column(Modifier.padding(12.dp)) { Text(label, style = MaterialTheme.typography.labelLarge); Text(value, style = MaterialTheme.typography.titleMedium) } }
}
