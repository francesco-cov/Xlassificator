package classificator.tree;

import classificator.exception.*;
import classificator.data.ContinuousAttribute;
import classificator.data.Data;
import classificator.data.DiscreteAttribute;
import java.io.*;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Modella l'entità albero di decisione come insieme di sotto-alberi.
 */
public class DecisionTree implements Serializable{

    private Node root = null;
    private DecisionTree childTree[];

    /**
     * Costruttore di classe.
     */
    public DecisionTree(){
        // DO NOTHING
    }


    /**
     * Costruttore di classe. Instanzia un sotto-albero dell'intero albero e avvia
     * l'induzione dell'albero dagli esempi di training in input.
     * @param trainingSet Training set complessivo
     * @throws NoSplitException
     * @throws UnknownAttributeIndexException
     */
    public DecisionTree(Data trainingSet) throws NoSplitException, UnknownAttributeIndexException{
        if(trainingSet.getNumberOfExplanatoryAttributes() == 0)
            throw new NoSplitException();
        learnTree(trainingSet, 0, trainingSet.getNumberOfExamples()-1, trainingSet.getNumberOfExamples()*10/100);
    }


    /**
     * @return Radice del sotto-albero corrente
     */
    public Node getRoot(){
        return root;
    }


    /** Restituisce il figlio in posizione 'child'
     * @param child Indice che rappresenta la radice del sotto-albero in childTree[]
     * @return Sotto-albero originante nel nodo childTree[child]
     */
    public DecisionTree subTree(int child){
        return childTree[child];
    }
    

    /**
     * Metodo ricorsivo che genera un sotto-albero con il sotto-insieme di input, istanziando
     * un nodo fogliare oppure un nodo di split. In quest'ultimo caso determinerà il miglior nodo
     * rispetto al sotto-insieme di input e assocerà ad esso un sottoalbero. Ricorsivamente per ogni oggetto DecisionTree in
     * childTree[] sarà invocato il metodo per l'apprendimento su un insieme ridotto del sotto-insieme attuale.
     * @param trainingSet Training set complessivo
     * @param beginExampleIndex Indice di inizio del sotto-insieme di training
     * @param endExampleIndex Indice di fine del sotto-insieme di training
     * @param numberOfExamplesPerLeaf Numero massimo di esempi che una foglia deve contenere
     * @throws NoSplitException
     * @throws UnknownAttributeIndexException
     */
    private void learnTree(Data trainingSet, int beginExampleIndex, int endExampleIndex, int numberOfExamplesPerLeaf) throws NoSplitException, UnknownAttributeIndexException{
        if(isLeaf(trainingSet, beginExampleIndex, endExampleIndex, numberOfExamplesPerLeaf)){
            //determina la classe che compare più frequentemente nella partizione corrente
            root = new LeafNode(trainingSet, beginExampleIndex, endExampleIndex);
        }
        else{ //split node
            root = determineBestSplitNode(trainingSet, beginExampleIndex, endExampleIndex);
            if(root.getNumberOfChildren()>1){
                childTree=new DecisionTree[root.getNumberOfChildren()];
                for(int i=0;i<root.getNumberOfChildren();i++){
                    childTree[i] = new DecisionTree();
                    childTree[i].learnTree(trainingSet, ((SplitNode)root).getSplitInfo(i).beginIndex, ((SplitNode)root).getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
                }
            }
            else
                root = new LeafNode(trainingSet, beginExampleIndex, endExampleIndex);
        }
    }


    /**
     * Verifica se il sotto-insieme corrente può essere coperto da una foglia controllando
     * la cardinalità di tale sotto-insieme e la frequenza dei valori dell'attributo di classe.
     * @param trainingSet Training set complessivo
     * @param beginExampleIndex Indice di inizio del sotto-insieme di training
     * @param endExampleIndex Indice di fine del sotto-insieme di training
     * @param numberOfExamplesPerLeaf Numero minimo di esempi che devono ricadere in una foglia
     * @return esito sulle condizioni per i nodi fogliari
     */
    private boolean isLeaf(Data trainingSet, int beginExampleIndex, int endExampleIndex, int numberOfExamplesPerLeaf){
        //Il nodo corrente è foglia se:
        //1) il numero di esempi addestramento che ricadono nella partizione corrente è minore di numberOfExamplesPerLeaf, oppure
        //2) tutti gli esempi addestramento che ricadono nella partizione corrente appartengono alla stessa classe
        boolean RC=true;
        if( (endExampleIndex-beginExampleIndex)>numberOfExamplesPerLeaf ){
            for(int i=beginExampleIndex+1; i<=endExampleIndex && RC==true; i++)
                if( trainingSet.getClassValue(beginExampleIndex).equals(trainingSet.getClassValue(i)) == false )
                    RC = false;
        }
        return RC;
    }
    

    /**
     * Per ciascun attributo indipendente, istanzia un DiscreteAttribute o un ContinuousAttribute
     * e ne computa l'information gain. Restituisce il nodo con maggior information gain.
     * @param trainingSet Training set complessivo
     * @param beginExampleIndex Indice di inizio del sotto-insieme di training
     * @param endExampleIndex Indice di fine del sotto-insieme di training
     * @return Nodo di split migliore per il sotto-insieme di training
     * @throws UnknownAttributeIndexException
     */
    private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) throws UnknownAttributeIndexException{
        SortedSet<SplitNode> ts = new TreeSet<SplitNode>();
        SortedSet<SplitNode> tsDup = new TreeSet<SplitNode>(); // albero che "raccoglie" gli eventuali nodi duplicati

        for(int i=0; i<trainingSet.getNumberOfExplanatoryAttributes(); i++){
            SplitNode current = null;
            if(trainingSet.getExplanatoryAttribute(i) instanceof DiscreteAttribute)
                current = new DiscreteNode( trainingSet, begin, end, (DiscreteAttribute)(trainingSet.getExplanatoryAttribute(i)) );
            else
                current = new ContinuousNode( trainingSet, begin, end, (ContinuousAttribute)(trainingSet.getExplanatoryAttribute(i)) );
            if( ts.add(current) == false )
                tsDup.add(current);
        }
        if(!tsDup.isEmpty())
            if(ts.last().compareTo(tsDup.last())==0) // se il bestNode è uguale al bestNode dei duplicati
                System.err.println("Two (or more) bestSplitNode; [Sub]Tree built choosing: "+ ts.last().getAttribute().toString());

        SplitNode bestNode = ts.last();

        // applico l'ordinamento coerente con il tipo di split scelto
        trainingSet.sort(bestNode.getAttribute(), begin, end);

        return bestNode;
    }


