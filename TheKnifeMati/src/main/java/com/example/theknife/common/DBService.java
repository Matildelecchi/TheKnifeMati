package com.example.theknife.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interfaccia remota RMI che definisce i servizi di accesso al database
 * dell'applicazione TheKnife.
 * <p>
 * Questa interfaccia espone tutte le operazioni CRUD (Create, Read, Update, Delete)
 * relative a utenti, ristoranti, recensioni, preferiti e proprietà dei ristoranti.
 * Le chiamate avvengono tramite Java RMI e possono generare eccezioni di rete
 * o di accesso al database.
 * </p>
 *
 * <p>
 * Tutti i metodi sono remoti e possono lanciare {@link RemoteException}.
 * Molti metodi possono inoltre lanciare {@link SQLException} in caso di errori
 * durante l’interazione con il database.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */

public interface DBService extends Remote {

     /** Restituisce data e ora corrente dal server. */
    String getCurrentDateTime() throws RemoteException;
     /** Recupera utenti in base a una query SQL. */
    ArrayList<Utente> getUtenti(String query) throws RemoteException, SQLException;
     /** Recupera le informazioni di ownership (proprietà ristoranti). */
    ArrayList<String> getOwnership(String query) throws RemoteException, SQLException;
   /** Salva o aggiorna un ristorante nel database. */
    boolean saveRistorante(String string, Ristorante ristorante) throws RemoteException, SQLException;
    /** Associa un ristorante a un utente proprietario. */
    boolean saveOwnership(String string, String username, String nome) throws RemoteException ;
    /** Inserisce un nuovo utente nel database. */
    Boolean setUtente(Utente utente) throws RemoteException ;
     /** Inserisce un nuovo ristorante nel database associandolo all’utente creatore. */
    Boolean setRistorante(Ristorante ristorante,String utente) throws RemoteException ;
    /** Recupera un ristorante tramite numero di telefono. */
    Ristorante getRistorante(String num_tel) throws RemoteException, SQLException;
    /** Recupera i ristoranti filtrati per città e stato. */
    ArrayList<Ristorante> getRistoranti(String citta, String stato) throws RemoteException, SQLException;
     /** Recupera tutte le recensioni di un ristorante. */
    ArrayList<Recensione> getRecensioni(String num_tel) throws RemoteException, SQLException;
    /** Recupera le recensioni filtrate per numero di stelle. */
    ArrayList<Recensione> getRecensioniByStars(String num_tel, List<Integer> stars) throws RemoteException, SQLException;
     /** Recupera le recensioni scritte da un determinato utente. */
    ArrayList<Recensione> getRecensioniByUsername(String username) throws RemoteException, SQLException;
     /** Recupera la lista dei ristoranti preferiti di un utente. */
    ArrayList<Ristorante> getPreferiti(String username) throws RemoteException, SQLException;
    /** Recupera la risposta del ristoratore a una recensione. */
    String getRisposta(int id_rec) throws RemoteException, SQLException;
     /** Salva una risposta del ristoratore a una recensione. */
    boolean saveRisposta(int id_rec, String testo) throws RemoteException, SQLException;
   /** Rimuove una risposta a una recensione. */
    boolean removeRisposta(int id_rec) throws RemoteException, SQLException;
     /** Modifica una risposta a una recensione esistente. */
    boolean modifyRisposta(int id_rec, String testo) throws RemoteException, SQLException;
    /** Inserisce una nuova recensione per un ristorante. */
    boolean saveRecensione(String titolo, String testo, double stelle, String num_tel, String username) throws RemoteException, SQLException;
    /** Modifica una recensione esistente. */
    boolean modifyRecensione(int id_rec, String titolo, String testo, double stelle) throws RemoteException, SQLException;
    /** Elimina una recensione. */
    boolean removeRecensione(int id_rec) throws RemoteException, SQLException;
    /** Calcola la media delle stelle di un ristorante. */
    double getStelleByTel(String num_tel) throws RemoteException, SQLException;
    /** Recupera i ristoranti associati a uno specifico utente. */
    ArrayList<Ristorante> getRistorantiByUsername(String username) throws RemoteException, SQLException;
     /** Verifica se un ristorante è nei preferiti di un utente. */
    boolean isPreferito(String username, String num_tel) throws RemoteException, SQLException;
    /** Rimuove un ristorante dai preferiti di un utente. */
    boolean removePreferito(String username, String num_tel) throws RemoteException, SQLException;
    /** Aggiunge un ristorante ai preferiti di un utente. */
    boolean savePreferito(String username, String num_tel) throws RemoteException, SQLException;
     /** Recupera un utente tramite username. */
    Utente getUtenteByUsername(String username) throws RemoteException, SQLException;
    /** Modifica un campo specifico dell’utente (es. email, password, ecc.). */
    boolean modifyUsernameCampo(String username, String field, String set) throws RemoteException, SQLException;

    ArrayList<String> getNumeriTelefonoRisoranti(String user) throws RemoteException, SQLException;
}

