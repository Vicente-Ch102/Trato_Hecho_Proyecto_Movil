package com.example.tratohecho.ui.screens.comun

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tratohecho.data.model.*
import com.example.tratohecho.ui.navigation.Rutas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nombre: String,
    correo: String,
    perfil: Perfil,
    estaOnline: Boolean,
    clientes: List<Cliente>,
    cotizaciones: List<Cotizacion>,
    go: (String) -> Unit,
    logout: () -> Unit
) {
    var perfilDialog by remember { mutableStateOf(false) }
    val propias = cotizaciones.filter { perfil != Perfil.USUARIO || it.propietarioCorreo == correo }
    val clientesPropios = clientes.filter { it.propietarioCorreo == correo }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("Hola, $nombre"); Text(nombrePerfil(perfil), style = MaterialTheme.typography.bodySmall) } },
                actions = { IconButton(onClick = { perfilDialog = true }) { Icon(Icons.Default.AccountCircle, "Perfil") } }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = true, onClick = { }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") })
                when (perfil) {
                    Perfil.USUARIO -> {
                        NavigationBarItem(selected = false, onClick = { go(Rutas.CLIENTES) }, icon = { Icon(Icons.Default.People, null) }, label = { Text("Clientes") })
                        NavigationBarItem(selected = false, onClick = { go(Rutas.COTIZACIONES) }, icon = { Icon(Icons.Default.ReceiptLong, null) }, label = { Text("Cotizaciones") })
                    }
                    Perfil.SUPERVISOR, Perfil.ADMINISTRADOR -> NavigationBarItem(selected = false, onClick = { go(Rutas.COTIZACIONES) }, icon = { Icon(Icons.Default.ReceiptLong, null) }, label = { Text("Cotizaciones") })
                    Perfil.FACTURACION -> NavigationBarItem(selected = false, onClick = { go(Rutas.FACTURACION) }, icon = { Icon(Icons.Default.ReceiptLong, null) }, label = { Text("Facturación") })
                }
                NavigationBarItem(selected = false, onClick = { perfilDialog = true }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Perfil") })
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = {}, label = { Text(if (estaOnline) "En línea" else "Sin conexión") }, leadingIcon = { Icon(if (estaOnline) Icons.Default.Wifi else Icons.Default.WifiOff, null, Modifier.size(18.dp)) })
                Text(correo, style = MaterialTheme.typography.bodySmall)
            }
            when (perfil) {
                Perfil.USUARIO -> {
                    DashboardCard("Mis clientes", "${clientesPropios.size} clientes", Icons.Default.People) { go(Rutas.CLIENTES) }
                    DashboardCard("Mis cotizaciones", "${propias.size} cotizaciones", Icons.Default.ReceiptLong) { go(Rutas.COTIZACIONES) }
                }
                Perfil.SUPERVISOR -> DashboardCard("Cotizaciones del equipo", "${propias.size} cotizaciones", Icons.Default.Groups) { go(Rutas.COTIZACIONES) }
                Perfil.ADMINISTRADOR -> {
                    DashboardCard("Cotizaciones de la empresa", "${cotizaciones.size} cotizaciones", Icons.Default.BusinessCenter) { go(Rutas.COTIZACIONES) }
                    DashboardCard("Administración", "Catálogo, impuestos y usuarios", Icons.Default.Settings) { go(Rutas.ADMIN) }
                    DashboardCard("Indicadores", "Consulta de indicadores disponibles", Icons.Default.Assessment) { go(Rutas.INDICADORES) }
                }
                Perfil.FACTURACION -> DashboardCard("Cotizaciones aceptadas", "${cotizaciones.count { it.estado == EstadoCotizacion.ACEPTADA || it.estado == EstadoCotizacion.FACTURADA }} cotizaciones", Icons.Default.ReceiptLong) { go(Rutas.FACTURACION) }
            }
            Text("Actividad reciente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            propias.take(3).forEach { c ->
                OutlinedCard(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text("COT-${c.id}", fontWeight = FontWeight.SemiBold); Text(c.clienteNombre, style = MaterialTheme.typography.bodySmall) }
                        EstadoChip(c.estado)
                    }
                }
            }
            if (propias.isEmpty()) Text("Sin actividad reciente", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (perfilDialog) AlertDialog(
        onDismissRequest = { perfilDialog = false },
        title = { Text("Perfil") },
        text = { Column { Text(nombre); Text(correo); Text(nombrePerfil(perfil)) } },
        confirmButton = { TextButton(onClick = { perfilDialog = false }) { Text("Cerrar") } },
        dismissButton = { TextButton(onClick = logout) { Text("Cerrar sesión") } }
    )
}

@Composable
private fun DashboardCard(titulo: String, descripcion: String, icono: ImageVector, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
            Column { Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold); Text(descripcion, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

private fun nombrePerfil(perfil: Perfil) = when (perfil) {
    Perfil.ADMINISTRADOR -> "Administrador"; Perfil.SUPERVISOR -> "Supervisor"; Perfil.USUARIO -> "Usuario"; Perfil.FACTURACION -> "Facturación"
}
