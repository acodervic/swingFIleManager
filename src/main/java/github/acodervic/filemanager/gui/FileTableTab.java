package github.acodervic.filemanager.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.MenuElement;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import com.jediterm.terminal.Terminal;
import com.jediterm.terminal.ui.JediTermWidget;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.provider.local.LocalFile;
import org.oxbow.swingbits.dialog.task.TaskDialogs;

import github.acodervic.filemanager.gui.popmenus.MyPopMenuItem;
import github.acodervic.filemanager.model.History;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.model.SearchRootRESWallper;
import github.acodervic.filemanager.thread.PublicThreadPool;
import github.acodervic.filemanager.treetable.FileSystemModel;
import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.filemanager.treetable.TreeTableCellRenderer;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.Value;
import github.acodervic.mod.data.str;
import github.acodervic.mod.i118n.Properties;
import github.acodervic.mod.swing.MessageBox;
import github.acodervic.mod.thread.AsyncTaskRunnable;
import github.acodervic.mod.thread.Task;
import github.acodervic.mod.thread.TimePool;
import net.miginfocom.swing.MigLayout;
public class FileTableTab  extends JPanel implements GuiUtil,FocusListener {
    MainCenterTabsPanel tabsPanel;//?????????tabspanel
    JPopupMenu popupMenu=new JPopupMenu();
    RESWallper nowRootNodeDir;
    FileSystemModel rootTreeTableModel;
    List<History>  historyList=new ArrayList<>();
    HashMap<RESWallper,RESWallper>   rESWallperWithMap=new HashMap<>(); 
    int nowHistoryListPront=0;//???????????? historyList?????????
    JPanel centerPane=new JPanel(new MigLayout());
    JTreeTable nowShowingTable;
    JPanel trashOperatePanel;//????????????,??????????????????????????????
    JPanel searchTextInputPanel;//????????????,??????????????????text
    JTextField searchTextInputField;
    JCheckBox isRegexRaido;
    FileTableTab me;
    String uiModel=UIModel_FILE;
    FileSystemManager fileSystemManager;//????????????????????????
    Opt<BiConsumer<String,Boolean>> onSearchEnterFun=new Opt<>(); 
    DraggableTabbedPane tabbedPane;
    public static String UIModel_FILE="FILE";
    public static String UIModel_SEACH="SEACH";
    JPanel tabHeaderPanel;
    FileTreeTablePanel fileTreeTablePanel = null;

 
    //?????????????????????
    
    //???????????????

    //??????????????????
    TerminalPanel terminalPanel;

