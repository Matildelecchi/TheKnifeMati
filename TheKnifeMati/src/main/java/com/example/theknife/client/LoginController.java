package com.example.theknife.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import com.example.theknife.common.DBService;
import com.example.theknife.common.Utente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller per la gestione del login degli utenti.
 * <p>
 * Questa classe gestisce l'autenticazione degli utenti tramite verifica delle credenziali
 * recuperate dal server remoto (RMI) e controlla il flusso di navigazione verso la schermata principale.
 *
 * Supporta anche:
 * <ul>
 *   <li>Accesso come ospite</li>
 *   <li>Navigazione verso la schermata di registrazione</li>
 *   <li>Cifratura delle password tramite SHA-256</li>
 * </ul>
 *
 * @author Matilde Lecchi, 759875, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Claudio Bonci, 759939, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */
public class LoginController {
     /** File CSV utenti (non più principale, sostituito da RMI). */
    private static final String USERS_FILE = "data/utenti.csv";
    
    /** Path del file CSS dell'interfaccia. */
    private static final String CSS_PATH = "/data/stile.css";

    @FXML
    private TextField campoUsername;
    @FXML
    private PasswordField campoPassword;

    /** Callback eseguita dopo login riuscito. */
    private Runnable onLoginSuccess;
    /** Servizio remoto RMI per autenticazione utenti. */
    private static DBService server;

