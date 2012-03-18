package classificator.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;


/**
 * Classe singoletto che fornisce servizi di utilità
 * per stampe a video e su log (di errore e di messaggi)
 */
public class PrintUtil{

    private static PrintUtil instance = null;
    private final static String EXECUTIONDIR = new File(PrintUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent().replaceAll("%20", " ");
    private final static String ERRORFILENAME = EXECUTIONDIR + "/error.log";
    private final static String MESSAGEFILENAME = EXECUTIONDIR + "/message.log";
    
    private static PrintWriter errorLog = null;
    private static PrintWriter messageLog = null;

    /**
     * Costruttore di classe, valorizza i PrintWriter
     */
    private PrintUtil(){
        try {
            errorLog = new PrintWriter(new BufferedWriter(new FileWriter(ERRORFILENAME)));
            messageLog = new PrintWriter(new BufferedWriter(new FileWriter(MESSAGEFILENAME)));
        } catch (IOException ex){
            System.out.println("I/O initialization error: log file will not write");
        }
    }
    

    /**
     * Si utilizza per questa singoletto in sostituzione
     * del costruttore, utile per garantire che ci sia
     * solo una ed una sola istanza ma che ci sia
     * @return istanza di PrintUtil
     */
    static PrintUtil getInstance() {
        if (instance == null)
            instance = new PrintUtil();
        return instance;
    }

    
    /**
     * Restituisce il timestamp dell'istante quando invocato
     * @return Stringa del tipo: [HH:MM:SS]
     */
    static String getTimestamp(){
        return "[" + Calendar.getInstance().getTime().toString().substring(11, 19) + "] ";
    }


    /**
     * Procedura di servizio per la stampa a video e a log dei messaggi del server:
     * giustappone il timestamp attuale prima del messaggio da stampare a cui
     * concatena l'id del client
     * @param msg Messaggio da stampare
     */
    static void printMsg(String msg, int clientID){
        System.out.println(getTimestamp() + msg + " [client ID: " + clientID + "]");
        messageLog.println(getTimestamp() + msg + " [client ID: " + clientID + "]");
        messageLog.flush(); // flusha il buffer così che il log sia visualizzabile ad ogni msg
    }


    /**
     * Procedura di servizio per la stampa a video e a log delle eccezioni:
     * giustappone il timestamp attuale prima del messaggio da stampare a cui
     * concatena l'id del client che ha lanciato l'eccezione a video
     * e a log stampa il clientID più lo stacktrace e l'eccezione
     * @param e Eccezione
     */
    static void printExc(Exception e, int clientID){
        System.out.println(getTimestamp() + e.getClass().getName() + " message: "
                           + e.getMessage() + " [client ID: " + clientID + "]");
        System.out.println("More details available at " + ERRORFILENAME + " file");
        errorLog.println(getTimestamp() + "client ID " + clientID + " throwed exception. Stacktrace:");
        e.printStackTrace(errorLog);
        errorLog.flush(); // flusha il buffer così che il log sia visualizzabile ad ogni msg
    }

}
