# 🏦 caixabank-prueba-tecnica-backend

Proyecto backend desarrollado con **Spring Boot 3** y **Java 17** como solución a la prueba técnica de CaixaBank.

---

## 📚 Índice

1. [Instrucciones para ejecutar el proyecto](#-1-instrucciones-para-ejecutar-el-proyecto)
2. [Arquitectura y decisiones técnicas](#-2-arquitectura-y-decisiones-técnicas)
3. [Mejoras o extensiones futuras](#-3-mejoras-o-extensiones-futuras)

---

## 🚀 1. Instrucciones para ejecutar el proyecto

### 📋 Requisitos

- Java **17**
- Maven **3.8+**

---

### ▶️ Arranque en local

Desde la raíz del proyecto:

```bash
mvn spring-boot:run
```

La aplicación se inicia en:

```
http://localhost:8080
```

---

## 🗄️ Base de datos

Se utiliza **H2 persistida en fichero**, por lo que los datos se mantienen entre reinicios.

### H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./caixabank_db`
- Usuario: `sa`
- Password: *(vacío)*

El fichero físico se genera en:

```
./caixabank_db.mv.db
```

> En `application.yml` se configura `MODE=PostgreSQL` para simular el comportamiento de PostgreSQL.

---

## 🌐 Endpoints disponibles

**Base path:**

```
/api/v1/solicitud
```

---

### ➕ Crear solicitud

```
POST /api/v1/solicitud/create
```

```json
{
  "nombreSolicitante": "Juan Pérez",
  "importeSolicitado": 1500.50,
  "divisa": "EUR",
  "dni": "12345678A"
}
```

---

### 📄 Listar todas las solicitudes

```
GET /api/v1/solicitud/all
```

---

### 🔍 Obtener solicitud por ID

```
GET /api/v1/solicitud/id/{id}
```

---

### 🔄 Actualizar estado de solicitud

```
PATCH /api/v1/solicitud/update/id/{id}
```

```json
{
  "nuevoEstado": "APROBADA"
}
```

---

## 📌 Estados soportados

- `PENDIENTE`
- `APROBADA`
- `RECHAZADA`
- `CANCELADA`

---

## 🔁 Transiciones permitidas

| Estado actual | Estados permitidos |
|--------------|-------------------|
| PENDIENTE    | APROBADA, RECHAZADA |
| APROBADA     | CANCELADA |

Cualquier transición no contemplada es rechazada por la lógica de negocio.

---

## 🏗️ 2. Arquitectura y decisiones técnicas

### Arquitectura por capas

#### Controller

- Exposición de endpoints REST.
- Validación de requests con `@Valid`.

#### Service

- Lógica de negocio.
- Control de duplicados en estado `PENDIENTE`.
- Creación automática del solicitante si no existe.
- Máquina de estados para control de transiciones.

#### Repository

- Spring Data JPA.
- Métodos derivados de búsqueda.

---

### Modelo de datos

Relación entre entidades:

```
Solicitud  → @ManyToOne → Solicitante
Solicitante → @OneToMany → Solicitud
```

---

### DTO y Mapper

**DTOs utilizados:**

- `SolicitudRequest`
- `UpdateEstadoSolicitudRequest`
- `SolicitudResponse`

Se utiliza un **mapper manual** para desacoplar entidades del modelo de exposición.

---

### Gestión de errores

- Excepción de dominio: `ApiException`
- `GlobalExceptionHandler`:
  - `ApiError` para errores de negocio.
  - `ProblemDetail` para errores de validación.

---

## ⚙️ Decisiones técnicas

- Spring Boot 3 + Java 17
- Base de datos H2 persistida en fichero
- Bean Validation
- Máquina de estados implementada en `enum`
- JPA con `ddl-auto:update`

---

## 🚧 3. Mejoras o extensiones futuras

### Funcionales

- Búsqueda y filtrado por DNI, estado, divisa o fechas.
- Paginación y ordenación.
- Cancelación controlada de solicitudes.
- Control de duplicados mediante restricción única en base de datos.

---

### Técnicas / Arquitecturales

- Documentación con Swagger / OpenAPI.
- Seguridad con Spring Security.
- Validaciones de formato (DNI, divisa ISO).

---


