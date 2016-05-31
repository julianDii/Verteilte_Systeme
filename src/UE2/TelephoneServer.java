package UE2;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * This Class represents a simple Telephone server.
 *
 * UE_2 Verteilte Systeme SS2016
 * Created by julian on 03.05.16.
 */
public class TelephoneServer {


    /**
     * The array list component of the Telefonserver.
     * It holds the results of the different search types.
     */
    public static ArrayList<String[]> resultList = new ArrayList<>();

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


    /**
     * The main Method to start the program.
     * runs the main thread with the program loop for the search.
     * Application will leave loop with typing 'q'.
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("******************************T-BOOK***********************************\n");

        Scanner scanner = new Scanner(System.in);


        while (true){

            // clear the list of results to have a empty list before each new search.
            resultList.clear();

            System.out.println("Search for a name or a number, or a name and number! press 'q' for exit the Application \n"

                    + "Pls enter first the Name then the Number \n"
                    + "when searching for a name and a number separate name and number with a ':'");

            String input  = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")){
                System.out.println("Exit Application");
                break;
            }



            final String[] inputs = input.split(":");

            if (inputs.length > 1) {



                if (isName(inputs[0]) && isNumber(inputs [1])){

                    Thread nameThread = new Thread() {

                        @Override
                        public void run() {

                            searchName(inputs[0]);

                        }
                    };

                    Thread numberThread = new Thread(){

                        @Override
                        public void run() {

                            searchNumber(inputs[1]);

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
                    System.out.println("The input was no name and no number");
                    System.out.println("blankNumber");
                    continue;
                }


            } else {
                if (isName(inputs[0])){
                    searchName(inputs[0]);
                }
                else if (isNumber(inputs[0])) {
                    searchNumber(inputs[0]);
                } else {
                    System.out.println("The input was no name and no number");
                    System.out.println("blankName");
                    continue;
                }

            }
            if (resultList.isEmpty()){
                System.out.println("no results for " + inputs[0] );
            } else {
                for ( String[] string : resultList) {
                    System.out.println(Arrays.deepToString(string));
                }
            }
        }
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


}
