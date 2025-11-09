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
    ```bash
  docker compose run --rm dynamodb-setup
  ```
 
- **Using Podman Compose**
  ```bash
  podman compose up -d
  ```
    ```bash
  podman compose run --rm dynamodb-setup
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
This endpoint is only allowed to `{ADMIN, CONTRIBUTOR}` users.

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

### POST `/api/members`

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

### POST `/api/games`

Adds a new game.
Consumes: multipart/form-data
This endpoint is only allowed to `{ADMIN, CONTRIBUTOR}` users.

### Request Body Example
```
title=Dark Souls
description=A challenging RPG
genres=RPG,MMORPG
publisher=electronic arts
platforms=Nintendo Switch,Nintendo Wii U
coverImage=image.jpg
```

### PUT `/api/games`

Update an existing game.
Consumes: multipart/form-data
This endpoint is only allowed to `{ADMIN, CONTRIBUTOR}` users.

### Request Body Example
```
id=5
title=Dark Souls
description=A challenging RPG
genres=RPG,MMORPG
publisher=electronic arts
platforms=Nintendo Switch,Nintendo Wii U
coverImagePath=934e35e5-ca4a-4120-850b-cab212102816.jpg
coverImage=image.jpg
```

### GET `/api/games`

Retrieves paginated and optionally filtered list of games.

Query Parameters Example

```/api/games?page=0&size=12&title=Zelda&genres=ADVENTURE&publisher=NINTENDO&platforms=SWITCH```

### DELETE `/api/games/{id}`

Delete an existing game.
This endpoint is only allowed to `{ADMIN, CONTRIBUTOR}` users.

### GET `/api/images/{filename}`

Retrieves a stored cover image.

Path Variable Example

```/api/images/dark_souls.jpg```

### GET `/api/genres`

Retrieves all genres.

### GET `/api/platforms`

Retrieves all platforms.

### GET `/api/publishers`

Retrieves all publishers.

### POST `/api/reviews`

Add a new review.

#### Request Body
```json
{
    "comment" : "Very nice",
    "rate" : 3,
    "gameId" : 3
}
```

### GET `/api/reviews/{gameId}`

Retrieves all reviews by the game id.

Query Parameters Example to fetch all the reviews of the game with id 3

```/api/reviews/3```

### DELETE `/api/reviews`

Delete an existing review.
This endpoint is only allowed to `{ADMIN, CONTRIBUTOR}` users.

#### Request Body
```json
{
    "reviewId" : "e559d78b-667e-4c68-a1da-faea54632280",
    "timeStamp" : "2025-11-08T21:07:32.345124446Z"
}
```

### GET `/api/users`

Retrieves all users.

This endpoint is only allowed to `{ADMIN}` users.

