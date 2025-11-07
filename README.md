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
* **Database:** H2 (In-memory)
* **Server:** Embedded Tomcat

## How to Run This Project

1. **Clone the repository**
```bash
    git clone https://github.com/rafaelSandovalR/todo-api.git
    cd todo-api
```
2.  **Run from IntelliJ (Easiest Way):**
    * Open the `pom.xml` file as a project in IntelliJ IDEA.
    * Maven will automatically download all the required dependencies.
    * Navigate to `src/main/java/com/rsandoval/todoapi/TodoApiApplication.java` and click the green "play" arrow to run the `main` method.
    * The server will start on `http://localhost:8080`.


3.  **(Optional) View the Live Database:**
    * While the application is running, open your browser and navigate to **`http://localhost:8080/h2-console`**.
    * **JDBC URL:** Look in your IntelliJ console for the H2 JDBC URL (it will look like `jdbc:h2:mem:some-unique-id`).
    * Paste that URL into the "JDBC URL" field on the login page and click "Connect."
    * You can now see the `TASK` table and run SQL queries live!

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