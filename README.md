# Running the Application

This guide explains how to run the application locally using a containerized database with either **Docker Compose** or **Podman Compose**. 

It includes instructions for **Linux**, **macOS**, and **Windows**.

---

## Prerequisites

- **Docker** or **Podman** installed on your system.  
- **Java 25** installed.  

---

## Starting the Application

### 1. Start the Database

You need to start the database containers in detached mode:

- **Using Docker Compose**
  ```bash
  docker compose up -d
  ```
 
- **Using Podman Compose**
  ```bash
  docker compose up -d
  ```

### 2. Start the Spring Boot Application

Run the Spring Boot application using the Maven Wrapper:

- **Linux / macOS**
  ```bash
  ./mvnw spring-boot:run
  ```
  
- **Windows (PowerShell or CMD)**
  ```bash
  mvnw spring-boot:run
  ```

## Stopping the Application

### 1. Stop the Spring Boot App

Press ``CTRL + C`` in the terminal where the application is running.
This will gracefully terminate the Spring Boot process.

### 2. Stop the Database

Once you’re done, stop and remove the database containers:

- **Using Docker Compose**
  ```bash
  docker compose down -v
  ```

- **Using Podman Compose**
  ```bash
  podman compose down -v
  ```
  
The ``-v`` option removes the associated volumes (for example, database data), ensuring a clean state for the next run.

## 📚 API Endpoints

### 🔐 POST `/api/login`
Authenticates a user and returns a JWT token.  
_No DTO required — uses Basic Auth (username & password)._

---

### POST `/api/contributors`
Registers a new contributor.

#### Request Body
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "username": "johndoe",
  "password": "secret123"
}
```

### POST /api/members

Registers a new member.

#### Request Body
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  "username": "janesmith",
  "password": "secure123"
}
```

### POST /api/games

Adds a new game.
Consumes: multipart/form-data

### Request Body Example
```
title=Dark Souls
description=A challenging RPG
genres=RPG,ADVENTURE
publisher=EPIC_GAMES
platforms=PC,PLAYSTATION
contributorUsername=johndoe
coverImage=@/path/to/image.jpg
```

### GET /api/games

Retrieves paginated and optionally filtered list of games.

Query Parameters Example

/api/games?page=0&size=12&title=Zelda&genres=ADVENTURE&publisher=NINTENDO&platforms=SWITCH

### GET /api/images/{filename}

Retrieves a stored cover image.

Path Variable Example

/api/images/dark_souls.jpg



