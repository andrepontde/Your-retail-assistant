# 🏪 Retail Management System

A comprehensive, enterprise-grade retail management system built with Spring Boot that helps businesses streamline their inventory, sales, and user management operations. Features professional SKU/UPC management, receipt generation, and multi-store support with role-based access control.

## 🚀 Key Features

### � **Professional SKU & UPC Management**
- **Industry-standard SKU format**: `[CATEGORY]-[BRAND]-[VARIANT]-[SEQUENCE]` (e.g., `ELE-SAM-32GB-001`)
- **12-digit UPC barcode generation** for retail scanning compatibility
- **Automatic SKU/UPC generation** with uniqueness validation
- **SKU-based inventory operations** for user-friendly management
- **Dual-mode system**: Technical operations use database IDs, business operations use SKUs

### 🧾 **Sales & Receipt System**
- **Professional receipt generation** with company branding
- **Complete sales transaction processing** with automatic inventory updates
- **Multiple payment methods** (Cash, Card, Digital Wallet)
- **Sales history and reporting** with date range filtering
- **Refund processing** with inventory adjustments

### 📦 **Advanced Inventory Management**
- **Real-time stock tracking** across multiple store locations
- **SKU-based stock operations** (add/remove/check by SKU)
- **Low stock alerts** with customizable thresholds
- **Stock transfer** between stores
- **Stock reservation** for pending sales

### 🔐 **Authentication & Authorization**
- **JWT-based authentication** with secure token management
- **Role-based access control** (Corporate Admin, Store Manager, Sales Associate)
- **User registration and management** with store assignments
- **Secure password encryption** using BCrypt

### 🏢 **Multi-Store Support**
- **Multiple store locations** with centralized management
- **Store-specific inventory tracking**
- **Cross-store operations** for managers and admins
- **User-context-aware operations** (automatic store detection)

### 🎨 **Modern Web Interface**
- **Interactive API testing interface** with SKU management
- **Professional SKU generation** with real-time preview
- **Responsive design** that works on all devices
- **User-friendly inventory management** with SKU-based operations

## 🛠️ Technology Stack

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
git clone https://github.com/andrepontde/Arka-Inventory.git
cd Arka-Inventory
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

### SKU & UPC Management

#### Generate SKU/UPC Pair
```http
POST /api/items/generate-sku
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "category": "Electronics",
  "brand": "Samsung",
  "variant": "32GB"
}
```

#### Find Item by SKU
```http
GET /api/items/by-sku/{sku}
Authorization: Bearer {jwt-token}
```

#### Find Item by UPC
```http
GET /api/items/by-upc/{upc}
Authorization: Bearer {jwt-token}
```

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

#### Create Item with Auto-Generated SKU/UPC
```http
POST /api/items
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "name": "Samsung Galaxy Phone",
  "description": "32GB Smartphone",
  "category": "Electronics",
  "brand": "Samsung",
  "variant": "32GB",
  "price": 599.99
}
```

### SKU-Based Inventory Management

#### Add Stock by SKU
```http
POST /api/inventory/add-stock-by-sku
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "sku": "ELE-SAM-32GB-001",
  "quantity": 50
}
```

#### Check Stock by SKU
```http
GET /api/inventory/stock-by-sku/{sku}
Authorization: Bearer {jwt-token}
```

### Sales & Receipt Generation

#### Process Sale with Receipt
```http
POST /api/sales
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "customerName": "John Customer",
  "customerEmail": "customer@example.com",
  "customerPhone": "+1234567890",
  "paymentMethod": "CARD",
  "items": [
    {
      "itemId": 1,
      "quantity": 2,
      "unitPrice": 599.99
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
│   │   ├── service/             # Business logic layer (including SKUService)
│   │   ├── repository/          # Data access layer
│   │   ├── entity/              # JPA entities with SKU/UPC fields
│   │   ├── dto/                 # Data transfer objects
│   │   └── security/            # Security configuration
│   └── resources/
│       ├── static/              # Web interface with SKU management
│       └── application.properties
└── test/                        # Unit and integration tests
```

## 🎯 Usage Examples

### Creating Items with SKU/UPC
1. **Navigate to** `http://localhost:8080`
2. **Login** with test credentials
3. **Fill in item details** (name, category, brand, variant, price)
4. **Click "Generate SKU"** to auto-create SKU and UPC
5. **Submit** to create item with professional identifiers

### SKU-Based Inventory Management
1. **Use the "SKU-Based Inventory" section**
2. **Add stock** using SKU: `ELE-SAM-32GB-001`
3. **Check stock levels** by SKU instead of database ID
4. **Professional workflow** for retail staff

### Receipt Generation
1. **Process a sale** through the sales interface
2. **Automatic receipt generation** with company branding
3. **Receipt shows SKUs** for easy reference
4. **Professional transaction records**

## 🔒 Security Features

- **JWT Authentication:** Stateless authentication with secure token generation
- **Role-Based Access:** Three-tier permission system (Admin, Manager, Associate)
- **Password Encryption:** BCrypt hashing for secure password storage
- **SQL Injection Prevention:** JPA/Hibernate parameterized queries
- **User-context operations:** Automatic store detection for security

## 🧪 Testing

### Run Unit Tests
```bash
./mvnw test
```

### API Testing
The application includes a built-in web interface for testing all API endpoints. Navigate to `http://localhost:8080` and use the interactive forms to test:
- **SKU generation and management**
- **Inventory operations by SKU**
- **Receipt generation**
- **All retail management functions**

## 🚀 Deployment

### Complete Docker Deployment
```bash
# Start both application and database
docker-compose up --build

# Run in background
docker-compose up --build -d

# Stop services
docker-compose down

# Clean restart (removes data)
docker-compose down -v && docker-compose up --build
```

## 🎯 Current Features & Roadmap

### ✅ Recently Added
- **Professional SKU/UPC management** with industry-standard format
- **Receipt generation system** with company branding
- **SKU-based inventory operations** for user-friendly management
- **Dual-mode system** (technical IDs + business SKUs)
- **Complete sales transaction processing** with automatic inventory updates

### 🔄 System Architecture
- **Database IDs:** Used internally for performance and referential integrity
- **SKUs:** User-facing business identifiers for daily operations
- **UPCs:** Barcode-compatible for retail scanning
- **Best of both worlds:** Technical efficiency + business usability

---

**Built with ❤️ using Spring Boot and modern retail management practices**

*This system provides professional retail management capabilities with industry-standard SKU/UPC support, receipt generation, and comprehensive inventory management. Perfect for single stores or multi-location retail chains.*