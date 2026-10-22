package com.example.tratohecho.ui.screens.comun

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tratohecho.data.model.EstadoCotizacion
import com.example.tratohecho.data.model.EstadoSync

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page(title: String, onBack: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun Menu(t: String, s: String, on: () -> Unit) {
    Card(onClick = on, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(t, style = MaterialTheme.typography.titleMedium)
            Text(s, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun EstadoChip(estado: EstadoCotizacion) {
    AssistChip(onClick = {}, label = { AnimatedContent(targetState = estado, label = "estado") { Text(it.name.lowercase().replaceFirstChar { it.uppercase() }) } })
}

@Composable
fun SyncText(sync: EstadoSync, onRetry: (() -> Unit)? = null) {
    val (icon, label, color) = when (sync) {
        EstadoSync.SINCRONIZADO -> Triple(Icons.Default.CheckCircle, "Sincronizado", MaterialTheme.colorScheme.primary)
        EstadoSync.PENDIENTE -> Triple(Icons.Default.CloudUpload, "Pendiente de sincronización", MaterialTheme.colorScheme.tertiary)
        EstadoSync.SINCRONIZANDO -> Triple(Icons.Default.Sync, "Sincronizando…", MaterialTheme.colorScheme.secondary)
        EstadoSync.ERROR -> Triple(Icons.Default.Error, "Error de sincronización", MaterialTheme.colorScheme.error)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Text(label, color = color, style = MaterialTheme.typography.labelMedium)
        if (sync == EstadoSync.ERROR && onRetry != null) TextButton(onClick = onRetry, contentPadding = PaddingValues(0.dp)) { Text("Reintentar") }
    }
}

@Composable
fun FieldError(text: String?) {
    if (text != null) Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
        Text(text, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SuccessPanel(title: String, message: String, primaryText: String, onPrimary: () -> Unit, secondaryText: String? = null, onSecondary: (() -> Unit)? = null) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF159447), modifier = Modifier.size(84.dp))
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onPrimary, modifier = Modifier.fillMaxWidth()) { Text(primaryText) }
        if (secondaryText != null && onSecondary != null) TextButton(onClick = onSecondary, modifier = Modifier.fillMaxWidth()) { Text(secondaryText) }
    }
}
