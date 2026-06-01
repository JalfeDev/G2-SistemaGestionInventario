export const fallbackCategorias = [
  { id: 1, nombre: 'Limpieza' },
  { id: 2, nombre: 'Habitacion' },
  { id: 3, nombre: 'Lavanderia' },
  { id: 4, nombre: 'Amenidades' },
]

export const fallbackUnidades = [
  { id: 1, nombre: 'Unidad', abreviatura: 'und' },
  { id: 2, nombre: 'Litro', abreviatura: 'L' },
  { id: 3, nombre: 'Paquete', abreviatura: 'paq' },
]

export const fallbackProductos = [
  { id: 1, nombre: 'Shampoo hotelero', stockActual: 38, stockMinimo: 60, categoria: fallbackCategorias[3], unidad: fallbackUnidades[0] },
  { id: 2, nombre: 'Toalla de bano', stockActual: 142, stockMinimo: 80, categoria: fallbackCategorias[1], unidad: fallbackUnidades[0] },
  { id: 3, nombre: 'Papel higienico premium', stockActual: 24, stockMinimo: 90, categoria: fallbackCategorias[0], unidad: fallbackUnidades[2] },
  { id: 4, nombre: 'Detergente industrial', stockActual: 68, stockMinimo: 30, categoria: fallbackCategorias[2], unidad: fallbackUnidades[1] },
  { id: 5, nombre: 'Jabon de manos', stockActual: 45, stockMinimo: 55, categoria: fallbackCategorias[3], unidad: fallbackUnidades[0] },
  { id: 6, nombre: 'Desinfectante multiuso', stockActual: 81, stockMinimo: 35, categoria: fallbackCategorias[0], unidad: fallbackUnidades[1] },
]

export const fallbackProveedores = [
  { id: 1, nombre: 'Suministros Andinos SAC', ruc: 204812345, telefono: '01 480 1280', correo: 'ventas@suministrosandinos.pe', direccion: 'Av. Industrial 324, Lima' },
  { id: 2, nombre: 'Hotel Supply Peru', ruc: 206234567, telefono: '01 510 4412', correo: 'pedidos@hotelsupply.pe', direccion: 'Jr. Comercio 812, Lima' },
  { id: 3, nombre: 'Distribuidora Pacifico', ruc: 205678901, telefono: '01 422 7930', correo: 'contacto@dpacifico.pe', direccion: 'Calle Los Pinos 190, Callao' },
]

export const fallbackRoles = [
  { id: 1, nombre: 'ADMINISTRADOR' },
  { id: 2, nombre: 'ALMACEN' },
  { id: 3, nombre: 'HOUSEKEEPING' },
]

export const fallbackUsuarios = [
  { id: 1, usuario: 'admin', nombres: 'Lucia', apellidos: 'Mendoza', email: 'lucia@hotelpiramide.pe', rol: 'ADMINISTRADOR' },
  { id: 2, usuario: 'almacen01', nombres: 'Carlos', apellidos: 'Vera', email: 'carlos@hotelpiramide.pe', rol: 'ALMACEN' },
  { id: 3, usuario: 'housekeeping', nombres: 'Andrea', apellidos: 'Torres', email: 'andrea@hotelpiramide.pe', rol: 'HOUSEKEEPING' },
]

export const fallbackHabitaciones = [
  { id: 1, numero: '101', piso: 1 },
  { id: 2, numero: '204', piso: 2 },
  { id: 3, numero: '305', piso: 3 },
]

export const fallbackIngresos = [
  { id: 1, fechaIngreso: '2026-05-29T09:35:00', observacion: 'Reposicion semanal', usuario: fallbackUsuarios[1] },
  { id: 2, fechaIngreso: '2026-05-27T15:10:00', observacion: 'Compra urgente', usuario: fallbackUsuarios[0] },
]

export const fallbackDetallesIngreso = [
  { id: 1, cantidad: 80, costoUnitario: 2.8, ingresoInventario: fallbackIngresos[0], producto: fallbackProductos[0], proveedor: fallbackProveedores[0] },
  { id: 2, cantidad: 40, costoUnitario: 24.5, ingresoInventario: fallbackIngresos[0], producto: fallbackProductos[3], proveedor: fallbackProveedores[2] },
  { id: 3, cantidad: 100, costoUnitario: 2.65, ingresoInventario: fallbackIngresos[1], producto: fallbackProductos[0], proveedor: fallbackProveedores[1] },
  { id: 4, cantidad: 45, costoUnitario: 18.2, ingresoInventario: fallbackIngresos[1], producto: fallbackProductos[2], proveedor: fallbackProveedores[0] },
]

export const fallbackDistribuciones = [
  { id: 1, fecha: '2026-05-31T08:20:00', habitacion: fallbackHabitaciones[1], usuario: fallbackUsuarios[2] },
  { id: 2, fecha: '2026-05-30T14:05:00', habitacion: fallbackHabitaciones[0], usuario: fallbackUsuarios[2] },
]

export const fallbackDetallesDistribucion = [
  { id: 1, cantidad: 4, distribucion: fallbackDistribuciones[0], producto: fallbackProductos[0] },
  { id: 2, cantidad: 2, distribucion: fallbackDistribuciones[1], producto: fallbackProductos[1] },
]

export const fallbackSolicitudes = [
  { id: 1, fechaSolicitud: '2026-05-31T10:00:00', estado: 'PENDIENTE', comentario: 'Stock por debajo del minimo', solicitante: fallbackUsuarios[1] },
  { id: 2, fechaSolicitud: '2026-05-28T11:30:00', estado: 'APROBADA', comentario: 'Reposicion mensual', solicitante: fallbackUsuarios[1], aprobador: fallbackUsuarios[0] },
]

export const fallbackDetallesSolicitud = [
  { id: 1, cantidadSolicitada: 120, solicitud: fallbackSolicitudes[0], producto: fallbackProductos[2] },
  { id: 2, cantidadSolicitada: 60, solicitud: fallbackSolicitudes[1], producto: fallbackProductos[4] },
]

export const fallbackNotificaciones = [
  { id: 1, mensaje: 'Papel higienico premium se encuentra por debajo del stock minimo.', fecha: '2026-05-31T10:15:00', leido: false },
  { id: 2, mensaje: 'Solicitud #2 aprobada para reposicion mensual.', fecha: '2026-05-29T09:10:00', leido: true },
]
