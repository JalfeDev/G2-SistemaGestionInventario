# Sistema de Gestión de Inventario — Hotel Pirámide

> **Ingeniería de Software II · Grupo 2**
> Universidad de Lima — Facultad de Ingeniería de Sistemas

---

## Equipo

| Nombre | Rol Scrum | Responsabilidad principal |
|--------|-----------|--------------------------|
| Juan | Product Owner | Arquitectura backend, Spring Boot, JWT, Dashboard |
| Diago Estrada | Scrum Master | Coordinación del equipo, CRUD base, Solicitudes de reabastecimiento |
| Sebastian García | Developer | Entradas de insumos, Distribución, Notificaciones por correo, Historial de precios |
| Romina | Developer | Frontend React, Importación CSV, Consulta de stock, Alertas visuales |
| Fabricio Carreño | Developer | Reportes PDF, Documentación, Informe Release 1 |

---

## Descripción del proyecto

Sistema web para gestionar el inventario de insumos del Hotel Pirámide. Permite registrar entradas de productos, controlar el stock disponible, generar alertas de reabastecimiento automáticas por correo y producir reportes de consumo y costos por proveedor.

El sistema está orientado a cuatro roles principales: **Administrador**, **Gerente**, **Encargado de Almacén** y **Housekeeping**, cada uno con acceso restringido a las funcionalidades de su responsabilidad.

**Stack tecnológico:** React (Vite) · Java Spring Boot · Spring Security + JWT · JPA/Hibernate · MySQL · JavaMailSender

---

## Historias de usuario

---

## Sprint 1 — Base del sistema
**Fechas:** 2026-05-25 → 2026-06-19 · **Meta:** Sistema base funcional con autenticación, productos, entradas y distribuciones en React + Java

---

### 1. Feature: Autenticación de usuarios

**Historia de usuario**
Como usuario del sistema, quiero iniciar sesión con mi nombre de usuario y contraseña, para acceder únicamente a las funcionalidades permitidas por mi rol.

**Criterios de aceptación**
- [ ] El sistema solicita nombre de usuario y contraseña antes de permitir acceso a cualquier pantalla
- [ ] Si las credenciales son incorrectas, el sistema muestra el mensaje "Usuario o contraseña incorrectos" sin especificar cuál falló
- [ ] El sistema redirige al dashboard correspondiente según el rol: Gerente, Administrador, Almacén o Housekeeping
- [ ] El token JWT se genera al iniciar sesión y expira a las 8 horas
- [ ] El sistema cierra la sesión y elimina el token al hacer clic en "Cerrar sesión"

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Juan (Product Owner) |
| Labels | `enhancement` `backend` `frontend` `security` |
| Priority | P0 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-25 |
| Target Date | 2026-06-01 |

---

### 2. Feature: Gestión de usuarios

**Historia de usuario**
Como administrador, quiero crear, modificar y consultar usuarios con sus roles asignados, para controlar el acceso al sistema según las responsabilidades de cada persona.

**Criterios de aceptación**
- [ ] El administrador puede registrar un usuario ingresando: nombre, apellido, nombre de usuario y rol
- [ ] El sistema no permite registrar dos usuarios con el mismo nombre de usuario
- [ ] Los roles disponibles son: Gerente, Encargado de Almacén y Housekeeping
- [ ] El administrador puede cambiar el rol de un usuario existente
- [ ] El administrador puede desactivar un usuario para que no pueda iniciar sesión sin eliminarlo del sistema
- [ ] La contraseña se almacena encriptada con BCrypt — nunca en texto plano

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Diago Estrada (Scrum Master) |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P0 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-25 |
| Target Date | 2026-06-05 |

---

### 3. Feature: Gestión de productos y catálogo

**Historia de usuario**
Como administrador, quiero registrar, editar, eliminar y consultar productos del catálogo, para mantener actualizada la información de insumos disponibles en el hotel.

**Criterios de aceptación**
- [ ] El administrador puede crear un producto indicando: nombre, categoría y unidad de medida
- [ ] El sistema no permite registrar dos productos con el mismo nombre dentro de la misma categoría
- [ ] El administrador puede editar nombre, categoría y unidad de medida de un producto existente
- [ ] El administrador puede eliminar un producto solo si no tiene movimientos de stock registrados
- [ ] La lista de productos muestra nombre, categoría, stock actual y stock mínimo

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Diago Estrada (Scrum Master) |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-30 |
| Target Date | 2026-06-07 |

