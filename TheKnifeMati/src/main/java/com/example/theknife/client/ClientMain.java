package com.example.theknife.client;

import com.example.theknife.common.DBService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain
{
    static int PORT = 1234;

    public static void main(String[] args) {

        try {
            // Getting the registry
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", PORT);

            // Looking up the registry for the remote object
            DBService stub = (DBService) registry.lookup("TimeService");

            // Calling the remote method using the obtained object
            System.out.println(stub.getCurrentDateTime());
            App.main(args);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }
}
