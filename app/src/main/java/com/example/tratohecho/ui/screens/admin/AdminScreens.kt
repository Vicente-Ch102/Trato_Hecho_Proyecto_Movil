package com.example.tratohecho.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tratohecho.data.model.*
import com.example.tratohecho.ui.screens.comun.*

@Composable
fun AdminScreen(go: (String) -> Unit, onBack: () -> Unit) {
    Page("Administración", onBack) {
        Menu("Tipos de unidad", "Parametrizar catálogo") { go("unidades") }
        Menu("Impuestos", "Crear, editar y activar impuestos") { go("impuestos") }
        Menu("Usuarios", "Gestionar usuarios, perfiles y supervisión") { go("usuarios") }
    }
}

@Composable
fun UnidadesScreen(
    items: List<TipoUnidad>,
    onSave: (TipoUnidad?, String, String, Boolean) -> String?,
    onToggle: (TipoUnidad) -> Unit,
    onBack: () -> Unit
) {
    var editando by remember { mutableStateOf<TipoUnidad?>(null) }
    var creando by remember { mutableStateOf(false) }
    var exito by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var activo by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    fun cargar(x: TipoUnidad?) {
        editando = x; creando = true; exito = false
        nombre = x?.nombre.orEmpty(); codigo = x?.codigo.orEmpty(); activo = x?.activo ?: true; error = null
    }
    Page(if (creando) if (editando == null) "Nuevo tipo de unidad" else "Editar tipo de unidad" else "Tipos de unidad", if (creando) ({ creando = false; exito = false }) else onBack) {
        if (exito) {
            SuccessPanel("Tipo de unidad guardado", "El tipo de unidad fue guardado correctamente.", "Volver al catálogo", { creando = false; exito = false })
        } else if (!creando) {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.id }) { x ->
                    OutlinedCard(Modifier.fillMaxWidth().clickable { cargar(x) }) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text(x.nombre, style = MaterialTheme.typography.titleMedium); Text(x.codigo, style = MaterialTheme.typography.bodySmall) }
                            Switch(x.activo, { onToggle(x) })
                        }
                    }
                }
            }
            Button(onClick = { cargar(null) }, modifier = Modifier.fillMaxWidth()) { Text("+ Nuevo tipo de unidad") }
        } else {
            OutlinedTextField(nombre, { nombre = it; error = null }, label = { Text("Nombre *") }, isError = error?.contains("nombre", true) == true, modifier = Modifier.fillMaxWidth())
            if (error?.contains("nombre", true) == true) FieldError(error)
            OutlinedTextField(codigo, { codigo = it; error = null }, label = { Text("Código *") }, isError = error?.contains("código", true) == true, modifier = Modifier.fillMaxWidth())
            if (error?.contains("código", true) == true) FieldError(error)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Activo"); Switch(activo, { activo = it }) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton({ creando = false }, Modifier.weight(1f)) { Text("Cancelar") }
                Button({ error = onSave(editando, nombre, codigo, activo); if (error == null) exito = true }, Modifier.weight(1f)) { Text("Guardar") }
            }
        }
    }
}

@Composable
fun ImpuestosScreen(
    items: List<Impuesto>,
    onSave: (Impuesto?, String, String, Boolean) -> String?,
    onToggle: (Impuesto) -> Unit,
    onBack: () -> Unit
) {
    var editando by remember { mutableStateOf<Impuesto?>(null) }; var creando by remember { mutableStateOf(false) }; var exito by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }; var porcentaje by remember { mutableStateOf("") }; var activo by remember { mutableStateOf(true) }; var error by remember { mutableStateOf<String?>(null) }
    fun cargar(x: Impuesto?) { editando = x; creando = true; nombre = x?.nombre.orEmpty(); porcentaje = x?.porcentaje?.toString().orEmpty(); activo = x?.activo ?: true; error = null }
    Page(if (creando) if (editando == null) "Nuevo impuesto" else "Editar impuesto" else "Impuestos", if (creando) ({ creando = false; exito = false }) else onBack) {
        if (exito) {
            SuccessPanel("Impuesto guardado correctamente", "El impuesto \"$nombre\" ha sido guardado exitosamente.", "Volver a impuestos", { creando = false; exito = false })
        } else if (!creando) {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.id }) { x ->
                    OutlinedCard(Modifier.fillMaxWidth().clickable { cargar(x) }) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text(x.nombre, style = MaterialTheme.typography.titleMedium); Text("${x.porcentaje}%") }
                            Switch(checked = x.activo, onCheckedChange = { onToggle(x) })
                        }
                    }
                }
            }
            Button(onClick = { cargar(null) }, modifier = Modifier.fillMaxWidth()) { Text("+ Nuevo impuesto") }
        } else {
            OutlinedTextField(nombre, { nombre = it; error = null }, label = { Text("Nombre *") }, isError = error?.contains("nombre", true) == true, modifier = Modifier.fillMaxWidth())
            if (error?.contains("nombre", true) == true) FieldError(error)
            OutlinedTextField(porcentaje, { porcentaje = it; error = null }, label = { Text("Porcentaje *") }, suffix = { Text("%") }, isError = error?.contains("porcentaje", true) == true || error?.contains("0 y 100") == true, modifier = Modifier.fillMaxWidth())
            if (error != null && !error!!.contains("nombre", true)) FieldError(error)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Activo"); Switch(activo, { activo = it }) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton({ creando = false }, Modifier.weight(1f)) { Text("Cancelar") }
                Button({ error = onSave(editando, nombre, porcentaje, activo); if (error == null) exito = true }, Modifier.weight(1f)) { Text("Guardar") }
            }
        }
    }
}

