package com.example.tratohecho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.example.tratohecho.data.database.AppDatabase
import com.example.tratohecho.data.database.DemoDataSeeder
import com.example.tratohecho.data.datastore.SessionDataStore
import com.example.tratohecho.data.repository.*
import com.example.tratohecho.ui.navigation.AppNavGraph
import com.example.tratohecho.ui.theme.TratoHechoTheme
import com.example.tratohecho.ui.viewmodel.ViewModelFactory
import com.example.tratohecho.util.NetworkObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        lifecycleScope.launch(Dispatchers.IO) { DemoDataSeeder.seed(database) }

        val factory = ViewModelFactory(
            session = SessionDataStore(applicationContext),
            clientes = ClienteRepository(database.clienteDao()),
            cotizaciones = CotizacionRepository(database, database.cotizacionDao(), database.itemDao()),
            admin = AdminRepository(database.adminDao()),
            facturacion = FacturacionRepository(database.facturacionDao()),
            indicadores = IndicadorRepository()
        )

        setContent {
            TratoHechoTheme {
                val networkObserver = remember { NetworkObserver(applicationContext) }
                val estaOnline by networkObserver.estaConectado.collectAsState(initial = true)
                AppNavGraph(factory = factory, estaOnline = estaOnline)
            }
        }
    }
}
