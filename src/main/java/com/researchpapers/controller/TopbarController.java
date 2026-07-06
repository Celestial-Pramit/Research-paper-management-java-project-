package com.researchpapers.controller;

import com.researchpapers.utill.AlertUtil;
import com.researchpapers.utill.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TopbarController {

    @FXML public Label lblUserName;
    @FXML public Label lblUserRole;
    @FXML public Label lblAvatarInitials;
    @FXML public Button btnNotification;
    @FXML public Button btnRoleDropdown;

    @FXML
    public void initialize() {
        lblUserName.setText(SessionManager.loggedUserFullName != null ? SessionManager.loggedUserFullName : SessionManager.loggedUser);
        lblUserRole.setText(SessionManager.loggedUserRole != null ? SessionManager.loggedUserRole : "");
        lblAvatarInitials.setText(getInitials(SessionManager.loggedUserFullName));
    }

    @FXML public void notificationEvent() {
        AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Notifications", "Notification features will be available in a future update.");
    }

    @FXML public void roleDropdownEvent() {
        AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Role Switcher", "Role switching features will be available in a future update.");
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            String p = parts[0];
            return p.length() >= 2 ? p.substring(0, 2).toUpperCase() : p.toUpperCase();
        }
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
