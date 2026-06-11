package com.example.theknife.server;

import com.example.theknife.common.TimeService;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;

public class ServerMain implements TimeService {
    static int PORT = 1234;


    public ServerMain(){
    }

    public static void main(String[] args) {
        System.out.println("Hello from Server!");
        TimeService stub = null;

        ServerMain obj = new ServerMain();
        try {
            stub = (TimeService) UnicastRemoteObject.exportObject(
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
}
