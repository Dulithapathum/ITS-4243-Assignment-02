# Smart Travel Booking Platform

## Overview
A distributed travel booking backend platform built using Spring Boot 3+ microservices architecture with inter-service communication using REST API, Feign Client, and WebClient.

## Architecture

### Microservices (6)

```
┌─────────────────────────────────────────────────────────────────────┐
│                      SMART TRAVEL PLATFORM                           │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────┐         ┌──────────────────────────────────┐
│ User Service │◄────────┤   Booking Service (Orchestrator) │
│   (8081)     │ WebClient       (8086)                     │
└──────────────┘         │                                    │
                         │  • Feign Client to Flight & Hotel│
┌──────────────┐         │  • WebClient to User & Notify    │
│Flight Service│◄────────┤                                    │
│   (8082)     │ Feign   │                                    │
└──────────────┘         │                                    │
                         │                                    │
┌──────────────┐         │                                    │
│Hotel Service │◄────────┤                                    │
│   (8083)     │ Feign   │                                    │
└──────────────┘         │                                    │
                         │                                    │
┌──────────────┐         │                                    │
│Notification  │◄────────┤                                    │
│Service(8084) │ WebClient                                   │
└──────────────┘         └──────────────────────────────────┘
                                        ▲
                                        │ WebClient
                                        │
                         ┌──────────────┴───────┐
                         │  Payment Service     │
                         │     (8085)           │
                         └──────────────────────┘
```

### Service Details

| Service | Port | Technology | Description |
|---------|------|------------|-------------|
| **User Service** | 8081 | REST API | Manages user accounts and validation |
| **Flight Service** | 8082 | REST API | Manages flight inventory and reservations |
| **Hotel Service** | 8083 | REST API | Manages hotel inventory and bookings |
| **Notification Service** | 8084 | REST API | Sends notifications (Email/Push) |
| **Payment Service** | 8085 | REST + WebClient | Processes payments and updates booking status |
| **Booking Service** | 8086 | REST + Feign + WebClient | Main orchestrator for travel bookings |

## Communication Patterns

### Feign Client (Declarative REST Client)
- **Booking → Flight Service**: Check availability, reserve seats
- **Booking → Hotel Service**: Check availability, reserve rooms

### WebClient (Reactive REST Client)
- **Booking → User Service**: Validate user, fetch user details
- **Booking → Notification Service**: Send booking notifications
- **Payment → Booking Service**: Update booking status after payment

## Technology Stack

- **Java**: 17+
- **Spring Boot**: 3.2.0
- **Spring Cloud OpenFeign**: For declarative REST clients
- **Spring WebFlux**: For reactive WebClient
- **Spring Data JPA**: For database operations
- **H2 Database**: In-memory database for each service
- **Lombok**: Reduce boilerplate code
- **SpringDoc OpenAPI**: API documentation (Swagger)
- **Maven**: Dependency management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Postman (for API testing)

## Project Structure

```
smart-travel-platform/
├── user-service/
│   ├── src/main/java/com/travel/user/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── exception/
│   └── pom.xml
├── flight-service/
│   └── [similar structure]
├── hotel-service/
│   └── [similar structure]
├── notification-service/
│   └── [similar structure]
├── payment-service/
│   ├── config/WebClientConfig.java
│   └── [similar structure]
├── booking-service/
│   ├── config/WebClientConfig.java
│   ├── client/FlightServiceClient.java (Feign)
│   ├── client/HotelServiceClient.java (Feign)
│   └── [similar structure]
└── README.md
```

## Setup and Installation

### 1. Clone the Repository
```bash
cd "Assignment 2 - micro"
```

### 2. Build All Services
Each service needs to be built independently:

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

### 3. Start Services in Order

**Important**: Start services in this specific order to ensure dependencies are available:

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

### 4. Verify Services are Running

Check each service at:
- User Service: http://localhost:8081/swagger-ui.html
- Flight Service: http://localhost:8082/swagger-ui.html
- Hotel Service: http://localhost:8083/swagger-ui.html
- Notification Service: http://localhost:8084/swagger-ui.html
- Payment Service: http://localhost:8085/swagger-ui.html
- Booking Service: http://localhost:8086/swagger-ui.html

## Complete Booking Flow

### Step-by-Step Booking Process

