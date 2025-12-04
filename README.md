# Smart Travel Booking Platform

## Overview
A travel booking system built with Spring Boot 3+ that utilizes small services (microservices) communicating with each other via REST APIs, Feign Clients, and WebClient to handle bookings.

## Architecture

### Microservices (6)
<img width="3123" height="1412" alt="Untitled diagram-2025-12-04-120527" src="https://github.com/user-attachments/assets/d6d5a09d-d611-45d6-8543-73c7123b8e8d" />


### Service Details

| Service | Port | Technology | What it does |
|---------|------|------------|-------------|
| **User Service** | 8081 | REST API | Manages user accounts and checks if they are valid |
| **Flight Service** | 8082 | REST API | Manages flight availability and bookings |
| **Hotel Service** | 8083 | REST API | Manages hotel availability and room bookings |
| **Notification Service** | 8084 | REST API | Sends messages to users (Email/Push) |
| **Payment Service** | 8085 | REST + WebClient | Takes payments and updates booking information |
| **Booking Service** | 8086 | REST + Feign + WebClient | Main service that handles all travel bookings |

## Tools and Languages Used

- **Java**: 17 or newer
- **Spring Boot**: 3.2.0
- **Spring Cloud OpenFeign**: Helps services call each other
- **Spring WebFlux**: Allows non-blocking service calls
- **Spring Data JPA**: Helps work with databases
- **H2 Database**: A simple database that stores data in memory
- **Lombok**: Saves you from writing lots of code
- **SpringDoc OpenAPI**: Creates API documentation (Swagger)
- **Maven**: Manages project libraries

## Setup and Running the System

### 1. Get the Code
```bash
git clone https://github.com/Dulithapathum/ITS-4243-Assignment-02.git .
```

### 2. Build All Services
Each service needs to be built on its own:

```bash
# User Service
cd user-service
mvn clean install
cd ..

# Flight Service
cd flight-service
mvn clean install
cd ..

# Hotel Service
cd hotel-service
mvn clean install
cd ..

# Notification Service
cd notification-service
mvn clean install
cd ..

# Payment Service
cd payment-service
mvn clean install
cd ..

# Booking Service
cd booking-service
mvn clean install
cd ..
```

### 3. Start All Services

**Important**: Start services in this exact order so they can find each other:

```bash
# Terminal 1 - User Service
cd user-service
mvn spring-boot:run

# Terminal 2 - Flight Service
cd flight-service
mvn spring-boot:run

# Terminal 3 - Hotel Service
cd hotel-service
mvn spring-boot:run

# Terminal 4 - Notification Service
cd notification-service
mvn spring-boot:run

# Terminal 5 - Payment Service
cd payment-service
mvn spring-boot:run

# Terminal 6 - Booking Service (Start this last)
cd booking-service
mvn spring-boot:run
```

### 4. Check if Services are Working

Visit these links to see if everything started:
- User Service: http://localhost:8081/swagger-ui.html
- Flight Service: http://localhost:8082/swagger-ui.html
- Hotel Service: http://localhost:8083/swagger-ui.html
- Notification Service: http://localhost:8084/swagger-ui.html
- Payment Service: http://localhost:8085/swagger-ui.html
- Booking Service: http://localhost:8086/swagger-ui.html

## How to Test with Postman

### Load the Test Collection
1. Open Postman
2. Click **Import**
3. Choose `Smart_Travel_Platform.postman_collection.json`
4. All test requests will be loaded

### Testing in Order

**Step 1: Create a New User**
```
POST http://localhost:8081/api/users
```
<img width="1920" height="1030" alt="1 Create User" src="https://github.com/user-attachments/assets/85502a21-247a-4e90-a676-41dabbc1fcff" />


**Step 2: Create a Flight**
```
POST http://localhost:8082/api/flights
```
<img width="1920" height="1030" alt="3 Create Flight" src="https://github.com/user-attachments/assets/91bd5fd3-50bb-4e0e-9a88-ad2c83273583" />


**Step 3: Create a Hotel**
```
POST http://localhost:8083/api/hotels
```
<img width="1920" height="1027" alt="2 Create Hotel" src="https://github.com/user-attachments/assets/828afcd1-2c0b-4a22-9ad3-6bda10b43a3c" />


**Step 4: Create a Booking (This runs everything)**
```
POST http://localhost:8086/api/bookings
```
<img width="1920" height="1030" alt="4 Create Booking" src="https://github.com/user-attachments/assets/3ecb2745-91bf-4917-b0b2-431f379c4c1b" />


**Step 5: Make a Payment**
```
POST http://localhost:8085/api/payments/process
```
<img width="1920" height="1030" alt="6 Create Paymnet" src="https://github.com/user-attachments/assets/7b38f1c6-4026-45a1-a82e-069aea3c7843" />


**Step 6: Check the Booking**
```
GET http://localhost:8086/api/bookings/1
```
<img width="1920" height="1028" alt="7 GET Booking Updated Notification" src="https://github.com/user-attachments/assets/70ead38e-73be-42b0-9b9a-017a1ca5e11c" />


## What This System Can Do

### ✅ Microservices Design
- 6 separate services that work independently
- Each service runs on its own port
- Each service has its own database

### ✅ Services Communicate
- **Feign Client**: Fast way to call Flight and Hotel services
- **WebClient**: Another way to call User, Notification, and Payment services
- No old RestTemplate used

### ✅ API Documentation
- Each service shows its APIs using Swagger UI
- You can test APIs from your browser

### ✅ Database Management
- Uses @Transactional to handle database changes properly
- Fixes problems if something goes wrong

### ✅ Input Checking
- Checks if data is correct before saving
- Custom error messages for problems

## Error Handling

Every service knows how to handle problems:

- **ResourceNotFoundException** - Can't find something (404 error)
- **Validation Errors** - Data is not correct (400 error)
- **Business Logic Errors** - Something is wrong with the request (400/409 error)
- **Service Communication Errors** - Can't reach another service (503 error)
- **Other Errors** - Something unexpected happened (500 error)

## Log Files

All services write logs using SLF4J and Logback:
- Shows what requests come in and what responses go out
- Shows when services talk to each other
- Shows when problems happen

## Who Made This

Dulitha Pathum

## License

This project is made for learning purposes.

---



