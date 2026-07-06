package com.researchpapers.controller;

import com.researchpapers.Main;
import com.researchpapers.dao.NoteDAO;
import com.researchpapers.dao.PaperDAO;
import com.researchpapers.dao.ReadingProgressDAO;
import com.researchpapers.model.Note;
import com.researchpapers.model.Paper;
import com.researchpapers.model.ReadingProgress;
import com.researchpapers.service.CitationGenerator;
import com.researchpapers.utill.AlertUtil;
import com.researchpapers.utill.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.Desktop;
import java.io.File;

public class PaperDetailsController {

    @FXML public Label lblPaperId;
    @FXML public Label lblTitle;
    @FXML public Label lblAuthors;
    @FXML public Label lblConference;
    @FXML public Label lblJournal;
    @FXML public Label lblYear;
    @FXML public Label lblDoi;
    @FXML public Label lblKeywords;
    @FXML public Label lblPdfPath;
    @FXML public Label lblUploadDate;
    @FXML public TextArea txtAbstractDetails;
    @FXML public TableView<Note> tblNotes;
    @FXML public TableColumn<Note, String> colNoteText;
    @FXML public TableColumn<Note, String> colNotePage;
    @FXML public TableColumn<Note, String> colNoteDate;
    @FXML public TextArea txtNewNote;
    @FXML public TextField txtPageNumber;
    @FXML public Button btnAddNote;
    @FXML public Button btnDeleteNote;
    @FXML public ComboBox<String> cmbReadingStatus;
    @FXML public TextField txtLastPage;
    @FXML public ComboBox<String> cmbRating;
    @FXML public CheckBox chkFavorite;
    @FXML public Button btnSaveProgress;
    @FXML public Button btnMarkCompleted;
    @FXML public Button btnOpenPdf;
    @FXML public Button btnExportCitation;
    @FXML public Button btnBackToLibrary;
    @FXML public Button btnDashboard;
    @FXML public Button btnPaperLibrary;
    @FXML public Button btnCollections;

    private static int selectedPaperId = -1;
    private Paper currentPaper;
    private ReadingProgress currentProgress;

    private final PaperDAO paperDAO = new PaperDAO();
    private final NoteDAO noteDAO = new NoteDAO();
    private final ReadingProgressDAO progressDAO = new ReadingProgressDAO();
    private final ObservableList<Note> notesList = FXCollections.observableArrayList();

    public static void setSelectedPaperId(int id) { selectedPaperId = id; }

    @FXML
    public void initialize() {
        Main.setActiveNav(btnPaperLibrary, btnDashboard, btnPaperLibrary, btnCollections);
        cmbReadingStatus.getItems().addAll("Not Started", "Reading", "Completed");
        cmbRating.getItems().addAll("1", "2", "3", "4", "5");
        setupNotesTable();
        loadPaper();
    }

    private void loadPaper() {
        if (selectedPaperId <= 0) return;
        currentPaper = paperDAO.findById(selectedPaperId);
        if (currentPaper == null) return;

        lblPaperId.setText(String.valueOf(currentPaper.getId()));
        lblTitle.setText(currentPaper.getTitle());
        lblAuthors.setText(currentPaper.getAuthors());
        lblYear.setText(String.valueOf(currentPaper.getPublicationYear()));
        lblConference.setText(currentPaper.getPublicationVenue());
        lblDoi.setText(currentPaper.getDoi());
        lblPdfPath.setText(currentPaper.getFilePath());
        txtAbstractDetails.setText(currentPaper.getAbstractText());

        currentProgress = progressDAO.findByPaperAndUser(currentPaper.getId(), SessionManager.currentUserId);
        if (currentProgress != null) {
            cmbReadingStatus.setValue(currentProgress.getStatus());
            txtLastPage.setText(String.valueOf(currentProgress.getCurrentPage()));
        } else {
            cmbReadingStatus.setValue("Not Started");
        }

        loadNotes();
    }

    private void setupNotesTable() {
        colNoteText.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContent()));
        colNoteDate.setCellValueFactory(c -> {
            var ts = c.getValue().getCreatedAt();
            return new SimpleStringProperty(ts != null ? ts.toString().substring(0, 10) : "");
        });
        tblNotes.setItems(notesList);
    }

    private void loadNotes() {
        notesList.setAll(noteDAO.findByPaperId(selectedPaperId));
    }

    @FXML public void addNoteEvent() {
        String text = txtNewNote.getText().trim();
        if (text.isEmpty()) return;
        Note note = new Note(selectedPaperId, SessionManager.currentUserId, text);
        int id = noteDAO.insert(note);
        if (id > 0) {
            note.setId(id);
            notesList.add(0, note);
            txtNewNote.clear();
        }
    }

    @FXML public void deleteNoteEvent() {
        Note selected = tblNotes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            noteDAO.delete(selected.getId());
            notesList.remove(selected);
        }
    }

    @FXML public void saveProgressEvent() {
        String status = cmbReadingStatus.getValue();
        int lastPage = 0;
        try { lastPage = Integer.parseInt(txtLastPage.getText().trim()); } catch (NumberFormatException ignored) {}
        boolean fav = chkFavorite.isSelected();

        if (currentProgress == null) {
            currentProgress = new ReadingProgress(selectedPaperId, SessionManager.currentUserId, status);
            currentProgress.setCurrentPage(lastPage);
            int id = progressDAO.insert(currentProgress);
            if (id > 0) currentProgress.setId(id);
        } else {
            currentProgress.setStatus(status);
            currentProgress.setCurrentPage(lastPage);
            if (fav) currentProgress.setStatus("Favorite");
            progressDAO.update(currentProgress);
        }
    }

    @FXML public void markCompletedEvent() {
        if (currentProgress == null) {
            currentProgress = new ReadingProgress(selectedPaperId, SessionManager.currentUserId, "Completed");
            int id = progressDAO.insert(currentProgress);
            if (id > 0) currentProgress.setId(id);
        } else {
            currentProgress.setStatus("Completed");
            progressDAO.update(currentProgress);
        }
        cmbReadingStatus.setValue("Completed");
    }

    @FXML public void dashboardEvent() { Main.changeScene("dashboard"); }
    @FXML public void paperLibraryEvent() { Main.changeScene("paper_library"); }
    @FXML public void collectionsEvent() { Main.changeScene("collection_manager"); }
    @FXML public void logoutEvent() { SessionManager.clear(); Main.changeScene("login"); }
    @FXML public void openPdfEvent() {
        if (currentPaper == null) return;
        String path = currentPaper.getFilePath();
        if (path == null || path.isBlank()) {
            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Open PDF", "No PDF file associated with this paper.");
            return;
        }
        File f = new File(path);
        if (!f.exists()) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Open PDF", "File not found:\n" + path);
            return;
        }
        try {
            Desktop.getDesktop().open(f);
        } catch (Exception e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Open PDF", "Could not open file:\n" + e.getMessage());
        }
    }

    @FXML public void exportCitationEvent() {
        if (currentPaper == null) return;
        String ieee = CitationGenerator.generateIeee(currentPaper);

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Export Citation — IEEE");
        dialog.setHeaderText("IEEE Citation Format");

        TextArea ta = new TextArea(ieee);
        ta.setWrapText(true);
        ta.setEditable(false);
        ta.setPrefRowCount(6);
        ta.setPrefWidth(500);

        dialog.getDialogPane().setContent(ta);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    @FXML public void backToLibraryEvent() { Main.changeScene("paper_library"); }
}
