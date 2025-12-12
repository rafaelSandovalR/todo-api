[![Docker Hub Pulls](https://img.shields.io/docker/pulls/rsandoval0408/todo-api?style=flat-square)](https://hub.docker.com/r/rsandoval0408/todo-api)

# Full-Stack Secure To-Do App

This is a complete, full-stack application featuring a secure REST API backend, a persistent database, and a responsive frontend UI, **fully deployed and accessible on the cloud** It is built with professional DevOps standards, including containerization and a fully automated CI/CD pipeline.

The application enforces **Multi-Tenancy**, ensuring that every user has their own private list of tasks.

## Key Features
* **Secure Authentication:** User registration and login protected by Spring Security and JWT (JSON Web Tokens).
* **Data Ownership:** Users can only view, update, and delete *their own* tasks. Access control is enforced at the controller level.
* **Full-Stack Integration:** Includes a lightweight HTML/JS frontend served directly by the backend.
* **CI/CD Pipeline:** Automated testing, building, and Docker image publishing via Jenkins.
* **Cloud Native:** Deployed on modern cloud infrastructures with a serverless database.

## Technologies & Skills

* **Backend:** Spring Boot, Java 17+, Maven
* **Frontend:** HTML5, CSS3, Vanilla JavaScript (Fetch API)
* **Database:** PostgreSQL (Persistent Docker Volume)
* **Testing** JUnit 5, Mockito, MockMvc (Unit & Integration Tests)
* **Security** Spring Security, JWT, BCrypt, Stateless Session Management
* **DevOps:** Docker, Docker Compose, Jenkins (Pipeline as Code)
* **Cloud & Infrastructure:** Render (PaaS), Neon (Serverless PostgreSQL) 

## How to Run

### Option 1: Live Demo

The application is deployed on the cloud and fully functional!
**[View Live App](https://my-secure-todo-app.onrender.com)**
*(Note: Hosted on Render Free Tier. It may take ~1 minute to wake up on the first load.)*

---

### Option 2: Quick Start (Docker Compose)

The fastest way to run the full stack locally (Database + API) without installing Java. You only need [Docker Desktop](https://www.docker.com/products/docker-desktop/).

1. Create a folder and create a file named **`docker-compose.yml`**.
2. Paste the following content into it:

    ```yaml
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

3. Open your terminal in that folder and run:
    ```bash
    docker-compose up
    ```

4. Open your browser: Go to  `http://localhost:8080` to use the App!

---

### Option 3: Developer Setup (Run from Source)

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
    * Access the UI at `http://localhost:8080`

## API Endpoints
While the UI handles everything for you, you can still interact with the API directly using [Postman](https://www.postman.com/).

**IMPORTANT:** All `/api/tasks` endpoints are secured. You **must** include a valid JWT in the `Authorization` header of your request:
`Authorization: Bearer <your_token>`

### Authentication

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/api/auth/register` | Create a new account. Body: `{ "username": "...", "password": "..." }` |
| **POST** | `/api/auth/login` | Authenticate and receive a JWT. Body: `{ "username": "...", "password": "..." }` |

### Task Management (Secured)

All actions below apply *only* to the logged-in user's data.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/tasks` | Get all your tasks. |
| **POST** | `/api/tasks` | Create a task. Body: `{ "description": "..." }` |
| **GET** | `/api/tasks/{id}` | Get one of your tasks by ID. |
| **PUT** | `/api/tasks/{id}` | Update a task. Body: `{ "description": "...", "completed": true }` |
| **DELETE** | `/api/tasks/{id}` | Delete a task. |
| **GET** | `/api/tasks/search` | Filter by status. Example: `?completed=true` |