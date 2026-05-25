# Sistema de Gestión de Inventario — Hotel Pirámide

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

Sistema web para gestionar el inventario de insumos del Hotel Pirámide. Permite registrar entradas de productos, controlar el stock, generar alertas de reabastecimiento y producir reportes de consumo y costos por proveedor.

Roles del sistema: **Administrador**, **Gerente**, **Encargado de Almacén** y **Housekeeping**.

---

## Historias de usuario

---

### 1. Feature: Autenticación de usuarios

## Historia de Usuario

Como usuario del sistema,
quiero iniciar sesión con mi nombre de usuario y contraseña,
para acceder solo a las funciones que me corresponden según mi rol.

## Criterios de Aceptación

- [ ] El sistema pide usuario y contraseña antes de mostrar cualquier pantalla
- [ ] Si los datos son incorrectos, muestra un mensaje de error
- [ ] Cada rol ve un menú distinto al entrar: Gerente, Administrador, Almacén o Housekeeping
- [ ] Al cerrar sesión el sistema regresa a la pantalla de login

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

## Historia de Usuario

Como administrador,
quiero crear y administrar los usuarios del sistema con sus roles,
para controlar quién puede entrar y qué puede hacer.

## Criterios de Aceptación

- [ ] Puede registrar un usuario ingresando nombre, apellido, nombre de usuario y rol
- [ ] No permite registrar dos usuarios con el mismo nombre de usuario
- [ ] Los roles disponibles son: Gerente, Encargado de Almacén y Housekeeping
- [ ] Puede cambiar el rol de un usuario existente
- [ ] Puede desactivar un usuario para que no pueda ingresar al sistema

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

## Historia de Usuario

Como administrador,
quiero registrar, editar y eliminar productos del catálogo,
para mantener actualizada la lista de insumos del hotel.

## Criterios de Aceptación

- [ ] Puede registrar un producto con nombre, categoría y unidad de medida
- [ ] No permite registrar dos productos con el mismo nombre en la misma categoría
- [ ] Puede editar los datos de un producto existente
- [ ] Solo puede eliminar un producto si no tiene movimientos registrados
- [ ] La lista muestra nombre, categoría, stock actual y stock mínimo de cada producto

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

### 4. Feature: Configuración del sistema

## Historia de Usuario

Como administrador,
quiero configurar el stock mínimo, las categorías y las unidades de medida,
para adaptar el sistema a cómo trabaja el hotel.

## Criterios de Aceptación

- [ ] Puede definir y modificar el stock mínimo de cada producto
- [ ] Puede crear, editar y eliminar categorías de productos
- [ ] Puede crear, editar y eliminar unidades de medida
- [ ] No se puede eliminar una categoría que tenga productos asignados
- [ ] Los cambios se ven reflejados de inmediato en el sistema

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Diago Estrada |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-30 |
| Target Date | 2026-06-05 |

---

### 5. Feature: Registro de entradas de insumos

## Historia de Usuario

Como encargado de almacén,
quiero registrar las entradas de insumos indicando el proveedor y el precio pagado,
para mantener el stock actualizado y saber cuánto costó cada compra.

## Criterios de Aceptación

- [ ] Puede registrar una entrada con: producto, cantidad, fecha, proveedor y precio por unidad
- [ ] El stock del producto sube automáticamente al guardar la entrada
- [ ] La cantidad debe ser mayor a cero
- [ ] El sistema calcula el costo total de la entrada (cantidad × precio unitario)
- [ ] Queda guardado quién registró la entrada y cuándo

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

### 6. Feature: Consulta de stock actual

## Historia de Usuario

Como encargado de almacén,
quiero ver el stock actual de todos los productos con opción de filtrar,
para saber qué productos necesitan reabastecerse pronto.

## Criterios de Aceptación

- [ ] Muestra todos los productos con su stock actual, mínimo y categoría
- [ ] Se puede filtrar por categoría o buscar por nombre
- [ ] Los productos con stock igual o menor al mínimo se resaltan visualmente
- [ ] La información está siempre actualizada

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

### 7. Feature: Registro de distribución por housekeeping

## Historia de Usuario

Como personal de housekeeping,
quiero registrar los insumos que uso en cada habitación,
para llevar el control del consumo y que el stock se descuente correctamente.

## Criterios de Aceptación

- [ ] Puede registrar el insumo usado, la cantidad y la habitación atendida
- [ ] El stock del producto baja automáticamente al confirmar el registro
- [ ] El sistema no permite registrar más cantidad de la que hay en stock
- [ ] Se puede revisar el historial de distribuciones por fecha o habitación

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

### 8. Feature: Importación de productos desde CSV

## Historia de Usuario

Como administrador,
quiero cargar el catálogo de productos desde un archivo CSV,
para no tener que ingresar uno por uno cuando hay muchos productos.

## Criterios de Aceptación

- [ ] Acepta archivos .csv con columnas: nombre, categoría y unidad de medida
- [ ] Muestra un resumen de cuántos productos se importaron y cuáles tuvieron error
- [ ] Indica el motivo del error por cada fila que falló
- [ ] No sobreescribe productos que ya existen en el sistema
- [ ] Hay un archivo de ejemplo descargable para guiar el formato

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

### 9. Feature: Historial de precios por proveedor

