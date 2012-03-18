package classificator.backend;

import classificator.TreeApplet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Gestisce la connessione con il server
 */
public class ServerConnection{

    private static TreeApplet base = null;
    private static Socket socket = null;


    /**
     * Costruttore di classe
     * @param parent oggetto TreeApplet per i riferimenti alle componenti di tutte le GUI
     */
    public ServerConnection(TreeApplet parent){
        base = parent;
    }


    /**
     * Connette il client al server, non è gia stato effettuato
     * @param host Server's Host
     * @param port Server's Port
     * @throws Exception
     */
    public void connect(String host, int port) throws Exception{
        if(!isConnected()){
            InetAddress addr = InetAddress.getByName(host);
            socket = new Socket(addr, port);
            base.out = new ObjectOutputStream( socket.getOutputStream() );
            base.in = new ObjectInputStream( socket.getInputStream() ); // stream con richieste del client
        }
    }


    /**
     * Disconnette il client dal server, se connesso
     * @throws IOException
     */
    public void disconnect() throws IOException{
        if(isConnected()){
            base.out = null;
            base.in = null;
            socket.close();
        }
    }


    /**
     * @return true se connesso;
     *         false altrimenti.
     */
    public boolean isConnected(){
        return socket != null;
    }

}
