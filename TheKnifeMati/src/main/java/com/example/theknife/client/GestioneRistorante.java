package com.example.theknife.client;

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
 * Servizio client per la gestione dei ristoranti.
 * <p>
 * Mantiene una cache locale dei ristoranti e delle associazioni
 * tra utenti e ristoranti (proprietà).
 * Tutte le operazioni di caricamento e persistenza vengono eseguite
 * tramite chiamate RMI verso il server remoto.
 * </p>
 *
 * <p>
 * Questa classe implementa il pattern Singleton per garantire
 * un'unica istanza condivisa nell'applicazione client.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */
public class GestioneRistorante {
    /** Istanza Singleton della classe. */
    private static GestioneRistorante instance;
    
    /** Mappa nome ristorante → oggetto Ristorante. */
    private final Map<String, Ristorante> ristoranti = new HashMap<>();
    
    /** Mappa username → insieme ristoranti posseduti. */
    private final Map<String, Set<String>> proprietariRistoranti = new HashMap<>();

    /**
     * Costruttore privato.
     * Inizializza il caricamento dei dati dal server.
     */
    private GestioneRistorante() {
        caricaRistoranti();
        caricaProprietari();
    }

    /**
     * Restituisce l’istanza Singleton del servizio.
     *
     * @return istanza unica di {@link GestioneRistorante}
     */
    public static synchronized GestioneRistorante getInstance() {
        if (instance == null) {
            instance = new GestioneRistorante();
        }
        return instance;
    }

    /**
     * Carica tutti i ristoranti dal server tramite RMI.
     * <p>
     * I dati vengono salvati nella cache locale.
     * </p>
     */
    public void caricaRistoranti() {
        
        ArrayList<Ristorante> remoteRistoranti = null;
        try {
            remoteRistoranti = RMIService.getService().getRistoranti("","");
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

/**
     * Restituisce un ristorante dato il nome.
     *
     * @param nome nome del ristorante
     * @return oggetto {@link Ristorante} oppure null
     */
    public Ristorante getRistorante(String nome) {
        return ristoranti.get(nome);
    }

    /**
     * Restituisce tutti i ristoranti disponibili.
     *
     * @return lista di ristoranti
     */
    public List<Ristorante> getTuttiRistoranti() {
        return new ArrayList<>(ristoranti.values());
    }

    /**
     * Restituisce i ristoranti corrispondenti ai nomi forniti.
     *
     * @param nomi collezione di nomi ristorante
     * @return lista di ristoranti trovati
     */
    public List<Ristorante> getRistorantiByNomi(Collection<String> nomi) {
        return nomi.stream()
                .map(this::getRistorante)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Aggiunge un nuovo ristorante e ne registra la proprietà.
     * <p>
     * L’operazione viene salvata sul server tramite RMI.
     * </p>
     *
     * @param username utente proprietario
     * @param ristorante ristorante da aggiungere
     * @return true se l’operazione è andata a buon fine
     */
    public boolean aggiungiRistorante(String username, Ristorante ristorante) {
        if (username == null || ristorante == null) {
            return false;
        }

        
            boolean saved = false;
            try {
                saved = RMIService.getService().saveRistorante(
                        "INSERT INTO ristoranti (nome, indirizzo, citta, prezzo, cucina, longitudine, latitudine, num_tel, sito, sitoWeb, premi, stellaVerde, servizi, descrizione) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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
