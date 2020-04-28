import java.awt.Container;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;

public class LayerChanger implements WindowListener {
    Connection con = null;

    public static void main(String[] args) {
        new LayerChanger();
    }

    public LayerChanger() {
        Connection con  = null;
        do {
            JTextField sField = new JTextField(10);
            sField.setText("gv-srv-w00118:1521/TTSIB");
            JTextField uField = new JTextField(10);
            JPasswordField pField = new JPasswordField(10);


            JPanel myPanel = new JPanel();
            myPanel.setLayout(new GridLayout(3, 2));
            myPanel.add(new JLabel("Datenbank"));
            myPanel.add(sField);
            myPanel.add(new JLabel("Benutzer"));
            myPanel.add(uField);
            myPanel.add(new JLabel("Passwort"));
            myPanel.add(pField);

            int result = JOptionPane.showConfirmDialog(null, myPanel, "TTSIB-Login", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String server = sField.getText();
                String user = uField.getText();
                String passwort = new String(pField.getPassword());

                con = connect(server, user, passwort);

            } else {
                System.exit(0);
            }
        } while (con == null);

        JFrame window = new JFrame("LayerChanger");
        window.addWindowListener(this);

        Container panel = window.getContentPane();

        try {
            Statement st = con.createStatement();
            ResultSet r = st.executeQuery(
                    "SELECT STAND, WERT FROM SYSADM5.PTOPT_TEMPL WHERE PTOPT_ID = (SELECT ID FROM SYSADM5.PTOPT WHERE OPT = 'WS.strassendaten')");
            r.next();
            String stand = r.getString(1);
            String xml = r.getString(2);
            System.out.print(xml);
            r.close();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(xml.toString().getBytes("UTF-8"));
            Document doc = builder.parse(input);

            Element root = doc.getDocumentElement();
            NodeList layers = root.getChildNodes();

            JPanel scrollPane = new JPanel();
            JScrollPane scroll = new JScrollPane(scrollPane);
            panel.add(scroll);

            scrollPane.setLayout(new GridLayout(layers.getLength(), 1));
            for (int i = 0; i < layers.getLength(); i++) {
                Node layer = layers.item(i);
                JPanel layerPanel = new JPanel();
                NamedNodeMap attributes = layer.getAttributes();

                int count = attributes.getLength();
                if (layer.getTextContent().length() > 0)
                    count++;

                layerPanel.setLayout(new GridLayout(count, 2));

                for (int j = 0; j < attributes.getLength(); j++) {
                    Node attribut = attributes.item(j);
                    layerPanel.add(new JLabel(attribut.getNodeName()));
                    JTextField box = new JTextField();
                    box.setText(attribut.getNodeValue());
                    layerPanel.add(box);
                }

                scrollPane.add(layerPanel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setVisible(true);
    }

    private Connection connect(String server, String user, String passwort) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.con = DriverManager.getConnection("jdbc:oracle:thin:@//"+server, user, passwort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.con;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowClosing(WindowEvent e) {
        // TODO Auto-generated method stub
        try {
            this.con.close();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }
}