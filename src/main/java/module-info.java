module ERP {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.base;

    requires java.net.http;
    requires org.json;

    opens erp.ui to javafx.fxml;
    opens erp.model to javafx.base;

    exports erp.ui;
    exports erp.model;
    exports erp.service;
}
