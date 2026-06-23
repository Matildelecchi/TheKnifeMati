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
    public ArrayList<Ristorante> getRistoranti(String query) throws RemoteException, SQLException {
        ArrayList<Ristorante> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty() || query.contains("TODO")) {
            // TODO: implement SQL query execution for ristoranti
            return results;
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

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
                System.out.println(ristorante);
                results.add(ristorante);
            }

        } catch (SQLException e) {
            System.err.println("Errore DB getRistoranti: " + e.getMessage());
            throw e;
        }

        return results;
    }

    @Override
    public ArrayList<Recensione> getRecensioni(String query) throws RemoteException, SQLException {
        ArrayList<Recensione> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty() || query.contains("TODO")) {
            // TODO: implement SQL query execution for recensioni
            return results;
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Recensione recensione = new Recensione(
                        rs.getInt("stelle"),
                        rs.getString("testo"),
                        rs.getString("ristorante"),
                        rs.getString("username")
                );
                recensione.setData(rs.getString("data"));
                recensione.setRisposta(rs.getString("risposta"));
                results.add(recensione);
            }

        } catch (SQLException e) {
            System.err.println("Errore DB getRecensioni: " + e.getMessage());
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
    public ArrayList<String> getPreferiti(String query) throws RemoteException, SQLException {
        ArrayList<String> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty() || query.contains("TODO")) {
            // TODO: implement SQL query execution for preferiti data
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
            System.err.println("Errore DB getPreferiti: " + e.getMessage());
            throw e;
        }

        return results;
    }


    @Override
    public boolean saveRistorante(String string, Ristorante ristorante) throws RemoteException {
        if (string == null || string.trim().isEmpty() || ristorante == null) {
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(string)) {
            ps.setString(1, ristorante.getNome());
            ps.setString(2, ristorante.getIndirizzo());
            ps.setString(3, ristorante.getcitta());
            ps.setString(4, ristorante.getPrezzo());
            ps.setString(5, ristorante.getCucina());
            ps.setString(6, ristorante.getNumeroTelefono());
            
            ps.setString(7, ristorante.getSitoWeb());
            ps.setString(8, ristorante.getPremio());
            ps.setDouble(9, ristorante.getStellaVerde());
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

        // Aggiunta della foreign key 'username' che punta alla tabella 'utenti'
        String insertQuery = "INSERT INTO ristoranti (nome, indirizzo, citta, prezzo, cucina, num_tel, sito, sitoWeb, premi, stellaVerde, servizi, descrizione, proprietario, stato) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {

            ps.setString(1, ristorante.getNome());
            ps.setString(2, ristorante.getIndirizzo());
            ps.setString(3, ristorante.getcitta());
            ps.setString(4, ristorante.getPrezzo());
            ps.setString(5, ristorante.getCucina());
            ps.setString(6, ristorante.getNumeroTelefono());
            ps.setString(7, ristorante.getSitoWeb());
            ps.setString(8, ristorante.getPremio());
            ps.setDouble(9, ristorante.getStellaVerde());
            ps.setString(10, ristorante.getServizi());
            ps.setString(11, ristorante.getDescrizione());
            // Imposta la foreign key verso l'utente proprietario (username)
            ps.setString(12, utente);
            ps.setString(13, ristorante.getStato());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore DB setRistorante: " + e.getMessage());
            return false;
        }
    }
}

