package github.acodervic.filemanager.gui.popmenus;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;

import github.acodervic.filemanager.gui.FileTableTab;
import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.treetable.FileSystemModel;
import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.filemanager.treetable.TreeTableModelAdapter;

public class Refresh extends MyPopMenuItem {

 

    @Override
    public List<MyPopMenuItem> getSubItems() {
         return null;
    }

    @Override
    public String getName() {
         return "刷新";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.update);
    }

    @Override
    public String getTip() {
         return null;
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        FileTableTab fileTableTab = mainPanel.getCenterTabsPanel().getNowSeletedTabPanel().get();
        fileTableTab.getNowShowingTable().updateAllTable();
        //fileTableTab.getNowShowingTable().refreshUi();

    }

    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        
        return true;
    }
    
}
