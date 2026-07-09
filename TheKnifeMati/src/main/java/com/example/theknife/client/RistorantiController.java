package com.example.theknife.client;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

import com.example.theknife.common.DBService;
import com.example.theknife.common.Ristorante;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;        
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Controller della schermata principale di visualizzazione e ricerca dei ristoranti.
 *
 * <p>
 * Questa classe rappresenta il controller principale dell'applicazione e gestisce
 * la visualizzazione della lista dei ristoranti e le relative operazioni di ricerca,
 * filtro e navigazione.
 * </p>
 *
 * <p>
 * In particolare consente di:
 * </p>
 * <ul>
 *     <li>Visualizzare tutti i ristoranti disponibili in una tabella JavaFX</li>
 *     <li>Effettuare ricerche per nome, città e tipo di cucina</li>
 *     <li>Filtrare i risultati per fascia di prezzo</li>
 *     <li>Aprire la schermata dettagli di un ristorante</li>
 *     <li>Gestire la navigazione verso profilo utente o registrazione</li>
 *     <li>Accedere alla dashboard del ristoratore (se autorizzato)</li>
 * </ul>
 *
 * <p>
 * * Il comportamento della UI varia in base al ruolo dell'utente gestito tramite
 * {@code SessioneUtente}. I dati vengono recuperati tramite un servizio remoto
 * {@link DBService}.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */
public class RistorantiController implements Initializable {

    /**
     * La tabella che visualizza i dati dei ristoranti.
     */
    @FXML private TableView<Ristorante> tabellaRistoranti;
    /**
     * Colonna della tabella per il nome del ristorante.
     */
    @FXML private TableColumn<Ristorante, String> colonnaNome;
    /**
     * Colonna della tabella per l'indirizzo del ristorante.
     */
    @FXML private TableColumn<Ristorante, String> colonnaIndirizzo;
    /**
     * Colonna della tabella per la località del ristorante.
     */
    @FXML private TableColumn<Ristorante, String> colonnacitta;
    /**
     * Colonna della tabella per la città del ristorante.
     */
    @FXML private TableColumn<Ristorante, String> colonnastato;
    /**
     * Colonna della tabella per lo stato del ristorante.
     */
    @FXML private TableColumn<Ristorante, String> colonnaPrezzo;
    /**
     * Colonna della tabella per il tipo di cucina del ristorante.
     */
    @FXML private TableColumn<Ristorante, String> colonnaCucina;
    /**
     * Campo di testo per la ricerca per nome del ristorante.
     */
    @FXML private TextField campoRicerca;
    /**
     * Campo di testo per la ricerca per località.
     */
    @FXML private TextField campoRicerca1;
    /**
     * Campo di testo per la ricerca per tipo di cucina.
     */
    @FXML private TextField campoRicerca2;
    /**
     * Pulsante con menu a tendina per selezionare la fascia di prezzo.
     */
    @FXML private MenuButton fasciaPrezzo;
    /**
     * Pulsante che reindirizza alla dashboard del ristoratore.
     */
    @FXML private Button dashboardButton;
    /**
     * Pulsante che reindirizza al profilo utente o alla schermata di registrazione.
     */
    @FXML private Button profiloButton;

    @FXML private HBox headerBar; 

    /** Lista osservabile dei ristoranti caricati. */
    private final ObservableList<Ristorante> listaRistoranti = FXCollections.observableArrayList();
    /** Fascia di prezzo selezionata per il filtro. */
    private String fasciaPrezzoSelezionata = "";
    /** Servizio remoto per l'accesso ai dati. */
    private static DBService server;

