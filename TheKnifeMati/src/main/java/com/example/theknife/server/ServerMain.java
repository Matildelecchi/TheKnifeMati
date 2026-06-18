package com.example.theknife.server;

import com.example.theknife.common.DBService;

import java.rmi.AlreadyBoundException;
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

public class ServerMain implements DBService {
    static int PORT = 1234;
    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";

    


    public Connection getConnection() throws SQLException {
        // Con JDBC moderni non serve più chiamare esplicitamente Class.forName
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    public ServerMain(){
    }

    public static void main(String[] args) {
        System.out.println("Hello from Server!");
        DBService stub = null;

        ServerMain obj = new ServerMain();
        try {
            stub = (DBService) UnicastRemoteObject.exportObject(
                    obj, PORT);
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
            registry.bind("TimeService", stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
        System.err.println("Server ready");
    }


    @Override
    public String getCurrentDateTime() throws RemoteException {
        return ""+ LocalDateTime.now();
    }
    @Override
    public ArrayList<Object> getData(String query) throws RemoteException{
        String sql = "SELECT id, owner, something FROM ownership";
        ArrayList<Object> results = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String owner = rs.getString("owner");
                String something = rs.getString("something");
                ArrayList<Object> row = new ArrayList<>();
                row.add(id);
                row.add(owner);
                row.add(something);
                results.add(row);
            }

        } catch (SQLException e) {
            System.err.println("Errore DB: " + e.getMessage());
        }

        return results;
    }
}
