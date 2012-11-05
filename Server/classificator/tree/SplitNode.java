package classificator.tree;

import classificator.data.Attribute;
import classificator.data.Data;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;


/**
 * Modella l'astrazione dell'entità nodo di split (continuo o discreto).
 * Estende la classe Node e implementa l'interfaccia Comparable per realizzare il confronto tra oggetti di tipo SplitNode.
 */
public abstract class SplitNode extends Node implements Comparable{

    private Attribute attribute; // Attributo dello split
    List<SplitInfo> mapSplit = new ArrayList<SplitInfo>(); // Lista degli archi uscenti dal nodo di split 
    float infoGain;// Information gain 


    /**
     * Classe interna che aggrega tutte le informazioni riguardanti un nodo di split
     */
    class SplitInfo implements Serializable{
        
        Object splitValue;
        int beginIndex;
        int endIndex;
        int numberChild;
        String comparator = "=";
        

        /**
         * Costruttore che avvalora gli attributi di classe per split a valori discreti.
         * @param splitValue Valore che definisce lo split
         * @param beginIndex Indice di inizio del sotto-insieme di training
         * @param endIndex Indice di fine del sotto-insieme di training
         * @param numberChild Identificatore dello split corrente
         */
        SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild){
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
        }
        

        /**
         * Costruttore che avvalora gli attributi di classe per split a valori continui.
         * @param splitValue Valore che definisce lo split
         * @param beginIndex Indice di inizio del sotto-insieme di training
         * @param endIndex Indice di fine del sotto-insieme di training
         * @param numberChild Identificatore dello split corrente
         * @param comparator Operatore matematico che definisce il test nel nodo corrente
         */
        SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator){
            this.splitValue = splitValue;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
            this.numberChild = numberChild;
            this.comparator = comparator;
        }
        

        /**
         * @return Indice di inizio del sotto-insieme di training
         */
        int getBeginIndex(){
            return beginIndex;			
        }
        

        /**
         * @return Indice di fine del sotto-insieme di training
         */
        int getEndIndex(){
            return endIndex;
        }
        

        /**
         * @return Valore dello split
         */
        Object getSplitValue(){
            return splitValue;
        }


        /**
         * @return Stringa contenente i valori degli attributi di classe
         */
        @Override
        public String toString(){
            return "Child " + numberChild + ", Split value " + comparator + 
                   " " + splitValue + " [Examples:" + beginIndex + "-" + endIndex + "]";
        }
        

        /**
         * @return Valore dell'operatore matematico che definisce il test
         */
        String getComparator(){
            return comparator;
        }

    }
    

    /**
     * Metodo abstract per generare le informazioni necessarie per ciascuno degli split candidati (in mapSplit[]).
     * @param trainingSet Training set complessivo
     * @param beginExampleIndex Indice di inizio del sotto-insieme di training
     * @param endExampleIndex Indice di fine del sotto-insieme di training
     * @param attribute Attributo indipendente sul quale si definisce lo split
     */
    abstract void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute);

    /**
     * Metodo abstract per modellare la condizione di test. Per ogni valore di test c'è un ramo dallo split.
     * @param value Valore dell'attributo che si vuole testare
     * @return Numero del ramo di split
     */
    abstract int testCondition(Object value);

    /**
     * Costruttore di classe. Invoca il costruttore della superclasse, determina
     * i possibili split, computa l'entropia per l'attributo usato
     * nello split e determina il corrispondente information gain.
     * @param trainingSet Training set complessivo
     * @param beginExampleIndex Indice di inizio del sotto-insieme di training
     * @param endExampleIndex Indice di fine del sotto-insieme di training
     * @param attribute Attributo indipendente sul quale si definisce lo split
     */
    SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute){
        super(trainingSet, beginExampleIndex, endExampleIndex);
        this.attribute = attribute;
        trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
        setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);

        //compute entropy = sum_i{pi*E(i)} i=1..m ;m = number of classes
        float splitEntropy = 0;
        for(int i=0; i<mapSplit.size(); i++){
            float p=((float)(mapSplit.get(i).getEndIndex()-mapSplit.get(i).getBeginIndex()+1))/(endExampleIndex-beginExampleIndex+1); // Per sunny 5/14
            float localEntropy = new LeafNode(trainingSet, mapSplit.get(i).getBeginIndex(), mapSplit.get(i).getEndIndex()).getEntropy(); // Per sunny 0.971
            splitEntropy+=(p*localEntropy); // i=0: E(sunny)=5/14*0.971; fine ciclo: E(Outlook)=0.694
        }

        //compute info gain
        infoGain = entropy-splitEntropy; // splitEntropy==E(Outlook)
    }


    /**
     * @return Attributo del nodo di split
     */
    public Attribute getAttribute(){
        return attribute;
    }


    /**
     * @return Information gain dello split corrente
     */
    public float getInformationGain(){
        return infoGain;
    }


    /**
     * Implementazione da class abstract Node.
     * @return Numero dei rami originanti nel nodo corrente
     */
    @Override
    public int getNumberOfChildren(){
        return mapSplit.size();
    }


    /**
     * @param child Indice del ramo in mapSplit[]
     * @return Informazioni per il ramo in mapSplit[] indicizzato da child
     */
    SplitInfo getSplitInfo(int child){
        return mapSplit.get(child);
    }


   /**
    * Necessario per la predizione di nuovi esempi.
    * @return Array di stringhe ciascuna della quali riporta le informazioni concatenate relative ad ogni
    * oggetto SplitInfo in mapsplit[]
    */
    public String[] formulateArrayQuery(){
        // Restituisce la domanda da porre all'utente per capire quale ramo dello split seguire
        String query[] = new String[mapSplit.size()];
        for(int i=0; i<mapSplit.size(); i++)
            query[i] = (mapSplit.get(i).numberChild + ": " + attribute +" "+ mapSplit.get(i).getComparator() +" "+ mapSplit.get(i).getSplitValue());
        return query;
    }


    /**
     * Ridefinizione del metodo compareTo. Confronta i valori di information gain dei due nodi
     * e restituisce l'esito.
     * @param o Oggetto (nodo di split) da confrontare con il nodo corrente
     * @return Esito del confronto [0: uguali, -1: gain minore, 1: gain maggiore]
     */
    @Override
    public int compareTo(Object o){
        if( this.getInformationGain() == ((SplitNode)o).getInformationGain() )
            return 0;
        else
            if( this.getInformationGain() < ((SplitNode)o).getInformationGain() )
                return -1;
            else // >
                return 1;
    }


    /**
     * @return Stringa contenente tutte le informazioni relative al nodo di split
     */
    @Override
    public String toString(){
        String v = "SPLIT : attribute = " + attribute + " " + super.toString() +  "Info Gain: " + getInformationGain() + "\n" ;
        for(int i=0; i<mapSplit.size(); i++){
                v += "\t" + mapSplit.get(i) + "\n";
        }
        return v;
    }

}
