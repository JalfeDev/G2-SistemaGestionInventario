# Sistema de Gestión de Inventario — Hotel Pirámide

## Demo

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

*(Las historias 6 a la 15 permanecen con la misma estructura, únicamente cambiando los encabezados "## Historia de Usuario" por "#### Historia de Usuario" y "## Criterios de Aceptación" por "#### Criterios de aceptación". No es necesario modificar el contenido porque ya es correcto y natural.)*

---

## Resumen

### Sprint

| Sprint | Historias de usuario | Puntos | Fechas |
|--------|----------------------|--------|--------|
| Sprint 1 | HU-01, HU-02, HU-03, HU-04, HU-05, HU-09 | 31 pts | 2026-05-25 → 2026-06-07 |
| Sprint 2 | HU-06, HU-07, HU-08, HU-10, HU-11, HU-12, HU-13, HU-14, HU-15 | 57 pts | 2026-06-09 → 2026-07-17 |
| **Total** | **15 HU** | **88 pts** | **8 semanas** |

### Release

| Release | Historias de usuario |
|---------|----------------------|
| Release 1 | HU-01 a HU-09 |
| Release 2 | HU-10 a HU-15 |

---

## Prioridades

| Nivel | Significado | Historias de usuario |
|-------|-------------|----------------------|
| P0 | Funcionalidades críticas | HU-01, HU-02, HU-03, HU-04 |
| P1 | Funcionalidades principales | HU-05, HU-06, HU-07, HU-08, HU-09, HU-10, HU-11, HU-12, HU-14, HU-15 |
| P2 | Funcionalidades complementarias | HU-13 |