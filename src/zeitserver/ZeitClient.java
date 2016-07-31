package zeitserver;

import java.rmi.Naming;

/**
 * Created by Julian Dobrot on 14.06.2016.
 */
public class ZeitClient {

    public ZeitClient() {
    }

    public static void main(String[] args) throws Exception {
        long t1 = 0, t2 = 0;
        ZeitInterface remoteZeit = (ZeitInterface) Naming.lookup("rmi://localhost/MyService");

        //2x RPCs / 2x RMIs
        t1 = remoteZeit.getDate().getTime();
        t2 = remoteZeit.getDate().getTime();

        System.out.println("RMI brauchte " + (t2 - t1) + " ms");
    }
}