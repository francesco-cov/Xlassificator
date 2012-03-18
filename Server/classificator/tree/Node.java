package classificator.tree;

import classificator.data.Data;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


/**
 * La classe modella un generico nodo (fogliare o intermedio) dell'albero di decisione.
 */
public abstract class Node implements Serializable{

    static int idNodeCount = 0;
    int idNode;
    int beginExampleIndex;
    int endExampleIndex;
    // Integer perchè non si possono inserire tipi primitivi quindi wrappiamo int
    Map<String, Integer> classValueAbsoluteFrequency = new HashMap<String, Integer>();

    float entropy;

    /**
     * Costruttore di classe. Avvalora gli attributi di classe e calcola l'entropia rispetto
     * all'attributo di classe nel sotto-insieme di training ricoperto dal nodo corrente
     * @param trainingSet Oggetto di classe Data contenente il training set complessivo
     * @param beginExampleIndex Indice di inizio del sotto-insieme di training
     * @param endExampleIndex Indice di fine del sotto-insieme di training
     */
    Node(Data trainingSet, int beginExampleIndex, int endExampleIndex){
        // assegna un id unico identificativo del nodo
        idNode = idNodeCount++;

        // mantiene gli indici di inizio-fine per la partizione dati
        // compresa tra beginExampleIndex e endExampleIndex
        this.beginExampleIndex = beginExampleIndex;
        this.endExampleIndex = endExampleIndex;

        // aggiorna classValueAbsoluteFrequency in maniera che per ciascun valore distinto di classe sia
        // mantenuta la frequenza della classe nella partizione dati compresa tra beginExampleIndex e endExampleIndex
        for(int i=0; i<trainingSet.getClassAttribute().getNumberOfDistinctValues(); i++)
            classValueAbsoluteFrequency.put(trainingSet.getClassAttribute().getValue(i).toLowerCase(),0);

        for(int i=beginExampleIndex; i<=endExampleIndex; i++){
            String classValue = trainingSet.getClassValue(i).toLowerCase();
            classValueAbsoluteFrequency.put(classValue.toLowerCase(), classValueAbsoluteFrequency.get(classValue)+1);
        }

        // calcola il valore di entropia per la partizione dati
        // compresa tra beginExampleIndex e endExampleIndex
        entropy = 0;

        int numberOfExamples = endExampleIndex-beginExampleIndex+1;

        for(Iterator it = classValueAbsoluteFrequency.values().iterator(); it.hasNext(); ){
            int v = (Integer)it.next();
            if(v!=0){
                float p=((float) v)/numberOfExamples;
                entropy +=( -p*Math.log10(p)/Math.log10(2)); // E(sunny|rain|..) l'entropia di un sottoinsieme di valori di un attributo
            }
        }

    }


    /**
     * @return Identificativo del nodo
     */
    public int getIdNode(){
        return idNode;
    }


    /**
     * @return Indice del primo esempio del sotto-insieme rispetto al training set complessivo
     */
    public int getBeginExampleIndex(){
        return beginExampleIndex;
    }


    /**
     * @return Indice dell'ultimo esempio del sotto-insieme rispetto al training set complessivo
     */
    public int getEndExampleIndex(){
        return endExampleIndex;
    }


    /**
     * @return Valore dell'entropia rispetto al nodo corrente
     */
    public float getEntropy(){
        return entropy;
    }


    /**
     * Metodo astratto.
     * @return Numero di figli del nodo corrente
     */
    public abstract int getNumberOfChildren();


    /**
     * @return Stringa contenente tutte le informazioni relative al nodo.
     */
    @Override
    public String toString(){
        return "[Examples: " + getBeginExampleIndex() + "-" + getEndExampleIndex() + "] Entropy: " + getEntropy() + "; ";
    }

}
