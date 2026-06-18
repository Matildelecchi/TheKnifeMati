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
    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";

    


    public Connection getConnection() throws SQLException {
        // Con JDBC moderni non serve più chiamare esplicitamente Class.forName
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    public ServerMain() throws RemoteException {
    }

    public static void main(String[] args) throws RemoteException {
        System.out.println("Hello from Server!");
        DBService stub = null;

        ServerMain obj = new ServerMain();
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
                        rs.getString("nome"),
                        rs.getString("indirizzo"),
                        rs.getString("localita"),
                        rs.getString("prezzo"),
                        rs.getString("cucina"),
                        rs.getDouble("longitudine"),
                        rs.getDouble("latitudine"),
                        rs.getString("numeroTelefono"),
                        rs.getString("url"),
                        rs.getString("sitoWeb"),
                        rs.getString("premio"),
                        rs.getString("stellaVerde"),
                        rs.getString("servizi"),
                        rs.getString("descrizione")
                );
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
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("username"),
                        rs.getString("passwordHash"),
                        rs.getString("dataNascita"),
                        rs.getString("luogoDomicilio"),
                        rs.getString("ruolo")
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
            ps.setString(3, ristorante.getLocalita());
            ps.setString(4, ristorante.getPrezzo());
            ps.setString(5, ristorante.getCucina());
            ps.setDouble(6, ristorante.getLongitudine());
            ps.setDouble(7, ristorante.getLatitudine());
            ps.setString(8, ristorante.getNumeroTelefono());
            ps.setString(9, ristorante.getUrl());
            ps.setString(10, ristorante.getSitoWeb());
            ps.setString(11, ristorante.getPremio());
            ps.setString(12, ristorante.getStellaVerde());
            ps.setString(13, ristorante.getServizi());
            ps.setString(14, ristorante.getDescrizione());

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
}

