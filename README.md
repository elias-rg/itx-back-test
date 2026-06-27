# itx-back-test — Similar Products API

## Español

### Descripción

API REST desarrollada con **Spring Boot + WebFlux** que expone un endpoint para obtener los productos similares a uno dado. El servicio orquesta dos APIs externas de forma reactiva y paralela, aplicando resiliencia ante errores y timeouts.

### Arquitectura

El proyecto sigue una **arquitectura hexagonal (ports & adapters)**:

```
domain/
  model/          → ProductDetail, ProductNotFoundException
  port/in/        → GetSimilarProductsUseCase (puerto de entrada)
  port/out/       → SimilarProductIdsPort, ProductDetailPort (puertos de salida)

application/
  service/        → SimilarProductsService (lógica de negocio)

infrastructure/
  adapter/in/     → SimilarProductsController (REST)
  adapter/out/    → ExternalProductApiAdapter (HTTP client)
  config/         → WebClientConfig
```

### Endpoint expuesto

```
GET http://localhost:5000/product/{productId}/similar
```

**Respuesta 200:**
```json
[
  { "id": "1", "name": "Shirt", "price": 9.99, "availability": true },
  { "id": "2", "name": "Dress", "price": 19.99, "availability": true }
]
```

**Respuesta 404:** cuando el producto no existe.

### Decisiones técnicas

- **WebFlux + flatMap**: las llamadas de detalle de producto se ejecutan **en paralelo**, no secuencialmente.
- **Timeout de 2s por producto**: productos cuya respuesta supere los 2 segundos son descartados silenciosamente.
- **Resiliencia**: si un producto similar devuelve 404 o 500, se omite y se devuelven el resto.
- **Configuración externalizada**: el timeout y la URL base de la API externa son configurables via `application.yml`.

### Requisitos

- Java 17
- Maven 3.x

### Ejecución

```bash
./mvnw spring-boot:run
```

La aplicación arranca en el puerto **5000**.

### Tests

```bash
./mvnw test
```

### Configuración

`src/main/resources/application.yml`:

```yaml
server:
  port: 5000

external:
  api:
    base-url: http://localhost:3001
    product-detail-timeout-ms: 2000
```

---

## English

### Description

REST API built with **Spring Boot + WebFlux** that exposes an endpoint to retrieve similar products for a given one. The service orchestrates two external APIs reactively and in parallel, applying resilience against errors and timeouts.

### Architecture

The project follows a **hexagonal architecture (ports & adapters)**:

```
domain/
  model/          → ProductDetail, ProductNotFoundException
  port/in/        → GetSimilarProductsUseCase (inbound port)
  port/out/       → SimilarProductIdsPort, ProductDetailPort (outbound ports)

application/
  service/        → SimilarProductsService (business logic)

infrastructure/
  adapter/in/     → SimilarProductsController (REST)
  adapter/out/    → ExternalProductApiAdapter (HTTP client)
  config/         → WebClientConfig
```

### Exposed endpoint

```
GET http://localhost:5000/product/{productId}/similar
```

**200 response:**
```json
[
  { "id": "1", "name": "Shirt", "price": 9.99, "availability": true },
  { "id": "2", "name": "Dress", "price": 19.99, "availability": true }
]
```

**404 response:** when the product does not exist.

### Technical decisions

- **WebFlux + flatMap**: product detail calls are executed **in parallel**, not sequentially.
- **2s timeout per product**: products whose response exceeds 2 seconds are silently discarded.
- **Resilience**: if a similar product returns 404 or 500, it is skipped and the rest are returned.
- **Externalized configuration**: timeout and external API base URL are configurable via `application.yml`.

### Requirements

- Java 17
- Maven 3.x

### Run

```bash
./mvnw spring-boot:run
```

The application starts on port **5000**.

### Tests

```bash
./mvnw test
```

### Configuration

`src/main/resources/application.yml`:

```yaml
server:
  port: 5000

external:
  api:
    base-url: http://localhost:3001
    product-detail-timeout-ms: 2000
```
