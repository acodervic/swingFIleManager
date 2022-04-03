package github.acodervic.filemanager.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.mod.swing.panel.MyJScrollPane;
import net.miginfocom.swing.MigLayout;

public class FileTreeTablePanel extends MyJScrollPane {
    JTreeTable treeTable;

    public FileTreeTablePanel(JTreeTable treeTable) {
        this.treeTable = treeTable;
        this.treeTable.setFileTreeTablePanel(this);
        setViewportView(treeTable);
        // enableScrollAnima();
    }



    /**
     * @return the treeTable
     */
    public JTreeTable getTreeTable() {
        return treeTable;
    }

}
