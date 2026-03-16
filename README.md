# Campaign Management System

A comprehensive campaign management platform built with Spring Boot and Spring Modulith, designed for organizations to create, manage, and track marketing campaigns across multiple channels.

## 🏗️ Architecture

This application follows a modular architecture using **Spring Modulith**, which enforces strict module boundaries and dependency rules. The system is organized into the following modules:

### Core Modules

- **📊 Analytics** - Campaign performance tracking and reporting
- **⚙️ Automation** - Workflow automation and campaign triggers
- **📢 Campaign** - Core campaign management functionality
- **📡 Channel** - Multi-channel communication (Email, SMS, etc.)
- **📞 Contact** - Contact management and segmentation
- **📝 Template** - Message template management
- **👤 User** - User management and authentication
- **🔧 Common** - Shared utilities and configurations

## 🚀 Features

### Campaign Management

- Create and manage marketing campaigns
- Multi-channel support (Email, SMS)
- Campaign scheduling and automation
- Contact segmentation and targeting
- Template-based messaging

### User Management

- User registration and authentication
- Organization-based multi-tenancy
- Role-based access control
- JWT token-based security
- Invitation system for team members

### Analytics & Reporting

- Campaign performance metrics
- Real-time tracking and analytics
- Comprehensive reporting dashboard

### Integration Ready

- RESTful API with OpenAPI/Swagger documentation
- Google OAuth2 authentication
- Email service integration (Mailtrap)
- PostgreSQL database

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Architecture**: Spring Modulith
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **API Documentation**: OpenAPI/Swagger
- **Mapping**: MapStruct
- **Build Tool**: Maven
- **Java Version**: 17

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Docker (optional, for containerized deployment)

## ⚡ Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd campaign-management-system
```

### 2. Database Setup

```bash
# Using Docker
docker run --name postgres-cms -e POSTGRES_DB=myDb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=root -p 5433:5432 -d postgres:13

# Or install PostgreSQL locally and create database
createdb myDb
```

### 3. Environment Configuration

Create `application-local.yaml` or set environment variables:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/myDb
    username: postgres
    password: root

  mail:
    username: your-mailtrap-username
    password: your-mailtrap-password

  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-google-client-id
            client-secret: your-google-client-secret
```

### 4. Build and Run

```bash
# Build the application
./mvnw clean compile

# Run the application
./mvnw spring-boot:run

# Or run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

Once the application is running, access the API documentation at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 🔐 Authentication

The application uses JWT-based authentication. Key endpoints:

- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/accept-invitation` - Accept team invitation

Include the JWT token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

## 🧪 Testing

### Run All Tests

```bash
./mvnw test
```

### Run Modularity Tests

```bash
./mvnw test -Dtest=ModularityTest
```

### Run Specific Module Tests

```bash
./mvnw test -Dtest="*ServiceTest"
```

## 📦 Build & Deployment

### Build JAR

```bash
./mvnw clean package
```

### Build with Docker

```bash
# Build Docker image
./mvnw spring-boot:build-image

# Run with Docker Compose
docker-compose up
```

## 🔧 Development

### Module Structure

Each module follows a consistent structure:

```
module-name/
├── entity/          # JPA entities
├── repository/      # Data access layer
├── service/         # Business logic
├── controller/      # REST endpoints
├── dto/            # Data transfer objects
├── mapper/         # MapStruct mappers
├── events/         # Domain events
└── package-info.java # Module configuration
```

### Code Quality

- **Modularity**: Strict module boundaries enforced by Spring Modulith
- **Testing**: Unit and integration tests for all modules
- **Code Style**: Follows Spring Boot conventions

### Adding New Features

1. Identify the appropriate module for the feature
2. Update module dependencies in `package-info.java` if needed
3. Implement the feature following the module structure
4. Add comprehensive tests
5. Update API documentation

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:

- Create an issue in the repository
- Check the API documentation
- Review the Spring Modulith documentation

## 🔄 Version History

- **0.0.1-SNAPSHOT** - Initial modular architecture with core campaign management features

---

Built with ❤️ using Spring Boot and Spring Modulith
