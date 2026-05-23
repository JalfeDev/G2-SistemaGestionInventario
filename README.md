# Sistema de Gestión de Inventario — Hotel Pirámide
 
> **Ingeniería de Software II · Grupo 2**  
> Universidad de Lima — Facultad de Ingeniería de Sistemas
 
---
 
## Descripción del proyecto
 
Sistema web para gestionar el inventario de insumos del Hotel Pirámide. Permite registrar entradas de productos, controlar el stock disponible, generar alertas de reabastecimiento y producir reportes de consumo y costos por proveedor.
 
El sistema está orientado a cuatro roles principales: **Administrador**, **Gerente**, **Encargado de Almacén** y **Housekeeping**, cada uno con acceso restringido a las funcionalidades de su responsabilidad.
 
---
 
## Historias de usuario
 
---
 
### 1. Feature: Autenticación de usuarios
 
**Historia de usuario**  
Como usuario del sistema, quiero iniciar sesión con mis credenciales, para acceder únicamente a las funcionalidades permitidas por mi rol.
 
**Criterios de aceptación**
- [ ] El sistema solicita nombre de usuario y contraseña antes de permitir acceso a cualquier funcionalidad
- [ ] El sistema restringe el acceso a módulos según el rol asignado al usuario
- [ ] El sistema muestra mensaje de error si las credenciales son incorrectas
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` `frontend` |
| Priority | P0 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-25 |
| Target Date | 2026-05-29 |
 
---
 
### 2. Feature: Gestión de usuarios
 
**Historia de usuario**  
Como administrador, quiero crear y gestionar usuarios con roles asignados, para controlar el acceso al sistema según las responsabilidades de cada persona.
 
**Criterios de aceptación**
- [ ] El administrador puede registrar un nuevo usuario ingresando nombre, apellido, nombre de usuario y rol
- [ ] Los roles disponibles para asignar son: gerente, encargado de almacén y housekeeping
- [ ] El administrador puede modificar el rol de un usuario existente
- [ ] Un usuario sin rol asignado no puede acceder al sistema
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P0 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-25 |
| Target Date | 2026-06-02 |
 
---
 
### 3. Feature: Gestión de productos
 
**Historia de usuario**  
Como administrador, quiero registrar, modificar y eliminar productos del catálogo, para mantener actualizada la información de insumos disponibles en el hotel.
 
**Criterios de aceptación**
- [ ] El administrador puede crear un producto ingresando nombre y categoría
- [ ] El administrador puede editar el nombre y categoría de un producto existente
- [ ] El administrador puede eliminar un producto cuando corresponda
- [ ] El sistema valida que no se registren productos duplicados
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 1 |
| Start Date | 2026-05-30 |
| Target Date | 2026-06-04 |
 
---
 
### 4. Feature: Importación de productos desde CSV
 
**Historia de usuario**  
Como administrador, quiero cargar el catálogo de productos desde un archivo CSV, para evitar el ingreso manual uno por uno y agilizar la configuración inicial del sistema.
 
**Criterios de aceptación**
- [ ] El sistema acepta archivos en formato .csv con los campos: nombre y categoría
- [ ] El sistema muestra un resumen de productos importados exitosamente y los que tuvieron error
- [ ] El sistema rechaza registros con datos incompletos o duplicados e informa el motivo
- [ ] La importación no sobreescribe productos existentes sin confirmación del usuario
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` `frontend` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-05 |
| Target Date | 2026-06-10 |
 
---
 
### 5. Feature: Configuración del sistema
 
**Historia de usuario**  
Como administrador, quiero configurar el stock mínimo, categorías y unidades de medida, para adaptar el sistema a las necesidades operativas del hotel.
 
**Criterios de aceptación**
- [ ] El administrador puede definir y modificar el stock mínimo por producto
- [ ] El administrador puede crear, editar y eliminar categorías de productos
- [ ] El administrador puede crear, editar y eliminar unidades de medida
- [ ] Los cambios de configuración se reflejan de inmediato en el sistema
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-06 |
| Target Date | 2026-06-14 |
 
