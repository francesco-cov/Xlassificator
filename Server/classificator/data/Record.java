package classificator.data;

import java.util.Comparator;

/**
 * Classe che sostituisce la matrice di cui ogni istanza rappresenta una riga del trainingSet
 */
public class Record{
    
    Object values[] = null; // array di valori per quel record


    /**
     * Costruttore di classe
     * @param values Array di valori per valorizzare la tupla
     */
    Record(Object[] values){
        this.values = new Object[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length); // deep copy
    }


    /**
     * Restituisce il valore della tupla nella colonna idAttribute
     * @param idAttribute Identificativo di colonna
     * @return Object del valore
     */
    Object getValue(int idAttribute){
        return values[idAttribute];
    }


    /**
     * Valorizza a 'value' quell' attributo
     * (di tipo primitivo o di classe readonly quindi sufficiente '=' per l'assegnazione)
     * @param idAttribute Identificativo attributo
     * @param value Valore
     */
    void setValue(int idAttribute, Object value){ 
        values[idAttribute] = value;
    }

}


/**
 * Implementazione dell'interfaccia Comparator in modo tale da permettere l'utilizzo di Arrays.sort su Record
 */
 class RecordComparator implements Comparator<Record>{

        private int index;
        private Attribute attribute;

        /**
         * Costruttore di classe
         * @param attribute Attributo su quale effettuare la comparazione
         */
        public RecordComparator(Attribute attribute) {
            index = attribute.getIndex();
            this.attribute = attribute;
        }


        /**
         *
         * @param ob1 Record da confrontare
         * @param ob2 Record da confrontare
         * @return 0 se i nomi dei record sono lessicograficamente uguali;
         *         -1 se ob1 è lessicograficamente inferiore a ob2;
         *         1 se ob1 è lessicograficamente superiore a ob2;
         */
        @Override
        public int compare(Record ob1, Record ob2) {
            if(attribute instanceof DiscreteAttribute)
                return ( (String)ob1.getValue(index) ).compareTo( (String)ob2.getValue(index) );
            else
                return ( (Float)ob1.getValue(index) ).compareTo( (Float)ob2.getValue(index) );
        }

}