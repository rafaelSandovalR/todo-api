[![Docker Hub Pulls](https://img.shields.io/docker/pulls/rsandoval0408/todo-api?style=flat-square)](https://hub.docker.com/r/rsandoval0408/todo-api)

# Spring Boot To-Do List REST API

This is a complete, backend REST API for a simple To-Do List application. It is built with Java and Spring Boot and follows professional API design principles by supporting all four **CRUD** (Create, Read, Update, Delete) operations.

This project is fully unit-tested at the web layer (`TaskController`) using `MockMvc` and `Mockito` to ensure all endpoints and logic are working correctly.

## Technologies & Skills Demonstrated

* **Framework:** Spring Boot
* **Language** Java (17+)
* **API:** Spring Web (`@RestController`, `@GetMapping`, `@PostMapping`, etc.)
* **Database:** Spring Data JPA (with `JpaRepository`)
* **Testing** JUnit 5, Spring Boot Test (`@WebMvcTest`), Mockito, Hamcrest
* **Build:** Maven (dependencies managed in `pom.xml`)
* **Security** Spring Security, JWT (JSON Web Tokens), BCrypt
* **Database:** PostgreSQL (Persistent)
* **Containerization:** Docker
* **Server:** Embedded Tomcat

## How to Run

This application requires a PostgreSQL database and the Spring Boot API server. The fastest, most reliable way to run the full stack is using **Docker Compose**.

### Option 1: Quick Start (No Code Required)

You do not need Java, Maven, or the source code installed. You only need [Docker Desktop](https://www.docker.com/products/docker-desktop/).

1.  **Create a folder** anywhere on your computer.
2.  Inside that folder, create a file named **`docker-compose.yml`**.
3.  Paste the following content into it:

    ```yaml
    version: '3.8'
    services:
      # 1. The Database Service
      db:
        image: postgres:latest
        container_name: my-postgres-db
        ports:
          - "5432:5432"
        volumes:
          - postgres_data:/var/lib/postgresql
        environment:
          - POSTGRES_PASSWORD=mysecretpassword
          - POSTGRES_DB=tasks

      # 2. The API Application Service
      app:
        # Pulls the pre-built image from Docker Hub
        image: rsandoval0408/todo-api:latest
        container_name: todo-api-container
        ports:
          - "8080:8080"
        depends_on:
          - db
        environment:
          # Connects to the 'db' service on the internal Docker network
          - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/tasks
          - SPRING_DATASOURCE_USERNAME=postgres
          - SPRING_DATASOURCE_PASSWORD=mysecretpassword

    volumes:
      postgres_data:
    ```

4.  Open your terminal in that folder and run:
    ```bash
    docker-compose up
    ```
    *(Or `docker compose up` depending on your version)*

The application will start, connect to the database automatically, and be accessible at `http://localhost:8080`.

---

### Option 2: Developer Setup (Run from Source)

Use this method if you want to modify the code or run the application inside IntelliJ IDEA.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/rafaelSandovalR/todo-api.git
    cd todo-api
    ```

2.  **Start the Database:**
    You still need a running database. You can use the `docker-compose.yml` included in the source code to spin up *just* the database:
    ```bash
    docker-compose up -d db
    ```

3.  **Run the App:**
    * Open the project in IntelliJ IDEA.
    * Run the `TodoApiApplication.main()` method.
    * The app is configured in `application.properties` to connect to the database running on `localhost:5432`.




## API Endpoints (How to Use)
You can use a tool like [Postman](https://www.postman.com/) or IntelliJ's HTTP Client (`test.http`) to interact with this API.

**IMPORTANT:** All `/api/tasks` endpoints are secured. You **must** include a valid JWT in the `Authorization` header of your request:
`Authorization: Bearer <your_token>`

---
### Register a User
**`POST`** `/api/auth/register` 
Creates a new user account. The password will be securely encrypted.

**Request Body:**
```json
{
  "username": "new_user",
  "password": "secretpassword"
}
```

**Response:**
* **Status:** `201 Created`
* **Body:** `"User registered successfully"`

---
### Login(Get Token)
**`POST`** `/api/auth/login` Authenticates a user and returns a JWT(JSON Web Token). Copy this token to use in all other requests.

**Request Body:**
```json
{
  "username": "new_user",
  "password": "secretpassword"
}
```

**Response:**
* **JWT:** `eyJhbGciOiJIUzI1NiJ9.eyJzdWIiO...`

---
### The following examples require the JWT in the Authorization header (Bearer Token)

---
### Create a Task
**`POST`** `/api/tasks` Creates a new task. The request body must be a JSON object with the task's details.

**Request Body:**
```json
{
  "description": "Buy fruit",
  "completed": false
}
```

**Response:**
```json
{
  "id": 1,
  "description": "Buy fruit",
  "completed": false
}
```
---
### Get All Tasks
**`GET`** `/api/tasks` Returns a JSON list of all tasks currently in the database.

**Response:**
```json
[
  {
    "id": 1,
    "description": "Buy fruit",
    "completed": false
  },
  {
    "id": 2,
    "description": "Finish drawing",
    "completed": true
  }
]
```
---
### Get Tasks by Status (Search)
**`GET`** `/api/tasks/search` Returns a JSON list of all tasks that match a given status.

**Example Request:** `GET /api/tasks/search?completed=true`

**Response:**
```json
[
  {
    "id": 2,
    "description": "Walk the dog",
    "completed": true
  },
  {
    "id": 4,
    "description": "Feed the dog",
    "completed": true
  }
]
```
---
### Get a Single Task
**`GET`** `/api/tasks/{id}` Returns a single task by its ID.

**Example Request:** `GET /api/tasks/1`

**Response (Success):**
```json
{
  "id": 1,
  "description": "Buy fruit",
  "completed": false
}
```

**Response (Not Found)**
* **Status:** `404 Not Found`
---
### Update a Task
**`PUT`** `/api/tasks/1` Updates an existing task with new information. The full task object must be sent in the request body.

**Example Request:** `PUT /api/tasks/1`

**Request Body:**

```json
{
  "description": "Buy fruit and spinach",
  "completed": true
}
```
**Response:**
```json
{
  "id": 1,
  "description": "Buy fruit and spinach",
  "completed": true
}
```
---
### Delete a Task
**`DELETE`** `/api/tasks/{id}` Deletes a task by its ID. Returns an empty body with a `200 OK` status.

**Example Request:** `DELETE /api/tasks/1`

**Response:**
* **Status:** `200 OK`