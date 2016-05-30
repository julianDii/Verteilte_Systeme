package UE2;
// Datei: Request.java
// Autor: Brecht
// Datum: 24.05.14
// Thema: Stream-Socket-Verbindungen zwischen Browser und Web-
//        Server. GET-Request herausfiltern, falls POST-Requests
//        nicht benutzt werden.
// -------------------------------------------------------------


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

class Request {


    private static String remote = null;
    private static int port = 0;


    public static void main(String[] args) throws Exception {

        java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
        remote = localMachine.getHostName();

        try {
            port = args.length > 1 ? Integer.parseInt(args[1]):9876;
        }catch (Exception e ){
            System.out.println("setze standart port ein");
            port = 9876;
        }


        // Vereinbarungen
        // ---------------------------------------------------------
        ServerSocket ss       = null;  // Fuer das accept()
        Socket cs             = null;  // Fuer die Requests
        InputStream is        = null;  // Aus dem Socket lesen
        InputStreamReader isr = null;
        BufferedReader br     = null;
        OutputStream os       = null;  // In den Socket schreiben
        PrintWriter pw        = null;
        String zeile          = null;  // Eine Zeile aus dem Socket
        String host           = null;  // Der Hostname


        // Programmstart und Portbelegung
        // ---------------------------------------------------------
        host = InetAddress.getLocalHost().getHostName();

        System.out.println("Server startet auf "+host+" an "+port);

        // ServerSocket einrichten und in einer Schleife auf
        // Requests warten.
        // ---------------------------------------------------------
        ss = new ServerSocket(port);
        while(true) {
            System.out.println("Warte im accept()");
            cs = ss.accept();               // <== Auf Requests warten

            // Den Request lesen (Hier nur erste Zeile)
            // -------------------------------------------------------
            is    = cs.getInputStream();
            isr   = new InputStreamReader(is);
            br    = new BufferedReader(isr);
            zeile = br.readLine();
            System.out.println("Kontrollausgabe: "+zeile);

            // Favicon-Requests nicht bearbeiten
            // -------------------------------------------------------
            if(zeile.startsWith("GET /favicon")) {
                System.out.println("Favicon-Request");
                br.close();
                continue;                       // Zum naechsten Request
            }

            HashMap<String ,String> hm = erstelleTeilString(zeile);
            System.out.println(hm);




            // Den Request bearbeiten (Hier: nur zuruecksenden)
            // -------------------------------------------------------
            System.out.println("Request wird bearbeitet");
            os  = cs.getOutputStream();
            pw  = new PrintWriter(os);

            pw.println("HTTP/1.1 200 OK");               // Der Header
            pw.println("Content-Type: text/html");
            pw.println();
            pw.println("<html><meta charset=\"utf-8\"/>");                    // Die HTML-Seite
            pw.println("<body>");
            pw.println("<h2 align=center>Telefonverzeichnis</h2>\n" +
                    "<h3>Sie können nach Name oder nach Telefonnummer oder nach beiden (nebenläufig) suchen.</h3>\n" +
                    "<form method=get action=\"http://"+remote+":"+port+"\">\n" +
                    "<table>\n" +
                    "<tr> <td valign=top>Name:</td>    <td><input name=A></td>    <td></td> </tr>\n" +
                    "<tr> <td valign=top>Nummer:</td> <td><input name=B></td>    <td></td> </tr>\n" +
                    "<tr> <td valign=top><input type=submit name=C value=Suchen></td>\n" +
                    "        <td><input type=reset></td>\n" +
                    "        <td><input type=submit name=D value=\"Server beenden\" ></td> </tr>\n" +
                    "</table>\n" +
                    "</form>");


            if (hm.get("D") != null) {
                System.out.println(pw.printf("<h4>Der Server wurde beendet</h4>"));
            }


            else if (hm.size() > 0){

                ArrayList<String[]> results = new ArrayList<>();

                TelephoneServer ts = new TelephoneServer();
                results =  ts.searchEntries(hm.get("A"), hm.get("B"));



                {
                    if (results.size() > 0) {

                        pw.println("<h3>Ihre Suche lieffert folgende Ergebnisse</h3>");
                        pw.println("<ul>");

                        for (String[] string : results) {

                            pw.println("<li>" + string[0] + " : " + string[1] + "</li>");
                        }
                        pw.println("</ul>");

                    } else {

                        if (hm.get("A").matches("\\p{javaWhitespace}*")&(hm.get("A").length()!=0)){
                            System.out.println("A includes whitespace");
                            pw.print("<h4>(A) not only blankds allowed</h4>");
                        }
                        else if (hm.get("B").matches("\\p{javaWhitespace}*")&(hm.get("B").length()!=0)){
                            System.out.println("B includes whitespace");
                            pw.print("<h4>(B) not only blankds allowed</h4>");
                        }

                        else if (!hm.get("A").isEmpty() && (!hm.get("B").isEmpty())) {
                            pw.println("<h4>Ihre Suche nach:  " + " ' " + hm.get("A") + " ' " + " und " + " ' " + hm.get("B") + " ' " + " " + "lieffert keine Ergebnisse" + "</h4>");
                        } else if (!hm.get("A").isEmpty()) {
                            pw.println("<h4>Ihre Suche nach:  " + " ' " + hm.get("A") + " ' " + "lieffert keine Ergebnisse" + "</h4>");
                        } else if (!hm.get("B").isEmpty()) {
                            pw.println("<h4>Ihre Suche nach:  " + " ' " + hm.get("B") + " ' " + "lieffert keine Ergebnisse" + "</h4>");
                        } else {
                            pw.print("<h4>Sie müssen mindestens einen Namen oder eine Nummer eingeben</h4>");
                        }


                    }
                }

            }

            pw.println("</body>");
            pw.println("</html>");
            pw.println();
            pw.flush();
            pw.close();
            br.close();

            if (hm.get("D") != null) {
                System.out.println("Der Server wurde beendet");
                System.exit(0);
            }
        }  // end while
    }  // end main()

    private static HashMap<String,String> erstelleTeilString(String zeile) {

        HashMap<String,String> hm = new HashMap<>();
        String[] parts = zeile.split("&|\\?|\\s");

        for (String srr : parts) {
            if(srr.contains("=")){
                String[] ergebnis = srr.split("=");


                if (ergebnis.length > 1) {
                    try {
                        hm.put(ergebnis[0],URLDecoder.decode(ergebnis[1],"UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else{
                    hm.put(ergebnis[0], "");
                }
            }
        }
        return hm;

    }
}  // end class