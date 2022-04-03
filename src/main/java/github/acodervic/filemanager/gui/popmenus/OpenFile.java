package github.acodervic.filemanager.gui.popmenus;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;

public class OpenFile  extends MyPopMenuItem {
 

    @Override
    public List<MyPopMenuItem> getSubItems() {
        return null;
    }

    @Override
    public String getName() {
        return "Open";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getTip() {
        return "open file/s";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers,RESWallper nowDir,MainPanel mainPanel) {
        for (int i = 0; i < seletedResWallpers.size(); i++) {
            RESWallper resWallper = seletedResWallpers.get(i);
            if (resWallper.isDir()) {
                //在新tab中打开
                mainPanel.getCenterTabsPanel().addFilesTableTab(resWallper,true);
            }else{
                resWallper.xdgOpen();
            }
        }
    }

    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        seletedResWallpers.removeIf(res  ->{
            return res.isDir(); 
        });
        return seletedResWallpers.size()>0;
    }
    
}
