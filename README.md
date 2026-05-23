# Sistema de Gestión de Inventario — Hotel Pirámide
 
> **Ingeniería de Software II · Grupo 2**  
> Universidad de Lima — Facultad de Ingeniería de Sistemas
 
---
 
## Descripción del proyecto
 
Sistema web para gestionar el inventario de insumos del Hotel Pirámide. Permite registrar entradas de productos, controlar el stock disponible, generar alertas de reabastecimiento y producir reportes de consumo y costos por proveedor.
 
El sistema está orientado a cuatro roles principales: **Administrador**, **Gerente**, **Encargado de Almacén** y **Housekeeping**, cada uno con acceso restringido a las funcionalidades de su responsabilidad.
 
---
## Creación de Historias de Usuario

### 1. Feature: Autenticación de usuarios
  **Historia de usuario**
Como usuario del sistema, quiero iniciar sesión con mis credenciales, para acceder únicamente a las funcionalidades permitidas por mi rol. 

  **Criterios de aceptación**
- [ ] El sistema solicita nombre de usuario y contraseña antes de permitir accso a cualquier funcionalidad
- [ ] El sistema restringe el acceso a módulos según el rol asignado al usuario
- [ ] El sistrema muestra mensaje de error si las credenciales son incorrectas
 **Propiedades del Issue**

- Status: Todo

- Labels: enhancement, backend, frontend

- Priority: P0

- Size: M

- Estimate: 5 pts

- Iteration: Sprint 1

- Start Date: 2026-05-25

- Target Date: 2026-05-29
### 2. Feature: Gestión de usuarios
   **Historia de usuario **
Como administrador, quiero crear y gestionar usuarios con roles asignados,
para controlar el acceso al sistema según las responsabilidades de cada persona.

  **Criterios de aceptación**
- [ ] El administrador puede registrar un nuevo usuario ingresando nombre, apellido, nombre de usuario y rol
- [ ] Los roles disponibles para asignar son: gerente, encargado de almacén y housekeeping
- [ ] El administrador puede modificar el rol de un usuario existente
- [ ] Un usuario sin rol asignado no puede acceder al sistema
**Propiedades del Issue**

- Status: Todo

- Labels: enhancement, backend, frontend, database

- Priority: P0

- Size: L

- Estimate: 8 pts

- Iteration: Sprint 1

- Start Date: 2026-05-25

- Target Date: 2026-06-02


### 3. Feature: Gestión de productos
   **Historia de usuario**
Como administrador, quiero registrar, modificar y eliminar productos del catálogo,
para mantener actualizada la información de insumos disponibles en el hotel.

  **Criterios de aceptación**
- [ ] El administrador puede crear un producto ingresando nombre y categoría
- [ ] El administrador puede editar el nombre y categoría de un producto existente
- [ ] El administrador puede eliminar un producto cuando corresponda
- [ ] El sistema valida que no se registren productos duplicados
**Propiedades del Issue**

- Status: Todo

- Labels: enhancement, backend, frontend, database

- Priority: P1

- Size: M

- Estimate: 5 pts

- Iteration: Sprint 1

- Start Date: 2026-05-30

- Target Date: 2026-06-04

### 4. Feature: Importación de productos desde CSV
**Historia de usuario**
Como administrador, quiero cargar el catálogo de productos desde un archivo CSV,
para evitar el ingreso manual uno por uno y agilizar la configuración inicial del sistema.

 **Criterios de aceptación**
- [ ] El sistema acepta archivos en formato .csv con los campos: nombre y categoría
- [ ] El sistema muestra un resumen de productos importados exitosamente y los que tuvieron error
- [ ] El sistema rechaza registros con datos incompletos o duplicados e informa el motivo
- [ ] La importación no sobreescribe productos existentes sin confirmación del usuario
**Propiedades del Issue**
- Status: Todo

- Labels: enhancement, backend, frontend

- Priority: P1

- Size: M

- Estimate: 5 pts

- Iteration: Sprint 2

- Start Date: 2026-06-05

- Target Date: 2026-06-10
### 5. Feature: Configuración del sistema
  **Historia de usuario**
Como administrador, quiero configurar el stock mínimo, categorías y unidades de medida,
para adaptar el sistema a las necesidades operativas del hotel.

  **Criterios de aceptación**
- [ ] El administrador puede definir y modificar el stock mínimo por producto
- [ ] El administrador puede crear, editar y eliminar categorías de productos
- [ ] El administrador puede crear, editar y eliminar unidades de medida
- [ ] Los cambios de configuración se reflejan de inmediato en el sistema
**Propiedades del Issue**
- Status: Todo