## Historia de Usuario

Como encargado de almacén,
quiero consultar el historial de precios de cada producto por proveedor,
para comparar cuánto ha costado cada insumo en distintas fechas.

## Criterios de Aceptación

- [ ] Se puede filtrar por proveedor o por producto
- [ ] Muestra: fecha, producto, cantidad comprada, precio por unidad y costo total
- [ ] Los registros aparecen del más reciente al más antiguo
- [ ] Muestra el precio promedio del producto con ese proveedor

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

### 10. Feature: Notificaciones automáticas de stock crítico

## Historia de Usuario

Como encargado de almacén,
quiero recibir un correo automático cuando el stock de un producto baje del mínimo,
para poder hacer el pedido a tiempo y evitar que se acabe.

## Criterios de Aceptación

- [ ] El sistema detecta automáticamente cuando el stock llega o baja del mínimo
- [ ] Envía un correo al encargado de almacén y al gerente con el nombre del producto, stock actual y el mínimo configurado
- [ ] No manda el mismo correo dos veces para el mismo producto hasta que el stock vuelva a subir
- [ ] El correo incluye un enlace para ir directamente al módulo de solicitudes

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Sebastian García |
| Labels | `enhancement` `backend` `database` |
| Priority | P1 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-18 |
| Target Date | 2026-06-30 |

---

### 11. Feature: Módulo de solicitudes de reabastecimiento

## Historia de Usuario

Como encargado de almacén,
quiero enviar solicitudes de reabastecimiento al gerente para que las apruebe,
para que quede un registro formal de cada pedido y su estado.

## Criterios de Aceptación

- [ ] Puede crear una solicitud indicando producto, cantidad y motivo
- [ ] La solicitud aparece como pendiente hasta que el gerente la revise
- [ ] El gerente puede aprobarla o rechazarla, y si rechaza debe escribir el motivo
- [ ] El encargado puede ver el estado de todas sus solicitudes enviadas
- [ ] El encargado recibe un correo cuando su solicitud es aprobada o rechazada

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

## Historia de Usuario

Como gerente,
quiero ver un panel con los indicadores principales del inventario,
para tener una visión rápida del estado actual sin revisar reporte por reporte.

## Criterios de Aceptación

- [ ] Muestra los 5 productos más consumidos en los últimos 30 días
- [ ] Muestra el consumo total agrupado por categoría en un gráfico
- [ ] Muestra cómo ha variado el stock en los últimos 30 días
- [ ] La información se actualiza cada vez que entra al panel
- [ ] Solo el gerente puede ver esta pantalla

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

### 13. Feature: Reporte de consumo de inventario

## Historia de Usuario

Como gerente,
quiero generar un reporte de consumo por rango de fechas y descargarlo en PDF,
para analizar cuánto se usa de cada insumo y planificar las compras.

## Criterios de Aceptación

- [ ] Puede elegir una fecha de inicio y una fecha de fin
- [ ] El reporte muestra el producto, categoría y cantidad consumida en ese período
- [ ] Los datos están agrupados por categoría con un subtotal por cada una
- [ ] Se puede descargar en PDF
- [ ] Si no hay movimientos en ese rango, muestra un mensaje indicándolo

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

### 14. Feature: Reporte de costos por proveedor

## Historia de Usuario

Como gerente,
quiero ver un reporte de cuánto se ha gastado por proveedor y descargarlo en PDF,
para saber con quién conviene seguir comprando.

## Criterios de Aceptación

- [ ] Se puede filtrar por proveedor, por fechas o por ambos
- [ ] Muestra por proveedor: productos comprados, cantidades, precio por unidad y total gastado
- [ ] Se puede descargar en PDF con el mismo contenido que se ve en pantalla
- [ ] El PDF muestra el total por proveedor y un total general al final

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

### 15. Feature: Visualización de alertas de stock mínimo

## Historia de Usuario

Como gerente,
quiero ver en cualquier momento qué productos tienen stock crítico,
para saber qué hay que pedir con más urgencia.

## Criterios de Aceptación

- [ ] Muestra solo los productos con stock actual igual o menor al mínimo
- [ ] Incluye: nombre del producto, categoría, stock actual, stock mínimo y cuánto falta
- [ ] Los productos se ordenan del más urgente al menos urgente
- [ ] El menú lateral muestra cuántos productos están en alerta en ese momento
- [ ] Desde esta pantalla se puede crear una solicitud de reabastecimiento directamente

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

## Resumen

| Sprint | HUs | Puntos | Fechas |
|--------|-----|--------|--------|
| Sprint 1 | HU-01 al HU-06 | 31 pts | 2026-05-25 → 2026-06-07 |
| Sprint 2 | HU-07 al HU-15 | 57 pts | 2026-06-09 → 2026-07-17 |
| **Total** | **15 HU** | **88 pts** | **8 semanas** |

---

## Prioridades

| Nivel | Significado | HUs |
|-------|-------------|-----|
| P0 | Crítico — el sistema no funciona sin esto | HU-01, HU-02, HU-03, HU-05 |
| P1 | Alta — funcionalidad importante del Release | HU-04, HU-06, HU-07, HU-09, HU-10, HU-11, HU-12, HU-13, HU-14, HU-15 |
| P2 | Media — mejora de comodidad | HU-08 |
