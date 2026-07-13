# Sistema de Gestión de Inventario — Hotel Pirámide

## Enlace de la aplicación

[Aplicación Web](https://frontend-g2-sistemagestioninventario-itsajob.up.railway.app/login)

> **Ingeniería de Software II · Grupo 2**  
> Universidad de Lima — Facultad de Ingeniería de Sistemas

---

## Equipo

| Nombre | Rol Scrum |
|--------|-----------|
| Juan Loyola | Product Owner |
| Diago Estrada | Scrum Master |
| Sebastian García | Developer |
| Romina Rebaza | Developer |
| Fabricio Carreño | Developer |

---

## Descripción del proyecto

Sistema web desarrollado para administrar el inventario de insumos del Hotel Pirámide. Permite registrar productos, controlar el stock disponible, gestionar entradas y salidas, generar alertas de stock mínimo y consultar reportes de consumo y costos por proveedor.

Roles del sistema:

- **Administrador**
- **Gerente**
- **Encargado de Almacén**
- **Housekeeping**

---

## Historias de usuario

### 1. Feature: Autenticación de usuarios

#### Historia de Usuario

Como usuario del sistema, quiero iniciar sesión con mi nombre de usuario y contraseña para acceder únicamente a las funciones permitidas según mi rol.

#### Criterios de aceptación

- [ ] El sistema solicita usuario y contraseña antes de mostrar cualquier pantalla.
- [ ] Si las credenciales son incorrectas, muestra un mensaje de error.
- [ ] Cada rol visualiza un menú diferente: Gerente, Administrador, Encargado de Almacén o Housekeeping.
- [ ] Al cerrar sesión, el sistema regresa a la pantalla de inicio de sesión.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Juan Loyola |
| Labels | `enhancement` `backend` `frontend` |
| Priority | P0 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-25 |
| Target Date | 2026-05-30 |

---

### 2. Feature: Gestión de usuarios

#### Historia de Usuario

Como administrador, quiero crear y administrar los usuarios del sistema con sus respectivos roles para controlar el acceso a la aplicación.

#### Criterios de aceptación

- [ ] Registrar un usuario con nombre, apellido, nombre de usuario y rol.
- [ ] No permitir usuarios con el mismo nombre de usuario.
- [ ] Los roles disponibles son Gerente, Encargado de Almacén y Housekeeping.
- [ ] Modificar el rol de un usuario existente.
- [ ] Desactivar usuarios para impedir su acceso.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Diago Estrada |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P0 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-25 |
| Target Date | 2026-06-02 |

---

### 3. Feature: Gestión de productos

#### Historia de Usuario

Como administrador, quiero registrar, editar y eliminar productos para mantener actualizado el catálogo de insumos del hotel.

#### Criterios de aceptación

- [ ] Registrar productos indicando nombre, categoría y unidad de medida.
- [ ] Evitar productos duplicados en la misma categoría.
- [ ] Editar productos existentes.
- [ ] Eliminar únicamente productos sin movimientos registrados.
- [ ] Mostrar nombre, categoría, stock actual y stock mínimo.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Diago Estrada |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P0 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-27 |
| Target Date | 2026-06-03 |

---

### 4. Feature: Registro de entradas de insumos

#### Historia de Usuario

Como encargado de almacén, quiero registrar las entradas de insumos para mantener actualizado el stock y registrar los costos de compra.

#### Criterios de aceptación

- [ ] Registrar producto, cantidad, fecha, proveedor y precio unitario.
- [ ] Actualizar automáticamente el stock.
- [ ] Validar que la cantidad sea mayor que cero.
- [ ] Calcular automáticamente el costo total.
- [ ] Registrar quién realizó la operación y cuándo.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Sebastian García |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P0 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-30 |
| Target Date | 2026-06-06 |

---

### 5. Feature: Consulta de stock actual

#### Historia de Usuario

Como encargado de almacén, quiero consultar el stock de los productos para identificar aquellos que requieren reabastecimiento.

#### Criterios de aceptación

- [ ] Mostrar stock actual, stock mínimo y categoría.
- [ ] Permitir búsqueda por nombre o categoría.
- [ ] Resaltar productos con stock crítico.
- [ ] Mostrar información actualizada.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Romina Rebaza |
| Labels | `enhancement` `frontend` `backend` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-06-04 |
| Target Date | 2026-06-07 |

---

### 6. Feature: Registro de distribución por housekeeping

#### Historia de Usuario

Como personal de housekeeping, quiero registrar los insumos utilizados en cada habitación para mantener un control del consumo y actualizar el stock de manera automática.

#### Criterios de aceptación

- [ ] Registrar el insumo utilizado, la cantidad y la habitación atendida.
- [ ] Descontar automáticamente el stock al confirmar el registro.
- [ ] No permitir registrar una cantidad mayor al stock disponible.
- [ ] Consultar el historial de distribuciones por fecha o habitación.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Sebastian García |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-09 |
| Target Date | 2026-06-16 |

---

### 7. Feature: Reporte de consumo de inventario

#### Historia de Usuario

Como gerente, quiero generar un reporte de consumo dentro de un rango de fechas y descargarlo en PDF para analizar el uso de los insumos y facilitar la planificación de futuras compras.

#### Criterios de aceptación

- [ ] Seleccionar una fecha de inicio y una fecha de fin.
- [ ] Mostrar el producto, la categoría y la cantidad consumida durante el período.
- [ ] Agrupar la información por categoría con su respectivo subtotal.
- [ ] Permitir descargar el reporte en formato PDF.
- [ ] Mostrar un mensaje cuando no existan movimientos en el período seleccionado.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Fabricio Carreño |
| Labels | `enhancement` `backend` `frontend` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-07-01 |
| Target Date | 2026-07-10 |

---

### 8. Feature: Visualización de alertas de stock mínimo

#### Historia de Usuario

Como gerente, quiero consultar los productos con stock crítico para identificar oportunamente cuáles requieren reabastecimiento.

#### Criterios de aceptación

- [ ] Mostrar únicamente los productos cuyo stock sea igual o menor al stock mínimo.
- [ ] Incluir nombre del producto, categoría, stock actual, stock mínimo y cantidad faltante.
- [ ] Ordenar los productos desde el más urgente hasta el menos urgente.
- [ ] Mostrar en el menú lateral la cantidad de productos en estado crítico.
- [ ] Permitir crear una solicitud de reabastecimiento desde la misma pantalla.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Romina Rebaza |
| Labels | `enhancement` `frontend` `backend` |
| Priority | P1 |
| Size | S |
| Estimate | 3 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-07-08 |
| Target Date | 2026-07-17 |

---

### 9. Feature: Configuración del sistema

#### Historia de Usuario

Como administrador, quiero configurar el stock mínimo, las categorías y las unidades de medida para adaptar el sistema a las necesidades operativas del hotel.

#### Criterios de aceptación

- [ ] Definir y modificar el stock mínimo de cada producto.
- [ ] Crear, editar y eliminar categorías de productos.
- [ ] Crear, editar y eliminar unidades de medida.
- [ ] Impedir la eliminación de categorías que tengan productos asociados.
- [ ] Reflejar los cambios inmediatamente en el sistema.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Juan Loyola |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-30 |
| Target Date | 2026-06-05 |

---

### 10. Feature: Notificaciones automáticas de stock crítico

#### Historia de Usuario

Como encargado de almacén, quiero recibir una notificación por correo cuando un producto alcance el stock mínimo para realizar el reabastecimiento de forma oportuna.

#### Criterios de aceptación

- [ ] Detectar automáticamente cuando el stock llegue o sea inferior al mínimo configurado.
- [ ] Enviar un correo al encargado de almacén y al gerente con la información del producto.
- [ ] Evitar el envío repetido de la misma notificación mientras el producto continúe en estado crítico.
- [ ] Incluir un enlace directo al módulo de solicitudes de reabastecimiento.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Fabricio Carreño |
| Labels | `enhancement` `backend` `database` |
| Priority | P1 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-18 |
| Target Date | 2026-06-30 |

---

### 11. Feature: Módulo de solicitudes de reabastecimiento

#### Historia de Usuario

Como encargado de almacén, quiero enviar solicitudes de reabastecimiento al gerente para mantener un registro del pedido y conocer su estado hasta que sea aprobado o rechazado.

#### Criterios de aceptación

- [ ] Crear una solicitud indicando el producto, la cantidad requerida y el motivo.
- [ ] Registrar la solicitud con estado **Pendiente** hasta su revisión.
- [ ] Permitir que el gerente apruebe o rechace la solicitud, indicando un motivo en caso de rechazo.
- [ ] Consultar el estado de todas las solicitudes realizadas.
- [ ] Enviar una notificación por correo cuando la solicitud sea aprobada o rechazada.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Diago Estrada |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | M |
| Estimate | 8 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-22 |
| Target Date | 2026-07-03 |

---

### 12. Feature: Dashboard de indicadores para el gerente

#### Historia de Usuario

Como gerente, quiero visualizar un panel con los principales indicadores del inventario para conocer rápidamente el estado general del sistema.

#### Criterios de aceptación

- [ ] Mostrar los cinco productos con mayor consumo en los últimos 30 días.
- [ ] Mostrar el consumo total agrupado por categoría mediante un gráfico.
- [ ] Visualizar la evolución del stock durante los últimos 30 días.
- [ ] Actualizar la información cada vez que se ingrese al panel.
- [ ] Restringir el acceso únicamente al gerente.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Juan Loyola |
| Labels | `enhancement` `frontend` `backend` |
| Priority | P1 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-25 |
| Target Date | 2026-07-07 |

---

### 13. Feature: Importación de productos desde CSV

#### Historia de Usuario

Como administrador, quiero importar el catálogo de productos desde un archivo CSV para agilizar el registro de grandes cantidades de información.

#### Criterios de aceptación

- [ ] Aceptar archivos `.csv` con las columnas nombre, stockActual, stockMinimo, categoría y unidad de medida.
- [ ] Mostrar un resumen con la cantidad de productos importados y los registros con error.
- [ ] Indicar el motivo de cada error detectado.
- [ ] No sobrescribir productos existentes.
- [ ] Proporcionar un archivo de ejemplo para facilitar la importación.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Romina Rebaza |
| Labels | `enhancement` `backend` `frontend` |
| Priority | P2 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-09 |
| Target Date | 2026-06-18 |

---

### 14. Feature: Historial de precios por proveedor

#### Historia de Usuario

Como encargado de almacén, quiero consultar el historial de precios de los productos por proveedor para comparar costos y facilitar futuras compras.

#### Criterios de aceptación

- [ ] Filtrar la información por proveedor o por producto.
- [ ] Mostrar fecha, producto, cantidad comprada, precio unitario y costo total.
- [ ] Ordenar los registros del más reciente al más antiguo.
- [ ] Mostrar el precio promedio del producto para cada proveedor.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Sebastian García |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-12 |
| Target Date | 2026-06-20 |

---

### 15. Feature: Reporte de costos por proveedor

#### Historia de Usuario

Como gerente, quiero consultar el gasto realizado por proveedor y descargar el reporte en PDF para apoyar la toma de decisiones sobre futuras compras.

#### Criterios de aceptación

- [ ] Filtrar el reporte por proveedor, por rango de fechas o por ambos.
- [ ] Mostrar los productos comprados, las cantidades, el precio unitario y el gasto total por proveedor.
- [ ] Permitir la descarga del reporte en formato PDF.
- [ ] Mostrar el total por proveedor y un total general al finalizar el reporte.

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Fabricio Carreño |
| Labels | `enhancement` `backend` `frontend` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-07-06 |
| Target Date | 2026-07-14 |

---

# Resumen

## Sprint

| Sprint | Historias de usuario | Puntos | Fechas |
|--------|----------------------|--------|--------|
| Sprint 1 | HU-01, HU-02, HU-03, HU-04, HU-05, HU-09 | 31 pts | 2026-05-25 → 2026-06-07 |
| Sprint 2 | HU-06, HU-07, HU-08, HU-10, HU-11, HU-12, HU-13, HU-14, HU-15 | 57 pts | 2026-06-09 → 2026-07-17 |
| **Total** | **15 HU** | **88 pts** | **8 semanas** |

## Release

| Release | Historias de usuario |
|---------|----------------------|
| Release 1 | HU-01 a HU-09 |
| Release 2 | HU-10 a HU-15 |

---

# Prioridades

| Nivel | Significado | Historias de usuario |
|-------|-------------|----------------------|
| P0 | Funcionalidades críticas para el funcionamiento del sistema. | HU-01, HU-02, HU-03, HU-04 |
| P1 | Funcionalidades principales del proyecto. | HU-05, HU-06, HU-07, HU-08, HU-09, HU-10, HU-11, HU-12, HU-14, HU-15 |
| P2 | Funcionalidades complementarias orientadas a mejorar la usabilidad. | HU-13 |