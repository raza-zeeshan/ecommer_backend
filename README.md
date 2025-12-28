# E-Commerce Backend (com.ecommerce)

[![Java](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## Project Title

**E-Commerce Backend** (package: `com.ecommerce`)

---

## Description

A modular, extensible backend for an online store built with Spring Boot. It provides RESTful APIs for user authentication, product & category management, and order processing. The project demonstrates JWT-based authentication, role-based access (ADMIN vs USER), and persistence with PostgreSQL.

---

## Features ✅

- User registration & login (JWT authentication)
- Role-based access control (ADMIN / USER)
- CRUD for products and categories (admin-restricted where appropriate)
- Order creation and management (create, list, update status)
- Search and category filtering for products
- Integration-ready: production-friendly settings and Dockerfile included

---

## Tech Stack 🛠️

- Java 17+
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- PostgreSQL (default) / H2 (dev)
- Maven

---

## Project Structure 📁

```
src/
  main/
    java/com/ecommerce/
      Application.java
      config/               # Security config & beans
      controller/           # REST controllers (API endpoints)
      model/                # JPA entities
      repository/           # Spring Data repositories
      security/             # JWT utils & filters
      service/              # Business logic

src/main/resources/
  application.properties  # DB, JWT, CORS configs
  data.sql                # Optional seed data

Dockerfile
pom.xml
README.md
```

---

## Installation (Local) 🧭

Prerequisites:
- Java 17+
- Maven (or use the included Maven wrapper)
- PostgreSQL (or use H2 for quick local runs)

Steps:
1. Clone the repository
   ```bash
   git clone <repo-url>
   cd com.ecommerce
   ```
2. Configure database in `src/main/resources/application.properties` or via environment variables:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ecommercedb
   spring.datasource.username=your_user
   spring.datasource.password=your_pass
   jwt.secret=<your_jwt_secret>
   jwt.expiration=86400000
   ```
3. Run the application:
   ```bash
   # Linux / macOS
   ./mvnw spring-boot:run

   # Windows (PowerShell)
   .\mvnw.cmd spring-boot:run
   ```

---

## Usage 🚀

- Default server port: 8080 (configurable via `server.port`)
- Example: open `http://localhost:8080/api/products` to fetch products
- Use the Auth endpoints to register and obtain a JWT, then pass it in `Authorization: Bearer <token>` for protected routes.

### Docker

Build and run a Docker image:
```bash
docker build -t ecommerce-backend .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://<host>:5432/ecommercedb" \
  -e SPRING_DATASOURCE_USERNAME="<user>" \
  -e SPRING_DATASOURCE_PASSWORD="<pass>" \
  ecommerce-backend
```

---

## API Endpoints (Highlights) 🔌

> All endpoints are prefixed with `/api`.

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | /api/auth/register | No | Register a new user
| POST | /api/auth/login | No | Login; returns JWT token
| GET  | /api/products | No | List all products
| GET  | /api/products/{id} | No | Get product details
| GET  | /api/products/category/{categoryId} | No | Products by category
| GET  | /api/products/search?keyword=.. | No | Search products
| POST | /api/products | ADMIN | Create a product
| PUT  | /api/products/{id} | ADMIN | Update a product
| DELETE | /api/products/{id} | ADMIN | Delete a product
| GET  | /api/categories | No | List categories
| POST | /api/categories | ADMIN | Create category
| POST | /api/orders | AUTH | Create an order
| GET  | /api/orders/user/{userId} | AUTH | Get orders for a user
| PUT  | /api/orders/{id}/status | ADMIN | Update order status

### Example: Login (curl)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret"}'

# Response: {"token":"eyJhbGci..."}
```

### Example: Create Order (JSON body)

```json
{
  "userId": 1,
  "shippingAddress": "123 Main St, City",
  "orderItems": [
    {"productId": 10, "quantity": 2, "price": 19.99},
    {"productId": 12, "quantity": 1, "price": 49.5}
  ]
}
```

---

## Screenshots / Demo 🖼️

Add UI screenshots or a demo link here (update when frontend is available):

- Frontend demo: https://zee-commerce-frontend.vercel.app/ (example)

---

## Contributing 🤝

Thanks for wanting to contribute! A simple guideline:
1. Fork the repo and create a feature branch: `feature/my-feature`
2. Follow existing code conventions and add tests where applicable
3. Open a pull request with a clear description
4. Ensure all tests pass (`./mvnw test`)

Consider adding a `CONTRIBUTING.md` for project-specific practices.

---

## License

This repository uses the **MIT License**. Add a `LICENSE` file to the repository root or change this section to your preferred license.

---

## Contact

- Name: Zeeshan Raza
- Email: zeeshanraza0201@gmail.com
- GitHub: https://github.com/raza-zeeshan
- LinkedIn: https://www.linkedin.com/in/zeeshanraza01/

