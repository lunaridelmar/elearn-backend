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
    * Student registration (`POST /auth/register`)
    * Teacher registration (`POST /auth/register-teacher`) — temporary, for testing/demo
    * Login with encrypted password (BCrypt) → returns access + refresh tokens
    * Refresh endpoint (`POST /auth/refresh`) → get new tokens without re-login
    * Role-based access (STUDENT vs TEACHER)


* **Lessons & Courses**
    * A course contains multiple lessons
    * Lessons can include quizzes
    * Quizzes can be created by teachers and answered by students
    * Submissions are validated (question must belong to quiz, no duplicate submissions)


* **Quizzes**
    * Teachers can create quizzes and attach them to lessons
        * `POST /quizzes` → create a quiz (TEACHER only)
    * Students and teachers can view quiz questions
        * `GET /quizzes/{quizId}/questions` → list quiz questions
    * Students can submit answers
        * `POST /quizzes/{quizId}/submit` → submit answers
        * Validates that each question belongs to the quiz
        * Prevents duplicate submissions per student/question
    * Submissions are stored with correctness (true/false) for result tracking

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

1. **Register student** → `POST /auth/register`
2. **(Optional for testing)** Register teacher → `POST /auth/register-teacher`
3. **Login** → `POST /auth/login` → returns access + refresh tokens
4. **Authorize** in Swagger using `Bearer <accessToken>`
5. **Refresh** → `POST /auth/refresh` → get new tokens when access token expires

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
* **Day 5**: Student submissions + quiz results
* **Day 6**: JWT login with access + refresh tokens
* **Day 7**: `/auth/refresh` endpoint and token expiry handling

---

## 🗺️ Roadmap (Next Steps)

* **Day 8**: Teacher dashboard (view class results, quiz statistics)
* **Day 9**: Unit + integration tests with Testcontainers
* **Day 10**: CI/CD pipeline (GitHub Actions + Docker)

---

## 👩‍💻 Author

**Kateryna Yashnyk**
[GitHub: lunaridelmar](https://github.com/lunaridelmar)

---

