package github.acodervic.filemanager.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.UtilFunInter;

public  abstract class MainFramePanel  extends JPanel  implements  GuiUtil{ 
    MainFrame mainFrame;
    
    public  MainFramePanel(MainFrame mainFrame) {
        this(mainFrame, true,true);
    }


    public  MainFramePanel(MainFrame mainFrame,Boolean autoInitGui) {
        this(mainFrame, true,autoInitGui);
    }


    public  MainFramePanel(MainFrame mainFrame,boolean addBorder,boolean autoInitGui) {
        super();
        this.mainFrame=mainFrame;
        if (addBorder) {
            setBorder(BorderFactory.createLineBorder(Color.gray));
        }
        if (autoInitGui) {
            initGui();
        }
    }
    public abstract void initGui(); 

    /**
     * @return the mainFrame
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }
}
