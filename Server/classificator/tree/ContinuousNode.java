package classificator.tree;

import classificator.data.Attribute;
import classificator.data.ContinuousAttribute;
import classificator.data.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Estende la classe SplitNode e rappresenta un nodo corrispondente ad un attributo continuo.
 */
public class ContinuousNode extends SplitNode{

    /**
     * Costruttore di classe. Istanzia un oggetto invocando il costruttore della superclasse.
     * @param trainingSet Training set complessivo
     * @param beginExampleIndex Indice di inizio del sotto-insieme di training
     * @param endExampleIndex Indice di fine del sotto-insieme di training
     * @param attribute Attributo indipendente sul quale si definisce lo split
     */
    public ContinuousNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, ContinuousAttribute attribute){
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }


    /**
     * Implementazione da class abstract SplitNode.
     * Genera le informazioni necessarie per ciascuno degli split candidati (in mapSplit[]).
     * @param trainingSet Training set complessivo
     * @param beginExampleIndex Indice di inizio del sotto-insieme di training
     * @param endExampleIndex Indice di fine del sotto-insieme di training
     * @param attribute Attributo indipendente sul quale si definisce lo split
     */
    @Override
    void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute){
        // Update mapSplit defined in SplitNode -- contiene gli indici del partizionamento
        Float currentSplitValue = (Float)trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
        float bestInfoGain = 0;
        List<SplitInfo>  bestMapSplit = null;

        for(int i=beginExampleIndex+1; i<=endExampleIndex; i++){
            Float value = (Float)trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if(value.floatValue() != currentSplitValue.floatValue() ){
                if(bestMapSplit == null){
                    bestMapSplit = new ArrayList<SplitInfo>();
                    bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i-1, 0, "<="));
                    bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
                }
                else{
                    // compute entropy = sum_i{pi*E(i)} i=1..m ;m = number of classes
                    float splitEntropy=0;
                    float p = ((float)((i-1)-beginExampleIndex+1))/(endExampleIndex-beginExampleIndex+1);
                    float localEntropy = new LeafNode(trainingSet, beginExampleIndex, i-1).getEntropy();
                    splitEntropy += (p*localEntropy);
                    p = ((float)(endExampleIndex-i+1))/(endExampleIndex-beginExampleIndex+1);
                    localEntropy = new LeafNode(trainingSet, i, endExampleIndex).getEntropy();
                    splitEntropy += (p*localEntropy);
                    //compute info gain
                    infoGain = entropy-splitEntropy;
                    if(bestInfoGain < infoGain){
                        bestInfoGain = infoGain;
                        bestMapSplit.set(0, new SplitInfo(currentSplitValue, beginExampleIndex, i-1, 0, "<="));
                        bestMapSplit.set(1, new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
                    }
                }
                currentSplitValue = value;
            }
        }
        mapSplit = bestMapSplit;
        //rimuovo split inutili (che includono tutti gli esempi nella stessa partizione)
        if((mapSplit.get(1).getBeginIndex() == mapSplit.get(1).getEndIndex()))
            mapSplit.remove(1);
    }
	 

    /**
     * Implementazione da class abstract SplitNode.
     * Effettua il controllo del valore di input rispetto agli split di mapSplit[] e restituisce
     * l'identificativo dello split con cui il test è positivo.
     * @param value Valore continuo dell'attributo che si vuole testare
     * @return Numero del ramo di split
     */
    @Override
    int testCondition (Object value){
        if( ((Float)value).floatValue() <= ((Float)mapSplit.get(0).splitValue).floatValue() )
            return 0;
        else
            return 1;
    }


    /**
     * @return Rappresentazione del nodo continuo di split
     */
    @Override
    public String toString(){
        return "CONTINUOUS " + super.toString();
    }

}