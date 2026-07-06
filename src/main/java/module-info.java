module com.researchpapers {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;

    opens com.researchpapers to javafx.fxml;
    opens com.researchpapers.controller to javafx.fxml;
    exports com.researchpapers;
    exports com.researchpapers.controller;
    exports com.researchpapers.model;
    exports com.researchpapers.dao;
    exports com.researchpapers.utill;
}
