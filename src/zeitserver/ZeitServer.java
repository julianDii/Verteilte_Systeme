package zeitserver;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

/**
 * Created by Julian Dobrot on 14.06.2016.
 */
public class ZeitServer extends UnicastRemoteObject
        implements ZeitInterface {

    public ZeitServer() throws RemoteException {}

    public Date getDate() throws RemoteException {
        return new Date();
    }

    public static void main(String[] args) throws Exception {

        LocateRegistry.createRegistry(1099);     // Port 1099=DEFAULT
        ZeitServer zeit = new ZeitServer();

        // Anmeldung des Dienstes mit
        // rmi://Serverhostname/Eindeutige Bezeichnung des Dienstes
        // ---------------------------------------------------------

        //Rebind, da bei bind() der Port blockiert
        //Rechner namens ‘compute’ und Server namens ‘MyService’

        Naming.rebind("rmi://localhost/MyService", zeit);
        System.out.println("Server wartet auf RMIs");
        //An dieser Stelle entsteht Enlosschleife, weil Server auf RMIs wartet
    }
}
