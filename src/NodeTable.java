
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;


public class NodeTable extends JTable {

    public NodeTable(TableModel tm) {
        super(tm);
    }

    private static final long serialVersionUID = 1L;

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        this.resizeTable();
    }

    public void resizeTable() {
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] cWidth = new int[this.getColumnCount()];

        for (int column = 0; column < this.getColumnCount(); column++) {
            TableColumn tableColumn = this.getColumnModel().getColumn(column);
            cWidth[column] = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            for (int row = 0; row < this.getRowCount(); row++) {
                TableCellRenderer cellRenderer = this.getCellRenderer(row, column);
                Component c = this.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + this.getIntercellSpacing().width;
                cWidth[column] = Math.max(cWidth[column], width);

                // We've exceeded the maximum width, no need to check other rows

                if (cWidth[column] >= maxWidth) {
                    cWidth[column] = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth(cWidth[column]);
        }

    }

}