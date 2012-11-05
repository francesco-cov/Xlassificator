package classificator.server;

import classificator.exception.*;
import java.net.Socket;
import classificator.data.Data;
import classificator.database.DbAccess;
import classificator.tree.DecisionTree;
import classificator.tree.LeafNode;
import classificator.tree.SplitNode;
import java.sql.Statement;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe che si occupa del control flow e della comunicazione di un singolo client
 */
class ServeOneClient extends Thread{

    private Socket socket = null;
    private ObjectInputStream in = null; // stream con richieste del client
    private ObjectOutputStream out = null;
    private DecisionTree tree = null;
    private static int clientNumber = 0;
    int clientID = clientNumber++;
    PrintUtil printer_logger = PrintUtil.getInstance();

    /**
     * Costruttore di classe
     * @param socket Socket instaurato tra multiserver e client corrente
     * @throws IOException
     */
    public ServeOneClient(Socket socket) throws IOException{
        this.socket = socket;
        try{
            out = new ObjectOutputStream( socket.getOutputStream() );
            in = new ObjectInputStream( socket.getInputStream() );
        }
        catch(IOException e){
            socket.close();
            PrintUtil.printMsg("Failed to initialize I/O streams", clientID);
            throw e;
        }
    }


    /**
     * control flow del thread per il client corrente
     */
    @Override
    public void run(){
        boolean cycle = true;
        PrintUtil.printMsg("Client connected", clientID);
        while(cycle){
            try{
                int command = ((Integer)in.readObject()).intValue();
                switch(command){
                    case 0: // FILL NAMETABLES Client's ComboBox
                        List<String> list = new LinkedList<String>();
                        try{
                            Statement stm = DbAccess.getConnection().createStatement();
                            ResultSet rs = stm.executeQuery("SHOW TABLES");
                            while(rs.next())
                                list.add(rs.getString(1));
                            out.writeObject(list);
                        }
                        catch(SQLException e){ // Problemi nel retrieving della lista di tabelle
                            try {
                                out.writeObject(e);
                            } catch (IOException ex){
                                // DO NOTHING
                            }
                            PrintUtil.printMsg("no fatal SQLException: continue..", clientID);
                        }
                        break;
                    case 1: // LEARNING A DECISION TREE
                        String tableName = (String)in.readObject();
                        Data trainingSet = null;
                        trainingSet = new Data(tableName); // dal db
                        tree = new DecisionTree(trainingSet);
                        PrintUtil.printMsg("Tree learned", clientID);
                        out.writeObject("OK");
                        break;
                    case 2: // SERIALIZE THE TREE IN RAW BYTE FORMAT AND THEN TRANSFER TO CLIENT
                        byte[] treeByte = tree.serializeToByteArray();
                        out.writeObject( new Integer(treeByte.length) );
                        out.write(treeByte);
                        out.flush();
                        PrintUtil.printMsg("Exporting tree file to", clientID);
                        break;
                    case 3: // RECEIVE THE DECISION TREE FROM CLIENT AND DESERIALIZE TO DECISIONTREE
                        byte buf[] = new byte[((Integer)in.readObject()).intValue()]; //alloca un array di byte della dimensione dell'array che si dovrà ricevere dal server
                        in.readFully(buf);
                        tree = DecisionTree.deserializeFromByteArray( buf );
                        out.writeObject("OK");
                        PrintUtil.printMsg("Importing tree file from", clientID);
                        break;
                    case 4: // USE THE CURRENT TREE TO PREDICT AN EXAMPLE
                        String classValue = predictClass(tree);
                        out.writeObject("Class value transmitted ...");
                        out.writeObject("Class value: " + classValue + "\n");
                        break;
                    case 5:
                        out.writeObject(tree.getRules());
                        PrintUtil.printMsg("Training set rules sent", clientID);
                        break;
                    case 6: // MATRIX GRAPH
                        out.writeObject(tree.getMatrixGraph());
                        PrintUtil.printMsg("Decision tree's matrix graph sent", clientID);
                        break;
                    case 7: // LOGOFF FROM SERVER
                        cycle = false;
                        PrintUtil.printMsg("Disconnected", clientID);
                        break;
                    default:
                        PrintUtil.printMsg("Unknown command", clientID);
                        PrintUtil.printMsg("Disconnected", clientID);
                        cycle = false;
                }
            }
            catch(Exception e){
                cycle = false;
                try {
                    out.writeObject(e);
                } catch (IOException ex){
                    // DO NOTHING
                }
                PrintUtil.printExc(e, clientID);
                PrintUtil.printMsg("Disconnected", clientID);
            }
        } // END SWITCH
        try{
            in.close();
            out.close();
            socket.close();
        }
        catch (IOException e){
            PrintUtil.printMsg("Socket is not closed!", clientID);
        }
    }


    /**
     * Invia al client array di stringhe contenenti le possibili query per predire il valore di classe
     * quando si arriva alla foglia restituisce il valore di classe
     * @param tree Albero sul quale avverrà la predizione del valore di classe
     * @return Stringa contenente il valore di classe (valore nella foglia) in quel ramo
     * @throws UnknownValueException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private String predictClass(DecisionTree tree) throws UnknownValueException, ClassNotFoundException, IOException{
        if(tree.getRoot() instanceof LeafNode)
            return ( (LeafNode)tree.getRoot() ).getPredictedClassValue();
        else
        {
            int choice;
            out.writeObject( ((SplitNode)tree.getRoot()).formulateArrayQuery() );
            choice = ((Integer)in.readObject()).intValue();
            if( choice < 0 || choice >= tree.getRoot().getNumberOfChildren() )
                throw new UnknownValueException();
            else
                return predictClass( tree.subTree(choice) );
        }
    }

}