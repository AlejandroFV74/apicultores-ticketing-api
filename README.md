# 🎟️ Sistema de Ticketing para Eventos — Backend

API REST para la gestión de eventos, usuarios, reservas y compra de entradas, desarrollada con **Java y Spring Boot**. El backend implementa una arquitectura por capas y proporciona los servicios necesarios para gestionar el ciclo de vida de los tickets, autenticación de usuarios, reservas temporales y validación de entradas mediante códigos QR.

El proyecto forma parte de un sistema de ticketing compuesto por **Frontend + Backend + Base de Datos**, siguiendo una arquitectura monolítica a nivel de solución y una arquitectura **N-Capas** para el backend.

## 📌 Descripción

El sistema permite administrar el proceso de venta y control de entradas para eventos, incluyendo:

* Gestión de usuarios y roles.
* Autenticación mediante JWT.
* Gestión de eventos.
* Gestión de localidades y asientos.
* Selección y reserva temporal de asientos.
* Compra de tickets.
* Control de cantidad máxima de tickets por usuario.
* Generación de códigos QR para tickets.
* Validación de tickets.
* Consulta del historial de compras.
* Gestión del estado de tickets.
* Expiración automática de reservas.
* Control de acceso para administradores y organizadores.

El backend expone una **API REST** consumida por el frontend de la aplicación.


## 🏗️ Arquitectura

El backend utiliza una arquitectura **N-Capas**, buscando separar las responsabilidades de cada componente y evitar que la lógica de negocio quede concentrada en los controladores.

┌──────────────────────────────────────────┐
│              Cliente / Frontend          │
│          React / Angular / HTTP          │
└────────────────────┬─────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────┐
│              Controllers                 │
│        Exposición de API REST            │
└────────────────────┬─────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────┐
│                Services                  │
│          Lógica de negocio               │
└────────────────────┬─────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────┐
│              Repositories                │
│       Acceso y persistencia de datos     │
└────────────────────┬─────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────┐
│              PostgreSQL                  │
│             Base de datos                │
└──────────────────────────────────────────┘

### Principios aplicados

* Separación de responsabilidades.
* Inyección de dependencias mediante Spring.
* DTOs para entrada y salida de información.
* Mapeo entre entidades y DTOs.
* Manejo centralizado de excepciones.
* Validación de datos de entrada.
* Autenticación y autorización mediante JWT.
* Separación de lógica de negocio y persistencia.
* Uso de servicios especializados para operaciones complejas.
* Control de estados de tickets y reservas.

La arquitectura N-Capas es parte de la estructura definida para el backend del proyecto.

---

# 🛠️ Tecnologías

| Tecnología         | Uso                                    |
| ------------------ | -------------------------------------- |
| Java               | Lenguaje principal                     |
| Spring Boot        | Framework backend                      |
| Spring Web         | API REST                               |
| Spring Data JPA    | Persistencia                           |
| Hibernate          | ORM                                    |
| PostgreSQL         | Base de datos                          |
| Spring Security    | Seguridad y autenticación              |
| JWT                | Autenticación basada en tokens         |
| Maven              | Gestión de dependencias y construcción |
| Git / GitHub       | Control de versiones                   |
| Insomnia / Postman | Pruebas de API                         |

---

# 📂 Estructura del proyecto

La estructura está organizada por responsabilidades para mantener el código desacoplado y facilitar su mantenimiento.

src/
└── main/
    ├── java/
    │   └── .../
    │       ├── config/
    │       ├── controller/
    │       ├── dto/
    │       ├── entity/
    │       ├── exception/
    │       ├── mapper/
    │       ├── repository/
    │       ├── security/
    │       ├── service/
    │       └── ...
    │
    └── resources/
        ├── application.properties
        └── ...

### Responsabilidad de las capas

**Controller**

Recibe las solicitudes HTTP y expone los endpoints de la API.

**Service**

Contiene las reglas y procesos de negocio.

