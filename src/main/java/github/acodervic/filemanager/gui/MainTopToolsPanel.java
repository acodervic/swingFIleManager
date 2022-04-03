package github.acodervic.filemanager.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.commons.vfs2.FileSystemException;
import org.oxbow.swingbits.dialog.task.TaskDialogs;

import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.Opt;
import net.miginfocom.swing.MigLayout;

/**
 * 上方显示工具栏的panel
 */
public class MainTopToolsPanel  extends MainFramePanel {

    JToolBar letToolBar;//工具栏
    JButton goToggleButton;
    JButton backToggleButton;
    JButton upToggleButton;
    JButton downToggleButton;
    JButton refreshToggleButton;
    JButton homeToggleButton;
    JButton diskRootToggleButton;
    JButton locationButton;




    LocationToolPanel locationToolPanel;//路径输入矿



    JPanel rightToolBarPanel;//右侧工具栏
    JButton openTerminalToggleButton;
    JButton mkdirToggleButton;
    JProgressBar allBackgoundTaskProgressBar;


    public MainTopToolsPanel(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public void initGui() {

        //分为三部分  工具栏  地址栏目  工具栏目
        setLayout(new MigLayout());
        add(getLeftToolBar(),"height 20px ");
        //add(getLocationToolPanel(),"width 70%:95%:98%,height 20px ");
        add(getLocationToolPanel(),"width 70%:85%:90%,height 20px ");
        add(getRightToolBar(),"width 3%:7%:20%,height 20px ");
    }

    public Opt<FileTableTab> getNowSeletedTabPanel() {
        return mainFrame.getMainPanel().getCenterTabsPanel().getNowSeletedTabPanel();
    }
    /**
     * @return the toolBar
     */
    public synchronized JToolBar getLeftToolBar() {
        if (isNull(letToolBar)) {
            letToolBar=new JToolBar();
            letToolBar.add(getBackToggleButton());
            letToolBar.add(getGoToggleButton());
            letToolBar.add(getUpToggleButton());
            letToolBar.add(getRefreshToggleButton());
            letToolBar.add(getHomeToggleButton());
            letToolBar.add(getDiskRootToggleButton());
            
            //letToolBar.add(getDownToggleButton());

        }
        return letToolBar;
    }

    /**
     * @return the goToggleButton
     */
    public JButton getGoToggleButton() {
        if (isNull(goToggleButton)) {
            goToggleButton=newIconToggleButton(Icons.go);
            onClick(goToggleButton, e ->{
                getNowSeletedTabPanel().get().go();

            });
        }
        return goToggleButton;
    }

    /**
     * @return the backToggleButton
     */
    public JButton getBackToggleButton() {
        if (isNull(backToggleButton)) {
            backToggleButton=newIconToggleButton(Icons.back);
            onClick(backToggleButton, e ->{
                getNowSeletedTabPanel().get().back();

            });
        }
        return backToggleButton;
    }
    


 
    /**
     * @return the refreshToggleButton
     */
    public synchronized   JButton getRefreshToggleButton() {
        if (isNull(refreshToggleButton)) {
            refreshToggleButton=newIconToggleButtonName(Icons.refreshIconName);
            onClick(refreshToggleButton, e -> {
                FileTableTab fileTableTab = mainFrame.getMainPanel().getCenterTabsPanel().getNowSeletedTabPanel().get();
                fileTableTab.getNowShowingTable().updateAllTable();
            });
        }
        
        return refreshToggleButton;
    }


    /**
     * @return the homeToggleButton
     */
    public  synchronized JButton getHomeToggleButton() {
        if (isNull(homeToggleButton)) {
            homeToggleButton=newIconToggleButton(Icons.userHome);
            onClick(homeToggleButton, e ->{
                Opt<FileTableTab> nowSeletedTabPanel = mainFrame.getMainPanel().getCenterTabsPanel().getNowSeletedTabPanel();
                if(nowSeletedTabPanel.notNull_()){
                    FileTableTab fileTableTab = nowSeletedTabPanel.get();
                    try {
                        fileTableTab.navigationToDIr(new RESWallper(newLocalFIle("/home/"+getUser()+"/").get()), true);
                    } catch (FileSystemException e1) {
                        e1.printStackTrace();
                        TaskDialogs.showException(e1);
                    }
                }
            });
        }
        return homeToggleButton;
    }

    /**
     * @return the upToggleButton
     */
    public JButton getUpToggleButton() {
        if (isNull(upToggleButton)) {
            upToggleButton=newIconToggleButton(Icons.up);
            onClick(upToggleButton, e  ->{
                Opt<FileTableTab> nowSeletedTabPanel = mainFrame.getMainPanel().getCenterTabsPanel().getNowSeletedTabPanel();
                if(nowSeletedTabPanel.notNull_()){
                    FileTableTab fileTableTab = nowSeletedTabPanel.get();
                    fileTableTab.up();
                }
                
            });
        }
        return upToggleButton;
    }


    /**
     * @return the diskRootToggleButton
     */
    public synchronized JButton getDiskRootToggleButton() {
        if (isNull(diskRootToggleButton)) {
            diskRootToggleButton=newIconToggleButtonName(Icons.diskRootIconName);
            onClick(diskRootToggleButton, e  ->{
                getNowSeletedTabPanel().ifNotNull_(fileTab  ->{
                    try {
                        fileTab.navigationToDIr(new RESWallper(newLocalFIle("/").get()), true);
                    } catch (FileSystemException e1) {
                        e1.printStackTrace();
                        TaskDialogs.showException(e1);
                    } 
                });
                
            });
        }
        return diskRootToggleButton;
    }
    /**
     * @return the downToggleButton
     */
    public JButton getDownToggleButton() {
        if (isNull(downToggleButton)) {
            downToggleButton=newIconToggleButton(Icons.down);
            
        }
        return downToggleButton;
    }

 
    /**
     * @return the locationToolPanel
     */
    public synchronized LocationToolPanel getLocationToolPanel() {
        if (isNull(locationToolPanel)) {
            locationToolPanel=new LocationToolPanel(mainFrame);
        }
        return locationToolPanel;
    }


    /**
     * @return the rightToolBar
     */
    public synchronized JPanel getRightToolBar() {
        if (isNull(rightToolBarPanel)) {
            rightToolBarPanel=new  JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightToolBarPanel.setLayout(new MigLayout("align right"));
            rightToolBarPanel.add(getLocationButton());
            rightToolBarPanel.add(getAllBackgoundTaskProgressBar());
            rightToolBarPanel.add(getOpenTerminalToggleButton());
            rightToolBarPanel.add(getMkdirToggleButton());
        }
        return rightToolBarPanel;
    }

    /**
     * @return the openTerminalToggleButton
     */
    public JButton getOpenTerminalToggleButton() {
        if (isNull(openTerminalToggleButton)) {
            openTerminalToggleButton=newIconToggleButton(Icons.terminal);
            openTerminalToggleButton.setToolTipText("在当前位置打开终端");
            openTerminalToggleButton.setBorder(BorderFactory.createLineBorder(Color.white));
        }
        return openTerminalToggleButton;
    }

    /**
     * @return the mkdirToggleButton
     */
    public JButton getMkdirToggleButton() {
        if (isNull(mkdirToggleButton)) {
            mkdirToggleButton=newIconToggleButton(Icons.newFolder);
            mkdirToggleButton.setToolTipText("在当前位置创建文件夹");
        }

        return mkdirToggleButton;
    }

    /**
     * @return the locationButton
     */
    public synchronized JButton getLocationButton() {
        if (isNull(locationButton)) {
            locationButton=newIconToggleButton(Icons.location);
            locationButton.setToolTipText("切换路径模式");
            onClick(locationButton, e  ->{
                LocationToolPanel lp = getLocationToolPanel();
                if(lp.isButtonGroupModel()){
                    lp.changeToLocationEditModel();
                }else{
                     lp.changeToButtonGroupModel();
                }
                SwingUtilities.invokeLater(()  ->{
                    lp.updateUI();
                });
                
            });
        }
        return locationButton;
    }

    public synchronized JProgressBar getAllBackgoundTaskProgressBar() {
        if (isNull(allBackgoundTaskProgressBar)) {
            allBackgoundTaskProgressBar = new JProgressBar();
            allBackgoundTaskProgressBar.setUI(new ProgressCircleUI());
            allBackgoundTaskProgressBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            allBackgoundTaskProgressBar.setStringPainted(true);
            allBackgoundTaskProgressBar.setForeground(Color.GRAY);
            allBackgoundTaskProgressBar.setValue(0);
            onClick(allBackgoundTaskProgressBar, e  ->{
                mainFrame.getBackgroundTaskManager().showBackgroundTaskManagerFrame();
            });
        }
        return allBackgoundTaskProgressBar;
    }
}
