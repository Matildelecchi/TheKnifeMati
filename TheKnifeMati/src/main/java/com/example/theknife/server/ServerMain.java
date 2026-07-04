package com.example.theknife.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.theknife.common.DBService;
import com.example.theknife.common.Recensione;
import com.example.theknife.common.Ristorante;
import com.example.theknife.common.Utente;

public class ServerMain implements DBService {
    static int PORT = 1234;
    static int PORT_STUB = 1;
    private static final String URL = "jdbc:postgresql://localhost:5432/theknife";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    


    public Connection getConnection() throws SQLException {
        // Con JDBC moderni non serve più chiamare esplicitamente Class.forName
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    public ServerMain() throws RemoteException {
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS proprietari_ristoranti (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(255) NOT NULL, " +
                    "ristorante VARCHAR(255) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "UNIQUE(username, ristorante))";
            
            try (PreparedStatement ps = conn.prepareStatement(createTableSQL)) {
                ps.execute();
                System.out.println("proprietari_ristoranti table initialized");
            }
            
            String createIndexSQL1 = "CREATE INDEX IF NOT EXISTS idx_proprietari_username ON proprietari_ristoranti(username)";
            String createIndexSQL2 = "CREATE INDEX IF NOT EXISTS idx_proprietari_ristorante ON proprietari_ristoranti(ristorante)";
            
            try (PreparedStatement ps = conn.prepareStatement(createIndexSQL1)) {
                ps.execute();
            }
            try (PreparedStatement ps = conn.prepareStatement(createIndexSQL2)) {
                ps.execute();
            }
            System.out.println("Database indexes initialized");
        } catch (SQLException e) {
            System.err.println("Errore durante l'inizializzazione del database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RemoteException {
        System.out.println("Hello from Server!");
        DBService stub = null;

        ServerMain obj = new ServerMain();
        obj.initializeDatabase();
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


    @Override
    public String getCurrentDateTime() throws RemoteException {
        return ""+ LocalDateTime.now();
    }
    @Override
    public ArrayList<Ristorante> getRistoranti(String citta,String stato) throws RemoteException, SQLException {
        ArrayList<Ristorante> results = new ArrayList<>();
        //System.out.println(stato);
        String query;
        if (citta == null || citta.trim().isEmpty()) {
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

    @Override
    public boolean savePreferito(String username, String num_tel) throws RemoteException, SQLException {
        if (username == null || username.trim().isEmpty() || num_tel == null || num_tel.trim().isEmpty()) {
            return false;
        }
        
        String query = "INSERT INTO preferiti(username, num_tel) VALUES(?, ?);";
        //query = query.replace("?",""+id_rec+"");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, num_tel);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore DB savePreferiti: " + e.getMessage());
            //throw e;
        }
        return false;
    }

    @Override
    public boolean removePreferito(String username, String num_tel) throws RemoteException, SQLException {
        if (username == null || username.trim().isEmpty() || num_tel == null || num_tel.trim().isEmpty()) {
            return false;
        }
        
        String query = "DELETE FROM preferiti WHERE username = ? AND num_tel = ?;";
        //query = query.replace("?",""+id_rec+"");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, num_tel);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore DB removePreferiti: " + e.getMessage());
            //throw e;
        }
        return false;
    }

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
            System.err.println("Errore DB getPreferiti: " + e.getMessage());
            throw e;
        }
        return false;
    }

    @Override
    public boolean saveRistorante(String string, Ristorante ristorante) throws RemoteException, SQLException {
        if (string == null || string.trim().isEmpty() || ristorante == null) {
            return false;
        }

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

    @Override
    public boolean saveOwnership(String string, String username, String nome) throws RemoteException {
        if (string == null || string.trim().isEmpty() || username == null || nome == null) {
            return false;
        }

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


    @Override
    public Boolean setUtente(Utente utente) {
        if (utente == null) {
            return false;
        }

        String insertQuery = "INSERT INTO utenti (username, nome, cognome, email, password_hash, indirizzo, stato, citta, data_nascita, ruolo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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


    @Override
    public Boolean setRistorante(Ristorante ristorante,String utente) throws RemoteException {
        if (ristorante == null) {
            return false;
        }
        //System.out.println(ristorante);

        // Aggiunta della foreign key 'username' che punta alla tabella 'utenti'
        String insertQuery = "INSERT INTO ristoranti (nome, indirizzo, citta, prezzo, cucina, num_tel, sito, sitoPremio, premi, stelle, servizi, descrizione, proprietario, stato) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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


    public boolean existRisposta(int id_rec) throws RemoteException, SQLException {
        //ArrayList<String> results = new ArrayList<>();
        if (id_rec <= 0) {
            // TODO: implement SQL query execution for utenti
            return false;
        }
        String query = "SELECT 1 from risposte where id_rec = ?";
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


    @Override
    public boolean saveRisposta(int id_rec, String testo) throws RemoteException, SQLException {
        //ArrayList results = new ArrayList<>();
        if (id_rec <= 0 || existRisposta(id_rec)) {
            // TODO: implement SQL query execution for recensioni
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
        //return true;
    }


    @Override
    public boolean removeRisposta(int id_rec) throws RemoteException, SQLException {
        //ArrayList results = new ArrayList<>();
        if (id_rec <= 0 || !existRisposta(id_rec)) {
            // TODO: implement SQL query execution for recensioni
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
        //return true;
    }


    @Override
    public boolean modifyRisposta(int id_rec, String testo) throws RemoteException, SQLException {
        //ArrayList results = new ArrayList<>();
        if (id_rec <= 0 || !existRisposta(id_rec)) {
            // TODO: implement SQL query execution for recensioni
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
        //return true;
    }


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
        //return false;
    }

    /*public int nextRecensioneId() {
        String query = "SELECT COALESCE(MAX(id_rec), 0) + 1 AS next_id FROM recensioni";
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("next_id");
            }
        } catch (SQLException e) {
            System.err.println("Errore DB nextRecensioneId: " + e.getMessage());
        }
        return 1;
    }*/

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


    @Override
    public boolean modifyRecensione(int id_rec, String titolo, String testo, double stelle) throws RemoteException, SQLException {
        //ArrayList results = new ArrayList<>();
        if (id_rec <= 0 || !existRecensione(id_rec)) {
            // TODO: implement SQL query execution for recensioni
            return false;
        }
        String query = "UPDATE recensioni SET testo = ?, titolo = ?, stelle = ? WHERE id_rec = ?;";
        //query = query.replace("?",""+id_rec+"");
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
        //return true;
    }


    @Override
    public boolean removeRecensione(int id_rec) throws RemoteException, SQLException {
        //ArrayList results = new ArrayList<>();
        if (id_rec <= 0 || !existRecensione(id_rec)) {
            // TODO: implement SQL query execution for recensioni
            return false;
        }
        String query = "DELETE FROM recensioni WHERE id_rec = ?;";
        //query = query.replace("?",""+id_rec+"");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id_rec);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore DB removeRecensione: " + e.getMessage());
            throw e;
        }
        //return false;
    }


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

