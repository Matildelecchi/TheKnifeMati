package com.example.theknife.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.theknife.common.DBService;
import com.example.theknife.common.Recensione;
import com.example.theknife.common.Ristorante;
import com.example.theknife.common.Utente;
/**
 * Classe principale del server RMI dell'applicazione "The Knife".
 * <p>
 * Si occupa dell'inizializzazione del server, della connessione al database
 * PostgreSQL e della pubblicazione del servizio remoto {@link DBService}
 * tramite Java RMI.
 * </p>
 *
 * <p>
 * La classe implementa l'interfaccia {@link DBService}, fornendo i metodi
 * necessari per la gestione di utenti, ristoranti, recensioni, preferiti
 * e delle altre operazioni richieste dai client dell'applicazione.
 * </p>
 *
 * <p>
 * All'avvio il server inizializza le strutture del database necessarie,
 * esporta l'oggetto remoto, crea il registry RMI e registra il servizio
 * con il nome "TimeService", rendendolo disponibile ai client.
 * </p>
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */

/**
 * Implementazione del servizio remoto {@code DBService} che gestisce
 * tutte le operazioni di accesso al database dell'applicazione TheKnife.
 *
 * <p>
 * La classe espone tramite Java RMI i servizi necessari alla gestione di
 * utenti, ristoranti, recensioni, preferiti e risposte alle recensioni.
 * Ogni operazione interagisce con il database PostgreSQL mediante JDBC
 * utilizzando query parametrizzate.
 *
 * @author Claudio Bonci, 759939, Sede CO
 * @author Eleonora Anna Caredda, 762576, Sede CO
 * @author Filippo Crippa, 762174, Sede CO
 * @author Matilde Lecchi, 759875, Sede CO
 * @version 1.0
 * @since 2026-05-20
 */

