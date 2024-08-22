# <div align="center">ZYLYTY Web Forum App</div>

<div align="center">

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)

</div>

A highly-efficient, dynamic, and secure REST API backend for a web forum application. Built with Java and Spring Boot, it supports user registration and authentication, category management, thread creation, post management, and search functionality. The backend is fully dockerized, enabling rapid deployment and scalability in any environment.

[See the short presentation video on Loom.](https://www.loom.com/share/fcf8053248f24213bc22e9c921e5bf49)

## How to Run

There are multiple ways of running the project. You can:

1. **Run the database externally and execute the main method of DemoApplication by creating IntelliJ run configuration.**
2. **Run the database externally and running the app with `docker run`.**
3. **[EASIEST] Just run the docker compose from the root of the project.**

```sh
docker compose up -d
```


## API Documentation

### User Registration
- **URL**: `http://localhost:8080/user/register`
- **Method**: `POST`
- **Request**:
    ```json
    {
        "username": "newUser123",
        "password": "userPassword123",
        "email": "newuser123@example.com"
    }
    ```
- **Response**:
  - `201 Created`: Registration successful.
  - `400 Bad Request`: Invalid data.
  - `418 I'm a teapot`: Email or username already registered.

### User Login
- **URL**: `http://localhost:8080/user/login`
- **Method**: `POST`
- **Request**:
    ```json
    {
        "username": "existingUser",
        "password": "userPassword123"
    }
    ```
- **Response**:
  - `200 OK`: Authentication successful.
  - `401 Unauthorized`: Invalid credentials.

### List Categories
- **URL**: `http://localhost:8080/categories`
- **Method**: `GET`
- **Headers**: `Cookie: session=<sessionCookie>`
- **Response**:
  - `200 OK`: List of categories.
  - `401 Unauthorized`: Invalid session cookie.

### Create Categories
- **URL**: `http://localhost:8080/categories`
- **Method**: `POST`
- **Headers**: `Token: <adminApiKey>`
- **Request**:
    ```json
    {
        "categories": ["Technology", "Health"]
    }
    ```
- **Response**:
  - `201 Created`: Categories created.
  - `400 Bad Request`: Invalid or duplicate categories.
  - `401 Unauthorized`: Invalid admin API key.

### Delete Category
- **URL**: `http://localhost:8080/categories?category=<categoryName>`
- **Method**: `DELETE`
- **Headers**: `Token: <adminApiKey>`
- **Response**:
  - `200 OK`: Category deleted.
  - `400 Bad Request`: Cannot delete "Default" category or category does not exist.
  - `401 Unauthorized`: Invalid admin API key.

### Create Thread
- **URL**: `http://localhost:8080/thread`
- **Method**: `POST`
- **Headers**: `Cookie: session=<sessionCookie>`
- **Request**:
    ```json
    {
        "category": "Default",
        "title": "Exciting Discussion",
        "openingPost": {
            "text": "Let's talk about something exciting!"
        }
    }
    ```
- **Response**:
  - `201 Created`: Thread created.
  - `401 Unauthorized`: Invalid session cookie.

### List Threads
- **URL**: `http://localhost:8080/thread`
- **Method**: `GET`
- **Headers**: `Cookie: session=<sessionCookie>`
- **Query Parameters**:
  - `categories`: List of categories to filter by.
  - `newest_first`: Boolean to sort by newest first.
  - `page`: Page number.
  - `page_size`: Number of threads per page.
- **Response**:
  - `200 OK`: List of threads.
  - `400 Bad Request`: Missing or invalid filter parameters.
  - `401 Unauthorized`: Invalid session cookie.

### Add Posts to Thread
- **URL**: `http://localhost:8080/thread/post`
- **Method**: `POST`
- **Headers**: `Cookie: session=<sessionCookie>`
- **Request**:
    ```json
    {
        "threadId": 1,
        "posts": [
            {
                "text": "I completely agree with this point!"
            },
            {
                "text": "Here's an interesting fact that might add to the discussion..."
            }
        ]
    }
    ```
- **Response**:
  - `201 Created`: Posts added.
  - `401 Unauthorized`: Invalid session cookie.

### Get Posts in Thread
- **URL**: `http://localhost:8080/thread/post`
- **Method**: `GET`
- **Headers**: `Cookie: session=<sessionCookie>`
- **Query Parameters**:
  - `thread_id`: ID of the thread.
- **Response**:
  - `200 OK`: List of posts.
  - `400 Bad Request`: Invalid thread ID.
  - `401 Unauthorized`: Invalid session cookie.

### Search Threads
- **URL**: `http://localhost:8080/search`
- **Method**: `GET`
- **Headers**: `Cookie: session=<sessionCookie>`
- **Query Parameters**:
  - `text`: Search query.
- **Response**:
  - `200 OK`: Search results.
  - `401 Unauthorized`: Invalid session cookie.

### Delete Thread
- **URL**: `http://localhost:8080/thread?id=<threadId>`
- **Method**: `DELETE`
- **Headers**: `Token: <adminApiKey>`
- **Response**:
  - `204 No Content`: Thread deleted.
  - `400 Bad Request`: Invalid thread ID.
  - `401 Unauthorized`: Invalid admin API key.

### Import Users via CSV
- **URL**: `http://localhost:8080/csv`
- **Method**: `POST`
- **Headers**: `Token: <adminApiKey>`
- **Request**:
    ```text
    username,password,email
    user1,password1,user1@example.com
    user2,password2,user2@example.com
    ```
- **Response**:
  - `201 Created`: Users imported.
  - `400 Bad Request`: Invalid CSV format or data.
  - `401 Unauthorized`: Invalid admin API key.
  - `200 OK`: Empty CSV import.
