# Research Paper Management System

Java 21 + JavaFX 21 desktop app (Maven project, NO mvn CLI on PATH).  
Database: MySQL 8.0 on localhost:3306 (root / 12345!!).

## Build & Run

```powershell
# 1. Clean compile (module-path from local .m2)
Remove-Item -Path target\classes -Recurse -Force -ErrorAction SilentlyContinue
$fxBase = "$env:USERPROFILE\.m2\repository\org\openjfx"
$mysql = "$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\8.2.0\mysql-connector-j-8.2.0.jar"
$mp = "$fxBase\javafx-base\21\javafx-base-21-win.jar;$fxBase\javafx-controls\21\javafx-controls-21-win.jar;$fxBase\javafx-fxml\21\javafx-fxml-21-win.jar;$fxBase\javafx-graphics\21\javafx-graphics-21-win.jar;$mysql"
javac -d target\classes --module-path "$mp" --add-modules javafx.controls,javafx.fxml -sourcepath src\main\java (Get-ChildItem src\main\java -Recurse -Filter *.java | % { $_.FullName })

# 2. Copy resources
Copy-Item src\main\resources\* target\classes -Recurse -Force

# 3. Run
java --module-path "$mp;target\classes" --add-modules javafx.controls,javafx.fxml -m com.researchpapers/com.researchpapers.Main
```

## Key Architecture

- **Entry**: `com.researchpapers.Main` — fixed 1280×720 scene, min 900×620
- **Navigation**: `Main.changeScene("name")` loads `view/name.fxml` from classpath
- **Layout**: AnchorPane — sidebar 185px left, topbar 56px top (left-offset 185px), content fills rest
- **CSS**: `style.css` in `src/main/resources/com/researchpapers/css/` (one file for all screens)
- **Module**: `module-info.java` requires `javafx.controls`, `javafx.fxml`, `java.sql`, `mysql.connector.j`
- **Controllers** open to `javafx.fxml` for FXML injection

## Screens

| FXML | Controller | Description |
|------|-----------|-------------|
| `login.fxml` | LoginController | Logs in via UserDAO against MySQL; 111/111 works |
| `dashboard.fxml` | DashboardController | Stats + recent papers + charts from DB |
| `paper_library.fxml` | PaperLibraryController | Search/filter + table + pagination; 👁 opens details |
| `collection_manager.fxml` | CollectionController | Create/manage collections (DB-backed) |
| `paper_details.fxml` | PaperDetailsController | Paper info, notes CRUD, reading progress |

## Conventions

- All nav buttons use `nav-item` + `nav-active` for current page
- `SessionManager.loggedUser` / `SessionManager.currentUserId` stores login session
- DAOs use raw JDBC via `ConnectionSingleton` (MySQL, no connection pool)
- Login requires valid DB credentials; 111/111 is a seeded user
- DB schema: `research_papers` with tables `users`, `papers`, `notes`, `collections`, `collection_papers`, `reading_progress`
