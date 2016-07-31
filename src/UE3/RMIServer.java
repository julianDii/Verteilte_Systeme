package UE3;


import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Julian Dobrot on 14.06.2016.
 */
public class RMIServer extends UnicastRemoteObject implements RMIServerInterface{

    /**
     * The array list component of the Telefonserver.
     * It holds the results of the different search types.
     */
    public static ArrayList<String[]> resultList = new ArrayList<>();

    static Remote registry = null;


    /**
     * The telefon book component.
     * Holds name and numbers of all entries in an two dimensional string array.
     */
    private static String [][] telefonbook = {

            {"Meier","4711"},
            {"Schmitt","0815"},
            {"Müller","4711"},
            {"von dobrot","0816"},
            {"Müller","7867"}};

    protected RMIServer() throws RemoteException, UnknownHostException {


    }


    /**
     * The main Method to start the program.
     * runs the main thread with the program loop for the search.
     * Application will leave loop with typing 'q'.
     * @param args
     */
    public static void main(String[] args) throws RemoteException, MalformedURLException, UnknownHostException {

        registry = LocateRegistry.createRegistry(1099);     // Port 1099=DEFAULT
        RMIServer rmiServer = new RMIServer();

        // Anmeldung des Dienstes mit
        // rmi://Serverhostname/Eindeutige Bezeichnung des Dienstes
        // ---------------------------------------------------------

        //Rebind, da bei bind() der Port blockiert
        //Rechner namens ‘compute’ und Server namens ‘MyService’



        InetAddress localMachine = InetAddress.getLocalHost();
        String host = localMachine.getHostName();


        //später localhost vom remote
        Naming.rebind("rmi://"+host+"/MyTelephoneBook", rmiServer);
        System.out.println("Server wartet auf RMIs");

    }

    /**
     * This method searches for a name in the book.
     * @param name
     */
    private static void searchName(String name) {

        for (String[] string  : telefonbook){
            if (string[0].trim().equalsIgnoreCase(name.trim())){
                addToResultList(string);
            }
        }

    }

    /**
     * This method searches for a number in the book.
     * @param number
     */
    private static void searchNumber(String number) {

        for (String[] string : telefonbook) {
            if (string[1].trim().equals(number.trim())){
                addToResultList(string);
            }
        }
    }

    /**
     * This method adds the given string array to the result list.
     * The method is synchronized to make the result list thread save.
     * @param strings
     */
    private static synchronized void addToResultList(String[]strings){
        resultList.add(strings);
    }

    /**
     * This method checks if the goven string is a number
     * @param number
     * @return returns true if the given string is a number
     */
    private static boolean isNumber(String number){

        number = number.trim();

        return (number.matches("[0-9]+"));

    }

    /**
     * This method checks if the given string is a name.
     * @param name
     * @return returns true if the given string is a name.
     */
    private static boolean isName(String name){

        name = name.trim();

        return (name.matches("^[a-zA-ZöüäÖÜÄ]+[a-zA-ZöüäÖÜÄ\\s]*"));
    }

    public ArrayList<String[]> searchEntries (final String name, final String number) {

        resultList.clear();

        if (name.length() > 0 && number.length() > 0) {

            if (isName(name) && isNumber(number)){

                Thread nameThread = new Thread() {

                    @Override
                    public void run() {

                        searchName(name);

                    }
                };

                Thread numberThread = new Thread(){

                    @Override
                    public void run() {

                        searchNumber(number);

                    }
                };

                nameThread.start();
                numberThread.start();

                try {
                    nameThread.join();
                    numberThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            } else {

                System.out.println("Habe nicht gesucht. Enter Name oder Nummer");
                return resultList;
            }


        } else {
            if (isName(name)){
                searchName(name);
            }
            else if (isNumber(number)) {
                searchNumber(number);
            } else {

                System.out.println("Habe nicht gesucht. Enter Name oder Nummer");
                return resultList;


            }

        }
        if (resultList.isEmpty()){
            System.out.println("no results for " + name );
        } else {
            for ( String[] string : resultList) {
                System.out.println(Arrays.deepToString(string));
            }
        }

        return resultList;
    }

    @Override
    public void quit() throws RemoteException {

        InetAddress localMachine = null;
        try {
            localMachine = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String host = localMachine.getHostName();


        try {
            Naming.unbind("rmi://"+host+"/MyTelephoneBook");
            UnicastRemoteObject.unexportObject(registry,false);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Thread thread = new Thread(){

            @Override
            public void run() {

                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        };
        thread.start();


    }


}


