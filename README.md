1) Instrucciones para ejecutar el proyecto
Requisitos

Java 17

Maven 3.8+ (recomendado)

Arranque en local

Desde la raíz del proyecto:

mvn spring-boot:run


La aplicación levanta en:

http://localhost:8080

Base de datos (H2 en fichero)

La configuración usa H2 persistida en disco, por lo que los datos se mantienen entre reinicios.

H2 Console: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:file:./caixabank_db

User: sa

Password: (vacío)

El fichero de BD queda en la raíz: ./caixabank_db.mv.db

Nota: en application.yml se usa MODE=PostgreSQL para acercar el comportamiento de H2 a PostgreSQL.

Endpoints disponibles (API)

Base path: /api/v1/solicitud

Crear solicitud

POST /api/v1/solicitud/create

Body ejemplo:

{
  "nombreSolicitante": "Juan Pérez",
  "importeSolicitado": 1500.50,
  "divisa": "EUR",
  "dni": "12345678A"
}


Listar todas

GET /api/v1/solicitud/all

Obtener por id

GET /api/v1/solicitud/id/{id}

Actualizar estado

PATCH /api/v1/solicitud/update/id/{id}

Body ejemplo:

{ "nuevoEstado": "APROBADA" }


Estados soportados: PENDIENTE, APROBADA, RECHAZADA, CANCELADA.

Transiciones permitidas (según EstadoSolicitud.transicionarA):

PENDIENTE -> APROBADA | RECHAZADA

APROBADA -> CANCELADA

2) Breve descripción de la arquitectura y decisiones técnicas
Arquitectura (capas)

Controller (SolicitudController)

Expone endpoints REST.

Usa @Valid para validar requests.

Service (SolicitudService, SolicitanteService)

Contiene la lógica de negocio:

Evita duplicados “en proceso” comprobando si existe una solicitud del mismo DNI + divisa + importe en estado PENDIENTE.

Crea Solicitante si no existe.

Gestiona la máquina de estados y valida transiciones.

Repository (Spring Data JPA)

SolicitudRepository y SolicitanteRepository.

Un método derivado para búsqueda: findBySolicitanteDniAndDivisaAndImporteSolicitado(...).

Modelo (JPA Entities)

Solicitud y Solicitante con relación:

Solicitud -> @ManyToOne a Solicitante

Solicitante -> @OneToMany a Solicitud

DTO + Mapper

DTOs para entrada/salida (SolicitudRequest, UpdateEstadoSolicitudRequest, SolicitudResponse)

Mapper manual SolicitudResponseMapper para desacoplar entidad de respuesta.

Gestión de errores

Excepción de dominio ApiException con code y HttpStatus.

GlobalExceptionHandler devuelve errores consistentes:

ApiError para errores de negocio y JSON malformado/enum inválido.

ProblemDetail para validaciones (MethodArgumentNotValidException) incluyendo un map con campos inválidos.

Decisiones técnicas

Spring Boot 3 + Java 17: stack moderno y estándar.

H2 persistida a fichero: facilita ejecución local sin infraestructura.

Validación con Bean Validation: asegura integridad de datos de entrada (campos obligatorios y importe > 0).

Máquina de estados en un enum: encapsula reglas de transición (EstadoSolicitud) y simplifica el service.

JPA con ddl-auto: update: útil para una prueba técnica (arranque rápido sin migraciones).

3) Mejoras o extensiones con más tiempo (funcionales y técnicas/arquitecturales)

Búsqueda y filtrado de solicitudes (por DNI, estado, rango de fechas, divisa…).

Paginación y ordenación en el endpoint de listado (/all) para escalabilidad.

Cancelación controlada: por ejemplo, permitir PENDIENTE -> CANCELADA si el enunciado lo contempla (ahora no está permitido).

Evitar duplicados de forma robusta:

Añadir una restricción única (o estrategia de idempotencia) para el caso “DNI+divisa+importe+estado=PENDIENTE”, y así soportar concurrencia real.


Si aplica: autenticación/autorización (Spring Security), validaciones adicionales (formato DNI, divisa, etc.).
