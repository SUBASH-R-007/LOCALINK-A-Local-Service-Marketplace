# Localink: Local Service Marketplace (JavaFX + PostgreSQL)

## What is Localink?
Localink connects people with trusted local service providers (e.g., electricians, plumbers, tutors) in one place. Customers can explore categories, view services, book appointments, and get a confirmation. Providers can list services; the app is built to extend toward provider/admin dashboards.

## Highlights
- **Modern UI** with JavaFX and FXML
- **PostgreSQL** database via **HikariCP** connection pooling
- **Authentication** with BCrypt (Register/Login)
- **End-to-end flow**: Categories → Services → Booking → Confirmation
- **Seed data**: 20+ services across diverse categories for instant demo

## Architecture (3-tier)
- **Presentation Layer**: JavaFX views in `src/main/resources/fxml/` and controllers in `src/main/java/com/localink/controller/`
- **Business Logic Layer**: Services in `src/main/java/com/localink/service/`
- **Data Access Layer**: DAOs using JDBC in `src/main/java/com/localink/dao/`

## Requirements
- Java 17+
- PostgreSQL 14+
- Maven 3.8+ (or use your IDE’s Maven integration)

> Tip: If Maven is not installed system-wide, use your IDE to run the Maven goal `javafx:run`. If you want, we can add a Maven Wrapper so you can run `mvnw` without installing Maven.

## Quick Start (Windows)

1) Create the database (skip if it already exists):
```powershell
psql -U postgres -c "CREATE DATABASE localink;"
```

2) Apply schema + seed data using the full path to this project (adjust path if different):
```powershell
psql -U postgres -d localink -f "C:\'path_to_your_project_directory'\db\schema.sql"
```

3) Configure DB credentials in `src/main/resources/application.properties`:
```
db.url=jdbc:postgresql://localhost:5432/localink
db.username="Your DB username"
db.password="Your DB password"
```

4) Run the app
- Using IDE: open the project and run the Maven goal `javafx:run`.
- Using terminal (requires Maven installed on PATH):
```bash
mvn clean javafx:run
```

## Run in IntelliJ IDEA (Windows)
- **Import project**
  - File → Open → select the project folder `java - dbms project/` → Open as Project.
  - IntelliJ detects Maven (`pom.xml`) and indexes dependencies.
- **Set Project SDK**
  - File → Project Structure → Project → Project SDK: select Java 17.
  - Language level: 17.
- **Configure DB first** (one-time)
  - Run the commands in Quick Start to create DB and apply `db/schema.sql`.
  - Ensure `src/main/resources/application.properties` matches your DB.
- **Method A (recommended): Maven Tool Window**
  - View → Tool Windows → Maven.
  - Expand `Plugins` → `javafx` → double-click `javafx:run`.
  - This uses the JavaFX Maven plugin to start the app.
- **Method B: Application Run Configuration** (if you prefer Run ▶)
  - Run → Edit Configurations… → + → Application.
  - Name: Localink.
  - Main class: `com.localink.Main`.
  - Use classpath of module: select the main module (IntelliJ shows it after Maven import).
  - Working directory: project root.
  - No special VM options needed when using Maven dependencies; if you face JavaFX module errors, switch to Method A.
  - Apply → OK → Run.
- **Tip**: Settings → Build, Execution, Deployment → Build Tools → Maven → check “Delegate IDE build/run actions to Maven” to ensure consistent classpaths.

## Using the App
- **Login**: Use “Register” to create a user, then login.
- **Browse**: After login, you land on categories. Pick a category → choose a service.
- **Book**: Select date/time and confirm booking.
- **Confirm**: See booking ID and details on the confirmation screen.

## Packaging for Distribution (runtime image)
Create a self-contained runtime image (includes a minimal JRE + app):
```bash
mvn clean javafx:jlink
```
Output: `target/localink.zip`
- Extract and run on Windows:
```
target/localink/bin/localink.bat
```

> For a Windows installer (`.msi/.exe`) we can add `jpackage`. Ask if you want this set up.

## Project Structure
```
java - dbms project/
├─ pom.xml
├─ db/
│  └─ schema.sql                 # PostgreSQL schema & seed data
├─ src/
│  ├─ main/java/com/localink/
│  │  ├─ App.java                # JavaFX entry
│  │  ├─ Main.java               # Main launcher
│  │  ├─ config/Database.java    # HikariCP + PostgreSQL config
│  │  ├─ controller/             # JavaFX controllers
│  │  ├─ dao/                    # JDBC DAOs
│  │  ├─ model/                  # POJOs (User, Service, Booking)
│  │  ├─ service/                # AuthService, etc.
│  │  └─ util/                   # ViewNavigator, Session
│  └─ main/resources/
│     ├─ fxml/                   # Views: login, register, categories, services_list, booking, confirmation
│     ├─ styles/app.css
│     └─ application.properties  # DB configuration
└─ README.md
```

## Troubleshooting
- **Maven not recognized**: Use your IDE’s Maven tool window, or install Maven, or request a Maven Wrapper.
- **Schema file not found**: Use the absolute path to `db/schema.sql` when running `psql`, or run the command from the project root.
- **Auth failed for user**: Check `application.properties` credentials; ensure the PostgreSQL service is running; verify with:
  ```powershell
  psql "postgresql://postgres:postgres@localhost:5432/localink" -c "\\dt"
  ```
- **FXML padding error (Insets)**: Always use:
  ```xml
  <padding>
    <Insets top="12" right="12" bottom="12" left="12" />
  </padding>
  ```
  instead of `padding="12"`.

## Roadmap
- Provider dashboard (accept/complete jobs)
- Admin panel (user/service moderation)
- Reviews & ratings UI
- Payment integration (stub → provider)

## License
MIT License. See `LICENSE` for full text.
