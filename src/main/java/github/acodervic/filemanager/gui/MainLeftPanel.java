package github.acodervic.filemanager.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.vfs2.FileSystemException;

import github.acodervic.filemanager.FSUtil;
import github.acodervic.filemanager.device.DeviceMounter;
import github.acodervic.filemanager.device.LinuxDeviceMountter;
import github.acodervic.filemanager.model.BookMark;
import github.acodervic.filemanager.model.Device;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.swing.MessageBox;
import github.acodervic.mod.swing.tree.FilteredTree;
import net.miginfocom.swing.MigLayout;

 
/**
 * 左侧面板,显示书签和驱动器等
 */
public class MainLeftPanel extends MainFramePanel {
    List<BookMark>         readAllBookMarks=new ArrayList<>();
    

    
    FilteredTree<DefaultMutableTreeNode>  allTree;
    DefaultMutableTreeNode  allRootNode;
    DefaultMutableTreeNode  mycomputerRootNode;
    DefaultMutableTreeNode homeDir;
    DefaultMutableTreeNode rootDir;
    DefaultMutableTreeNode docmentDir;
    DefaultMutableTreeNode desktopDir;
    DefaultMutableTreeNode downloadDir;
    DefaultMutableTreeNode picturesDir;
    DefaultMutableTreeNode videosDir;
    DefaultMutableTreeNode musicsDir;
    DefaultMutableTreeNode trashDir;










//===========================================
    DefaultMutableTreeNode  bookMarksRootNode;
    



    //===============================================
    DefaultMutableTreeNode  devicesRootNode;
    


    //=============================
    DefaultMutableTreeNode  netWorkDeviceNode;
    
    
    
    public MainLeftPanel(MainFrame mainFrame) {
        super(mainFrame,false);
    }

    @Override
    public void initGui() {
        setPreferredSize(new Dimension(250, 1));
        setLayout(new MigLayout());
                
        add(getAllTree(),"width 100%,height 100%,wrap");
        //add(getMycomputerTree(),"width 100%,height 25%,wrap");
        //add(getBookMarksTree(),"width 100%,height 50%,wrap");
 
        
    }


    /**
     * @return the allRootNode
     */
    public DefaultMutableTreeNode getAllRootNode() {
        if (isNull(allRootNode)) {
            allRootNode=new DefaultMutableTreeNode("All");
            allRootNode.add(getMycomputerRootNode());
            allRootNode.add(getBookMarksRootNode());
            allRootNode.add(getDevicesRootNode());
        }
        return allRootNode;
    }
    /**
     * @return the mycomputerRootNode
     */
    public DefaultMutableTreeNode getMycomputerRootNode() {
        if (isNull(mycomputerRootNode)) {
            mycomputerRootNode=new DefaultMutableTreeNode("MyComputer");
        }
        return mycomputerRootNode;
    }

