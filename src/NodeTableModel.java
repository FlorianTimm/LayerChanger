import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class NodeTableModel implements TableModel {
    private NodeList layers;
    final private String[] columns = { "Nr", "T", "Sichtbar", "Name", "URL", "Layer", "Transparenz", "MIME" };

    public NodeTableModel(NodeList layer) {
        this.layers = layer;
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

    @Override
    public int getRowCount() {
        return this.layers.getLength();
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];

    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 1:
            case 2:
            case 6:
                return Boolean.class;
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        System.out.println("isCellEditable: " + rowIndex + " " + columnIndex);
        if (columnIndex == 0) {
            System.out.println("no");
            return false;
        } else if (columnIndex == 1 || columnIndex == 2) {
            System.out.println("yes");
            return true;
        }

        try {
            if (this.findAttribut(rowIndex, "wmsUrl").getNodeValue().equals("")) {
                System.out.println("no");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("yes");
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            switch (columnIndex) {
                case 0:
                    return this.findAttribut(rowIndex, "position").getNodeValue();
                case 1:
                    return this.findAttribut(rowIndex, "baseLayer").getNodeValue().equals("true");
                case 2:
                    return this.findAttribut(rowIndex, "visible").getNodeValue().equals("true");
                case 3:
                    return this.findAttribut(rowIndex, "title").getNodeValue();
                case 4:
                    return this.findAttribut(rowIndex, "wmsUrl").getNodeValue();
                case 5:
                    return this.findAttribut(rowIndex, "name").getNodeValue();
                case 6:
                    return this.findAttribut(rowIndex, "wmsTransparent").getNodeValue().equals("true");
                case 7:
                    return this.findAttribut(rowIndex, "wmsMimeType").getNodeValue();
                default:
                    return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            if (columnIndex != 2 && columnIndex != 1 && this.findAttribut(rowIndex, "wmsUrl").getNodeValue().equals(""))
                return;

            if (aValue.equals(""))
                return;

            switch (columnIndex) {
                case 1:
                    this.findAttribut(rowIndex, "baseLayer").setNodeValue(aValue.toString());
                    break;
                case 2:
                    this.findAttribut(rowIndex, "visible").setNodeValue(aValue.toString());
                    break;
                case 3:
                    this.findAttribut(rowIndex, "title").setNodeValue(aValue.toString());
                    break;
                case 4:
                    this.findAttribut(rowIndex, "wmsUrl").setNodeValue(aValue.toString());
                    break;
                case 5:
                    this.findAttribut(rowIndex, "name").setNodeValue(aValue.toString());
                    break;
                case 6:
                    this.findAttribut(rowIndex, "wmsTransparent").setNodeValue(aValue.toString());
                    break;
                case 7:
                    this.findAttribut(rowIndex, "wmsMimeType").setNodeValue(aValue.toString());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
    }
}