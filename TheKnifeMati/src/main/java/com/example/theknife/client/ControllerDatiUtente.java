package com.example.theknife.client;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.theknife.common.DBService;
import com.example.theknife.common.Utente;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller della schermata di gestione del profilo utente.
 * <p>
 * Permette all’utente autenticato di visualizzare e modificare i propri dati
 * anagrafici (nome, cognome, indirizzo, città, stato e password).
 * </p>
 *
 * <p>
 * I dati vengono recuperati dalla {@link SessioneUtente} e sincronizzati con il
 * server remoto tramite il servizio {@link DBService}.
 * </p>
 *
 * <p>
 * Le modifiche vengono validate lato client e poi inviate al server tramite RMI.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 2.0
 * @since 2026-07-04
 */
public class ControllerDatiUtente implements Initializable {

    @FXML
    private Text campoEmail;
    @FXML
    private Text campoData;
    @FXML
    private Text campoRuolo;

    @FXML
    private TextField nomeField;
    @FXML
    private TextField cognomeField;
    @FXML
    private TextField statoField;
    @FXML
    private TextField cittaField;
    @FXML
    private TextField vecchiaPassword;
    @FXML
    private TextField nuovaPassword;
    @FXML
    private TextField confermaPassword;

    @FXML
    private Button buttonNome;
    @FXML
    private Button buttonCognome;
    @FXML
    private Button buttonStato;
    @FXML
    private Button buttonCitta;
    @FXML
    private Button buttonIndietro;
    @FXML
    private Button buttonLogout;
    @FXML
    private Button buttonPsw;
    @FXML
    private Label usernameLabel;

    /** Riferimento al servizio remoto RMI. */
    private static DBService server;

    /**
     * Applica il foglio di stile CSS principale alla scena, evitando duplicazioni.
     *
     * @param scene La scena a cui applicare lo stile.
     */
    private void addStylesheet(Scene scene) {
        try {
            String cssPath = getClass().getResource("/data/stile.css").toExternalForm();
            if (!scene.getStylesheets().contains(cssPath)) {
                scene.getStylesheets().add(cssPath);
            }
        } catch (Exception e) {
            System.err.println("Impossibile caricare il CSS: " + e.getMessage());
        }
    }

    /**
     * Inizializza il controller dopo il caricamento del file FXML.
     * Carica le icone, recupera il riferimento al server RMI e popola
     * i campi con i dati dell'utente in sessione.
     *
     * @param location  L'URL di localizzazione della risorsa FXML, o null se non
     *                  noto.
     * @param resources Le risorse per la localizzazione, o null se non note.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            server = ClientMain.getServer();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        if (!SessioneUtente.isUtenteLoggato()) {
            System.out.println("ERRORE: Nessun utente loggato! Torno alla home.");
            handleTornaAlMenu();
            return;
        }
        usernameLabel.setText(SessioneUtente.getUsernameUtente());
        campoData.setText(SessioneUtente.getData());
        campoEmail.setText(SessioneUtente.getEmail());
        String ruolo = SessioneUtente.isRistoratore() ? "Ristoratore" : "Cliente";
        campoRuolo.setText(ruolo);

        setText("all");
    }

    /**
     * Popola i campi testuali con i dati dell'utente attualmente in sessione.
     * I valori vengono sempre letti da {@link SessioneUtente}.
     */
    public void setText(String field) {
        Utente user = null;
        try {
            user = server.getUtenteByUsername(SessioneUtente.getUsernameUtente());
        } catch (RemoteException | SQLException e) {
            e.printStackTrace();
        }
        if(field.equals("all")) {
            nomeField.setText(user.getNome());
            cognomeField.setText(user.getCognome());
            statoField.setText(user.getStato());
            cittaField.setText(user.getCitta());
        } else if(field.equals("nome")) {
            nomeField.setText(user.getNome());
        } else if(field.equals("cognome")) {
            cognomeField.setText(user.getCognome());
        } else if(field.equals("citta")) {
            cittaField.setText(user.getCitta());
        } else if(field.equals("stato")) {
            statoField.setText(user.getStato());
        } else if(field.equals("password_hash")) {
            clearPsw();
        }
    }

