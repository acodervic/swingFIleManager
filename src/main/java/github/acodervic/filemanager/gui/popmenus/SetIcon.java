package github.acodervic.filemanager.gui.popmenus;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.mod.data.str;
import github.acodervic.mod.swing.MessageBox;

public class SetIcon extends MyPopMenuItem {

    @Override
    public List<MyPopMenuItem> getSubItems() {
        return null;
    }

    @Override
    public String getName() {
        return "Set Icon";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.icon);
    }

    @Override
    public String getTip() {
        return "set  file/dirs icon";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        str iconFIlePath = str(MessageBox.showInputDialog(mainPanel, "输入", "输入图标文件", null, 0));
        if (iconFIlePath.trim().notEmpty()) {
            seletedResWallpers.forEach(res -> {
                res.setUserIcon(iconFIlePath.to_s());
                mainPanel.getMainFrame().nodeChange(res);
            });
        }
    }

    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        if (!isLinux()) {
            return false;
        }
        if (seletedResWallpers.size()>0&& nowDir.isWritable()) {
            return true;
        }
        return false;
    }

}
