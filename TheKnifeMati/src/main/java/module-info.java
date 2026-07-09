/**
 * Definisce il modulo principale dell'applicazione TheKnife.
 *
 * <p>
 * Il modulo include le dipendenze necessarie al funzionamento
 * dell'applicazione, tra cui JavaFX per l'interfaccia grafica,
 * Java RMI per la comunicazione client-server, JDBC per
 * l'accesso al database PostgreSQL e le librerie esterne
 * utilizzate per la gestione dei file CSV e dei controlli
 * grafici avanzati.
 *
 * <p>
 * Inoltre esporta i package contenenti le classi comuni,
 * il client e il server, e rende accessibile il package
 * del client al framework JavaFX tramite la direttiva
 * {@code opens}.
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026
 */
module com.example.theknife {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.rmi;
    requires java.desktop;
    requires transitive javafx.graphics;
    requires java.net.http;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires org.controlsfx.controls;

    opens com.example.theknife.client to javafx.fxml;
    exports com.example.theknife.client;
    exports com.example.theknife.server;
    exports com.example.theknife.common;
}