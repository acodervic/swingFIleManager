package github.acodervic.filemanager.treetable;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;

// 
    // treetable tree节点渲染器
    //

    public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

        JTreeTable treeTable;
        protected int visibleRow;
       
        public TreeTableCellRenderer(TreeModel model,JTreeTable treeTable) { 
            super(model); 
            this.treeTable=treeTable;
            if (model instanceof FileSystemModel fsm) {
                fsm.setTree( this);
            }
            MyTreeUI treeUi = new MyTreeUI();
            setUI(treeUi);
            treeUi.setRowHeight(treeTable.getRowHeight());
            
        }
    
        public void setBounds(int x, int y, int w, int h) {
            super.setBounds(x, 0, w, treeTable.getHeight());
        }
    
        public void paint(Graphics g) {
            g.translate(0, -visibleRow * getRowHeight()-1);
            super.paint(g);
        }
    
        public Component getTableCellRendererComponent(JTable table,
                                   Object value,
                                   boolean isSelected,
                                   boolean hasFocus,
                                   int row, int column) {
            if(isSelected)
            setBackground(table.getSelectionBackground());
            else
            setBackground(table.getBackground());
           
            visibleRow = row;
            return this;
        }

        @Override
        public void setModel(TreeModel newModel) {
            super.setModel(newModel);
            if (newModel instanceof FileSystemModel fsm) {
                fsm.setTree(this);
            }
        }

        
    }