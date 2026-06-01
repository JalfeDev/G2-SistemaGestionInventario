# Getting Started

## Guides
The following guides illustrate how to use some features concretely:
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.6/maven-plugin/build-image.html)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

## Dependencias del proyecto
1. Spring Data JPA
2. Spring Security
3. JWT
4. Spring Web
5. Validation
6. Mail
7. Kafka
8. Lombok
9. MySQL
10. Dotenv

## Autenticacion
Recibe
```
{
  "usuario":"admin",
  "contrasena":"admin123"
}
```
Devuelve
```
{
  "token":"JWT...",
  "rol":"GERENTE",
  "nombre":"Administrador"
}
```

## Endpoints
```POST /api/auth/login```
```GET /api/auth/me```

## Estructura
```
main
├───java
│   └───com
│       └───g2
│           └───demo
│               ├───config
│               ├───controller
│               ├───dto
│               ├───entity
│               ├───repository
│               └───service
```

## Los Requisitos funcionales
# Sistema de Gestión de Inventario - Hotel Pirámide

## Requisitos Funcionales

### Generales

* **RF-18:** El sistema deberá limitar ciertas funcionalidades dependiendo del rol asignado al usuario.
* **RF-31:** El sistema deberá permitir registrar proveedores de insumos.
* **RF-32:** Un producto podrá estar asociado a uno o varios proveedores.
* **RF-33:** Cada ingreso de inventario deberá almacenar el costo unitario pagado al proveedor.
* **RF-34:** El sistema deberá mantener un historial de precios de compra por proveedor y producto.
* **RF-35:** El sistema deberá calcular el valor económico del inventario utilizando los costos registrados.

### Gerente(a)

* **RF-01:** El sistema deberá permitir al gerente generar reportes de consumo de inventario por rango de fechas, mostrando el nombre del producto y la cantidad consumida.
* **RF-02:** El sistema deberá permitir al gerente visualizar una lista de productos cuyo stock actual sea menor o igual al stock mínimo configurado.
* **RF-03:** El sistema deberá permitir al gerente exportar los reportes de consumo en formato PDF.
* **RF-04:** El sistema deberá permitir al gerente exportar los costos de insumo por cada proveedor en formato PDF.
* **RF-19:** El sistema deberá permitir al gerente visualizar un panel de indicadores.
* **RF-20:** El sistema deberá permitir al gerente visualizar un panel de indicadores en tiempo real, incluyendo gráficos de consumo por categoría, top 5 productos más utilizados y tendencia de stock de los últimos 30 días.
* **RF-21:** El sistema deberá mostrar al gerente los cinco insumos con mayor consumo durante un período determinado.
* **RF-22:** El sistema deberá mostrar al gerente la evolución histórica del stock durante los últimos 30 días.
* **RF-24:** El gerente podrá aprobar o rechazar solicitudes de reabastecimiento generadas por el encargado de almacén.

### Administrador(a)

* **RF-05:** El sistema deberá permitir al administrador crear nuevos usuarios registrando nombre, apellido, nombre de usuario y rol asignado.
* **RF-06:** El sistema deberá permitir al administrador asignar a un usuario uno de los siguientes roles: gerente, encargado de almacén o housekeeping.
* **RF-07:** El sistema deberá permitir al administrador modificar el rol asignado a un usuario.
* **RF-08:** El sistema deberá permitir al administrador configurar el stock mínimo, las categorías de productos y las unidades de medida.
* **RF-09:** El sistema deberá permitir al administrador acceder a todas las funcionalidades disponibles.
* **RF-10:** El sistema deberá permitir registrar nuevos productos indicando nombre y categoría.
* **RF-11:** El sistema deberá permitir modificar la información de los productos.
* **RF-12:** El sistema deberá permitir eliminar productos.
* **RF-29:** El administrador podrá cargar múltiples productos mediante archivos CSV.
* **RF-30:** El sistema deberá validar el formato, las columnas y los registros antes de realizar la importación.

### Encargado de Almacén

* **RF-13:** El sistema deberá permitir registrar entradas de insumos indicando producto, cantidad recibida y fecha de ingreso.
* **RF-14:** El sistema deberá permitir consultar el stock actual disponible por producto.
* **RF-15:** El sistema deberá generar una notificación automática cuando el stock actual de un producto sea menor o igual al stock mínimo configurado.
* **RF-23:** El encargado de almacén podrá generar solicitudes de compra para productos con stock bajo.
* **RF-25:** El sistema deberá conservar todo el historial de solicitudes de compra y sus estados.
* **RF-26:** El sistema deberá registrar quién creó, aprobó o rechazó cada solicitud para garantizar la trazabilidad.
* **RF-27:** El sistema deberá enviar correos automáticos cuando un producto alcance stock crítico.
* **RF-28:** Las alertas deberán enviarse al gerente y al encargado de almacén.

### Housekeeping

* **RF-16:** El sistema deberá permitir registrar la cantidad de insumos distribuidos por cada habitación atendida.
* **RF-17:** El sistema deberá permitir consultar el stock disponible de los insumos antes de realizar una solicitud.

---

## Requisitos Funcionales Adicionales

* **RF-36:** El sistema deberá registrar las salidas de inventario asociadas a la distribución de insumos.
* **RF-37:** El sistema deberá mantener un historial completo de movimientos de inventario (Kardex).
* **RF-38:** El sistema deberá permitir anular movimientos incorrectos mediante ajustes autorizados.
* **RF-39:** El sistema deberá permitir registrar y administrar habitaciones del hotel.
* **RF-40:** El sistema deberá permitir consultar el historial de movimientos por producto.
* **RF-41:** El sistema deberá permitir realizar ajustes de inventario por diferencias encontradas en auditorías o conteos físicos.
* **RF-42:** El sistema deberá registrar la fecha, hora y usuario responsable de cada modificación realizada en el sistema.
* **RF-43:** El sistema deberá impedir que el stock de un producto sea negativo.
* **RF-44:** El sistema deberá generar notificaciones internas además de los correos electrónicos automáticos.

---

## Requisitos No Funcionales

* **RNF-01:** El sistema deberá responder a cualquier solicitud del usuario en un tiempo máximo de 3 segundos bajo condiciones normales de operación y con hasta 50 usuarios concurrentes.
* **RNF-02:** El sistema deberá estar disponible al menos el 99% del tiempo mensual, excluyendo los periodos programados de mantenimiento.
* **RNF-03:** El sistema deberá restringir el acceso a las funcionalidades según el rol asignado al usuario.
* **RNF-04:** El sistema deberá realizar copias de seguridad automáticas de la base de datos al menos una vez cada 24 horas.
* **RNF-05:** El sistema deberá requerir autenticación antes de permitir el acceso a cualquier funcionalidad.
* **RNF-06:** El sistema deberá garantizar consistencia transaccional durante las operaciones de inventario.
* **RNF-07:** Toda operación crítica deberá registrar usuario, fecha, hora y acción realizada.
* **RNF-08:** La base de datos deberá soportar al menos 100,000 registros de movimientos sin degradación significativa del rendimiento.
* **RNF-09:** El sistema deberá funcionar correctamente en los navegadores Chrome, Edge y Firefox.
* **RNF-10:** El sistema deberá permitir restaurar la base de datos a partir de respaldos automáticos.