    long  doubleCLickTIme=100;
    MouseListener doubleClick = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            int ctime = (int)(System.currentTimeMillis() - doubleCLickTIme);// ????????????
            doubleCLickTIme = System.currentTimeMillis();
            if (notNull(me.nowShowingTable)) {
                TreeTableCellRenderer tree = me.nowShowingTable.getTree();
                // int selRow = tree.getRowForLocation(e.getX(), e.getY());
                // TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                // if (selRow != -1) {

                if (e.getClickCount() == 1) {
                        
                    } else if (e.getClickCount() == 2&&e.getButton()==1) {//&&ctime<270) {
                        List<RESWallper> selectedResWallper = getNowShowingTable().getSelectedResWallper();
                        if(selectedResWallper.size()==0){

                        }else{
                            RESWallper res=selectedResWallper.get(0);
                            if(res.isDir()){
                                if(getNowShowingTable().inSearchModel()){
                                    //???????????????????????????tab?????????
                                    tabsPanel.getMainFrame().getMainPanel().getCenterTabsPanel().addFilesTableTab(res, true);
                                }else{
                                    navigationToDIr(res,true);//?????????????????????
                                }
                            }else{
                                getTabsPanel().getMainFrame().getXdgOpenConfigManager().open(res);
                                //res.xdgOpen();
                                //??????????????????
                                
    
                            }                             
                        }

                    }
                //}
            }

        }
    };
    
    Runnable  changeLocationFun=()->{
        if(tabsPanel.mainFrame.isShowing()){
            tabsPanel.mainFrame.getMainPanel().getTopToolsPanel().getLocationToolPanel().setLocationDir(nowRootNodeDir);
        }
    };
    public  FileTableTab(RESWallper nowRootNodeDir, MainCenterTabsPanel tabsPanel, FileSystemManager fileSystemManager) {
        this.tabsPanel=tabsPanel;
        this.fileSystemManager=fileSystemManager;
        try {
            rootTreeTableModel=new FileSystemModel(newLocalFIle("/").get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initPopmenus();
        navigationToDIr(nowRootNodeDir,true);
        setLayout(new MigLayout());
        add(centerPane,"width 100%,height 85%,wrap");
        add(getTerminalPanel(),"width 100%,height 15%");
        me=this;
        addFocusListener(this);
    }

    private void initPopmenus() {
        List<Class> classsByPackageSuperClass = github.acodervic.mod.reflect.ReflectUtil.getClasssByPackageSuperClass("github.acodervic.filemanager.gui.popmenus", MyPopMenuItem.class);
        for (int i = 0; i < classsByPackageSuperClass.size(); i++) {
            try {
                MyPopMenuItem popMenu=(MyPopMenuItem)classsByPackageSuperClass.get(i).newInstance();
                addPopMenu(popMenu);
                logInfo("??????????????????"+popMenu.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }   
        }
    }

  

    /**
     * @return the tabbedPane
     */
    public DraggableTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    /**
     * @param tabbedPane the tabbedPane to set
     */
    public void setTabbedPane(DraggableTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }
 
    public String getTitle() {
        String titleString="";
        if (isNull(nowRootNodeDir)) {
            return "";
        }
        if ( nowRootNodeDir.toString().equals("/")) {
            titleString=  "Root Path";
        }else{
            titleString=nowRootNodeDir.getBaseName();
        }
        if (getNowShowingTable().inSearching()) {
            titleString+=" Searching..";
        }else{
            if (getNowShowingTable().inSearchModel()) {
                titleString+=" Searched";
            }
        }
        try {
            if (!nowRootNodeDir.exists()) {
                titleString+="( UnMounted !)";
                 if (getNowShowingTable().getRowCount()>0) {
                    getNowShowingTable().updateAllTable();
                     getNowShowingTable().refreshUi();
                     getNowShowingTable().requestFocus();
                 }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return titleString;
    }

    
    public void locationChanged() {
        changeLocationFun.run();;
    }


    public Opt<Task> navigationTask=new Opt<Task>();
    /**
     * ?????????????????????
     * @param dir
     * @param saveHistory ???????????????????????????,?????? nowHistoryListPrint+1
     */
    public synchronized void navigationToDIr(RESWallper dir_p,Boolean saveHistory) {
        if (!dir_p.allowNavigation()) {
            return ;
        }
        //if (!allowNavigation(dir) ) {
          //  return  ;
        //}
        Value  dir=new Value(dir_p);
        try {
            dir.setValue(dir_p.cloneRes());;
        } catch (Exception e1) {
            e1.printStackTrace();
            TaskDialogs.showException(e1);
            return ;
        }
        logInfo("navigationToDIr "+dir);
        if (dir.get(RESWallper.class).isLocalFile()) {
            //????????????????????????
            navigation(dir.get(RESWallper.class), saveHistory);
        }else{
             //????????????
             if (navigationTask.notNull_()) {
                 //????????????????????????????????????
                 navigationTask.get().killWorkThread();
             }
 
                //navigationTask.of(task);
                Task<Object> filePorcessPool_Exec = PublicThreadPool.FilePorcessPool_Exec(" navigationToDIr"+dir.get(RESWallper.class), ()  ->{
                    try {
                        loadIng();
                        randerTabHeaderPanel();
                        //?????????????????????????????????????????????
                        navigation(dir.get(RESWallper.class), saveHistory);
                        randerTabHeaderPanel();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MessageBox.showErrorMessageDialog(centerPane, "??????????????????????????????",dir.get(RESWallper.class).getAbsolutePath()+"   "+e.getMessage() );
                    }
                    me.navigationTask.ofNull();
                    cancelLoadIng();
                });
                navigationTask.of(filePorcessPool_Exec);
        }
        


        //terminal.
        //terminal.newLine();;

        //terminal.
    }

    private void navigation(RESWallper  dir, Boolean saveHistory) {
        if (notNull(nowRootNodeDir)&&dir.equals(nowRootNodeDir.toString())){
            //?????????????????????????????????
            return ;
        }
        if (notNull(nowShowingTable)) {
            if (nowShowingTable.inSearchModel()) {
                // ?????????????????????????????????????????????????????????
                nowShowingTable.changeToFileSystemModel();
            }
        }

        RESWallper oldRootNodeDir=nowRootNodeDir;
            //????????????????????????
            for (int i = (historyList.size()-1); i >-1; i--) {
                History history = historyList.get(i);
                if (history.getRes().getAbsolutePath().equals( oldRootNodeDir.getAbsolutePath())) {
                    history.setSelectedRse(getNowShowingTable().getSelectedResWallper());
                    history.setRectangle(getNowShowingTable().getVisibleRect());
                    break;
                }
            }
      
        try {
            FileSystemModel newModel = new FileSystemModel(dir);
            List<RESWallper> childResList = ((RESWallper) newModel.getRoot()).getChildResList();// ??????????????????,??????????????????swing??????
                                                                                                // ???????????????
            for (int i = 0; i < childResList.size(); i++) {
                childResList.get(i).getTreeNameColumnPanel();
            }
            JTreeTable jTreeTable = new JTreeTable(newModel, this);
            fileTreeTablePanel = new FileTreeTablePanel(jTreeTable);
            jTreeTable.addMouseListener(doubleClick);

            // ????????????????????????
            unRegisterWatchDir(oldRootNodeDir);
            registerWatchDir((RESWallper) newModel.getRoot());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Value panel=new Value(fileTreeTablePanel);

        nowRootNodeDir=dir;//???????????????????????????
        locationChanged();
        switchToFileModel(fileTreeTablePanel);
 
        MouseAdapter mouseAdapter = new MouseAdapter() {
 
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != 3) {
                    // ???????????????
                    return;
                }
                FileTreeTablePanel sroll = panel.get(FileTreeTablePanel.class );
                JTreeTable treeTable = sroll.getTreeTable();
                int rowindex = treeTable.getSelectedRow();
                List<RESWallper> selectedResWallper = new ArrayList<>(treeTable.getSelectedResWallper());
                // if (rowindex < 0)
                // return;
                //if (e.getComponent() instanceof JTable) {
                    // ???????????????????????????
                    for (int i = 0; i < popupMenu.getSubElements().length; i++) {
                        MenuElement item=popupMenu.getSubElements()[i] ;
                    if (item instanceof MyPopMenuItem myPopMenuItem) {
                        myPopMenuItem.setVisible(true);
                        Boolean needDisplay = true;
                        if (myPopMenuItem.getSupportedFileSystem().contains(fileSystemManager.getClass())) {
                            for (int j = 0; j < selectedResWallper.size(); j++) {
                                if (selectedResWallper.get(j) instanceof SearchRootRESWallper) {// ???????????????????????????????????????
                                    needDisplay = false;
                                    break;
                                }
                            }
                            if (needDisplay) {
                                needDisplay = myPopMenuItem.needDisplay(new ArrayList<>(selectedResWallper),
                                        treeTable.getRootDir().get(),
                                        getTabsPanel().getMainFrame().getMainPanel());
                            }
                            if (notNull(needDisplay)) {
                                myPopMenuItem.setVisible(needDisplay);
                            }
                        }
                    }else{
                        JMenu jMenu = (JMenu)item;
                        jMenu.setVisible(true);
                        for (int j = 0; j < selectedResWallper.size(); j++) {
                            if (selectedResWallper.get(j) instanceof SearchRootRESWallper) {// ???????????????????????????????????????
                                jMenu.setVisible(false);
                                break;
                            }
                        }
                    }
                }
                    popupMenu.updateUI();
                    int verticalScrollValue = sroll.getVerticalScrollBar().getValue();

                    
                    popupMenu.show(me, e.getX(), e.getY()-verticalScrollValue);
                //}
            }
        };
        //????????????table???scrollpanel????????? ??????
        fileTreeTablePanel.addMouseListener(mouseAdapter);
        fileTreeTablePanel.getTreeTable().addMouseListener(mouseAdapter);
        
        JTreeTable oldJTreeTable=nowShowingTable;
        nowShowingTable=fileTreeTablePanel.getTreeTable();
        nowShowingTable.requestFocus();//????????????
        nowShowingTable.addFocusListener(this);
        if (notNull(oldJTreeTable)) {
            fileTreeTablePanel.removeKeyListener(oldJTreeTable.getHotKeyListener());
            //??????????????????????????????table???
            TableColumnModel columnModel = oldJTreeTable.getColumnModel();
            for (int i = 0; i < columnModel.getColumnCount(); i++) {
                TableColumn column = columnModel.getColumn(i);
                int wid = column.getWidth();
 
                fileTreeTablePanel.getTreeTable().getColumnModel().getColumn(i).setPreferredWidth(wid);
            }
        }
        fileTreeTablePanel.addKeyListener(nowShowingTable.getHotKeyListener());
        if (saveHistory) {

            // ??????????????????
            if (nowHistoryListPront < historyList.size() - 1) {
                // ?????????????????????
                for (int i = (historyList.size()-1) ; i > (nowHistoryListPront) ; i--) {
                    History history = historyList.get(i);
                    RESWallper dirRes =history.getRes(); 
                    historyList.remove(i);
                    //????????????????????????
                    if (rESWallperWithMap.containsKey(dirRes)) {
                        tabsPanel.getMainFrame().unRegisterWatchDir(rESWallperWithMap.get(dirRes));;
                    }
                }
            }
            historyList.add(new History(dir, null));
            nowHistoryListPront = (historyList.size() - 1);
        }
        tabsPanel.resetThisTabheaderWidth();//??????title???tab??????
        tabsPanel.getMainFrame().changeMainFrameTitle();
        TerminalPanel terminalPanel2 = getTerminalPanel();
        JediTermWidget terminal2 = terminalPanel2.getTerminal();
        if (notNull(terminal2)&&dir.isLocalFile()) {
            String cmd=("cd '"+dir.getAbsolutePathWithoutProtocol()+"' \n");
            terminal2.getTerminalStarter().sendString(cmd);
            
        }
        randerTabHeaderPanel();
    }

    public  void switchToFileModel(FileTreeTablePanel fileTreeTablePanel) {
        centerPane.removeAll();
        if (rootDirIsRrash()) {
            centerPane.add(getTrashOperatePanel(),"width 100%,height 30!,wrap");
            centerPane.add(fileTreeTablePanel,"width 100%,height 90%:98%:100%");

        }else{
            centerPane.add(fileTreeTablePanel,"width 100%,height 100%");
        }
        getSearchTextInputField().setText("");
        centerPane.updateUI();
        setUiModel(UIModel_FILE);
        if (notNull(getNowShowingTable())) {
            getNowShowingTable().requestFocus();
        }
    }


    public void switchToSearchModel(FileTreeTablePanel fileTreeTablePanel,BiConsumer<String,Boolean> onsearchEnterFun) {
        centerPane.removeAll();
        if (rootDirIsRrash()) {
            centerPane.add(getSearchTextInputPanel(),"width 100%,height 28!,wrap");
            centerPane.add(fileTreeTablePanel,"width 100%,height 90%:98%:100%");
        }else{
            centerPane.add(getSearchTextInputPanel(),"width 100%,height 28!,wrap");
            centerPane.add(fileTreeTablePanel,"width 100%,height 90%:98%:100%");
        }
        centerPane.updateUI();
        getOnSearchEnterFun().of(onsearchEnterFun);
        setUiModel(UIModel_SEACH);
        TimePool.getStaticTimePool().setTimeOut(300, ()  ->{
            getSearchTextInputField().requestFocus();
        });
    }

 

    /**
     * ???????????????
     * @return
     */
    public boolean up() {
        if (notNull(nowRootNodeDir)) {
            try {
                FileObject parent = nowRootNodeDir.getFileObj().getParent();
                if (notNull(parent)) {
                    navigationToDIr(new RESWallper(parent), true);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public boolean back() {
        if (historyList.size()==0) {
             return false;//????????????
        }
        if (nowHistoryListPront==0) {
            return false;//????????????
        }
        if (nowHistoryListPront>=historyList.size()) {
            return false;//????????????
        }
        nowHistoryListPront=nowHistoryListPront-1;
        History history = historyList.get(nowHistoryListPront);
        RESWallper dirRes = history.getRes();
        if (notNull(dirRes)) {
            navigationToDIr(dirRes,false);
            for (int i = (historyList.size()-1); i >-1; i--) {
                History historyData = historyList.get(i);
                if (historyData.getRes().getFileObj().toString().equals(dirRes.getFileObj().toString())) {
                    List<RESWallper> selectedRse = historyData.getSelectedRse();
                    JTreeTable nt = getNowShowingTable();
                    nt.setSelectedResWallpers(selectedRse);
                    nt.scrollRectToVisible(historyData.getRectangle());
                    break;
                }
                
            }
            return true;
        }
        return false;
    }

    public boolean go() {
        if (historyList.size()==0) {
            return false;//????????????
       }
       if (nowHistoryListPront+1>=historyList.size()) {
           return false;//????????????
       }
       nowHistoryListPront=nowHistoryListPront+1;
       History history = historyList.get(nowHistoryListPront);
       RESWallper dirRes = history.getRes();
       if (notNull(dirRes)) {
           navigationToDIr(dirRes,false);
           return true;
       }
       return false;
    }

    public void registerWatchDir(RESWallper dir) {
        if (!dir.isLocalFile()) {
            return ;
        }
        if (dir.isFile()) {
            return ;
        }
        tabsPanel.getMainFrame().registerWatchDir(dir);
    }


    public void unRegisterWatchDir(RESWallper dir) {
        if (isNull(dir)) {
            return ;
        }
        if (dir.isFile()) {
            return ;
        }
        tabsPanel.getMainFrame().unRegisterWatchDir(dir);
    }
 


	public void addPopMenu(MyPopMenuItem popMenuitem) {
        ActionListener l = e  ->{
            RESWallper nowDir = nowShowingTable.getRootDir().get();
			List<RESWallper> selectedResWallper = nowShowingTable.getSelectedResWallper();
            if (selectedResWallper.size()==1) {
            }
            popMenuitem.action(selectedResWallper, nowDir, tabsPanel.mainFrame.getMainPanel());
        };

        List<MyPopMenuItem> subItems = popMenuitem.getSubItems();
        if (notNull(subItems) && subItems.size() > 0) {
            JMenu menu = new JMenu(popMenuitem.getName());
            menu.setIcon(popMenuitem.getIcon());
            menu.setToolTipText(popMenuitem.getTip());
            menu.addActionListener(l);
            for (int i = 0; i < subItems.size(); i++) {
                JMenuItem jMenuItem = subItems.get(i);
                jMenuItem.addActionListener(e -> {
                    RESWallper nowDir = nowShowingTable.getRootDir().get();
                    List<RESWallper> selectedResWallper = nowShowingTable.getSelectedResWallper();
                    if (selectedResWallper.size() == 1) {

                    }
                    ((MyPopMenuItem) jMenuItem).action(selectedResWallper, nowDir, tabsPanel.mainFrame.getMainPanel());
                });
                jMenuItem.setText(jMenuItem.getName());
                jMenuItem.setIcon(jMenuItem.getIcon());
                menu.add(jMenuItem);
            }
            popupMenu.add(menu);
        } else {
            popMenuitem.setToolTipText(popMenuitem.getTip());
            popMenuitem.setText(popMenuitem.getName());
            popMenuitem.setIcon(popMenuitem.getIcon());
            popMenuitem.addActionListener(l);
            popupMenu.add(popMenuitem);
        }

    }

    @Override
    public void focusGained(FocusEvent e) {
        locationChanged();      
        if (e.getComponent() instanceof JTreeTable treeTable) {
            //?????????????????????fileTab
            tabsPanel.setNowSeletedTabPanel(me);
        }
        tabsPanel.getMainFrame().changeMainFrameTitle();
        //System.out.println("12313");
    }

    @Override
    public void focusLost(FocusEvent e) {

    }


    /**
     * @return the nowShowingTable
     */
    public JTreeTable getNowShowingTable() {
        return nowShowingTable;
    }

    /**
     * @return the tabsPanel
     */
    public MainCenterTabsPanel getTabsPanel() {
        return tabsPanel;
    }


 
    /**
     * @return the terminalPanel
     */
    public  synchronized TerminalPanel getTerminalPanel() {
        if (isNull(terminalPanel)) {
            terminalPanel=new TerminalPanel();
            terminalPanel.open();//??????pty
        }
        return terminalPanel;
    }
    
 

    public  static String trashPath="file:///home/"+System.getProperty("user.name")+"/.local/share/Trash/";
    public  static String trashInfoPath="/home/"+System.getProperty("user.name")+"/.local/share/Trash/info/";
    /**
     * ????????????????????????
     * @return
     */
    public boolean allowNavigation(FileObject dir) {
        return !inTrashDir(dir);
    }

    public boolean inTrashDir(FileObject dir) {
        try {
            String absolutePath = new RESWallper(dir, TOOL_TIP_TEXT_KEY).getAbsolutePath();
            if (absolutePath.equals(trashPath+"files/")) {
                return false;
            }
            if (absolutePath.startsWith(trashPath)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean rootDirIsRrash() {
        return  nowRootNodeDir.getAbsolutePath().equals(trashPath+"files");
    }
    JButton restoreButton ;
    JButton emptyTrashButton ;


    /**
     * @return the emptyTrashButton
     */
    public synchronized JButton getEmptyTrashButton() {
        if (isNull(emptyTrashButton)) {
            emptyTrashButton = newIconToggleButtonText("Empty Trash");
            onClick(emptyTrashButton, e -> {

                PublicThreadPool.FilePorcessPool_Exec("trash-empty", () -> {
                    exec2String("trash-empty");
                });
            });

        }
        return emptyTrashButton;
    }

    /**
     * @return the restore
     */
    public synchronized JButton getRestoreButton() {
        if (isNull(restoreButton)) {
            restoreButton= newIconToggleButtonText("Restore Selected Items");
            onClick(restoreButton, e  ->{
                List<RESWallper> selectedResWallper = getNowShowingTable().getSelectedResWallper();
                if(selectedResWallper.size()>0){
                    //???????????????????????????????????????
                        for(int i=0;i<selectedResWallper.size();i++){
                            RESWallper trashFIle = selectedResWallper.get(i);
                            String baseName = trashFIle.getFileObj().getName().getBaseName();
                            String trashFilePath = trashInfoPath+baseName;
                            Opt<LocalFile> trashInfoFile = newLocalFIle(trashFilePath+".trashinfo");
                            try {
                                LocalFile localTrashInfoFile = trashInfoFile.get();
                                if(trashInfoFile.notNull_()&&localTrashInfoFile.exists()&&trashFIle.exists()){
                                    String dataInfo = localTrashInfoFile.getContent().getString("UTF-8");
                                    String url ="";
                                try {
                                    url = localTrashInfoFile.getParent().getPath().toString()+"/"+localTrashInfoFile.getName().getBaseName();
                                } catch (IllegalArgumentException e2) {
                                    e2.printStackTrace();
                                    TaskDialogs.showException(e2);
                                    return ;

                                }

                                    Properties p = new Properties(url, "UTF-8");
                                    String rawPath = p.get("Path");
                                    if (notNull(rawPath)&&rawPath.length()>0) {
                                        //
                                        Opt<LocalFile> newLocalFIle = newLocalFIle(rawPath);
                                        trashFIle.getFileObj().moveTo(newLocalFIle.get());
                                        logInfo("restore file "+localTrashInfoFile.toString());
                                        if (newLocalFIle.get().exists()) {
                                            logInfo("restore file sucess path="+newLocalFIle.get().toString());
                                            //?????????????????????????????????
                                            localTrashInfoFile.delete();



                                        }else{
                                            logInfo("restore file failure path="+newLocalFIle.get().toString());                                             
                                        }
                                    }
                                }
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            
                        }

                }
                
            });
        }
        return restoreButton;
    }

    
        /**
     * @return the operatePanel
     */
    public synchronized JPanel getTrashOperatePanel() {
        if (isNull( trashOperatePanel)) {
            trashOperatePanel=new JPanel();
            
            trashOperatePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            trashOperatePanel.add(getRestoreButton());   
            trashOperatePanel.add(getEmptyTrashButton());   
        }
        return trashOperatePanel;
    }


 


    public void freeMe() {
        rESWallperWithMap.keySet().forEach(dir  ->{
            tabsPanel.getMainFrame().unRegisterWatchDir(rESWallperWithMap.get(dir));
        });
    }


    public void loadIng() {
        getTabHeaderPanel().add(loadIcon);
        randerTabHeaderPanel();
    }
    public void cancelLoadIng() {
        getTabHeaderPanel().remove(loadIcon);
        getTabHeaderPanel().updateUI();
        randerTabHeaderPanel();        
    }
    
    /**
     * @return the searchTextInputPanel
     */
    public synchronized JPanel getSearchTextInputPanel() {
        if (isNull(searchTextInputPanel)) {
            searchTextInputPanel=new JPanel(new MigLayout());
            searchTextInputPanel.add(getIsRegexRaido());
            searchTextInputPanel.add(getSearchTextInputField(),"width 99%");


        }
        return searchTextInputPanel;
    }

    /**
     * @return the searchTextInputField
     */
    public synchronized JTextField getSearchTextInputField() {
        if (isNull(searchTextInputField)) {
            searchTextInputField=new JTextField();
            setLinetBoder(searchTextInputField);
            searchTextInputField.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                    
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode()==10) {
                        //??????????????????
                        str search = str(searchTextInputField.getText());
                        //if (search.trim().notEmpty()&&!lastSearchText.equals(search.to_s())) {
                        if (search.trim().notEmpty()) {
                            if (search.length()<3) {
                                MessageBox.showErrorMessageDialog(centerPane, "??????", "???????????????????????????!");
                                return ;
                            }
                            if (getOnSearchEnterFun().notNull_()) {
                                lastSearchText=search.to_s();
                                //????????????????????????
                                stopSearch(false);
                                getOnSearchEnterFun().get().accept(search.to_s(),getIsRegexRaido().isSelected());
                            }
                        }

                    }else  if (e.getKeyCode()==27) {
                        //esc??????????????????
                        stopSearch(false);
                    }
                    if (e.isControlDown()) {
                        if (e.getKeyCode()==70) {//ctrl+f//??????????????????
                            boolean stopSearch = stopSearch(true);
                            if (stopSearch) {
                                MessageBox.showErrorMessageDialog(null, "??????", "??????????????????!");
                            }
                        }
                        
                    }
                }


                
            });

        }
        return searchTextInputField;
    }
 

    private boolean stopSearch(Boolean switchToFileMode) {
        if (inSearchModel()) {
            JTreeTable nt = getNowShowingTable();
            lastSearchText="";
             boolean stopSearch = nt.stopSearch();
             if (switchToFileMode) {
                nt.switchToFileModel();
            }
            return stopSearch;
        }
        return false;
    }

    String lastSearchText="";

    /**
     * @return the onSearchEnterFun
     */
    public Opt<BiConsumer<String,Boolean>> getOnSearchEnterFun() {
        return onSearchEnterFun;
    }
    
    
    /**
     * @return the uiModel
     */
    public String getUiModel() {
        return uiModel;
    }

    public boolean inSearchModel() {
        return getUiModel().equals(UIModel_SEACH);
    }

    /**
     * @param uiModel the uiModel to set
     */
     void setUiModel(String uiModel) {
        this.uiModel = uiModel;
    }

    JLabel textNameLable=new JLabel();
    JLabel loadIcon=new JLabel(new ImageIcon(Icons.loading));
    
    /**
     * @return the tabHeaderPanel
     */
    public synchronized JPanel getTabHeaderPanel() {
        if (isNull(tabHeaderPanel)) {
            tabHeaderPanel=new JPanel();
            if (notNull(getNowShowingTable())) {
                textNameLable=new JLabel(getNowShowingTable().getRootDir().get().getName());
            }else{
                 textNameLable=new JLabel("Loading");
            }
            tabHeaderPanel.add(textNameLable);
        }
        return tabHeaderPanel;
    }

    public void randerTabHeaderPanel() {
        JTreeTable nowShowingTable2 = getNowShowingTable();
        if (notNull(nowShowingTable2)) {
            textNameLable.setText(nowShowingTable2.getRootDir().get().getName());
        }
        getTabHeaderPanel().updateUI();
    }

    
    /**
     * @return the fileTreeTablePanel
     */
    public FileTreeTablePanel getFileTreeTablePanel() {
        return fileTreeTablePanel;
    }


    /**
     * @return the isRegexRaido
     */
    public synchronized JCheckBox getIsRegexRaido() {
        if (isNull(isRegexRaido)) {
            isRegexRaido=new JCheckBox("Regex");
        }
        return isRegexRaido;
    }
}
