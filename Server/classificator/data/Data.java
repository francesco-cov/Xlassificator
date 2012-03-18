package classificator.data;

import classificator.database.QUERY_TYPE;
import classificator.database.TableData;
import classificator.database.TableData.TupleData;
import classificator.database.TableSchema;
import classificator.exception.ClassTypeException;
import classificator.exception.DataException;
import classificator.exception.EmptyTrainingSetException;
import classificator.exception.UnknownAttributeIndexException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Modella l'insieme di esempi di training
 */
public class Data{
	
    private Record data[];
    private int numberOfExamples;
    private List<Attribute> explanatorySet = new LinkedList<Attribute>();
    private DiscreteAttribute classAttribute = null;

    
    /**
     * Costruttore di classe
     * @param tableName Nome della tabella del database ClassificationData su MySQL dalla quale riempire il trainingSet
     * @throws DataException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws ClassTypeException
     * @throws EmptyTrainingSetException
     */
    public Data(String tableName) throws DataException, InstantiationException, ClassNotFoundException, IllegalAccessException, ClassTypeException, EmptyTrainingSetException{
        TableSchema ts = null;
        TableData td = null;
        List<TupleData> transactions = null;
        try{
            td = new TableData();
            ts = new TableSchema(tableName);
            transactions = td.getTransactions(tableName);

            numberOfExamples = transactions.size();
            if(numberOfExamples == 0)
                throw new EmptyTrainingSetException();
            data = new Record[numberOfExamples];

            Iterator<TupleData> it = transactions.iterator();
            for(int i=0; i<numberOfExamples; i++)
                data[i] = new Record( it.next().getTupleValues().toArray() );

            for(int i=0; i<ts.getNumberOfAttributes()-1; i++)
                if(ts.getColumn(i).isNumber())
                    explanatorySet.add( new ContinuousAttribute(ts.getColumn(i).getColumnName(), i) );
                else
                    explanatorySet.add( new DiscreteAttribute(ts.getColumn(i).getColumnName(), // name
                                                              i,  // index
                                                              td.getColumnValues(tableName, ts.getColumn(i), QUERY_TYPE.DISTINCT).toArray(new String[0])) ); // distinct values
            
            if(!ts.getColumn(ts.getNumberOfAttributes()-1).isNumber())
                classAttribute = new DiscreteAttribute( ts.getColumn(ts.getNumberOfAttributes()-1).getColumnName(), // name
                                                        ts.getNumberOfAttributes()-1, // index
                                                        td.getColumnValues(tableName, ts.getColumn(ts.getNumberOfAttributes()-1), QUERY_TYPE.DISTINCT).toArray(new String[0]) ); // distinct values
            else
                throw new ClassTypeException(); // attributo di classe continuo
        }
        catch(SQLException sqlEx){
            throw new DataException(sqlEx.getMessage());
        }
    }

    
    /**
     * Restiuisce il valore del membro numberOfExamples
     * @return Cardinalità dell'insieme di esempi
     */
    public int getNumberOfExamples(){
        return numberOfExamples;
    }


    /**
     * Restituisce la lunghezza dell'array explanatorySet[]
     * @return Cardinalità dell'insieme di attributi indipendenti
     */
    public int getNumberOfExplanatoryAttributes(){
        return explanatorySet.size();
    }


    /**
     * Restituisce il valore dell'attributo di classe per l'esempio exampleIndex
     * @param exampleIndex  Indice di riga per l'array di record data[] per uno specifico esempio
     * @return Valore dell'attributo di classe per l'esempio exampleIndex
     */
    public String getClassValue(int exampleIndex){
        return (String)data[exampleIndex].getValue(getNumberOfExplanatoryAttributes());
    }

    
    /**
     * Restituisce il valore dell'attributo indicizzato da attributeIndex per l'esempio exampleIndex
     * @param exampleIndex Indice di riga per l'array di record data[] per uno specifico esempio
     * @param attributeIndex Indice di colonna per lo specifico elemento in riga exampleIndex
     * @return Object associato all'attributo indipendente per l'esempio indicizzato in input
     */
    public Object getExplanatoryValue(int exampleIndex, int attributeIndex){
        return data[exampleIndex].getValue(attributeIndex);
    }

    
    /**
     * Restituisce l'attributo indicizzato da index in explanatorySet[]
     * @param index indice per uno spefico esempio
     * @return Oggetto attributo indicizzato da index
     * @throws UnknownAttributeIndexException
     */
    public Attribute getExplanatoryAttribute(int index) throws UnknownAttributeIndexException{
        Attribute attr  = null;
        ListIterator it = explanatorySet.listIterator();
        while(it.hasNext()){
            attr = (Attribute)it.next();
            if( attr.getIndex() == index )
                return attr;
        }
        throw new UnknownAttributeIndexException(); // se non ha trovato l'attributo
    }


    /**
     * Restituisce una lista contenente gli attributi del trainingset
     * @return Lista di attributi discreti e continui
     */
    public List<Attribute> getExplanatorySet(){
        return explanatorySet;
    }

    
    /**
     * Valorizza l'explanatorySet con la lista di Attribute in input
     * @param sourceSet Lista di Attribute sorgente
     */
    public void setExplanatorySet(List<Attribute> sourceSet){
        Collections.copy(explanatorySet, sourceSet);
    }

    
    /**
     * Restituisce l'attributo di classe
     * @return Attributo di classe discreto
     */
    public DiscreteAttribute getClassAttribute(){
        return classAttribute;
    }


    /**
     * Ordina il trainingSet tramite il metodo di classe Arrays.sort per attributo
     * @param attribute Criterio d'ordinamento (lessicografico)
     * @param beginExampleIndex Indice di inizio porzione trainingSet da ordinare
     * @param endExampleIndex Indice di fine porzione trainingSet da ordinare
     */
    public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex){
        // Chiamo il metodo statico Arrays.sort passando:
        // data : array di record da ordinare
        // beginExampleIndex : indice di inizio
        // endExampleIndex+1 : indice di fine
        // new TupleComparator(attribute) : oggetto di tipo TupleComparator costruito con 'attribute' necessario per "ORDER BY attribute"
        Arrays.sort(data, beginExampleIndex, endExampleIndex+1, new RecordComparator(attribute));
    }


    /**
     * @return Rappresentazione testuale del trainingSet
     */
    @Override
    public String toString(){
        String text = new String();
        
        for(int i=0; i<getNumberOfExamples(); i++){
            for(int j=0; j<=getNumberOfExplanatoryAttributes(); j++)
                text = text + data[i].getValue(j).toString() + " ";
           text = text + "\n";
        }
        return text;
    }

}
