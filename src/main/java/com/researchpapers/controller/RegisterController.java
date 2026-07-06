package com.researchpapers.controller;

import com.researchpapers.Main;
import com.researchpapers.dao.UserDAO;
import com.researchpapers.model.User;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class RegisterController {

    @FXML
    public TextField txtFullName;

    @FXML
    public TextField txtUsername;

    @FXML
    public TextField txtEmail;

    @FXML
    public PasswordField txtPassword;

    @FXML
    public TextField txtPasswordVisible;

    @FXML
    public PasswordField txtConfirmPassword;

    @FXML
    public TextField txtConfirmPasswordVisible;

    @FXML
    public Button btnTogglePassword;

    @FXML
    public Button btnToggleConfirmPassword;

    @FXML
    public Button btnRegister;

    @FXML
    public Button btnBackToLogin;

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
    private boolean confirmPasswordVisible = false;

    @FXML
    public void initialize() {
        btnTogglePassword.setText("\uD83D\uDC41");
        btnToggleConfirmPassword.setText("\uD83D\uDC41");
        txtPassword.textProperty().addListener((obs, old, val) -> txtPasswordVisible.setText(val));
        txtPasswordVisible.textProperty().addListener((obs, old, val) -> txtPassword.setText(val));
        txtConfirmPassword.textProperty().addListener((obs, old, val) -> txtConfirmPasswordVisible.setText(val));
        txtConfirmPasswordVisible.textProperty().addListener((obs, old, val) -> txtConfirmPassword.setText(val));
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
    public void toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible;
        txtConfirmPassword.setVisible(!confirmPasswordVisible);
        txtConfirmPassword.setManaged(!confirmPasswordVisible);
        txtConfirmPasswordVisible.setVisible(confirmPasswordVisible);
        txtConfirmPasswordVisible.setManaged(confirmPasswordVisible);
        if (confirmPasswordVisible) {
            txtConfirmPasswordVisible.requestFocus();
            txtConfirmPasswordVisible.positionCaret(txtConfirmPasswordVisible.getText().length());
        }
    }

    @FXML
    public void registerEvent() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirm = txtConfirmPassword.getText();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showMessage("All fields are required", false);
            return;
        }

        if (username.length() < 3) {
            showMessage("Username must be at least 3 characters", false);
            return;
        }

        if (!username.matches("^[a-zA-Z0-9_-]+$")) {
            showMessage("Username can only contain letters, numbers, underscores, and hyphens", false);
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            showMessage("Please enter a valid email address", false);
            return;
        }

        if (password.length() < 6) {
            showMessage("Password must be at least 6 characters", false);
            return;
        }

        if (!password.equals(confirm)) {
            showMessage("Passwords do not match", false);
            return;
        }

        if (userDAO.findByUsername(username) != null) {
            showMessage("Username is already taken", false);
            return;
        }

        if (userDAO.findByEmail(email) != null) {
            showMessage("Email is already registered", false);
            return;
        }

        User user = new User(username, fullName, email, password);
        int id = userDAO.register(user);
        if (id > 0) {
            showMessage("Registration successful! Redirecting to login...", true);
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> Main.changeScene("login"));
            delay.play();
        } else {
            showMessage("Registration failed. Please try again.", false);
        }
    }

    @FXML
    public void backToLoginEvent() {
        Main.changeScene("login");
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