    /**
     * @return Stringa contenente tutte le informazioni relative all'intero albero.
     */
    @Override
    public String toString(){
        String textTree = root.toString() + "\n";
        if(root instanceof LeafNode){
            
        }
        else{ // split node
            for(int i=0; i<childTree.length; i++)
                textTree += childTree[i];
        }
        return textTree;
    }


    /**
     * Invoca il metodo di classe toString().
     */
    public void printTree(){
        System.out.println("********* TREE **********\n");
        System.out.println(toString());
        System.out.println("*************************\n\n");
    }


    /**
     * Scandisce ciascun ramo dell'albero completo dalla radice alla foglia concatenando le
     * informazioni dei nodi di split fino al nodo foglia.
     * @return Stringa contenente tutte le regole relative al DecisionTree considerato
     */
     public String getRules(){
        String acc = new String();
        String rule = "If(";
        if (root instanceof LeafNode)
            acc += "Class = " + ((LeafNode)root).getPredictedClassValue() + "\n";
        else{ // radice dell'albero
            if (root instanceof DiscreteNode){
                rule += ((SplitNode)root).getAttribute().getName() + "="; // 'rules' conterrà il nome del nodo radice + "="
                String current = null;
                for(int i=0; i<((SplitNode)root).mapSplit.size(); i++){ // per ogni arco uscente dalla radice
                    current = rule + ((SplitNode)root).mapSplit.get(i).splitValue +")"; // aggiungo a "If(Outlook=" l'etichetta dell'arco +")"
                    acc = childTree[i].getRules(current, acc); // chiamo printRules sul figlio i della radice
                }
            }
            else{ // instanceof ContinuosNode
               rule += ((SplitNode)root).getAttribute().getName();
               String current = null;
               for(int i=0; i<((SplitNode)root).mapSplit.size(); i++){
                    current = rule + ((SplitNode)root).mapSplit.get(i).getComparator() + ((SplitNode)root).mapSplit.get(i).splitValue +")"; // Aggiungo a "If(Outlook=Sunny) AND (Temperature [comparator di quell'arco] "l'etichetta dell'arco +")"
                    acc = childTree[i].getRules(current, acc);
               }
            }
        }
        return acc;
     }


     /**
      * Metodo ricorsivo che supporta il metodo public String getRules().
      * Concatena alle informazioni in current del precedente nodo quelle del nodo root del
      * corrente sotto-albero.
      * @param current Stringa contenente tutte le informazioni relative al nodo precedente
      * @param acc Stringa accumulatore contenente le regole ottenute. Alla prima invocazione del metodo, 
      *        acc è vuota
      * @return Stringa contenente tutte le regole ottenute
      */
    private String getRules(String current, String acc){ // current="If(Outlook=Rain){
        if (root instanceof LeafNode)
           acc += current + "==> Class = " + ((LeafNode)root).getPredictedClassValue() + "\n";
        else{
           if (root instanceof DiscreteNode){
               current += " AND (" + ((SplitNode)root).getAttribute().getName() + "="; // Aggiungo a current il nome del nodo(Wind) in cui entra l'arco citato in current
               String tmp = null;
                   for(int i=0; i<((SplitNode)root).mapSplit.size(); i++){ // Per ogni arco uscente dal nodo(Wind)
                        tmp = current + ((SplitNode)root).mapSplit.get(i).splitValue +")"; // Aggiungo a "If(Outlook=Rain) AND (Wind=" l'etichetta dell'arco +")"
                        acc = childTree[i].getRules(tmp, acc); // Chiamo printRules sul nodo in cui entra l'arco i
                   }
           }
           else{ // instanceof ContinuosNode
               current += " AND (" + ((SplitNode)root).getAttribute().getName();
               String tmp = null;
                   for(int i=0; i<((SplitNode)root).mapSplit.size(); i++){ // Per ogni arco uscente dal nodo(Temperature)
                        tmp = current + ((SplitNode)root).mapSplit.get(i).getComparator() + ((SplitNode)root).mapSplit.get(i).splitValue +")"; // Aggiungo a "If(Outlook=Sunny) AND (Temperature [comparator di quell'arco] "l'etichetta dell'arco +")"
                        acc = childTree[i].getRules(tmp, acc); // Chiamo printRules sul nodo in cui entra l'arco i
                   }
           }
        }
        return acc;
    }


