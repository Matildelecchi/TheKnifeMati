package com.example.theknife.client;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import com.example.theknife.common.DBService;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Classe principale dell'applicazione JavaFX "The Knife".
 * <p>
 * Responsabile dell'avvio del client, dell'inizializzazione dell'interfaccia grafica
 * e del caricamento della schermata iniziale di login.
 * La classe gestisce inoltre la configurazione della finestra principale e il supporto
 * alla modalità schermo intero.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */
public class App extends Application {

    /** Larghezza minima della finestra dell'applicazione. */
    private static final double MIN_WIDTH = 800;

    /** Altezza minima della finestra dell'applicazione. */
    private static final double MIN_HEIGHT = 600;

    /** Istanza del client principale dell'applicazione. */
    private static ClientMain client;

    /** Riferimento al servizio remoto del database (RMI). */
    private static DBService server;
    
   /**
     * Avvia l'applicazione JavaFX e configura la finestra principale.
     * <p>
     * Il metodo esegue le seguenti operazioni:
     * <ul>
     *   <li>Recupera le dimensioni dello schermo principale;</li>
     *   <li>Calcola dimensioni iniziali della finestra in modo responsivo;</li>
     *   <li>Carica la vista FXML della schermata di login;</li>
     *   <li>Applica fogli di stile CSS;</li>
     *   <li>Configura dimensioni minime, massime e posizione della finestra;</li>
     *   <li>Abilita il supporto alla modalità schermo intero.</li>
     * </ul>
     * </p>
     *
     * @param stage lo stage principale fornito da JavaFX
     * @throws IOException se il caricamento delle risorse FXML o CSS fallisce
     */

    @Override
    public void start(Stage stage) throws IOException {
        // Ottieni le dimensioni del monitor primario
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Calcola le dimensioni iniziali (90% dello schermo per una migliore adattabilità)
        double initialWidth = screenBounds.getWidth() * 0.9;
        double initialHeight = screenBounds.getHeight() * 0.9;

        // Assicurati che le dimensioni non siano inferiori ai valori minimi
        double windowWidth = Math.max(initialWidth, MIN_WIDTH);
        double windowHeight = Math.max(initialHeight, MIN_HEIGHT);

        System.out.println("Risoluzione schermo: " + screenBounds.getWidth() + "x" + screenBounds.getHeight());
        System.out.println("Dimensioni finestra: " + windowWidth + "x" + windowHeight);

        // Carica il file FXML per la schermata di login
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/example/theknife/client/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Applica il foglio di stile CSS dal classpath
        scene.getStylesheets().add(getClass().getResource("/data/stile.css").toExternalForm());

        // Configura lo stage
        stage.setTitle("TheKnife");
        stage.setScene(scene);

        // Aggiunta l'icona all'applicazione
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/data/IMG/LOGO.png");
            if (is == null) {
                System.err.println("ERRORE: Icona non trovata! Path: /data/IMG/LOGO.png");
                System.err.println("Controlla che il file sia in src/main/resources/data/IMG/LOGO.png");
            } else {
                stage.getIcons().add(new Image(is));
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dell'icona: " + e.getMessage());
            e.printStackTrace();
        }

        // Imposta dimensioni iniziali
        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);

        // Imposta dimensioni minime per evitare finestre troppo piccole
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        // Imposta dimensioni massime basate sullo schermo
        stage.setMaxWidth(screenBounds.getWidth());
        stage.setMaxHeight(screenBounds.getHeight());

        // Centra la finestra sullo schermo
        stage.setX((screenBounds.getWidth() - windowWidth) / 2);
        stage.setY((screenBounds.getHeight() - windowHeight) / 2);

        // Permetti il ridimensionamento
        stage.setResizable(true);

        // Configura il supporto per schermo intero
        setupFullScreenSupport(stage, scene);

        // Mostra la finestra
        stage.show();

    }

    /**
     * Configura le scorciatoie da tastiera e il comportamento della modalità fullscreen.
     * <p>
     * Shortcut implementate:
     * <ul>
     *   <li>F11 → attiva/disattiva schermo intero</li>
     *   <li>ESC → esce dalla modalità schermo intero (se attiva)</li>
     * </ul>
     * </p>
     *
     * @param stage lo stage principale dell'applicazione
     * @param scene la scena su cui registrare gli eventi da tastiera
     */

    private void setupFullScreenSupport(Stage stage, Scene scene) {
        // Scorciatoia F11 per attivare/disattivare schermo intero
        KeyCombination fullScreenKey = new KeyCodeCombination(KeyCode.F11);
        scene.setOnKeyPressed(event -> {
            if (fullScreenKey.match(event)) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        // Scorciatoia ESC per uscire dallo schermo intero
        KeyCombination escapeKey = new KeyCodeCombination(KeyCode.ESCAPE);
        scene.setOnKeyPressed(event -> {
            if (escapeKey.match(event) && stage.isFullScreen()) {
                stage.setFullScreen(false);
            }
        });

        // Personalizza il messaggio di uscita dallo schermo intero
        stage.setFullScreenExitHint("Premi ESC o F11 per uscire dalla modalità schermo intero");

        // Listener per cambiamenti di modalità schermo intero
        stage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Modalità schermo intero: " + (newValue ? "ATTIVA" : "DISATTIVA"));
        });
    }

    /**
     * Punto di ingresso dell'applicazione.
     * <p>
     * Inizializza il client RMI e avvia il ciclo di vita JavaFX.
     * </p>
     *
     * @param args argomenti da riga di comando
     * @throws RemoteException se fallisce la comunicazione RMI
     * @throws NotBoundException se il servizio remoto non è disponibile
     */
    
    public static void main(String[] args) throws RemoteException, NotBoundException {
        client = new ClientMain();
        server = client.getServer();
        launch();
    }
}