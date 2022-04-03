package github.acodervic.filemanager.gui;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 * 下方用于显示信息的面板
 */
public class MainBottomInfoPanel extends MainFramePanel {
    JPanel leftPanel;
    JPanel centerPanel;
    JPanel rightPanel;
    JLabel centerInfoLable;
    JLabel rightInfoLable;

    public MainBottomInfoPanel(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public void initGui() {
        setLayout(new MigLayout());
        add(getLeftPanel(),"width 33%");
        add(getCenterPanel(),"width 33%");
        add(getRightPanel(),"width 33%");
    }


    /**
     * @return the rightInfoLable
     */
    public synchronized JLabel getRightInfoLable() {
        if (isNull(rightInfoLable)) {
            rightInfoLable=new JLabel();
        }
        return rightInfoLable;
    }

    /**
     * @return the leftPanel
     */
    public synchronized JPanel getLeftPanel() {
        if (isNull(leftPanel)) {
            leftPanel=new JPanel(new MigLayout());
            JButton newIconToggleButtonName = newIconToggleButtonName(Icons.siteBar);
            onClick(newIconToggleButtonName, e  ->{
                MainPanel mp=   mainFrame.getMainPanel(); 
               mp.resetLayout(!mp.getHasSitebar());
            });
            leftPanel.add(newIconToggleButtonName);
        }
        return leftPanel;
    }

    /**
     * @return the centerPanel
     */
    public synchronized JPanel getCenterPanel() {
        if (isNull(centerPanel)) {
            centerPanel=new JPanel();
            centerPanel.add(getCenterInfoLable());
        }
        return centerPanel;
    }


    /**
     * @return the rightPanel
     */
    public synchronized JPanel getRightPanel() {
        if (isNull(rightPanel)) {
            rightPanel=new JPanel();
            rightPanel.add(getRightInfoLable());

        }
        return rightPanel;
    }
    /**
     * @return the infoLable
     */
    public synchronized JLabel getCenterInfoLable() {
        if (isNull(centerInfoLable)) {
            centerInfoLable = new JLabel(" ");
        }
        return centerInfoLable;
    }

    

    public void setInfText(String text) {
        getCenterInfoLable().setText(text);
    }

}
