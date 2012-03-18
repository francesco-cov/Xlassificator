// <applet code=classificator.TreeApplet width=700 height=700>
// </applet>

package classificator;

import classificator.backend.ServerConnection;
import classificator.backend.Util;
import classificator.frontend.ConnectGui;
import classificator.frontend.MainGui;
import classificator.frontend.TreeGraph;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JApplet;
import javax.swing.JOptionPane;

/**
 * Classe che crea la JApplet e ne gestisce il ciclo di vita e le chiamate alle sub-GUI
 */
public class TreeApplet extends JApplet{

    public ObjectOutputStream out = null;
    public ObjectInputStream in = null;
    public ConnectGui connectPanel = null;
    public MainGui mainPanel = null;
    public TreeGraph graphPanel = null;
    public Container cp = getContentPane();
    public ServerConnection srvConnection = new ServerConnection(this);
    public Util utility = new Util(this);
    

    /**
     * Innesca il processo istanziando una gui di connessione
     */
    @Override
    public void init(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        cp.setLayout(new GridBagLayout());
        try{
            cp.add(connectPanel = new ConnectGui(this));
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(cp, e.getClass().getName(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Effettua le operazioni di cleaning in chiusura
     */
    @Override
    public void destroy(){
        try {
            out.writeObject(new Integer(7));
            srvConnection.disconnect();
        }
        catch (IOException e){
            // DO NOTHING
        }
        finally{
            // necessario perchè sennò verrebbe richiamato semplicemente il metodo di init
            // e molti attributi rimarrebbero erroneamente valorizzati
            // condizione che potrebbe accadere al refresh del browser o dopo un' eccezione fatale
            System.exit(0);
        }
    }

}