---
 
### 6. Feature: Registro de entradas de insumos
 
**Historia de usuario**  
Como encargado de almacén, quiero registrar las entradas de insumos recibidos, para mantener actualizado el stock disponible en el sistema.
 
**Criterios de aceptación**
- [ ] El encargado puede registrar una entrada indicando: producto, cantidad recibida y fecha de ingreso
- [ ] El stock del producto se actualiza automáticamente al guardar la entrada
- [ ] El sistema valida que la cantidad ingresada sea mayor a cero
- [ ] El registro queda almacenado con fecha, hora y usuario que realizó la entrada
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` `database` |
| Priority | P0 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-10 |
| Target Date | 2026-06-15 |
 
---
 
### 7. Feature: Historial de precios por proveedor
 
**Historia de usuario**  
Como encargado de almacén, quiero registrar el costo unitario en cada entrada de insumo por proveedor, para poder calcular el costo total del inventario y hacer seguimiento de precios.
 
**Criterios de aceptación**
- [ ] Al registrar una entrada, el encargado puede ingresar el proveedor y el costo unitario del producto
- [ ] El sistema almacena el historial de precios por proveedor y producto
- [ ] El sistema calcula automáticamente el costo total de la entrada (cantidad × precio unitario)
- [ ] El administrador y gerente pueden consultar el historial de precios por proveedor
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` `database` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 2 |
| Start Date | 2026-06-12 |
| Target Date | 2026-06-18 |
 
---
 
### 8. Feature: Consulta de stock actual
 
**Historia de usuario**  
Como encargado de almacén, quiero consultar el stock actual disponible por producto, para tomar decisiones oportunas sobre reabastecimiento.
 
**Criterios de aceptación**
- [ ] El encargado puede ver una lista de todos los productos con su stock actual
- [ ] El sistema permite filtrar productos por categoría
- [ ] Los productos con stock igual o menor al mínimo se destacan visualmente
- [ ] La información de stock se muestra en tiempo real
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `frontend` `backend` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 3 |
| Start Date | 2026-06-16 |
| Target Date | 2026-06-21 |
 
---
 
### 9. Feature: Notificaciones automáticas de stock crítico
 
**Historia de usuario**  
Como encargado de almacén, quiero recibir una notificación automática cuando un producto alcance el stock mínimo, para gestionar el reabastecimiento a tiempo.
 
**Criterios de aceptación**
- [ ] El sistema genera una notificación automática cuando el stock de un producto es menor o igual al mínimo configurado
- [ ] La notificación se envía por correo electrónico al encargado de almacén y al gerente
- [ ] La notificación incluye el nombre del producto, stock actual y stock mínimo
- [ ] No se generan notificaciones duplicadas por el mismo producto en el mismo evento
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` |
| Priority | P0 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 3 |
| Start Date | 2026-06-18 |
| Target Date | 2026-06-26 |
 
---
 
### 10. Feature: Módulo de solicitudes de reabastecimiento
 
**Historia de usuario**  
Como encargado de almacén, quiero generar solicitudes formales de reabastecimiento para aprobación del gerente, para dejar trazabilidad del proceso de compra.
 
**Criterios de aceptación**
- [ ] El encargado puede crear una solicitud de reabastecimiento indicando producto y cantidad requerida
- [ ] El gerente recibe la solicitud y puede aprobarla o rechazarla
- [ ] El historial de solicitudes queda registrado con estado, fechas y usuario responsable
- [ ] El encargado puede consultar el estado de sus solicitudes enviadas
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 3 |
| Start Date | 2026-06-22 |
| Target Date | 2026-06-30 |
 
---
 
### 11. Feature: Registro de distribución de insumos por housekeeping
 
**Historia de usuario**  
Como usuario de housekeeping, quiero registrar los insumos distribuidos por habitación atendida, para llevar control del consumo real y descontar del stock disponible.
 
**Criterios de aceptación**
- [ ] El usuario de housekeeping puede registrar el insumo, cantidad y habitación atendida
- [ ] El stock del producto se descuenta automáticamente al confirmar la distribución
- [ ] El sistema permite consultar el stock disponible antes de registrar una distribución
- [ ] El sistema impide registrar una distribución mayor al stock disponible
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` `frontend` `database` |
| Priority | P1 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 3 |
| Start Date | 2026-06-22 |
| Target Date | 2026-06-28 |
 
