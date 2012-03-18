package classificator.tree;

import classificator.data.Data;
import java.util.Iterator;


/**
 * Estende la classe Node e modella l'entità nodo fogliare.
 */
public class LeafNode extends Node{

    private String predictedClassValue;

    /**
     * Costruttore di classe. Istanzia un oggetto invocando il costruttore della superclasse.
     * @param trainingSet Training set complessivo
     * @param beginExampleIndex Indice di inizio del sotto-insieme di training coperto dalla foglia
     * @param endExampleIndex Indice di fine del sotto-insieme di training coperto dalla foglia
     */
    LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex){
        super(trainingSet, beginExampleIndex, endExampleIndex);
        predictedClassValue = determineMostFrequentClass();
    }


    /**
     * Determina l'attributo di classe più frequente nel sotto-insieme di training.
     * @return Valore di classe rappresentato dalla foglia
     */
    private String determineMostFrequentClass(){
        Iterator it = classValueAbsoluteFrequency.keySet().iterator();

        String max = (String)it.next(); // avvaloro max alla prima chiave
        while(it.hasNext()){
            String current = (String)it.next();
            if( classValueAbsoluteFrequency.get(current) > classValueAbsoluteFrequency.get(max) ){
                max = current;
            }
        }
        return max;
    }


    /**
     * @return Valore di classe del nodo foglia corrente
     */
    public String getPredictedClassValue(){
        return predictedClassValue;
    }


    /**
     * Implementazione da class abstract Node.
     * @return Numero di split originanti dal nodo foglia, cioè 0
     */
    @Override
    public int getNumberOfChildren(){
        return 0;
    }


    /**
     * @return Stringa contenente tutte le informazioni relative al nodo foglia
     */
    @Override
    public String toString(){
        return "LEAF : " + super.toString() + " ClassValue = " + predictedClassValue + "\n";
    }

}
