# SitMe - Workspace Reservation System

![Java](https://img.shields.io/badge/Java-21-orange?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Supported-2496ED?logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/CI-GitHub%20Actions-purple?logo=githubactions&logoColor=white)
![License](https://img.shields.io/badge/License-Educational-yellow)

**SitMe** is a Spring Boot application for managing workspace reservations, built with modern Java technologies and containerized with Docker.

<br>

## 📑 Table of Contents

- [🚀 Features](#-features)
- [🛠️ Tech Stack](#tech-stack)
- [📋 Prerequisites](#-prerequisites)
- [🏗️ Project Structure](#project-structure)
- [⚙️ Configuration](#configuration)
- [🚀 Getting Started](#-getting-started)
- [🧪 Testing](#-testing)
- [📚 API Documentation](#-api-documentation)
- [🔧 Key Features](#-key-features)
- [🐳 Docker Support](#-docker-support)
- [🔄 CI/CD Pipeline](#-cicd-pipeline)
- [📝 API Usage Examples](#-api-usage-examples)
- [🤝 Contributing](#-contributing)
- [👩‍💻 Team](#team)
- [📄 License](#-license)
- [🆘 Support](#-support)

<br>


## 🚀 Features

- **User Management**: Registration, authentication, and profile management with JWT tokens
- **Space Management**: Create and manage different types of workspaces (rooms and tables)
- **Reservation System**: Book spaces for different time slots (morning, afternoon, full day)
- **Image Upload**: Profile and space images via Cloudinary integration
- **Email Notifications**: Automated registration confirmation emails
- **Role-based Security**: Admin and user roles with different permissions
- **RESTful API**: Comprehensive REST endpoints with Swagger documentation
- **Database Integration**: MySQL with JPA/Hibernate
- **Health Checks**: Actuator endpoints to monitor application status
- **Containerization**: Full Docker support with multi-stage builds

<br>

<h2 id="tech-stack">🛠️ Tech Stack</h2>

- **Backend**: Java 21, Spring Boot 3.5.5
- **Database**: MySQL 8.0
- **Security**: Spring Security with JWT authentication
- **Image Storage**: Cloudinary
- **Email**: Spring Mail (configured for MailHog in development)
- **Testing**: JUnit, Mockito
- **API Documentation**: Swagger/OpenAPI 3
- **Containerization**: Docker, Docker Compose
- **Build Tool**: Maven
- **CI/CD**: GitHub Actions

<br>

## 📋 Prerequisites

- Java 21
- Maven 3.9+
- Docker and Docker Compose
- MySQL 8.0 (if running locally without Docker)

<br>

<h2 id="project-structure">🏗️ Project Structure</h2>

```
src/
├── main/java/com/femcoders/sitme/
│   ├── cloudinary/          # Image upload service
│   ├── email/               # Email notifications
│   ├── reservation/         # Reservation management
│   ├── security/            # JWT security configuration
│   ├── shared/              # Common utilities and exceptions
│   ├── space/               # Workspace management
│   └── user/                # User management and authentication
└── main/resources/
    ├── application.properties
    └── data.sql             # Initial data setup
```

<br>

<h2 id="configuration">⚙️ Configuration</h2>

### Environment Variables

Create a `.env` file in the project root:

```env
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/sitme
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT Configuration
JWT_SECRET_KEY=your_jwt_secret_key
JWT_EXPIRATION=1800000

# Server Configuration
SERVER_PORT=8080

# Cloudinary Configuration (for image uploads)
CLOUDINARY_NAME=your_cloudinary_name
CLOUDINARY_KEY=your_cloudinary_api_key
CLOUDINARY_SECRET=your_cloudinary_secret

# Docker Hub (for CI/CD)
DOCKER_USERNAME=your_docker_username
DOCKER_PASSWORD=your_docker_password
```

<br>

## 🚀 Getting Started

### Option 1: Using Docker Compose (Recommended)

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd sitme
   ```

2. **Start the application**:
   ```bash
   docker-compose up -d
   ```

3. **Access the application**:
    - API: http://localhost:8081
    - Database: localhost:3306
    - Swagger Documentation: http://localhost:8081/swagger-ui.html

### Option 2: Local Development

1. **Install dependencies**:
   ```bash
   mvn clean install
   ```

2. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

3. **Access the application**:
    - API: http://localhost:8080
    - Swagger Documentation: http://localhost:8080/swagger-ui.html

<br>

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests with Docker
```bash
docker-compose -f docker-compose-test.yml up --abort-on-container-exit
```

The test suite includes:
- Unit tests for all service layers using Mockito
- Integration tests for REST endpoints

<br>

## 📚 API Documentation

### Authentication Endpoints

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login (returns JWT token)

### User Management

- `GET /api/users/{id}` - Get user by ID (Admin only)
- `PUT /api/users/{id}` - Update user profile (Admin only)
- `POST /api/users/{id}/image` - Upload user profile image
- `DELETE /api/users/{id}/image` - Delete user profile image

### Space Management

- `GET /api/spaces` - Get all spaces
- `GET /api/spaces/filter/type?type={TYPE}` - Filter spaces by type (ROOM/TABLE)
- `GET /api/spaces/filter/available` - Get available spaces only
- `POST /api/spaces` - Create new space (Admin only)
- `PUT /api/spaces/{id}` - Update space (Admin only)
- `DELETE /api/spaces/{id}` - Delete space (Admin only)

### Reservation Management

- `GET /api/reservations` - Get all reservations (Admin only)
- `GET /api/reservations/{id}` - Get reservation by ID (Admin only)

### Default Users

The application comes with preloaded test data:

- **Admin**: username: `admin`, password: `Password123.`
- **Test Users**: username: `debora`, `roberto`, `jenni`, etc. (password: `Password123.`)

<br>

## 🔧 Key Features

### Security
- JWT-based authentication
- Role-based authorization (USER/ADMIN roles)
- Password encryption with BCrypt
- CORS configuration for API access

### Observability
- Spring Boot Actuator integration for health checks and monitoring endpoints

### Image Management
- Cloudinary integration for image storage
- File validation (size and format)
- Automatic cleanup when images are deleted

### Email System
- Registration confirmation emails
- Configurable SMTP settings
- MailHog integration for development

### Database
- MySQL with JPA/Hibernate
- Automatic schema creation
- Pre-loaded test data
- Connection pooling

<br>

## 🐳 Docker Support

### Development
```bash
docker-compose up -d
```

### Testing
```bash
docker-compose -f docker-compose-test.yml up --abort-on-container-exit
```

### Production Build
The application uses multi-stage Docker builds for optimized production images.

<br>

## 🔄 CI/CD Pipeline

GitHub Actions workflows included:

- **Test**: Runs on pull requests
- **Build**: Builds and pushes Docker images on main branch pushes
- **Release**: Creates releases and pushes tagged images

<br>

## 📝 API Usage Examples

### Register a new user
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "maria",
    "email": "maria@example.com",
    "password": "SecurePass123!"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "maria",
    "password": "SecurePass123!"
  }'
```

### Get all spaces
```bash
curl -X GET http://localhost:8080/api/spaces
```

<br>

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

<br>

<h2 id="team">👩‍💻 Team</h2>

- **Débora Rubio** – Team Leader, Scrum Master and Developer
- **Lara Pla** – Product Owner and Developer
- **Mariia Sycheva** – Developer
- **Mayleris Echezuria** – Developer
- **Vita Poperechna** – Developer
- **Saba Ur Rehman** – Developer

<br>

## 📄 License

This project is part of a learning bootcamp and is intended for educational purposes.

<br>

## 🆘 Support

For questions or issues, please create an issue in the repository or contact the development team.

<br>

---

**Built with 💜 using Spring Boot and modern Java technologies**
