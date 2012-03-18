package classificator.frontend;

import classificator.tree.MatrixCell;
import java.awt.Color;
import java.awt.Container;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;


public class TreeGraph extends JFrame{

    MatrixCell[] matrix = null;
    public JGraph graph = null;

    
    /**
     * Costruttore di classe
     * @param matrix Array di MatrixCell dal quale costruire il grafico
     */
    public TreeGraph(MatrixCell[] matrix){
        super("Decision tree's graph");
        setSize(1200, 800);
        Container cp = getContentPane();

        this.matrix = matrix;

        GraphLayoutCache graphCont = new GraphLayoutCache();
        graph = new JGraph(graphCont);
        graph.setEditable(false);
        graph.setMoveable(false);
        graph.setSelectionEnabled(false);
        cp.add(new JScrollPane(graph));

        if(matrix.length == 1){
            DefaultGraphCell rootLeaf = new DefaultGraphCell(matrix[0].label);
            GraphConstants.setBounds(rootLeaf.getAttributes(), new Rectangle2D.Double(50, 50, 90, 30));
            GraphConstants.setOpaque(rootLeaf.getAttributes(), true);
            GraphConstants.setGradientColor(rootLeaf.getAttributes(), Color.GREEN);
            graphCont.insert(rootLeaf);
        }
        else{
            DefaultGraphCell graphCell[] = new DefaultGraphCell[matrix.length];
            List<DefaultEdge> graphEdgeList = new ArrayList<DefaultEdge>(matrix.length-1);
            graphCell = makeCellAndLayout(graphCell);
            graphEdgeList = makeEdge(graphEdgeList, graphCell, 0);

            graphCont.insert(graphCell);
            graphCont.insert(graphEdgeList.toArray());
        }

        pack();
        setSize(getWidth()+50, getHeight()+50);
    }


    /**
     * Costruisce un array di DefaultGraphCell valorizzando gli attributi come
     * label, colore e posizionandoli correttamente. Inizia il processo dalla radice
     * e una volta finiti i figli della radice chiama su di essi
     * makeCellAndLayout(DefaultGraphCell[] graphCell, int child, int x1, int x2, int y)
     * @param graphCell Array di DefaultGraphCell per iniziare la ricorsione
     * @return Array di DefaultGraphCell (celle, elementi grafici) completamente riempito
     *         (con settate le proprietà cella per cella come colore e posizione)
     */
    private DefaultGraphCell[] makeCellAndLayout(DefaultGraphCell[] graphCell){
        graphCell[0] = new DefaultGraphCell(matrix[0].label);
        graphCell[0].addPort();
        GraphConstants.setBounds(graphCell[0].getAttributes(), new Rectangle2D.Double(getWidth()/2-45, 15, 90, 30));
        GraphConstants.setOpaque(graphCell[0].getAttributes(), true);

        if(!matrix[0].hasChildren())
            GraphConstants.setGradientColor(graphCell[0].getAttributes(), Color.GREEN);
        else{
            GraphConstants.setGradientColor(graphCell[0].getAttributes(), Color.ORANGE);
            Integer[] rootChild = matrix[0].adjacency.keySet().toArray(new Integer[0]);

            for(int i=0; i<rootChild.length; i++)
                graphCell = makeCellAndLayout( graphCell, rootChild[i], ((getWidth()/rootChild.length)*i), ((getWidth()/rootChild.length)*i + (getWidth()/rootChild.length)), 55+45);
        }
        return graphCell;
    }


    /**
     * Continua il popolamento di graphCell
     * @param graphCell Array di DefaultGraphCell per iniziare la ricorsione
     * @param child Nodo corrente
     * @param x1 Estremo sinistro dell'intervallo disponibile orizzontalmente per disporre la cella
     * @param x2 Estremo destro dell'intervallo disponibile orizzontalmente per disporre la cella
     * @param y Punto per il posizionamento verticale della cella
     * @return Array di DefaultGraphCell popolato
     */
    private DefaultGraphCell[] makeCellAndLayout(DefaultGraphCell[] graphCell, int child, int x1, int x2, int y){
        graphCell[child] = new DefaultGraphCell(matrix[child].label);
        GraphConstants.setOpaque(graphCell[child].getAttributes(), true);
        GraphConstants.setBounds(graphCell[child].getAttributes(), new Rectangle2D.Double( (x1+x2)/2-45, y, 90, 30));

        if(!matrix[child].hasChildren()){
            graphCell[child].addPort(); // "gancio" per il successivo inserimento degli archi
            GraphConstants.setGradientColor(graphCell[child].getAttributes(), Color.GREEN);
        }
        else{
            graphCell[child].addPort();
            GraphConstants.setGradientColor(graphCell[child].getAttributes(), Color.ORANGE);

            Integer[] subChild = matrix[child].adjacency.keySet().toArray(new Integer[0]);
            for(int i=0; i<subChild.length; i++)
                graphCell = makeCellAndLayout( graphCell, subChild[i], x1+(((x2-x1)/subChild.length)*i), x1+(((x2-x1)/subChild.length)*i) + ((x2-x1)/subChild.length), y+85 );
        }
        return graphCell;
    }


    /**
     * Visita tutti i nodi dell'albero generando una lista di archi con definiti
     * sorgente e destinazione
     * @param graphEdgeList Lista di archi, parametro necessario per la ricorsione
     * @param graphCell Array di DefaultGraphCell, per l'inserimento degli archi tra celle (tra elementi grafici)
     * @param node Nodo di partenza
     * @return Lista di archi
     */
    private List<DefaultEdge> makeEdge(List<DefaultEdge> graphEdgeList, DefaultGraphCell[] graphCell, int node){
        
        if(matrix[node].hasChildren()){
            Integer[] child = matrix[node].adjacency.keySet().toArray(new Integer[0]);
            for(int i=0; i<child.length; i++){
                DefaultEdge arco = new DefaultEdge(matrix[node].adjacency.get(child[i]));
                arco.setSource(graphCell[node].getChildAt(0));
                arco.setTarget(graphCell[child[i]].getChildAt(0));
                GraphConstants.setLineEnd(arco.getAttributes(), GraphConstants.ARROW_CLASSIC);
                GraphConstants.setEndFill(arco.getAttributes(), true);
                graphEdgeList.add(arco);
                graphEdgeList = makeEdge(graphEdgeList, graphCell, child[i]);
            }
        }
        return graphEdgeList;
    }

}
