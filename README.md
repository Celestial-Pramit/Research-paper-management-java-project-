# Research Paper Management System

A Java + JavaFX desktop application for managing academic research papers, built with MySQL.

## Why This System?

Researchers, students, and academics accumulate large collections of papers from conferences, journals, and preprints. Managing these manually leads to:

- **Scattered files** — PDFs across folders, no central search
- **Lost citations** — scrambling to rebuild bibliographies
- **No reading progress tracking** — forgetting where you left off in a paper
- **Disorganized notes** — insights buried across different tools

This system solves all of that in a single, offline desktop application.

## Features & What Each Function Does

### Authentication (`LoginController`, `RegisterController`)

| Function | What it does |
|---|---|
| `loginEvent()` | Validates username/email + password against MySQL. Routes regular users to Dashboard, admin users to Admin Dashboard. |
| `togglePasswordVisibility()` | Shows/hides password characters via an eye toggle. |
| `forgotPasswordEvent()` | Accepts a registered email and displays a simulated password-reset message. |
| `registerEvent()` | Accepts full name, username, email, password, confirm-password. Validates format, uniqueness of username/email, minimum length. On success inserts into DB and auto-redirects to login after 2 seconds. |
| `backToLoginEvent()` | Returns to login screen. |

### User Dashboard (`DashboardController`)

| Function | What it does |
|---|---|
| `loadStats()` | Queries total papers, currently reading count, completed count, and favorite count for the logged-in user. Displays as four stat cards. |
| `loadRecentPapers()` | Populates a table with all user's papers. Each row has a View button that navigates to Paper Details. |
| `loadCharts()` | Builds a bar chart (papers grouped by year) and a pie chart (reading-status distribution). |
| `addPaperEvent()` | Opens the shared add-paper dialog. On success, refreshes stats, recent table, and charts. |
| `refreshEvent()` | Re-queries and redraws all dashboard data. |
| Sidebar nav | `dashboardEvent()`, `paperLibraryEvent()`, `collectionsEvent()` navigate between screens. `logoutEvent()` clears the session and returns to login. |

### Paper Library (`PaperLibraryController`)

| Function | What it does |
|---|---|
| `loadPapers()` | Fetches all papers for the logged-in user from MySQL. |
| `applyFilters()` | Filters the full list by search text (title/author), reading status, category, and publication year. Updates the paper-count label. Resets to page 1. |
| `updatePagination()` | Displays the correct 10-paper page. Enables/disables Previous/Next buttons. Updates "Page X of Y" label. |
| `setupTable()` | Configures 7 columns: Title, Authors, Year (with column sorting), Status (color-coded badge), Category (color-coded tag), Rating (star display), Actions (View/Edit/Delete buttons). |
| `showPaperDialog(Paper)` | Opens the dialog for add (null inserts) or edit (populated updates). Shared via `PaperDialogService`. |
| `deletePaper(Paper)` | Shows confirmation alert. On confirm deletes from DB and refreshes the table. |
| `exportEvent()` | Opens a FileChooser for CSV save. Calls `ExportService.exportToCsv()` with currently filtered papers. |
| `clearFiltersEvent()` | Resets all dropdown filters to "All" and clears search text. |
| `previousPageEvent()` / `nextPageEvent()` | Move pagination backward/forward. |
| Sidebar nav | `dashboardEvent()`, `collectionsEvent()`, `logoutEvent()` for screen navigation. |

### Paper Details (`PaperDetailsController`)

| Function | What it does |
|---|---|
| `loadPaper()` | Loads full paper metadata (title, authors, year, venue, DOI, abstract, PDF path) from DB. Also loads existing reading progress if any. |
| `setupNotesTable()` | Configures notes table with content and date columns. |
| `loadNotes()` | Fetches all notes for this paper from MySQL. |
| `addNoteEvent()` | Saves the new note text to DB and prepends it to the notes list. |
| `deleteNoteEvent()` | Deletes the selected note from DB and removes it from the list. |
| `saveProgressEvent()` | Saves/updates reading status, current page number, and favorite flag in the `reading_progress` table. |
| `markCompletedEvent()` | One-click sets status to "Completed" and updates the dropdown. |
| `openPdfEvent()` | Opens the associated PDF file via the OS default application. Shows an error if the file is missing. |
| `exportCitationEvent()` | Generates an IEEE-format citation string and displays it in a read-only dialog. |
| `backToLibraryEvent()` | Returns to the Paper Library. |
| Sidebar nav | `dashboardEvent()`, `paperLibraryEvent()`, `collectionsEvent()`, `logoutEvent()` for screen navigation. |

### Collection Manager (`CollectionController`)

| Function | What it does |
|---|---|
| `loadCollections()` | Fetches all named collections for the user and populates the ListView. Updates the collection count. |
| `loadAllPapers()` | Fetches all user papers as the available pool. |
| `loadCollectionPapers(int)` | Loads papers belonging to the selected collection. Removes those papers from the available list. |
| `createCollectionEvent()` | Creates a new empty collection with the entered name and selects it automatically. |
| `deleteCollectionEvent()` | Deletes the selected collection and its paper mappings from DB. |
| `addToCollectionEvent()` | Adds the selected available paper to the current collection. |
| `removeFromCollectionEvent()` | Removes the selected paper from the current collection and moves it back to available. |
| `refreshCollectionsEvent()` | Re-queries all collections and papers from DB. |

### Admin Dashboard (`AdminDashboardController`)

| Function | What it does |
|---|---|
| `loadStats()` | Shows system-wide totals: users, papers, collections, active users. |
| `setupTable()` | Configures user table with ID, Username, Full Name, Email, Role, Status, and Actions. |
| `loadUsers()` | Fetches all users from DB ordered by ID. |
| `toggleUserStatus(User)` | Toggles a user between Active/Inactive. The Activate/Deactivate button only appears for non-admin users. Refreshes stats and table. |
| `refreshEvent()` | Re-queries stats and user list. |

### Shared Services

| Service / Function | What it does |
|---|---|
| `PaperDialogService.showPaperDialog()` | Reusable dialog with all paper fields (title, authors, abstract, venue, year, DOI, category, rating, PDF chooser). Validates required fields. Used by both Dashboard and Paper Library. |
| `ExportService.exportToCsv()` | Writes papers to a CSV file with columns: Title, Authors, Year, Category, Rating, DOI, Venue, Abstract. |
| `CitationGenerator.generateIeee()` | Formats a paper into IEEE citation format with proper author initial formatting. |
| `Main.changeScene(fxml)` | Loads a new FXML scene by name and replaces the current stage content. |
| `Main.setActiveNav(active, buttons)` | Highlights the current sidebar button with the `nav-active` CSS class. |

### Topbar (`TopbarController`)

| Function | What it does |
|---|---|
| `initialize()` | Sets the user's full name, role label, and avatar initials from `SessionManager`. |
| `notificationEvent()` | Placeholder showing a coming-soon alert. |
| `roleDropdownEvent()` | Placeholder showing a coming-soon alert. |
