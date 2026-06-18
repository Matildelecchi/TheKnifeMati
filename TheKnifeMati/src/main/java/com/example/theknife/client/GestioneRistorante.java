package com.example.theknife.client;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.example.theknife.common.Ristorante;

/**
 * Servizio per la gestione dei ristoranti sul client.
 * Tutti i dati vengono caricati e salvati tramite RMI verso il server.
 */
public class GestioneRistorante {
    private static GestioneRistorante instance;
    private final Map<String, Ristorante> ristoranti = new HashMap<>();
    private final Map<String, Set<String>> proprietariRistoranti = new HashMap<>();

    private GestioneRistorante() {
        caricaRistoranti();
        caricaProprietari();
    }

    public static synchronized GestioneRistorante getInstance() {
        if (instance == null) {
            instance = new GestioneRistorante();
        }
        return instance;
    }

    /**
     * Carica i ristoranti tramite chiamata RMI.
     */
    public void caricaRistoranti() {
        
        ArrayList<Ristorante> remoteRistoranti = null;
        try {
            remoteRistoranti = RMIService.getService().getRistoranti(
                    "SELECT nome, indirizzo, localita, prezzo, cucina, longitudine, latitudine, numeroTelefono, url, sitoWeb, premio, stellaVerde, servizi, descrizione FROM ristoranti"
            );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ristoranti.clear();
        if (remoteRistoranti != null) {
            for (Ristorante ristorante : remoteRistoranti) {
                if (ristorante != null && ristorante.getNome() != null) {
                    ristoranti.put(ristorante.getNome(), ristorante);
                }
            }
        }
        
    }

    /**
     * Carica le associazioni proprietario-ristorante tramite chiamata RMI.
     */
    private void caricaProprietari() {
        
        ArrayList<String> remoteOwnership = null;
        try {
            remoteOwnership = RMIService.getService().getOwnership(
                    "SELECT username, ristorante FROM proprietari_ristoranti"
            );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        proprietariRistoranti.clear();
        if (remoteOwnership != null) {
            for (String row : remoteOwnership) {
                if (row == null || row.isBlank()) {
                    continue;
                }
                String[] parts = row.split(",", 2);
                if (parts.length >= 2) {
                    String username = parts[0].trim();
                    String ristoranteId = parts[1].trim();
                    proprietariRistoranti.computeIfAbsent(username, k -> new HashSet<>()).add(ristoranteId);
                }
            }
        }
        
    }

    public Ristorante getRistorante(String nome) {
        return ristoranti.get(nome);
    }

    public List<Ristorante> getTuttiRistoranti() {
        return new ArrayList<>(ristoranti.values());
    }

    public List<Ristorante> getRistorantiByNomi(Collection<String> nomi) {
        return nomi.stream()
                .map(this::getRistorante)
                .filter(Objects::nonNull)
                .toList();
    }

    public boolean aggiungiRistorante(String username, Ristorante ristorante) {
        if (username == null || ristorante == null) {
            return false;
        }

        
            boolean saved = false;
            try {
                saved = RMIService.getService().saveRistorante(
                        "INSERT INTO ristoranti (nome, indirizzo, localita, prezzo, cucina, longitudine, latitudine, numeroTelefono, url, sitoWeb, premio, stellaVerde, servizi, descrizione) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        ristorante
                );
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (saved) {
                proprietariRistoranti.computeIfAbsent(username, k -> new HashSet<>()).add(ristorante.getNome());
                try {
                    return RMIService.getService().saveOwnership(
                            "INSERT INTO proprietari_ristoranti (username, ristorante) VALUES (?, ?)",
                            username,
                            ristorante.getNome()
                    );
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        
        return false;
    }
}