**Repository**

Se encarga del acceso a la base de datos mediante Spring Data JPA.

**Entity**

Representa las entidades persistidas en PostgreSQL.

**DTO**

Define los objetos utilizados para recibir y enviar información mediante la API sin exponer directamente las entidades.

**Mapper**

Realiza la conversión entre entidades y DTOs.

**Security**

Contiene los componentes relacionados con autenticación, autorización y JWT.

**Exception**

Centraliza las excepciones y respuestas de error de la API.


# 🔐 Autenticación y autorización

El sistema utiliza **JWT (JSON Web Token)** para autenticar a los usuarios.

El flujo general es:

Usuario
   │
   ▼
POST /auth/login
   │
   ▼
Validación de credenciales
   │
   ▼
Generación de JWT
   │
   ▼
Cliente almacena el token
   │
   ▼
Authorization: Bearer <token>
   │
   ▼
JwtAuthenticationFilter
   │
   ▼
Validación del token
   │
   ▼
Acceso al recurso protegido

Los roles principales definidos para el sistema son:

* `ADMIN`
* `ORGANIZER`
* `BUYER` / Cliente

Los permisos se aplican dependiendo del recurso y operación solicitada.

---

# 👥 Roles

## Administrador

Responsable de la administración general del sistema.

Puede realizar operaciones administrativas y de control sobre los recursos protegidos.

## Organizador

Responsable de administrar y operar eventos.

También puede realizar operaciones relacionadas con la validación y control de acceso de tickets.

## Cliente

Usuario que puede consultar eventos, reservar/comprar entradas y consultar su historial.

Los tres perfiles forman parte de los roles definidos originalmente para el sistema.

---

# 🎫 Gestión de tickets

Los tickets representan las entradas adquiridas por los usuarios para asistir a un evento.

Cada ticket mantiene información relacionada con:

* Evento.
* Asiento.
* Propietario.
* Estado.
* Código QR.
* Información de pago.
* Fechas relevantes.

### Estados principales

AVAILABLE
    │
    ▼
RESERVED
    │
    ▼
PAID
    │
    ▼
USED

También se contemplan estados relacionados con:

REFUNDED
TRANSFERRED

El diseño funcional del proyecto contempla los estados `Disponible`, `Reservado`, `Usado`, `Reembolsado` y `Transferido`.

---

# 💺 Reservas temporales

El sistema implementa reservas temporales de asientos.

Cuando un usuario selecciona determinados asientos, estos pueden quedar reservados durante un período limitado antes de completar la compra.

### Tiempo de reserva

15 minutos

El backend utiliza un proceso programado para detectar y procesar reservas expiradas.

Configuración utilizada:

properties
app.reservation.expiry-check-ms=300000

Esto permite ejecutar periódicamente el proceso de expiración sin depender exclusivamente del temporizador del frontend.

### Estados de una reserva

ACTIVE
   │
   ├──► CONFIRMED
   │
   └──► EXPIRED

La especificación del proyecto contempla reservas activas, expiradas y confirmadas.

---

# 🔄 Flujo de reserva


1. Usuario selecciona asientos
          │
          ▼
2. Backend verifica disponibilidad
          │
          ▼
3. Se crea la reserva
          │
          ▼
4. Los asientos quedan temporalmente bloqueados
          │
          ▼
5. Usuario completa la compra
          │
       ┌──┴──┐
       │     │
      Sí     No
       │     │
       ▼     ▼
    Compra  Expira
    ticket  reserva
       │     │
       ▼     ▼
   Confirmado Disponible


Esto permite evitar que dos usuarios puedan adquirir simultáneamente los mismos asientos.

---

# 🎟️ Compra de tickets

El proceso de compra valida, entre otros aspectos:

* Existencia del evento.
* Existencia de los asientos.
* Disponibilidad de los asientos.
* Estado de la reserva.
* Propietario de la reserva.
* Límite de tickets permitido por usuario.
* Estado del proceso de pago.

