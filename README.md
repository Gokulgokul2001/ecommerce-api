# 🛒 E-Commerce REST API

A full-stack E-Commerce application backend built using **Java, Spring Boot, Spring Security, JWT, MySQL, JPA/Hibernate, and Docker**.

The REST API provides authentication, role-based authorization, product and category management, shopping cart functionality, wishlist management, order processing, inventory management, validation, exception handling, automated testing, API documentation, and Docker support.

---

## 📌 Project Overview

The E-Commerce API is the backend service for an online shopping application.

It provides RESTful APIs for two primary user roles:

### 👤 Customer

Customers can:

- Register an account
- Login
- Browse products
- View product details
- Manage their shopping cart
- Manage their wishlist
- Place orders
- View their orders
- View individual order details
- Cancel eligible orders

### 👨‍💼 Admin

Administrators can:

- Manage products
- Manage categories
- View all customer orders
- View customer information associated with orders
- Update order status
- Manage product inventory

---

# 🎯 Features

## 🔐 Authentication & Authorization

- User registration
- User login
- JWT-based authentication
- Spring Security
- Role-based authorization
- Customer and Admin roles
- Password encryption
- Protected REST endpoints

Supported roles:

```text
ROLE_CUSTOMER
ROLE_ADMIN