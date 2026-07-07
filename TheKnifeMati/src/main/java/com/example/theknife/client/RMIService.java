package com.example.theknife.client;

import com.example.theknife.common.DBService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Classe di utilità per la gestione della connessione al server RMI
 * dell'applicazione "The Knife".
 * <p>
 * Si occupa di stabilire la connessione con il registry RMI,
 * recuperare lo stub del servizio remoto {@link DBService}
 * ed esporlo alle altre classi del client.
 * </p>
 *
 * <p>
 * La classe implementa un meccanismo di inizializzazione lazy:
 * lo stub remoto viene ottenuto solo alla prima richiesta e
 * successivamente riutilizzato per tutte le chiamate, evitando
 * lookup ripetuti al registry RMI.
 * </p>
 *
 * <p>
 * Essendo una classe di utilità, non può essere istanziata.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */

public final class RMIService {
    /**
     * Indirizzo del server RMI.
     */
    private static final String HOST = "127.0.0.1";
    
    /**
     * Porta del registry RMI.
     */
    private static final int PORT = 1234;
    
    /**
     * Nome del servizio registrato nel registry RMI.
     */
    private static final String SERVICE_NAME = "TimeService";

    /**
     * Stub del servizio remoto {@link DBService}.
     * <p>
     * Viene inizializzato in modo lazy alla prima chiamata di {@link #getService()}.
     * </p>
     */
    private static volatile DBService stub;

    /**
     * Costruttore privato per impedire l’istanziazione della classe.
     * <p>
     * Questa classe è una utility e non deve essere istanziata.
     * </p>
     */
    private RMIService() {
        // utility class
    }

    /**
     * Restituisce l’istanza del servizio remoto {@link DBService}.
     *
     * <p>
     * Se lo stub non è ancora stato inizializzato, viene effettuata la connessione
     * al registry RMI e recuperato il servizio tramite lookup.
     * </p>
     *
     * @return istanza del servizio remoto {@link DBService}
     * @throws Exception se la connessione al registry o il lookup del servizio falliscono
     */
    public static synchronized DBService getService() throws Exception {
        if (stub == null) {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            stub = (DBService) registry.lookup(SERVICE_NAME);
        }
        return stub;
    }
}
