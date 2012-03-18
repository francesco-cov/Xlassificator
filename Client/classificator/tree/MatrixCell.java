package classificator.tree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe per la rappresentazione dei nodi del DecisionTree
 * in forma di celle della matrice di adiacenza del grafo
 * per la costruzione e il disegno lato client del grafico
 */
public class MatrixCell implements Serializable{
    
    public String label; // etichetta sul nodo (nome attributo o valore di classe)
    public int childNumber; // numero di figli
    // "vettore" (dizionario) di adiacenze con archi etichettati
    // Key: indice del figlio
    // Value: valore di split
    public Map<Integer, Object> adjacency = new HashMap<Integer, Object>();


    /**
     * @return true: il nodo ha figli;
     *         false: altrimenti.
     */
    public boolean hasChildren(){
        return childNumber > 0;
    }


    /**
     * @return Stringa rappresentante la cella in forma:
     *         se SplitNode: NomeAttr i_figlio1->valore_di_split i_figlio2->valore_di_split;
     *         se LeafNode: valore_di_classe
     */
    @Override
    public String toString(){
        Integer[] keyArray = adjacency.keySet().toArray(new Integer[0]);
        String adjString = new String();
        String output = label;
        for(int i=0; i<keyArray.length; i++)
            adjString += adjacency.get(keyArray[i].intValue()) + "->" + keyArray[i].intValue() + " ";
        if(childNumber > 0)
            output += " - " + childNumber + " - " + adjString;
        return output;
    }

}
