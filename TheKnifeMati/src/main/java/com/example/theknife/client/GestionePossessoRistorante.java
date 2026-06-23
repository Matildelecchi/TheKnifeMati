package com.example.theknife.client;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servizio per la gestione delle propriet� dei ristoranti.
 * Implementa il pattern Singleton e gestisce le associazioni tra ristoratori
 * e i loro ristoranti, mantenendo la persistenza tramite RMI dal server.
 *
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */

public class GestionePossessoRistorante {
    private static final String OWNERSHIP_FILE_PATH = "data/proprietari_ristoranti.csv";
    private static final String CSV_HEADER = "username,ristorante";

    private static GestionePossessoRistorante instance;
    private final Map<String, List<String>> ownershipMap = new HashMap<>();
    private boolean isInitialized = false;

    private GestionePossessoRistorante() {}

    public static GestionePossessoRistorante getInstance() {
        if (instance == null) {
            instance = new GestionePossessoRistorante();
        }
        return instance;
    }

    public void initialize() {
        if (!isInitialized) {
            loadOwnershipData();
            isInitialized = true;
        }
    }

    /**
     * Carica i dati di propriet� dal server tramite RMI.
     * <p>
     * I dati vengono memorizzati nella mappa {@code ownershipMap},
     * associando ciascun utente alla lista di ristoranti posseduti.
     * Se il caricamento fallisce, l'applicazione continua comunque.
     * </p>
     */
    private void loadOwnershipData() {
        ownershipMap.clear();
        
        try {
            ArrayList<String> remoteOwnership = RMIService.getService().getOwnership(
                    "SELECT username, ristorante FROM proprietari_ristoranti"
            );
            
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
            System.err.println("Errore nel caricamento dei dati di propriet� da server: " + e.getMessage());
            // Non lanciare eccezione, permettere all'applicazione di continuare
        }
    }

    /**
     * Crea il file di propriet� con header se non esiste.
     *@param file file CSV da creare
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
     * Processa una singola riga del file di propriet�.
     * <p>
     * La riga deve contenere almeno due campi: username e ID del ristorante.
     * Se il ristorante non esiste nel database, l'associazione viene ignorata.
     * </p>
     *
     * @param line riga del file CSV da processare
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
     * Restituisce la lista degli ID dei ristoranti posseduti da un utente.
     *
     * @param username username del ristoratore
     * @return lista degli ID dei ristoranti posseduti (vuota se nessuno)
     */
    public List<String> getOwnedRestaurants(String username) {
        return ownershipMap.getOrDefault(username, new ArrayList<>());
    }
    /**
     * Associa un ristorante a un proprietario, salvando l'associazione
     * sia nel file CSV che nella mappa {@code ownershipMap}.
     *
     * @param ristoranteNome nome o ID del ristorante da associare
     * @param username       nome utente del proprietario
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
     * Aggiorna la mappa {@code ownershipMap} rileggendo i dati dal server.
     */
    public void refreshOwnershipData() {
        loadOwnershipData();
    }
}
