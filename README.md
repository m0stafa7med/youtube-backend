# 🔧 YouTube Clone — Backend

Spring Boot REST API for the YouTube Clone platform. Handles video management, user authentication, comments, subscriptions, playlists, notifications, and file storage.

## 🛠️ Tech Stack

- **Java 17** + **Spring Boot 3.2**
- **Spring Security** — OAuth 2.0 Resource Server with JWT
- **Spring Data MongoDB** — Database layer
- **Auth0** — Identity provider
- **Local File Storage** — Videos & thumbnails stored on server filesystem

## 📁 Project Structure

```
src/main/java/com/mostafa/youtubeclone/
├── config/
│   ├── SecurityConfig.java        # JWT auth + public endpoints
│   ├── AudienceValidator.java     # Auth0 audience validation
│   └── WebMvcConfig.java          # CORS configuration
├── controller/
│   ├── VideoController.java       # Video CRUD + upload endpoints
│   ├── UserController.java        # User registration & profile
│   ├── FileController.java        # Serve uploaded files (public)
│   ├── NotificationController.java
│   ├── PlaylistController.java
│   └── ReportController.java
├── service/
│   ├── VideoService.java          # Video business logic
│   ├── UserService.java           # User management
│   ├── FileStorageService.java    # Local file upload/delete/validate
│   ├── NotificationService.java
│   ├── PlaylistService.java
│   ├── ReportService.java
│   └── UserRegistrationService.java
├── model/
│   ├── Video.java                 # Video entity with likes, comments, views
│   ├── User.java                  # User entity with subscriptions
│   ├── Comment.java               # Comment with nested replies
│   ├── Notification.java
│   ├── Playlist.java
│   └── Report.java
├── dto/                           # Data transfer objects
├── repository/                    # MongoDB repositories
└── YoutubeCloneApplication.java
```

## 🔌 API Endpoints

### Videos
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/videos` | ❌ | List all videos (paginated) |
| GET | `/api/videos/{id}` | ❌ | Get video details |
| POST | `/api/videos` | ✅ | Upload a video |
| PUT | `/api/videos` | ✅ | Edit video metadata |
| DELETE | `/api/videos/{id}` | ✅ | Delete a video |
| POST | `/api/videos/{id}/like` | ✅ | Like a video |
| POST | `/api/videos/{id}/dislike` | ✅ | Dislike a video |
| POST | `/api/videos/{id}/comment` | ✅ | Add a comment |
| GET | `/api/videos/{id}/comment` | ❌ | Get all comments |
| POST | `/api/videos/thumbnail` | ✅ | Upload thumbnail |
| GET | `/api/videos/search` | ❌ | Search videos |
| GET | `/api/videos/trending` | ❌ | Get trending videos |

### Users
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/user/register` | ✅ | Register/get current user |
| GET | `/api/user/{userId}` | ✅ | Get user profile |
| POST | `/api/user/subscribe/{userId}` | ✅ | Subscribe to user |
| POST | `/api/user/unsubscribe/{userId}` | ✅ | Unsubscribe |

### Files (Public)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/files/videos/{filename}` | ❌ | Stream a video file |
| GET | `/api/files/thumbnails/{filename}` | ❌ | Get a thumbnail image |

## ⚙️ Configuration

All config is in `application.properties` with environment variable overrides:

| Variable | Default | Description |
|----------|---------|-------------|
| `MONGO_HOST` | `localhost` | MongoDB host |
| `MONGO_PORT` | `27017` | MongoDB port |
| `MONGO_DB` | `youtube-clone` | Database name |
| `UPLOAD_STORAGE_DIR` | `/uploads` | File storage directory |
| `UPLOAD_BASE_URL` | `http://localhost:8080` | Base URL for file URLs |
| `AUTH0_ISSUER_URI` | — | Auth0 issuer URL |
| `AUTH0_AUDIENCE` | — | Auth0 API audience |
| `AUTH0_USERINFO_ENDPOINT` | — | Auth0 userinfo URL |

### Upload Limits
- **Video max size**: 50 MB
- **Thumbnail max size**: 5 MB
- **Max videos per user**: 50
- **Allowed video types**: mp4, webm, mov, avi, mkv
- **Allowed image types**: jpeg, png, webp, gif

## 🐳 Docker

Multi-stage build: Maven builds the JAR, then runs on Eclipse Temurin 17 JRE Alpine.

## 🏃 Running Locally

```bash
# Requires Java 17 + Maven + MongoDB running on localhost:27017
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api/`
