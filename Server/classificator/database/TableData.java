package classificator.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import classificator.database.TableSchema.Column;
import java.sql.Connection;


/**
 * Classe per gestire i dati contenuti in una tabella
 */
public class TableData{


    /**
     * Inner-class utilizzata per gestire una tupla della tabella
     */
    public class TupleData {

        private List<Object> tuple = new ArrayList<Object>();

        /**
         * Costruttore di classe
         */
        private TupleData(){
            // DO NOTHING
        }

        /**
         * Restituisce la lista di valori della tupla
         * @return Lista di oggetti della tupla
         */
        public List<Object> getTupleValues(){
            return tuple;
        }


        /**
         * @return Stringa di valori della tupla del tipo "val1 val2 val3"
         */
        @Override
        public String toString() {
            String value = new String();
            Iterator<Object> it = tuple.iterator();
            while (it.hasNext())
                value += (it.next().toString() + " ");
            return value;
        }

    }

    /**
     * Costruttore di classe
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public TableData() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
        // DO NOTHING
    }


    /**
     * Interroga la tabella in input e restituisce una lista di tuple
     * @param tableName Tabella da interrogare
     * @return Lista di tuple contenute nella tabella
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     */
    public List<TupleData> getTransactions(String tableName) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException{
        List<TupleData> transSet = new LinkedList<TupleData>();

        Connection conn = DbAccess.getConnection();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM " + tableName);
        
        TableSchema ts = new TableSchema(tableName);
        TupleData tempTuple = null;

        while(rs.next()){
            tempTuple = new TupleData();
            for(int i=1; i <= ts.getNumberOfAttributes(); i++)
                if(ts.getColumn(i-1).isNumber())
                    tempTuple.tuple.add( rs.getFloat(i) );
                else
                    tempTuple.tuple.add( rs.getString(i) );
            transSet.add(tempTuple);
        }
        DbAccess.closeConnection(); // chiudo la connessione (e rilascio automaticamente il ResultSet e la Statement)
        return transSet;
    }


    /**
     * Restituisce i valori dell'attributo column
     * @param tableName Tabella da interrogare
     * @param column Colonna da interrogare
     * @param modality Modalità di query (DISTINCT/NODISTINCT)
     * @return Lista dei valori di un attributo
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public List<Object> getColumnValues(String tableName, Column column, QUERY_TYPE modality) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        List<Object> valueSet = new LinkedList<Object>();

        Connection conn = DbAccess.getConnection();
        Statement stm = conn.createStatement();
        String sql = "SELECT ";

        if(modality.equals(QUERY_TYPE.DISTINCT))
            sql += "DISTINCT ";
        sql += column.getColumnName() + " FROM " + tableName + " ORDER BY " + column.getColumnName();
        ResultSet rs = stm.executeQuery(sql);
        if(column.isNumber())
            while(rs.next())
                valueSet.add(rs.getFloat(column.getColumnName()));
        else
            while(rs.next())
                valueSet.add(rs.getString(column.getColumnName()));
        DbAccess.closeConnection(); // chiudo la connessione (e rilascio automaticamente il ResultSet e la Statement)
        return valueSet;
    }
    
}
