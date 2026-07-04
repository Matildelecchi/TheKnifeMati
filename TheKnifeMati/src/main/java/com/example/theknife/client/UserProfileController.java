package com.example.theknife.client;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.theknife.common.DBService;
import com.example.theknife.common.Recensione;
import com.example.theknife.common.Ristorante;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller per la gestione del profilo utente.
 * <p>
 * Questa classe gestisce l'interfaccia utente dedicata al profilo personale,
 * mostrando informazioni dell'utente, i ristoranti preferiti e le recensioni
 * scritte. Fornisce inoltre i meccanismi per il logout, la navigazione
 * verso altre schermate e l'accesso alla dashboard del ristoratore.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */
public class UserProfileController implements Initializable {
    /**
     * Etichetta che visualizza il nome completo dell'utente.
     */
    @FXML private Label nomeLabel;
    /**
     * Etichetta che mostra il ruolo dell'utente.
     */
    @FXML private Label ruoloLabel;
    /**
     * Tabella per visualizzare le recensioni scritte dall'utente.
     */
    @FXML private TableView<Recensione> recensioniTable;
    /**
     * Colonna della tabella delle recensioni per il nome del ristorante.
     */
    @FXML private TableColumn<Recensione, String> ristoranteColumn;
    /**
     * Colonna della tabella delle recensioni per il numero di stelle.
     */
    @FXML private TableColumn<Recensione, Integer> stelleColumn;
    /**
     * Colonna della tabella delle recensioni per il testo della recensione.
     */
    @FXML private TableColumn<Recensione, String> testoColumn;
    /**
     * Colonna della tabella delle recensioni per la data della recensione.
     */
    @FXML private TableColumn<Recensione, String> dataColumn;

    @FXML private TableColumn<Recensione, String> titoloColumn;
    /**
     * Contenitore per la sezione dei ristoranti preferiti, visibile solo per i clienti.
     */
    @FXML private VBox preferitiBox;
    /**
     * Pulsante per eseguire il logout dall'applicazione.
     */
    @FXML private Button logoutButton;
    /**
     * Pulsante per tornare alla schermata del menu principale.
     */
    @FXML private Button tornaalMenuButton;
    /**
     * Pulsante per accedere alla dashboard del ristoratore, visibile solo per gli utenti con ruolo "ristoratore".
     */
    @FXML private Button dashboardButton;

    @FXML private Button userDataButton;
    
    //@FXML private Label emailLabel;
    //@FXML private Label statoLabel;

    //private final GestioneRecensioni gestioneRecensioni = GestioneRecensioni.getInstance();
    //private final GestionePreferiti gestionePreferiti = com.example.theknife.client.GestionePreferiti.getInstance();
    //private final GestioneRistorante gestioneRistorante = GestioneRistorante.getInstance();

    @FXML private TableView<Ristorante> preferitiList;

    @FXML private TableColumn<Ristorante, String> ristoranteColumn1;

    @FXML private TableColumn<Ristorante, String> cittaColumn;

    @FXML private TableColumn<Ristorante, String> stelleColumn1;

    @FXML private TableColumn<Ristorante, String> statoColumn;

    @FXML private TableColumn<Ristorante, String> cucinaColumn;
    
    //private final GestioneRecensioni gestioneRecensioni = GestioneRecensioni.getInstance();
    //private final GestionePreferiti gestionePreferiti = com.example.theknife.client.GestionePreferiti.getInstance();
    //private final GestioneRistorante gestioneRistorante = GestioneRistorante.getInstance();

