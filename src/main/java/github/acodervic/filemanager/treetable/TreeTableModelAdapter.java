package github.acodervic.filemanager.treetable;

/*
 * %W% %E%
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import github.acodervic.filemanager.gui.MainFrame;
import github.acodervic.filemanager.model.RESWallper;

/**
 * This is a wrapper class takes a TreeTableModel and implements
 * the table model interface. The implementation is trivial, with
 * all of the event dispatching support provided by the superclass:
 * the AbstractTableModel.
 *
 * @version %I% %G%
 *
 * @author Philip Milne
 * @author Scott Violet
 */

public class TreeTableModelAdapter extends AbstractTableModel {
    JTree tree;
    JTreeTable treeTable;
    TreeTableModel treeTableModel;

    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree,JTreeTable treeTable) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;
        this.treeTable=treeTable;
        MainFrame mainFrame = getTreeTable().getFileTableTab().getTabsPanel().getMainFrame();
        //DefaultTreeModel
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            // Don't use fireTableRowsInserted() here;
            // the selection model would get updated twice.
            public void treeExpanded(TreeExpansionEvent event) {
                fireTableDataChanged();//当节点展开的时候重新刷新表格
                //当前展开的treePath
                TreePath path = event.getPath();
                RESWallper  targetDir = (RESWallper)path.getLastPathComponent();
            }
            public void treeCollapsed(TreeExpansionEvent event) {
                fireTableDataChanged();//当节点展开的时候重新刷新表格
                TreePath path = event.getPath();
                RESWallper  targetDir = (RESWallper)path.getLastPathComponent();
                //取消监听目录更改
                mainFrame.unRegisterWatchDir(targetDir);
            }
        });
        
    }

    // Wrappers, implementing TableModel interface.

    @Override//TableModel.getColumnCount
    public int getColumnCount() {
        return treeTableModel.getColumnCount();
    }

    @Override //TableModel.getColumnName
    public String getColumnName(int column) {
        return treeTableModel.getColumnName(column);
    }


    @Override   //AbstractTableModel.getColumnClass
    public Class getColumnClass(int column) {
        return treeTableModel.getColumnClass(column);
    }


    @Override   //TableModel.getRowCount
    public int getRowCount() {
        return tree.getRowCount();
    }

    protected Object nodeForRow(int row) {
        TreePath treePath = getTreePathForRow(row);
        if (treePath!=null) {
            return treePath.getLastPathComponent();
        }
        return null;
    }

    public TreePath getTreePathForRow(int row) {
        return tree.getPathForRow(row);
    }

    @Override   //TableModel.getValueAt
    public Object getValueAt(int row, int column) {
        Object nodeForRow = nodeForRow(row);
        if (nodeForRow==null) {
            return null;
        }
        return treeTableModel.getValueAt(nodeForRow, column);
    }

    @Override   //AbstractTableModel.isCellEditable
    public boolean isCellEditable(int row, int column) {
        Object nodeForRow = nodeForRow(row);
        if (nodeForRow==null) {
            return false;
        }
        return treeTableModel.isCellEditable(nodeForRow, column);
    }

    public Object getRowValueAt(int row) {
        return nodeForRow(row);
    }

    @Override   //AbstractTableModel.setValueAt
    public void setValueAt(Object value, int row, int column) {
        Object nodeForRow = nodeForRow(row);
        if (nodeForRow!=null) {
            treeTableModel.setValueAt(value, nodeForRow, column);
        }
    }

    /**
     * @param treeTableModel the treeTableModel to set
     */
    public void setTreeTableModel(TreeTableModel treeTableModel) {
        this.treeTableModel = treeTableModel;
    }

    /**
     * @return the treeTableModel
     */
    public TreeTableModel getTreeTableModel() {
        return treeTableModel;
    }

    /**
     * @return the tree
     */
    public JTree getTree() {
        return tree;
    }

 


    /**
     * @return the treeTable
     */
    public JTreeTable getTreeTable() {
        return treeTable;
    }
}