---

### 4. Feature: Configuración del sistema

**Historia de usuario**
Como administrador, quiero configurar el stock mínimo por producto, categorías y unidades de medida, para adaptar el sistema a las necesidades operativas del hotel.

**Criterios de aceptación**
- [ ] El administrador puede definir y modificar el stock mínimo de cada producto individualmente
- [ ] El administrador puede crear, editar y eliminar categorías de productos
- [ ] El administrador puede crear, editar y eliminar unidades de medida
- [ ] No se puede eliminar una categoría que tenga productos asignados — el sistema muestra un mensaje de advertencia
- [ ] Los cambios de configuración se reflejan de inmediato en el sistema sin necesidad de recargar la página

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Diago Estrada (Scrum Master) |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-06-01 |
| Target Date | 2026-06-10 |

---

### 5. Feature: Registro de entradas de insumos con costo por proveedor

**Historia de usuario**
Como encargado de almacén, quiero registrar las entradas de insumos recibidos indicando proveedor y costo unitario, para mantener actualizado el stock y el historial de costos por proveedor.

**Criterios de aceptación**
- [ ] El encargado registra una entrada indicando: producto, cantidad, fecha, proveedor y costo unitario
- [ ] El stock del producto se actualiza automáticamente sumando la cantidad ingresada
- [ ] El sistema valida que la cantidad sea mayor a cero
- [ ] El sistema calcula automáticamente el costo total de la entrada (cantidad × costo unitario)
- [ ] El registro queda almacenado con fecha, hora y usuario que realizó la entrada
- [ ] Si el proveedor no existe en el sistema, el encargado puede crearlo en el momento del registro

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Sebastian García (Developer) |
| Labels | `enhancement` `backend` `database` |
| Priority | P0 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-06-05 |
| Target Date | 2026-06-13 |

---

### 6. Feature: Registro de distribución de insumos por housekeeping

**Historia de usuario**
Como usuario de housekeeping, quiero registrar los insumos distribuidos por habitación atendida, para llevar control del consumo real y descontar del stock disponible.

**Criterios de aceptación**
- [ ] El usuario registra: insumo, cantidad y habitación atendida
- [ ] El stock del producto se descuenta automáticamente al confirmar la distribución
- [ ] El sistema impide registrar una distribución mayor al stock disponible y muestra el stock actual disponible
- [ ] El sistema dispara automáticamente el evento de alerta si el stock resultante queda en o bajo el mínimo configurado
- [ ] El historial de distribuciones puede consultarse filtrado por fecha o habitación

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Sebastian García (Developer) |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-06-08 |
| Target Date | 2026-06-15 |

---

### 7. Feature: Consulta de stock actual

**Historia de usuario**
Como encargado de almacén, quiero consultar el stock actual disponible por producto con filtros, para tomar decisiones oportunas sobre reabastecimiento.

**Criterios de aceptación**
- [ ] La lista muestra todos los productos con su stock actual, stock mínimo y categoría
- [ ] El sistema permite filtrar por categoría y buscar por nombre de producto
- [ ] Los productos con stock igual o menor al mínimo se destacan visualmente en rojo con el déficit calculado
- [ ] La información de stock se muestra en tiempo real sin necesidad de recargar la página
- [ ] La lista puede ordenarse por nombre, categoría o nivel de urgencia (menor diferencia stock actual vs mínimo)

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Romina (Developer) |
| Labels | `enhancement` `frontend` `backend` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-06-08 |
| Target Date | 2026-06-15 |

---

## Sprint 2 — Funcionalidades nuevas
**Fechas:** 2026-06-20 → 2026-07-17 · **Meta:** Las 5 funcionalidades del profesor + reportes PDF + alertas visuales para el gerente

---

### 8. Feature: Importación masiva de productos desde CSV

**Historia de usuario**
Como administrador, quiero cargar el catálogo de productos desde un archivo CSV, para evitar el ingreso manual uno por uno y agilizar la configuración inicial del sistema.

