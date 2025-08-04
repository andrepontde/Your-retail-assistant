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

### � **Mobile App Ready**
- **CORS-enabled API** for cross-origin requests
- **Public discovery endpoints** for server information
- **Network-accessible deployment** for mobile testing
- **RESTful API design** optimized for mobile consumption

## �🛠️ Technology Stack

- **Backend:** Spring Boot 3.5.3, Java 21
- **Database:** PostgreSQL with JPA/Hibernate
- **Security:** Spring Security with JWT authentication
- **Frontend:** HTML5, CSS3, Vanilla JavaScript
- **Build Tool:** Maven
- **Containerization:** Docker & Docker Compose

## 📋 Prerequisites

Before you can run this application, make sure you have the following installed:

- **Java 21** or higher
- **Maven 3.6+**
- **Docker & Docker Compose**
- **Git** (for cloning the repository)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/andrepontde/Your-retail-assistant.git
cd Your-retail-assistant
```

### 2. Start the Complete System
```bash
docker-compose up --build
```

This will start both the application and PostgreSQL database with a single command.

The system includes:
- **PostgreSQL database** on port `5332`
- **Spring Boot application** on port `8080`
- **Automatic database connection** and schema creation

### 3. Access the Application
- **Web Interface**: `http://localhost:8080`
- **API Health Check**: `http://localhost:8080/api/public/health`
- **Server Information**: `http://localhost:8080/api/public/server-info`

### 4. Network Access (Mobile Development)
To access from other devices on your network:
- Find your computer's IP address (e.g., `192.168.1.100`)
- Configure Windows Firewall to allow port 8080
- Access via: `http://your-ip:8080`

## 🔧 Configuration

### Database Configuration
The application automatically connects to the containerized PostgreSQL instance:

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

### Public Endpoints (No Authentication Required)

#### Server Information
```http
GET /api/public/server-info
```
Returns server capabilities and version information for mobile app discovery.

#### Health Check
```http
GET /api/public/health
```
Returns server health status and timestamp.

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
- **CORS Support:** Configured for mobile app and cross-origin requests
- **SQL Injection Prevention:** JPA/Hibernate parameterized queries

## 📱 Mobile App Development

### API Features for Mobile
- **CORS-enabled endpoints** for cross-platform access
- **Public discovery API** at `/api/public/server-info`
- **Health monitoring** at `/api/public/health`
- **JWT-based authentication** perfect for mobile apps
- **RESTful design** with JSON responses

### Network Access Setup
1. **Start the application** with `docker-compose up --build`
2. **Find your computer's IP** (e.g., `ipconfig` on Windows)
3. **Configure firewall** to allow port 8080
4. **Access from mobile** via `http://your-ip:8080`

### Mobile App Architecture
The system supports both:
- **Official hosted server** deployment
- **Local server instances** for development/testing
- **Multi-server switching** in mobile apps

## 🧪 Testing

### Run Unit Tests
```bash
./mvnw test
```

### API Testing
The application includes a built-in web interface for testing all API endpoints. Simply navigate to `http://localhost:8080` and use the interactive forms to test functionality.

### Mobile Testing
Test API endpoints from mobile devices using the network IP address (e.g., `http://192.168.1.100:8080/api/public/health`).

## 🚀 Deployment

### Complete Docker Deployment
```bash
# Start both application and database
docker-compose up --build

# Run in background
docker-compose up --build -d

# Stop services
docker-compose down
```

### Traditional Deployment
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

### ✅ Recently Added
- **CORS support** for mobile app development
- **Public API endpoints** for server discovery
- **Complete Docker deployment** with single command
- **Network access configuration** for mobile testing
- **Java 21 compatibility** and optimized build process

### Upcoming Features
- **Native mobile applications** (React Native/Flutter)
- **Advanced analytics dashboard** with charts and graphs
- **Barcode scanning support** for inventory management
- **Email notifications** for low stock and sales
- **Advanced reporting** with PDF generation
- **Integration with payment gateways**
- **Multi-tenant support** for SaaS deployment

### Performance Improvements
- **Redis caching layer** implementation
- **Database indexing optimization**
- **API rate limiting** and throttling
- **Batch processing** for large operations
- **Microservices architecture** for scale

---

**Built with ❤️ using Spring Boot and modern web technologies**

*This system is designed to be scalable, maintainable, and user-friendly. Whether you're managing a single store or a chain of retail locations, this solution provides the tools you need to succeed.*