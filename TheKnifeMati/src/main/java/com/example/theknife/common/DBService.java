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
 * durante l'interazione con il database.
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

    /**
     * Restituisce la data e l'ora correnti del server.
     *
     * @return una stringa contenente data e ora correnti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     */
    String getCurrentDateTime() throws RemoteException;

    /**
     * Restituisce l'elenco degli utenti ottenuti eseguendo la query SQL specificata.
     *
     * @param query la query SQL da eseguire.
     * @return una lista contenente gli utenti trovati.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    ArrayList<Utente> getUtenti(String query) throws RemoteException, SQLException;

    /**
     * Restituisce i dati relativi ai proprietari dei ristoranti.
     *
     * @param query la query SQL da eseguire.
     * @return una lista contenente i risultati della query.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    ArrayList<String> getOwnership(String query) throws RemoteException, SQLException;

    /**
     * Salva un nuovo ristorante nel database utilizzando la query SQL fornita.
     *
     * @param string la query SQL parametrizzata da eseguire.
     * @param ristorante il ristorante da salvare.
     * @return {@code true} se l'inserimento è avvenuto correttamente,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    boolean saveRistorante(String string, Ristorante ristorante) throws RemoteException, SQLException;

    /**
     * Salva l'associazione tra un utente e un ristorante di sua proprietà.
     *
     * @param string la query SQL parametrizzata da eseguire.
     * @param username lo username del proprietario.
     * @param nome il nome del ristorante.
     * @return {@code true} se l'associazione è stata salvata correttamente,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     */
    boolean saveOwnership(String string, String username, String nome) throws RemoteException;

    /**
     * Inserisce un nuovo utente nel database.
     *
     * @param utente l'utente da registrare.
     * @return {@code true} se l'inserimento è avvenuto correttamente,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     */
    Boolean setUtente(Utente utente) throws RemoteException;

    /**
     * Inserisce un nuovo ristorante nel database associandolo al proprietario
     * specificato.
     *
     * @param ristorante il ristorante da inserire.
     * @param utente lo username del proprietario del ristorante.
     * @return {@code true} se il ristorante è stato inserito correttamente,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     */
    Boolean setRistorante(Ristorante ristorante, String utente) throws RemoteException;

    /**
     * Restituisce il ristorante associato al numero di telefono specificato.
     *
     * @param num_tel il numero di telefono del ristorante.
     * @return il ristorante corrispondente oppure {@code null} se non esiste.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    Ristorante getRistorante(String num_tel) throws RemoteException, SQLException;

    /**
     * Restituisce l'elenco dei ristoranti presenti nel database.
     *
     * @param citta la città da utilizzare per l'ordinamento dei risultati;
     *              può essere {@code null} o vuota.
     * @param stato lo Stato associato alla città specificata.
     * @return una lista contenente i ristoranti trovati.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    ArrayList<Ristorante> getRistoranti(String citta, String stato) throws RemoteException, SQLException;

    /**
     * Restituisce tutte le recensioni associate a un determinato ristorante.
     *
     * @param num_tel il numero di telefono del ristorante.
     * @return una lista contenente le recensioni del ristorante.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    ArrayList<Recensione> getRecensioni(String num_tel) throws RemoteException, SQLException;

    /**
     * Restituisce le recensioni di un ristorante filtrandole in base al numero
     * di stelle specificato.
     *
     * @param num_tel il numero di telefono del ristorante.
     * @param stars elenco dei punteggi da considerare nel filtro.
     * @return la lista delle recensioni corrispondenti ai criteri di ricerca.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    ArrayList<Recensione> getRecensioniByStars(String num_tel, List<Integer> stars) throws RemoteException, SQLException;

    /**
     * Restituisce tutte le recensioni pubblicate da un determinato utente.
     *
     * @param username lo username dell'utente.
     * @return una lista contenente le recensioni scritte dall'utente.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    ArrayList<Recensione> getRecensioniByUsername(String username) throws RemoteException, SQLException;

    /**
     * Restituisce tutti i ristoranti presenti tra i preferiti di un utente.
     *
     * @param username lo username dell'utente.
     * @return la lista dei ristoranti preferiti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    ArrayList<Ristorante> getPreferiti(String username) throws RemoteException, SQLException;

    /**
     * Restituisce la risposta associata a una recensione.
     *
     * @param id_rec l'identificativo della recensione.
     * @return il testo della risposta oppure una stringa vuota se non presente.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    String getRisposta(int id_rec) throws RemoteException, SQLException;

    /**
     * Inserisce una risposta a una recensione.
     *
     * @param id_rec l'identificativo della recensione.
     * @param testo il testo della risposta.
     * @return {@code true} se la risposta è stata salvata correttamente,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    boolean saveRisposta(int id_rec, String testo) throws RemoteException, SQLException;

    /**
     * Elimina la risposta associata a una recensione.
     *
     * @param id_rec l'identificativo della recensione.
     * @return {@code true} se la risposta è stata eliminata correttamente,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    boolean removeRisposta(int id_rec) throws RemoteException, SQLException;

    /**
     * Modifica il testo della risposta associata a una recensione.
     *
     * @param id_rec l'identificativo della recensione.
     * @param testo il nuovo testo della risposta.
     * @return {@code true} se la modifica è stata eseguita correttamente,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    boolean modifyRisposta(int id_rec, String testo) throws RemoteException, SQLException;

    /**
     * Inserisce una nuova recensione nel database.
     *
     * @param titolo il titolo della recensione.
     * @param testo il testo della recensione.
     * @param stelle la valutazione assegnata al ristorante.
     * @param num_tel il numero di telefono del ristorante recensito.
     * @param username lo username dell'autore della recensione.
     * @return {@code true} se la recensione è stata inserita correttamente,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    boolean saveRecensione(String titolo, String testo, double stelle, String num_tel, String username) throws RemoteException, SQLException;

    /**
     * Modifica il titolo, il testo e la valutazione di una recensione esistente.
     *
     * @param id_rec l'identificativo della recensione.
     * @param titolo il nuovo titolo.
     * @param testo il nuovo testo.
     * @param stelle la nuova valutazione.
     * @return {@code true} se la modifica è stata eseguita correttamente,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    boolean modifyRecensione(int id_rec, String titolo, String testo, double stelle) throws RemoteException, SQLException;

    /**
     * Elimina una recensione dal database.
     *
     * @param id_rec l'identificativo della recensione da eliminare.
     * @return {@code true} se la recensione è stata eliminata correttamente,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    boolean removeRecensione(int id_rec) throws RemoteException, SQLException;

    /**
     * Restituisce la media delle valutazioni assegnate a un ristorante.
     *
     * @param num_tel il numero di telefono del ristorante.
     * @return la media delle valutazioni.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    double getStelleByTel(String num_tel) throws RemoteException, SQLException;

    /**
     * Restituisce tutti i ristoranti appartenenti a un determinato utente.
     *
     * @param username lo username del proprietario.
     * @return la lista dei ristoranti appartenenti all'utente.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    ArrayList<Ristorante> getRistorantiByUsername(String username) throws RemoteException, SQLException;

    /**
     * Verifica se un ristorante è presente tra i preferiti di un utente.
     *
     * @param username lo username dell'utente.
     * @param num_tel il numero di telefono del ristorante.
     * @return {@code true} se il ristorante è presente tra i preferiti,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    boolean isPreferito(String username, String num_tel) throws RemoteException, SQLException;

    /**
     * Rimuove un ristorante dall'elenco dei preferiti di un utente.
     *
     * @param username lo username dell'utente.
     * @param num_tel il numero di telefono del ristorante.
     * @return {@code true} se il ristorante è stato rimosso dai preferiti,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    boolean removePreferito(String username, String num_tel) throws RemoteException, SQLException;

    /**
     * Aggiunge un ristorante all'elenco dei preferiti di un utente.
     *
     * @param username lo username dell'utente.
     * @param num_tel il numero di telefono del ristorante.
     * @return {@code true} se il ristorante è stato aggiunto ai preferiti,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    boolean savePreferito(String username, String num_tel) throws RemoteException, SQLException;

    /**
     * Restituisce un utente identificato dal relativo username.
     *
     * @param username lo username dell'utente da ricercare.
     * @return l'utente corrispondente oppure {@code null} se non esiste.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    Utente getUtenteByUsername(String username) throws RemoteException, SQLException;

    /**
     * Modifica il valore di uno specifico campo appartenente a un utente.
     *
     * @param username lo username dell'utente da modificare.
     * @param field il nome della colonna da aggiornare.
     * @param set il nuovo valore da assegnare.
     * @return {@code true} se la modifica è stata eseguita correttamente,
     *         {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    boolean modifyUsernameCampo(String username, String field, String set) throws RemoteException, SQLException;

    /**
     * Restituisce l'elenco dei numeri di telefono dei ristoranti di proprietà
     * di un determinato utente.
     *
     * @param user lo username del proprietario dei ristoranti.
     * @return una lista contenente i numeri di telefono dei ristoranti associati
     *         all'utente.
     * @throws RemoteException se si verifica un errore nella comunicazione remota.
     * @throws SQLException se si verifica un errore durante l'accesso al database.
     */
    ArrayList<String> getNumeriTelefonoRisoranti(String user) throws RemoteException, SQLException;
}