@Composable
fun UsuariosScreen(
    items: List<Usuario>,
    onSave: (Usuario?, String, String, Perfil, Boolean, String) -> String?,
    onToggle: (Usuario) -> Unit,
    onBack: () -> Unit
) {
    var editando by remember { mutableStateOf<Usuario?>(null) }; var creando by remember { mutableStateOf(false) }; var exito by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }; var correo by remember { mutableStateOf("") }; var perfil by remember { mutableStateOf(Perfil.USUARIO) }; var activo by remember { mutableStateOf(true) }; var supervisor by remember { mutableStateOf("") }; var error by remember { mutableStateOf<String?>(null) }
    val supervisores = items.filter { it.perfil == Perfil.SUPERVISOR && it.activo }
    fun cargar(x: Usuario?) { editando=x; creando=true; nombre=x?.nombre.orEmpty(); correo=x?.correo.orEmpty(); perfil=x?.perfil ?: Perfil.USUARIO; activo=x?.activo ?: true; supervisor=x?.supervisorCorreo.orEmpty(); error=null }
    Page(if (creando) if (editando == null) "Nuevo usuario" else "Editar usuario" else "Usuarios", if (creando) ({ creando=false; exito=false }) else onBack) {
        if (exito) {
            SuccessPanel("Usuario guardado correctamente", "El usuario \"$nombre\" ha sido guardado exitosamente.", "Volver a usuarios", { creando=false; exito=false })
        } else if (!creando) {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.id }) { x -> OutlinedCard(Modifier.fillMaxWidth().clickable { cargar(x) }) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) { Text(x.nombre, style = MaterialTheme.typography.titleMedium); Text(x.correo); Text(x.perfil.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall); if (x.supervisorCorreo.isNotBlank()) Text("Supervisor: ${x.supervisorCorreo}", style = MaterialTheme.typography.bodySmall) }
                        Switch(x.activo, { onToggle(x) })
                    }
                } }
            }
            Button(onClick = { cargar(null) }, modifier = Modifier.fillMaxWidth()) { Text("+ Nuevo usuario") }
        } else {
            OutlinedTextField(nombre, { nombre=it; error=null }, label={Text("Nombre *")}, isError=error?.contains("nombre",true)==true, modifier=Modifier.fillMaxWidth())
            if (error?.contains("nombre",true)==true) FieldError(error)
            OutlinedTextField(correo, { correo=it; error=null }, label={Text("Correo *")}, isError=error?.contains("correo",true)==true, modifier=Modifier.fillMaxWidth())
            if (error?.contains("correo",true)==true) FieldError(error)
            Text("Perfil *")
            Perfil.entries.forEach { p -> Row { RadioButton(perfil == p, { perfil=p; error=null }); Text(p.name.lowercase().replaceFirstChar { it.uppercase() }, Modifier.padding(top=12.dp)) } }
            if (perfil == Perfil.USUARIO) {
                Text("Supervisor *")
                supervisores.forEach { s -> Row { RadioButton(supervisor == s.correo, { supervisor=s.correo; error=null }); Text("${s.nombre} · ${s.correo}", Modifier.padding(top=12.dp)) } }
                if (supervisores.isEmpty()) Text("Crea primero un usuario con perfil Supervisor.", color=MaterialTheme.colorScheme.error)
                if (error?.contains("supervisor",true)==true) FieldError(error)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) { Text("Estado activo"); Switch(activo,{activo=it}) }
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                OutlinedButton({ creando=false }, Modifier.weight(1f)) { Text("Cancelar") }
                Button({ error=onSave(editando,nombre,correo,perfil,activo,supervisor); if(error==null) exito=true }, Modifier.weight(1f)) { Text("Guardar") }
            }
        }
    }
}
