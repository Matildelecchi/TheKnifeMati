package com.example.theknife.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DBService extends Remote {

    String getCurrentDateTime() throws RemoteException;
    ArrayList<Ristorante> getRistoranti(String query) throws RemoteException, SQLException;
    ArrayList<Recensione> getRecensioni(String query) throws RemoteException, SQLException;
    ArrayList<Utente> getUtenti(String query) throws RemoteException, SQLException;
    ArrayList<String> getOwnership(String query) throws RemoteException, SQLException;
    ArrayList<String> getPreferiti(String query) throws RemoteException, SQLException;
    boolean saveRistorante(String string, Ristorante ristorante) throws RemoteException ;
    boolean saveOwnership(String string, String username, String nome) throws RemoteException ;
    Boolean setUtente(Utente utente) throws RemoteException ;
}

