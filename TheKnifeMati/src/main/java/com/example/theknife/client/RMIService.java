package com.example.theknife.client;

import com.example.theknife.common.DBService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Utility class per la gestione della connessione RMI al servizio remoto {@link DBService}.
 *
 * <p>
 * Questa classe fornisce un punto centralizzato per ottenere l’istanza del servizio remoto
 * esposto tramite RMI (Remote Method Invocation). Il servizio viene recuperato dal registry
 * RMI utilizzando host, porta e nome del servizio predefiniti.
 * </p>
 *
 * <p>
 * L’implementazione segue un pattern di tipo singleton lazy:
 * lo stub viene inizializzato solo alla prima richiesta e poi riutilizzato
 * per tutte le chiamate successive, evitando lookup ripetuti al registry.
 * </p>
 *
 * <p>
 * La classe è thread-safe grazie alla sincronizzazione del metodo di accesso.
 * </p>
 *  *
 * <p>
 * Configurazione della connessione RMI:
 * </p>
 * <ul>
 *     <li>HOST: 127.0.0.1 (localhost)</li>
 *     <li>PORT: 1234</li>
 *     <li>SERVICE_NAME: TimeService</li>
 * </ul>
 *
 * @author Claudio Bonci, 759939, Sede CO
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
