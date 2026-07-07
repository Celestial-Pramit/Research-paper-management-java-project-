# Research Paper Management System

Java 21 + JavaFX 21 desktop app (Maven project, NO `mvn` CLI on PATH).  
Database: MySQL 8.0 on localhost:3306 (`root` / `12345!!`).  
GitHub: https://github.com/Celestial-Pramit/Research-paper-management-java-project-.git

## Database Setup (First Time)

Before building the app, set up the MySQL database:

```powershell
mysql -u root -p12345!! < database.sql
```
> Change `12345!!` to your MySQL root password if different.

This creates the `research_papers` database with all tables and seed data.

## Seeded Users

After running `database.sql`, these users exist:

| Username | Email | Password | Role |
|----------|-------|----------|------|
| `doeuser` | `111` | `111` | USER |
| `aaa` | `aaa@gmail.com` | `111` | ADMIN |

Login accepts **username OR email** in the username field.

## Build & Run

### 0. Download Dependencies (First Time Only)

On a new PC, download JavaFX 21 and MySQL Connector JARs into the local Maven repository:

```powershell
# Option A — Using Maven (recommended, requires Maven installed):
mvn dependency:copy-dependencies -DoutputDirectory="$env:USERPROFILE\.m2\repository"

# Option B — Manual download URLs (Java 21, Windows x64):
#   javafx-base:     https://repo1.maven.org/maven2/org/openjfx/javafx-base/21/javafx-base-21-win.jar
#   javafx-controls: https://repo1.maven.org/maven2/org/openjfx/javafx-controls/21/javafx-controls-21-win.jar
#   javafx-fxml:     https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/21/javafx-fxml-21-win.jar
#   javafx-graphics: https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/21/javafx-graphics-21-win.jar
#   mysql-connector: https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar
# Save them to: $env:USERPROFILE\.m2\repository\org\openjfx\... and ...\com\mysql\...
```

### 1. Compile

```powershell
Remove-Item -Path target\classes -Recurse -Force -ErrorAction SilentlyContinue
$fxBase = "$env:USERPROFILE\.m2\repository\org\openjfx"
$mysql = "$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\8.2.0\mysql-connector-j-8.2.0.jar"
$mp = "$fxBase\javafx-base\21\javafx-base-21-win.jar;$fxBase\javafx-controls\21\javafx-controls-21-win.jar;$fxBase\javafx-fxml\21\javafx-fxml-21-win.jar;$fxBase\javafx-graphics\21\javafx-graphics-21-win.jar;$mysql"
javac -d target\classes --module-path "$mp" --add-modules javafx.controls,javafx.fxml -sourcepath src\main\java (Get-ChildItem src\main\java -Recurse -Filter *.java | % { $_.FullName })
```

### 2. Copy Resources

```powershell
Copy-Item src\main\resources\* target\classes -Recurse -Force
```

### 3. Run

```powershell
java --module-path "$mp;target\classes" --add-modules javafx.controls,javafx.fxml -m com.researchpapers/com.researchpapers.Main
```

## Architecture

- **Entry**: `com.researchpapers.Main` — 1280×720 scene, min 900×620
- **Navigation**: `Main.changeScene("name")` loads `view/name.fxml` from classpath. `Main.setActiveNav(active, buttons...)` applies `nav-active` CSS class.
- **Layout**: AnchorPane — sidebar 185px left, topbar 56px top (left-offset 185px), content fills rest
- **CSS**: `style.css` in `src/main/resources/com/researchpapers/css/`. `dark-mode.css` exists (partial implementation).
- **Package note**: util classes live under `com.researchpapers.utill` (double `l`), not `util`.
- **Module**: `module-info.java` requires `javafx.controls`, `javafx.fxml`, `java.sql`, `java.desktop`, `mysql.connector.j`.
- **Controllers** open to `javafx.fxml` for FXML injection.
- **DAOs** use raw JDBC via `ConnectionSingleton` (throws RuntimeException on connection failure, no connection pool).
- **Session**: `SessionManager` stores `loggedUser`, `currentUserId`, `loggedUserFullName`, `loggedUserRole`. `SessionManager.clear()` on logout.

## Screens

| FXML | Controller | Description |
|------|-----------|-------------|
| `login.fxml` | LoginController | Login by username or email. Dark mode toggle on login/register screens. |
| `register.fxml` | RegisterController | Registration with full validation. |
| `dashboard.fxml` | DashboardController | Stats cards + recent papers table (with View button) + bar chart (papers/year) + pie chart (reading status). Add Paper button uses `PaperDialogService`. |
| `paper_library.fxml` | PaperLibraryController | Search & filter (text, status, category, year), sortable table, in-memory pagination (10/page), color-coded status badges & category tags, star ratings, CSV export via `ExportService`. |
| `paper_details.fxml` | PaperDetailsController | Full paper metadata, notes CRUD, reading progress tracking, Open PDF (`java.awt.Desktop`), IEEE citation export via `CitationGenerator`. |
| `collection_manager.fxml` | CollectionController | Create/delete named collections, add/remove papers via two side-by-side tables (available ↔ collection). |
| `admin_dashboard.fxml` | AdminDashboardController | System-wide stats, user management table with Activate/Deactivate buttons (non-admin only). |

## Shared Components

- **`topbar.fxml`** + **`TopbarController`** — Unified topbar included via `<fx:include>` in all dashboard screens. Shows avatar initials, user full name, role label, notification bell (coming soon), role dropdown (coming soon).
- **`PaperDialogService.showPaperDialog()`** — Shared add/edit paper dialog with all fields + validation. Used by both Dashboard and Paper Library.
- **`ExportService.exportToCsv()`** — Writes filtered paper list to CSV via FileChooser.
- **`CitationGenerator.generateIeee()`** — Produces IEEE-formatted citation strings.
- **`AlertUtil.showAlert()`** — Static convenience for JavaFX `Alert`.

## Data Model

| Table | Key columns |
|-------|-------------|
| `users` | id, username, full_name, email, password, role (USER/ADMIN), status (Active/Inactive) |
| `papers` | id, title, authors, abstract_text, publication_venue, publication_year, doi, file_path, category, rating, user_id |
| `notes` | id, paper_id, user_id, content, created_at, updated_at |
| `collections` | id, name, description, user_id |
| `collection_papers` | collection_id, paper_id |
| `reading_progress` | id, paper_id, user_id, status, current_page, total_pages, last_read_at |

## Conventions

- All nav buttons use `nav-item` + `nav-active` for current page sidebar highlight.
- `ConnectionSingleton.getConnection()` throws RuntimeException on failure — DAOs never get null.
- In-memory filtering/pagination in `PaperLibraryController` (small dataset assumption).
- FXML fields are `public` (JavaFX FXMLLoader limitation).
- Action buttons in tables use `action-btn action-view-btn` / `action-edit-btn` / `action-delete-btn` CSS classes.
- No database logic inside controllers — all DB access through DAOs.
- Prepared Statements only — no SQL injection.
