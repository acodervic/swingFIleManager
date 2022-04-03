package github.acodervic.filemanager.treetable.renderer;

import java.awt.Component;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import github.acodervic.filemanager.treetable.Column;
import github.acodervic.filemanager.treetable.Columns;
import github.acodervic.mod.data.TimeUtil;
import github.acodervic.mod.swing.tree.filter.TipJPanel;

public class TableDateColumnRander extends DefaultTableCellRenderer {
    
     public TableDateColumnRander() {
         
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int columnIndex) {
                Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, columnIndex);

                //只对修改/创建时间做处理
                if (value instanceof Date date ){
                    long dateValue = System.currentTimeMillis()-date.getTime();
                    String betweenPrintMaxTime = TimeUtil.getBetweenPrintMaxTime(dateValue);
                    if (betweenPrintMaxTime.length()>0) {
                        setText(betweenPrintMaxTime+"前");
                    }else{
                        String betweenPrintTime = TimeUtil.getBetweenPrintTime(dateValue);
                        setText(betweenPrintTime.trim()+"前");
                    }
                } 
                return tableCellRendererComponent;
    }
}