public class ServerMain implements DBService { 
    /**
     * Porta utilizzata dal registro RMI.
     */
    static int PORT = 1234;
    /**
     * Porta utilizzata per esportare l'oggetto remoto.
     */
    static int PORT_STUB = 1;
    /**
     * URL di connessione al database PostgreSQL.
     */
    private static final String URL = "jdbc:postgresql://localhost:5432/theknife";
    /**
     * Nome utente utilizzato per la connessione al database.
     */
    private static final String USER = "postgres";
    /**
     * Password utilizzata per la connessione al database.
     */
    private static final String PASSWORD = "password";

    

/**
 * Restituisce una connessione al database PostgreSQL.
 *
 * <p>
 * La connessione viene creata utilizzando i parametri configurati
 * nella classe.
 *
 * @return una connessione attiva al database.
 * @throws SQLException se la connessione non può essere stabilita.
 */
    public Connection getConnection() throws SQLException {
        // Con JDBC moderni non serve più chiamare esplicitamente Class.forName
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

/**
 * Costruisce un'istanza del server remoto.
 *
 * @throws RemoteException se si verifica un errore durante la creazione
 *                         dell'oggetto remoto.
 */
    public ServerMain() throws RemoteException {
    }

/**
 * Avvia il server RMI dell'applicazione.
 *
 * <p>
 * Il metodo inizializza il database, esporta l'oggetto remoto,
 * crea il registro RMI e registra il servizio remoto affinché
 * possa essere utilizzato dai client.
 *
 * @param args argomenti passati da riga di comando.
 * @throws RemoteException se si verifica un errore durante
 *                         l'inizializzazione del servizio remoto.
 */
    public static void main(String[] args) throws RemoteException {
        System.out.println("Hello from Server!");
        ensureDatabaseExists();
        DBService stub = null;

        ServerMain obj = new ServerMain();
        //obj.initializeDatabase();
        try {
            stub = (DBService) UnicastRemoteObject.exportObject(
                    obj, PORT_STUB);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Bind the remote object's stub in the registry
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(PORT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            registry.rebind("TimeService", stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.err.println("Server ready");
    }

/**
 * Verifica che il database 'theknife' esista; se non esiste lo crea e lo
 * popola eseguendo il dump SQL incluso nelle risorse del progetto.
 */
    private static void ensureDatabaseExists() {
        System.out.println("Verifica esistenza database 'theknife'...");
        boolean dbExists = false;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            dbExists = true;
            System.out.println("Database 'theknife' trovato.");
        } catch (SQLException e) {
            if ("3D000".equals(e.getSQLState())) {
                System.out.println("Database 'theknife' non trovato. Inizializzazione in corso...");
            } else {
                System.err.println("Errore connessione al database: " + e.getMessage());
                return;
            }
        }

        if (dbExists) return;

        // Crea il database vuoto
        String postgresUrl = "jdbc:postgresql://localhost:5432/postgres";
        try (Connection conn = DriverManager.getConnection(postgresUrl, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE theknife");
            System.out.println("Database 'theknife' creato.");
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione del database: " + e.getMessage());
            return;
        }

        // Popola il database eseguendo il dump SQL tramite psql
        InputStream sqlStream = ServerMain.class.getResourceAsStream("/data/theknife.sql");
        if (sqlStream == null) {
            System.err.println("File 'theknife.sql' non trovato nelle risorse del progetto.");
            return;
        }

        Path tempSql = null;
        try {
            tempSql = Files.createTempFile("theknife_", ".sql");
            Files.copy(sqlStream, tempSql, StandardCopyOption.REPLACE_EXISTING);

            ProcessBuilder pb = new ProcessBuilder(
                    "psql", "-U", USER, "-d", "theknife", "-f", tempSql.toAbsolutePath().toString());
            pb.environment().put("PGPASSWORD", PASSWORD);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[psql] " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Database inizializzato con successo.");
            } else {
                System.err.println("psql terminato con codice di uscita " + exitCode);
            }
        } catch (Exception e) {
            System.err.println("Errore durante l'inizializzazione del database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (tempSql != null) {
                try { Files.deleteIfExists(tempSql); } catch (Exception ignored) {}
            }
        }
    }

/**
 * Restituisce la data e l'ora correnti del server.
 *
 * @return una stringa contenente data e ora correnti.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 */
    @Override
    public String getCurrentDateTime() throws RemoteException {
        return ""+ LocalDateTime.now();
    }

/**
 * Restituisce l'elenco dei ristoranti presenti nel database.
 *
 * <p>
 * Se viene specificata una città, i risultati vengono ordinati dando
 * priorità ai ristoranti appartenenti alla città indicata, successivamente
 * a quelli dello stesso Stato e infine a tutti gli altri.
 * Se la città non viene specificata, vengono restituiti tutti i ristoranti.
 *
 * @param citta la città da utilizzare per l'ordinamento dei risultati;
 *              può essere {@code null} o vuota.
 * @param stato lo Stato associato alla città specificata.
 * @return una lista contenente i ristoranti trovati.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public ArrayList<Ristorante> getRistoranti(String citta,String stato) throws RemoteException, SQLException {
        ArrayList<Ristorante> results = new ArrayList<>();
        //System.out.println(stato);
        String query;
        if (citta == null || citta.trim().isEmpty() || stato == null || stato.trim().isEmpty()) {
            query = "SELECT * FROM ristoranti";
        } else {
            query = "SELECT * FROM ristoranti ORDER BY CASE " +
                    "WHEN citta = ? THEN 0 " +
                    "WHEN stato = ? THEN 1 ELSE 2 END";
                    //"WHEN stato = ? THEN 0"
        }
        //String query = "SELECT * FROM ristoranti";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            if (citta != null && !citta.trim().isEmpty()) {
                ps.setString(1,citta);
                ps.setString(2,stato);
            } 
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ristorante ristorante = new Ristorante(
                        rs.getString("num_tel"),
                        rs.getString("nome"),
                        rs.getString("indirizzo"),
                        rs.getString("stato"),
                        rs.getString("citta"),
                        rs.getString("servizi"),
                        rs.getString("sito"),
                        
                        rs.getString("premi"),
                        rs.getString("cucina"),
                        rs.getDouble("stelle"),
                        rs.getString("prezzo"),
                        rs.getBoolean("prenotazione"),
                        rs.getBoolean("consegna"),
                        rs.getString("descrizione"),
                        rs.getString("proprietario")
                );
                //System.out.println(ristorante);
                results.add(ristorante);
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("Errore DB getRistoranti: " + e.getMessage());
            throw e;
        }

        return results;
    }

    @Override
    public ArrayList<String> getNumeriTelefonoRisoranti(String user) throws RemoteException, SQLException{
        ArrayList<String> results = new ArrayList<>();
        //System.out.println(stato);
        if (user == null || user.trim().isEmpty()) {
            return null;
        }

        String query = "SELECT num_tel FROM ristoranti WHERE proprietario = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("num_tel"));
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("Errore DB getRistoranti: " + e.getMessage());
        }

        return results;
    }
/**
 * Restituisce tutte le recensioni associate a un determinato ristorante.
 *
 * @param num_tel il numero di telefono del ristorante.
 * @return una lista contenente le recensioni del ristorante.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public ArrayList<Recensione> getRecensioni(String num_tel) throws RemoteException, SQLException {
        ArrayList<Recensione> results = new ArrayList<>();
        if (num_tel == null || num_tel.trim().isEmpty()) {
            // TODO: implement SQL query execution for recensioni
            return results;
        }
        String query = "SELECT * from recensioni where num_tel = ?";
        //query = query.replace("?",""+num_tel+"");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, num_tel.trim());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Recensione recensione = new Recensione(
                        rs.getInt("id_rec"),
                        rs.getString("titolo"),
                        rs.getString("testo"),
                        rs.getInt("stelle"),
                        rs.getString("data_rec"),
                        rs.getString("ora"),
                        rs.getString("num_tel"),
                        rs.getString("username")
                );
                //recensione.setData(rs.getString("data"));
                //recensione.setRisposta(rs.getString("risposta"));
                results.add(recensione);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Errore DB getRecensioni: " + e.getMessage());
            throw e;
        }
        return results;
    }
/**
 * Restituisce le recensioni di un ristorante filtrandole in base al numero
 * di stelle specificato.
 *
 * <p>
 * Se l'elenco delle stelle è vuoto o nullo vengono restituite tutte le
 * recensioni del ristorante.
 * Le eventuali risposte dei ristoratori vengono recuperate insieme alle
 * recensioni.
 *
 * @param num_tel il numero di telefono del ristorante.
 * @param stars elenco dei punteggi da considerare nel filtro.
 * @return la lista delle recensioni corrispondenti ai criteri di ricerca.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public ArrayList<Recensione> getRecensioniByStars(String num_tel, List<Integer> stars)
            throws RemoteException, SQLException {
        ArrayList<Recensione> results = new ArrayList<>();
        if (num_tel == null || num_tel.trim().isEmpty()) {
            return results;
        }

        List<Integer> safeStars = stars == null ? List.of() : stars.stream()
                .filter(star -> star != null && star >= 1 && star <= 5)
                .distinct()
                .toList();

        StringBuilder query = new StringBuilder(
                "SELECT r.id_rec, r.titolo, r.testo, r.stelle, r.data_rec, r.ora, r.num_tel, r.username, " +
                        "COALESCE(rs.testo, '') AS risposta " +
                        "FROM recensioni r LEFT JOIN risposte rs ON r.id_rec = rs.id_rec " +
                        "WHERE r.num_tel = ?");

        if (!safeStars.isEmpty()) {
            String placeholders = safeStars.stream().map(star -> "?").collect(Collectors.joining(","));
            query.append(" AND r.stelle IN (").append(placeholders).append(")");
        }
        query.append(" ORDER BY r.data_rec DESC, r.id_rec DESC");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {
            int index = 1;
            ps.setString(index++, num_tel.trim());
            for (Integer star : safeStars) {
                ps.setInt(index++, star);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recensione recensione = new Recensione(
                            rs.getInt("id_rec"),
                            rs.getString("titolo"),
                            rs.getString("testo"),
                            rs.getInt("stelle"),
                            rs.getString("data_rec"),
                            rs.getString("ora"),
                            rs.getString("num_tel"),
                            rs.getString("username"));
                    recensione.setRisposta(rs.getString("risposta"));
                    results.add(recensione);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore DB getRecensioniByStars: " + e.getMessage());
            throw e;
        }

        return results;
    }

/**
 * Restituisce l'elenco degli utenti ottenuti eseguendo la query SQL specificata.
 *
 * @param query la query SQL da eseguire.
 * @return una lista contenente gli utenti trovati.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public ArrayList<Utente> getUtenti(String query) throws RemoteException, SQLException {
        ArrayList<Utente> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty() || query.contains("TODO")) {
            // TODO: implement SQL query execution for utenti
            return results;
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Utente utente = new Utente(
                        rs.getString("username"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("indirizzo"),
                        rs.getString("stato"),
                        rs.getString("citta"),
                        rs.getString("data_nascita"),
                        rs.getBoolean("ruolo")
                );
                results.add(utente);
            }

        } catch (SQLException e) {
            System.err.println("Errore DB getUtenti: " + e.getMessage());
            throw e;
        }

        return results;
    }

/**
 * Restituisce i dati relativi ai proprietari dei ristoranti.
 *
 * @param query la query SQL da eseguire.
 * @return una lista contenente i risultati della query.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public ArrayList<String> getOwnership(String query) throws RemoteException, SQLException {
        ArrayList<String> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty() || query.contains("TODO")) {
            // TODO: implement SQL query execution for ownership data
            return results;
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String row = rs.getString(1);
                if (rs.getMetaData().getColumnCount() > 1) {
                    row += "," + rs.getString(2);
                }
                results.add(row);
            }

        } catch (SQLException e) {
            System.err.println("Errore DB getOwnership: " + e.getMessage());
            if (e.getMessage() != null && (e.getMessage().contains("proprietari_ristoranti") || e.getMessage().contains("not exist"))) {
                System.err.println("Ownership table not found, returning empty results");
                return results;
            }
            throw e;
        }

        return results;
    }

/**
 * Restituisce tutti i ristoranti presenti tra i preferiti di un utente.
 *
 * @param username lo username dell'utente.
 * @return la lista dei ristoranti preferiti.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public ArrayList<Ristorante> getPreferiti(String username) throws RemoteException, SQLException {
        ArrayList<Ristorante> results = new ArrayList<>();
        if (username == null || username.trim().isEmpty()) {
            // TODO: implement SQL query execution for preferiti data
            return results;
        }
        String query = "SELECT r.* from preferiti as p join ristoranti as r on r.num_tel = p.num_tel where p.username = ?";
        //query = query.replace("?",""+username+"");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ristorante ristorante = new Ristorante(
                        rs.getString("num_tel"),
                        rs.getString("nome"),
                        rs.getString("indirizzo"),
                        rs.getString("stato"),
                        rs.getString("citta"),
                        rs.getString("servizi"),
                        rs.getString("sito"),
                        rs.getString("premi"),
                        rs.getString("cucina"),
                        rs.getDouble("stelle"),
                        rs.getString("prezzo"),
                        rs.getBoolean("prenotazione"),
                        rs.getBoolean("consegna"),
                        rs.getString("descrizione"),
                        rs.getString("proprietario")
                );
                results.add(ristorante);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Errore DB getPreferiti: " + e.getMessage());
            throw e;
        }
        return results;
    }

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
    @Override
    public boolean savePreferito(String username, String num_tel) throws RemoteException, SQLException {
        if (username == null || username.trim().isEmpty() || num_tel == null || num_tel.trim().isEmpty()) {
            return false;
        }
        
        String query = "INSERT INTO preferiti(username, num_tel) VALUES(?, ?);";
        //query = query.replace("?",""+id_rec+"");
        synchronized(this) {
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, num_tel);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB savePreferiti: " + e.getMessage());
                //throw e;
            }
        }
        
        return false;
    }

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
    @Override
    public boolean removePreferito(String username, String num_tel) throws RemoteException, SQLException {
        if (username == null || username.trim().isEmpty() || num_tel == null || num_tel.trim().isEmpty()) {
            return false;
        }
        
        String query = "DELETE FROM preferiti WHERE username = ? AND num_tel = ?;";
        //query = query.replace("?",""+id_rec+"");
        synchronized(this) {
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, num_tel);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB removePreferiti: " + e.getMessage());
                //throw e;
            }
        }
        return false;
    }

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
    @Override
    public boolean isPreferito(String username, String num_tel) throws RemoteException, SQLException {
        if (username == null || username.trim().isEmpty() || num_tel == null || num_tel.trim().isEmpty()) {
            return false;
        }
        String query = "SELECT * from preferiti where username = ? and num_tel = ?";
        //query = query.replace("?",""+username+"");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2,num_tel);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.close();
                return true;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Errore DB isPreferito: " + e.getMessage());
            throw e;
        }
        return false;
    }
/**
 * Salva un nuovo ristorante nel database utilizzando la query SQL fornita.
 *
 * <p>
 * I valori del ristorante vengono associati ai parametri della query tramite
 * un {@code PreparedStatement}.
 *
 * @param string la query SQL parametrizzata da eseguire.
 * @param ristorante il ristorante da salvare.
 * @return {@code true} se l'inserimento è avvenuto correttamente,
 *         {@code false} altrimenti.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public boolean saveRistorante(String string, Ristorante ristorante) throws RemoteException, SQLException {
        if (string == null || string.trim().isEmpty() || ristorante == null) {
            return false;
        }
        synchronized(this) {
            try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(string)) {
                ps.setString(1, ristorante.getNome());
                ps.setString(2, ristorante.getIndirizzo());
                ps.setString(3, ristorante.getCitta());
                ps.setString(4, ristorante.getPrezzo());
                ps.setString(5, ristorante.getCucina());
                ps.setString(6, ristorante.getNumeroTelefono());
                
                ps.setString(7, ristorante.getSitoWeb());
                ps.setString(8, ristorante.getPremio());
                ps.setDouble(9, ristorante.getStelle());
                ps.setString(10, ristorante.getServizi());
                ps.setString(11, ristorante.getDescrizione());
                ps.setString(12, ristorante.getStato());

                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB saveRistorante: " + e.getMessage());
                return false;
            }
        }
        
    }

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
    @Override
    public boolean saveOwnership(String string, String username, String nome) throws RemoteException {
        if (string == null || string.trim().isEmpty() || username == null || nome == null) {
            return false;
        }

        synchronized(this) {
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(string)) {
                ps.setString(1, username);
                ps.setString(2, nome);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB saveOwnership: " + e.getMessage());
                return false;
            }
        }
        
    }

/**
 * Inserisce un nuovo utente nel database.
 *
 * @param utente l'utente da registrare.
 * @return {@code true} se l'inserimento è avvenuto correttamente,
 *         {@code false} altrimenti.
 */
    @Override
    public Boolean setUtente(Utente utente) {
        if (utente == null) {
            return false;
        }

        String insertQuery = "INSERT INTO utenti (username, nome, cognome, email, password_hash, indirizzo, stato, citta, data_nascita, ruolo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        synchronized(this) {
            try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(insertQuery)) {

                ps.setString(1, utente.getUsername());
                ps.setString(2, utente.getNome());
                ps.setString(3, utente.getCognome());
                ps.setString(4, utente.getEmail());
                ps.setString(5, utente.getPasswordHash());
                ps.setString(6, utente.getLuogoDomicilio());
                ps.setString(7, utente.getStato());
                ps.setString(8, utente.getCitta());
                ps.setDate(9, utente.getDataNascitaSql());
                ps.setBoolean(10, utente.isRistoratore());

                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB setUtente: " + e.getMessage());
                return false;
            }
        }
        
    }

/**
 * Inserisce un nuovo ristorante nel database associandolo al proprietario
 * specificato.
 *
 * <p>
 * Il proprietario viene memorizzato tramite il relativo username.
 *
 * @param ristorante il ristorante da inserire.
 * @param utente lo username del proprietario del ristorante.
 * @return {@code true} se il ristorante è stato inserito correttamente,
 *         {@code false} altrimenti.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 */
    @Override
    public Boolean setRistorante(Ristorante ristorante,String utente) throws RemoteException {
        if (ristorante == null) {
            return false;
        }
        //System.out.println(ristorante);

        // Aggiunta della foreign key 'username' che punta alla tabella 'utenti'
        String insertQuery = "INSERT INTO ristoranti (nome, indirizzo, citta, prezzo, cucina, num_tel, sito, sitoPremio, premi, stelle, servizi, descrizione, proprietario, stato) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        synchronized(this) {
            try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {

                ps.setString(1, ristorante.getNome());
                ps.setString(2, ristorante.getIndirizzo());
                ps.setString(3, ristorante.getCitta());
                ps.setString(4, ristorante.getPrezzo());
                ps.setString(5, ristorante.getCucina());
                ps.setString(6, ristorante.getNumeroTelefono());
                ps.setString(7, ristorante.getSitoWeb());
                ps.setString(8, ristorante.getUrl());
                ps.setString(9, ristorante.getPremio());
                ps.setDouble(10, ristorante.getStellaVerde());
                ps.setString(11, ristorante.getServizi());
                ps.setString(12, ristorante.getDescrizione());
                // Imposta la foreign key verso l'utente proprietario (username)
                ps.setString(13, utente);
                ps.setString(14, ristorante.getStato());

                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB setRistorante: " + e.getMessage());
                return false;
            }
        }
        
    }