    /**
     * Inizializza il controller dopo il caricamento del file FXML.
     *
     * <p>
     * Configura la tabella, inizializza i listener degli eventi, imposta i
     * valori delle ComboBox e carica i dati iniziali dei ristoranti.
     * Inoltre gestisce la visibilità dei componenti in base al ruolo utente.
     * </p>
     *
     * @param location URL del file FXML
     * @param resources risorse di localizzazione
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            server = ClientMain.getServer();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        loadLogo();


        // Collega le colonne della tabella alle proprietà dell'oggetto Ristorante
        colonnaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colonnaIndirizzo.setCellValueFactory(new PropertyValueFactory<>("indirizzo"));
        colonnacitta.setCellValueFactory(new PropertyValueFactory<>("citta"));
        colonnastato.setCellValueFactory(new PropertyValueFactory<>("stato"));
        colonnaPrezzo.setCellValueFactory(new PropertyValueFactory<>("prezzo"));
        colonnaCucina.setCellValueFactory(new PropertyValueFactory<>("cucina"));

        // Imposta la proprietà reorderable su false per ogni colonna
        colonnaNome.setReorderable(false);
        colonnaIndirizzo.setReorderable(false);
        colonnacitta.setReorderable(false);
        colonnastato.setReorderable(false);
        colonnaPrezzo.setReorderable(false);
        colonnaCucina.setReorderable(false);

        // Configura le colonne per larghezza uguale e non ridimensionabili dall'utente
        tabellaRistoranti.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Aggiunge un listener per gestire il doppio click sulla tabella
        tabellaRistoranti.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Ristorante ristorante = tabellaRistoranti.getSelectionModel().getSelectedItem();
                if (ristorante != null) {
                    apriDettagliRistorante(ristorante);
                }
            }

        });

        // Configura il listener per il menu della fascia di prezzo
        for (MenuItem item : fasciaPrezzo.getItems()) {
            item.setOnAction(e -> {
                fasciaPrezzoSelezionata = item.getText();
                fasciaPrezzo.setText(item.getText());
                onCercaClick(e); // Applica il filtro immediatamente
            });
        }

        // Carica i dati iniziali
        refreshData();

        boolean isRistoratore = SessioneUtente.isRistoratore();
        boolean isCliente = SessioneUtente.isCliente();
        boolean isOspite = SessioneUtente.isOspite();
        String username = SessioneUtente.getUsernameUtente();

        if (isOspite || username == null || username.isBlank()) {
            profiloButton.setText("Registrati");
        } else {
            profiloButton.setText(username);
        }

        // Il profilo deve restare sempre visibile: per i ristoratori la dashboard è un pulsante aggiuntivo.
        profiloButton.setVisible(true);
        profiloButton.setManaged(true);

        dashboardButton.setVisible(isRistoratore);
        dashboardButton.setManaged(isRistoratore);

        if (isCliente || isRistoratore) {
            campoRicerca1.setText(SessioneUtente.getCitta());
        }

        this.onCercaClick(null);
            }

        private void loadLogo() {
        try {
        if (headerBar != null) {
            Image logo = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/data/IMG/LOGO.png")
            ));

            ImageView logoView = new ImageView(logo);
            logoView.setFitHeight(60);
            logoView.setPreserveRatio(true);

            headerBar.getChildren().add(0, logoView);
            System.out.println("Logo caricato!");
        } else {
            System.err.println("headerBar è null");
        }
    } catch (Exception e) {
        System.err.println("Errore caricamento logo: " + e.getMessage());
        e.printStackTrace();
    }
}

    /**
     * Carica i dati dei ristoranti dal servizio remoto.
     *
     * <p>
     * Recupera la lista dei ristoranti dal database in base alla città e allo
     * stato dell'utente. I risultati vengono aggiunti alla lista osservabile.
     * </p>
     */
    private void caricadatiSQL() {
        try {
            String citta = (SessioneUtente.isOspite()) ? "" : SessioneUtente.getCitta();
            String stato = (SessioneUtente.isOspite()) ? "" : SessioneUtente.getStato();
            for(Ristorante r: server.getRistoranti(citta,stato)) {
                if(r!=null) listaRistoranti.add(r);
            }
            if(listaRistoranti == null) {
                System.out.println("Non è presente alcun ristorante nel Database");
                return;
            }
            System.out.println("Sono presenti ristoranti nel Database");
        } catch(RemoteException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Apre la schermata dei dettagli di un ristorante.
     *
     * @param ristorante ristorante selezionato
     */
    private void apriDettagliRistorante(Ristorante ristorante) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ristorante-detail.fxml"));
            Parent root = loader.load();
            RistoranteDetailController controller = loader.getController();
            controller.setRistorante(ristorante);

            Scene scene = tabellaRistoranti.getScene();
            Parent rootToRestore = tabellaRistoranti.getScene().getRoot();
            controller.setRootToRestore(rootToRestore);
            controller.setTornaAlMenuPrincipaleCallback(() -> {
                //Scene scene = root.getScene();
                scene.setRoot(rootToRestore);
                this.refreshData();
            });

            scene.setRoot(root);
        } catch (IOException e) {
            mostraErrore("Errore nell'apertura della finestra dei dettagli del ristorante.", e);
        }
    }