Una regla fundamental del sistema es que **un ticket debe estar asociado a por lo menos un asiento**, evitando la existencia de tickets sin asignación de asiento.

---

# 📱 Código QR

Cada ticket adquirido cuenta con un **código QR único**.

El QR permite identificar el ticket durante el proceso de ingreso al evento.

Flujo:


Compra confirmada
       │
       ▼
Generación de QR
       │
       ▼
QR asociado al ticket
       │
       ▼
Lector QR
       │
       ▼
POST /tickets/validate
       │
       ▼
Validación
       │
       ▼
Acceso permitido / rechazado


La generación y validación de códigos QR forman parte del módulo de control de acceso definido para el backend.

---

# 🚪 Validación de tickets

La validación comprueba que el ticket sea válido antes de permitir el acceso al evento.

Entre las validaciones consideradas se encuentran:

* Ticket existente.
* Código QR válido.
* Ticket perteneciente al evento correspondiente.
* Ticket no utilizado anteriormente.
* Ticket en un estado válido para ingreso.
* Usuario con permisos para ejecutar la validación.

La validación puede ser realizada por perfiles autorizados, principalmente administradores y organizadores.

---

# 📡 API REST

## Autenticación

### Login

```http
POST /auth/login
```

Autentica al usuario y devuelve la información necesaria para iniciar una sesión mediante JWT.

---

## Eventos

### Obtener eventos

```http
GET /events
```

Obtiene los eventos disponibles.

### Crear evento

```http
POST /events
```

Crea un nuevo evento.

---

## Tickets

### Reservar tickets

```http
POST /tickets/reserve
```

Crea una reserva temporal para los asientos seleccionados.

### Comprar tickets

```http
POST /tickets/purchase
```

Procesa la compra de los tickets correspondientes.

### Validar ticket

```http
POST /tickets/validate
```

Valida un ticket mediante la información asociada a su código QR.

---

## Usuario

### Historial

```http
GET /user/history
```

Obtiene el historial de operaciones/compras del usuario autenticado.

Los endpoints anteriores forman parte de los endpoints principales definidos para el sistema.

> Para la documentación completa de request/response, códigos HTTP y ejemplos, consultar la documentación de la API del proyecto.

---

# 🗃️ Modelo de datos

El sistema utiliza **PostgreSQL** como sistema gestor de base de datos.

Entre las entidades principales contempladas se encuentran:

```text
Usuario
   │
   ├── Ticket
   │       │
   │       └── Asiento
   │
   ├── Reservación
   │       │
   │       └── Asiento-Reservación
   │
   └── Reembolso

Evento
   │
   ├── Asiento
   ├── Ticket
   └── Evento-Auditoría

Método de pago
Lista de espera
Descuento
Reservación-Historial
```

Las entidades forman parte del modelo funcional definido para el proyecto.

---

# ⚙️ Requisitos

Para ejecutar el proyecto localmente se requiere:

* Java JDK compatible con la versión utilizada por el proyecto.
* Maven.
* PostgreSQL.
* Git.
* IDE recomendado:

  * IntelliJ IDEA
  * Eclipse
  * Visual Studio Code

Comprobar las instalaciones:

```bash
java -version
mvn -version
psql --version
git --version
```

---

# 🚀 Instalación

## 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
```

Entrar al directorio:

```bash
cd <NOMBRE_DEL_REPOSITORIO>
```

---

## 2. Crear la base de datos

Crear una base de datos PostgreSQL para el proyecto:

```sql
CREATE DATABASE ticketing_db;
```

---

## 3. Configurar variables de entorno

Configurar las credenciales de PostgreSQL y los parámetros de seguridad necesarios.

Ejemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ticketing_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

jwt.secret=${JWT_SECRET}
```

> No se deben subir credenciales, secretos JWT ni contraseñas reales al repositorio.

