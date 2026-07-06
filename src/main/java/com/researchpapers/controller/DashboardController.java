package com.researchpapers.controller;

import com.researchpapers.Main;
import com.researchpapers.dao.PaperDAO;
import com.researchpapers.dao.ReadingProgressDAO;
import com.researchpapers.model.Paper;
import com.researchpapers.service.PaperDialogService;
import com.researchpapers.utill.SessionManager;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

public class DashboardController {

    @FXML public Label lblTotalPapers;
    @FXML public Label lblReadingCount;
    @FXML public Label lblCompletedCount;
    @FXML public Label lblFavoriteCount;
    @FXML public TableView<Paper> tblRecentPapers;
    @FXML public TableColumn<Paper, String> colRecentTitle;
    @FXML public TableColumn<Paper, String> colRecentAuthors;
    @FXML public TableColumn<Paper, Number> colRecentYear;
    @FXML public TableColumn<Paper, String> colRecentStatus;
    @FXML public TableColumn<Paper, Void> colActions;
    @FXML public BarChart<String, Number> barChartPapersByYear;
    @FXML public PieChart pieChartReadingProgress;
    @FXML public Button btnDashboard;
    @FXML public Button btnPaperLibrary;
    @FXML public Button btnCollections;

    private final PaperDAO paperDAO = new PaperDAO();
    private final ReadingProgressDAO progressDAO = new ReadingProgressDAO();

    @FXML
    public void initialize() {
        Main.setActiveNav(btnDashboard, btnDashboard, btnPaperLibrary, btnCollections);
        loadStats();
        loadRecentPapers();
        loadCharts();
    }

    private void loadStats() {
        int uid = SessionManager.currentUserId;
        lblTotalPapers.setText(String.valueOf(paperDAO.findAll(uid).size()));
        lblReadingCount.setText(String.valueOf(progressDAO.countByStatus(uid, "Reading")));
        lblCompletedCount.setText(String.valueOf(progressDAO.countByStatus(uid, "Completed")));
        lblFavoriteCount.setText(String.valueOf(progressDAO.countByStatus(uid, "Favorite")));
    }

    private void loadRecentPapers() {
        colRecentTitle.setCellValueFactory(c -> Bindings.createStringBinding(() -> c.getValue().getTitle()));
        colRecentAuthors.setCellValueFactory(c -> Bindings.createStringBinding(() -> c.getValue().getAuthors()));
        colRecentYear.setCellValueFactory(c -> Bindings.createIntegerBinding(() -> c.getValue().getPublicationYear()));
        colRecentStatus.setCellValueFactory(c -> Bindings.createStringBinding(() -> getStatus(c.getValue().getId())));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("\u25C9");
            {
                viewBtn.getStyleClass().addAll("action-btn", "action-view-btn");
                viewBtn.setTooltip(new Tooltip("View details"));
                viewBtn.setOnAction(e -> {
                    Paper paper = getTableView().getItems().get(getIndex());
                    PaperDetailsController.setSelectedPaperId(paper.getId());
                    Main.changeScene("paper_details");
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });

        int uid = SessionManager.currentUserId;
        ObservableList<Paper> items = FXCollections.observableArrayList(paperDAO.findAll(uid));
        tblRecentPapers.setItems(items);
    }

    private String getStatus(int paperId) {
        var p = progressDAO.findByPaperAndUser(paperId, SessionManager.currentUserId);
        return p != null ? p.getStatus() : "Not Started";
    }

    private void loadCharts() {
        barChartPapersByYear.getData().clear();
        pieChartReadingProgress.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Papers per Year");
        int uid = SessionManager.currentUserId;
        java.util.Map<Integer, Integer> yearCount = new java.util.HashMap<>();
        for (Paper p : paperDAO.findAll(uid)) {
            yearCount.merge(p.getPublicationYear(), 1, Integer::sum);
        }
        yearCount.forEach((y, c) -> series.getData().add(new XYChart.Data<>(String.valueOf(y), c)));
        barChartPapersByYear.getData().add(series);

        pieChartReadingProgress.getData().addAll(
            new PieChart.Data("Reading", progressDAO.countByStatus(uid, "Reading")),
            new PieChart.Data("Completed", progressDAO.countByStatus(uid, "Completed")),
            new PieChart.Data("Not Started", progressDAO.countByStatus(uid, "Not Started")),
            new PieChart.Data("Favorite", progressDAO.countByStatus(uid, "Favorite"))
        );
    }

    @FXML public void dashboardEvent() {}
    @FXML public void paperLibraryEvent() { Main.changeScene("paper_library"); }
    @FXML public void collectionsEvent() { Main.changeScene("collection_manager"); }
    @FXML public void logoutEvent() { SessionManager.clear(); Main.changeScene("login"); }
    @FXML public void addPaperEvent() {
        PaperDialogService.showPaperDialog(null, SessionManager.currentUserId).ifPresent(paper -> {
            int id = paperDAO.insert(paper);
            if (id > 0) {
                loadStats();
                loadRecentPapers();
                loadCharts();
            }
        });
    }
    @FXML public void refreshEvent() { loadStats(); loadRecentPapers(); loadCharts(); }
}
