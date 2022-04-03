package github.acodervic.filemanager.gui.popmenus;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.mod.data.str;
import github.acodervic.mod.swing.MessageBox;

public class SetComment extends MyPopMenuItem {

    @Override
    public List<MyPopMenuItem> getSubItems() {
        return null;
    }

    @Override
    public String getName() {
        return "Set Comment";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.comment);
    }

    @Override
    public String getTip() {
        return "set  file/Dirs comments";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        str comment = str(MessageBox.showInputDialog(mainPanel, "输入", "输入评论", null, 0));
        if (comment.trim().notEmpty()) {
            seletedResWallpers.forEach(res -> {
                res.setUserComment(comment.to_s());
                mainPanel.getMainFrame().nodeChange(res);
            });
        }
    }

    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        if (seletedResWallpers.size()>0&&nowDir.isWritable()) {
            return true;
        }
        return false;
    }

}