**Criterios de aceptación**
- [ ] El sistema acepta archivos .csv con columnas: nombre, categoría y unidad de medida
- [ ] Antes de importar, el sistema muestra una tabla de vista previa con los registros detectados
- [ ] El sistema importa solo los registros válidos y muestra un resumen: cuántos se importaron correctamente y cuáles tuvieron error con el motivo específico fila por fila
- [ ] El sistema rechaza registros con campos obligatorios vacíos o con nombre de producto duplicado
- [ ] La importación no sobreescribe productos existentes — los duplicados se reportan como error sin modificar el original
- [ ] La interfaz incluye un archivo CSV de ejemplo descargable con el formato esperado

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Romina (Developer) |
| Labels | `enhancement` `backend` `frontend` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-20 |
| Target Date | 2026-06-27 |

---

### 9. Feature: Historial de precios por proveedor

**Historia de usuario**
Como encargado de almacén, quiero consultar el historial de precios de cada producto por proveedor, para comparar costos entre fechas y proveedores y optimizar las compras.

**Criterios de aceptación**
- [ ] El gerente y el administrador pueden consultar el historial de precios filtrado por proveedor o por producto
- [ ] El historial muestra: fecha de entrada, producto, cantidad, costo unitario y costo total
- [ ] Los registros se muestran ordenados de más reciente a más antiguo
- [ ] El sistema calcula y muestra el precio promedio por producto y proveedor en el período consultado

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Sebastian García (Developer) |
| Labels | `enhancement` `backend` `database` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-20 |
| Target Date | 2026-06-27 |

---

### 10. Feature: Notificaciones automáticas de stock crítico por correo

**Historia de usuario**
Como sistema, quiero enviar una notificación automática por correo cuando el stock de un producto baje del mínimo, para que el encargado de almacén y el gerente gestionen el reabastecimiento a tiempo.

**Criterios de aceptación**
- [ ] El sistema detecta automáticamente cuando el stock de un producto es menor o igual al mínimo configurado
- [ ] Se envía un correo al encargado de almacén y al gerente con: nombre del producto, stock actual, stock mínimo y déficit
- [ ] No se generan correos duplicados por el mismo producto hasta que su stock supere el mínimo nuevamente
- [ ] El correo incluye un enlace directo al módulo de solicitudes de reabastecimiento
- [ ] El sistema registra cada notificación enviada con fecha, producto y destinatarios para auditoría

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Sebastian García (Developer) |
| Labels | `enhancement` `backend` |
| Priority | P0 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-23 |
| Target Date | 2026-07-02 |

---

### 11. Feature: Módulo de solicitudes de reabastecimiento

**Historia de usuario**
Como encargado de almacén, quiero generar solicitudes formales de reabastecimiento para aprobación del gerente, para dejar trazabilidad del proceso de compra y evitar compras no autorizadas.

**Criterios de aceptación**
- [ ] El encargado crea una solicitud indicando: producto, cantidad requerida y motivo
- [ ] La solicitud queda en estado PENDIENTE hasta que el gerente la procese
- [ ] El gerente puede aprobar o rechazar la solicitud — si rechaza debe ingresar el motivo
- [ ] El encargado puede consultar el estado de sus solicitudes con el historial completo: fechas, estado y comentarios del gerente
- [ ] El sistema notifica al encargado por correo cuando su solicitud es aprobada o rechazada
- [ ] El historial de solicitudes es visible para el administrador y el gerente con filtros por estado y fecha

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Diago Estrada (Scrum Master) |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-25 |
| Target Date | 2026-07-05 |

---

### 12. Feature: Dashboard de indicadores para el gerente

**Historia de usuario**
Como gerente, quiero visualizar un dashboard con los principales indicadores de inventario, para tomar decisiones basadas en datos actualizados sin necesitar reportes manuales.

**Criterios de aceptación**
- [ ] El dashboard muestra un gráfico de barras con el top 5 de productos más consumidos en los últimos 30 días
- [ ] El dashboard muestra un gráfico circular con el consumo total agrupado por categoría
- [ ] El dashboard muestra un gráfico de línea con la tendencia de stock de los últimos 30 días para los productos más críticos
- [ ] Los datos se actualizan automáticamente al cargar la página sin necesidad de recarga manual
- [ ] El dashboard solo es visible para el rol Gerente
- [ ] Al hacer clic en cualquier producto del top 5 se muestra el detalle de sus movimientos

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Juan (Product Owner) · Diago Estrada (Scrum Master) |
| Labels | `enhancement` `frontend` `backend` |
| Priority | P1 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-28 |
| Target Date | 2026-07-08 |

