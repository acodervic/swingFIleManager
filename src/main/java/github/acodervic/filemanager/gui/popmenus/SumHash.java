package github.acodervic.filemanager.gui.popmenus;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;

public class SumHash extends MyPopMenuItem {

    @Override
    public List<MyPopMenuItem> getSubItems() {
         return null;
    }

    @Override
    public String getName() {
        return "calc MD5";
    }

    @Override
    public Icon getIcon() {
         return null;
    }

    @Override
    public String getTip() {
         return null;
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {

    }
    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        seletedResWallpers.removeIf(res  ->{
            return res.isDir();
        });

        return seletedResWallpers.size()>0;
    }
    
}
