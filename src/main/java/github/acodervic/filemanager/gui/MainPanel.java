package github.acodervic.filemanager.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

 
public class MainPanel extends MainFramePanel {
 
    MainLeftPanel leftPanel;
    MainCenterTabsPanel centerTabsPanel;
    MainBottomInfoPanel bottomInfoPanel;
    MainTopToolsPanel topToolsPanel;
    Boolean hasSitebar=true;


    public MainPanel(MainFrame mainFrame) {
        super(mainFrame);
    }

    public void initGui() {
        setLayout(new BorderLayout()); 
        resetLayout(true);
    }


    /**
     * @return the hasSitebar
     */
    public Boolean getHasSitebar() {
        return hasSitebar;
    }
    public void resetLayout(Boolean hasSitebar) {
        this.hasSitebar=hasSitebar;
        removeAll();
        JPanel centerPanel=new JPanel(new MigLayout());
        if (this.hasSitebar) {
            centerPanel.add(getLeftPanel(),"width 250:250:250,height 100%");
            centerPanel.add(getCenterTabsPanel(),"width 100%,height 100%");
        }else{
            centerPanel.add(getCenterTabsPanel(),"width 100%,height 100%");
        }
        add(centerPanel,BorderLayout.CENTER);
        add(getBottomInfoPanel(),BorderLayout.SOUTH);
        add(getTopToolsPanel(),BorderLayout.NORTH);
        updateUI();
    }

    /**
     * @return the leftPanel
     */
    public synchronized MainLeftPanel getLeftPanel() {
        if (isNull(leftPanel)) {
            leftPanel=new MainLeftPanel(mainFrame);
            leftPanel.initGui();
        }
        return leftPanel;
    }

    /**
     * @return the centerTabsPanel
     */
    public synchronized MainCenterTabsPanel getCenterTabsPanel() {
        if (isNull(centerTabsPanel)) {
            centerTabsPanel=new MainCenterTabsPanel(mainFrame);
        }
        return centerTabsPanel;
    }

    /**
     * @return the bottomInfoPanel
     */
    public synchronized MainBottomInfoPanel getBottomInfoPanel() {
        if (isNull(bottomInfoPanel)) {
            bottomInfoPanel=new MainBottomInfoPanel(mainFrame);
        }
        return bottomInfoPanel;
    }

    /**
     * @return the topToolsPanel
     */
    public synchronized MainTopToolsPanel getTopToolsPanel() {
        if (isNull(topToolsPanel)) {
            topToolsPanel=new MainTopToolsPanel(mainFrame);
        }
        return topToolsPanel;
    }
    
}