    private static DBService server;
    /**
     * Applica il foglio di stile CSS principale alla scena per uniformare l'aspetto dell'interfaccia utente.
     * Questo metodo verifica prima se lo stile è già stato applicato per evitare duplicazioni.
     *
     * @param scene La scena JavaFX alla quale applicare lo stile.
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
     * Inizializza il controller dopo che il file FXML è stato caricato.
     * Questo metodo viene chiamato automaticamente dal framework JavaFX.
     * Configura le informazioni e le viste del profilo utente in base al ruolo dell'utente,
     * imposta i gestori degli eventi per i pulsanti e le tabelle, e carica i dati iniziali.
     *
     * @param location L'URL utilizzato per risolvere percorsi relativi per l'oggetto root, o {@code null} se non noto.
     * @param resources Le risorse utilizzate per localizzare l'oggetto root, o {@code null} se l'oggetto root non è stato localizzato.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            server = ClientMain.getServer();
        } catch(RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        // Imposta le informazioni personali dell'utente nelle etichette.
        nomeLabel.setText(SessioneUtente.getNomeCompleto());
        ruoloLabel.setText(
         SessioneUtente.isRistoratore()
                ? "Ruolo: Ristoratore"
                : "Ruolo: Cliente"
        );
        //emailLabel.setText(SessioneUtente.getEmail());
        //statoLabel.setText(SessioneUtente.getStato());

        // Configura le colonne della tabella delle recensioni associandole ai campi della classe Recensione.
        //ristoranteColumn.setCellValueFactory(new PropertyValueFactory<>("num_tel"));
        ristoranteColumn.setCellValueFactory(cellData -> {
            String numTel = cellData.getValue().getRistoranteTel();
            String nome = numTel;
            try {
                Ristorante r = server.getRistorante(numTel);
                if (r != null) {
                    nome = r.getNome();
                }
            } catch (RemoteException | SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty(nome);
        });
        titoloColumn.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        stelleColumn.setCellValueFactory(new PropertyValueFactory<>("stelle"));
        testoColumn.setCellValueFactory(new PropertyValueFactory<>("testo"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));

        // Disabilita il riordino delle colonne
        ristoranteColumn.setReorderable(false);
        stelleColumn.setReorderable(false);
        testoColumn.setReorderable(false);
        dataColumn.setReorderable(false);
        titoloColumn.setReorderable(false);

        // Configura le colonne della tabella delle recensioni associandole ai campi della classe Recensione.
        ristoranteColumn1.setCellValueFactory(new PropertyValueFactory<>("nome"));
        stelleColumn1.setCellValueFactory(new PropertyValueFactory<>("stelle"));
        cittaColumn.setCellValueFactory(new PropertyValueFactory<>("citta"));
        statoColumn.setCellValueFactory(new PropertyValueFactory<>("stato"));
        cucinaColumn.setCellValueFactory(new PropertyValueFactory<>("cucina"));

        // Disabilita il riordino delle colonne
        ristoranteColumn1.setReorderable(false);
        stelleColumn1.setReorderable(false);
        cittaColumn.setReorderable(false);
        statoColumn.setReorderable(false);
        cucinaColumn.setReorderable(false);

        // Imposta la politica di ridimensionamento per riempire la larghezza della tabella
        recensioniTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        preferitiList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Carica e visualizza le recensioni dell'utente corrente.
        List<Recensione> recensioniUtente = null; //gestioneRecensioni.getRecensioniUtente(SessioneUtente.getUsernameUtente());
        try {
            recensioniUtente = server.getRecensioniByUsername(SessioneUtente.getUsernameUtente()); //gestioneRecensioni.getRecensioniRistorante(ristorante.getNome());
        } catch (RemoteException e) {
            System.err.println("Errore di connessione al server RMI:");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Errore di query o database sul server:");
            e.printStackTrace();
        }
        recensioniTable.setItems(FXCollections.observableArrayList(recensioniUtente));

        // Gestisce la visibilità delle sezioni in base al ruolo dell'utente.
        boolean isCliente = SessioneUtente.isCliente();
        boolean isRistoratore = SessioneUtente.isRistoratore();

        //preferitiBox.setVisible(isCliente);
        dashboardButton.setVisible(isRistoratore);

        /*if (isCliente) {
            aggiornaListaPreferiti();
        }*/
        aggiornaListaPreferiti();

