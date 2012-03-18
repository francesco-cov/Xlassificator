package classificator.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Mappa lo schema di una tabella di un database
 */
public class TableSchema {

    /**
     * Inner-class descrittiva delle colonne dello schema
     */
    public class Column{

        private String name;
        private String type;

        /**
         * Costruttore di classe
         * @param name Nome della nuova colonna
         * @param type Tipo della nuova colonna
         */
        Column(String name, String type){
            this.name = name;
            this.type = type;
        }


        /**
         * Restituisce il nome della colonna
         * @return Nome della colonna
         */
        public String getColumnName(){
            return name;
        }


        /**
         * Restituisce 'true' se la colonna è una colonna di valori numerici
         * @return true se è una colonna di valori numerici;
         *         false altrimenti;
         */
        public boolean isNumber(){
            return type.equals("number");
        }


        /**
         * @return Stringa in forma: "nome:tipo"
         */
        @Override
        public String toString(){
            return name + ":" + type;
        }

    }

    private List<Column> tableSchema = new ArrayList<Column>();

    /**
     * Costruttore di classe
     * crea lo schema della tabella in input
     * @param tableName Tabella del database
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public TableSchema(String tableName) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
        Map<String, String> mapSQL_JAVATypes = new HashMap<String, String>();

        mapSQL_JAVATypes.put("CHAR","string");
        mapSQL_JAVATypes.put("VARCHAR","string");
        mapSQL_JAVATypes.put("LONGVARCHAR","string");
        mapSQL_JAVATypes.put("BIT","string");
        mapSQL_JAVATypes.put("SHORT","number");
        mapSQL_JAVATypes.put("INT","number");
        mapSQL_JAVATypes.put("LONG","number");
        mapSQL_JAVATypes.put("FLOAT","number");
        mapSQL_JAVATypes.put("DOUBLE","number");

        Connection conn = DbAccess.getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getColumns(null, null, tableName, null);

        while(rs.next()) // costituisco il mio schema in memoria (con un array list) "leggendolo" dal db
            if(mapSQL_JAVATypes.containsKey(rs.getString("TYPE_NAME")))
                tableSchema.add( new Column( rs.getString("COLUMN_NAME"), mapSQL_JAVATypes.get(rs.getString("TYPE_NAME"))) ); // passo per l'hashmap "per convertire" il tipo in un JAVAType
        DbAccess.closeConnection(); // chiudo la connessione (e rilascio automaticamente il ResultSet)
    }


    /**
     * Restituisce il numero degli attributi (column) della tabella
     * @return Numero di attributi della tabella
     */
    public int getNumberOfAttributes(){
        return tableSchema.size();
    }


    /**
     * Restituisce una colonna dello schema
     * @param index Indice (posizione) della colonna
     * @return Oggetto Column in posizione index dello schema
     */
    public Column getColumn(int index){
        return tableSchema.get(index);
    }

}