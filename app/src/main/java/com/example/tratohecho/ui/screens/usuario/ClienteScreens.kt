package com.example.tratohecho.ui.screens.usuario

import android.Manifest
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.tratohecho.data.model.Cliente
import com.example.tratohecho.ui.screens.comun.*

@Composable
fun ClientesScreen(
    items: List<Cliente>, propietarioCorreo: String, online: Boolean,
    goDetalle: (Cliente) -> Unit, goNuevo: () -> Unit, onRetry: (Long) -> Unit, onBack: () -> Unit
) {
    val propios = remember(items, propietarioCorreo) { items.filter { it.propietarioCorreo == propietarioCorreo } }
    var busqueda by remember { mutableStateOf("") }
    val filtrados = propios.filter { it.nombre.contains(busqueda, true) || it.contacto.contains(busqueda, true) }
    Page("Mis clientes", onBack) {
        OutlinedTextField(busqueda, { busqueda = it }, label = { Text("Buscar clientes…") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        if (!online) AssistChip(onClick = {}, label = { Text("Sin conexión · los cambios se guardarán localmente") })
        if (filtrados.isEmpty()) Text("Aún no tienes clientes registrados.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtrados, key = { it.id }) { cliente ->
                Card(onClick = { goDetalle(cliente) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(cliente.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(cliente.contacto.ifBlank { "Sin contacto principal" })
                        if (cliente.correo.isNotBlank()) Text(cliente.correo, style = MaterialTheme.typography.bodySmall)
                        SyncText(cliente.sync) { onRetry(cliente.id) }
                    }
                }
            }
        }
        Button(onClick = goNuevo, modifier = Modifier.fillMaxWidth()) { Text("+ Registrar cliente") }
    }
}

@Composable
fun ClienteDetalleScreen(cliente: Cliente?, onEditar: () -> Unit, onNuevaCotizacion: () -> Unit, onBack: () -> Unit) {
    Page(cliente?.nombre ?: "Detalle cliente", onBack) {
        if (cliente == null) { Text("No hay cliente seleccionado"); return@Page }
        OutlinedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Nombre o razón social", style = MaterialTheme.typography.labelLarge); Text(cliente.nombre, style = MaterialTheme.typography.titleMedium)
                if (cliente.rut.isNotBlank()) Text("RUT: ${cliente.rut}")
                HorizontalDivider(); Text("Contacto principal", style = MaterialTheme.typography.labelLarge)
                Text(cliente.contacto.ifBlank { "Sin contacto principal" })
                if (cliente.correo.isNotBlank()) Text(cliente.correo)
                if (cliente.telefono.isNotBlank()) Text(cliente.telefono)
                if (cliente.direccion.isNotBlank()) Text(cliente.direccion)
                SyncText(cliente.sync)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onEditar, modifier = Modifier.weight(1f)) { Text("Editar") }
            Button(onClick = onNuevaCotizacion, modifier = Modifier.weight(1f)) { Text("Nueva cotización") }
        }
    }
}

@Composable
fun ClienteFormScreen(
    online: Boolean, inicial: Cliente?, nombreError: String?, correoError: String?, saved: Boolean,
    onSave: (String, String, String, String, String, String) -> Unit,
    onViewSaved: () -> Unit, onBackToList: () -> Unit, onBack: () -> Unit
) {
    val context = LocalContext.current
    var nombre by remember(inicial?.id) { mutableStateOf(inicial?.nombre.orEmpty()) }
    var rut by remember(inicial?.id) { mutableStateOf(inicial?.rut.orEmpty()) }
    var contacto by remember(inicial?.id) { mutableStateOf(inicial?.contacto.orEmpty()) }
    var correo by remember(inicial?.id) { mutableStateOf(inicial?.correo.orEmpty()) }
    var telefono by remember(inicial?.id) { mutableStateOf(inicial?.telefono.orEmpty()) }
    var direccion by remember(inicial?.id) { mutableStateOf(inicial?.direccion.orEmpty()) }
    var importado by remember { mutableStateOf(false) }

    fun leerContacto(uri: android.net.Uri) {
        val resolver = context.contentResolver
        var contactId: String? = null
        resolver.query(uri, arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                contactId = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                contacto = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)).orEmpty()
            }
        }
        contactId?.let { id ->
            resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER), "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID}=?", arrayOf(id), null)?.use { c ->
                if (c.moveToFirst()) telefono = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)).orEmpty()
            }
            resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS), "${ContactsContract.CommonDataKinds.Email.CONTACT_ID}=?", arrayOf(id), null)?.use { c ->
                if (c.moveToFirst()) correo = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS)).orEmpty()
            }
        }
        importado = true
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickContact()) { uri -> if (uri != null) leerContacto(uri) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) picker.launch(null) }
    fun importar() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) picker.launch(null)
        else permission.launch(Manifest.permission.READ_CONTACTS)
    }

    Page(if (inicial == null) "Nuevo cliente" else "Editar cliente", onBack) {
        if (saved) {
            SuccessPanel(
                "Cliente guardado correctamente",
                if (online) "El cliente fue guardado y está sincronizado." else "El cliente está almacenado localmente y se sincronizará cuando recuperes conexión.",
                "Ver cliente", onViewSaved, "Volver a mis clientes", onBackToList
            )
            return@Page
        }

        Text("Información del cliente", style = MaterialTheme.typography.titleMedium)
        OutlinedButton(onClick = ::importar, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Contacts, null); Spacer(Modifier.width(8.dp)); Text("Importar desde contactos") }
        AnimatedVisibility(importado) { AssistChip(onClick = {}, label = { Text("Datos importados desde contactos") }) }
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre o razón social *") }, isError = nombreError != null, modifier = Modifier.fillMaxWidth())
        FieldError(nombreError)
        OutlinedTextField(rut, { rut = it }, label = { Text("RUT") }, modifier = Modifier.fillMaxWidth())
        Text("Contacto principal", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(contacto, { contacto = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(correo, { correo = it }, label = { Text("Correo") }, isError = correoError != null, modifier = Modifier.fillMaxWidth())
        FieldError(correoError)
        OutlinedTextField(telefono, { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
        if (!online) AssistChip(onClick = {}, label = { Text("Sin conexión · se guardará en el dispositivo") })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Cancelar") }
            Button(onClick = { onSave(nombre, rut, contacto, correo, telefono, direccion) }, modifier = Modifier.weight(1f)) { Text("Guardar") }
        }
    }
}
