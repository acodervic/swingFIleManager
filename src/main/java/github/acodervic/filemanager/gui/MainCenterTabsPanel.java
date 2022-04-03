package github.acodervic.filemanager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.list.ListUtil;
import net.miginfocom.swing.MigLayout;

/**
 * 中间用于显示文件表格的面板
 */
public class MainCenterTabsPanel extends MainFramePanel implements ChangeListener,FocusListener {
    DraggableTabbedPane draggableTabbedPane;
    FileTableTab nowSeletedTabPanel;//当前激活的文件表格tab
    List<FileTableTab>  fileTableTabs;


    public MainCenterTabsPanel(MainFrame mainFrame) {
        super(mainFrame);
        mainFrame.addOnonFrameShowedFun(()  ->{
            resetTabheaderWidth(draggableTabbedPane);
        });
        draggableTabbedPane.addChangeListener(this);
    }


    

 


    /**
     * @return the draggableTabbedPane
     */
    public DraggableTabbedPane getDraggableTabbedPane() {
        if (isNull(draggableTabbedPane)) {
            draggableTabbedPane=new DraggableTabbedPane(this);
            draggableTabbedPane.addChangeListener(this);

        }
        return draggableTabbedPane;
    }

    @Override
    public void initGui() {
        setLayout(new BorderLayout());
        add(getDraggableTabbedPane(),BorderLayout.CENTER);
        try {
            addFilesTableTab(new RESWallper(newLocalFIle("/home/"+getUser()+"/").get()),true);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
        //addFilesTableTab(new DirRes("/home/w/Music/"));
        //addFilesTableTab(new DirRes("/home/w/Downloads/"));
        //addFilesTableTab(new DirRes("/home/w/.msf4/"));
 
 
    }

    public int indexTab(FileTableTab tab) {
        return getDraggableTabbedPane().indexOfComponent(tab);
    }
    public FileTableTab addFilesTableTab(RESWallper dir,Boolean seleted) {
        FileTableTab fileTbaleTab = new FileTableTab(dir,this,dir.getFileObj().getFileSystem().getFileSystemManager());
        getFIleTableTabsList().add(fileTbaleTab);
        getDraggableTabbedPane().add(fileTbaleTab.getTitle(),fileTbaleTab);        
        fileTbaleTab.setTabbedPane(getDraggableTabbedPane());
        int indexTab = indexTab(fileTbaleTab);
        getDraggableTabbedPane().setTabComponentAt(indexTab, fileTbaleTab.getTabHeaderPanel());

        resetTabheaderWidth(draggableTabbedPane);
        if (seleted) {
            //选中新添加的tabasdads`````c
            getDraggableTabbedPane().setSelectedComponent(fileTbaleTab);
            if (notNull(fileTbaleTab.getNowShowingTable())) {
                fileTbaleTab.getNowShowingTable().requestFocus();;//请求焦点
            }
        }
        return fileTbaleTab;
    }

    public void removeFilesTableTab(FileTableTab tab) {
        getFIleTableTabsList().removeIf(obj  ->{
            return obj==tab;
        });
        // 删除tab
        if (tab.getParent() instanceof DraggableTabbedPane dtp) {
            if (dtp.getTabCount()==1) {
                return ;//不处理只有一个表格
            }
            for (int i = 0; i < dtp.getTabCount(); i++) {
                Component componentAt = dtp.getComponentAt(i);
                if (componentAt == tab) {
                    // 删除
                    dtp.removeTabAt(i);
                    //TODO 释放所有资源
                    break;
                }
            }
        }
    }

        //调整tab大小

    public void resetTabheaderWidth(DraggableTabbedPane pane) {
        if (pane.getTabCount()>0) {
            //pane.setBorder(BorderFactory.createLineBorder(Color.red));
            int wid = (pane.getSize().width-((pane.getTabCount()*30)))/pane.getTabCount();
            for(int i=0; i<pane.getTabCount();i++){
                Component tabComponentAt = pane.getComponentAt(i);
                if (notNull(tabComponentAt)&& tabComponentAt instanceof FileTableTab tab) {
                    //pane.setTitleAt(i,"<html><div style=\"width: "+(wid-100)+"px\">"+tab.getTitle()+"</div></html>");
                    pane.setTitleAt(i,"<html><div >"+tab.getTitle()+"</div></html>");
    
                }
            }
        }

    }


    public void resetThisTabheaderWidth() {
        resetTabheaderWidth(draggableTabbedPane);
    }

    public Opt<FileTableTab> getNowSeletedTabPanel() {
        return new Opt<FileTableTab>(nowSeletedTabPanel);
    }


    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof DraggableTabbedPane tabspanel) {
            resetTabheaderWidth(tabspanel);
            locationChanged();
            getMainFrame().changeMainFrameTitle();
        }
        
    }













    private void locationChanged() {
        getNowSeletedTabPanel().ifNotNull_(tab  ->{
            tab.locationChanged(); 
        });
    }




    @Override
    public void focusGained(FocusEvent e) {
        locationChanged();
        }




    @Override
    public void focusLost(FocusEvent e) {
        
    }


    /**
     * @param nowSeletedTabPanel the nowSeletedTabPanel to set
     */
    public synchronized void setNowSeletedTabPanel(FileTableTab nowSeletedTabPanel) {
        this.nowSeletedTabPanel = nowSeletedTabPanel;
    }
    

 

    public List<FileTableTab> getFIleTableTabsList() {
        if (isNull(fileTableTabs)) {
            fileTableTabs=new ArrayList<>();
        }
        return fileTableTabs;
    }
 
    
}
