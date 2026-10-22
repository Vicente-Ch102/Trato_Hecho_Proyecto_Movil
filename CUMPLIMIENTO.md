# Checklist de cumplimiento

## Correcciones finales implementadas
- [x] Edición y activación/desactivación de tipos de unidad.
- [x] Edición y activación/desactivación de impuestos.
- [x] Impuesto validado en rango 0–100.
- [x] Edición y activación/desactivación de usuarios.
- [x] Asociación Usuario → Supervisor.
- [x] Supervisor limitado a cotizaciones de su equipo.
- [x] Contactos importa nombre, teléfono y correo.
- [x] Confirmación visual de guardado de cliente, impuesto, usuario y catálogo.
- [x] Estados Pendiente / Sincronizando / Error / Sincronizado + reintento.
- [x] Tenant dinámico desde DataStore en ViewModels principales.
- [x] Segundo tenant de prueba para demostrar aislamiento.
- [x] Validaciones visuales con `isError`, icono y mensaje en formularios principales.
- [x] Home con contadores, actividad reciente, estado de conexión y navegación inferior.
- [x] Login sin selector ni tarjetas visibles de cuentas demo.
- [x] Flujo de envío con preparación visual, PDF, almacenamiento y correo nativo.

## Límite externo conocido
La sincronización contra servidor no puede ser real hasta disponer del contrato/endpoints de la API privada de Trato Hecho. El proyecto conserva Room, estados, repositorios y puntos de integración preparados sin inventar servicios privados no definidos por el caso.
