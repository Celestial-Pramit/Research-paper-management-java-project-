package com.researchpapers.controller;

import com.researchpapers.Main;
import com.researchpapers.dao.PaperDAO;
import com.researchpapers.dao.UserDAO;
import com.researchpapers.model.User;
import com.researchpapers.utill.ConnectionSingleton;
import com.researchpapers.utill.SessionManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboardController {

    @FXML public Button btnDashboard;
    @FXML public Button btnPaperLibrary;
    @FXML public Button btnCollections;
    @FXML public Button btnLogout;
    @FXML public Label lblTotalUsers;
    @FXML public Label lblTotalPapers;
    @FXML public Label lblTotalCollections;
    @FXML public Label lblActiveUsers;
    @FXML public TableView<User> tblUsers;
    @FXML public TableColumn<User, Number> colUserId;
    @FXML public TableColumn<User, String> colUsername;
    @FXML public TableColumn<User, String> colFullName;
    @FXML public TableColumn<User, String> colEmail;
    @FXML public TableColumn<User, String> colRole;
    @FXML public TableColumn<User, String> colStatus;
    @FXML public TableColumn<User, Void> colActions;

    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> usersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Main.setActiveNav(btnDashboard, btnDashboard, btnPaperLibrary, btnCollections);
        loadStats();
        setupTable();
        loadUsers();
    }

    private void loadStats() {
        Connection conn = ConnectionSingleton.getConnection();
        try {
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users");
                 ResultSet rs = ps.executeQuery()) {
                lblTotalUsers.setText(rs.next() ? String.valueOf(rs.getInt(1)) : "0");
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM papers");
                 ResultSet rs = ps.executeQuery()) {
                lblTotalPapers.setText(rs.next() ? String.valueOf(rs.getInt(1)) : "0");
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM collections");
                 ResultSet rs = ps.executeQuery()) {
                lblTotalCollections.setText(rs.next() ? String.valueOf(rs.getInt(1)) : "0");
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE status = 'Active'");
                 ResultSet rs = ps.executeQuery()) {
                lblActiveUsers.setText(rs.next() ? String.valueOf(rs.getInt(1)) : "0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTable() {
        colUserId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        colUsername.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        colFullName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colRole.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button toggleBtn = new Button();
            {
                toggleBtn.getStyleClass().add("add-paper-btn");
                toggleBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    if (u != null) toggleUserStatus(u);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }
                User u = getTableView().getItems().get(getIndex());
                if ("ADMIN".equals(u.getRole())) {
                    setGraphic(null);
                    return;
                }
                boolean active = "Active".equals(u.getStatus());
                toggleBtn.setText(active ? "Deactivate" : "Activate");
                toggleBtn.setStyle(active ? "-fx-background-color: #e74c3c; -fx-text-fill: white;" : "-fx-background-color: #27ae60; -fx-text-fill: white;");
                setGraphic(toggleBtn);
            }
        });
    }

    private void loadUsers() {
        Connection conn = ConnectionSingleton.getConnection();
        usersList.clear();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM users ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usersList.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tblUsers.setItems(usersList);
    }

    private void toggleUserStatus(User user) {
        String newStatus = "Active".equals(user.getStatus()) ? "Inactive" : "Active";
        Connection conn = ConnectionSingleton.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET status = ? WHERE id = ?")) {
            ps.setString(1, newStatus);
            ps.setInt(2, user.getId());
            ps.executeUpdate();
            user.setStatus(newStatus);
            tblUsers.refresh();
            loadStats();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML public void dashboardEvent() {}
    @FXML public void paperLibraryEvent() { Main.changeScene("paper_library"); }
    @FXML public void collectionsEvent() { Main.changeScene("collection_manager"); }
    @FXML public void logoutEvent() { SessionManager.clear(); Main.changeScene("login"); }
    @FXML public void refreshEvent() { loadStats(); loadUsers(); }
}