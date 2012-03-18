package classificator.backend;

import classificator.TreeApplet;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

/**
 * Classe che fornisce servizi di utilità
 */
public class Util{

    private TreeApplet base = null;

    /**
     * Classe interna per implementare il filefiltering nelle dialog relative
     */
    static public class TreeFileFilter extends FileFilter{
        String acceptedExt = null;

        /**
         * Costruttore di classe
         * @param ext Stringa relativa all'estensione da accettare
         */
        TreeFileFilter(String ext){
            acceptedExt = ext;
        }


        /**
         * @param f File
         * @return true se è una directory o il file è nella forma xxx.ext;
         *         false altrimenti.
         */
        @Override
        public boolean accept(File f){
            String ext = f.getName().substring( f.getName().lastIndexOf('.')+1, f.getName().length() );
            if ( f.isDirectory() || ext.equals(acceptedExt))
                return true;
            else
                return false;
        }


        /**
         * @return Stringa ".'ext' file"
         */
        @Override
        public String getDescription(){
            return "." + acceptedExt + " file";
        }

    }

    /**
     * Costruttore di classe
     * @param parent oggetto TreeApplet per i riferimenti alle componenti di tutte le GUI
     */
    public Util(TreeApplet parent){
        base = parent;
    }


    /**
     * Popola la combo box con la lista delle tabelle
     * disponibili per l'apprendimento del trainingset
     * @throws IOException
     */
    public void fillDbList() throws IOException{
        List<String> list = null;
        base.out.writeObject(new Integer(0));
        try{
            list = (List<String>)base.in.readObject();
            Iterator it = list.iterator();
            while(it.hasNext())
                base.mainPanel.dbList.addItem(it.next());
        }
        catch(Exception e){
            base.mainPanel.dbRadio.setEnabled(false);
            base.mainPanel.dbList.setEnabled(false);
            base.mainPanel.dbLb.setEnabled(false);
            base.mainPanel.dbRadio.setSelected(false);
            base.mainPanel.fileRadio.setSelected(true);
            base.mainPanel.fileNameLb.setEnabled(true);
            base.mainPanel.fileChooserBt.setEnabled(true);
            base.mainPanel.pathFileLb.setEnabled(true);
            JOptionPane.showMessageDialog(base.cp, "Database's not reachable");
        }
    }


    /**
     * Rende attivi/disattivi tutti i componenti presenti nel JPanel in input
     * @param panel JPanel di input
     * @param bool true se si vuole renderli attivi;
     *             false altrimenti.
     */
    public void setEnabling(JPanel panel, boolean bool){
        Component compArr[] = panel.getComponents();

        for(int i=0; i < compArr.length ; i++){
            if(compArr[i] instanceof JPanel){
                try{
                    setEnabling((JPanel)compArr[i], bool);
                }
                catch(ClassCastException e){
                    JOptionPane.showMessageDialog(base.cp, e.getClass().getName(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else{
                compArr[i].setEnabled(bool);
                compArr[i].setFocusable(bool);
            }
        }
    }


    /**
     * Aggiunge radio button a runtime a seconda della struttura
     * del decision tree e delle scelte dell'utente
     * @param queryArray Un radio button per elemento di queryArray
     */
    public void addQueryRadio(String[] queryArray){
        JRadioButton tmpRadioBt = null;
        for(int i=0; i<queryArray.length; i++){
            tmpRadioBt = new JRadioButton( queryArray[i] );
            base.mainPanel.queryRadioList.add( tmpRadioBt );
            base.mainPanel.cpPredictionQueryLeft.add( tmpRadioBt );
            base.mainPanel.queryRadioGroup.add(tmpRadioBt);
        }
        base.mainPanel.queryRadioList.get(0).setSelected(true);
        base.mainPanel.cpPredictionQueryLeft.revalidate(); // necessario per l'aggiunta dei radio a runtime
        base.mainPanel.cpPredictionQueryLeft.repaint();    // come sopra
    }


    /**
     * Rimuove tutti i radio button relativi alla query
     */
    public void clearQueryRadio(){
        base.mainPanel.cpPredictionQueryLeft.removeAll();
        base.mainPanel.queryRadioList.clear();
    }


    /**
     * Concatena la regola corrente alla regola in costruzione
     * (all'atto del prediction)
     * @param txt Regola da concatenare
     */
    public void appendRules(String txt){
        if(!base.mainPanel.rulesLb.isVisible())
            base.mainPanel.rulesLb.setVisible(true);
        base.mainPanel.rulesLb.setText(base.mainPanel.rulesLb.getText() + txt);
    }

}