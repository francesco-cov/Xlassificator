package classificator.frontend;

import classificator.TreeApplet;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

/**
 * Classe per costruire la GUI principale MainGUI
 */
public class MainGui extends JPanel{

    TreeApplet base = null;

    public JButton startPredictionBt = new JButton("Start");
    public JButton continueBt = new JButton("Continue");
    public JButton treeConstructionBt = new JButton("Tree");
    public JLabel msgAreaTxt = new JLabel("Please, answer to my request!");
    public JLabel dbLb = new JLabel("Database");
    public JLabel fileNameLb = new JLabel("Filename: ");
    public JComboBox dbList = new JComboBox();
    public JRadioButton dbRadio = new JRadioButton("Learning tree from db");
    public JRadioButton fileRadio = new JRadioButton("Reading tree from file");
    public JButton fileChooserBt = new JButton( new ImageIcon(getClass().getResource("/classificator/images/open.gif")) );
    public JButton fileSaverBt = new JButton( "Save to..", new ImageIcon(getClass().getResource("/classificator/images/save.gif")) );
    public JPanel cpTree = new JPanel();
    public JPanel cpTreeHead = new JPanel();
    public JPanel cpTreeTail = new JPanel();
    public JPanel cpTreeConstruction = new JPanel();
    public JPanel cpTreeInput = new JPanel();
    public JPanel cpTreeInputHead = new JPanel();
    public JPanel cpTreeInputTail = new JPanel();
    public JPanel cpTreeOutput = new JPanel();
    public JPanel cpPrediction = new JPanel();
    public JPanel cpStartPredicting = new JPanel();
    public JPanel cpPredictionQuery = new JPanel();
    public JPanel cpPredictionQueryLeft = new JPanel();
    public JPanel cpPredictionQueryRight = new JPanel();
    public JPanel cpRules = new JPanel();
    public JPanel cpRulesHead = new JPanel();
    public JPanel cpRulesTail = new JPanel();
    public JPanel cpMessage = new JPanel();
    public JLabel pathFileLb = new JLabel("[none]");
    public JFileChooser fc = new JFileChooser();
    public JFileChooser fs = new JFileChooser();
    public JLabel rulesLb = new JLabel();
    public JButton allRulesBt = new JButton("Print all classificator rules");
    public JButton showTreeGraphBt = new JButton("Show tree's graph");
    public JButton exportGraphBt = new JButton("Export tree's graph as image file..");
    public List<JRadioButton> queryRadioList = new LinkedList<JRadioButton>();
    public ButtonGroup radioGroup = new ButtonGroup();
    public ButtonGroup queryRadioGroup = new ButtonGroup();

