package com.researchpapers.controller;

import com.researchpapers.Main;
import com.researchpapers.dao.CollectionDAO;
import com.researchpapers.dao.PaperDAO;
import com.researchpapers.model.Collection;
import com.researchpapers.model.Paper;
import com.researchpapers.utill.SessionManager;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CollectionController {

    @FXML public TextField txtCollectionName;
    @FXML public Button btnCreateCollection;
    @FXML public Button btnDeleteCollection;
    @FXML public Button btnRefreshCollections;
    @FXML public ListView<String> lstCollections;
    @FXML public Label lblCollectionCount;

    @FXML public TableView<Paper> tblAvailablePapers;
    @FXML public TableColumn<Paper, String> colAvailableTitle;
    @FXML public TableColumn<Paper, String> colAvailableAuthors;
    @FXML public TableColumn<Paper, Number> colAvailableYear;
    @FXML public Label lblAvailableCount;

    @FXML public TableView<Paper> tblCollectionPapers;
    @FXML public TableColumn<Paper, String> colCollectionTitle;
    @FXML public TableColumn<Paper, String> colCollectionAuthors;
    @FXML public TableColumn<Paper, Number> colCollectionYear;
    @FXML public Label lblCollectionPaperCount;

    @FXML public Button btnAddToCollection;
    @FXML public Button btnRemoveFromCollection;
    @FXML public Button btnDashboard;
    @FXML public Button btnPaperLibrary;
    @FXML public Button btnCollections;

    private final CollectionDAO collectionDAO = new CollectionDAO();
    private final PaperDAO paperDAO = new PaperDAO();
    private final ObservableList<Collection> collectionsList = FXCollections.observableArrayList();
    private final ObservableList<Paper> allPapers = FXCollections.observableArrayList();
    private final ObservableList<Paper> availablePapers = FXCollections.observableArrayList();
    private final ObservableList<Paper> collectionPapers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Main.setActiveNav(btnCollections, btnDashboard, btnPaperLibrary, btnCollections);

        colAvailableTitle.setCellValueFactory(c -> Bindings.createStringBinding(() -> c.getValue().getTitle()));
        colAvailableAuthors.setCellValueFactory(c -> Bindings.createStringBinding(() -> c.getValue().getAuthors()));
        colAvailableYear.setCellValueFactory(c -> Bindings.createIntegerBinding(() -> c.getValue().getPublicationYear()));
        colCollectionTitle.setCellValueFactory(c -> Bindings.createStringBinding(() -> c.getValue().getTitle()));
        colCollectionAuthors.setCellValueFactory(c -> Bindings.createStringBinding(() -> c.getValue().getAuthors()));
        colCollectionYear.setCellValueFactory(c -> Bindings.createIntegerBinding(() -> c.getValue().getPublicationYear()));

        tblAvailablePapers.setItems(availablePapers);
        tblCollectionPapers.setItems(collectionPapers);

        loadCollections();
        loadAllPapers();

        lstCollections.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null) loadCollectionPapers(getSelectedCollectionId());
        });
    }

    private void loadCollections() {
        collectionsList.setAll(collectionDAO.findByUserId(SessionManager.currentUserId));
        lstCollections.setItems(FXCollections.observableArrayList(
            collectionsList.stream().map(Collection::getName).toList()
        ));
        updateCounts();
    }

    private void loadAllPapers() {
        allPapers.setAll(paperDAO.findAll(SessionManager.currentUserId));
        availablePapers.setAll(allPapers);
    }

    private void loadCollectionPapers(int collectionId) {
        if (collectionId <= 0) return;
        var paperIds = collectionDAO.findPaperIds(collectionId);
        collectionPapers.clear();
        availablePapers.setAll(allPapers);
        for (int pid : paperIds) {
            Paper p = paperDAO.findById(pid);
            if (p != null) { collectionPapers.add(p); availablePapers.remove(p); }
        }
        updateCounts();
    }

    private int getSelectedCollectionId() {
        int idx = lstCollections.getSelectionModel().getSelectedIndex();
        if (idx >= 0 && idx < collectionsList.size()) return collectionsList.get(idx).getId();
        return -1;
    }

    @FXML
    public void createCollectionEvent() {
        String name = txtCollectionName.getText().trim();
        if (name.isEmpty()) return;
        Collection c = new Collection(name, SessionManager.currentUserId);
        int id = collectionDAO.insert(c);
        if (id > 0) {
            c.setId(id);
            collectionsList.add(c);
            lstCollections.getItems().add(name);
            lstCollections.getSelectionModel().select(name);
            txtCollectionName.clear();
        }
        updateCounts();
    }

    @FXML
    public void deleteCollectionEvent() {
        int id = getSelectedCollectionId();
        int idx = lstCollections.getSelectionModel().getSelectedIndex();
        if (id > 0) {
            collectionDAO.delete(id);
            collectionsList.remove(idx);
            lstCollections.getItems().remove(idx);
            collectionPapers.clear();
        }
        updateCounts();
    }

    @FXML
    public void addToCollectionEvent() {
        int colId = getSelectedCollectionId();
        Paper selected = tblAvailablePapers.getSelectionModel().getSelectedItem();
        if (colId > 0 && selected != null) {
            collectionDAO.addPaper(colId, selected.getId());
            collectionPapers.add(selected);
            availablePapers.remove(selected);
        }
        updateCounts();
    }

    @FXML
    public void removeFromCollectionEvent() {
        int colId = getSelectedCollectionId();
        Paper selected = tblCollectionPapers.getSelectionModel().getSelectedItem();
        if (colId > 0 && selected != null) {
            collectionDAO.removePaper(colId, selected.getId());
            collectionPapers.remove(selected);
            availablePapers.add(selected);
        }
        updateCounts();
    }

    @FXML
    public void refreshCollectionsEvent() {
        loadCollections();
        loadAllPapers();
        int colId = getSelectedCollectionId();
        if (colId > 0) loadCollectionPapers(colId);
    }

    private void updateCounts() {
        lblCollectionCount.setText(String.valueOf(collectionsList.size()));
        lblAvailableCount.setText(String.valueOf(availablePapers.size()));
        lblCollectionPaperCount.setText(String.valueOf(collectionPapers.size()));
    }

    @FXML public void dashboardEvent() { Main.changeScene("dashboard"); }
    @FXML public void paperLibraryEvent() { Main.changeScene("paper_library"); }
    @FXML public void collectionsEvent() {}
    @FXML public void logoutEvent() { SessionManager.clear(); Main.changeScene("login"); }
}
