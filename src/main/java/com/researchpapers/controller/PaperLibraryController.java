package com.researchpapers.controller;

import com.researchpapers.Main;
import com.researchpapers.dao.PaperDAO;
import com.researchpapers.dao.ReadingProgressDAO;
import com.researchpapers.model.Paper;
import com.researchpapers.service.ExportService;
import com.researchpapers.service.PaperDialogService;
import com.researchpapers.utill.AlertUtil;
import com.researchpapers.utill.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;

public class PaperLibraryController {

    @FXML public Button btnDashboard;
    @FXML public Button btnPaperLibrary;
    @FXML public Button btnCollections;
    @FXML public Button btnLogout;
    @FXML public Label lblPageTitle;
    @FXML public TextField txtSearch;
    @FXML public ComboBox<String> cmbFilterStatus;
    @FXML public ComboBox<String> cmbFilterCategory;
    @FXML public ComboBox<String> cmbFilterYear;
    @FXML public Button btnClearFilters;
    @FXML public Button btnAddPaper;
    @FXML public Button btnExport;
    @FXML public Button btnPreviousPage;
    @FXML public Button btnNextPage;
    @FXML public Label lblPaperCount;
    @FXML public Label lblPageInfo;

    @FXML public TableView<Paper> tblPapers;
    @FXML public TableColumn<Paper, String> colTitle;
    @FXML public TableColumn<Paper, String> colAuthors;
    @FXML public TableColumn<Paper, Number> colYear;
    @FXML public TableColumn<Paper, String> colStatus;
    @FXML public TableColumn<Paper, String> colCategory;
    @FXML public TableColumn<Paper, Number> colRating;
    @FXML public TableColumn<Paper, Void> colActions;

    private final PaperDAO paperDAO = new PaperDAO();
    private final ReadingProgressDAO progressDAO = new ReadingProgressDAO();
    private ObservableList<Paper> allPapers;
    private ObservableList<Paper> filteredPapers;

    private static final int PAGE_SIZE = 10;
    private int currentPage = 1;

    @FXML
    public void initialize() {
        Main.setActiveNav(btnPaperLibrary, btnDashboard, btnPaperLibrary, btnCollections);
        cmbFilterStatus.getItems().addAll("All", "Reading", "Completed", "Not Started", "Favorite");
        cmbFilterCategory.getItems().addAll("All", "AI/ML", "Quantum", "Healthcare", "General", "Blockchain");
        cmbFilterYear.getItems().addAll("All", "2025", "2024", "2023", "2022", "2021", "2020");
        cmbFilterStatus.setValue("All");
        cmbFilterCategory.setValue("All");
        cmbFilterYear.setValue("All");

        setupTable();
        loadPapers();

        txtSearch.textProperty().addListener((o, old, val) -> applyFilters());
        cmbFilterStatus.valueProperty().addListener((o, old, val) -> applyFilters());
        cmbFilterCategory.valueProperty().addListener((o, old, val) -> applyFilters());
        cmbFilterYear.valueProperty().addListener((o, old, val) -> applyFilters());
    }

    private void loadPapers() {
        allPapers = FXCollections.observableArrayList(paperDAO.findAll(SessionManager.currentUserId));
        applyFilters();
    }

    private void applyFilters() {
        String search = txtSearch.getText().toLowerCase().trim();
        String status = cmbFilterStatus.getValue();
        String category = cmbFilterCategory.getValue();
        String year = cmbFilterYear.getValue();

        filteredPapers = FXCollections.observableArrayList(
            allPapers.filtered(p -> {
                if (!search.isEmpty() && !p.getTitle().toLowerCase().contains(search)
                    && !p.getAuthors().toLowerCase().contains(search)) {
                    return false;
                }
                if (!"All".equals(status)) {
                    var prog = progressDAO.findByPaperAndUser(p.getId(), SessionManager.currentUserId);
                    String s = prog != null ? prog.getStatus() : "Not Started";
                    if (!s.equals(status)) return false;
                }
                if (!"All".equals(category) && !category.equals(p.getCategory())) return false;
                if (!"All".equals(year)) {
                    try {
                        if (Integer.parseInt(year) != p.getPublicationYear()) return false;
                    } catch (NumberFormatException e) { return false; }
                }
                return true;
            }).stream().collect(Collectors.toList())
        );

        lblPaperCount.setText("ALL PAPERS (" + filteredPapers.size() + ")");
        currentPage = 1;
        updatePagination();
    }

    private void updatePagination() {
        int total = filteredPapers.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (currentPage > totalPages) currentPage = totalPages;

        int from = (currentPage - 1) * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, total);

        if (total == 0) {
            tblPapers.setItems(FXCollections.observableArrayList());
            lblPageInfo.setText("Page 0 of 0");
        } else {
            tblPapers.setItems(FXCollections.observableArrayList(filteredPapers.subList(from, to)));
            lblPageInfo.setText("Page " + currentPage + " of " + totalPages);
        }

