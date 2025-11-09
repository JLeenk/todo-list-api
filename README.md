# Todo List API

A simple **Java Spring Boot** backend API for managing a Todo list.  
Supports **CRUD operations** via REST API and stores data in an **SQLite database**.

---

## Features

- RESTful API for Todo resource:
  - `GET /todos` – retrieve all todos
  - `GET /todos/{id}` – retrieve a single todo
  - `POST /todos` – create a new todo
  - `PATCH /todos/{id}` – update a todo
  - `DELETE /todos/{id}` – delete a todo
- SQLite persistence using JDBC
- Proper HTTP status codes (`200`, `201`, `204`, `404`, `500`)
- Exception handling with meaningful error responses
- Unit and integration tests included

---

## Requirements

- Java 21
- Maven
- SQLite (database file stored locally)
- Optional: Docker

---

## Setup

1. Clone the repository:

```bash
git clone https://github.com/JLeenk/todo-list-api.git
cd todo-list-api
```

2. Build the project:

```bash
./mvnw clean install
```

3. Run the application:

```bash
./mvnw spring-boot:run
```

The API will be available at: http://localhost:8080/todos

---

## API Endpoints

1. Get all Todos

```bash
curl -X GET http://localhost:8080/todos
```

2. Get Todo by ID

```bash
curl -X GET http://localhost:8080/todos/1
```

3. Create a new Todo

```bash
curl -X POST http://localhost:8080/todos \
  -H "Content-Type: application/json" \
  -d '{"description": "New task", "completed": false}'
```

4. Update a Todo

```bash
curl -X PATCH http://localhost:8080/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"description": "Updated task", "completed": true}'
```

5. Delete a Todo

```bash
curl -X DELETE http://localhost:8080/todos/1
```

---

## Testing

- All Tests

```bash
./mvnw test
```

- Unit Tests

```bash
./mvnw test -Dtest=TodoControllerTest,TodoRepositoryTest,TodoServiceTest
```

- Integration Tests

Integration tests use the SQLite database.
Database is cleaned before each test to ensure isolation.

```bash
./mvnw test -Dtest=TodoApiIntegrationTest
```

---

## Docker

- Build and Run

```bash
docker build -t todo-list-api .
docker run -p 8080:8080 todo-list-api
```

The API will be available at http://localhost:8080/todos

- Persist SQLite Data

Mount a volume to keep data outside the container:

```bash
docker run -p 8080:8080 -v $(pwd)/data:/app/data todo-list-api
```

---

## Stopping and Cleaning Docker

1. List running containers:

```bash
docker ps
```

2. Stop a container:

```bash
docker stop <container_id_or_name>
```

3. Remove a stopped container:

```bash
docker rm <container_id_or_name>
```

4. List Docker images:

```bash
docker images
```

5. Remove an image:

```bash
docker rmi -f <image_id_or_name>
```

