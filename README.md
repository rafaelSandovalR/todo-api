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
* **Database:** PostgreSQL (Persistent)
* **Containerization:** Docker
* **Server:** Embedded Tomcat

## How to Run
This is a backend API, and it **requires a running PostgreSQL database** to connect to. The easiest way to run one locally is by using Docker.

### 1. **Start the PostgreSQL database**
Before running the app, you must start the Postgres database container. If you don't have it, run this command in your terminal first to get it:
```bash
  docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -v postgres_data:/var/lib/postgresql --name my-postgres-db postgres:latest
```
If you already have the container, just make sure its running
```bash
  docker start my-postgres-db
```
### 2. (Option A): **From Docker Hub**
This is the fastest way to run the application. This method assumes you have [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed.

1. **Run the To-Do API Container**
    * Once the database is running, pull and run the latest image from Docker Hub with this command:
         ```bash
         docker run -d -p 8080:8080 \
         -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/postgres \
         -e SPRING_DATASOURCE_USERNAME=postgres \
         -e SPRING_DATASOURCE_PASSWORD=mysecretpassword \
         --name todo-api-container \
         rsandoval0408/todo-api:latest
         ```
    * The server is now running and accessible at `http://localhost:8080`.

### 2. (Option B): **Run from Source (Developer Setup)**
1. **Clone the Repository:**
    ```bash
    git clone https://github.com/rafaelSandovalR/todo-api.git
    cd todo-api
    ```
2. **Run from IntelliJ (Easiest Way):**
   * Open the `pom.xml` file as a project in IntelliJ IDEA.
   * Maven will automatically download all the required dependencies.
   * The `application.properties` file is already configured to connect to your `my-postgres-db` container.
   * Navigate to `src/main/java/com/rsandoval/todo_api/TodoApiApplication.java` and click the green "play" arrow to run the `main` method.
   * The server will start on `http://localhost:8080`.




## API Endpoints (How to Use)
You can use a tool like [Postman](https://www.postman.com/) or IntelliJ's HTTP Client (`test.http`) to interact with this API.

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