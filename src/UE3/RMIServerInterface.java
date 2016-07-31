package UE3;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by Julian Dobrot on 14.06.2016.
 */
public interface RMIServerInterface extends Remote {

    ArrayList<String[]> searchEntries (final String name, final String number)throws Exception;
    void quit() throws RemoteException;



}
