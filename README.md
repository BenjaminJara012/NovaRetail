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

## 6. Iniciar las bases de datos

Desde la carpeta principal del proyecto, ejecutar:

```bash
docker compose up -d
```

Este comando inicia los servicios de PostgreSQL definidos en `docker-compose.yml`.

Para comprobar su estado:

```bash
docker compose ps
```

## 7. Instalar dependencias y ejecutar los microservicios

Cada microservicio contiene su propio proyecto Spring Boot.

En Windows, abrir una terminal para cada servicio.

### ms-productos

```powershell
cd ms-productos
.\mvnw.cmd spring-boot:run
```

Disponible en:

`http://localhost:8081`

### ms-carrito

Desde la carpeta principal del repositorio:

```powershell
cd ms-carrito
.\mvnw.cmd spring-boot:run
```

Disponible en:

`http://localhost:8083`

Para utilizar las funcionalidades del carrito, `ms-productos` debe estar ejecutándose.

### ms-usuarios y ms-pedidos

Los microservicios de usuarios y pedidos deben ejecutarse desde sus respectivos directorios, siguiendo su configuración de Spring Boot y sus dependencias de base de datos.

Sus instrucciones específicas se completarán al integrar las implementaciones correspondientes.

## 8. Endpoints principales

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

## 9. Ejecutar pruebas unitarias

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

## 10. Compilar y generar archivos JAR

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

## 11. Control de versiones

El proyecto utiliza Git y GitHub con una estrategia de ramas:

- `main`: rama principal.
- `develop`: rama de integración.
- `feature/*`: ramas para desarrollar funcionalidades y pruebas.

Los cambios se integran mediante Pull Requests.

## 12. Autores

Proyecto académico desarrollado en parejas para la asignatura Java: Diseño y Construcción de Soluciones Nativas en Nube.