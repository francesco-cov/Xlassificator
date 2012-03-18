package classificator.backend;

import classificator.TreeApplet;
import classificator.frontend.TreeGraph;
import classificator.tree.MatrixCell;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

/**
 * Classe per gestire le azioni di MainGUI
 */
public class MainGUIActionHandler{
    
    private  TreeApplet base = null;

    /**
     * Costruttore di classe, avvia i listener sulle componenti
     * @param parent oggetto TreeApplet per i riferimenti alle componenti di tutte le GUI
     */
    public MainGUIActionHandler(TreeApplet parent){
        base = parent;

        base.mainPanel.treeConstructionBt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
               treeConstructionBt_clicked();
            }
        });
        base.mainPanel.startPredictionBt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                startPredictionBt_clicked();
            }
        });
        base.mainPanel.continueBt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                continueBt_clicked();
            }
        });
        base.mainPanel.dbRadio.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                dbRadio_selected();
            }
        });
        base.mainPanel.fileRadio.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                fileRadio_selected();
            }
        });
        base.mainPanel.fileChooserBt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                fileChooserBt_clicked();
            }
        });
        base.mainPanel.fileSaverBt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                fileSaverBt_clicked();
            }
        });
        base.mainPanel.allRulesBt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                allRulesBt_clicked();
            }
        });
        base.mainPanel.showTreeGraphBt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                showTreeGraphBt_clicked();
            }
        });
        base.mainPanel.exportGraphBt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                exportGraphBt_clicked();
            }
        });
    }


    /**
     * Avvia la construzione dell'albero (apprendendolo dal db o deserializzandolo dal file)
     */
     void treeConstructionBt_clicked(){
         try{
            if(base.mainPanel.dbRadio.isSelected()){
                base.out.writeObject(new Integer(1));
                base.out.writeObject(base.mainPanel.dbList.getSelectedItem());
            }
            else{// fileRadio.isSelected()
                base.out.writeObject(new Integer(3));

                File treeFile = new File(base.mainPanel.fc.getSelectedFile().getAbsolutePath());
                byte[] treeByte = new byte[(int)treeFile.length()];
                InputStream is = new BufferedInputStream( new FileInputStream(treeFile) );
                is.read(treeByte);
                is.close();
                base.out.writeObject( new Integer(treeByte.length) );
                base.out.write(treeByte);
                base.out.flush();
            }
           
            Object handShakeRC = null;
            if ( (handShakeRC = base.in.readObject()) instanceof Exception)
                throw (Exception)handShakeRC;

            if (base.graphPanel != null)
                base.graphPanel = null;
            base.mainPanel.startPredictionBt.setEnabled(true);
            base.mainPanel.fileSaverBt.setEnabled(true);
            base.mainPanel.allRulesBt.setEnabled(true);
            base.mainPanel.showTreeGraphBt.setEnabled(true);
            base.mainPanel.exportGraphBt.setEnabled(true);
            if(!base.mainPanel.rulesLb.getText().equals(""))
                base.mainPanel.rulesLb.setEnabled(false);
            base.mainPanel.msgAreaTxt.setText("Tree learned");
            
         }
         catch(Exception e){
            JOptionPane.showMessageDialog(base.cp, e.getClass().getName(), "Error", JOptionPane.ERROR_MESSAGE);
            base.destroy();
         }
    }


     /**
      * Inizia il processo di prediction guidato dall'utente
      */
     void startPredictionBt_clicked(){
        base.utility.setEnabling(base.mainPanel.cpTree, false); // disabilita il panel relativo all'apprendimento dell'albero
        Object readed = null;
        try{
            base.utility.clearQueryRadio();
            base.mainPanel.rulesLb.setVisible(false);
            base.mainPanel.rulesLb.setText("IF ");
            base.mainPanel.rulesLb.setEnabled(true);
            base.mainPanel.allRulesBt.setEnabled(false);
            base.mainPanel.showTreeGraphBt.setEnabled(false);
            base.mainPanel.exportGraphBt.setEnabled(false);
            base.out.writeObject(new Integer(4));
            readed = base.in.readObject();
            if(readed instanceof Exception)
                throw (Exception)readed;
            if(readed instanceof String){ // readed == Transmitting class value ...
                String classValue = (String)base.in.readObject();
                JOptionPane.showMessageDialog(base.cp, classValue);
                base.mainPanel.continueBt.setEnabled(false);
                base.mainPanel.allRulesBt.setEnabled(true);
                base.mainPanel.showTreeGraphBt.setEnabled(true);
                base.mainPanel.exportGraphBt.setEnabled(true);
                base.mainPanel.msgAreaTxt.setText(classValue);
                base.utility.setEnabling(base.mainPanel.cpTree, true);
            }
            else{ //readed instanceof String[]
                base.mainPanel.continueBt.setEnabled(true);
                base.utility.addQueryRadio( (String[])readed );
            }
            base.mainPanel.startPredictionBt.setEnabled(false);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(base.cp, e.getClass().getName(), "Error", JOptionPane.ERROR_MESSAGE);
            base.destroy();
        }
    }


     /**
      * Avanza nel processo di prediction
      */
     void continueBt_clicked(){
        Object readed = null;
        try{
            Iterator it = base.mainPanel.queryRadioList.iterator();
            int numberChild = 0;
            JRadioButton tmp = null;
            while(it.hasNext()){
                tmp = ((JRadioButton)it.next());
                if( tmp.isSelected() ){
                    base.utility.appendRules("(" + tmp.getText().substring(3) + ")");
                    break;
                }
                numberChild++;
            }
            base.out.writeObject( new Integer(numberChild) );
            readed = base.in.readObject();
            if(readed instanceof String) // readed == Transmitting class value ...
            {
                String classValue = (String)base.in.readObject();
                base.utility.appendRules(" ==> " + classValue.substring(13).toUpperCase());
                JOptionPane.showMessageDialog(base.cp, classValue);
                base.mainPanel.continueBt.setEnabled(false);
                base.mainPanel.allRulesBt.setEnabled(true);
                base.mainPanel.showTreeGraphBt.setEnabled(true);
                base.mainPanel.exportGraphBt.setEnabled(true);
                base.mainPanel.msgAreaTxt.setText(classValue);
                base.utility.setEnabling(base.mainPanel.cpTree, true);
                if(base.mainPanel.dbRadio.isSelected())
                    dbRadio_selected();
                else
                    fileRadio_selected();
                base.utility.setEnabling(base.mainPanel.cpPredictionQuery, false);
            }
            else{
                base.utility.clearQueryRadio();
                base.utility.addQueryRadio( (String[])readed );
                base.utility.appendRules(" AND ");
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(base.cp, e.getClass().getName(), "Error", JOptionPane.ERROR_MESSAGE);
            base.destroy();
        }
    }


     /**
      * Costruisce e gestisce il dialog per scegliere il file dal quale caricare l'albero
      * (file cioè da inviare al server)
      */
     void fileChooserBt_clicked(){
        base.mainPanel.fc.addChoosableFileFilter(new Util.TreeFileFilter("dat"));

        int retval = base.mainPanel.fc.showDialog(base.cp, "Select tree file..");
        if(retval == JFileChooser.APPROVE_OPTION){
            JOptionPane.showMessageDialog(
                base.cp, "You chose this tree file: " +
                base.mainPanel.fc.getSelectedFile().getAbsolutePath()
            );
            base.mainPanel.pathFileLb.setText(base.mainPanel.fc.getSelectedFile().getName());
            base.mainPanel.treeConstructionBt.setEnabled(true);
	}
        else
            JOptionPane.showMessageDialog(base.cp, "No file was chosen.");
    }

     /**
     * Costruisce e gestisce il dialog per scegliere il file sul quale salvare l'albero
     */
     void fileSaverBt_clicked(){
        boolean toSave = false;
        base.mainPanel.fs.addChoosableFileFilter(new Util.TreeFileFilter("dat"));

        int retval = base.mainPanel.fs.showDialog(base.cp, "Save as name..");
        if(retval == JFileChooser.APPROVE_OPTION){
            String pathFile = base.mainPanel.fs.getSelectedFile().getAbsolutePath();
            if( !pathFile.contains(".dat") )
                pathFile += ".dat";
            File saveFile = new File(pathFile);
            if( !saveFile.exists() )
                toSave = true;
            else
                if( JOptionPane.showConfirmDialog(base, "overwrite?") == JOptionPane.YES_OPTION )
                    toSave = true;
            if(toSave){
                try{
                    base.out.writeObject(new Integer(2));
                    OutputStream outFile = new BufferedOutputStream( new FileOutputStream(saveFile) );
                    byte buf[] = new byte[((Integer)base.in.readObject()).intValue()]; //alloca un array di byte della dimensione dell'array che si dovrà ricevere dal server
                    base.in.readFully(buf);
                    // scrivo l'albero localmente su un file (cioè che risiede sul client)
                    outFile.write(buf);
                    outFile.close();
                    JOptionPane.showMessageDialog( base.cp, "Tree saved to file: " + pathFile );
                }
                catch (Exception e){
                    JOptionPane.showMessageDialog(base.cp, "Error occured: tree is not saved.");
                    return;
                }
            }
        }
        if(!toSave)
            JOptionPane.showMessageDialog( base.cp, "Tree not saved", "Warning", JOptionPane.WARNING_MESSAGE);
    }


    /**
     * Stampa tutte le regole del DecisionTree
     */
    void allRulesBt_clicked(){
        try{
            base.out.writeObject(new Integer(5));

            StringBuilder work = new StringBuilder( (String)base.in.readObject() );
            for(int i=1; i<work.length(); i++){
                if( work.substring(i-1, i).equals("\n") )
                    work.replace(i-1, i, "<br>");
            }
            String htmlRules = "<html>" + work + "</html>";
            base.mainPanel.msgAreaTxt.setText( htmlRules );
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(base.cp, e.getClass().getName(), "Error", JOptionPane.ERROR_MESSAGE);
            base.destroy();
        }
    }


    /**
    * Quando è selezionato il radio button relativo all'apprendimento da db
    */
    void dbRadio_selected(){
        base.mainPanel.fileNameLb.setEnabled(false);
        base.mainPanel.fileChooserBt.setEnabled(false);
        base.mainPanel.dbLb.setEnabled(true);
        base.mainPanel.dbList.setEnabled(true);
        base.mainPanel.pathFileLb.setEnabled(false);
        base.mainPanel.treeConstructionBt.setEnabled(true);
    }

    /**
    * Quando è selezionato il radio button relativo al caricamento da file
    */
    void fileRadio_selected(){
        base.mainPanel.treeConstructionBt.setEnabled(false);
        base.mainPanel.dbLb.setEnabled(false);
        base.mainPanel.dbList.setEnabled(false);
        base.mainPanel.fileNameLb.setEnabled(true);
        base.mainPanel.fileChooserBt.setEnabled(true);
        base.mainPanel.pathFileLb.setEnabled(true);
        if(!base.mainPanel.pathFileLb.getText().equals("[none]"))
            base.mainPanel.treeConstructionBt.setEnabled(true);
    }


    /**
     * Per visualizzare il grafo disegnato
     */
    void showTreeGraphBt_clicked(){
        try{
            if(base.graphPanel == null){
                base.out.writeObject(new Integer(6));
                Object readed = base.in.readObject();
                base.graphPanel = new TreeGraph( (MatrixCell[])readed );
            }
            base.graphPanel.setVisible(true);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(base.cp, e.getClass().getName() + "\nTree's graph not drawed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Per esportare il grafo disegnato come file immagine (.png)
     */
    void exportGraphBt_clicked(){
        boolean toSave = false;
        try{
            if(base.graphPanel == null){
                base.out.writeObject(new Integer(6));
                base.graphPanel = new TreeGraph( (MatrixCell[])base.in.readObject() );
            }
            JFileChooser fs = new JFileChooser();
            fs.addChoosableFileFilter(new Util.TreeFileFilter("png"));
            int retval = fs.showDialog(base.cp, "Save as name..");
            if(retval == JFileChooser.APPROVE_OPTION){
                String pathFile = fs.getSelectedFile().getAbsolutePath();
                if( !pathFile.contains(".png") )
                    pathFile += ".png";
                File saveFile = new File(pathFile);
                if( !saveFile.exists() )
                    toSave = true;
                else
                    if( JOptionPane.showConfirmDialog(base, "overwrite?") == JOptionPane.YES_OPTION )
                        toSave = true;
                if(toSave){
                    BufferedImage img = base.graphPanel.graph.getImage(null, 10);
                    ImageIO.write(img, "png", saveFile);
                    JOptionPane.showMessageDialog( base.cp, "Tree's graph saved to file: " + pathFile );
                }
                if(!toSave)
                    JOptionPane.showMessageDialog( base.cp, "Tree's graph not exported", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(base.cp, e.getClass().getName() + "\nTree's graph not exported", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
