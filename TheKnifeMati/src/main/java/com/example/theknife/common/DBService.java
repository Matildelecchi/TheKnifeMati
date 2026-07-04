package com.example.theknife.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface DBService extends Remote {

    String getCurrentDateTime() throws RemoteException;
    ArrayList<Utente> getUtenti(String query) throws RemoteException, SQLException;
    ArrayList<String> getOwnership(String query) throws RemoteException, SQLException;
    boolean saveRistorante(String string, Ristorante ristorante) throws RemoteException, SQLException;
    boolean saveOwnership(String string, String username, String nome) throws RemoteException ;
    Boolean setUtente(Utente utente) throws RemoteException ;
    Boolean setRistorante(Ristorante ristorante,String utente) throws RemoteException ;
    Ristorante getRistorante(String num_tel) throws RemoteException, SQLException;
    ArrayList<Ristorante> getRistoranti(String citta, String stato) throws RemoteException, SQLException;
    ArrayList<Recensione> getRecensioni(String num_tel) throws RemoteException, SQLException;
    ArrayList<Recensione> getRecensioniByStars(String num_tel, List<Integer> stars) throws RemoteException, SQLException;
    ArrayList<Recensione> getRecensioniByUsername(String username) throws RemoteException, SQLException;
    ArrayList<Ristorante> getPreferiti(String username) throws RemoteException, SQLException;
    String getRisposta(int id_rec) throws RemoteException, SQLException;
    boolean saveRisposta(int id_rec, String testo) throws RemoteException, SQLException;
    boolean removeRisposta(int id_rec) throws RemoteException, SQLException;
    boolean modifyRisposta(int id_rec, String testo) throws RemoteException, SQLException;
    boolean saveRecensione(String titolo, String testo, double stelle, String num_tel, String username) throws RemoteException, SQLException;
    boolean modifyRecensione(int id_rec, String titolo, String testo, double stelle) throws RemoteException, SQLException;
    boolean removeRecensione(int id_rec) throws RemoteException, SQLException;
    double getStelleByTel(String num_tel) throws RemoteException, SQLException;
    ArrayList<Ristorante> getRistorantiByUsername(String username) throws RemoteException, SQLException;
    boolean isPreferito(String username, String num_tel) throws RemoteException, SQLException;
    boolean removePreferito(String username, String num_tel) throws RemoteException, SQLException;
    boolean savePreferito(String username, String num_tel) throws RemoteException, SQLException;
}

