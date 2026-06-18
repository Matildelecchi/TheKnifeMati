package com.example.theknife.client;

import com.example.theknife.common.DBService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class RMIService {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1234;
    private static final String SERVICE_NAME = "TimeService";

    private static volatile DBService stub;

    private RMIService() {
        // utility class
    }

    public static synchronized DBService getService() throws Exception {
        if (stub == null) {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            stub = (DBService) registry.lookup(SERVICE_NAME);
        }
        return stub;
    }
}
