package com.researchpapers.service;

import com.researchpapers.model.Paper;
import com.researchpapers.utill.AlertUtil;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;

public class PaperDialogService {

    public static Optional<Paper> showPaperDialog(Paper existing, int userId) {
        Dialog<Paper> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Paper" : "Edit Paper");
        dialog.setHeaderText(existing == null ? "Enter the details of the new paper" : "Update the paper details");

        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Paper title");
        TextField txtAuthors = new TextField();
        txtAuthors.setPromptText("Author(s)");
        TextArea txtAbstract = new TextArea();
        txtAbstract.setPromptText("Abstract (optional)");
        txtAbstract.setPrefRowCount(3);
        TextField txtVenue = new TextField();
        txtVenue.setPromptText("Publication venue (optional)");
        TextField txtYear = new TextField();
        txtYear.setPromptText("Publication year");
        TextField txtDoi = new TextField();
        txtDoi.setPromptText("DOI (optional)");
        ComboBox<String> cmbCategory = new ComboBox<>();
        cmbCategory.getItems().addAll("General", "AI/ML", "Quantum", "Healthcare", "Blockchain");
        cmbCategory.setValue("General");
        ComboBox<Integer> cmbRating = new ComboBox<>();
        cmbRating.getItems().addAll(1, 2, 3, 4, 5);
        cmbRating.setValue(0);

        Label lblFilePath = new Label("No file selected");
        lblFilePath.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");
        Button btnChoosePdf = new Button("Choose PDF");
        btnChoosePdf.getStyleClass().add("clear-filter-btn");
        btnChoosePdf.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select PDF");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File f = fc.showOpenDialog(lblFilePath.getScene().getWindow());
            if (f != null) lblFilePath.setText(f.getAbsolutePath());
        });

        if (existing != null) {
            txtTitle.setText(existing.getTitle());
            txtAuthors.setText(existing.getAuthors());
            txtAbstract.setText(existing.getAbstractText() != null ? existing.getAbstractText() : "");
            txtVenue.setText(existing.getPublicationVenue() != null ? existing.getPublicationVenue() : "");
            txtYear.setText(String.valueOf(existing.getPublicationYear()));
            txtDoi.setText(existing.getDoi() != null ? existing.getDoi() : "");
            cmbCategory.setValue(existing.getCategory() != null ? existing.getCategory() : "General");
            cmbRating.setValue(existing.getRating() > 0 ? existing.getRating() : 0);
            if (existing.getFilePath() != null && !existing.getFilePath().isEmpty())
                lblFilePath.setText(existing.getFilePath());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Title *:"), 0, 0);
        grid.add(txtTitle, 1, 0);
        grid.add(new Label("Authors *:"), 0, 1);
        grid.add(txtAuthors, 1, 1);
        grid.add(new Label("Abstract:"), 0, 2);
        grid.add(txtAbstract, 1, 2);
        grid.add(new Label("Venue:"), 0, 3);
        grid.add(txtVenue, 1, 3);
        grid.add(new Label("Year *:"), 0, 4);
        grid.add(txtYear, 1, 4);
        grid.add(new Label("DOI:"), 0, 5);
        grid.add(txtDoi, 1, 5);
        grid.add(new Label("Category:"), 0, 6);
        grid.add(cmbCategory, 1, 6);
        grid.add(new Label("Rating:"), 0, 7);
        grid.add(cmbRating, 1, 7);
        grid.add(new Label("PDF:"), 0, 8);
        HBox pdfRow = new HBox(8, btnChoosePdf, lblFilePath);
        pdfRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        grid.add(pdfRow, 1, 8);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn != saveBtn) return null;
            String title = txtTitle.getText().trim();
            String authors = txtAuthors.getText().trim();
            if (title.isEmpty() || authors.isEmpty()) {
                AlertUtil.showAlert(Alert.AlertType.WARNING, "Validation", "Title and Authors are required.");
                return null;
            }
            int year;
            try {
                year = Integer.parseInt(txtYear.getText().trim());
                if (year < 1000 || year > 9999) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                AlertUtil.showAlert(Alert.AlertType.WARNING, "Validation", "Year must be a valid 4-digit number.");
                return null;
            }

            Paper p = new Paper(title, authors, year, userId);
            p.setAbstractText(txtAbstract.getText().trim());
            p.setPublicationVenue(txtVenue.getText().trim());
            p.setDoi(txtDoi.getText().trim());
            p.setCategory(cmbCategory.getValue());
            p.setRating(cmbRating.getValue() != null ? cmbRating.getValue() : 0);
            String fp = lblFilePath.getText();
            if (!"No file selected".equals(fp)) p.setFilePath(fp);
            if (existing != null) p.setId(existing.getId());
            return p;
        });

        return dialog.showAndWait();
    }
}
