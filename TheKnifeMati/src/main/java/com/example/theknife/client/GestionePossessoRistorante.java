package com.example.theknife.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.theknife.common.DBService;

/**
 * Servizio per la gestione delle proprietà dei ristoranti.
 * <p>
 * Implementa un pattern Singleton e mantiene in memoria le associazioni tra
 * ristoratori e ristoranti.
 * I dati vengono sincronizzati con il database remoto tramite RMI e
 * memorizzati localmente in una struttura {@link Map}.
 * </p>
 *
 * <p>
 * La classe garantisce una cache locale delle informazioni per ridurre
 * le chiamate remote e migliorare le prestazioni dell'applicazione.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */

public class GestionePossessoRistorante {
    /** Percorso del file CSV locale di backup delle associazioni. */
    private static final String OWNERSHIP_FILE_PATH = "data/proprietari_ristoranti.csv";
    
    /** Header del file CSV. */
    private static final String CSV_HEADER = "username,ristorante";

    /** Istanza Singleton della classe. */
    private static GestionePossessoRistorante instance;
    
    /** Mappa username → lista ID ristoranti posseduti. */
    private final Map<String, List<String>> ownershipMap = new HashMap<>();
    
     /** Indica se i dati sono già stati inizializzati. */
    private boolean isInitialized = false;

    private static DBService server;

    /** Costruttore privato (Singleton). */
    private GestionePossessoRistorante() {}

    /**
     * Restituisce l’istanza Singleton del servizio.
     *
     * @return istanza unica di {@link GestionePossessoRistorante}
     */

    public static GestionePossessoRistorante getInstance() {
        if (instance == null) {
            instance = new GestionePossessoRistorante();
        }
        return instance;
    }

 /**
     * Inizializza il servizio caricando i dati dal server remoto.
     * <p>
     * L’operazione viene eseguita una sola volta.
     * </p>
     */

    public void initialize() {
        if (!isInitialized) {
            try {
                server = ClientMain.getServer();
            } catch (RemoteException | NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            loadOwnershipData();
            isInitialized = true;
        }
    }

    /**
     * Carica i dati di proprietà dal server remoto tramite RMI.
     * <p>
     * I dati vengono salvati nella mappa locale {@code ownershipMap}.
     * In caso di errore, il sistema continua a funzionare utilizzando
     * eventuali dati già presenti.
     * </p>
     */
    private void loadOwnershipData() {
        ownershipMap.clear();
        
        try {
            /*ArrayList<String> remoteOwnership = RMIService.getService().getOwnership(
                    "SELECT proprietario, nome FROM ristoranti"
            );*/
            ArrayList<String> remoteOwnership = server.getOwnership("SELECT proprietario, nome FROM ristoranti");
            
            if (remoteOwnership != null) {
                for (String row : remoteOwnership) {
                    if (row == null || row.isBlank()) {
                        continue;
                    }
                    processOwnershipLine(row);
                }
            }
            System.out.println("Ownership data loaded successfully from database");
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dei dati di proprieta\' da server: " + e.getMessage());
            // Non lanciare eccezione, permettere all'applicazione di continuare
        }
    }

     /**
     * Crea il file CSV se non esiste.
     *
     * @param file file da inizializzare
     */

    private void createOwnershipFile(File file) {
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(CSV_HEADER + "\n");
        } catch (IOException e) {
            System.err.println("Errore nella creazione del file: " + e.getMessage());
        }
    }

    /**
     * Elabora una riga CSV e aggiorna la mappa delle proprietà.
     *
     * @param line riga del file CSV nel formato "username,ristorante"
     */

    private void processOwnershipLine(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 2) {
            String username = parts[0].trim();
            String ristoranteId = parts[1].trim();

            if (!username.isEmpty() && !ristoranteId.isEmpty()) {
                // Verifica che il ristorante esista prima di aggiungerlo
                if (GestioneRistorante.getInstance().getRistorante(ristoranteId) != null) {
                    ownershipMap.computeIfAbsent(username, k -> new ArrayList<>()).add(ristoranteId);
                } else {
                    System.err.println("Ristorante non trovato nel database: " + ristoranteId);
                }
            }
        }
    }
    
     /**
     * Restituisce la lista dei ristoranti posseduti da un utente.
     *
     * @param username username del ristoratore
     * @return lista di ID ristoranti (vuota se nessuno)
     */

    public List<String> getOwnedRestaurants(String username) {
        return ownershipMap.getOrDefault(username, new ArrayList<>());
    }
    

    /**
     * Associa un ristorante a un proprietario.
     * <p>
     * L’associazione viene salvata sia in locale (CSV) che in memoria.
     * </p>
     *
     * @param ristoranteNome nome o ID del ristorante
     * @param username username del proprietario
     */

    public void associaRistoranteAProprietario(String ristoranteNome, String username) {
        try (FileWriter writer = new FileWriter(OWNERSHIP_FILE_PATH, true)) {
            writer.write(String.format("%s,%s%n", username, ristoranteNome));
            ownershipMap.computeIfAbsent(username, k -> new ArrayList<>()).add(ristoranteNome);
        } catch (IOException e) {
            System.err.println("Errore durante l'associazione del ristorante: " + e.getMessage());
        }
    }

    /**
     * Aggiorna i dati ricaricandoli dal server remoto.
     */
    public void refreshOwnershipData() {
        loadOwnershipData();
    }
}