/**
 * Restituisce il ristorante associato al numero di telefono specificato.
 *
 * @param num_tel il numero di telefono del ristorante.
 * @return il ristorante corrispondente oppure {@code null} se non esiste.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public Ristorante getRistorante(String num_tel) throws RemoteException, SQLException {
        Ristorante results = null;
        if (num_tel == null || num_tel.trim().isEmpty()) {
            // TODO: implement SQL query execution for ristoranti
            return results;
        }
        String query = "SELECT * from ristoranti where num_tel = ?";
        //query = query.replace("?", "" + num_tel + "");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, num_tel);
            ResultSet rs = ps.executeQuery();
            Ristorante ristorante = null;
            if(rs.next()) {
                ristorante = new Ristorante(
                    rs.getString("num_tel"),
                    rs.getString("nome"),
                    rs.getString("indirizzo"),
                    rs.getString("stato"),
                    rs.getString("citta"),
                    rs.getString("servizi"),
                    rs.getString("sito"),
                    rs.getString("premi"),
                    rs.getString("cucina"),
                    rs.getDouble("stelle"),
                    rs.getString("prezzo"),
                    rs.getBoolean("prenotazione"),
                    rs.getBoolean("consegna"),
                    rs.getString("descrizione"),
                    rs.getString("proprietario")
                );
            }
            //System.out.println(ristorante);
            results = ristorante;
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Errore DB getRistorante: " + e.getMessage());
            throw e;
        }
        return results;
    }

/**
 * Restituisce tutti i ristoranti appartenenti a un determinato utente.
 *
 * <p>
 * La ricerca viene effettuata utilizzando lo username del proprietario.
 *
 * @param username lo username del proprietario.
 * @return la lista dei ristoranti appartenenti all'utente.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public ArrayList<Ristorante> getRistorantiByUsername(String username) throws RemoteException, SQLException {
        ArrayList<Ristorante> results = new ArrayList<>();
        if (username == null || username.trim().isEmpty()) {
            // TODO: implement SQL query execution for ristoranti
            return results;
        }
        String query = "SELECT * FROM ristoranti WHERE proprietario = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1,username);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Ristorante ristorante = new Ristorante(
                        rs.getString("num_tel"),
                        rs.getString("nome"),
                        rs.getString("indirizzo"),
                        rs.getString("stato"),
                        rs.getString("citta"),
                        rs.getString("servizi"),
                        rs.getString("sito"),
                        rs.getString("premi"),
                        rs.getString("cucina"),
                        rs.getDouble("stelle"),
                        rs.getString("prezzo"),
                        rs.getBoolean("prenotazione"),
                        rs.getBoolean("consegna"),
                        rs.getString("descrizione"),
                        rs.getString("proprietario")
                );
                //System.out.println(ristorante);
                results.add(ristorante);
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("Errore DB getRistorantiByUsername: " + e.getMessage());
            throw e;
        }

        return results;
    }

/**
 * Restituisce tutte le recensioni pubblicate da un determinato utente.
 *
 * @param username lo username dell'utente.
 * @return una lista contenente le recensioni scritte dall'utente.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public ArrayList<Recensione> getRecensioniByUsername(String username) throws RemoteException, SQLException {
        ArrayList<Recensione> results = new ArrayList<>();
        if (username == null || username.trim().isEmpty()) {
            // TODO: implement SQL query execution for recensioni
            return results;
        }
        String query = "SELECT * from recensioni where username = ?";
        //query = query.replace("?",""+username+"");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Recensione recensione = new Recensione(
                        rs.getInt("id_rec"),
                        rs.getString("titolo"),
                        rs.getString("testo"),
                        rs.getInt("stelle"),
                        rs.getString("data_rec"),
                        rs.getString("ora"),
                        rs.getString("num_tel"),
                        rs.getString("username")
                );
                //recensione.setData(rs.getString("data_rec"));
                //recensione.setRisposta(rs.getString("risposta"));
                results.add(recensione);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Errore DB getRecensioniByUsername: " + e.getMessage());
            throw e;
        }
        return results;
    }

/**
 * Verifica se una risposta è già associata a una determinata recensione.
 *
 * @param id_rec l'identificativo della recensione.
 * @return {@code true} se la risposta esiste, {@code false} altrimenti.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    public boolean existRisposta(int id_rec) throws RemoteException, SQLException {
        //ArrayList<String> results = new ArrayList<>();
        if (id_rec <= 0) {
            // TODO: implement SQL query execution for utenti
            return false;
        }
        String query = "SELECT testo from risposte where id_rec = ?";
        //query = query.replace("?",""+id_rec+"");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id_rec);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return true;
            //results.add(String.valueOf(rs.getInt("id_rec")));
            //results.add(rs.getString("testo"));
            
            rs.close();
        } catch (SQLException e) {
            System.err.println("Errore DB existRisposta: " + e.getMessage());
            throw e;
        }
        //return results;
        return false;
    }

/**
 * Restituisce la risposta associata a una recensione.
 *
 * @param id_rec l'identificativo della recensione.
 * @return il testo della risposta oppure una stringa vuota se non presente.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override 
    public String getRisposta(int id_rec) throws RemoteException, SQLException {
        String results = "";
        if (id_rec <= 0 || !existRisposta(id_rec)) {
            // TODO: implement SQL query execution for utenti
            return "";
        }
        String query = "SELECT 1 from risposte where id_rec = ?";
        //query = query.replace("?",""+id_rec+"");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id_rec);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) results = rs.getString("testo");
            //results.add(String.valueOf(rs.getInt("id_rec")));
            //results.add(rs.getString("testo"));
            
            rs.close();
        } catch (SQLException e) {
            System.err.println("Errore DB getRisposta: " + e.getMessage());
            throw e;
        }
        return results;
    }

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
    @Override
    public boolean saveRisposta(int id_rec, String testo) throws RemoteException, SQLException {
        //ArrayList results = new ArrayList<>();
        if (id_rec <= 0) {
            // TODO: implement SQL query execution for recensioni
            return false;
        }
        synchronized(this) {
            if (existRisposta(id_rec)) {
                return false;
            }
            String query = "INSERT INTO risposte(id_rec, testo) VALUES(?, ?);";
            //query = query.replace("?",""+id_rec+"");
            try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, id_rec);
                ps.setString(2, testo);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB saveRisposta: " + e.getMessage());
                throw e;
            }
        }
        //return true;
    }

/**
 * Elimina la risposta associata a una recensione.
 *
 * @param id_rec l'identificativo della recensione.
 * @return {@code true} se la risposta è stata eliminata correttamente,
 *         {@code false} altrimenti.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public boolean removeRisposta(int id_rec) throws RemoteException, SQLException {
        //ArrayList results = new ArrayList<>();
        if (id_rec <= 0) {
            // TODO: implement SQL query execution for recensioni
            return false;
        }
        synchronized(this) {
            if (!existRisposta(id_rec)) {
                return false;
            }
            String query = "DELETE FROM risposte WHERE id_rec = ?;";
            //query = query.replace("?",""+id_rec+"");
            try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, id_rec);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB removeRisposta: " + e.getMessage());
                throw e;
            }
        }
        //return true;
    }

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
    @Override
    public boolean modifyRisposta(int id_rec, String testo) throws RemoteException, SQLException {
        //ArrayList results = new ArrayList<>();
        if (id_rec <= 0) {
            // TODO: implement SQL query execution for recensioni
            return false;
        }
        synchronized(this) {
            if (!existRisposta(id_rec)) {
                return false;
            }
            String query = "UPDATE risposte SET testo = ? WHERE id_rec = ?;";
            //query = query.replace("?",""+id_rec+"");
            try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, testo);
                ps.setInt(2, id_rec);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB modifyRisposta: " + e.getMessage());
                throw e;
            }
        }
        //return true;
    }

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
    @Override
    public boolean saveRecensione(String titolo, String testo, double stelle, String num_tel, String username) throws RemoteException, SQLException {
        //ArrayList results = new ArrayList<>();
        //int id_rec = nextRecensioneId();
        /*if (id_rec <= 0 || existRecensione(id_rec)) {
            // TODO: implement SQL query execution for recensioni
            return false;
        }*/
        String query = "INSERT INTO recensioni(titolo,testo,stelle,num_tel,username)" +
                       "VALUES(?,?,?,?,?);";
        //query = query.replace("?",""+id_rec+"");
        synchronized(this) {
            try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, titolo);
                ps.setString(2, testo);
                ps.setDouble(3, stelle);
                ps.setString(4, num_tel);
                ps.setString(5, username);

                //ps.executeUpdate();
                //return true;
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB saveRecensione: " + e.getMessage());
                throw e;
            }
        }
        
        //return false;
    }

