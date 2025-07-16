# 🏪 Retail Management System

A comprehensive, enterprise-grade retail management system built with Spring Boot that helps businesses streamline their inventory, sales, and user management operations. This system provides a complete solution for managing multiple store locations with role-based access control and real-time inventory tracking.

## 🚀 Features

### 🔐 **Authentication & Authorization**
- **JWT-based authentication** with secure token management
- **Role-based access control** (Corporate Admin, Store Manager, Sales Associate)
- **User registration and management** with store assignments
- **Secure password encryption** using BCrypt

### 📦 **Inventory Management**
- **Real-time stock tracking** across multiple store locations
- **Low stock alerts** with customizable thresholds
- **Stock transfer** between stores
- **Stock reservation** for pending sales
- **Automatic inventory updates** during sales transactions

### 🛍️ **Sales Processing**
- **Complete sales transaction processing** with item tracking
- **Sales history and analytics** with date range filtering
- **Revenue reporting** and transaction summaries
- **Refund and return processing** with inventory adjustments
- **Customer information management**

### 🏢 **Multi-Store Support**
- **Multiple store locations** with centralized management
- **Store-specific inventory tracking**
- **Cross-store operations** for managers and admins
- **Store performance analytics**

### 👥 **User Management**
- **Employee management** with role assignments
- **Store-specific user access**
- **User activity tracking**
- **Flexible permission system**

### 🎨 **Modern Web Interface**
- **Interactive API testing interface** built with vanilla JavaScript
- **Responsive design** that works on all devices
- **Real-time feedback** and error handling
- **Clean, professional UI** with modern styling

## 🛠️ Technology Stack

- **Backend:** Spring Boot 3.5.3, Java 24
- **Database:** PostgreSQL with JPA/Hibernate
- **Security:** Spring Security with JWT authentication
- **Frontend:** HTML5, CSS3, Vanilla JavaScript
- **Build Tool:** Maven
- **Containerization:** Docker & Docker Compose

## 📋 Prerequisites

Before you can run this application, make sure you have the following installed:

- **Java 24** or higher
- **Maven 3.6+**
- **Docker & Docker Compose**
- **Git** (for cloning the repository)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/andrepontde/Your-retail-assistant.git
cd Your-retail-assistant
```

### 2. Start the Database
```bash
docker-compose up -d
```

This will start a PostgreSQL database on port `5332` with the following credentials:
- **Username:** `postgres`
- **Password:** `261010`
- **Database:** `postgres`

### 3. Run the Application
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access the Web Interface
Open your browser and navigate to `http://localhost:8080` to access the interactive API testing interface.

## 🔧 Configuration

### Database Configuration
The application is pre-configured to connect to the Docker PostgreSQL instance. If you need to modify the database settings, update the `application.properties` file:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5332/postgres
spring.datasource.username=postgres
spring.datasource.password=261010
```

### JWT Configuration
JWT tokens are configured with a default secret and 24-hour expiration. For production use, update these settings:

```properties
jwt.secret=your-secure-secret-key-here
jwt.expiration=86400
```

## 📚 API Documentation

### Authentication Endpoints

#### User Registration
```http
POST /api/users/register
Content-Type: application/json

{
  "username": "john.doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "SALES_ASSOCIATE",
  "password": "password123",
  "primaryStoreId": 1
}
```

#### User Login
```http
POST /api/users/login
Content-Type: application/json

{
  "username": "john.doe",
  "password": "password123"
}
```

### Item Management

#### Create Item
```http
POST /api/items
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "name": "Product Name",
  "description": "Product Description",
  "category": "Electronics",
  "price": 29.99,
  "sku": "PROD-001"
}
```

#### Get All Items
```http
GET /api/items
Authorization: Bearer {jwt-token}
```

### Inventory Management

#### Add Stock
```http
POST /api/inventory/add-stock
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "itemId": 123,
  "quantity": 50
}
```

#### Check Stock Level
```http
GET /api/inventory/stock/{itemId}
Authorization: Bearer {jwt-token}
```

### Sales Management

#### Process Sale
```http
POST /api/sales
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "customerName": "John Customer",
  "customerEmail": "customer@example.com",
  "items": [
    {
      "itemId": 123,
      "quantity": 2,
      "unitPrice": 29.99
    }
  ]
}
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/dev/andrepontde/retailmanager/retail_system/
│   │   ├── controller/          # REST API controllers
│   │   ├── service/             # Business logic layer
│   │   ├── repository/          # Data access layer
│   │   ├── entity/              # JPA entities
│   │   ├── dto/                 # Data transfer objects
│   │   └── security/            # Security configuration
│   └── resources/
│       ├── static/              # Web interface files
│       └── application.properties
└── test/                        # Unit and integration tests
```

## 🔒 Security Features

- **JWT Authentication:** Stateless authentication with secure token generation
- **Role-Based Access:** Three-tier permission system (Admin, Manager, Associate)
- **Password Encryption:** BCrypt hashing for secure password storage
- **CORS Protection:** Configurable cross-origin request handling
- **SQL Injection Prevention:** JPA/Hibernate parameterized queries

## 🧪 Testing

### Run Unit Tests
```bash
./mvnw test
```

### API Testing
The application includes a built-in web interface for testing all API endpoints. Simply navigate to `http://localhost:8080` and use the interactive forms to test functionality.

## 🚀 Deployment

### Docker Deployment
```bash
# Build the application
./mvnw clean package

# Build Docker image
docker build -t retail-management-system .

# Run with Docker Compose
docker-compose up --build
```

### Production Considerations
- Update JWT secret key in production
- Configure proper database credentials
- Set up SSL/TLS certificates
- Configure logging levels
- Set up monitoring and health checks

## 🤝 Contributing

We welcome contributions! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Make your changes** with proper tests
4. **Commit your changes** (`git commit -m 'Add amazing feature'`)
5. **Push to the branch** (`git push origin feature/amazing-feature`)
6. **Open a Pull Request**

### Development Guidelines
- Follow Java coding conventions
- Write unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Andre Pont** - *Initial work* - [andrepontde](https://github.com/andrepontde)

## 🆘 Support

If you encounter any issues or have questions:

1. **Check the documentation** above
2. **Search existing issues** on GitHub
3. **Create a new issue** with detailed information
4. **Contact the maintainer** for urgent matters

## 🎯 Roadmap

### Upcoming Features
- **Mobile app integration** with REST API
- **Advanced analytics dashboard** with charts and graphs
- **Barcode scanning support** for inventory management
- **Email notifications** for low stock and sales
- **Advanced reporting** with PDF generation
- **Integration with payment gateways**

### Performance Improvements
- **Database indexing optimization**
- **Caching layer implementation**
- **API rate limiting**
- **Batch processing for large operations**

---

**Built with ❤️ using Spring Boot and modern web technologies**

*This system is designed to be scalable, maintainable, and user-friendly. Whether you're managing a single store or a chain of retail locations, this solution provides the tools you need to succeed.*