        btnPreviousPage.setDisable(currentPage <= 1);
        btnNextPage.setDisable(currentPage >= totalPages);
    }

    private void setupTable() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthors.setCellValueFactory(new PropertyValueFactory<>("authors"));
        colYear.setCellValueFactory(c -> new javafx.beans.property.ReadOnlyIntegerWrapper(c.getValue().getPublicationYear()));

        colStatus.setCellValueFactory(c -> {
            var p = progressDAO.findByPaperAndUser(c.getValue().getId(), SessionManager.currentUserId);
            return new javafx.beans.property.ReadOnlyStringWrapper(p != null ? p.getStatus() : "Not Started");
        });
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setText(null); setGraphic(null); return; }
                Label badge = new Label(status.toUpperCase());
                badge.getStyleClass().addAll("status-badge");
                switch (status.toLowerCase()) {
                    case "reading" -> badge.getStyleClass().add("status-reading");
                    case "completed" -> badge.getStyleClass().add("status-completed");
                    case "favorite" -> badge.getStyleClass().add("status-favorite");
                    default -> badge.getStyleClass().add("status-notstarted");
                }
                setGraphic(badge);
            }
        });

        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCategory.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String cat, boolean empty) {
                super.updateItem(cat, empty);
                if (empty || cat == null) { setText(null); setGraphic(null); return; }
                Label tag = new Label(cat);
                tag.getStyleClass().addAll("category-tag");
                switch (cat) {
                    case "AI/ML" -> tag.getStyleClass().add("cat-aiml");
                    case "Quantum" -> tag.getStyleClass().add("cat-quantum");
                    case "Blockchain" -> tag.getStyleClass().add("cat-blockchain");
                    case "Healthcare" -> tag.getStyleClass().add("cat-healthcare");
                }
                setGraphic(tag);
            }
        });

        colRating.setCellValueFactory(c -> new javafx.beans.property.ReadOnlyIntegerWrapper(c.getValue().getRating()));
        colRating.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number rating, boolean empty) {
                super.updateItem(rating, empty);
                if (empty || rating == null) { setText(null); return; }
                int r = rating.intValue();
                setText("\u2605".repeat(r) + "\u2606".repeat(5 - r));
                getStyleClass().add("rating-stars");
            }
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("\u25C9");
            private final Button editBtn = new Button("\u270E");
            private final Button deleteBtn = new Button("\u2715");
            private final HBox pane = new HBox(4, viewBtn, editBtn, deleteBtn);
            {
                viewBtn.getStyleClass().addAll("action-btn", "action-view-btn");
                viewBtn.setTooltip(new Tooltip("View details"));
                viewBtn.setOnAction(e -> {
                    Paper paper = getTableView().getItems().get(getIndex());
                    PaperDetailsController.setSelectedPaperId(paper.getId());
                    Main.changeScene("paper_details");
                });
                editBtn.getStyleClass().addAll("action-btn", "action-edit-btn");
                editBtn.setTooltip(new Tooltip("Edit paper"));
                editBtn.setOnAction(e -> {
                    Paper paper = getTableView().getItems().get(getIndex());
                    showPaperDialog(paper);
                });
                deleteBtn.getStyleClass().addAll("action-btn", "action-delete-btn");
                deleteBtn.setTooltip(new Tooltip("Delete paper"));
                deleteBtn.setOnAction(e -> {
                    Paper paper = getTableView().getItems().get(getIndex());
                    deletePaper(paper);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void showPaperDialog(Paper existing) {
        Optional<Paper> result = PaperDialogService.showPaperDialog(existing, SessionManager.currentUserId);
        result.ifPresent(paper -> {
            if (existing == null) {
                int id = paperDAO.insert(paper);
                if (id > 0) loadPapers();
            } else {
                paperDAO.update(paper);
                loadPapers();
            }
        });
    }

    private void deletePaper(Paper paper) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete \"" + paper.getTitle() + "\"? This cannot be undone.",
            ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Delete Paper");
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            paperDAO.delete(paper.getId());
            loadPapers();
        }
    }

    @FXML public void dashboardEvent() { Main.changeScene("dashboard"); }
    @FXML public void paperLibraryEvent() {}
    @FXML public void collectionsEvent() { Main.changeScene("collection_manager"); }
    @FXML public void logoutEvent() { SessionManager.clear(); Main.changeScene("login"); }
    @FXML public void addPaperEvent() { showPaperDialog(null); }
    @FXML public void exportEvent() {
        if (filteredPapers == null || filteredPapers.isEmpty()) {
            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Export", "No papers to export.");
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Papers to CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fc.setInitialFileName("papers.csv");
        File file = fc.showSaveDialog(btnExport.getScene().getWindow());
        if (file == null) return;
        try {
            ExportService.exportToCsv(filteredPapers, file.getAbsolutePath());
            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Export", "Exported " + filteredPapers.size() + " papers to:\n" + file.getName());
        } catch (Exception e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Export Failed", e.getMessage());
        }
    }
    @FXML public void clearFiltersEvent() { cmbFilterStatus.setValue("All"); cmbFilterCategory.setValue("All"); cmbFilterYear.setValue("All"); txtSearch.clear(); }
    @FXML public void previousPageEvent() { if (currentPage > 1) { currentPage--; updatePagination(); } }
    @FXML public void nextPageEvent() {
        int total = filteredPapers.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (currentPage < totalPages) { currentPage++; updatePagination(); }
    }
}