# E-Learning Platform Backend

A learning platform backend built with **Java 21**, **Spring Boot 3**, **PostgreSQL**, and **JWT authentication**.
This is a personal project to refresh and demonstrate modern Java backend skills.

---

## 🚀 Tech Stack

* **Java 21**, **Spring Boot 3**
* **Spring Security + JWT** (role-based auth: STUDENT / TEACHER)
* **Spring Data JPA (Hibernate)**, **Flyway**
* **PostgreSQL** (local via Docker)
* **Maven**, **JUnit 5**, **Mockito**

---

## 📚 Features (current)

* **Authentication**

    * Register/login with encrypted password (BCrypt)
    * Role-based access (STUDENT vs TEACHER)

* **Lessons & Courses**

    * Courses have multiple lessons
    * Lessons can include quizzes

* **Quizzes**

    * `POST /quizzes` → create a quiz (TEACHER only)
    * `GET /quizzes/{quizId}/questions` → list quiz questions (STUDENT/TEACHER)
    * `POST /quizzes/{quizId}/submit` → student submits answers

        * Validates that question belongs to quiz
        * Prevents duplicate submissions

---

## 🛠️ Setup

### Prerequisites

* JDK 21
* Maven
* Docker

### Run locally

```bash
# Start PostgreSQL
docker compose up -d

# Run the app
mvn spring-boot:run
```

Swagger UI available at:
👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🔑 Authentication Flow

1. Register (`POST /auth/register`)
2. Login (`POST /auth/login`) → get JWT token
3. Authorize in Swagger (`Bearer <token>`)

---

## 🧪 Sample API Calls

### Register

```http
POST /auth/register
Content-Type: application/json

{
  "email": "bob@student.com",
  "password": "StrongPass123"
}
```

### Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "bob@student.com",
  "password": "StrongPass123"
}
```

Response:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1...",
  "refreshToken": "...",
  "tokenType": "Bearer"
}
```

### Submit Quiz

```http
POST /quizzes/2/submit
Authorization: Bearer eyJhbGciOiJIUzI1...
Content-Type: application/json

[
  { "questionId": 10, "answer": "@SpringBootApplication" },
  { "questionId": 11, "answer": "8080" }
]
```

Response:

```json
[
  { "id": 1, "questionId": 10, "correct": true },
  { "id": 2, "questionId": 11, "correct": true }
]
```

---

## 📅 Progress Log

* **Day 1**: Project setup (Spring Boot, Docker, PostgreSQL, Flyway)
* **Day 2**: User registration with hashed password
* **Day 3**: JWT security (roles: STUDENT/TEACHER), Lesson entity fix
* **Day 4**: QuizController (create, list questions, submit with validation)

---

## 🗺️ Roadmap (Next Steps)

* **Day 5**: View student submissions and quiz results
* **Day 6**: Track lesson/course progress per student
* **Day 7**: Teacher dashboard (view class results, quiz statistics)
* **Day 8**: Add refresh tokens & logout endpoint
* **Day 9**: Unit + integration tests with Testcontainers
* **Day 10**: CI/CD pipeline (GitHub Actions + Docker)

---

## 👩‍💻 Author

**Kateryna Yashnyk**
[GitHub: lunaridelmar](https://github.com/lunaridelmar)

---

