package com.example.theknife.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface TimeService extends Remote {

    String getCurrentDateTime() throws RemoteException;
}