        // Imposta i gestori degli eventi per i pulsanti.
        logoutButton.setOnAction(event -> handleLogout());
        tornaalMenuButton.setOnAction(event -> handleTornaAlMenu());

        // Aggiunge un gestore per gli eventi di doppio click sulle liste.
        preferitiList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedRistoranteTel = preferitiList.getSelectionModel().getSelectedItem().getNumeroTelefono();
                if (selectedRistoranteTel != null) {
                    openRistoranteDetail(selectedRistoranteTel);
                }
            }
        });

        recensioniTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Recensione selectedRecensione = recensioniTable.getSelectionModel().getSelectedItem();
                if (selectedRecensione != null) {
                    openRistoranteDetail(selectedRecensione.getRistoranteTel());
                }
            }
        });

        // Configura l'azione per il pulsante Dashboard.
        dashboardButton.setOnAction(event -> {
            try {
                URL resourceUrl = getClass().getResource("/com/example/theknife/client/ristoratore-dashboard.fxml");
                if (resourceUrl == null) {
                    throw new IOException("FXML file not found: ristoratore-dashboard.fxml");
                }
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                Parent root = loader.load();
                Stage currentStage = (Stage) dashboardButton.getScene().getWindow();
                Scene scene = new Scene(root);
                addStylesheet(scene);
                currentStage.setScene(scene);
                currentStage.show();
            } catch (IOException e) {
                System.err.println("Error loading dashboard: " + e.getMessage());
                showError("Errore", "Impossibile aprire la dashboard ristoratore: " + e.getMessage());
            }
        });
    }

    /**
     * Aggiorna la lista dei ristoranti preferiti dell'utente corrente.
     * Recupera i preferiti dalla classe di gestione e aggiorna la {@link ListView}.
     */
    private void aggiornaListaPreferiti() {
        /*preferitiList.setItems(FXCollections.observableArrayList(
                gestionePreferiti.getPreferiti(SessioneUtente.getUsernameUtente())
        ));*/
        //ObservableList<Ristorante> listaRistoranti = FXCollections.observableArrayList();

        try {
            preferitiList.setItems(FXCollections.observableArrayList(
                server.getPreferiti(SessioneUtente.getUsernameUtente())
            ));
        } catch(RemoteException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestisce l'evento di click sul pulsante "Torna al menu principale".
     * Riporta l'utente alla schermata iniziale di visualizzazione dei ristoranti ({@code lista.fxml}).
     *
     * @throws IOException se il file FXML della schermata principale non viene trovato.
     */
    @FXML
    private void handleTornaAlMenu() {
        try {
            URL resourceUrl = getClass().getResource("/com/example/theknife/client/lista.fxml");
            if (resourceUrl == null) {
                throw new IOException("FXML file not found: lista.fxml");
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            Stage currentStage = (Stage) tornaalMenuButton.getScene().getWindow();
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
     * Gestisce l'operazione di logout dell'utente.
     * Resetta la sessione utente e riporta l'applicazione alla schermata di login ({@code login.fxml}).
     *
     * @throws IOException se il file FXML della schermata di login non viene trovato.
     */
    @FXML
    private void handleLogout() {
        SessioneUtente.eseguiLogout();
        try {
            URL resourceUrl = getClass().getResource("/com/example/theknife/client/login.fxml");
            if (resourceUrl == null) {
                throw new IOException("FXML file not found: login.fxml");
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            addStylesheet(scene);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            System.err.println("Error during logout: " + e.getMessage());
            showError("Errore", "Impossibile tornare alla schermata di login");
        }
    }

    /**
     * Apre la schermata dei dettagli di un ristorante.
     * Questo metodo tenta di trovare il ristorante tramite diversi approcci,
     * partendo dal nome, per garantire la massima robustezza anche in caso
     * di mancato caricamento iniziale dei dati.
     *
     * @param nomeRistorante Il nome del ristorante da visualizzare.
     */
    private void openRistoranteDetail(String numRistorante) {
        System.out.println("DEBUG: Cercando ristorante con nome: '" + numRistorante + "'");

        Ristorante ristorante = null;
        try {
            // Tentativo 1: Forza il ricaricamento dei ristoranti (se il metodo è disponibile).
            //gestioneRistorante.caricaRistoranti();
            ristorante = server.getRistorante(numRistorante);
        } catch (RemoteException | SQLException e) {
            System.out.println("DEBUG: Metodo non disponibile: " + e.getMessage());
        }

        //Ristorante ristorante = gestioneRistorante.getRistorante(numRistorante);

        // Tentativo 2: Se il primo tentativo fallisce, cerca in tutti i ristoranti caricati.
        /*if (ristorante == null) {
            System.out.println("DEBUG: getRistorante() ha restituito null. Ricerca in tutti i ristoranti...");
            List<Ristorante> tuttiRistoranti = gestioneRistorante.getTuttiRistoranti();
            System.out.println("DEBUG: Totale ristoranti disponibili: " + tuttiRistoranti.size());
            for (Ristorante r : tuttiRistoranti) {
                if (r.getNome().equals(numRistorante)) {
                    ristorante = r;
                    System.out.println("DEBUG: Trovato match esatto!");
                    break;
                }
            }
        }*/

        // Se il ristorante non viene trovato in nessun modo, mostra un errore e interrompe l'esecuzione.
        if (ristorante == null) {
            String debugMessage = "Ristorante cercato con numero telefono: '" + numRistorante + "'\n";
            debugMessage += "Username corrente: " + SessioneUtente.getUsernameUtente() + "\n";
            try {
                debugMessage += "Preferiti dell'utente: " + server.getPreferiti(SessioneUtente.getUsernameUtente());
            } catch (RemoteException | SQLException e) {
                e.printStackTrace();
            }
            

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore Debug");
            alert.setHeaderText("Ristorante non trovato");
            alert.setContentText(debugMessage);
            alert.showAndWait();
            return;
        }

        // Se il ristorante è stato trovato, carica e visualizza la schermata dei dettagli.
        try {
            URL resourceUrl = getClass().getResource("/com/example/theknife/client/ristorante-detail.fxml");
            if (resourceUrl == null) {
                throw new IOException("FXML file not found: ristorante-detail.fxml");
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            RistoranteDetailController controller = loader.getController();
            controller.setRistorante(ristorante);

            Stage currentStage = (Stage) nomeLabel.getScene().getWindow();
            Scene originalScene = currentStage.getScene();

            // Imposta un callback per tornare a questa schermata dopo la chiusura dei dettagli.
            controller.setTornaAlMenuPrincipaleCallback(() -> {
                System.out.println("DEBUG: Callback eseguita, torno al profilo utente");
                currentStage.setScene(originalScene);
                currentStage.show();
                this.refreshData();
            });

            Scene newScene = new Scene(root);
            addStylesheet(newScene);
            currentStage.setScene(newScene);
            currentStage.show();

        } catch (IOException e) {
            System.err.println("Error loading restaurant details: " + e.getMessage());
            showError("Errore", "Impossibile aprire i dettagli del ristorante: " + e.getMessage());
        }
    }

    /**
     * Aggiorna le liste di recensioni e preferiti mostrate nel profilo.
     * Questo metodo è utile per riflettere le modifiche apportate dall'utente in altre parti dell'applicazione
     * (es. aggiunta di un preferito dalla schermata dei dettagli del ristorante).
     */
    public void refreshData() {
        // Aggiorna la tabella delle recensioni.
        ArrayList<Recensione> recensioniUtente = null;
        try {
            recensioniUtente = server.getRecensioniByUsername(SessioneUtente.getUsernameUtente());
        } catch(RemoteException | SQLException e) {
            e.printStackTrace();
        }
        
        recensioniTable.setItems(FXCollections.observableArrayList(recensioniUtente));

        // Aggiorna la lista dei preferiti.
        aggiornaListaPreferiti();
    }

    /**
     * Mostra una finestra di dialogo di errore all'utente.
     *
     * @param header Il titolo dell'errore.
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