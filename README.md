# 🛒 E-Commerce REST API

A production-style **E-Commerce REST API** built using Spring Boot.  
The application provides authentication, product and category management, shopping cart functionality, order processing, inventory management, role-based authorization, automated testing, Docker support, and cloud deployment.

---

## 📌 Project Overview

This project implements the backend of an e-commerce application using a layered REST architecture.

The application supports two primary roles:

- **CUSTOMER** – Browse products, manage cart, and place orders
- **ADMIN** – Manage products, categories, and orders

The application uses **JWT-based authentication** and **MySQL** for persistent data storage.

---

## 🎯 Features

### 🔐 Authentication & Authorization

- Customer registration
- User login
- JWT authentication
- Role-based authorization
- CUSTOMER and ADMIN roles
- Protected REST endpoints
- Password encryption using Spring Security

### 📦 Product Management

- Create products
- Get all products
- Get product by ID
- Update products
- Delete products
- Product name search
- Pagination
- Sorting
- Category association
- Stock management

### 🗂️ Category Management

- Create categories
- Get all categories
- Get category by ID
- Update categories
- Delete categories

### 🛒 Shopping Cart

- Get current cart
- Add products to cart
- Update product quantity
- Remove cart items
- Clear cart
- Automatic subtotal calculation
- Automatic total calculation
- Stock validation

### 📋 Order Management

- Create orders from cart
- Get customer orders
- Get order by ID
- Cancel orders
- Admin order management
- Order status management
- Order item price snapshot
- Automatic stock reduction
- Automatic cart clearing after successful order

### 📊 Order Status Workflow

```text
PENDING
   ↓
CONFIRMED
   ↓
PROCESSING
   ↓
SHIPPED
   ↓
DELIVERED