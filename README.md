# Cloud Smart Watch Party

A beginner-friendly monolithic Spring Boot 3 application for creating shared watch rooms, synchronizing basic HTML5 playback, and chatting with room members.

## Technology

- Java 21 (LTS)
- Spring Boot 3.4
- Spring MVC and Thymeleaf
- Spring Security with BCrypt and server-side sessions
- Spring Data JPA / Hibernate
- H2 for local development or MySQL for deployment
- Maven

## Run locally

Requirements: Java 21 and Maven 3.9 or newer.

```powershell
cd C:\path\to\cloud-smart-watch-party
mvn spring-boot:run
```

Open <http://localhost:8080>, create an account, create a room, and share the six-character room code. Use a second browser profile or incognito window to test two users.

The default database is a file-backed H2 database at `./data/watchparty`. To start clean, stop the application and remove that directory. It is ignored by Git.

## Features

- Registration with email, username, display name, password confirmation, validation, and BCrypt hashing.
- Login, logout, CSRF protection, and authentication-required pages/API requests.
- Room creation, unique room codes, joining, membership authorization, participant list, and leaving.
- Host-only room settings and playback-state updates.
- HTML5 video playback polling with position correction only when the difference is significant.
- Supported sample-video catalog at `GET /api/videos`.
- Room chat with persistent messages, a 500-character limit, safe browser rendering, and incremental `afterId` polling.
- MySQL-ready configuration through environment variables.

## MySQL configuration

Create the database and tables using [`database/schema.sql`](database/schema.sql), then set:

```powershell
$env:DB_URL = "jdbc:mysql://host:3306/cloud_watch_party?useSSL=true&serverTimezone=UTC"
$env:DB_USERNAME = "your_user"
$env:DB_PASSWORD = "your_password"
mvn spring-boot:run
```

The application configuration is in [`src/main/resources/application.properties`](src/main/resources/application.properties). Never commit production credentials.

## API summary

All room APIs require an authenticated session and room membership:

- `GET /api/videos`
- `GET /api/rooms/{code}/members`
- `GET /api/rooms/{code}/playback`
- `POST /api/rooms/{code}/playback` — host only
- `GET /api/rooms/{code}/chat?afterId=0`
- `POST /api/rooms/{code}/chat`

The browser pages use normal CSRF-protected forms for registration, login, room creation, joining, settings, leaving, and logout.

## Test and package

```powershell
mvn clean test package
java -jar target\cloud-smart-watch-party-1.0.0.jar
```

The test suite verifies application context wiring and BCrypt password behavior. The application has been validated locally with Maven, Java 21, and the H2 profile.
