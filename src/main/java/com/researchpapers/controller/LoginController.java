package com.researchpapers.controller;

import com.researchpapers.Main;
import com.researchpapers.dao.UserDAO;
import com.researchpapers.model.User;
import com.researchpapers.utill.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML
    public TextField txtEmail;

    @FXML
    public PasswordField txtPassword;

    @FXML
    public TextField txtPasswordVisible;

    @FXML
    public Button btnLogin;

    @FXML
    public Button btnRegister;

    @FXML
    public Button btnForgotPassword;

    @FXML
    public Button btnTogglePassword;

    @FXML
    public ToggleButton toggleDarkMode;

    @FXML
    public Label lblMessage;

    @FXML
    public VBox cardBody;

    @FXML
    public VBox loginRightPanel;

    private final UserDAO userDAO = new UserDAO();
    private boolean passwordVisible = false;

    @FXML
    public void initialize() {
        btnTogglePassword.setText("\uD83D\uDC41");
        txtPassword.textProperty().addListener((obs, old, val) -> txtPasswordVisible.setText(val));
        txtPasswordVisible.textProperty().addListener((obs, old, val) -> txtPassword.setText(val));
    }

    @FXML
    public void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        txtPassword.setVisible(!passwordVisible);
        txtPassword.setManaged(!passwordVisible);
        txtPasswordVisible.setVisible(passwordVisible);
        txtPasswordVisible.setManaged(passwordVisible);
        if (passwordVisible) {
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        }
    }

    @FXML
    public void loginEvent() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Username/email and password are required", false);
            return;
        }

        User user = userDAO.login(email, password);
        if (user != null) {
            SessionManager.loggedUser = user.getUsername();
            SessionManager.currentUserId = user.getId();
            SessionManager.loggedUserFullName = user.getFullName();
            SessionManager.loggedUserRole = user.getRole();
            if ("ADMIN".equals(user.getRole())) {
                Main.changeScene("admin_dashboard");
            } else {
                Main.changeScene("dashboard");
            }
        } else {
            showMessage("Invalid email or password", false);
        }
    }

    @FXML
    public void registerEvent() {
        Main.changeScene("register");
    }

    @FXML
    public void forgotPasswordEvent() {
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            showMessage("Please enter your username or email first", false);
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            showMessage("Please enter a valid email address", false);
            return;
        }
        showMessage("Password reset link sent to " + email, true);
    }

    @FXML
    public void darkModeEvent() {
        if (toggleDarkMode.isSelected()) {
            cardBody.getStyleClass().add("dark");
            loginRightPanel.getStyleClass().add("dark");
        } else {
            cardBody.getStyleClass().remove("dark");
            loginRightPanel.getStyleClass().remove("dark");
        }
    }

    private void showMessage(String text, boolean success) {
        lblMessage.setText(text);
        lblMessage.setVisible(true);
        lblMessage.setManaged(true);
        lblMessage.getStyleClass().removeAll("success", "error");
        lblMessage.getStyleClass().add(success ? "success" : "error");
    }
}
