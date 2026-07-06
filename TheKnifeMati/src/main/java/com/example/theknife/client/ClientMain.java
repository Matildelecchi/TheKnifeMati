package com.example.theknife.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.example.theknife.common.DBService;

/**
 * Classe principale del client RMI dell'applicazione "The Knife".
 * <p>
 * Si occupa della connessione al server remoto tramite Java RMI,
 * del recupero dello stub del servizio {@link DBService} e dell'avvio
 * dell'applicazione JavaFX.
 * </p>
 *
 * <p>
 * Questa classe funge da punto di bootstrap del client: inizializza
 * il registry RMI, effettua il lookup del servizio remoto e lo rende
 * disponibile all'intera applicazione.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */

public class ClientMain
{
    /** Istanza singleton del client (attualmente non utilizzata). */
    private static ClientMain client = null;
    
    /** Stub remoto del servizio DB ottenuto tramite RMI. */
    private static DBService stub = null;

    /** Porta del registry RMI. */
    static int PORT = 1234;

    /**
     * Punto di ingresso del client.
     * <p>
     * Esegue le seguenti operazioni:
     * <ul>
     *   <li>Connette al registry RMI sulla porta configurata;</li>
     *   <li>Esegue il lookup del servizio remoto "TimeService";</li>
     *   <li>Ottiene lo stub del {@link DBService};</li>
     *   <li>Testa la connessione stampando data e ora dal server;</li>
     *   <li>Avvia l'applicazione JavaFX {@link App}.</li>
     * </ul>
     * </p>
     *
     * @param args argomenti da riga di comando
     */

    public static void main(String[] args) {

        try {
            // Getting the registry
            Registry registry = LocateRegistry.getRegistry(PORT);

            // Looking up the registry for the remote object
            stub = (DBService) registry.lookup("TimeService");

            // Calling the remote method using the obtained object
            System.out.println(stub.getCurrentDateTime());
            //System.out.println("\n"+stub.getCurrentDateTime()+"\n");
            App.main(args);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }

    /*public static synchronized ClientMain getIstanza() throws RemoteException, NotBoundException {
        if (client == null) {
            client = new ClientMain();
        }
        return client;
    }*/

     /**
     * Restituisce lo stub del server remoto.
     * <p>
     * Permette alle altre classi del client di accedere ai servizi
     * esposti dal server RMI.
     * </p>
     *
     * @return lo stub {@link DBService}
     * @throws RemoteException se si verifica un errore di comunicazione RMI
     * @throws NotBoundException se il servizio non è registrato nel registry
     */

    public static synchronized DBService getServer() throws RemoteException, NotBoundException {
        return stub;
    }
}
