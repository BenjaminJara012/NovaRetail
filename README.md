# NovaRetail

Sistema de comercio electrónico desarrollado mediante una arquitectura de microservicios utilizando Java, Spring Boot y PostgreSQL.

## 1. Descripción del proyecto

NovaRetail es una aplicación backend orientada a la gestión de productos, usuarios, carritos de compra y pedidos.

El sistema utiliza una arquitectura de microservicios, donde cada servicio tiene responsabilidades independientes y puede comunicarse con otros mediante API REST.

## 2. Tecnologías utilizadas

- Java 21
- Spring Boot
- Maven
- PostgreSQL
- Docker y Docker Compose
- Spring Data JPA / Hibernate
- JUnit 5 y Mockito
- Postman
- Git y GitHub

## 3. Arquitectura del proyecto

| Microservicio | Puerto | Responsabilidad |
|---|---|---|
| ms-productos | 8081 | Gestión de productos e inventario |
| ms-usuarios | 8082 | Gestión de usuarios |
| ms-carrito | 8083 | Gestión de carritos de compra |
| ms-pedidos | 8084 | Gestión de pedidos |

Cada microservicio mantiene sus propias responsabilidades y utiliza persistencia de datos.

El microservicio `ms-carrito` se comunica con `ms-productos` mediante HTTP utilizando `RestClient`, para consultar la información y disponibilidad de los productos.

## 4. Requisitos previos

Antes de ejecutar el proyecto, instalar:

- Git
- Java JDK 21
- Docker Desktop con Docker Compose
- Postman (opcional, para probar los endpoints)

Los microservicios incluyen Maven Wrapper, por lo que no es obligatorio instalar Maven de forma independiente.

## 5. Clonar el repositorio

Abrir una terminal y ejecutar:

```bash
git clone https://github.com/BenjaminJara012/NovaRetail.git
cd NovaRetail
git checkout develop
```

La rama `develop` contiene la versión integrada del proyecto.

## 6. Bases de datos con Docker Compose

NovaRetail utiliza PostgreSQL 17 para la persistencia de datos. La infraestructura contempla una base de datos independiente para cada microservicio, siguiendo el principio de separación de responsabilidades.

| Microservicio | Base de datos | Puerto local |
|---|---|---|
| ms-productos | novaretail_productos | 5433 |
| ms-carrito | novaretail_carrito | 5434 |
| ms-usuarios | novaretail_usuarios | 5435 |
| ms-pedidos | novaretail_pedidos | 5436 |

### 7. Iniciar las bases de datos

Desde la carpeta raíz del proyecto, ejecutar:

```powershell
docker compose up -d
```

Para comprobar que los contenedores están en ejecución:

```powershell
docker compose ps
```

Para detenerlos sin eliminar los datos almacenados:

```powershell
docker compose down
```

Los datos se conservan mediante volúmenes persistentes de Docker.

**Nota:** Docker Compose inicia las cuatro instancias de PostgreSQL. Los microservicios Java se ejecutan por separado mediante Maven o sus archivos JAR. La disponibilidad de las bases de datos no implica que los cuatro microservicios estén funcionando.

### 8. Ejecutar los microservicios implementados

Se requiere Java 21 y Docker Desktop.

Con las bases de datos iniciadas, abrir una terminal para productos:

```powershell
cd ms-productos
.\mvnw.cmd spring-boot:run
```

En otra terminal, desde la raíz del repositorio, iniciar carrito:

```powershell
cd ms-carrito
.\mvnw.cmd spring-boot:run
```

Los servicios estarán disponibles en:

- Productos: `http://localhost:8081/api/productos`
- Carrito: `http://localhost:8083/api/carritos/{usuarioId}`

El microservicio de carrito consulta productos mediante HTTP, por lo que `ms-productos` debe estar disponible para realizar operaciones que requieran información de productos.

### 9. Pruebas y empaquetado

Para compilar, ejecutar las pruebas y generar el archivo JAR de cada microservicio, ingresar a su carpeta y ejecutar:

```powershell
.\mvnw.cmd clean package
```

Los archivos generados se encuentran en la carpeta `target` de cada microservicio.

Las pruebas automatizadas utilizan JUnit 5 y Mockito. Las operaciones REST también pueden verificarse mediante Postman.

### 10. Estado de integración

Los microservicios `ms-productos` y `ms-carrito` cuentan con implementación y pruebas verificadas. Las bases de datos de `ms-usuarios` y `ms-pedidos` están configuradas en Docker Compose; su integración y ejecución deben verificarse cuando estén disponibles sus implementaciones.

## 11. Endpoints principales

### Productos

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/productos` | Listar productos |
| GET | `/api/productos/{id}` | Buscar producto |
| POST | `/api/productos` | Crear producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |

### Carrito

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/carritos/{usuarioId}` | Consultar carrito |
| POST | `/api/carritos/{usuarioId}/items` | Agregar producto |
| PUT | `/api/carritos/{usuarioId}/items/{itemId}` | Actualizar cantidad |
| DELETE | `/api/carritos/{usuarioId}/items/{itemId}` | Eliminar producto |
| DELETE | `/api/carritos/{usuarioId}/items` | Vaciar carrito |

Los endpoints pueden probarse mediante Postman.

## 13. Ejecutar pruebas unitarias

### Pruebas de productos

Desde `ms-productos`:

```powershell
.\mvnw.cmd "-Dtest=ProductoServiceTest" test
```

### Pruebas de carrito

Desde `ms-carrito`:

```powershell
.\mvnw.cmd "-Dtest=CarritoServiceTest" test
```

Las pruebas utilizan JUnit 5 y Mockito.

Actualmente se han implementado:

- 4 pruebas unitarias de productos.
- 5 pruebas unitarias de carrito.

## 14. Compilar y generar archivos JAR

Desde la carpeta de cada microservicio:

```powershell
.\mvnw.cmd clean package
```

Si la compilación y las pruebas terminan correctamente, Maven genera el archivo ejecutable dentro de la carpeta `target/`.

Para ejecutar el archivo generado:

```powershell
java -jar target/nombre-del-archivo.jar
```

Reemplazar `nombre-del-archivo.jar` por el nombre real del archivo generado.

## 15. Control de versiones

El proyecto utiliza Git y GitHub con una estrategia de ramas:

- `main`: rama principal.
- `develop`: rama de integración.
- `feature/*`: ramas para desarrollar funcionalidades y pruebas.

Los cambios se integran mediante Pull Requests.

## 16. Autores

Proyecto académico desarrollado en parejas para la asignatura Java: Diseño y Construcción de Soluciones Nativas en Nube.