    /**
     * Costruttore di classe
     * @param parent oggetto TreeApplet per i riferimenti alle componenti di tutte le GUI
     * @throws Exception
     */
    public MainGui(TreeApplet parent) throws Exception{
        base = parent;

        // instanzio i bordi
        Border treeSettings = BorderFactory.createTitledBorder("Tree Settings");
        Border treeConstruction = BorderFactory.createTitledBorder("Tree Construction");
        Border inputParameter = BorderFactory.createTitledBorder("Input Parameters");
        Border outputParameter = BorderFactory.createTitledBorder("Output Parameters");
        Border predictionSettings = BorderFactory.createTitledBorder("Prediction Settings");
        Border toStartPredProcess = BorderFactory.createTitledBorder("To Start Prediction Process");
        Border defExampleToBePredicted = BorderFactory.createTitledBorder("Define the example to be predicted");
        Border formulateQuery = BorderFactory.createTitledBorder("Formulate Query");
        Border rules = BorderFactory.createTitledBorder("Rules");
        Border messageArea = BorderFactory.createTitledBorder("Message Area");

        // imposto proprietà delle componenti grafiche
        base.setSize(600, 600); // utile solo se si esegue l'applet tramite un wrapper (l'applet in una pagina web non ne viene influenzata)
        startPredictionBt.setEnabled(false);
        dbRadio.setSelected(true);
        fileNameLb.setEnabled(false);
        fileChooserBt.setEnabled(false);
        pathFileLb.setEnabled(false);
        fileSaverBt.setEnabled(false);
        allRulesBt.setEnabled(false);
        showTreeGraphBt.setEnabled(false);
        exportGraphBt.setEnabled(false);

        // setto i bordi
        cpTree.setBorder(treeSettings);
        cpTreeConstruction.setBorder(treeConstruction);
        cpTreeInput.setBorder(inputParameter);
        cpTreeOutput.setBorder(outputParameter);
        cpPrediction.setBorder(predictionSettings);
        cpStartPredicting.setBorder(toStartPredProcess);
        cpPredictionQuery.setBorder(defExampleToBePredicted);
        cpPredictionQueryLeft.setBorder(formulateQuery);
        cpRules.setBorder(rules);
        cpMessage.setBorder(messageArea);

        // imposto i layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        cpTreeConstruction.setLayout(new BoxLayout(cpTreeConstruction, BoxLayout.Y_AXIS));
        cpTree.setLayout(new BoxLayout(cpTree, BoxLayout.Y_AXIS));
        cpTreeInput.setLayout(new BoxLayout(cpTreeInput, BoxLayout.Y_AXIS));
        cpPrediction.setLayout(new BoxLayout(cpPrediction, BoxLayout.Y_AXIS));
        cpPredictionQueryLeft.setLayout(new BoxLayout(cpPredictionQueryLeft, BoxLayout.Y_AXIS));
        cpRules.setLayout(new BoxLayout(cpRules, BoxLayout.Y_AXIS));

        // popolo il gruppo di radiobutton
        radioGroup.add(dbRadio);
        radioGroup.add(fileRadio);

        // popolo il container principale
        add(cpTree);
        add(cpPrediction);

        // popolo i jpanel (livello più alto)
        cpTreeHead.add(cpTreeConstruction);
        cpTreeHead.add(cpTreeInput);
        cpTreeHead.add(cpTreeOutput);
        cpPrediction.add(cpStartPredicting);
        cpPrediction.add(cpPredictionQuery);
        cpPrediction.add(cpRules);
        cpPrediction.add(cpMessage);

        // popolo i jpanel (livello intermedio)
        cpTree.add(cpTreeHead);
        cpTree.add(cpTreeTail);
        cpTreeInput.add(cpTreeInputHead);
        cpTreeInput.add(cpTreeInputTail);
        cpPredictionQuery.add(cpPredictionQueryLeft);
        cpPredictionQuery.add(cpPredictionQueryRight);
        cpRules.add(cpRulesHead);
        cpRules.add(cpRulesTail);

        // popolo i jpanel di basso livello con gli oggetti grafici
        cpTreeConstruction.add(dbRadio);
        cpTreeConstruction.add(fileRadio);
        cpTreeInputHead.add(dbLb);
        cpTreeInputHead.add(dbList);
        cpTreeInputTail.add(fileNameLb);
        cpTreeInputTail.add(pathFileLb);
        cpTreeInputTail.add(fileChooserBt);
        cpTreeOutput.add(fileSaverBt);
        cpTreeTail.add(treeConstructionBt);
        cpStartPredicting.add(startPredictionBt);
        cpPredictionQueryRight.add(continueBt);
        cpPredictionQueryLeft.add(new JRadioButton("0: ...                       "));
        cpPredictionQueryLeft.add(new JRadioButton("1: ...                       "));
        cpPredictionQueryLeft.add(new JRadioButton("2: ...                       "));
        base.utility.setEnabling(cpPredictionQuery, false);
        cpRulesTail.add(rulesLb);
        cpRulesHead.add(allRulesBt);
        cpRulesHead.add(showTreeGraphBt);
        cpRulesHead.add(exportGraphBt);
        cpMessage.add(msgAreaTxt);
    }

}
