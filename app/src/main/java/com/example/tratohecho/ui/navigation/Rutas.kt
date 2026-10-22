package com.example.tratohecho.ui.navigation

import com.example.tratohecho.data.model.Perfil

object Rutas {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CLIENTES = "clientes"
    const val CLIENTE_DETALLE = "cliente_detalle"
    const val CLIENTE_FORM = "cliente_form"
    const val COTIZACIONES = "cotizaciones"
    const val COTIZACION_FORM = "cotizacion_form"
    const val ITEM_FORM = "item_form"
    const val VALORIZACION = "valorizacion"
    const val RESUMEN = "resumen"
    const val ENVIO = "envio"
    const val SEGUIMIENTO = "seguimiento"
    const val ADMIN = "admin"
    const val UNIDADES = "unidades"
    const val IMPUESTOS = "impuestos"
    const val USUARIOS = "usuarios"
    const val FACTURACION = "facturacion"
    const val FACTURACION_DETALLE = "facturacion_detalle"
    const val ORDEN_FORM = "orden_form"
    const val FACTURA_FORM = "factura_form"
    const val INDICADORES = "indicadores"

    fun puedeAcceder(perfil: Perfil, ruta: String): Boolean {
        if (ruta == LOGIN || ruta == HOME || ruta == INDICADORES) return true
        return when (perfil) {
            Perfil.ADMINISTRADOR -> ruta in setOf(COTIZACIONES, SEGUIMIENTO, ADMIN, UNIDADES, IMPUESTOS, USUARIOS)
            Perfil.SUPERVISOR -> ruta in setOf(COTIZACIONES, SEGUIMIENTO)
            Perfil.USUARIO -> ruta in setOf(CLIENTES, CLIENTE_DETALLE, CLIENTE_FORM, COTIZACIONES, COTIZACION_FORM, ITEM_FORM, VALORIZACION, RESUMEN, ENVIO, SEGUIMIENTO)
            Perfil.FACTURACION -> ruta in setOf(FACTURACION, FACTURACION_DETALLE, ORDEN_FORM, FACTURA_FORM)
        }
    }
}
