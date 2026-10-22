# Trato Hecho · Cotizaciones móviles

Proyecto Android nativo para el caso DSY1105 **Trato Hecho**, desarrollado en Kotlin + Jetpack Compose, con MVVM, Room 2.7.2 y DataStore.

## Credenciales de prueba
La pantalla de login no muestra cuentas demo. Para pruebas académicas use contraseña `123456`:

- `admin@tratohecho.cl` · Administrador · tenant 1
- `supervisor@tratohecho.cl` · Supervisor · tenant 1
- `usuario@tratohecho.cl` · Usuario · tenant 1
- `facturacion@tratohecho.cl` · Facturación · tenant 1
- `admin.b@tratohecho.cl` · Administrador · tenant 2 (permite comprobar aislamiento multi-tenant)

## Procesos cubiertos
- PN-01 Acceso, verificación visual, sesión persistente y Home por perfil.
- PN-02 Administración: tipos de unidad, impuestos y usuarios con crear/editar, estado activo/inactivo, validaciones y confirmaciones.
- PN-03 Clientes: listado, detalle, crear/editar, validaciones, Room offline, estados de sincronización e importación nativa de nombre/teléfono/correo desde Contactos.
- PN-04 Cotizaciones: listado según perfil, borradores, ítems, unidades, descuentos, impuestos y monedas.
- PN-05 Valorización: CLP/USD/UF y consulta pública de USD/UF.
- PN-06 Documento PDF, selector nativo de guardado y envío mediante aplicación de correo.
- PN-07 Seguimiento por estado y restricción de Supervisor a usuarios asignados a su equipo.
- PN-08 Facturación: cotizaciones aceptadas, órdenes de compra y facturas.
- PN-09 Indicadores disponibles y valores económicos.

## Perfiles y permisos
- **Administrador:** ve cotizaciones de su tenant y parametriza catálogo, impuestos y usuarios.
- **Supervisor:** ve únicamente cotizaciones cuyos propietarios están asignados a él mediante `supervisorCorreo`.
- **Usuario:** gestiona sus propios clientes y cotizaciones.
- **Facturación:** trabaja con cotizaciones aceptadas/facturadas de su tenant.

## Multi-tenant
El `tenantId` se obtiene desde la sesión almacenada en DataStore y se propaga a los ViewModels. Los DAO filtran por tenant. El tenant 2 incluido como fixture permite demostrar que un administrador de otra empresa no visualiza datos del tenant 1.

## Offline y sincronización
Clientes y borradores pueden guardarse sin conexión. Los estados visibles son:
- `PENDIENTE`
- `SINCRONIZANDO`
- `ERROR`
- `SINCRONIZADO`

El proyecto no inventa endpoints privados porque el caso no entrega contrato de API de Trato Hecho. La capa local deja el punto de sustitución preparado: al recuperar conexión se demuestra el ciclo de sincronización y la UI/MVVM no necesita cambiar cuando se conecte la API real.

## Recursos nativos
1. Contactos: `READ_CONTACTS` + selector de contacto para importar nombre, teléfono y correo.
2. Documentos: `ActivityResultContracts.CreateDocument("application/pdf")` para guardar el PDF.
3. Correo: `Intent.ACTION_SEND` para abrir el cliente de correo con el PDF adjunto.

## Consideraciones de build
- Android Gradle Plugin: 9.3.0
- Room: 2.7.2
- `android.r8.gradual.support=true` incluido en `gradle.properties`.

Abrir el proyecto en Android Studio, sincronizar Gradle y ejecutar en un dispositivo/emulador API compatible.
