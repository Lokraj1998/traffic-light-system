🚦 Traffic Light Controller API

A Spring Boot–based REST API to control traffic lights at an intersection.

This project implements a simple, extensible traffic light system that supports:

Multiple directions (NORTH, SOUTH, EAST, WEST)

State transitions (RED, YELLOW, GREEN)

Pause and resume operations

Conflict validation (no unsafe green signals)

Timing history tracking

Concurrency-safe in-memory storage

The solution is intentionally designed to be simple, clean, testable, and extensible, in line with the kata requirements.

📌 Key Features

Manage traffic light states for multiple directions

Prevent conflicting green signals

Pause and resume intersection operations

Track timing/state history

RESTful API design

Thread-safe in-memory storage

Clean separation of concerns

Comprehensive unit tests (controller, service, repository)

🏗️ Architecture Overview
controller
└── TrafficLightController

service
└── TrafficLightService

domain
└── Intersection
└── TrafficLight
└── Direction (enum)
└── LightColor (enum)
└── LightStateHistory

repository
└── HistoryRepository
└── InMemoryHistoryRepository

exception
└── TrafficLightException
└── GlobalExceptionHandler

🔹 Design Decisions
1. In-Memory Storage

The kata scope does not require persistence, so an in-memory repository (InMemoryHistoryRepository) is used.
It is thread-safe and easily replaceable with a database-backed implementation later.

2. Concurrency

ConcurrentHashMap for intersection storage

CopyOnWriteArrayList for history

synchronized in service for state transitions

3. Business Rules

NORTH & SOUTH may be green together

EAST & WEST may be green together

Cross-axis greens are forbidden

Only GREEN transitions are validated

4. Error Handling

All business and validation exceptions are handled centrally using @RestControllerAdvice.

🔹 API Endpoints
Base URL
http://localhost:8080/api/intersections

1️⃣ Get Current State

GET /api/intersections/{id}/state

curl -X GET http://localhost:8080/api/intersections/default/state


Response

{
"NORTH": "RED",
"SOUTH": "RED",
"EAST": "RED",
"WEST": "RED"
}

2️⃣ Change a Light Color

POST /api/intersections/{id}/change?direction={DIRECTION}&color={COLOR}

curl -X POST "http://localhost:8080/api/intersections/default/change?direction=NORTH&color=GREEN"


Response

HTTP 200 OK

3️⃣ Pause the Intersection

POST /api/intersections/{id}/pause

curl -X POST http://localhost:8080/api/intersections/default/pause

4️⃣ Resume the Intersection

POST /api/intersections/{id}/resume

curl -X POST http://localhost:8080/api/intersections/default/resume

5️⃣ Conflict Example

If NORTH is already GREEN:

curl -X POST "http://localhost:8080/api/intersections/default/change?direction=EAST&color=GREEN"


Response

{
"timestamp": "2026-01-20T11:15:30.123",
"error": "Traffic Light Rule Violation",
"message": "Cannot set EAST to GREEN because NORTH is already GREEN"
}


HTTP Status: 409 Conflict

6️⃣ Invalid Input Example
curl -X POST "http://localhost:8080/api/intersections/default/change?direction=NORTH&color=BLUE"


Response

{
"timestamp": "2026-01-20T11:16:10.456",
"error": "Bad Request",
"message": "Invalid value 'BLUE' for parameter 'color'"
}


HTTP Status: 400 Bad Request

🧪 Testing Strategy

This project follows a layered testing approach:

1. Controller Tests (@WebMvcTest)

Validates REST endpoints

Ensures proper HTTP status codes

Mocks service layer

Tests error handling

Tests invalid parameters

2. Service Tests (Pure Unit Tests)

Validates business rules

Prevents conflicting greens

Tests pause/resume logic

Verifies history persistence

Covers edge cases

3. Repository Tests

Validates in-memory storage

Verifies isolation per intersection

Tests default behavior

Validates thread-safety

All tests are written using:

JUnit 5

Mockito

Spring Test (MockMvc)

▶️ Running the Application
Prerequisites

Java 17

Maven 3.8+

Build & Run
mvn clean install
mvn spring-boot:run

▶️ Running Tests
mvn test

⚠️ Known Limitations

In-memory storage (no persistence)

Single default intersection

No automatic light sequencing

No scheduling

No authentication or security

🚀 Future Improvements

Given more time, I would:

Add support for multiple intersections

Add configurable light sequences

Add scheduling and timers

Persist history using a database

Add Swagger/OpenAPI

Add role-based access

Add real concurrency stress tests

🎯 Why This Design

This implementation intentionally favors:

Simplicity over over-engineering

Clean separation of concerns

Explicit business rules

Test-driven development

Clear error semantics

Extensibility

👤 Author

Lokraj Belbase
Java Full-Stack Developer
Spring Boot • Microservices • AWS

📝 Final Notes

This project was implemented as a coding kata.
The focus was on:

Clarity

Clean code

Testability

Business rule enforcement

Narrative-style implementation

🎉 Summary

This solution demonstrates:

✔ Clean REST API design
✔ Business rule enforcement
✔ Thread-safe architecture
✔ Layered testing
✔ Production-grade error handling
✔ Simple and extensible design

Want Me to Customize It Further?

If you want, I can next:

Add a Git history narrative section

Add test coverage numbers

Add OpenAPI section

Add architecture diagram

Add deployment section