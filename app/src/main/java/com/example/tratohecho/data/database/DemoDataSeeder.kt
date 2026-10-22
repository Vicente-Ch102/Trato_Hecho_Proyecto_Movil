package com.example.tratohecho.data.database

import com.example.tratohecho.data.model.*

object DemoDataSeeder {
    suspend fun seed(db: AppDatabase) {
        val clienteDao = db.clienteDao(); val cotDao = db.cotizacionDao(); val itemDao = db.itemDao(); val adminDao = db.adminDao()
        var clienteId = 0L
        if (clienteDao.contar(1) == 0) {
            clienteId = clienteDao.guardar(Cliente(tenantId = 1, propietarioCorreo = "usuario@tratohecho.cl", nombre = "Constructora Pérez", rut = "76.123.456-7", contacto = "Juan Pérez", correo = "juan@perez.cl", telefono = "+56 9 9876 5432", direccion = "Concepción"))
            clienteDao.guardar(Cliente(tenantId = 1, propietarioCorreo = "usuario@tratohecho.cl", nombre = "Empresa ABC", contacto = "María Soto", correo = "maria@abc.cl", telefono = "+56 9 8765 4321"))
        }
        if (cotDao.contar(1) == 0) {
            if (clienteId == 0L) clienteId = clienteDao.guardar(Cliente(tenantId=1, propietarioCorreo="usuario@tratohecho.cl", nombre="Constructora Pérez", contacto="Juan Pérez"))
            val borrador = cotDao.guardar(Cotizacion(tenantId=1, propietarioCorreo="usuario@tratohecho.cl", clienteId=clienteId, clienteNombre="Constructora Pérez", fecha="2026-10-21", estado=EstadoCotizacion.BORRADOR, sync=EstadoSync.PENDIENTE))
            itemDao.guardar(ItemCotizacion(cotizacionId=borrador, descripcion="Servicio de instalación", cantidad=2.0, unidad="HR", precioUnitario=50000.0))
            val enviada = cotDao.guardar(Cotizacion(tenantId=1, propietarioCorreo="usuario@tratohecho.cl", clienteId=clienteId, clienteNombre="Constructora Pérez", fecha="2026-10-20", estado=EstadoCotizacion.ENVIADA))
            itemDao.guardar(ItemCotizacion(cotizacionId=enviada, descripcion="Mantención", cantidad=1.0, unidad="UN", precioUnitario=30000.0))
            val aceptada = cotDao.guardar(Cotizacion(tenantId=1, propietarioCorreo="usuario@tratohecho.cl", clienteId=clienteId, clienteNombre="Constructora Pérez", fecha="2026-10-19", estado=EstadoCotizacion.ACEPTADA))
            itemDao.guardar(ItemCotizacion(cotizacionId=aceptada, descripcion="Implementación aceptada", cantidad=2.0, unidad="HR", precioUnitario=45000.0))
        }
        if (adminDao.contarUnidades(1) == 0) {
            adminDao.guardarUnidad(TipoUnidad(tenantId=1, nombre="Unidad", codigo="UN")); adminDao.guardarUnidad(TipoUnidad(tenantId=1, nombre="Hora", codigo="HR"))
        }
        if (adminDao.contarImpuestos(1) == 0) adminDao.guardarImpuesto(Impuesto(tenantId=1, nombre="IVA", porcentaje=19.0))
        if (adminDao.contarUsuarios(1) == 0) {
            adminDao.guardarUsuario(Usuario(tenantId=1, nombre="Carlos", correo="admin@tratohecho.cl", perfil=Perfil.ADMINISTRADOR))
            adminDao.guardarUsuario(Usuario(tenantId=1, nombre="Ana", correo="supervisor@tratohecho.cl", perfil=Perfil.SUPERVISOR))
            adminDao.guardarUsuario(Usuario(tenantId=1, nombre="Juan", correo="usuario@tratohecho.cl", perfil=Perfil.USUARIO, supervisorCorreo="supervisor@tratohecho.cl"))
            adminDao.guardarUsuario(Usuario(tenantId=1, nombre="María", correo="facturacion@tratohecho.cl", perfil=Perfil.FACTURACION))
        }
        // Segundo tenant de demostración para comprobar aislamiento multi-tenant.
        if (clienteDao.contar(2) == 0) {
            val clienteB = clienteDao.guardar(Cliente(tenantId=2, propietarioCorreo="usuario.b@tratohecho.cl", nombre="Cliente Empresa B", contacto="Contacto B"))
            val cotB = cotDao.guardar(Cotizacion(tenantId=2, propietarioCorreo="usuario.b@tratohecho.cl", clienteId=clienteB, clienteNombre="Cliente Empresa B", fecha="2026-10-21", estado=EstadoCotizacion.ENVIADA))
            itemDao.guardar(ItemCotizacion(cotizacionId=cotB, descripcion="Servicio tenant B", cantidad=1.0, unidad="UN", precioUnitario=99000.0))
        }
        if (adminDao.contarUnidades(2) == 0) adminDao.guardarUnidad(TipoUnidad(tenantId=2, nombre="Unidad", codigo="UN"))
        if (adminDao.contarImpuestos(2) == 0) adminDao.guardarImpuesto(Impuesto(tenantId=2, nombre="IVA", porcentaje=19.0))
        if (adminDao.contarUsuarios(2) == 0) {
            adminDao.guardarUsuario(Usuario(tenantId=2, nombre="Admin Empresa B", correo="admin.b@tratohecho.cl", perfil=Perfil.ADMINISTRADOR))
            adminDao.guardarUsuario(Usuario(tenantId=2, nombre="Supervisor Empresa B", correo="supervisor.b@tratohecho.cl", perfil=Perfil.SUPERVISOR))
            adminDao.guardarUsuario(Usuario(tenantId=2, nombre="Usuario Empresa B", correo="usuario.b@tratohecho.cl", perfil=Perfil.USUARIO, supervisorCorreo="supervisor.b@tratohecho.cl"))
        }

    }
}
