package com.example.theknife.client;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.example.theknife.common.DBService;
import com.example.theknife.common.Ristorante;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller per la gestione dell'interfaccia di inserimento di un nuovo ristorante.
 * <p>
 * Questa classe collega la view FXML con la logica di business per la creazione
 * e il salvataggio di un ristorante nel sistema.
 * </p>
 *
 * Gestisce:
 * - inserimento dati ristorante
 * - validazione input
 * - creazione oggetto dominio {@link Ristorante}
 * - salvataggio tramite {@link DBService}
 * - navigazione verso dashboard tramite callback
 *
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */
public class RistoranteInputController implements Initializable {
    /** Campo di testo per il nome del ristorante. */
    @FXML private TextField nomeField;
    /** Campo di testo per l'indirizzo del ristorante. */
    @FXML private TextField indirizzoField;
    /** Campo di testo per la località del ristorante. */
    @FXML private TextField cittaField;
    /** ComboBox per la selezione della fascia di prezzo. */
    @FXML private ComboBox<String> prezzoComboBox;
    /** ListView per la selezione multipla dei tipi di cucina. */
    @FXML private ListView<String> cucinaListView;
    /** ComboBox per la selezione del premio Michelin. */
    @FXML private ComboBox<String> premiComboBox;
    /** CheckBox per indicare la presenza della stella verde. */
    @FXML private CheckBox stellaVerdeCheckBox;
    /** Campo di testo per la longitudine. */
    @FXML private TextField longitudineField;
    /** Campo di testo per la latitudine. */
    @FXML private TextField latitudineField;
    /** Campo di testo per il numero di telefono. */
    @FXML private TextField telefonoField;
    /** Campo di testo per l'URL del ristorante. */
    @FXML private TextField urlField;
    /** Campo di testo per il sito web del ristorante. */
    @FXML private TextField sitoWebField;
    /** ListView per la selezione multipla dei servizi offerti. */
    @FXML private ListView<String> checkBoxServizi;
    /** Area di testo per la descrizione del ristorante. */
    @FXML private TextArea descrizioneArea;
    /** Pulsante per avviare il salvataggio dei dati. */
    @FXML private Button salvaButton;
    /** Pulsante per annullare l'operazione. */
    @FXML private Button annullaButton;

    private final GestioneRistorante gestioneRistorante = GestioneRistorante.getInstance();
    private final GestionePossessoRistorante ownershipService = GestionePossessoRistorante.getInstance();

     /** Callback ritorno dashboard */
    private Runnable tornaAllaDashboardCallback;
    /** Callback aggiornamento lista ristoranti */
    private Runnable aggiornaDatabaseRistorantiCallback;

    /** Servizio remoto database */
    private static DBService server;

    /**
     * Imposta il callback da eseguire per tornare alla dashboard principale.
     * Questo metodo viene utilizzato per implementare la navigazione tra le scene.
     *
     * @param callback L'oggetto {@link Runnable} che esegue l'operazione di ritorno.
     */
    public void setTornaAllaDashboardCallback(Runnable callback) {
        this.tornaAllaDashboardCallback = callback;
    }

    /**
     * Imposta il callback per aggiornare i dati dei ristoranti.
     *
     * @param callback azione da eseguire per aggiornare la vista
     */
    public void setAggiornaDatabaseRistorantiCallback(Runnable callback) {
        this.aggiornaDatabaseRistorantiCallback = callback;
    }

