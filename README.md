# 🛒 E-Commerce Application

A production-style **full-stack E-Commerce application** built using **Spring Boot, React, MySQL, Spring Security, JWT, and Docker**.

The application provides authentication, product and category management, shopping cart functionality, order processing, inventory management, role-based authorization, automated testing, Docker support, and cloud deployment.

---

## 📌 Project Overview

This project implements a full-stack e-commerce application with:

- Spring Boot REST API backend
- React frontend
- MySQL database
- JWT-based authentication
- Role-based authorization
- Customer and Admin functionality
- Product and category management
- Shopping cart management
- Order processing
- Inventory management
- Automated testing
- Docker support
- Cloud deployment

The application supports two primary roles:

### 👤 CUSTOMER

Customers can:

- Register an account
- Login
- Browse products
- Search products
- Filter products by category
- View product details
- Add products to cart
- Update cart quantities
- Remove products from cart
- Place orders
- View order history
- Cancel eligible orders

### 👨‍💼 ADMIN

Administrators can:

- View dashboard statistics
- Manage products
- Manage categories
- View all customer orders
- View customer details
- Update order status
- Manage inventory through product management

---

# 🎯 Features

## 🔐 Authentication & Authorization

- Customer registration
- User login
- JWT authentication
- Role-based authorization
- CUSTOMER and ADMIN roles
- Protected REST endpoints
- Protected React routes
- Admin-only frontend routes
- Password encryption using Spring Security

---

## 📦 Product Management

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
- Product availability validation

---

## 🗂️ Category Management

- Create categories
- Get all categories
- Get category by ID
- Update categories
- Delete categories
- Associate products with categories

---

## 🛒 Shopping Cart

- Get current cart
- Add products to cart
- Update product quantity
- Remove cart items
- Clear cart
- Automatic subtotal calculation
- Automatic total calculation
- Stock validation
- Cart count displayed in the frontend navbar

---

## 📋 Order Management

- Create orders from cart
- Get customer orders
- Get order by ID
- Cancel eligible orders
- Admin order management
- Order status management
- Customer information displayed to admin
- Order item price snapshot
- Automatic stock reduction
- Automatic cart clearing after successful order

---

## 📊 Order Status Workflow

Orders follow a controlled status workflow:

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