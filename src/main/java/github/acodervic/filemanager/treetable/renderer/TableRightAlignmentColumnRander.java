package github.acodervic.filemanager.treetable.renderer;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 一个支持列右对齐的列渲染器
 */
public class TableRightAlignmentColumnRander extends DefaultTableCellRenderer {
    
    public  TableRightAlignmentColumnRander() {
        setHorizontalAlignment(JLabel.RIGHT);
    }
}