Se recomienda utilizar variables de entorno o un archivo local de configuración excluido mediante `.gitignore`.

---

## 4. Ejecutar el proyecto

Con Maven:

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

También puede ejecutarse directamente desde el IDE mediante la clase principal de Spring Boot.

---

# 🧪 Pruebas de la API

La API puede probarse utilizando herramientas como:

* Insomnia
* Postman
* Swagger/OpenAPI, si se encuentra habilitado en el proyecto.

Ejemplo de autenticación:

```http
POST /auth/login
Content-Type: application/json
```

Posteriormente, utilizar el JWT recibido:

```http
Authorization: Bearer <JWT>
```

---

# 🔒 Seguridad

El backend implementa mecanismos para proteger los recursos de la API.

### Medidas principales

* Autenticación mediante JWT.
* Autorización basada en roles.
* Filtro de autenticación para solicitudes HTTP.
* Validación de credenciales.
* Protección de endpoints privados.
* Separación entre autenticación y lógica de negocio.
* No exposición directa de credenciales sensibles.

El token debe enviarse mediante el encabezado:

```http
Authorization: Bearer <token>
```

---

# 🧩 Manejo de excepciones

El backend utiliza un mecanismo centralizado para manejar errores de la aplicación.

Esto permite devolver respuestas HTTP consistentes ante situaciones como:

* Recursos inexistentes.
* Datos inválidos.
* Usuario no autenticado.
* Usuario sin permisos.
* Asientos no disponibles.
* Reserva expirada.
* Ticket inválido.
* Operaciones no permitidas por el estado actual del recurso.

Ejemplo conceptual:

```json
{
  "status": 400,
  "message": "The selected seat is no longer available"
}
```

La estructura exacta de las respuestas debe consultarse en la implementación actual de los manejadores de excepciones.

---

# 📊 Reglas de negocio principales

Entre las reglas principales implementadas o contempladas por el sistema se encuentran:

### Tickets

* Un ticket no puede existir sin un asiento asociado.
* Un ticket debe pertenecer a un evento.
* Un ticket debe tener un propietario.
* Un ticket debe mantener un estado válido.
* Un ticket utilizado no debe poder utilizarse nuevamente.

### Reservas

* Una reserva tiene una duración limitada.
* Una reserva puede expirar automáticamente.
* Los asientos reservados no deben estar disponibles para otra compra mientras la reserva sea válida.
* Una reserva confirmada pasa a formar parte del proceso de compra.

### Usuarios

* Los recursos protegidos requieren autenticación.
* Las operaciones administrativas requieren los roles correspondientes.
* El usuario no debe superar el límite máximo de tickets permitido.

### Control de acceso

* Los códigos QR deben ser únicos.
* Un ticket válido solamente puede utilizarse según su estado.
* La validación de acceso requiere permisos adecuados.

---

# 🧱 Diseño orientado a mantenibilidad

El backend busca evitar una implementación donde toda la lógica se encuentre directamente dentro de los controladores.

Por ejemplo:

```text
Controller
    │
    ▼
Service
    │
    ├── Validación
    ├── Reglas de negocio
    ├── Cambios de estado
    └── Coordinación de procesos
    │
    ▼
Repository
    │
    ▼
Database
```

Esto facilita:

* Pruebas unitarias.
* Mantenimiento.
* Reutilización de lógica.
* Escalabilidad del código.
* Incorporación de nuevas funcionalidades.
* Separación de responsabilidades.

---

# 📈 Control de concurrencia

La reserva de asientos constituye una de las operaciones críticas del sistema.

El backend debe evitar situaciones como:

```text
Usuario A ──► Asiento A1 ◄── Usuario B
                  │
                  ▼
             Una sola reserva
```

Por este motivo, las operaciones relacionadas con disponibilidad, reserva y compra deben ejecutarse de forma consistente para evitar que un mismo asiento sea asignado a múltiples usuarios.

