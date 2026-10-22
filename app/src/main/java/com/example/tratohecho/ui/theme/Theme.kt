package com.example.tratohecho.ui.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
private val scheme=lightColorScheme(primary=androidx.compose.ui.graphics.Color(0xFF0B72E7),secondary=androidx.compose.ui.graphics.Color(0xFF2368A2))
@Composable fun TratoHechoTheme(content:@Composable ()->Unit)=MaterialTheme(colorScheme=scheme,content=content)
