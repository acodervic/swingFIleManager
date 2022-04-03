package github.acodervic.filemanager.gui.popmenus;

import java.util.List;
import javax.swing.Icon;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;

public class OpenInNewTab extends MyPopMenuItem {

    @Override
    public List<MyPopMenuItem> getSubItems() {
        return null;
    }

    @Override
    public String getName() {
        return "Open in new Tab/s";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.tab);
    }

    @Override
    public String getTip() {
        return "在新的tab中打开";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        seletedResWallpers.removeIf(res -> {
            return !res.isDir();
        });
        for (int i = 0; i < seletedResWallpers.size(); i++) {
            RESWallper resWallper = seletedResWallpers.get(i);
            mainPanel.getCenterTabsPanel().addFilesTableTab(resWallper, true);
        }
    }

    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        seletedResWallpers.removeIf(res -> {
            return !res.isDir();
        });
        return seletedResWallpers.size() > 0;
    }

}
