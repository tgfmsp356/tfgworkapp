# TechServe

A marketplace Android app where users can offer and discover tech services. Built with Java, Firebase, and an AI-powered support chatbot.

## Features

- **Authentication** — Email/password and Google Sign-In via Firebase Auth
- **Service listings** — Create, browse, and filter service announcements by category
- **User profiles** — Profile pictures, descriptions, and account details
- **AI chatbot** — Support assistant that recommends services based on user queries
- **Image uploads** — Cloudinary integration for profile and listing images

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Build system | Gradle 8.13 (Kotlin DSL) |
| Min / Target SDK | API 24 / API 36 |
| UI | AndroidX, Material Design |
| Backend | Firebase (Auth, Firestore, Storage) |
| Images | Cloudinary, Glide |
| Networking | OkHttp, Gson |
| AI backend | REST API (FastAPI, `http://10.0.2.2:8000`) |

## Project Structure

```
app/src/main/java/com/example/tfg_app/
├── Activities/        # MainActivity, LoginActivity, RegisterActivity, NavActivity
├── Fragments/         # HomeFragment, ServicesFragment, CreateFragment, ProfileFragment, SupportFragment
├── POJOS/             # Data models (Anuncio, Usuario, Categoria, Valoracion…)
├── adapters/          # RecyclerView adapters
├── ChatBot/           # AI support chat (SupportFragment, AgentApiClient)
├── database/          # FirestoreHelper abstraction layer
└── Cloudinary*.java   # Image upload helpers
```

## Getting Started

### Prerequisites

- Android Studio (latest recommended)
- Android SDK with API 24–36
- Java 11+
- A running instance of the AI backend (see below)

### Setup

1. **Clone the repo**
   ```bash
   git clone <repository-url>
   cd tfgworkapp
   ```

2. **Open in Android Studio** and let Gradle sync.

3. **Firebase** — `google-services.json` is already included in `app/`. No additional setup needed.

4. **Cloudinary** — credentials are initialized in `CloudinaryIni.java`. Update that file if you use your own account.

5. **AI backend** — the chatbot calls `http://10.0.2.2:8000` (Android emulator alias for `localhost`). If running on a physical device, update the base URL in `AgentApiClient.java` to your machine's local IP.

### Build & Run

```bash
# Build
./gradlew build

# Install on connected device / emulator
./gradlew installDebug
```

Or use the **Run** button in Android Studio.

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires a connected device or emulator)
./gradlew connectedAndroidTest
```

## Screens

| Screen | Description |
|---|---|
| Home | Grid of service listings with personalized greeting |
| Services | Browse and filter by category |
| Create | Publish a new service (title, price, category, image) |
| Support | AI chatbot with service recommendations |
| Profile | View and edit user profile |

## Permissions

- `INTERNET` — required for Firebase, Cloudinary, and the AI backend
- `READ_MEDIA_IMAGES` — required for selecting profile and listing photos