```
1. User sends booking request to Booking Service
   POST http://localhost:8086/api/bookings
   {
     "userId": 1,
     "flightId": 1,
     "hotelId": 1,
     "travelDate": "2025-01-10"
   }

2. Booking Service validates user via WebClient
   → GET http://localhost:8081/api/users/1/validate

3. Booking Service checks flight availability via Feign Client
   → GET http://localhost:8082/api/flights/1/check-availability
   → GET http://localhost:8082/api/flights/1

4. Booking Service checks hotel availability via Feign Client
   → GET http://localhost:8083/api/hotels/1/check-availability
   → GET http://localhost:8083/api/hotels/1

5. Booking Service calculates total cost (Flight + Hotel)

6. Booking Service creates booking with status "PENDING"

7. Booking Service reserves flight seat via Feign Client
   → POST http://localhost:8082/api/flights/1/reserve

8. Booking Service reserves hotel room via Feign Client
   → POST http://localhost:8083/api/hotels/1/reserve

9. Booking Service sends notification via WebClient
   → POST http://localhost:8084/api/notifications/send

10. Client processes payment
    POST http://localhost:8085/api/payments/process
    {
      "bookingId": 1,
      "userId": 1,
      "amount": 600.00,
      "paymentMethod": "CREDIT_CARD"
    }

11. Payment Service updates booking status to "CONFIRMED" via WebClient
    → PUT http://localhost:8086/api/bookings/1/status

12. Final notification sent to user
```

## API Testing with Postman

### Import Postman Collection
1. Open Postman
2. Click **Import**
3. Select `Smart_Travel_Platform.postman_collection.json`
4. Collection will be loaded with all endpoints

### Testing Sequence

**Step 1: Create User**
```
POST http://localhost:8081/api/users
```

**Step 2: Create Flight**
```
POST http://localhost:8082/api/flights
```

**Step 3: Create Hotel**
```
POST http://localhost:8083/api/hotels
```

**Step 4: Create Booking (Orchestrated Flow)**
```
POST http://localhost:8086/api/bookings
```

**Step 5: Process Payment**
```
POST http://localhost:8085/api/payments/process
```

**Step 6: Verify Booking Status**
```
GET http://localhost:8086/api/bookings/1
```

## Database Access

Each service uses H2 in-memory database. Access H2 console:

- User Service: http://localhost:8081/h2-console
- Flight Service: http://localhost:8082/h2-console
- Hotel Service: http://localhost:8083/h2-console
- Notification Service: http://localhost:8084/h2-console
- Payment Service: http://localhost:8085/h2-console
- Booking Service: http://localhost:8086/h2-console

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:{servicename}db`
- Username: `sa`
- Password: (leave empty)

## Key Features

### ✅ Microservices Architecture
- 6 independent services
- Each service on separate port
- H2 in-memory database per service

### ✅ Inter-Service Communication
- **Feign Client**: Flight & Hotel service calls
- **WebClient**: User, Notification, and Payment calls
- No deprecated RestTemplate used



### ✅ API Documentation
- Swagger UI available for all services
- Interactive API testing

### ✅ Transaction Management
- @Transactional annotations
- Proper rollback handling

### ✅ Validation
- Jakarta Bean Validation
- Custom exceptions

## Error Handling

All services implement comprehensive error handling:

- **ResourceNotFoundException** (404)
- **Validation Errors** (400)
- **Business Logic Errors** (400/409)
- **Service Communication Errors** (503)
- **Generic Exceptions** (500)

## Logging

All services use SLF4J with Logback:
- Request/Response logging
- Service communication logs
- Error tracking



## Communication Flow Summary

```
Booking Service (Main Orchestrator):
├── WebClient → User Service (Validate & Get Details)
├── Feign Client → Flight Service (Check & Reserve)
├── Feign Client → Hotel Service (Check & Reserve)
└── WebClient → Notification Service (Send Notifications)

Payment Service:
└── WebClient → Booking Service (Update Status)
```

## Troubleshooting

### Service Won't Start
- Ensure port is not already in use
- Check Java version: `java -version`
- Verify Maven: `mvn -version`

### Feign Client Errors
- Ensure target service is running
- Check URL in application.yml
- Verify network connectivity

### WebClient Timeout
- Increase timeout in configuration
- Check target service health
- Verify service URLs


## Contributors

Assignment 02 - Microservices Architecture

## License

This project is created for educational purposes.

---