/**
 * Verifica se una recensione con l'identificativo specificato è presente
 * nel database.
 *
 * @param id_rec l'identificativo della recensione.
 * @return {@code true} se la recensione esiste, {@code false} altrimenti.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    public boolean existRecensione(int id_rec) throws RemoteException, SQLException {
        String query = "SELECT * from recensioni where id_rec = ?";
        //query = query.replace("?",""+id_rec+"");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id_rec);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Errore DB existRecensione: " + e.getMessage());
            throw e;
        }
        return false;
    }

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
    @Override
    public boolean modifyRecensione(int id_rec, String titolo, String testo, double stelle) throws RemoteException, SQLException {
        //ArrayList results = new ArrayList<>();
        if (id_rec <= 0 || !existRecensione(id_rec)) {
            // TODO: implement SQL query execution for recensioni
            return false;
        }
        String query = "UPDATE recensioni SET testo = ?, titolo = ?, stelle = ? WHERE id_rec = ?;";
        //query = query.replace("?",""+id_rec+"");
        synchronized(this) {
            if (!existRecensione(id_rec)) return false;
            try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, testo);
                ps.setString(2, titolo);
                ps.setDouble(3, stelle);
                ps.setInt(4,id_rec);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB modifyRecensione: " + e.getMessage());
                throw e;
            }
        }
        //return true;
    }

/**
 * Elimina una recensione dal database.
 *
 * @param id_rec l'identificativo della recensione da eliminare.
 * @return {@code true} se la recensione è stata eliminata correttamente,
 *         {@code false} altrimenti.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public boolean removeRecensione(int id_rec) throws RemoteException, SQLException {
        //ArrayList results = new ArrayList<>();
        if (id_rec <= 0) {
            // TODO: implement SQL query execution for recensioni
            return false;
        }
        String query = "DELETE FROM recensioni WHERE id_rec = ?;";

        //query = query.replace("?",""+id_rec+"");
        synchronized(this) {
            if(!existRecensione(id_rec)) return false;
            try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, id_rec);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB removeRecensione: " + e.getMessage());
                throw e;
            }
        }
        //return false;
    }

/**
 * Restituisce un utente identificato dal relativo username.
 *
 * @param username lo username dell'utente da ricercare.
 * @return l'utente corrispondente oppure {@code null} se non esiste.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public Utente getUtenteByUsername(String username) throws RemoteException, SQLException {
        // ArrayList<Utente> results = new ArrayList<>();
        if (username == null || username.trim().isEmpty() || username.contains("TODO")) {
            // TODO: implement SQL query execution for utenti
            return null;
        }

        String query = "SELECT * FROM utenti WHERE username = ?;";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Utente utente = new Utente(
                        rs.getString("username"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("indirizzo"),
                        rs.getString("stato"),
                        rs.getString("citta"),
                        rs.getString("data_nascita"),
                        rs.getBoolean("ruolo"));
                return utente;
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("Errore DB getUtenteByUsername: " + e.getMessage());
            throw e;
        }
        return null;
    }
	
/**
 * Modifica il valore di uno specifico campo appartenente a un utente.
 *
 * <p>
 * Il campo da modificare viene specificato tramite il parametro
 * {@code field}, mentre il nuovo valore è indicato nel parametro
 * {@code set}.
 *
 * @param username lo username dell'utente da modificare.
 * @param field il nome della colonna da aggiornare.
 * @param set il nuovo valore da assegnare.
 * @return {@code true} se la modifica è stata eseguita correttamente,
 *         {@code false} altrimenti.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
	@Override
    public boolean modifyUsernameCampo(String username, String field, String set) throws RemoteException, SQLException {
        if (username == null || username.trim().isEmpty() || username.contains("TODO")) {
            // TODO: implement SQL query execution for utenti
            return false;
        }

        String query = "UPDATE utenti SET " + field + " = ? WHERE username = ?;";
        synchronized(this) {
            try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, set);
                ps.setString(2, username);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Errore DB modifyRecensione: " + e.getMessage());
                throw e;
            }
        }
    }

/**
 * Restituisce la media delle valutazioni assegnate a un ristorante.
 *
 * <p>
 * La media viene calcolata considerando tutte le recensioni associate
 * al numero di telefono specificato.
 * Se il ristorante non possiede recensioni viene restituito {@code 0.0}.
 *
 * @param num_tel il numero di telefono del ristorante.
 * @return la media delle valutazioni.
 * @throws RemoteException se si verifica un errore nella comunicazione remota.
 * @throws SQLException se si verifica un errore durante l'accesso al database.
 */
    @Override
    public double getStelleByTel(String num_tel) throws RemoteException, SQLException {
        double val = 0.0;
        int num = 0;
        if (num_tel == null || num_tel.trim().isEmpty()) {
            // TODO: implement SQL query execution for recensioni
            return 0.0;
        }
        String query = "SELECT stelle from recensioni where num_tel = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, num_tel);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                val += rs.getDouble("stelle");
                num++;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Errore DB getStelle: " + e.getMessage());
            throw e;
        }
        if(num==0) return 0; //oppure deafult 3
        return val / num;
    }
}

