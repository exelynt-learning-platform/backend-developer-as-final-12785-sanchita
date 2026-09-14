# backend-developer-as-final-12785-sanchita
# Resource Booking System

A RESTful Resource Booking System built using Java, Spring Boot, Spring Security, JWT, Spring Data JPA, Hibernate, and MySQL.

## Features

* JWT-based authentication
* ADMIN and USER role-based authorization
* Resource Type management
* Resource management
* Reservation management
* Reservation ownership validation
* Reservation status management
* Reservation price calculation
* Filtering, pagination, and sorting
* Request validation
* Global exception handling
* BCrypt password encryption

## Technology Stack

* Java 17+
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA / Hibernate
* MySQL
* Maven
* JUnit / Mockito
* Postman

## Prerequisites

* Java 17 or higher
* Maven
* MySQL
* Git
* Postman (for API testing)

## Database Configuration

This project uses MySQL.

### 1. Create the Database
Open MySQL and execute:
CREATE DATABASE resourcemanagement;

### 2. Configure the Database
The application uses the following configuration:

spring.datasource.url=jdbc:mysql://localhost:3306/resourcemanagement
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=${JWT_SECRET}

Hibernate automatically creates and updates the required tables using:
spring.jpa.hibernate.ddl-auto=update

## Environment Variables
Set the following environment variables before running the application:
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
JWT_SECRET= ResourceBookingSystemSecretKey2026SecureABC123XYZ789


## Setup and Run
### 1. Clone the Repository
git clone https://github.com/exelynt-learning-platform/backend-developer-as-final-12785-sanchita


### 2. Build the Project
mvn clean install

### 3. Run the Application
mvn spring-boot:run

The application runs on:
http://localhost:8080


## Testing
Run the test cases using:
mvn test

## Postman Documentation
Complete API documentation, including endpoints, request bodies, authorization, query parameters, and example responses, is available in Postman.

### Postman Collection

The Postman collection is included in this repository.

Import the downloaded JSON file into Postman to test the APIs.

### Postman Published Documentation

https://documenter.getpostman.com/view/32768115/2sBYAytp8g


## Author
Sanchita Aglave
