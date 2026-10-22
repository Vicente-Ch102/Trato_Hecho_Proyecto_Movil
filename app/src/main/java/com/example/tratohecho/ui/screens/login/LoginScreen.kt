package com.example.tratohecho.ui.screens.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tratohecho.ui.screens.comun.FieldError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLogin: (String, String) -> Boolean) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var verificando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AnimatedContent(targetState = verificando, label = "acceso") { loading ->
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Default.Handshake, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(72.dp))
                    Text("Verificando acceso", style = MaterialTheme.typography.headlineSmall)
                    Text("Validando credenciales y perfil de usuario…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    CircularProgressIndicator()
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(Modifier.padding(28.dp).widthIn(max = 460.dp), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Handshake, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(76.dp))
                    Text("Trato Hecho", style = MaterialTheme.typography.headlineMedium)
                    Text("Cotizaciones móviles", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(email, { email = it; error = null }, label = { Text("Correo electrónico") }, singleLine = true, modifier = Modifier.fillMaxWidth(), isError = error != null)
                    OutlinedTextField(password, { password = it; error = null }, label = { Text("Contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), isError = error != null)
                    AnimatedVisibility(error != null) { FieldError(error) }
                    Button(
                        onClick = {
                            verificando = true
                            scope.launch {
                                delay(550)
                                val ok = onLogin(email, password)
                                if (!ok) { verificando = false; error = "Correo o contraseña incorrectos" }
                            }
                        },
                        enabled = email.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Iniciar sesión") }
                    TextButton(onClick = {}) { Text("¿Olvidaste tu contraseña?") }
                }
            }
        }
    }
}