- Labels: enhancement, backend, frontend, database

- Priority: P1

- Size: L

- Estimate: 8 pts

- Iteration: Sprint 2

- Start Date: 2026-06-06

- Target Date: 2026-06-14


### 6. Feature: Registro de entradas de insumos
   **Historia de usuario**
Como encargado de almacén, quiero registrar las entradas de insumos recibidos,
para mantener actualizado el stock disponible en el sistema.

  **Criterios de aceptación**
- [ ] El encargado puede registrar una entrada indicando: producto, cantidad recibida y fecha de ingreso
- [ ] El stock del producto se actualiza automáticamente al guardar la entrada
- [ ] El sistema valida que la cantidad ingresada sea mayor a cero
- [ ] El registro queda almacenado con fecha, hora y usuario que realizó la entrada
**Propiedades del Issue**
- Status: Todo

- Labels: enhancement, backend, database

- Priority: P0

- Size: M

- Estimate: 5 pts

- Iteration: Sprint 2

- Start Date: 2026-06-10

- Target Date: 2026-06-15      
### 7. Feature: Historial de precios por proveedor
   **Historia de usuario**
Como encargado de almacén, quiero registrar el costo unitario en cada entrada de insumo por proveedor,
para poder calcular el costo total del inventario y hacer seguimiento de precios.

  **Criterios de aceptación**
- [ ] Al registrar una entrada, el encargado puede ingresar el proveedor y el costo unitario del producto
- [ ] El sistema almacena el historial de precios por proveedor y producto
- [ ] El sistema calcula automáticamente el costo total de la entrada (cantidad × precio unitario)
- [ ] El administrador y gerente pueden consultar el historial de precios por proveedor
**Propiedades del Issue**
- Status: Todo

- Labels: enhancement, backend, database

- Priority: P1

- Size: M

- Estimate: 5 pts

- Iteration: Sprint 2

- Start Date: 2026-06-12

- Target Date: 2026-06-18

     
### 8. Feature: Consulta de stock actual
  **Historia de usuario**
Como encargado de almacén, quiero consultar el stock actual disponible por producto,
para tomar decisiones oportunas sobre reabastecimiento.

  **Criterios de aceptación**
- [ ] El encargado puede ver una lista de todos los productos con su stock actual
- [ ] El sistema permite filtrar productos por categoría
- [ ] Los productos con stock igual o menor al mínimo se destacan visualmente
- [ ] La información de stock se muestra en tiempo real
**Propiedades del Issue**
- Status: Todo

- Labels: enhancement, frontend, backend

- Priority: P1

- Size: M

- Estimate: 5 pts

- Iteration: Sprint 3

- Start Date: 2026-06-16

- Target Date: 2026-06-21


### 9. Feature: Notificaciones automáticas de stock crítico
   **Historia de usuario**
Como encargado de almacén, quiero recibir una notificación automática cuando un producto alcance el stock mínimo,
para gestionar el reabastecimiento a tiempo.

  **Criterios de aceptación**
- [ ] El sistema genera una notificación automática cuando el stock de un producto es menor o igual al mínimo configurado
- [ ] La notificación se envía por correo electrónico al encargado de almacén y al gerente
- [ ] La notificación incluye el nombre del producto, stock actual y stock mínimo
- [ ] No se generan notificaciones duplicadas por el mismo producto en el mismo evento
**Propiedades del Issue**
- Status: Todo

- Labels: enhancement, backend

- Priority: P0

- Size: L

- Estimate: 8 pts

- Iteration: Sprint 3

- Start Date: 2026-06-18

- Target Date: 2026-06-26      
### 10. Feature: Módulo de solicitudes de reabastecimiento
  **Historia de usuario**
Como encargado de almacén, quiero generar solicitudes formales de reabastecimiento para aprobación del gerente,
para dejar trazabilidad del proceso de compra.

  **Criterios de aceptación**
- [ ] El encargado puede crear una solicitud de reabastecimiento indicando producto y cantidad requerida
- [ ] El gerente recibe la solicitud y puede aprobarla o rechazarla
- [ ] El historial de solicitudes queda registrado con estado, fechas y usuario responsable
- [ ] El encargado puede consultar el estado de sus solicitudes enviadas
 **Propiedades del Issue**

- Status: Todo

- Labels: enhancement, backend, frontend, database

- Priority: P1

- Size: L

- Estimate: 8 pts

- Iteration: Sprint 3

- Start Date: 2026-06-22

- Target Date: 2026-06-30


