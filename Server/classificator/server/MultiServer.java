package classificator.server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe che gestisce la logica multiclient concorrenziale del server
 * @see ServeOneClient
 */
public class MultiServer extends Thread{

    private ServerSocket srvSocket = null;
    private Socket socket = null;
    
    /**
     * Esegue il bind del servizio con la porta in input
     * @param port Porta su cui il MultiServer si metterà in ascolto
     * @throws IOException
     */
    public MultiServer(int port) throws IOException{
        try{
            srvSocket = new ServerSocket(port);
        }
        catch (IOException e){
            if(e instanceof BindException)
                System.out.println("Failed to listen on port " + port + ": resource is busy");
            else
                System.out.println("Server initialization failed");
            throw e;
        }
        System.out.println("\n" + PrintUtil.getTimestamp() + " Server started: port " + port + "\nCTRL-C to shut down the service\n");
    }


    /**
     * Avvia il thread per accettare i client
     */
    public void bootUp(){
        start();
    }
    
    
    /**
     * Mette in ascolto (in attesa di client) il MultiServer nel thread avviato da bootUp
     */
    @Override
    public void run(){
        // Definisco un nuovo hook da eseguire nella sequenza di shutdown per questo contesto
        Runtime.getRuntime().addShutdownHook( new ShutdownMultiServer() );

        while(true){
            try{
                socket = srvSocket.accept();
                new ServeOneClient(socket).start();
            }
            catch(IOException e){
                System.out.println("Connection handshacking failed");
                continue; // salta la chiamata di ServeOneClient mettendosi in ascolto per il prossimo client
            }
        }
    }

}

/**
 * Classe che fornisce l'hook da eseguire in un thread separato allo shutdown del MultiServer
 */
class ShutdownMultiServer extends Thread{
    
    Socket socket = null;
    ServerSocket srvSocket = null;


    /**
     * Operazioni di chiusura
     */
    @Override
    public void run(){
        if(socket != null || srvSocket != null){
            try{
                if(!socket.isClosed())
                    socket.close();
                if(!srvSocket.isClosed())
                    srvSocket.close();
            }
            catch(IOException e){
                // DO NOTHING
            }
        }
        System.out.println(PrintUtil.getTimestamp() + "Server shutted down.");
    }

}
