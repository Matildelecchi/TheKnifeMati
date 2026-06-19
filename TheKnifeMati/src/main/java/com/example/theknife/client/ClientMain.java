package com.example.theknife.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.example.theknife.common.DBService;

public class ClientMain
{
    private static ClientMain client = null;
    private static DBService stub = null;
    static int PORT = 1234;

    public ClientMain() {

        try {
            // Getting the registry
            Registry registry = LocateRegistry.getRegistry(PORT);

            // Looking up the registry for the remote object
            stub = (DBService) registry.lookup("TimeService");

            // Calling the remote method using the obtained object
            System.out.println(stub.getCurrentDateTime());
            //System.out.println("\n"+stub.getCurrentDateTime()+"\n");
            //App.main(args);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }

    /*public static synchronized ClientMain getIstanza() throws RemoteException, NotBoundException {
        if (client == null) {
            client = new ClientMain();
        }
        return client;
    }*/

    public static synchronized DBService getServer() throws RemoteException, NotBoundException {
        return stub;
    }
}
