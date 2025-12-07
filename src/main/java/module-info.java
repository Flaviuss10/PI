module ERP {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens erp.ui to javafx.fxml;
    opens erp.model to javafx.base;

    exports erp.ui;
}
