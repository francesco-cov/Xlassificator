package classificator.frontend;

import classificator.backend.MainGUIActionHandler;
import classificator.TreeApplet;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Classe che costruisce la GUI di connessione, ConnectGUI
 */
public class ConnectGui extends JPanel{

    private TreeApplet base = null;

    private JButton goBt = new JButton("Go!");
    private JTextField ipTf = new JTextField(12);
    private JTextField portTf = new JTextField(4);
    private JLabel ipLb = new JLabel("IP Address:");
    private JLabel portLb = new JLabel("Port:");

    /**
     * Costruttore di classe
     * @param parent oggetto TreeApplet per i riferimenti alle componenti di tutte le GUI
     * @throws Exception
     */
    public ConnectGui(TreeApplet parent) throws Exception{
        base = parent;

        base.setSize(400, 80); // utile solo se si esegue l'applet tramite un wrapper (l'applet in una pagina web non ne viene influenzata)
        this.setBorder(BorderFactory.createTitledBorder("Connect to a classification server.."));
        ipTf.setText("localhost");
        portTf.setText("8080");

        this.add(ipLb);
        this.add(ipTf);
        this.add(portLb);
        this.add(portTf);
        this.add(goBt);

        goBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    base.srvConnection.connect(ipTf.getText(), new Integer(portTf.getText()));
                    base.cp.remove( base.connectPanel );
                    base.cp.setLayout( new BorderLayout() );
                    base.cp.add( base.mainPanel = new MainGui(base) );
                    base.utility.fillDbList(); // per riempire la combobox

                    MainGUIActionHandler mainActionHandler = new MainGUIActionHandler(base);
                }
                catch (Exception ex){
                    JOptionPane.showMessageDialog(base.cp, ex.getClass().getName() + "\nConnection error!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


}