    /**
     * Estrae il cognome dal nome completo restituito da
     * {@link SessioneUtente#getNomeCompleto()}.
     * NOTA: se {@code SessioneUtente} viene esteso con
     * {@code getNome()}/{@code getCognome()} dedicati,
     * questo metodo può essere sostituito da una chiamata diretta.
     *
     * @return Il cognome dell'utente, o stringa vuota se non determinabile.
     */
    private String cognomeDaNomeCompleto() {
        String nomeCompleto = SessioneUtente.getNomeCompleto();
        if (nomeCompleto == null || !nomeCompleto.contains(" ")) {
            return "";
        }
        return nomeCompleto.substring(nomeCompleto.indexOf(" ") + 1).trim();
    }

    /**
     * Mostra un alert all’utente.
     *
     * @param titolo titolo finestra
     * @param messaggio contenuto
     * @param tipo tipo di alert
     */

    private void mostraAvviso(String titolo, String messaggio, Alert.AlertType tipoAvviso) {
        Alert avviso = new Alert(tipoAvviso);
        avviso.setTitle(titolo);
        avviso.setHeaderText(null);
        avviso.setContentText(messaggio);
        avviso.showAndWait();
    }

    /**
     * Gestisce la modifica del nome dell'utente.
     * <p>
     * Se il nome inserito è diverso da quello attuale, aggiorna il dato sul server
     * e nella sessione utente, altrimenti mostra un messaggio di errore.
     * </p>
     *
     * @throws IOException se si verifica un errore durante l'aggiornamento dei dati.
     */
    @FXML
    private void cambiaNome() throws IOException {
        if (SessioneUtente.getNome().equals(nomeField.getText())) {
            mostraAvviso("Errore di modifica",
                    "Nome non è stato cambiato", Alert.AlertType.ERROR);
        } else {
            changeData("nome", nomeField.getText());
            SessioneUtente.setNome(nomeField.getText());
        }
    }

    /**
     * Gestisce la modifica del cognome dell'utente.
     * <p>
     * Se il cognome inserito è diverso da quello attuale, aggiorna il dato sul server
     * e nella sessione utente, altrimenti mostra un messaggio di errore.
     * </p>
     *
     * @throws IOException se si verifica un errore durante l'aggiornamento dei dati.
     */
    @FXML
    private void cambiaCognome() throws IOException {
        if (SessioneUtente.getCognome().equals(cognomeField.getText())) {
            mostraAvviso("Errore di modifica",
                    "Cognome non è stato cambiato", Alert.AlertType.ERROR);
        } else {
            changeData("cognome", cognomeField.getText());
            SessioneUtente.setCognome(cognomeField.getText());
        }
    }

    /**
     * Gestisce la modifica dello stato di residenza dell'utente.
     * <p>
     * Se lo stato inserito è diverso da quello attuale, aggiorna il dato sul server
     * e nella sessione utente, altrimenti mostra un messaggio di errore.
     * </p>
     *
     * @throws IOException se si verifica un errore durante l'aggiornamento dei dati.
     */
    @FXML
    private void cambiaStato() throws IOException {
        if (SessioneUtente.getStato().equals(statoField.getText())) {
            mostraAvviso("Errore di modifica",
                    "Stato non è stato cambiato", Alert.AlertType.ERROR);
        } else {
            changeData("stato", statoField.getText());
            SessioneUtente.setStato(statoField.getText());
        }
    }

    /**
     * Gestisce la modifica della città di residenza dell'utente.
     * <p>
     * Se la città inserita è diversa da quella attuale, aggiorna il dato sul server
     * e nella sessione utente, altrimenti mostra un messaggio di errore.
     * </p>
     *
     * @throws IOException se si verifica un errore durante l'aggiornamento dei dati.
     */
    @FXML
    private void cambiaCitta() throws IOException {
        if (SessioneUtente.getCitta().equals(cittaField.getText())) {
            mostraAvviso("Errore di modifica",
                    "Città non è stato cambiato", Alert.AlertType.ERROR);
        } else {
            changeData("citta", cittaField.getText());
            SessioneUtente.setCitta(cittaField.getText());
        }
    }