---

### 13. Feature: Reporte de consumo de inventario con export PDF

**Historia de usuario**
Como gerente, quiero generar reportes de consumo de inventario por rango de fechas y exportarlos en PDF, para analizar el uso de insumos y tomar decisiones de compra informadas.

**Criterios de aceptación**
- [ ] El gerente selecciona fecha inicio y fecha fin para generar el reporte
- [ ] El reporte muestra: nombre del producto, categoría y cantidad consumida en el período
- [ ] Los datos se muestran agrupados por categoría con subtotales por grupo
- [ ] El reporte puede exportarse en formato PDF con el nombre del hotel, fechas del período y fecha de generación
- [ ] Si no hay movimientos en el rango seleccionado, el sistema muestra el mensaje "No existen consumos registrados en el período indicado"

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Fabricio Carreño (Developer) |
| Labels | `enhancement` `backend` `frontend` |
| Priority | P0 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-07-01 |
| Target Date | 2026-07-09 |

---

### 14. Feature: Reporte de costos por proveedor con export PDF

**Historia de usuario**
Como gerente, quiero generar y exportar en PDF un reporte de costos de insumos por proveedor, para evaluar el gasto por proveedor y optimizar las decisiones de compra.

**Criterios de aceptación**
- [ ] El gerente puede filtrar el reporte por proveedor, por rango de fechas, o por ambos
- [ ] El reporte muestra por proveedor: productos suministrados, cantidad total, costo unitario por entrada y costo total acumulado
- [ ] El reporte puede exportarse en formato PDF con la misma estructura visual del reporte en pantalla
- [ ] El PDF incluye el costo total calculado por proveedor y un resumen al final con el total general
- [ ] El reporte se genera en menos de 5 segundos para períodos de hasta 12 meses

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Fabricio Carreño (Developer) |
| Labels | `enhancement` `backend` `frontend` |
| Priority | P0 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-07-03 |
| Target Date | 2026-07-11 |

---

### 15. Feature: Visualización de alertas de stock mínimo

**Historia de usuario**
Como gerente, quiero ver en todo momento la lista de productos cuyo stock esté en nivel crítico, para priorizar las solicitudes de reabastecimiento y actuar antes del desabastecimiento.

**Criterios de aceptación**
- [ ] El sistema muestra únicamente los productos con stock actual menor o igual al stock mínimo
- [ ] La lista incluye: nombre del producto, categoría, stock actual, stock mínimo y déficit (mínimo - actual)
- [ ] Los productos se ordenan de mayor a menor urgencia (menor stock relativo al mínimo primero)
- [ ] El badge de alertas en el menú lateral muestra en tiempo real el número de productos en estado crítico
- [ ] El gerente puede crear una solicitud de reabastecimiento directamente desde esta vista

**Propiedades del Issue**

| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | Romina (Developer) |
| Labels | `enhancement` `frontend` `backend` |
| Priority | P1 |
| Size | S |
| Estimate | 3 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-07-08 |
| Target Date | 2026-07-14 |

---

## Resumen del backlog

| Sprint | HUs | Story Points | Fechas |
|--------|-----|-------------|--------|
| Sprint 1 | HU-01 al HU-07 | 41 pts | 2026-05-25 → 2026-06-19 |
| Sprint 2 | HU-08 al HU-15 | 47 pts | 2026-06-20 → 2026-07-17 |
| **Total** | **15 HU** | **88 pts** | **8 semanas** |

---

## Distribución por integrante

| Integrante | Rol | HUs asignadas |
|-----------|-----|--------------|
| Juan | Product Owner | HU-01, HU-12 |
| Diago Estrada | Scrum Master | HU-02, HU-03, HU-04, HU-11, HU-12 |
| Sebastian García | Developer | HU-05, HU-06, HU-09, HU-10 |
| Romina | Developer | HU-07, HU-08, HU-15 |
| Fabricio Carreño | Developer | HU-13, HU-14 |