---
 
### 12. Feature: Dashboard de indicadores para el gerente
 
**Historia de usuario**  
Como gerente, quiero visualizar un dashboard con los principales indicadores de inventario, para tomar decisiones basadas en datos actualizados.
 
**Criterios de aceptación**
- [ ] El dashboard muestra gráficos de consumo por categoría
- [ ] El dashboard muestra el top 5 de productos más usados
- [ ] El dashboard muestra la tendencia de stock en los últimos 30 días
- [ ] Los datos del dashboard se actualizan automáticamente sin recargar la página
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `frontend` `backend` |
| Priority | P1 |
| Size | L |
| Estimate | 8 pts |
| Iteration | Sprint 4 |
| Start Date | 2026-07-01 |
| Target Date | 2026-07-09 |
 
---
 
### 13. Feature: Reporte de consumo de inventario
 
**Historia de usuario**  
Como gerente, quiero generar reportes de consumo de inventario por rango de fechas, para analizar el uso de insumos y tomar decisiones de compra informadas.
 
**Criterios de aceptación**
- [ ] El gerente puede seleccionar un rango de fechas para generar el reporte
- [ ] El reporte muestra nombre del producto y cantidad consumida en el período
- [ ] El reporte puede exportarse en formato PDF
- [ ] El sistema muestra los datos agrupados por categoría de producto
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` `frontend` |
| Priority | P0 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 4 |
| Start Date | 2026-07-05 |
| Target Date | 2026-07-11 |
 
---
 
### 14. Feature: Reporte de costos por proveedor
 
**Historia de usuario**  
Como gerente, quiero exportar un reporte de costos de insumos por proveedor en PDF, para evaluar el gasto por proveedor y optimizar las decisiones de compra.
 
**Criterios de aceptación**
- [ ] El reporte incluye nombre del proveedor, productos suministrados y costo unitario por entrada
- [ ] El reporte puede exportarse en formato PDF
- [ ] El gerente puede filtrar el reporte por proveedor o rango de fechas
- [ ] El PDF incluye el costo total calculado por proveedor
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `backend` `frontend` |
| Priority | P0 |
| Size | M |
| Estimate | 5 pts |
| Iteration | Sprint 4 |
| Start Date | 2026-07-08 |
| Target Date | 2026-07-14 |
 
---
 
### 15. Feature: Visualización de productos con stock mínimo
 
**Historia de usuario**  
Como gerente, quiero ver la lista de productos cuyo stock sea igual o menor al mínimo configurado, para priorizar las solicitudes de reabastecimiento.
 
**Criterios de aceptación**
- [ ] El sistema muestra únicamente los productos con stock actual ≤ stock mínimo
- [ ] La lista incluye nombre del producto, stock actual y stock mínimo
- [ ] La lista puede ser consultada desde el panel del gerente en cualquier momento
- [ ] Los productos se ordenan de mayor a menor urgencia (menor diferencia stock actual vs mínimo)
**Propiedades del Issue**
 
| Propiedad | Valor |
|-----------|-------|
| Status | Todo |
| Assignee | [Completar] |
| Labels | `enhancement` `frontend` `backend` |
| Priority | P1 |
| Size | S |
| Estimate | 3 pts |
| Iteration | Sprint 4 |
| Start Date | 2026-07-10 |
| Target Date | 2026-07-14 |
 
---
