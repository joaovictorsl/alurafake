# AluraFake API

This project is a Spring Boot application that simulates a part of Alura's domain, focusing on user and course management, and interactive tasks.

## Technologies Used

*   Java 18+
*   Spring Boot
*   Spring Data JPA
*   MySQL
*   Flyway for database migrations

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

*   **Java Development Kit (JDK) 18 or higher**: You can download it from [Oracle JDK](https://www.oracle.com/java/technologies/javase-jdk18-downloads.html) or use OpenJDK.
*   **Maven**: For dependency management and building the project. Download from [Apache Maven](https://maven.apache.org/download.cgi).
*   **MySQL Server**: The application uses MySQL as its database. Ensure you have a MySQL server running.
*   **Docker (Optional but Recommended)**: For easily setting up a MySQL database.

### Database Setup with Docker (Recommended)

You can quickly set up a MySQL database using Docker.

1.  **Run MySQL container**:
    ```bash
    docker compose up -d
    ```
    This command will:
    *   Create a container named `mysql`.
    *   Set the root password to `rootpassword`.
    *   Create a database named `alurafake`.
    *   Map port `3306` of your host to port `3306` of the container.

### Project Configuration

1.  **Clone the repository**:
    ```bash
    git clone git@github.com:joaovictorsl/alurafake.git
    cd alurafake
    ```

2.  **Database Connection**:
    The application expects a MySQL database named `alurafake` accessible at `localhost:3306`.
    You can configure your database connection in `src/main/resources/application.properties`.

### Running the Application

To run the Spring Boot application, navigate to the project root directory and execute:

1.  **Make the `mvnw` script executable (if not already done)**:
    ```bash
    chmod +x ./mvnw
    ```

2.  **Run the application**:
    ```bash
    make run
    ```

The application will start on `http://localhost:8080`.

## Testing

The project includes unit and integration tests. To run all tests, use:

```bash
make test
```

## API Endpoints (cURL Examples)

Here are some example `curl` commands to interact with the API. The base URL for all endpoints is `http://localhost:8080`.

### User Endpoints

#### Create a new User

```bash
curl -X POST http://localhost:8080/user/new \
  -H "Content-Type: application/json" \
  -d '{
        "name": "John Doe",
        "email": "john.doe@example.com",
        "role": "INSTRUCTOR",
        "password": "123456"
      }'
```

#### Get all Users

```bash
curl -u john.doe@example.com:123456 -X GET http://localhost:8080/user/all \
  -H "Content-Type: application/json"
```

### Course Endpoints

#### Create a new Course

```bash
curl -u john.doe@example.com:123456 -X POST http://localhost:8080/course/new \
  -H "Content-Type: application/json" \
  -d '{
        "title": "Spring Boot Basics",
        "description": "Aprenda Spring Boot na Alura",
        "emailInstructor": "john.doe@example.com"
      }'
```

#### Get all Courses

```bash
curl -u john.doe@example.com:123456 -X GET http://localhost:8080/course/all
```

#### Publish a Course

```bash
curl -u john.doe@example.com:123456 -X POST http://localhost:8080/course/{id}/publish
```

### Task Endpoints

#### Create an Open Text Task

```bash
curl -u john.doe@example.com:123456 -X POST http://localhost:8080/task/new/opentext \
  -H "Content-Type: application/json" \
  -d '{
        "courseId": 1,
        "statement": "What did you learn today?",
        "order": 1
      }'
```

#### Create a Single Choice Task

```bash
curl -u john.doe@example.com:123456 -X POST http://localhost:8080/task/new/singlechoice \
  -H "Content-Type: application/json" \
  -d '{
        "courseId": 1,
        "statement": "Which of these is a programming language?",
        "order": 2,
        "options": [
            {
                "option": "Java",
                "isCorrect": true
            },
            {
                "option": "HTML",
                "isCorrect": false
            },
            {
                "option": "CSS",
                "isCorrect": false
            }
        ]
      }'
```

#### Create a Multiple Choice Task

```bash
curl -u john.doe@example.com:123456 -X POST http://localhost:8080/task/new/multiplechoice \
  -H "Content-Type: application/json" \
  -d '{
        "courseId": 1,
        "statement": "Which of these are programming languages?",
        "order": 3,
        "options": [
            {
                "option": "Python",
                "isCorrect": true
            },
            {
                "option": "SQL",
                "isCorrect": false
            },
            {
                "option": "JavaScript",
                "isCorrect": true
            }
        ]
      }'
```