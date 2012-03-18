package classificator.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestisce la connessione al database
 */
public class DbAccess{

    private static String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
    private static final String DBMS = "jdbc:mysql";
    private static final String SERVER = "localhost";
    private static final String DATABASE = "ClassificationData";
    private static final String PORT = "3306";
    private static final String USER_ID = "decisionTreeID";
    private static final String PASSWORD = "decTreePassword";
    private static Connection conn = null;

    // callCounter: conta il numero di volte in cui è richiesta una connessione
    // IF uguale a 1 closeConnection può chiuderla
    // ELSE decrementa solo il contatore, perchè altri metodi si aspettano la connessione aperta
    private static int callCounter = 0;


    /**
     * Registra il driver di MySQL con il DriverManager di JDBC e inizializza la connessione (se non è gia stato fatto)
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void initConnection() throws ClassNotFoundException, SQLException{
        if(conn == null){
            Class.forName(DRIVER_CLASS_NAME); // Calling the Class.forName automatically creates an instance of a driver and registers it with the DriverManager, so i don't need to create an instance of the class.
            conn = DriverManager.getConnection( DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE,USER_ID, PASSWORD );
        }
    }


    /**
     * Inizializza la connessione se non è gia stato fatto e restituisce l'oggetto associato.
     * Incrementa callCounter che tiene traccia di quanti "contesti" si aspettano la connessione ON
     * @return Istanza di Connection relativa alla connessione corrente
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException{
        if(conn == null)
            initConnection();
        callCounter++;
        return conn;
    }

    /**
     * Chiude la connessione, se aperta
     * @throws SQLException
     */
    public static void closeConnection() throws SQLException{
        if(conn != null){
            if(callCounter == 1){ // se chi vuole chiudere la connessione è l'unico contesto ad utilizzarla
                if(!conn.isClosed())
                    conn.close();
                conn = null;
            }
            callCounter--;
        }
    }

}
