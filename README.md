# between-spring-boot-products-api-test

## Description

This project is a Spring Boot-based application that exposes product information through REST and gRPC APIs.
It demonstrates a clean architecture following the Hexagonal Architecture pattern, allowing easy testing and
flexibility.
The service provides endpoints to retrieve product details and similar product IDs.

## Architecture

The architecture used in this project is **Hexagonal Architecture** (also known as Ports and Adapters).
The structure is divided into different layers:

### Adapters

Adapters are responsible for interacting with external systems and frameworks. In this project, we have:

- Inbound Adapters (receiving input):
    - `ProductController` (inside `adapter.in.rest`): Manages RESTful API requests.
    - `GRPCProductService` (inside `adapter.in.grpc`): Handles gRPC requests.
- Outbound Adapters (sending output):
    - `ProductService` (inside `adapter.out.rest`): Calls external REST services.

### Application Layer

This layer contains business logic and service orchestration:

- `config`: Configuration files.
- `dto`: Data Transfer Objects (DTOs) used for communication between layers.
- `mapper`: Utility classes for mapping between domain and DTOs.

### Domain Layer

The core of the system, defining business entities and rules:

- `product`: Contains product-related domain models.
- `error`: Handles domain-specific errors.

### Ports

Ports define the interfaces for communication between the core domain and adapters. In this project, we have:

- `Inbound Ports` (used by adapters to interact with the application):
    - `ProductInPort` (inside `port.in.rest`): Defines REST-based interactions.
- `Outbound Ports` (used by the application to communicate with external services):
    - `ProductOutPort` (inside `port.out.rest`): Defines interactions with external REST services.

### Diagram
Below is a diagram illustrating the Hexagonal Architecture used in this project:

```mermaid
graph TD;

%% Definición de subgrupos
    subgraph Domain
        Product
        Error
    end

    subgraph Application
        Config
        DTO
        Mapper
    end

    subgraph Ports
        subgraph Inbound
            ProductInPort
        end
        subgraph Outbound
            ProductOutPort
        end
    end

    subgraph Adapters
        subgraph InboundAdapters
            GRPCProductService
            ProductController
        end
        subgraph OutboundAdapters
            ProductService
        end
    end

%% Conexiones entre capas
    Product -->|Uses| DTO
    Error -->|Handles| Product
    DTO -->|Mapped by| Mapper
    Mapper -->|Used by| Application
    Application -->|Uses| ProductInPort
    Application -->|Uses| ProductOutPort

%% Conexiones entre puertos y adaptadores
    ProductInPort -->|Implemented by| ProductController
    ProductInPort -->|Implemented by| GRPCProductService
    ProductOutPort -->|Implemented by| ProductService

%% Conexiones entre adaptadores y el exterior
    GRPCProductService -->|gRPC| ProductInPort
    ProductController -->|REST| ProductInPort
    ProductService -->|REST| ProductOutPort


```

## Features

- **REST API**: Exposes product details and similar product IDs.
- **gRPC API**: Exposes the same functionalities as the REST API but through a gRPC interface.
- **Resilience4j**: Circuit breakers, retries, and rate limiting are applied to ensure system resilience.
- **Caching**: Product data is cached to improve performance.
- **Timeouts and Retries**: Configured for handling time-sensitive requests.

## Requirements

- **Java 21** or later
- **Docker** (for containerized environment)
- **Maven** for building the project
- **gRPC** for gRPC-related functionalities
- **Spring Boot 2.x** or later
- **Redis** (optional, for caching support)

## Running the Application (Docker)

To run the application using Docker, follow these steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/edeldelgado90/between-spring-boot-products-api-test.git
   cd between-spring-boot-products-api-test
   ```
2. Build and run the Docker container:

```bash
docker-compose up --build
```

3. The application will be available at http://localhost:8080.

## Accessing the OpenAPI Documentation

Once the application is running, you can access the OpenAPI documentation at:

- URL: http://localhost:8080/swagger-ui.html
  This page allows you to interact with the REST API, view available endpoints, and try them out directly from the
  browser.

## REST API Endpoints

Here are the available REST API endpoints with example curl commands to access them:

### Get Similar Products

Endpoint: GET /product/{productId}/detail

```bash
curl -X GET "http://localhost:8080/product/1/detail" -H "accept: application/json"
````

## gRPC Usage

To interact with the gRPC service, you can use `grpcurl`. Below are examples of how to access the gRPC endpoints.

### Get Product Details

```bash
grpcurl -plaintext -d '{"productId": 1}' localhost:9090 product.ProductService/getSimilarProducts
```