    /**
     * Deserializza da un array di raw bytes un oggetto DecisionTree (se possibile)
     * (necessario per ottenere un DecisionTree dal client senza che quest'ultimo
     * sia al corrente che quella sequenza di bytes rappresentano un DecisionTree)
     * @param treeByte dati raw dal quale costruire l'istanza di DecisionTree
     * @return L'albero rappresentato come istanza di DecisionTree
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static DecisionTree deserializeFromByteArray(byte[] treeByte) throws FileNotFoundException, IOException, ClassNotFoundException{
        // Non è necessario bufferizzare in quanto il trasferimento è memoria->memoria
        InputStream is = new ByteArrayInputStream(treeByte);
        // Definisco l'InputStream necessario per la deserializzazione dell'oggetto (ObjectInputStream)
        ObjectInputStream objStream = new ObjectInputStream(is);
        DecisionTree tree = (DecisionTree)objStream.readObject(); // deserializzazione

        is.close(); // chiudo lo stream
        return tree;
    }


    /**
     * Serializza in un array di raw bytes l'albero
     * (necessario per inviare l'istanza di DecisionTree al client per la serializzazione
     *  su disco, anche se il client non ha le classi necessarie a gestire l'albero come DecisionTree)
     * @return Array di dati grezzi
     * @throws IOException
     */
    public byte[] serializeToByteArray() throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(out);
        objOut.writeObject((Object)this);
        byte[] bytes = out.toByteArray();
        objOut.close();
        return bytes;
    }


    /**
     * Opera la trasformazione del decisiontree in un grafo rappresentato
     * mediante matrice di adiacenza con archi etichettati
     * @return Array di celle (matrice) rappresentanti l'albero sottoforma di grafo
     *         di adiacenza del tipo:
     *         [0] outlook - 3 - overcast->1 rain->2 sunny->5
     *         [1] yes
     *         [2] wind - 2 - strong->3 weak->4
     *         [3] ...
     */
    public MatrixCell[] getMatrixGraph(){
        int totalNumberOfNode = countNode();
        MatrixCell[] matrix = new MatrixCell[totalNumberOfNode];
        
        matrix = getMatrixGraph(matrix, 0);
        return matrix;
    }


    /**
     * Metodo di supporto a getMatrixGraph per implementare
     * la ricorsione per la visita del DecisionTree e popolare
     * la matrice
     * @param matrix Array di MatrixCell
     * @param index Indice del nodo del DecisionTree dal quale partire
     * @return MatrixCell[] popolata
     */
    private MatrixCell[] getMatrixGraph(MatrixCell[] matrix, int index){
        MatrixCell cell = new MatrixCell();
        if (root instanceof LeafNode){
            cell = new MatrixCell();
            cell.label = ((LeafNode)root).getPredictedClassValue();
            cell.childNumber = 0;
            matrix[index] = cell;
        }
        else{ // SplitNode
            cell.label = ((SplitNode)root).getAttribute().getName();
            cell.childNumber = root.getNumberOfChildren();
            int totNodes = index;
            for(int i=0; i<root.getNumberOfChildren(); i++){
                for(int j=0; j<i; j++){
                    totNodes += childTree[j].countNode();
                }
                if(root instanceof DiscreteNode)
                    cell.adjacency.put(totNodes+1, ((DiscreteNode)root).getSplitInfo(i).splitValue);
                else
                    cell.adjacency.put(totNodes+1, ((ContinuousNode)root).getSplitInfo(i).getComparator() + ((ContinuousNode)root).getSplitInfo(i).splitValue);
                matrix = childTree[i].getMatrixGraph(matrix, totNodes+1);
                totNodes = index;
            }
            matrix[index] = cell;
        }
        return matrix;
    }


    /**
     * Conta di quanti nodi è formato l'albero e restituisce
     * il valore intero
     * @return Numero di nodi (leaf+split) dell'albero
     */
    public int countNode(){
        int tot = 0;
        if (root instanceof LeafNode)
            tot = 1;
        else{ // SplitNode
            for(int i=0; i<root.getNumberOfChildren(); i++)
                tot += childTree[i].countNode();
            tot++;
        }
        return tot;
    }

}