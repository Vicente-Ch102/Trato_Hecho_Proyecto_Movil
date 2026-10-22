package com.example.tratohecho.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tratohecho.data.model.Perfil
import com.example.tratohecho.ui.screens.admin.*
import com.example.tratohecho.ui.screens.comun.HomeScreen
import com.example.tratohecho.ui.screens.comun.IndicadoresScreen
import com.example.tratohecho.ui.screens.facturacion.*
import com.example.tratohecho.ui.screens.login.LoginScreen
import com.example.tratohecho.ui.screens.usuario.*
import com.example.tratohecho.ui.viewmodel.*

@Composable
fun AppNavGraph(factory: ViewModelFactory, estaOnline: Boolean) {
    val nav = rememberNavController()
    val auth: AuthViewModel = viewModel(factory = factory)
    val cli: ClienteViewModel = viewModel(factory = factory)
    val cot: CotizacionViewModel = viewModel(factory = factory)
    val admin: AdminViewModel = viewModel(factory = factory)
    val fact: FacturacionViewModel = viewModel(factory = factory)
    val ind: IndicadoresViewModel = viewModel(factory = factory)

    val ses by auth.session.collectAsState()
    val clientes by cli.clientes.collectAsState()
    val clienteEditando by cli.editando.collectAsState()
    val clienteSeleccionado by cli.seleccionado.collectAsState()
    val clienteForm by cli.form.collectAsState()
    val cotizaciones by cot.cotizaciones.collectAsState()
    val draftItems by cot.draftItems.collectAsState()
    val cotForm by cot.form.collectAsState()
    val cotEdicion by cot.editing.collectAsState()
    val clientePreseleccionadoId by cot.preselectedClientId.collectAsState()
    val cotDraftMeta by cot.draftMeta.collectAsState()
    val seleccionada by cot.selected.collectAsState()
    val selectedItems by cot.selectedItems.collectAsState()
    val unidades by admin.unidades.collectAsState()
    val impuestos by admin.impuestos.collectAsState()
    val usuarios by admin.usuarios.collectAsState()
    val ordenes by fact.ordenes.collectAsState()
    val facturas by fact.facturas.collectAsState()
    val indicadores by ind.state.collectAsState()
    var cotizacionFacturacionId by remember { mutableLongStateOf(0L) }

    LaunchedEffect(ses.logged) {
        if (ses.logged && nav.currentDestination?.route == Rutas.LOGIN) {
            nav.navigate(Rutas.HOME) { popUpTo(Rutas.LOGIN) { inclusive = true }; launchSingleTop = true }
        }
    }
    LaunchedEffect(ses.tenantId) {
        cli.setTenant(ses.tenantId)
        cot.setTenant(ses.tenantId)
        admin.setTenant(ses.tenantId)
        fact.setTenant(ses.tenantId)
    }
    LaunchedEffect(estaOnline, ses.tenantId) {
        cli.sincronizarSiCorresponde(estaOnline)
        cot.sincronizarSiCorresponde(estaOnline)
    }

    val correosEquipo = remember(usuarios, ses.correo) {
        usuarios.filter { it.activo && it.perfil == Perfil.USUARIO && it.supervisorCorreo.equals(ses.correo, true) }.map { it.correo }.toSet()
    }
    val cotizacionesVisibles = remember(cotizaciones, ses.perfil, ses.correo, correosEquipo) {
        when (ses.perfil) {
            Perfil.USUARIO -> cotizaciones.filter { it.propietarioCorreo.equals(ses.correo, true) }
            Perfil.SUPERVISOR -> cotizaciones.filter { it.propietarioCorreo in correosEquipo }
            Perfil.ADMINISTRADOR, Perfil.FACTURACION -> cotizaciones
        }
    }

    fun irA(ruta: String) { if (Rutas.puedeAcceder(ses.perfil, ruta)) nav.navigate(ruta) }

    NavHost(navController = nav, startDestination = if (ses.logged) Rutas.HOME else Rutas.LOGIN) {
        composable(Rutas.LOGIN) {
            LoginScreen { email, pass ->
                val ok = auth.login(email, pass)
                if (ok) nav.navigate(Rutas.HOME) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                ok
            }
        }

        composable(Rutas.HOME) {
            HomeScreen(
                nombre = ses.nombre, correo = ses.correo, perfil = ses.perfil, estaOnline = estaOnline,
                clientes = clientes, cotizaciones = cotizacionesVisibles, go = ::irA,
                logout = { auth.logout(); nav.navigate(Rutas.LOGIN) { popUpTo(Rutas.HOME) { inclusive = true } } }
            )
        }

        composable(Rutas.CLIENTES) { Protegida(nav, ses.perfil, Rutas.CLIENTES) {
            ClientesScreen(
                items = clientes, propietarioCorreo = ses.correo, online = estaOnline,
                goDetalle = { cli.seleccionar(it); irA(Rutas.CLIENTE_DETALLE) },
                goNuevo = { cli.editar(null); cli.limpiarResultado(); irA(Rutas.CLIENTE_FORM) },
                onRetry = { cli.reintentar(it, estaOnline) }, onBack = { nav.popBackStack() }
            )
        } }

        composable(Rutas.CLIENTE_DETALLE) { Protegida(nav, ses.perfil, Rutas.CLIENTE_DETALLE) {
            ClienteDetalleScreen(
                cliente = clienteSeleccionado,
                onEditar = { cli.editar(clienteSeleccionado); cli.limpiarResultado(); irA(Rutas.CLIENTE_FORM) },
                onNuevaCotizacion = { cot.nueva(clienteSeleccionado?.id); irA(Rutas.COTIZACION_FORM) },
                onBack = { nav.popBackStack() }
            )
        } }

        composable(Rutas.CLIENTE_FORM) { Protegida(nav, ses.perfil, Rutas.CLIENTE_FORM) {
            ClienteFormScreen(
                online = estaOnline, inicial = clienteEditando,
                nombreError = clienteForm.nombreError, correoError = clienteForm.correoError, saved = clienteForm.saved,
                onSave = { nombre, rut, contacto, correo, telefono, direccion -> cli.save(nombre, rut, contacto, correo, telefono, direccion, estaOnline, ses.correo) },
                onViewSaved = { cli.limpiarResultado(); nav.navigate(Rutas.CLIENTE_DETALLE) { popUpTo(Rutas.CLIENTE_FORM) { inclusive = true } } },
                onBackToList = { cli.limpiarResultado(); nav.navigate(Rutas.CLIENTES) { popUpTo(Rutas.CLIENTE_FORM) { inclusive = true }; launchSingleTop = true } },
                onBack = { cli.limpiarResultado(); nav.popBackStack() }
            )
        } }

        composable(Rutas.COTIZACIONES) { Protegida(nav, ses.perfil, Rutas.COTIZACIONES) {
            CotizacionesScreen(
                items = cotizacionesVisibles, perfil = ses.perfil, correo = ses.correo,
                goForm = { cot.nueva(); irA(Rutas.COTIZACION_FORM) },
                goSeg = { cot.seleccionar(it); irA(Rutas.SEGUIMIENTO) },
                onRetry = { cot.reintentar(it, estaOnline) }, onBack = { nav.popBackStack() }
            )
        } }

        composable(Rutas.COTIZACION_FORM) { Protegida(nav, ses.perfil, Rutas.COTIZACION_FORM) {
            CotizacionFormScreen(
                online = estaOnline, edicion = cotEdicion, clientePreseleccionadoId = clientePreseleccionadoId,
                clientes = clientes, propietarioCorreo = ses.correo, items = draftItems, error = cotForm.error,
                impuestosCatalogo = impuestos.filter { it.activo },
                onContinue = { cliente, moneda, desc, imp, vigencia, obs ->
                    if (cot.prepararBorrador(cliente, moneda, desc, imp, vigencia, obs)) irA(if (moneda == "CLP") Rutas.RESUMEN else Rutas.VALORIZACION)
                },
                onItem = { irA(Rutas.ITEM_FORM) }, onRemoveItem = cot::quitarItem, onBack = { nav.popBackStack() }
            )
        } }

        composable(Rutas.ITEM_FORM) { Protegida(nav, ses.perfil, Rutas.ITEM_FORM) {
            ItemFormScreen(unidades = unidades.filter { it.activo }, onSave = cot::agregarItem, onBack = { nav.popBackStack() })
        } }

        composable(Rutas.VALORIZACION) { Protegida(nav, ses.perfil, Rutas.VALORIZACION) {
            ValorizacionScreen(
                meta = cotDraftMeta, indicadorUsd = indicadores.dolar, indicadorUf = indicadores.uf, online = estaOnline, error = cotForm.error,
                onRefresh = { ind.cargar(estaOnline) }, onApply = cot::actualizarTipoCambio, onContinue = { irA(Rutas.RESUMEN) }, onBack = { nav.popBackStack() }
            )
        } }

        composable(Rutas.RESUMEN) { Protegida(nav, ses.perfil, Rutas.RESUMEN) {
            ResumenCotizacionScreen(
                meta = cotDraftMeta, items = draftItems,
                onEdit = { nav.popBackStack(Rutas.COTIZACION_FORM, inclusive = false) },
                onSave = { cot.guardarPreparado(estaOnline, ses.correo) { nav.navigate(Rutas.COTIZACIONES) { popUpTo(Rutas.COTIZACION_FORM) { inclusive = true }; launchSingleTop = true } } },
                onBack = { nav.popBackStack() }
            )
        } }

        composable(Rutas.SEGUIMIENTO) { Protegida(nav, ses.perfil, Rutas.SEGUIMIENTO) {
            SeguimientoScreen(
                cotizacion = seleccionada, items = selectedItems, perfil = ses.perfil,
                onEnvio = { irA(Rutas.ENVIO) }, onEditar = { cot.prepararEdicion { irA(Rutas.COTIZACION_FORM) } }, onBack = { nav.popBackStack() }
            )
        } }

        composable(Rutas.ENVIO) { Protegida(nav, ses.perfil, Rutas.ENVIO) {
            EnvioScreen(
                cotizacion = seleccionada, items = selectedItems,
                destinatarioInicial = clientes.firstOrNull { it.id == seleccionada?.clienteId }?.correo.orEmpty(),
                onMarkSent = cot::marcarEnviada, onBack = { nav.popBackStack() }
            )
        } }

        composable(Rutas.ADMIN) { Protegida(nav, ses.perfil, Rutas.ADMIN) { AdminScreen(::irA) { nav.popBackStack() } } }
        composable(Rutas.UNIDADES) { Protegida(nav, ses.perfil, Rutas.UNIDADES) { UnidadesScreen(unidades, admin::guardarUnidad, admin::alternarUnidad) { nav.popBackStack() } } }
        composable(Rutas.IMPUESTOS) { Protegida(nav, ses.perfil, Rutas.IMPUESTOS) { ImpuestosScreen(impuestos, admin::guardarImpuesto, admin::alternarImpuesto) { nav.popBackStack() } } }
        composable(Rutas.USUARIOS) { Protegida(nav, ses.perfil, Rutas.USUARIOS) { UsuariosScreen(usuarios, admin::guardarUsuario, admin::alternarUsuario) { nav.popBackStack() } } }

        composable(Rutas.FACTURACION) { Protegida(nav, ses.perfil, Rutas.FACTURACION) {
            FacturacionScreen(cotizaciones = cotizacionesVisibles, onSelect = { cotizacionFacturacionId = it; irA(Rutas.FACTURACION_DETALLE) }, onBack = { nav.popBackStack() })
        } }
        composable(Rutas.FACTURACION_DETALLE) { Protegida(nav, ses.perfil, Rutas.FACTURACION_DETALLE) {
            FacturacionDetalleScreen(
                cotizacion = cotizaciones.firstOrNull { it.id == cotizacionFacturacionId }, ordenes = ordenes, facturas = facturas,
                goOrden = { irA(Rutas.ORDEN_FORM) }, goFactura = { irA(Rutas.FACTURA_FORM) }, onBack = { nav.popBackStack() }
            )
        } }
        composable(Rutas.ORDEN_FORM) { Protegida(nav, ses.perfil, Rutas.ORDEN_FORM) {
            DocumentoFacturacionScreen("Registrar orden de compra", "Número de OC", cotizacionFacturacionId, fact::guardarOrden) { nav.popBackStack() }
        } }
        composable(Rutas.FACTURA_FORM) { Protegida(nav, ses.perfil, Rutas.FACTURA_FORM) {
            DocumentoFacturacionScreen("Registrar factura", "Número de factura", cotizacionFacturacionId, fact::guardarFactura) { nav.popBackStack() }
        } }

        composable(Rutas.INDICADORES) {
            IndicadoresScreen(cotizaciones = cotizacionesVisibles, state = indicadores, online = estaOnline, onRefresh = { ind.cargar(estaOnline) }, onBack = { nav.popBackStack() })
        }
    }
}

@Composable
private fun Protegida(nav: NavHostController, perfil: Perfil, ruta: String, content: @Composable () -> Unit) {
    if (Rutas.puedeAcceder(perfil, ruta)) content() else LaunchedEffect(perfil, ruta) {
        nav.navigate(Rutas.HOME) { popUpTo(Rutas.HOME) { inclusive = false }; launchSingleTop = true }
    }
}
