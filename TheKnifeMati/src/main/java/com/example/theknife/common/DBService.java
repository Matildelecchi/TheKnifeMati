package com.example.theknife.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface DBService extends Remote {

    String getCurrentDateTime() throws RemoteException;
    ArrayList<Object> getData(String query) throws RemoteException;
}

