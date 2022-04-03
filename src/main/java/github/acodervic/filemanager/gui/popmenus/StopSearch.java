package github.acodervic.filemanager.gui.popmenus;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.model.SearchRootRESWallper;
import github.acodervic.mod.data.str;
import github.acodervic.mod.swing.MessageBox;

public class StopSearch   extends MyPopMenuItem {

 

    @Override
    public List<MyPopMenuItem> getSubItems() {
        return null;
    }

    @Override
    public String getName() {
        return "Stop Search";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.stop);
    }

    @Override
    public String getTip() {
        return "stop the search";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers,RESWallper nowDir,MainPanel mainPanel) {
        if (nowDir instanceof SearchRootRESWallper  sres && sres.inSearching() ) {
            sres.stopSearch();
        }
    }


    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
         return nowDir instanceof SearchRootRESWallper  sres && sres.inSearching();
    }
 
    
}
