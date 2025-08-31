# Farmatodo E-commerce 🛒  

![Java](https://img.shields.io/badge/Java-17-red?logo=java)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?logo=springboot)  
![Maven](https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven)  
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker)  
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-DB-336791?logo=postgresql)  
![H2](https://img.shields.io/badge/H2-Database-lightgrey)  
![Coverage](https://img.shields.io/badge/Coverage-80%25-green?logo=codecov)  

Proyecto base para el **Reto Técnico - Desarrollador Java**, implementado con **Java 17**, **Spring Boot 3.5.5** y **Maven**.  
El sistema simula un flujo de e-commerce incluyendo **tokenización de tarjetas, gestión de clientes, productos, carrito de compras, pedidos/pagos, notificaciones y logs centralizados**.  

---

## 🚀 Tecnologías
- Java 17  
- Spring Boot 3.5.5  
- Spring Web, Data JPA, Validation, Security (API Key), Mail  
- PostgreSQL (producción/local con Docker)  
- H2 (pruebas / perfil cloud-h2 en producción demo)  
- Docker & Docker Compose  
- Swagger/OpenAPI 3 (documentación de APIs)  
- Flyway (migraciones de BD)  
- JaCoCo (cobertura de pruebas)  

---

## 📂 Estructura del Proyecto
El proyecto sigue **arquitectura hexagonal (puertos y adaptadores)**:  

```
src/main/java/com/farmatodo/ecommerce
 ├─ adapter/              # Adaptadores primarios/secundarios
 ├─ config/
 │   ├─ properties/       # Configuración externa (Payments, Search, Tokenization, Notifications)
 │   ├─ swagger/          # Configuración de seguridad y documentación (ApiKey, OpenAPI, SecurityConfig)
 │   └─ trasversal/       # Servicios transversales (ej: CryptoService)
 ├─ controller/           # Controladores REST (Customers, Products, Cart, Orders)
 ├─ DTOs/                 # Objetos de transferencia (Request / Response)
 ├─ entity/               # Entidades JPA
 ├─ enums/                # Enumeraciones del dominio
 ├─ exceptions/           # Manejo de errores y excepciones
 ├─ repository/           # Repositorios JPA
 ├─ usecase/              # Casos de uso (reglas de negocio)
 └─ FarmatodoECommerceApplication.java
```

---

## ⚙️ Configuración
Parámetros clave en `application.yml` / perfiles:
- `security.apiKey` → API Key requerida en los endpoints.  
- `tokenization.rejectionProbability` → Probabilidad de rechazo en tokenización.  
- `payments.approvalProbability` → Probabilidad de aprobación en pagos.  
- `payments.retry.maxAttempts` → Número máximo de reintentos de pago.  
- `search.minStock` → Stock mínimo visible en búsquedas.  
- `notifications.operatorEmail` → Correo de soporte para recibir copias de notificaciones.  
- `crypto.keyB64` → Clave AES en Base64 (32 bytes).  

---

## ▶️ Ejecución local
1. Compilar y ejecutar con Maven:
   ```bash
   mvn spring-boot:run
   ```
2. Acceder a:
   - API → `http://localhost:8080/ping`
   - Swagger UI → `http://localhost:8080/swagger-ui.html`

### Con Docker Compose
```bash
docker-compose up --build
```

Servicios:
- **API**: http://localhost:8080  
- **Ping**: http://localhost:8080/ping  
- **Swagger**: http://localhost:8080/swagger-ui.html  
- **Mailhog**: http://localhost:8025  

---

## 🌐 Producción (GCP - Cloud Run)
El servicio está desplegado en **Google Cloud Run** con perfil `cloud-h2`:

- **Base URL**:  
  `https://ecommerce-service-866466265019.us-central1.run.app`

- **Endpoints principales**:
  - `GET /ping` → prueba de salud.  
  - Swagger UI →  
    [https://ecommerce-service-866466265019.us-central1.run.app/swagger-ui.html](https://ecommerce-service-866466265019.us-central1.run.app/swagger-ui.html)  
  - OpenAPI docs →  
    [https://ecommerce-service-866466265019.us-central1.run.app/v3/api-docs](https://ecommerce-service-866466265019.us-central1.run.app/v3/api-docs)  

> ⚠️ Todos los endpoints (excepto `/ping`) requieren API Key en el header:  
> `x-api-key: changeme-123`  

Ejemplo:
```bash
curl -i -H "x-api-key: changeme-123"   https://ecommerce-service-866466265019.us-central1.run.app/customers
```

---

## ✅ Pruebas
Ejecutar pruebas unitarias con cobertura (JaCoCo ≥ 80%):
```bash
mvn clean verify
```

Reporte de cobertura:  
`target/site/jacoco/index.html`

---

## 🤖 Uso de AI
Durante el desarrollo de este proyecto se utilizó inteligencia artificial como apoyo para resolver dudas y documentar procesos técnicos.  

En particular, se solicitó ayuda para:  
- Configuración y despliegue en Google Cloud con **Docker** y **Docker Compose**.  
- Entendimiento y configuración de la base de datos **H2** (conceptos, funcionamiento, configuración en Spring Boot, acceso a consola, creación de tablas y uso en pruebas unitarias).  

📎 Conversaciones de referencia:  
- [Implementación en Google Cloud con Docker](https://chatgpt.com/share/68b3b331-1548-8007-b41e-e1b485b22d7d)  
- [Guía completa de H2 Database](https://chatgpt.com/share/68b3b584-3dac-8007-b124-6d37b8e50644)  

---

## 📦 Entregables del reto
- Código fuente en este repositorio.
- API desplegada en **Docker/GCP**.
- Colección Postman para pruebas.
- Diagramas de arquitectura.
- Documentación en este README.
- Cobertura de pruebas ≥ 80%.
