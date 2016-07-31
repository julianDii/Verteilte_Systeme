package zeitserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Julian Dobrot on 14.06.2016.
 */
public interface ZeitInterface extends Remote {
    java.util.Date getDate() throws RemoteException;

}
