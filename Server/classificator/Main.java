package classificator;

import classificator.server.MultiServer;
import java.io.IOException;

/**
 * Classe principale
 */
public class Main{
    
    private static int port = 8080;

    /**
     * Avvia la sequenza di start mettendo in ascolto il multiserver generando un thread
     * @param args argomenti da linea di comando: numero di porta su cui mettere in ascolto il multiserver
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        if(args.length == 1)
            port = new Integer(args[0]);
        try{
            new MultiServer(port).bootUp();
        }
        catch(IOException e) {
            System.exit(-1);
        }
    }

}