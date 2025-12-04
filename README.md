# Smart Travel Booking Platform

## Overview
A travel booking system built with Spring Boot 3+ that uses small services (microservices) that talk to each other using REST API, Feign Client, and WebClient to handle bookings.

## Architecture

### Microservices (6)


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
git clone https://github.com/Dulithapathum/ITS-4243-Assignment-02.git
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

**Step 2: Create a Flight**
```
POST http://localhost:8082/api/flights
```

**Step 3: Create a Hotel**
```
POST http://localhost:8083/api/hotels
```

**Step 4: Create a Booking (This runs everything)**
```
POST http://localhost:8086/api/bookings
```

**Step 5: Make a Payment**
```
POST http://localhost:8085/api/payments/process
```

**Step 6: Check the Booking**
```
GET http://localhost:8086/api/bookings/1
```

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



