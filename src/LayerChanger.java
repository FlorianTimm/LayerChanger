import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LayerChanger extends JFrame implements WindowListener, ActionListener {
    private static final long serialVersionUID = -969444163252872331L;
    Connection con = null;
    private java.sql.Date stand;
    private Document doc;
    private NodeList layers;
    private NodeTable table;

    public static void main(String[] args) {
        new LayerChanger();
    }

    public LayerChanger() {
        super("LayerChanger");
        Connection con = null;
        do {
            JTextField sField = new JTextField(10);
            sField.setText("gv-srv-w00118:1521/TTSIB");
            JTextField uField = new JTextField(10);
            uField.setText("SYSADM5");
            JPasswordField pField = new JPasswordField(10);

            JPanel myPanel = new JPanel();
            myPanel.setLayout(new GridLayout(3, 2));
            myPanel.add(new JLabel("Datenbank"));
            myPanel.add(sField);
            myPanel.add(new JLabel("Benutzer"));
            myPanel.add(uField);
            myPanel.add(new JLabel("Passwort"));
            myPanel.add(pField);

            pField.grabFocus();

            int result = JOptionPane.showConfirmDialog(this, myPanel, "TTSIB-Login", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String server = sField.getText();
                String user = uField.getText();
                String passwort = new String(pField.getPassword());

                con = connect(server, user, passwort);

            } else {
                System.exit(0);
            }
        } while (con == null);

        this.addWindowListener(this);

        Container panel = this.getContentPane();
        panel.setLayout(new BorderLayout());

        try {
            NodeList layers = this.getValuesFromDB();
            panel.add(new JLabel("Stand: " + this.stand), BorderLayout.NORTH);
            this.table = new NodeTable(new NodeTableModel(layers));
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            JButton button = new JButton("Speichern");
            button.addActionListener(this);
            button.setActionCommand("speichern");
            panel.add(button, BorderLayout.SOUTH);

            JMenuBar menu = new JMenuBar();
            this.setJMenuBar(menu);

            JMenu datei = new JMenu("Datei");
            menu.add(datei);

            JMenuItem neu = new JMenuItem("duplizieren");
            neu.addActionListener(this);
            neu.setActionCommand("neu");
            datei.add(neu);

            JMenuItem loeschen = new JMenuItem("löschen");
            loeschen.addActionListener(this);
            loeschen.setActionCommand("loeschen");
            datei.add(loeschen);

            datei.add(new JSeparator());

            JMenuItem ende = new JMenuItem("Ende");
            ende.addActionListener(this);
            ende.setActionCommand("ende");
            datei.add(ende);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(new Dimension(1000, 700));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private NodeList getValuesFromDB() throws Exception {
        Statement st = this.con.createStatement();

        ResultSet r = st.executeQuery(
                "SELECT STAND, WERT FROM SYSADM5.PTOPT_TEMPL WHERE PTOPT_ID = (SELECT ID FROM SYSADM5.PTOPT WHERE OPT = 'WS.strassendaten')");
        r.next();
        this.stand = r.getDate(1);
        String xml = r.getString(2);
        System.out.print(xml);
        r.close();

        logXML(stand, xml, "before");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(xml.toString().getBytes("ISO-8859-1"));
        this.doc = builder.parse(input);

        Element root = doc.getDocumentElement();
        this.layers = root.getChildNodes();
        return this.layers;
    }

    private void logXML(java.sql.Date stand, String xml, String suffix) throws FileNotFoundException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        Date date = new Date();
        PrintWriter out = new PrintWriter(dateFormat.format(date) + "_" + suffix + ".txt");
        out.println(stand);
        out.println(xml);
        out.close();
    }

    private Connection connect(String server, String user, String passwort) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.con = DriverManager.getConnection("jdbc:oracle:thin:@//" + server, user, passwort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.con;
    }

    private void ende() {
        try {
            this.con.close();
            System.out.println("Verbindung geschlossen");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        ende();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "speichern":
                speichern();
                break;
            case "neu":
                neu();
                break;
            case "loeschen":
                loeschen();
                break;
            case "ende":
                ende();
                break;
        }
    }

    private void loeschen() {
        try {
            int selection = this.table.getSelectedRow();
            if (selection < 0)
                return;

            Node selected = layers.item(selection);

            if (findAttribut(selection, "wmsUrl").getNodeValue().equals("")) {
                JOptionPane.showMessageDialog(this, "Es können nur WMS-Dienste kopiert werden!");
                return;
            }

            for (int i = selection; i < this.layers.getLength(); i++) {
                Node layer = layers.item(i);
                NamedNodeMap map = layer.getAttributes();
                for (int j = 0; j < map.getLength(); j++) {
                    if (map.item(j).getNodeName().equals("position")) {
                        map.item(j).setNodeValue(Integer.toString(Integer.parseInt(map.item(j).getNodeValue()) - 1));
                        continue;
                    }
                }
            }

            doc.getDocumentElement().removeChild(selected);

            this.layers = this.doc.getDocumentElement().getChildNodes();
            table.setModel(new NodeTableModel(this.layers));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void neu() {
        try {
            int selection = this.table.getSelectedRow();
            if (selection < 0)
                selection = 0;

            Node selected = layers.item(selection);

            if (findAttribut(selection, "wmsUrl").getNodeValue().equals("")) {
                JOptionPane.showMessageDialog(this, "Es können nur WMS-Dienste kopiert werden!");
                return;
            }

            Node newOne = selected.cloneNode(true);
            for (int i = selection; i < this.layers.getLength(); i++) {
                Node layer = layers.item(i);
                NamedNodeMap map = layer.getAttributes();
                for (int j = 0; j < map.getLength(); j++) {
                    if (map.item(j).getNodeName().equals("position")) {
                        map.item(j).setNodeValue(Integer.toString(Integer.parseInt(map.item(j).getNodeValue()) + 1));
                        continue;
                    }
                }
            }

            doc.getDocumentElement().insertBefore(newOne, layers.item(selection));

            this.layers = this.doc.getDocumentElement().getChildNodes();
            table.setModel(new NodeTableModel(this.layers));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void speichern() {
        // Speichern
        try {
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.INDENT, "no");
            trans.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc.getDocumentElement());

            trans.transform(source, result);
            String xmlString = sw.toString();
            System.out.print(xmlString);

            String alt = stand.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String neu = java.time.LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            java.sql.Date standNeu = java.sql.Date.valueOf(java.time.LocalDate.now());
            if (alt.equals(neu) || stand.after(standNeu)) {
                standNeu = java.sql.Date.valueOf(stand.toLocalDate().plusDays(1));
            }

            logXML(standNeu, xmlString, "after");

            PreparedStatement pst = this.con.prepareStatement(
                    "UPDATE SYSADM5.PTOPT_TEMPL SET STAND = ?, WERT = ? WHERE PTOPT_ID = (SELECT ID FROM SYSADM5.PTOPT WHERE OPT = 'WS.strassendaten')");

            pst.setDate(1, standNeu);
            pst.setString(2, xmlString);
            pst.execute();

            JOptionPane.showMessageDialog(this, "Gespeichert!");
            System.exit(0);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private Node findAttribut(int row, String attribut) throws Exception {
        NamedNodeMap attr = this.layers.item(row).getAttributes();
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.item(i).getNodeName().equals(attribut)) {
                return attr.item(i);
            }
        }
        throw new Exception("");
    }
}