    /**
     * Gestisce il click sul pulsante profilo.
     *
     * @param event evento UI
     */
    @FXML
    private void onProfiloClick(ActionEvent event) {
        try {
            FXMLLoader loader;
            if(SessioneUtente.isUtenteLoggato()){
                 loader = new FXMLLoader(getClass().getResource("user-profile.fxml"));
                 
            }else{
                 loader = new FXMLLoader(getClass().getResource("registrazione.fxml"));
            }

            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/data/stile.css")).toExternalForm());
            Window window = tabellaRistoranti.getScene().getWindow();
            Stage stage = (Stage) window;
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            mostraErrore("Errore durante l'apertura del profilo/registrazione", e);
            e.printStackTrace();
        }
    }

     /**
     * Gestisce il click sulla dashboard del ristoratore.
     *
     * @param event evento UI
     */
    @FXML
    private void onDashboardClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ristoratore-dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/data/stile.css")).toExternalForm());
            Window window = tabellaRistoranti.getScene().getWindow();
            Stage stage = (Stage) window;
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostraErrore("Errore durante l'apertura della dashboard", e);
        }
    }

    /**
     * Filtra i ristoranti in base ai criteri di ricerca.
     *
     * @param event evento UI
     */
    @FXML
    private void onCercaClick(ActionEvent event) {
        String ricercaR = campoRicerca.getText().toLowerCase().trim();
        String ricercaL = campoRicerca1.getText().toLowerCase().trim();
        String ricercaC = campoRicerca2.getText().toLowerCase().trim();
        int selezioneCount = fasciaPrezzoSelezionata == null ? 0 : fasciaPrezzoSelezionata.length();

        if (ricercaR.isEmpty() && ricercaL.isEmpty() && ricercaC.isEmpty() && selezioneCount == 0) {
            tabellaRistoranti.setItems(listaRistoranti);
            return;
        }

        ObservableList<Ristorante> risultati = FXCollections.observableArrayList(
                listaRistoranti.filtered(r -> {
                    boolean matchNome = ricercaR.isEmpty() || r.getNome().toLowerCase().contains(ricercaR);
                    boolean matchcitta = ricercaL.isEmpty() || r.getCitta().toLowerCase().startsWith(ricercaL);
                    boolean matchCucina = ricercaC.isEmpty() || r.getCucina().toLowerCase().contains(ricercaC);
                    int prezzoCount = r.getPrezzo() == null ? 0 : r.getPrezzo().length();
                    boolean matchPrezzo = (selezioneCount == 0) || (prezzoCount == selezioneCount);
                    return matchNome && matchcitta && matchCucina && matchPrezzo;
                })
        );

        tabellaRistoranti.setItems(risultati);
    }

     /**
     * Ricarica i dati della tabella ristoranti.
     */
    public void refreshData() {
        listaRistoranti.clear();
        caricadatiSQL();
        tabellaRistoranti.setItems(listaRistoranti);
    }

    /**
     * Mostra un alert di errore.
     *
     * @param messaggio descrizione errore
     * @param e eccezione
     */
    private void mostraErrore(String messaggio, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(messaggio);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}