    /**
     * Gestisce la modifica della password dell'utente.
     * <p>
     * Verifica che la vecchia password inserita sia corretta, valida la complessità 
     * della nuova password e la sua conferma, e infine aggiorna la password sul server.
     * </p>
     *
     * @throws NoSuchAlgorithmException se l'algoritmo di hashing SHA-256 non è disponibile.
     */
    @FXML
    private void cambiaPsw() throws NoSuchAlgorithmException {
        String vPassword = cifraPassword(vecchiaPassword.getText());

        try {
            if(!server.getUtenteByUsername(SessioneUtente.getUsernameUtente()).getPasswordHash().equals(vPassword)) {
                mostraAvviso("Errori di Validazione", "Password inserita non è corretta", Alert.AlertType.WARNING);
            }
        } catch (RemoteException | SQLException e) {
            System.err.println("Errore nella modifica utente: " + e.getMessage());
        }

        String password = nuovaPassword.getText();
        List<String> errori = new ArrayList<>();
        String confermaPsw = confermaPassword.getText();

        if (password.isEmpty()) {
            errori.add("Non inserita la password nuova");
        } else if (password.length() < 8) {
            errori.add("La password deve avere almeno 8 caratteri");
        } else {
            // Controllo complessità
            if (!password.matches(".*[A-Z].*")) {
                errori.add("La password deve contenere almeno una lettera maiuscola");
            }
            if (!password.matches(".*[a-z].*")) {
                errori.add("La password deve contenere almeno una lettera minuscola");
            }
            if (!password.matches(".*\\d.*")) {
                errori.add("La password deve contenere almeno un numero");
            }
            if (!password.matches(".*[^a-zA-Z0-9].*")) {
                errori.add("La password deve contenere almeno un carattere speciale");
            }
        }

        if(errori.isEmpty()) {
            if(confermaPsw.isEmpty()) {
                errori.add("La password non è stata confermata");
            } else if (!password.equals(confermaPsw)) {
                errori.add("La password non è uguale nei campi");
            }
        }

        if (!errori.isEmpty()) {
            String messaggioErrore = "Correggi i seguenti errori:\n\n" + String.join("\n", errori);
            mostraAvviso("Errori di Validazione", messaggioErrore, Alert.AlertType.WARNING);
        } else {
            try {
                changeData("password_hash", cifraPassword(password));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Ripulisce i campi di testo relativi all'inserimento e alla conferma della nuova password.
     */
    private void clearPsw() {
        vecchiaPassword.setText("");
        confermaPassword.setText("");
        nuovaPassword.setText("");
    }

    /**
     * Converte una password in hash SHA-256.
     *
     * @param password password in chiaro
     * @return hash esadecimale
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
     * Modifica un campo dell’utente sul server.
     *
     * @param field campo da modificare
     * @param set nuovo valore
     */
    
    private void changeData(String field, String set) throws IOException {
        try {
            server.modifyUsernameCampo(SessioneUtente.getUsernameUtente(), field, set);
        } catch (RemoteException | SQLException e) {
            System.err.println("Errore nella modifica utente: " + e.getMessage());
        }

        setText(field);
        
    }

    /**
     * Effettua il logout dell'utente e torna alla schermata di login.
     *
     * @throws IOException se il file FXML non viene trovato.
     */
    @FXML
    private void switchHomeNotLogged() throws IOException {
        SessioneUtente.eseguiLogout();
        URL resourceUrl = getClass().getResource("login.fxml");
        if (resourceUrl == null) {
            throw new IOException("FXML file not found: login.fxml");
        }
        FXMLLoader loader = new FXMLLoader(resourceUrl);
        Parent root = loader.load();
        Stage currentStage = (Stage) buttonLogout.getScene().getWindow();
        Scene scene = new Scene(root);
        addStylesheet(scene);
        currentStage.setScene(scene);
        currentStage.show();
    }

    /**
     * Riporta l'utente alla schermata principale con la lista dei ristoranti
     * ("lista.fxml").
     */
    @FXML
    private void handleTornaAlMenu() {
        try {
            URL resourceUrl = getClass().getResource("user-profile.fxml");
            if (resourceUrl == null) {
                throw new IOException("FXML file not found: user-profile.fxml");
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            Stage currentStage = (Stage) buttonIndietro.getScene().getWindow();
            Scene scene = new Scene(root);
            addStylesheet(scene);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            System.err.println("Error loading main menu: " + e.getMessage());
            showError("Errore", "Impossibile tornare al menu principale: " + e.getMessage());
        }
    }

    /**
     * Mostra una finestra di dialogo di errore all'utente.
     *
     * @param header  Il titolo dell'errore.
     * @param content Il messaggio descrittivo dell'errore.
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}