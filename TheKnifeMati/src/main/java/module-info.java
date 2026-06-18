module com.example.theknife {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.rmi;
    requires com.opencsv;
    requires java.desktop;
    requires transitive javafx.graphics;
    requires java.net.http;
    requires java.sql;


    opens com.example.theknife.client to javafx.fxml;
    exports com.example.theknife.client;
    exports com.example.theknife.server;
    exports com.example.theknife.common;
}