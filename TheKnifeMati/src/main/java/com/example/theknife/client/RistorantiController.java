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
     * Colonna della tabella per la fascia di prezzo del ristorante.
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
        // Collega le colonne della tabella alle proprietà dell'oggetto Ristorante
        colonnaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colonnaIndirizzo.setCellValueFactory(new PropertyValueFactory<>("indirizzo"));
        colonnacitta.setCellValueFactory(new PropertyValueFactory<>("citta"));
        colonnaPrezzo.setCellValueFactory(new PropertyValueFactory<>("prezzo"));
        colonnaCucina.setCellValueFactory(new PropertyValueFactory<>("cucina"));

        // Imposta la proprietà reorderable su false per ogni colonna
        colonnaNome.setReorderable(false);
        colonnaIndirizzo.setReorderable(false);
        colonnacitta.setReorderable(false);
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
        /* 
        String filePath = "data/michelin_my_maps.csv";
        File csvFile = new File(filePath);

        File parentDir = csvFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
            System.out.println("DEBUG: Cartella 'data' creata.");
        }

        if (!csvFile.exists()) {
            System.err.println("File CSV non trovato. Creazione di un nuovo file.");
            try (FileWriter writer = new FileWriter(csvFile)) {
                writer.append("nome,indirizzo,citta,prezzo,cucina,longitudine,latitudine,numeroTelefono,url,sitoWeb,premio,stellaVerde,servizi,descrizione\n");
                System.out.println("DEBUG: Nuovo file CSV creato con header.");
            } catch (IOException e) {
                System.err.println("Errore durante la creazione del file CSV: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        try (FileReader reader = new FileReader(csvFile);
             CSVReader csvReader = new CSVReader(reader)) {

            csvReader.readNext(); // Salta l'intestazione

            String[] riga;
            while ((riga = csvReader.readNext()) != null) {
                try {
                    String nome = riga[0];
                    String indirizzo = riga[1];
                    String citta = riga[2];
                    String prezzo = riga[3];
                    String cucina = riga[4];
                    double longitudine = Double.parseDouble(riga[5]);
                    double latitudine = Double.parseDouble(riga[6]);
                    String numeroTelefono = riga[7];
                    String url = riga[8];
                    String sitoWeb = riga[9];
                    String premio = riga[10];
                    String stellaVerde = riga[11];
                    String servizi = riga[12];
                    String descrizione = riga[13];

                    Ristorante ristorante = new Ristorante(
                            nome, indirizzo, citta, prezzo, cucina,
                            longitudine, latitudine, numeroTelefono,
                            url, sitoWeb, premio, stellaVerde,
                            servizi, descrizione
                    );
                    listaRistoranti.add(ristorante);
                } catch (Exception e) {
                    System.err.println("Errore nella riga: " + Arrays.toString(riga) + " - " + e.getMessage());
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Errore di I/O o di validazione nel caricamento del CSV.", e);
        }*/
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
        /*new Thread(() -> {
            try {
                var risultato = server.getRistoranti();
                Platform.runLater(() -> {
                    listaRistoranti.setAll(risultato);
                    tabellaRistoranti.setItems(listaRistoranti);
                });
            } catch (RemoteException | SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> mostraErrore("Errore nel caricamento dei ristoranti", e));
            }
        }).start();*/
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