    /**
     * Imposta una callback da eseguire dopo login riuscito.
     *
     * @param callback azione da eseguire
     */
    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }

    /**
     * Inizializza il controller e recupera il riferimento al server RMI.
     */
    @FXML
    private void initialize() {
        try {
            server = ClientMain.getServer();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestisce il login dell’utente.
     * <p>
     * Se le credenziali sono corrette, viene inizializzata la sessione utente
     * e l’utente viene reindirizzato alla schermata principale.
     * </p>
     *
     * @param evento evento generato dal click sul pulsante login
     */
    @FXML
    private void gestisciAccesso(ActionEvent evento) {
        String username = campoUsername.getText().trim();
        String password = campoPassword.getText();

        if (!validaInput(username, password)) {
            return;
        }

        try {
            Utente utenteAutenticato = autenticaUtente(username, password);

            if (utenteAutenticato != null) {
                SessioneUtente.impostaUtenteCorrente(
                        utenteAutenticato.getNome(),
                        utenteAutenticato.getCognome(),
                        utenteAutenticato.getUsername(),
                        utenteAutenticato.isRuolo(),
                        utenteAutenticato.getCitta(),
                        utenteAutenticato.getStato(),
                        utenteAutenticato.getEmail(),
                        utenteAutenticato.getDataNascita(),
                        utenteAutenticato.getLuogoDomicilio()

                );

                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }

                reindirizzaAllInterfacciaPrincipale(evento);
            } else {
                mostraAvviso("Errore di Autenticazione",
                        "Username o password non corretti!", Alert.AlertType.ERROR);
                campoPassword.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostraAvviso("Errore", "Errore durante l'autenticazione: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Consente accesso senza login (utente ospite).
     *
     * @param evento evento UI
     */
    @FXML
    private void gestisciAccessoSenzaLogin(ActionEvent evento) {
        try {
            SessioneUtente.impostaUtenteCorrente("Ospite", "", "", false, "", "", "", "", "");
            reindirizzaAllInterfacciaPrincipale(evento);
        } catch (Exception e) {
            e.printStackTrace();
            mostraAvviso("Errore", "Errore durante l'accesso: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Apre la schermata di registrazione.
     *
     * @param evento evento UI
     */
    @FXML
    private void gestisciRegistrazione(ActionEvent evento) {
        try {
            caricaScena(evento, "registrazione.fxml", "TheKnife - Registrazione", 700, 600);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della registrazione: " + e.getMessage());
            e.printStackTrace();
            mostraAvviso("Errore", "Impossibile caricare la schermata di registrazione.",
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Valida i dati di input dell’utente.
     *
     * @param username nome utente inserito
     * @param password password inserita
     * @return {@code true} se i campi sono validi, {@code false} altrimenti
     */
    private boolean validaInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            mostraAvviso("Errore", "Inserisci username e password!", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    /**
     * Autentica un utente tramite server RMI.
     *
     * @return utente autenticato o null
     */
    private Utente autenticaUtente(String username, String password) throws Exception {
        // List<Utente> utenti = caricaUtentiDaCSV();
        ArrayList<Utente> utenti = server.getUtenti("SELECT * FROM UTENTI");
        String passwordCifrata = cifraPassword(password);

        return utenti.stream()
                .filter(utente -> utente.getUsername().equals(username) &&
                        utente.getPasswordHash().equals(passwordCifrata))
                .findFirst()
                .orElse(null);
        /*
         * return utenti.stream()
         * .filter(utente -> utente.getUsername().equals(username) &&
         * utente.getPasswordHash().equals(password))
         * .findFirst()
         * .orElse(null);
         */
    }

    /**
     * Crea il file utenti con l’header se non esiste.
     *
     * @param file file CSV da creare
     * @throws IOException se si verifica un errore in scrittura
     */
    private void createUsersFile(File file) throws IOException {
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.append("nome,cognome,username,passwordHash,dataNascita,luogoDomicilio,ruolo\n");
        }
    }

    /**
     * Cifra una password in SHA-256.
     *
     * @param password password in chiaro
     * @return hash SHA-256 della password
     * @throws NoSuchAlgorithmException se l’algoritmo non è disponibile
     */
    private String cifraPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder stringaEsadecimale = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                stringaEsadecimale.append('0');
            }
            stringaEsadecimale.append(hex);
        }
        return stringaEsadecimale.toString();
    }

    /**
     * Reindirizza l’utente all’interfaccia principale.
     *
     * @param evento evento che ha generato il cambio scena
     * @throws IOException se si verifica un errore nel caricamento della scena
     */
    private void reindirizzaAllInterfacciaPrincipale(ActionEvent evento) throws IOException {
        caricaScena(evento, "lista.fxml", "TheKnife - Ricerca Ristoranti", 1024, 768);
    }

    /**
     * Metodo helper per caricare una nuova scena.
     *
     * @param evento        evento che ha generato il cambio scena
     * @param fxml          file FXML da caricare
     * @param titolo        titolo della finestra
     * @param defaultWidth  larghezza di default
     * @param defaultHeight altezza di default
     * @throws IOException se si verifica un errore di caricamento
     */
    private void caricaScena(ActionEvent evento, String fxml, String titolo,
            int defaultWidth, int defaultHeight) throws IOException {

        FXMLLoader caricatore = new FXMLLoader(getClass().getResource(fxml));
        Parent radice = caricatore.load();

        Rectangle2D limitiSchermo = Screen.getPrimary().getVisualBounds();
        double larghezza = Math.min(defaultWidth, limitiSchermo.getWidth() * 0.8);
        double altezza = Math.min(defaultHeight, limitiSchermo.getHeight() * 0.8);

        Scene scena = new Scene(radice, larghezza, altezza);

        // Applica CSS se disponibile
        try {
            if (getClass().getResource(CSS_PATH) != null) {
                scena.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());
            }
        } catch (Exception e) {
            System.out.println("WARNING: Errore nel caricamento CSS: " + e.getMessage());
        }

        Stage palcoscenico = (Stage) ((Node) evento.getSource()).getScene().getWindow();
        palcoscenico.setScene(scena);
        palcoscenico.show();
    }

    /**
     * Mostra un dialogo di avviso all’utente.
     *
     * @param titolo     titolo della finestra di dialogo
     * @param messaggio  testo del messaggio da mostrare
     * @param tipoAvviso tipo di {@link Alert}
     */
    private void mostraAvviso(String titolo, String messaggio, Alert.AlertType tipoAvviso) {
        Alert avviso = new Alert(tipoAvviso);
        avviso.setTitle(titolo);
        avviso.setHeaderText(null);
        avviso.setContentText(messaggio);
        avviso.showAndWait();
    }
}