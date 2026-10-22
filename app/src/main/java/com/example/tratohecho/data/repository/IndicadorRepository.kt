package com.example.tratohecho.data.repository

import com.example.tratohecho.data.model.IndicadorEconomico
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class IndicadorRepository {
    suspend fun obtener(codigo: String): Result<IndicadorEconomico> = withContext(Dispatchers.IO) {
        runCatching {
            val normalizado = codigo.lowercase()
            require(normalizado == "dolar" || normalizado == "uf")
            val conexion = (URL("https://mindicador.cl/api/$normalizado").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
            }
            try {
                if (conexion.responseCode !in 200..299) error("No fue posible consultar el indicador")
                val json = JSONObject(conexion.inputStream.bufferedReader().use { it.readText() })
                val serie = json.getJSONArray("serie")
                if (serie.length() == 0) error("Indicador sin datos")
                val ultimo = serie.getJSONObject(0)
                IndicadorEconomico(
                    codigo = codigo.uppercase(),
                    nombre = json.optString("nombre", codigo.uppercase()),
                    valor = ultimo.getDouble("valor"),
                    fecha = ultimo.optString("fecha").take(10)
                )
            } finally {
                conexion.disconnect()
            }
        }
    }
}
