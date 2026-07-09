package com.example.theknife.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.controlsfx.control.CheckComboBox;

import com.example.theknife.common.DBService;
import com.example.theknife.common.Recensione;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;


/**
         * Controller per la gestione delle recensioni di un ristorante.
         * <p>
         * Questa classe gestisce l'interfaccia grafica relativa alle recensioni e consente
         * a utenti e ristoratori di interagire con il sistema.
         * </p>
         *
         * <p>Funzionalità principali:</p>
         * <ul>
         *   <li>Visualizzazione delle recensioni in una TableView</li>
         *   <li>Inserimento di nuove recensioni da parte degli utenti autenticati</li>
         *   <li>Modifica ed eliminazione delle proprie recensioni</li>
         *   <li>Risposta alle recensioni da parte dei ristoratori proprietari</li>
         *   <li>Modifica delle risposte già inserite</li>
         *   <li>Filtraggio delle recensioni in base al numero di stelle</li>
         *   <li>Visualizzazione statistica tramite PieChart</li>
         * </ul>
         *
         * <p>
         * La comunicazione con il server avviene tramite {@link DBService} (RMI).
         * L'interfaccia è realizzata in JavaFX.
         * </p>
         *
         * @author Claudio Bonci, 759939, Sede CO
         * @author Eleonora Anna Caredda, 762576, Sede CO
         * @author Filippo Crippa, 762174, Sede CO
         * @author Matilde Lecchi, 759875, Sede CO
         * @version 1.0
         * @since 2026-05-20
         */
        public class RecensioniController {
            /** Grafico a torta per la distribuzione delle stelle */
            @FXML private PieChart pieChart;

            /** CheckComboBox per filtrare recensioni per stelle */
            @FXML private CheckComboBox<Integer> comboBox;

            /** Colonne della tabella */
            @FXML private TableView<Recensione> tableView;
            @FXML private TableColumn<Recensione, Integer> colId;
            @FXML private TableColumn<Recensione, String> colTitolo;
            @FXML private TableColumn<Recensione, Integer> colStelle;
            @FXML private TableColumn<Recensione, String> colTesto;
            @FXML private TableColumn<Recensione, String> colData;
            @FXML private TableColumn<Recensione, String> colUtente;
            @FXML private TableColumn<Recensione, String> colRisposta;
            
            /** Area di input recensione */
            @FXML private TextArea recensioneTextArea;

           /** Pulsanti azioni */
            @FXML private Button inviaButton;
            @FXML private Button modificaButton;
            @FXML private Button eliminaButton;
            @FXML private Button rispondiButton;
            @FXML private Button indietroButton;
            @FXML private Button modificaRispostaButton;
            
            /** Area risposta ristoratore */
            @FXML private VBox rispostaBox;
            @FXML private TextArea rispostaTextArea;

            /** UI informazioni */
            @FXML private Label totaleRecensioniLabel;
            @FXML private TextField titoloText;

            /** Stelle UI */
            @FXML private Label stelleLabel;
            @FXML private Label star1;
            @FXML private Label star2;
            @FXML private Label star3;
            @FXML private Label star4;
            @FXML private Label star5;

            /** Stelle selezionate dall’utente */
            private int stelleSelezionate = 3;
           
            /** ID ristorante corrente */
            private String numTelefono;
            
             /** Lista principale delle recensioni */
            private ObservableList<Recensione> masterRecensioniList;
            
            /** Controller padre (dashboard ristoratore) */
            private RistoratoreDashboardController parentController;
            
            /** Root da ripristinare nella navigazione */
            private Parent rootToRestore;
           
            /** Callback per ritorno al menu principale */
            private Runnable tornaAlMenuPrincipaleCallback;

            /** Servizio remoto DB */
            private static DBService server;

            /**
             * Imposta il controller della dashboard del ristoratore come parent.
             *
             * @param controller il controller padre della dashboard
             */
            public void setParentController(RistoratoreDashboardController controller) {
                this.parentController = controller;
            }

            /**
             * Notifica il controller padre di eventuali aggiornamenti alle recensioni.
             */
            private void notificaAggiornamentoRecensioni() {
                if (parentController != null) {
                    parentController.onRecensioneUpdated();
                }
            }

            /**
            * Imposta la root da ripristinare dopo la navigazione.
            *
            * @param root nodo root originale
            */
            public void setRootToRestore(Parent root) {
                this.rootToRestore = root;
            }

            /**
             * Imposta callback per ritorno al menu principale.
             *
             * @param callback azione da eseguire
             */
            public void setTornaAlMenuPrincipaleCallback(Runnable callback) {
                this.tornaAlMenuPrincipaleCallback = callback;
            }

            /**
             * Inizializza il controller e configura:
             * <ul>
             *   <li>Connessione al server RMI</li>
             *   <li>Tabella recensioni</li>
             *   <li>Filtro stelle</li>
             *   <li>UI stelle interattive</li>
             *   <li>Listener e comportamenti UI</li>
             * </ul>
             */
            @FXML
            public void initialize() {
                System.out.println(star1);
                System.out.println(star2);
                System.out.println(star3);
                System.out.println(star4);
                System.out.println(star5);
                try {
                    server = ClientMain.getServer();
                    System.out.println("server = "+server);
                } catch(RemoteException | NotBoundException e) {
                    System.out.println("errore con il server");
                    e.printStackTrace();
                }
                
                /**
                 * Configura la visibilità dei componenti UI in base al ruolo utente:
                 * <ul>
                 *   <li>Utente normale</li>
                 *   <li>Ristoratore proprietario</li>
                 * </ul>
                 */
                setupUI();

                /**
                 * Configura la TableView delle recensioni:
                 * <ul>
                 *   <li>Binding colonne</li>
                 *   <li>Lista osservabile</li>
                 *   <li>Render stelle grafiche</li>
                 * </ul>
                 */
                setupTable();

                /**
                 * Configura il filtro per stelle tramite CheckComboBox.
                 */
                setupStarFilter();

                /**
                 * Imposta i listener della tabella recensioni.
                 * Gestisce:
                 * <ul>
                 *   <li>Selezione recensione</li>
                 *   <li>Abilitazione pulsanti</li>
                 *   <li>Gestione campi input</li>
                 * </ul>
                 */
                setupListeners();

                // STELLE 
                Label[] stars = {star1, star2, star3, star4, star5};

                stelleSelezionate = 3;
                java.util.function.IntConsumer renderStars = (int value) -> {
                    for (int i = 0; i < stars.length; i++) {
                stars[i].setText(i < value ? "★" : "☆");
                }
            };

                 // stato iniziale
                renderStars.accept(stelleSelezionate);

                for (int i = 0; i < stars.length; i++) {
                    stars[i].setText(i < stelleSelezionate ? "★" : "☆");
                }

                for (int i = 0; i < stars.length; i++) {
                    int value = i + 1;

                    stars[i].setOnMouseClicked(e -> {
                        stelleSelezionate = value;

                        for (int j = 0; j < stars.length; j++) {
                            stars[j].setText(j < value ? "★" : "☆");
                        }
                    });

                    stars[i].setOnMouseEntered(e -> {
                        for (int j = 0; j < stars.length; j++) {
                            stars[j].setText(j < value ? "★" : "☆");
                        }
                    });

                    stars[i].setOnMouseExited(e -> {
                        for (int j = 0; j < stars.length; j++) {
                            stars[j].setText(j < stelleSelezionate ? "★" : "☆");
                        }
                    });
                }


                colId.setReorderable(false);
                colTitolo.setReorderable(false);
                colStelle.setReorderable(false);
                colTesto.setReorderable(false);
                colData.setReorderable(false);
                colUtente.setReorderable(false);
                colRisposta.setReorderable(false);
            }

            /**
             * Configura l'interfaccia utente in base al ruolo e allo stato dell'utente corrente.
             * <p>
             * - Mostra i pulsanti e i campi di input per le recensioni se l'utente è loggato
             *   e non è un ristoratore proprietario del ristorante.
             * - Mostra i pulsanti per rispondere/modificare risposte se l'utente è un ristoratore
             *   proprietario del ristorante.
             * </p>
             */
            private void setupUI() {
                boolean isUtenteLoggato = SessioneUtente.isUtenteLoggato();
                boolean isRistoratore = SessioneUtente.isRistoratore();

                // Verifica se il ristoratore possiede questo ristorante
                boolean isProprietario = false;
                if (isRistoratore && numTelefono != null) {
                    String currentUser = SessioneUtente.getUsernameUtente();
                    //List<String> ownedRestaurants = ownershipService.getOwnedRestaurants(currentUser);
                    //isProprietario = ownedRestaurants.contains(numTelefono);
                    try {
                        ArrayList<String> numRistoranti = server.getNumeriTelefonoRisoranti(currentUser);
                        isProprietario = numRistoranti.contains(numTelefono);
                    } catch(RemoteException | SQLException e) {
                        e.printStackTrace();
                    }
                }

                boolean puo_recensire = isUtenteLoggato && (!isRistoratore || !isProprietario);

                // Nasconde o mostra i bottoni in base al ruolo dell'utente
                inviaButton.setVisible(puo_recensire && !isProprietario);
                modificaButton.setVisible(false);
                eliminaButton.setVisible(false);
                indietroButton.setVisible(false);
                rispondiButton.setVisible(isRistoratore && isProprietario);
                modificaRispostaButton.setVisible(false); // Inizialmente nascosto
                rispostaBox.setVisible(isRistoratore && isProprietario);
                //rispostaBox.setVisible(false);
                //rispostaTextArea.setVisible(false);
                
                // I campi di input per le recensioni sono visibili per chi può recensire
                //recensioneTextArea.setVisible(false);
                /*star1.setVisible(puo_recensire);
                star2.setVisible(puo_recensire);
                star3.setVisible(puo_recensire);
                star4.setVisible(puo_recensire);
                star5.setVisible(puo_recensire);*/            
            }
            /**
             * Inizializza la tabella delle recensioni configurando le colonne
             * e preparando le liste osservabili necessarie per filtrare i dati.
             */
            private void setupTable() {
                // Collegamento colonne --> proprietà del model Recensione
                colId.setCellValueFactory(new PropertyValueFactory<>("idRec"));
                colTitolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));
                colStelle.setCellValueFactory(new PropertyValueFactory<>("stelle"));
                colTesto.setCellValueFactory(new PropertyValueFactory<>("testo"));
                colData.setCellValueFactory(new PropertyValueFactory<>("data"));
                colUtente.setCellValueFactory(new PropertyValueFactory<>("username"));
                colRisposta.setCellValueFactory(new PropertyValueFactory<>("risposta"));

                // Lista osservabile delle recensioni
                masterRecensioniList = FXCollections.observableArrayList();
                tableView.setItems(masterRecensioniList);
                
                 // Tabella si adatta automaticamente
                tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

               // Personalizza la colonna "stelle" per mostrare stelle grafiche
                colStelle.setCellFactory(col -> new TableCell<Recensione, Integer>() {

                 @Override
                protected void updateItem(Integer stelle, boolean empty) {
                    super.updateItem(stelle, empty);

                    // Se la riga è vuota o senza valore, non mostra nulla
                    if (empty || stelle == null) {
                        setText(null);
                    } else {
                        // Converte numero (1-5) in stelle grafiche
                        // esempio: 4 → ★★★★☆
                        setText("★".repeat(stelle) + "☆".repeat(5 - stelle));
                    }
                }
            });
        }

    /**
     * Configura il {@link CheckComboBox} utilizzato per filtrare le recensioni
     * in base al numero di stelle.
     * <p>
     * Il metodo popola la combo box con i valori da 1 a 5, imposta un
     * {@link StringConverter} per visualizzare le stelle in formato grafico
     * (es. "★★★") e aggiunge un listener che aggiorna i dati visualizzati
     * nella tabella ad ogni modifica della selezione.
     * </p>
     */
    private void setupStarFilter() {
        comboBox.getItems().setAll(1, 2, 3, 4, 5);
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                if (value == null) {
                    return "";
                }
                return "★".repeat(value);
            }

            @Override
            public Integer fromString(String string) {
                throw new UnsupportedOperationException("Conversion from text is not used");
            }
        });
        comboBox.setTitle("Filtra per stelle");
        comboBox.getCheckModel().checkAll();

        comboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Integer>) change -> {
            while (change.next()) {
                // The list itself is read after the change batch, this loop only drains events.
            }
            refreshData();
        });
    }
    /**
     * Imposta i listener sugli elementi dell'interfaccia, in particolare
     * sulla selezione della tabella delle recensioni.
     * <p>
     * Gestisce la visibilità dei pulsanti e dei campi in base:
     * - al ruolo dell'utente (cliente o ristoratore),
     * - al fatto che sia autore della recensione,
     * - alla presenza o meno di una risposta già esistente.
     * </p>
     */
    private void setupListeners() {

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isRistoratore = SessioneUtente.isRistoratore();
            String currentUser = SessioneUtente.getUsernameUtente();

            // Verifica se il ristoratore possiede questo ristorante
            boolean isProprietario = false;
            if (isRistoratore && numTelefono != null && currentUser != null) {
                try {
                    ArrayList<String> numRistoranti = server.getNumeriTelefonoRisoranti(currentUser);
                    isProprietario = numRistoranti.contains(numTelefono);
                } catch(RemoteException | SQLException e) {
                    e.printStackTrace();
                }
            }

            boolean puo_recensire = SessioneUtente.isUtenteLoggato() && (!isRistoratore || !isProprietario);

            if (newVal != null) {
                boolean isAutore = newVal.getUsername().equals(currentUser);
                boolean hasRisposta = newVal.getRisposta() != null && !newVal.getRisposta().isEmpty();

                // Visibilità per utenti che possono recensire
                modificaButton.setVisible(isAutore && puo_recensire);
                eliminaButton.setVisible(isAutore && puo_recensire);
                inviaButton.setVisible(false);

                // Visibilità per ristoratore proprietario
                rispondiButton.setVisible(isRistoratore && isProprietario && !hasRisposta);
                modificaRispostaButton.setVisible(isRistoratore && isProprietario && hasRisposta);
                //rispostaBox.setVisible(isRistoratore && isProprietario);

                // Precompila i campi se è l'autore della recensione
                recensioneTextArea.setText(newVal.getTesto());
                stelleSelezionate = newVal.getStelle();

                // Aggiorna la visualizzazione delle stelle
                Label[] stars = {star1, star2, star3, star4, star5};
                for (int i = 0; i < stars.length; i++) {
                    stars[i].setText(i < stelleSelezionate ? "★" : "☆");
                }
                titoloText.setText(newVal.getTitolo());
                if (isAutore && puo_recensire) {
                    recensioneTextArea.setDisable(false);
                    star1.setDisable(false);
                    star2.setDisable(false);
                    star3.setDisable(false);
                    star4.setDisable(false);
                    star5.setDisable(false);
                    titoloText.setDisable(false);
                    indietroButton.setVisible(false);
                } else {
                    recensioneTextArea.setDisable(true);
                    star1.setDisable(true);
                    star2.setDisable(true);
                    star3.setDisable(true);
                    star4.setDisable(true);
                    star5.setDisable(true);
                    titoloText.setDisable(true);
                    indietroButton.setVisible(true);
                }
                /*if (isAutore && puo_recensire) {
                    recensioneTextArea.setText(newVal.getTesto());
                    stelleSlider.setValue(newVal.getStelle());
                    titoloText.setText(newVal.getTitolo());
                } else {
                    recensioneTextArea.clear();
                    stelleSlider.setValue(3);
                    titoloText.setText("");
                }*/

                // Precompila la risposta se il ristoratore proprietario ha già risposto
                /*if (isRistoratore && isProprietario && hasRisposta) {
                    rispostaTextArea.setText(newVal.getRisposta());
                } else if (isRistoratore && isProprietario) {
                    rispostaTextArea.clear();
                }*/

                if(hasRisposta) {
                    rispostaBox.setVisible(true);
                    rispostaTextArea.setText(newVal.getRisposta());
                    if(isProprietario && isRistoratore) {
                        modificaRispostaButton.setVisible(true);
                        rispondiButton.setVisible(false);
                        rispostaTextArea.setDisable(false);
                    } else {
                        rispostaTextArea.setDisable(true);
                        modificaRispostaButton.setVisible(false);
                        rispondiButton.setVisible(false);
                    }
                } else {
                    if(isProprietario && isRistoratore) {
                        modificaRispostaButton.setVisible(false);
                        rispondiButton.setVisible(true);
                    } else {
                        rispostaBox.setVisible(false);
                    }   
                }

            } else {
                pulisciCampi();
                modificaButton.setVisible(false);
                eliminaButton.setVisible(false);
                rispondiButton.setVisible(false);
                modificaRispostaButton.setVisible(false);
                inviaButton.setVisible(puo_recensire);
            }
        });
    }

    /**
     * Ricarica le recensioni dal server e aggiorna la UI.
     */
    public void refreshData() {
        if (numTelefono != null) {
            loadFilteredReviewsFromServer();
            aggiornaPieChart();
            pulisciCampi();
        }
    }

    /**
     * Carica recensioni filtrate per stelle dal server remoto.
     */
    private void loadFilteredReviewsFromServer() {
        if (server == null) {
            mostraErrore("Errore server", "Servizio recensioni non disponibile.");
            return;
        }

        List<Integer> selectedStars = new ArrayList<>(comboBox.getCheckModel().getCheckedItems());
        try {
            List<Recensione> remoteReviews = server.getRecensioniByStars(numTelefono, selectedStars);
            masterRecensioniList.setAll(remoteReviews);
        } catch (RemoteException | SQLException e) {
            mostraErrore("Errore di rete", "Impossibile caricare le recensioni filtrate dal server.");
            e.printStackTrace();
        }
    }

    /**
     * Imposta il numero di telefono del ristorante corrente e aggiorna i dati.
     *
     * @param tel il numero di telefono del ristorante
     */
    public void setRistoranteTel(String tel) {
        this.numTelefono = tel;
        refreshData();
        aggiornaPieChart();

        // Ri-configura l'UI con il nuovo ristorante
        setupUI();
    }
    /**
     * Torna al menu principale.
     */
    @FXML
    private void handleTornaAlMenuPrincipale() {
        if (tornaAlMenuPrincipaleCallback != null) {
            tornaAlMenuPrincipaleCallback.run();
        } else {
            // Fallback se il callback non è impostato
            if (rootToRestore != null) {
                Scene scene = pieChart.getScene();
                scene.setRoot(rootToRestore);
            }
        }
    }

    /**
     * Gestisce il pulsante "Indietro" all'interno della schermata delle recensioni.
     * <p>
     * Ripristina la visibilità e l'abilitazione dei campi di input per la
     * recensione (titolo, testo e stelle), pulisce i campi e nasconde il
     * pulsante "Indietro" stesso, tornando allo stato iniziale di inserimento.
     * </p>
     */
    @FXML
    private void handleIndietro() {
        pulisciCampi();
        recensioneTextArea.setDisable(false);
        star1.setDisable(false);
        star2.setDisable(false);
        star3.setDisable(false);
        star4.setDisable(false);
        star5.setDisable(false);
        titoloText.setDisable(false);
        indietroButton.setVisible(false);
    }

    /**
     * Aggiorna il grafico a torta con il conteggio delle recensioni per ogni numero di stelle.
     */
    private void aggiornaPieChart() {
        pieChart.getData().clear();
        Map<Integer, Integer> recensioniMap = new HashMap<>();
        int totale = 0;

        // Conta le recensioni per ogni numero di stelle basandosi sulla lista filtrata lato server
        for (Recensione r : masterRecensioniList) {
            recensioniMap.merge(r.getStelle(), 1, Integer::sum);
            totale++;
        }

        // Mostra sempre tutte le 5 quantità di stelle, anche se il conteggio è zero
        for (int stelle = 1; stelle <= 5; stelle++) {
            pieChart.getData().add(new PieChart.Data(stelle + " ⭐", recensioniMap.getOrDefault(stelle, 0)));
        }

        if (totaleRecensioniLabel != null) {
            totaleRecensioniLabel.setText("Totale recensioni: " + masterRecensioniList.size());
        }
    }

    /**
     * Gestisce la creazione di una nuova recensione.
     * Controlla validità input e invia al server.
     */
    @FXML
    private void handleInvia() {
        if (!SessioneUtente.isUtenteLoggato()) {
            mostraErrore("Accesso richiesto", "Per scrivere una recensione devi effettuare l'accesso.");
            return;
        }
        if (recensioneTextArea.getText().trim().isEmpty()) {
            mostraErrore("Errore", "Il testo della recensione non può essere vuoto.");
            return;
        }

        if (titoloText.getText().trim().isEmpty()) {
            mostraErrore("Errore", "Il titolo della recensione non può essere vuoto.");
            return;
        }
        // Controllo se l'utente ha già recensito questo ristorante
        

        /*Recensione recensione = new Recensione(
                (int) stelleSlider.getValue(),
                recensioneTextArea.getText().trim(),
                numTelefono,
                SessioneUtente.getUsernameUtente()
        );
        gestioneRecensioni.aggiungiRecensione(recensione);*/
        try {
            server.saveRecensione(titoloText.getText().trim(), recensioneTextArea.getText().trim(), stelleSelezionate, numTelefono, SessioneUtente.getUsernameUtente());
        } catch(SQLException | RemoteException e) {
            e.printStackTrace();
        }
        refreshData();
        notificaAggiornamentoRecensioni();
    }

    /**
     * Modifica la recensione selezionata dall’utente.
     */
    @FXML
    private void handleModifica() {
        Recensione selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostraErrore("Selezione richiesta", "Seleziona una recensione da modificare.");
            return;
        }
        if (!Objects.equals(selected.getUsername(), SessioneUtente.getUsernameUtente())) {
            mostraErrore("Errore", "Puoi modificare solo le tue recensioni.");
            return;
        }
        
        /*gestioneRecensioni.modificaRecensione(
                selected.getUsername(),
                numTelefono,
                recensioneTextArea.getText(),
                (int) stelleSlider.getValue()
        );*/
        try {
            server.modifyRecensione(selected.getIdRec(), titoloText.getText(), recensioneTextArea.getText(), stelleSelezionate);
        } catch(SQLException | RemoteException e) {
            e.printStackTrace();
        }
        refreshData();
        notificaAggiornamentoRecensioni();
    }

    /**
     * Gestisce l'eliminazione della recensione selezionata.
     */
    @FXML
    private void handleElimina() {
        Recensione selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostraErrore("Selezione richiesta", "Seleziona una recensione da eliminare.");
            return;
        }
        if (!Objects.equals(selected.getUsername(), SessioneUtente.getUsernameUtente())) {
            mostraErrore("Errore", "Puoi eliminare solo le tue recensioni.");
            return;
        }

        //gestioneRecensioni.eliminaRecensione(selected.getUsername(), numTelefono);
        try {
            server.removeRecensione(selected.getIdRec());
        } catch(SQLException | RemoteException e) {
            e.printStackTrace();
        }
        refreshData();
        notificaAggiornamentoRecensioni();
    }

    /**
     * Gestisce l'invio di una risposta alla recensione selezionata.
     */
    @FXML
    private void handleRispondi() {
        Recensione selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null || !SessioneUtente.isRistoratore()) {
            mostraErrore("Errore", "Non puoi rispondere a questa recensione.");
            return;
        }
        if (rispostaTextArea.getText().trim().isEmpty()) {
            mostraErrore("Errore", "Il testo della risposta non può essere vuoto.");
            return;
        }

        selected.setRisposta(rispostaTextArea.getText().trim());
        //gestioneRecensioni.salvaRispostaRecensione(selected);
        try {
            server.saveRisposta(selected.getIdRec(), selected.getRisposta());
        } catch(SQLException | RemoteException e) {
            e.printStackTrace();
        }
        refreshData();
        notificaAggiornamentoRecensioni();
    }

    /**
     * Pulisce i campi di testo e resetta la selezione della tabella.
     */
    private void pulisciCampi() {
        titoloText.setText("");
        recensioneTextArea.clear();
        rispostaTextArea.clear();
        stelleSelezionate = 3;

        Label[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            stars[i].setText(i < stelleSelezionate ? "★" : "☆");
        }
        tableView.getSelectionModel().clearSelection();
    }

    /**
     * Mostra un alert di errore.
     */
    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    /**
     * Gestisce la modifica della risposta a una recensione da parte del ristoratore.
     * <p>
     * Controlla che:
     * <ul>
     *   <li>ci sia una recensione selezionata,</li>
     *   <li>l'utente sia un ristoratore,</li>
     *   <li>il ristoratore sia proprietario del ristorante,</li>
     *   <li>il testo della risposta non sia vuoto.</li>
     * </ul>
     * Se le condizioni sono soddisfatte, aggiorna la risposta e la salva tramite
     * {@code gestioneRecensioni}, ricaricando i dati e notificando l'aggiornamento.
     * In caso di successo mostra un messaggio di conferma.
     */
    @FXML
    private void handleModificaRisposta() {
        Recensione selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null || !SessioneUtente.isRistoratore()) {
            mostraErrore("Errore", "Non puoi modificare questa risposta.");
            return;
        }

        // Verifica che il ristoratore sia proprietario del ristorante
        String currentUser = SessioneUtente.getUsernameUtente();
        //List<String> ownedRestaurants = ownershipService.getOwnedRestaurants(currentUser);
        ArrayList<String> ownedRestaurants = null;
        try {
            ownedRestaurants = server.getNumeriTelefonoRisoranti(currentUser);
        } catch(RemoteException | SQLException e) {
            e.printStackTrace();
        }
        if (!ownedRestaurants.contains(numTelefono)) {
            mostraErrore("Errore", "Puoi modificare le risposte solo nei tuoi ristoranti.");
            return;
        }

        if (rispostaTextArea.getText().trim().isEmpty()) {
            mostraErrore("Errore", "Il testo della risposta non può essere vuoto.");
            return;
        }

        // Aggiorna la risposta
        selected.setRisposta(rispostaTextArea.getText().trim());
        //gestioneRecensioni.salvaRispostaRecensione(selected);
        try {
            server.modifyRisposta(selected.getIdRec(), selected.getRisposta());
        } catch(SQLException | RemoteException e) {
            e.printStackTrace();
        }
        refreshData();
        notificaAggiornamentoRecensioni();

        // Mostra conferma
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText("Risposta modificata con successo!");
        alert.showAndWait();
    }
}