    /**
     * Inizializza la view JavaFX dopo il caricamento del file FXML.
     *
     * <p>Configura:
     * <ul>
     *     <li>valori delle ComboBox</li>
     *     <li>liste di cucine e servizi</li>
     *     <li>modalità di selezione multipla</li>
     *     <li>connessione al server remoto</li>
     * </ul>
     * </p>
     *
     * @param location URL del file FXML
     * @param resources risorse di localizzazione
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prezzoComboBox.setItems(FXCollections.observableArrayList("€", "€€", "€€€", "€€€€"));
        try {
            try {
                server = ClientMain.getServer();
            } catch (NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        cucinaListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cucinaListView.setItems(FXCollections.observableArrayList(
                "Creativa", "Contemporanea", "Coreana", "Francese", "Italiana",
                "Giapponese", "Cinese", "Indiana", "Mediterranea", "Fusion",
                "Americana", "Messicana", "Thailandese", "Vegetariana", "Vegana",
                "Frutti di mare", "Steakhouse", "Tradizionale", "Moderna", "Internazionale",
                "Di Mare", "Contadina", "Alpina", "Siciliana", "Toscana", "Innovativa",
                "Di Stagione", "Classica"
        ));

        premiComboBox.setItems(FXCollections.observableArrayList(
                "Nessun premio", "1 Stella", "2 Stelle",
                "3 Stelle", "Bib Gourmand"
        ));
        premiComboBox.setValue("Nessun premio");

        checkBoxServizi.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        checkBoxServizi.setItems(FXCollections.observableArrayList(
                "Aria condizionata", "Giardino o parco", "Interessante carta dei vini",
                "Terrazza", "Accesso per sedie a rotelle", "Vista magnifica",
                "Ristorante con menu vegetariani", "Delivery", "Prenotazione on-line", "Cena al bancone",
                "Parcheggio"
        ));
    }

    /**
     * Gestisce il salvataggio di un nuovo ristorante.
     *
     * <p>
     * Il metodo valida i dati, crea un oggetto {@link Ristorante},
     * verifica eventuali duplicati e salva il ristorante nel database remoto.
     * </p>
     */
    @FXML
    private void handleSalva() {
        if (!validaInput()) {
            return;
        }

        try {
            double longitudine = longitudineField.getText().isEmpty() ? 0.0 : Double.parseDouble(longitudineField.getText());
            double latitudine = latitudineField.getText().isEmpty() ? 0.0 : Double.parseDouble(latitudineField.getText());

            String tel = telefonoField.getText().trim();
            String nome = nomeField.getText().trim();
            try {
                Ristorante r = server.getRistorante(tel);
                if(r != null) {
                    if (r.getNumeroTelefono() != null) {
                        mostraErrore("Errore", "Esiste già un ristorante con questo numero di telefono. Scegli un numero di telefono diverso.");
                        return;
                    }
                }
            } catch(RemoteException | SQLException e) {
                e.printStackTrace();
            }

            String cucine = cucinaListView.getSelectionModel().getSelectedItems()
                    .stream()
                    .collect(Collectors.joining(", "));

            String servizi = checkBoxServizi.getSelectionModel().getSelectedItems()
                    .stream()
                    .collect(Collectors.joining(", "));

            Ristorante nuovoRistorante = new Ristorante(
                    telefonoField.getText().trim(),
                    nome,
                    indirizzoField.getText().trim(),
                    "", // stato non disponibile nel form
                    cittaField.getText().trim(),
                    servizi,
                    //urlField.getText().trim(),
                    sitoWebField.getText().trim(),
                    premiComboBox.getValue(),
                    cucine,
                    stellaVerdeCheckBox.isSelected() ? 0.0 : 1.0,
                    prezzoComboBox.getValue(),
                    false, // prenotazione
                    false, // consegna
                    descrizioneArea.getText().trim(),
                    SessioneUtente.getUsernameUtente()
            );

            aggiungiRistoranteAlDB(nuovoRistorante);

            String username = SessioneUtente.getUsernameUtente();
            if (username != null && SessioneUtente.isRistoratore()) {
                ownershipService.associaRistoranteAProprietario(nome, username);
            }

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Successo");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Il ristorante è stato aggiunto con successo!");
            successAlert.showAndWait();

            System.out.println("Debug: Salvataggio ristorante completato");

            // Aggiorna i servizi dopo il salvataggio
            //gestioneRistorante.caricaRistoranti();
            GestionePossessoRistorante.getInstance().refreshOwnershipData();

            // Esegue il callback per tornare alla dashboard
            if (tornaAllaDashboardCallback != null) {
                tornaAllaDashboardCallback.run();
            }

        } catch (Exception e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
            e.printStackTrace();
            mostraErrore("Errore", e.getMessage());
        }
    }

    /**
     * Valida i dati inseriti dall'utente nel form.
     *
     * @return true se i dati sono validi, false altrimenti
     */
    private boolean validaInput() {
        StringBuilder errori = new StringBuilder();
        if (nomeField.getText().trim().isEmpty()) {
            errori.append("- Il nome è obbligatorio\n");
        }
        if (indirizzoField.getText().trim().isEmpty()) {
            errori.append("- L'indirizzo è obbligatorio\n");
        }
        if (cittaField.getText().trim().isEmpty()) {
            errori.append("- La località è obbligatoria\n");
        }
        if (prezzoComboBox.getValue() == null) {
            errori.append("- La fascia di prezzo è obbligatoria\n");
        }
        if (cucinaListView.getSelectionModel().getSelectedItems().isEmpty()) {
            errori.append("- Il tipo di cucina è obbligatorio\n");
        }
        if (!longitudineField.getText().trim().isEmpty() || !latitudineField.getText().trim().isEmpty()) {
            try {
                if (!longitudineField.getText().trim().isEmpty()) {
                    Double.parseDouble(longitudineField.getText());
                }
                if (!latitudineField.getText().trim().isEmpty()) {
                    Double.parseDouble(latitudineField.getText());
                }
            } catch (NumberFormatException e) {
                errori.append("- Le coordinate devono essere numeri validi\n");
            }
        }
        if (errori.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Correggi i seguenti errori:");
            alert.setContentText(errori.toString());
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Gestisce il pulsante annulla tornando alla dashboard.
     */
    @FXML
    private void handleAnnulla() {
        if (tornaAllaDashboardCallback != null) {
            tornaAllaDashboardCallback.run();
        }
    }

    /**
     * Mostra un messaggio di errore all'utente.
     *
     * @param titolo titolo della finestra
     * @param messaggio contenuto dell'errore
     */
    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    /**
     * Salva il ristorante nel database remoto tramite {@link DBService}.
     *
     * @param ristorante oggetto da salvare
     * @throws IOException in caso di errore di comunicazione
     */
    private void aggiungiRistoranteAlDB(Ristorante ristorante) throws IOException {
        Boolean risultato = server.setRistorante(ristorante, SessioneUtente.getUsernameUtente());
            // Usa un percorso esterno, che si trova nella stessa directory del JAR
            
           if (risultato != null && risultato) {
                System.out.println("DEBUG: Ristorante salvato nel database: " + ristorante.toString());
            
            } else {
                System.err.println("ERRORE: Impossibile salvare il ristorante nel database: " + ristorante.toString());
                
            }
    }
}