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
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller per la schermata di gestione del profilo dell'utente loggato.
 * <p>
 * Questa classe si occupa esclusivamente della visualizzazione e della modifica
 * dei dati anagrafici dell'utente (nome, cognome, email, indirizzo, città,
 * stato,
 * password). La gestione di recensioni e ristoranti preferiti non fa parte di
 * questo controller.
 * </p>
 * <p>
 * I dati mostrati a schermo provengono da {@link SessioneUtente}, mentre le
 * operazioni di salvataggio avvengono tramite il servizio remoto
 * {@link DBService}.
 * </p>
 *
 * @author TheKnifeTeam
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
    private TextField indirizzoField;
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
    private Button buttonIndirizzo;
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
     * Carica un'icona da risorsa e la assegna, rendendola visibile, a un
     * {@link ImageView}.
     *
     * @param view         L'ImageView di destinazione.
     * @param resourcePath Il percorso classpath dell'immagine.
     */
    private void loadIcona(ImageView view, String resourcePath) {
        URL risorsa = getClass().getResource(resourcePath);
        if (risorsa != null && view != null) {
            view.setImage(new javafx.scene.image.Image(risorsa.toExternalForm()));
            view.setVisible(true);
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
            indirizzoField.setText(user.getLuogoDomicilio());
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
        } else if(field.equals("indirizzo")) {
            indirizzoField.setText(user.getLuogoDomicilio());
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
     * Recupera dal server l'oggetto {@link Utente} completo corrispondente
     * all'utente in sessione, necessario per preservare i campi non presenti
     * in {@link SessioneUtente} (es. password hash, data di nascita) al momento
     * del salvataggio.
     *
     * @return L'utente completo, oppure {@code null} se non trovato o in caso di
     *         errore.
     */
    private Utente recuperaUtenteCorrente() {
        try {
            ArrayList<Utente> risultati = server.getUtenti(SessioneUtente.getUsernameUtente());
            for (Utente u : risultati) {
                if (u.getUsername().equals(SessioneUtente.getUsernameUtente())) {
                    return u;
                }
            }
        } catch (RemoteException | SQLException e) {
            System.err.println("Errore nel recupero dell'utente corrente: " + e.getMessage());
        }
        return null;
    }

    /**
     * Salva sul server l'utente aggiornato e allinea la sessione corrente
     * ai nuovi dati anagrafici (esclusa la password, non mantenuta in sessione).
     *
     * @param utenteAggiornato L'utente con i dati aggiornati da salvare.
     * @return {@code true} se il salvataggio è andato a buon fine.
     */
    /*
     * private boolean salvaUtente(Utente utenteAggiornato) {
     * try {
     * Boolean esito = server.setUtente(utenteAggiornato);
     * if (Boolean.TRUE.equals(esito)) {
     * SessioneUtente.impostaUtenteCorrente(
     * utenteAggiornato.getNome(),
     * utenteAggiornato.getCognome(),
     * utenteAggiornato.getUsername(),
     * utenteAggiornato.isRuolo(),
     * utenteAggiornato.getCitta(),
     * utenteAggiornato.getStato(),
     * utenteAggiornato.getEmail());
     * return true;
     * }
     * } catch (RemoteException e) {
     * System.err.println("Errore di connessione al server durante il salvataggio: "
     * + e.getMessage());
     * }
     * return false;
     * }
     */

    // ----- Handler dei pulsanti di modifica campo -----

    /*
     * @FXML
     * private void changeNomeData() throws IOException {
     * changeData("Nome");
     * }
     * 
     * @FXML
     * private void changeCognomeData() throws IOException {
     * changeData("Cognome");
     * }
     * 
     * @FXML
     * private void changeCittaData() throws IOException {
     * changeData("Citta");
     * }
     * 
     * @FXML
     * private void changeIndirizzoData() throws IOException {
     * changeData("Indirizzo");
     * }
     * 
     * @FXML
     * private void changeStatoData() throws IOException {
     * changeData("Stato");
     * }
     * 
     * @FXML
     * private void changeEamilData() throws IOException {
     * changeData("Email");
     * }
     * 
     * @FXML
     * private void changePasswordData() throws IOException {
     * changePsw();
     * }
     */

    private void mostraAvviso(String titolo, String messaggio, Alert.AlertType tipoAvviso) {
        Alert avviso = new Alert(tipoAvviso);
        avviso.setTitle(titolo);
        avviso.setHeaderText(null);
        avviso.setContentText(messaggio);
        avviso.showAndWait();
    }

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

    @FXML
    private void cambiaIndirizzo() throws IOException {
        if (SessioneUtente.getIndirizzo().equals(indirizzoField.getText())) {
            mostraAvviso("Errore di modifica",
                    "Indirizzo non è stato cambiato", Alert.AlertType.ERROR);
        } else {
            changeData("indirizzo", indirizzoField.getText());
            SessioneUtente.setIndirizzo(indirizzoField.getText());
        }
    }

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

    private void clearPsw() {
        vecchiaPassword.setText("");
        confermaPassword.setText("");
        nuovaPassword.setText("");
    }


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
     * Apre la finestra di dialogo per la modifica di un singolo campo anagrafico
     * dell'utente, quindi salva la modifica sul server tramite
     * {@link DBService#setUtente}.
     * <p>
     * NOTA: si assume che {@code ControllerChangeDataUser} esponga un metodo
     * {@code getNuovoValore()} che restituisce il nuovo valore inserito
     * (o {@code null} se l'utente ha annullato l'operazione).
     * </p>
     *
     * @param field Il nome del campo da modificare ("Nome", "Cognome", "Citta",
     *              "Indirizzo", "Stato", "Email").
     * @throws IOException se il file FXML del dialogo non viene trovato.
     */
    private void changeData(String field, String set) throws IOException {
        try {
            server.modifyUsernameCampo(SessioneUtente.getUsernameUtente(), field, set);
        } catch (RemoteException | SQLException e) {
            System.err.println("Errore nella modifica utente: " + e.getMessage());
        }

        setText(field);
        /*
         * FXMLLoader loader = new
         * FXMLLoader(getClass().getResource("ChangeDataUser.fxml"));
         * Parent root = loader.load();
         * Stage smallStage = new Stage();
         * Scene scene = new Scene(root, 433, 482);
         * addStylesheet(scene);
         * smallStage.setScene(scene);
         * smallStage.initModality(Modality.APPLICATION_MODAL);
         * 
         * ControllerChangeDataUser controller = loader.getController();
         * controller.setMyStage(smallStage);
         * controller.setValue(field, false);
         * smallStage.showAndWait();
         * 
         * String nuovoValore = controller.getNuovoValore();
         * if (nuovoValore == null) {
         * // Operazione annullata dall'utente.
         * return;
         * }
         * 
         * Utente utenteCorrente = recuperaUtenteCorrente();
         * if (utenteCorrente == null) {
         * showError("Errore", "Impossibile recuperare i dati dell'utente dal server.");
         * return;
         * }
         * 
         * switch (field) {
         * case "Nome" -> utenteCorrente.setNome(nuovoValore);
         * case "Cognome" -> utenteCorrente.setCognome(nuovoValore);
         * case "Citta" -> utenteCorrente.setCitta(nuovoValore);
         * case "Indirizzo" -> utenteCorrente.setLuogoDomicilio(nuovoValore);
         * case "Stato" -> utenteCorrente.setStato(nuovoValore);
         * case "Email" -> utenteCorrente.setEmail(nuovoValore);
         * default -> {
         * return;
         * }
         * }
         * 
         * if (salvaUtente(utenteCorrente)) {
         * setText();
         * } else {
         * showError("Errore", "Impossibile salvare le modifiche sul server.");
         * }
         */

    }

    /**
     * Apre la finestra di dialogo per la modifica della password, quindi salva
     * il nuovo hash sul server.
     * <p>
     * NOTA: si assume che {@code ControllerChangePasswordUser} esponga un metodo
     * {@code getNuovaPasswordHash()} che restituisce il nuovo hash calcolato
     * (o {@code null} se l'operazione è stata annullata).
     * </p>
     *
     * @throws IOException se il file FXML del dialogo non viene trovato.
     */
    /*
     * private void changePsw() throws IOException {
     * FXMLLoader loader = new
     * FXMLLoader(getClass().getResource("ChangePasswordUser.fxml"));
     * Parent root = loader.load();
     * 
     * Stage smallStage = new Stage();
     * Scene scene = new Scene(root, 433, 545);
     * addStylesheet(scene);
     * smallStage.setTitle("Cambiare Password");
     * smallStage.setScene(scene);
     * smallStage.initModality(Modality.APPLICATION_MODAL);
     * 
     * ControllerChangePasswordUser controller = loader.getController();
     * controller.setMyStage(smallStage);
     * smallStage.showAndWait();
     * 
     * String nuovaPasswordHash = controller.getNuovaPasswordHash();
     * if (nuovaPasswordHash == null) {
     * return;
     * }
     * 
     * Utente utenteCorrente = recuperaUtenteCorrente();
     * if (utenteCorrente == null) {
     * showError("Errore", "Impossibile recuperare i dati dell'utente dal server.");
     * return;
     * }
     * utenteCorrente.setPasswordHash(nuovaPasswordHash);
     * 
     * if (!salvaUtente(utenteCorrente)) {
     * showError("Errore", "Impossibile aggiornare la password sul server.");
     * }
     * }
     */

    // ----- Navigazione -----

    /**
     * Riporta l'utente alla schermata del ristoratore
     * ("ristoratore-dashboard.fxml").
     *
     * @throws IOException se il file FXML non viene trovato.
     */
    /*
     * @FXML
     * private void switchRistoratore() throws IOException {
     * URL resourceUrl = getClass().getResource("ristoratore-dashboard.fxml");
     * if (resourceUrl == null) {
     * throw new IOException("FXML file not found: ristoratore-dashboard.fxml");
     * }
     * FXMLLoader loader = new FXMLLoader(resourceUrl);
     * Parent root = loader.load();
     * Stage currentStage = (Stage) propriRistoranti.getScene().getWindow();
     * Scene scene = new Scene(root);
     * addStylesheet(scene);
     * currentStage.setScene(scene);
     * currentStage.show();
     * }
     */

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