    /**
     * @return the homeDir
     */
    public DefaultMutableTreeNode getHomeDir() {
        if (isNull(homeDir)) {
            try {
                homeDir=new DefaultMutableTreeNode(new RESWallper(newLocalFIle(getHomeDirPath()).get(), "Home", Icons.userHome) );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return homeDir;
    }


    /**
     * @return the desktopDir
     */
    public DefaultMutableTreeNode getDesktopDir() {
        if (isNull(desktopDir)) {
                try {
                    desktopDir=new DefaultMutableTreeNode(new RESWallper(newLocalFIle(getHomeDirPath()+"Desktopk").get() , "桌面", Icons.userDesktop) );
                } catch (Exception e) {
                    e.printStackTrace();
                }            
        }
        return desktopDir;
    }

    /**
     * @return the docmentDir
     */
    public DefaultMutableTreeNode getDocmentDir() {
        if (isNull(docmentDir)) {
            try {
                docmentDir=new DefaultMutableTreeNode(new RESWallper(newLocalFIle(getHomeDirPath()+"/Documents").get(), "文档", Icons.docment) );
            } catch (Exception e) {
                e.printStackTrace();
            }            
        }
        return docmentDir;
    }

    /**
     * @return the rootDir
     */
    public DefaultMutableTreeNode getRootDir() {
        if (isNull(rootDir)) {
            try {
                rootDir=new DefaultMutableTreeNode(new RESWallper(newLocalFIle("/").get(), "DeviceRoot", Icons.docment) );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rootDir;
    }

    /**
     * @return the musicsDir
     */
    public DefaultMutableTreeNode getMusicsDir() {
        if (isNull(musicsDir)) {
            try {
                musicsDir=new DefaultMutableTreeNode(new RESWallper(newLocalFIle(getHomeDirPath()+"/Music").get(), "Music", Icons.docment) );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return musicsDir;
    }
    
    /**
     * @return the downloadDir
     */
    public DefaultMutableTreeNode getDownloadDir() {
        if (isNull(downloadDir)) {
            try {
                downloadDir=new DefaultMutableTreeNode(new RESWallper(newLocalFIle(getHomeDirPath()+"/Downloads").get(), "Download", Icons.docment) );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return downloadDir;
    }

    /**
     * @return the picturesDir
     */
    public DefaultMutableTreeNode getPicturesDir() {
        if (isNull(picturesDir)) {
            try {
                picturesDir = new DefaultMutableTreeNode(
                        new RESWallper(newLocalFIle(getHomeDirPath() + "/Pictures").get(), "Pictures", Icons.docment));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return picturesDir;
    }

    /**
     * @return the trashDir
     */
    public DefaultMutableTreeNode getTrashDir() {
        if (isNull(trashDir)) {
            try {
                trashDir=new DefaultMutableTreeNode(new RESWallper(newLocalFIle("/home/w/.local/share/Trash/files").get(), "Trash", Icons.getIconByName("put-trash")) );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return trashDir;
    }


    /**
     * @return the allTree
     */
    public synchronized  FilteredTree<DefaultMutableTreeNode> getAllTree() {
        if (isNull(allTree)) {
            allTree=new FilteredTree<>(getAllRootNode());
            allTree.setTreeCellRendererCommponentsFunction(arbw  ->{
                List<Component> coms=new ArrayList<>();
                Opt<Object> userObject = arbw.getUserObject();
                if(userObject.notNull_() ){
                    if ( userObject.get() instanceof RESWallper res) {
                        coms.add(new JLabel(res.getIcon()));
                        coms.add(new JLabel(res.getName()));
                    }else if( userObject.get() instanceof String name){
                        coms.add(new JLabel(name));
                    }else if( userObject.get() instanceof BookMark bookmark){
                        RESWallper dirRes;
                        try {
                            dirRes = bookmark.getDirRes();
                            coms.add(new JLabel(dirRes.getIcon()));
                            coms.add(new JLabel(dirRes.getName()));
                        } catch (FileSystemException e) {
                            e.printStackTrace();
                        }

                        return   coms;

                    }else if( userObject.get() instanceof Device dev){
                                JLabel statusIconLable=new JLabel();
                                coms.add(new JLabel(Icons.getIconByName(Icons.vdisk)));
                                JLabel nameLabel = new JLabel(dev.getName());
                                coms.add(nameLabel);
                                if (dev.isMounted()) {
                                    nameLabel.setForeground(Color.WHITE);
                                }else{
                                    nameLabel.setForeground(Color.gray);
                                }
                                dev.getOnMountFuns().add(()  ->{
                                    nameLabel.setForeground(Color.WHITE);
                                });
                                dev.getOnUmountFuns().add(()  ->{
                                    nameLabel.setForeground(Color.gray);
                                });
                                coms.add(statusIconLable);
                        return   coms;
                    }
                }
                return   coms;
            });
            allTree.getTree().setRootVisible(false);//root节点不可见
            allTree.onClickNode(node  ->{
                Object userObj= node.getUserObject();;
                if(userObj instanceof RESWallper dir){
                    getNowSeletedTabPanel().get().navigationToDIr(dir, true);
                }else if(userObj instanceof BookMark bookmark){
                    try {
                        getNowSeletedTabPanel().get().navigationToDIr(bookmark.getDirRes(), true);
                    } catch (FileSystemException e) {
                        e.printStackTrace();
                    }
                }else if(userObj instanceof Device dev){
                    FileTableTab fileTableTab = getNowSeletedTabPanel().get();
                    try {
                        if (dev.isMounted()) {
                            //fileTableTab.navigationToDIr(dev.getDistDirRes(), true);
                        } else {
                            //自动挂载
                            mainFrame.getDeviceMounter().mount(dev);
                            if (dev.getDistDirRes().exists()) {
                              //  fileTableTab.navigationToDIr(dev.getDistDirRes(), true);
                            } else {
                                MessageBox.showErrorMessageDialog(mainFrame,
                                        "挂载磁盘" + dev.getSourceFIleRes().toString() + "错误",
                                        "无法找到挂载后的卷" + dev.getDistDirRes().toString());
                            }
                        }
                        dev.getOnUmountFuns().add(()  ->{
                            fileTableTab.getTabsPanel().resetThisTabheaderWidth();//刷新title
                        });
                        dev.getOnMountFuns().add(()  ->{
                            fileTableTab.getTabsPanel().resetThisTabheaderWidth();//刷新title
                        });
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            });
            allTree.remove(allTree.getFIlterTextField());//不显示搜索框
            randeMycomputerTreer();
            randerBookMarksTree();
            randerDevicesTree();
            allTree.expandAllNode();
            //randeMycomputerTreer();
            reloadTree();
            binPopToFilteredTree(allTree);
        }
        return allTree;
    }
  
    public void randeMycomputerTreer () {
        mycomputerRootNode.removeAllChildren();
        mycomputerRootNode.add(getRootDir());
        mycomputerRootNode.add(getHomeDir());
        mycomputerRootNode.add(getDesktopDir());
        mycomputerRootNode.add(getDocmentDir());
        mycomputerRootNode.add(getDesktopDir());
        mycomputerRootNode.add(getDownloadDir());
        mycomputerRootNode.add(getMusicsDir());
        mycomputerRootNode.add(getPicturesDir());
        mycomputerRootNode.add(getTrashDir());
    }
 

    /**
     * @return the bookMarksRootNode
     */
    public DefaultMutableTreeNode getBookMarksRootNode() {
        if (isNull(bookMarksRootNode)) {
            bookMarksRootNode=new DefaultMutableTreeNode("BookMarks");
        }
        return bookMarksRootNode;
    }
 


    public Opt<FileTableTab> getNowSeletedTabPanel() {
        return mainFrame.getMainPanel().getCenterTabsPanel().getNowSeletedTabPanel();
        
    }

 
    public void randerBookMarksTree() {
        getBookMarksRootNode().removeAllChildren();
        readAllBookMarks.clear();
        readAllBookMarks.addAll( FSUtil.readAllBookMarks());        
        for (int i = 0; i <readAllBookMarks.size(); i++) {
            BookMark bookMark = readAllBookMarks.get(i);
            DefaultMutableTreeNode node=new DefaultMutableTreeNode(bookMark);
            getBookMarksRootNode().add(node);
        }
    }

    public void reloadTree() {
        getAllTree().reloadTree();
        getAllTree().expandAllNode();
    }

    public String  getHomeDirPath() {
        if (isLinux()) {
            if (getUser().equals("root")) {
                return "/root";
            }else{
                return "/home/"+getUser();
            }
        }
        return "";
    }
 

    public void binPopToFilteredTree(FilteredTree treePanel) {
        JTree tree = treePanel.getTree();
        treePanel.getRootNode().ifNotNull_(node -> {

        });
        tree.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point point = e.getPoint();
                int selRow = tree.getClosestRowForLocation((int) point.getX(), (int) point.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                tree.setSelectionPath(selPath);
                if (selRow > -1) {
                    tree.setSelectionRow(selRow);
                }
            }
        });
        tree.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                
            }

            @Override
            public void mousePressed(MouseEvent e) {
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 2) {
                    // 中键新的tab打开
                    if (treePanel.getNowSelectedNode().get() instanceof DefaultMutableTreeNode node) {
                        if (node.getUserObject() instanceof RESWallper res) {
                            // 打开新tab
                            mainFrame.getMainPanel().getCenterTabsPanel().addFilesTableTab(res, true);
                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                
            }
            
        });
 
        // 绑定书签弹窗到 FilteredTree 上
        treePanel.addPopMenu("Open in new Tab", e -> {
            if (treePanel.getNowSelectedNode().get() instanceof DefaultMutableTreeNode node) {
                if (node.getUserObject() instanceof RESWallper res) {
                    // 打开新tab
                    mainFrame.getMainPanel().getCenterTabsPanel().addFilesTableTab(res, true);
                }
            }
        });
        treePanel.addPopMenu("Delete Book Mark", e -> {
            if (treePanel.getNowSelectedNode().get() instanceof DefaultMutableTreeNode node) {
                if (node.getUserObject() instanceof BookMark bm) {
                   
                    FSUtil.delBookMark(bm);
                    randerBookMarksTree();
                    SwingUtilities.invokeLater(()  ->{
                        reloadTree();
                    });
                }
            }
        });

        treePanel.addPopMenu("UnMount", e -> {
            if (treePanel.getNowSelectedNode().get() instanceof DefaultMutableTreeNode node) {
                if (node.getUserObject() instanceof Device dev) {
                    if (dev.isMounted()) {
                        try {
                            mainFrame.getDeviceMounter().unmount(dev);
                        } catch (Exception e1) {
                            Boolean force = MessageBox.showConfirmErrorDialog(mainFrame, "error", e1.getMessage()+"  , force Umount?");
                            if (force) {
                                try {
                                    mainFrame.getDeviceMounter().forceUmount(dev);
                                } catch (Exception e2) {
                                    MessageBox.showErrorMessageDialog(mainFrame , "erroe", e2.getMessage());
                                    
                                }
                            }
                        }
                    }
                    SwingUtilities.invokeLater(()  ->{
                        reloadTree();
                    });
                }
            }
        });

    }

    /**
     * @return the devicesNode
     */
    public synchronized DefaultMutableTreeNode getDevicesRootNode() {
        if (isNull(devicesRootNode)) {
            devicesRootNode=new DefaultMutableTreeNode("Devices");
        }
        return devicesRootNode;
    }

    public void randerDevicesTree() {
        getDevicesRootNode().removeAllChildren();
        DeviceMounter deviceMounter = mainFrame.getDeviceMounter();
        List<Device> allDevices = deviceMounter.getAllDevices();
        for (int i = 0; i < allDevices.size(); i++) {
            Device device = allDevices.get(i);
            DefaultMutableTreeNode node=new DefaultMutableTreeNode(device);
            getDevicesRootNode().add(node);
        }
    }
}