El control de concurrencia forma parte de las consideraciones técnicas establecidas para el sistema.

---

# 📚 Documentación adicional

El proyecto contempla los siguientes recursos:

* Documentación de API.
* Diagrama entidad-relación.
* Documentación de arquitectura.
* Guía de despliegue.
* Reporte de aportes de los integrantes.
* Video de guía para despliegue en la nube.

Estos forman parte de los entregables definidos para el proyecto.

---

# ☁️ Despliegue

El backend está diseñado para poder ser desplegado como una aplicación Spring Boot conectada a una instancia PostgreSQL.

Arquitectura conceptual:

```text
                   Internet
                      │
                      ▼
              ┌───────────────┐
              │    Frontend   │
              └───────┬───────┘
                      │
                    HTTP
                      │
                      ▼
              ┌───────────────┐
              │ Spring Boot   │
              │    Backend    │
              └───────┬───────┘
                      │
                    JDBC
                      │
                      ▼
              ┌───────────────┐
              │  PostgreSQL   │
              └───────────────┘
```

Las variables de entorno permiten utilizar diferentes configuraciones para desarrollo, pruebas y producción sin modificar el código fuente.

---

# 🗺️ Estado del proyecto

### ✅ Implementado

* [x] API REST con Spring Boot.
* [x] Arquitectura por capas.
* [x] PostgreSQL.
* [x] Persistencia mediante JPA/Hibernate.
* [x] Autenticación mediante JWT.
* [x] Autorización basada en roles.
* [x] Gestión de usuarios autenticados.
* [x] Gestión de tickets.
* [x] Generación de códigos QR.
* [x] Validación de tickets.
* [x] Selección/reserva de asientos.
* [x] Reservas temporales.
* [x] Expiración automática de reservas.
* [x] Límite de tickets por usuario.
* [x] Historial del usuario.
* [x] Manejo de excepciones.
* [x] DTOs y mappers.
* [x] Scheduler para procesamiento de reservas expiradas.

### 🚧 En desarrollo / integración

* [ ] Integración completa con el frontend.
* [ ] Integración completa del flujo de pago.
* [ ] Implementación/integración de lista de espera.
* [ ] Transferencia de tickets.
* [ ] Reembolsos.
* [ ] Descuentos.
* [ ] Notificaciones.
* [ ] Reportes y analítica.
* [ ] Despliegue definitivo en producción.
* [ ] Pruebas automatizadas completas.
* [ ] Documentación OpenAPI completa, si aún no está incorporada.


# 🌿 Estrategia de ramas

Se recomienda mantener las funcionalidades separadas mediante ramas:

```text
main
 │
 ├── feature/events
 ├── feature/purchases
 ├── feature/validation
 └── feature/users
```

Las ramas permiten desarrollar funcionalidades independientemente y posteriormente integrarlas mediante Pull Requests.

---

# 📄 Licencia

Este proyecto fue desarrollado con fines académicos como parte del desarrollo de un sistema de ticketing para eventos.

---

# 👤 Autoría

Proyecto desarrollado como parte del trabajo académico de Ingeniería Informática.

**Backend:** Java + Spring Boot + PostgreSQL

**Arquitectura:** N-Capas

**Tipo de aplicación:** API REST

---

## ⭐ Objetivo del proyecto

El objetivo principal es desarrollar una plataforma de ticketing capaz de gestionar de forma segura y consistente el ciclo completo de una entrada:

```text
Usuario
   ↓
Autenticación
   ↓
Selección de evento
   ↓
Selección de asiento
   ↓
Reserva temporal
   ↓
Compra
   ↓
Generación de ticket
   ↓
Generación de QR
   ↓
Validación en acceso
   ↓
Ingreso al evento
```

El proyecto busca aplicar conceptos de desarrollo backend, arquitectura de software, persistencia de datos, seguridad, manejo de concurrencia y diseño de APIs REST en un sistema de negocio realista.

