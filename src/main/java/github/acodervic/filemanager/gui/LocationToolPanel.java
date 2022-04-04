package github.acodervic.filemanager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;


import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.local.LocalFile;
import org.apache.commons.vfs2.provider.sftp.BytesIdentityInfo;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.oxbow.swingbits.dialog.task.TaskDialogs;

import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.thread.PublicThreadPool;
import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.Value;
import github.acodervic.mod.net.HttpUtil;
import github.acodervic.mod.swing.MessageBox;
import github.acodervic.mod.thread.AsyncTaskRunnable;
import net.miginfocom.swing.MigLayout;

/**
 * 代表顶部的地址面板,有i两种格式textFiled模式和button模式
 */
public class LocationToolPanel extends MainFramePanel {
    JComboBox<String> locationEditableCombox;
    JPanel locationButtonGroupPanel;
    JButton  backToParentDirButton;
    JButton  goToChildDirButton;
    RESWallper nowLocationDir;//当前位置的目录
    Boolean isButtonGroupModel=true;

    public LocationToolPanel(MainFrame mainFrame) {
        super(mainFrame,false,true);
    }


    /**
     * @return the backToParentDirButton
     */
    public synchronized JButton getBackToParentDirButton() {
        if (isNull(backToParentDirButton)) {
            backToParentDirButton=new JButton(Icons.back);
            backToParentDirButton.setPreferredSize(new Dimension(25, 35));
            addHoverColorToCommpoent(backToParentDirButton);
            onClick(backToParentDirButton, e ->{
                //返回到上级目录
                mainFrame.getMainPanel().getCenterTabsPanel().getNowSeletedTabPanel().get().back();
            });
            backToParentDirButton.setPreferredSize(new Dimension(25, 35));
            backToParentDirButton.setBorder(null);
        }
        return backToParentDirButton;
    }


    /**
     * @return the goToChildDirButton
     */
    public synchronized JButton getGoToChildDirButton() {
        if (isNull(goToChildDirButton)) {
            goToChildDirButton=new JButton(Icons.go);
            addHoverColorToCommpoent(goToChildDirButton);
            onClick(goToChildDirButton, e ->{
                //返回到上级目录
                if (notNull(nowLocationDir)) {
                    try {
                        FileObject dirRes = nowLocationDir.getFileObj().getParent();
                        mainFrame.getMainPanel().getCenterTabsPanel().getNowSeletedTabPanel().get().navigationToDIr(new RESWallper(dirRes), true);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
            goToChildDirButton.setPreferredSize(new Dimension(25, 35));
            goToChildDirButton.setBorder(null);
        }
        return goToChildDirButton;
    }
    /**
     * @return the locationEditableCombox
     */
    public synchronized JComboBox getLocationEditableCombox() {
        if (isNull(locationEditableCombox)) {
            locationEditableCombox=new JComboBox<>();
            locationEditableCombox.setEditable(true);
            Component editorComponent = locationEditableCombox.getEditor().getEditorComponent();
            if(editorComponent instanceof JTextField text){
                text.setBorder(BorderFactory.createLineBorder(Color.gray));
                //绑定回车导航到文件夹
                text.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        PublicThreadPool.FilePorcessPool_Exec("LocationToolPanel_KeyAdapter", ()  ->{
                            try {
                                if (e.getKeyCode()==10) {//回车进入目录
                                    String t =text.getText();
                                    if (t.endsWith(" ")) {
                                        t=t.substring(0, t.length()-1);
                                    }
                                    if (t.endsWith("\n")) {
                                        t=t.substring(0, t.length()-1);
                                    }
                                    text.setText(t);
                                    if (t.startsWith("file://")||t.startsWith("/")) {
                                        FileObject f=newLocalFIle(t).get();
                                        if (!f.exists()) {
                                            text.setBorder(BorderFactory.createLineBorder(Color.red));
                                            return ;
                                        }
                                        mainFrame.getMainPanel().getCenterTabsPanel().getNowSeletedTabPanel().ifNotNull_(tab ->{
                                            try {
                                                if (f.isFile()) {
                                                    tab.navigationToDIr(new RESWallper(f.getParent()), true);                                    
                                                    //选中文件,并滚动到文件
                                                    JTreeTable nowShowingTable = tab.getNowShowingTable();
                                                    Opt<RESWallper> searchNodeByFile = nowShowingTable.getTreeFileSystemModel().searchNodeByFile(new RESWallper(f), false);
                                                    SwingUtilities.invokeLater(()  ->{
                                                        if (searchNodeByFile.notNull_()) {
                                                            RESWallper resWallper = searchNodeByFile.get();
                                                            //选中并滚动
                                                            nowShowingTable.setSelectedResWallpers(newList(resWallper));
                                                            nowShowingTable.scrollToRes(resWallper );
                                                            //请求焦点
                                                            nowShowingTable.requestFocus();
                                                        }
                                                    });
                                                }else if(f.isFolder()){
                                                    tab.navigationToDIr(new RESWallper(f), true);
                                                    
                                                }
                                            } catch (FileSystemException e1) {
                                                e1.printStackTrace();
                                            }
                                
                                        });
                                        return ;
                                    }  
                                    
                                    URI uri = new URI(t);
                                    String scheme = uri.getScheme().toLowerCase().toLowerCase();
                                    String path = uri.getPath();
                                    String user = uri.getUserInfo();
                                    int port = uri.getPort();
                                    String host = uri.getHost();
                                    String password=null;
                                    int index=user.indexOf(":");
                                    if (index>0) {
                                        password=user.substring(index+1);
                                        user=user.substring(0,index);
                                    }
    
                                    
                                    if (path.trim().length()==0) {
                                        path="/";
                                    }
                                    //判断协议
                                     if (scheme.equals("sftp")) {
                                        if (port == -1) {
                                            port = 22;
                                        }
                                        Value portVal = new Value(port);
                                        Value pathVal = new Value(path);
                                        Value passwordVal = new Value(password);
                                        Value userVal = new Value(user);
                                        Opt<RESWallper> startFTP = new Opt();
                                        AsyncTaskRunnable task = new AsyncTaskRunnable(() -> {
                                            try {
                                                Opt<RESWallper> ftpFIle = startFTP(userVal.getString(), passwordVal.getString(), host, portVal.getInt(),
                                                        pathVal.getString());
                                                startFTP.of(ftpFIle.get());
                                                        startFTP.setException(ftpFIle.getException());
                                            } catch (MalformedURLException e1) {
                                                e1.printStackTrace();
                                            }
                                        });
                                        PublicThreadPool.FilePorcessPool_Exec("t", () -> {
                                            task.executeFun();
                                        });
                                        sleep(100);
                                        task.syncWaitFinish(10, TimeUnit.SECONDS);//10s后超时
                                        if (startFTP.notNull_()) {
                                            RESWallper dir = startFTP.get();
                                            if (dir.exists()) {
                                                mainFrame.getMainPanel().getCenterTabsPanel().addFilesTableTab(dir, true);
                                            }else{
                                                MessageBox.showErrorMessageDialog(null, "打开sftp错误", dir.getBaseName()+"目录不存在!");
                                                getLocationEditableCombox().requestFocus();
                                            }
                                        }else{
                                            if (isNull(startFTP.getException())) {
                                                startFTP.setException(new Exception("尝试连接"+uri.toString()+"超时!"));
                                            }
                                            TaskDialogs.showException(startFTP.getException());
                                        }


 

                                        
                                    }else if (scheme.equals("smb")) {
                                        if (port==-1) {
                                            port=445;
                                        }
                                    }else if (scheme.equals("ftp")) {
                                        if (port==-1) {
                                            port=21;
                                        }
                                    }else if (scheme.equals("tftp")) {
                                        if (port==-1) {
                                            port=21;
                                        }
                                    }else if (scheme.equals("jar")) {
                                        
                                    }else if (scheme.equals("zip")) {
                                        
                                    }else if (scheme.equals("tar")) {
                                        
                                    }
    
                                 }else if (e.getKeyCode()==27) {//esc推出编辑模式
                                    text.setText( nowLocationDir.toString());
                                    SwingUtilities.invokeLater(()  ->{
                                        changeToButtonGroupModel();
                                       updateUI();
                                    });
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                });
            }
        }
        return locationEditableCombox;
    }

    /**
     * 设置路径地址
     * @param dir
     */
    public synchronized void setLocationDir(RESWallper dir) {
        if (notNull(dir)){
            if (notNull(nowLocationDir)) {
                if (dir.toString().equals(nowLocationDir.toString())) {
                    //相同的目标路径不处理了
                    return  ;
                }
                
            }
            nowLocationDir=dir;
            //构造编辑框
            getLocationEditableCombox().removeAllItems();
            getLocationEditableCombox().addItem(dir.toString());
            getLocationEditableCombox().setSelectedIndex(0);
            //构造按钮组
            generationLocationCombox(dir.getFileObj());
            generationLocationButtonGroup(dir.getFileObj());
            changeToButtonGroupModel();
        }
    }


    public void changeToButtonGroupModel() {
        isButtonGroupModel=true;
        remove(getLocationEditableCombox());
        add(getLocationButtonGroupPanel(),BorderLayout.CENTER);
        //requestFIleTreeTable();
    }


    public void changeToLocationEditModel() {
        isButtonGroupModel=false;
        remove(getLocationButtonGroupPanel());
        add(getLocationEditableCombox(),BorderLayout.CENTER);
        //requestFIleTreeTable();
    }

    public void requestFIleTreeTable() {
        try {
            mainFrame.getMainPanel().getCenterTabsPanel().getNowSeletedTabPanel().ifNotNull_(tab  ->{
                tab.getNowShowingTable().requestFocus();
            });
        } catch (Exception e) {
        }
    }    

    /**
     * @return the isButtonGroupModel
     */
    public Boolean isButtonGroupModel() {
        return isButtonGroupModel;
    }

    

    @Override
    public void initGui() {
            setLayout(new BorderLayout());
            //changeToButtonGroupModel();            
            changeToLocationEditModel();
    }


    /**
     * @return the locationButtonGroupPanel
     */
    public synchronized JPanel getLocationButtonGroupPanel() {
        if (isNull(locationButtonGroupPanel)) {
            locationButtonGroupPanel=new JPanel(new MigLayout("gap -1px"));//每个按钮紧贴
        }
        return locationButtonGroupPanel;
    }


    public void generationLocationButtonGroup(FileObject dirRes) {
        JPanel panel = getLocationButtonGroupPanel();
        panel.removeAll();
        List<JButton>  buttons=new ArrayList<>();
        buttons.add(getGoToChildDirButton());
        JButton nowDirButton = newButtonByDir(dirRes);
        MatteBorder createMatteBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.white);
        
        nowDirButton.setBorder(createMatteBorder);
        buttons.add(nowDirButton);
        FileObject nowProcessDir=dirRes;
        while (true) {
            try {
                FileObject parentDir = nowProcessDir.getParent();
                if (parentDir==null) {
                    break;
                }else{
                    //生成一个按钮
                    FileObject targetDir = parentDir;
                    nowProcessDir = targetDir;
                    JButton newButtonByDir = newButtonByDir(nowProcessDir);
                    buttons.add(newButtonByDir);
                    onClick(newButtonByDir,e  ->{
                        try {
                            mainFrame.getMainPanel().getCenterTabsPanel().getNowSeletedTabPanel().get().navigationToDIr(new RESWallper(targetDir), true);
                        } catch (FileSystemException e1) {
                            e1.printStackTrace();
                            TaskDialogs.showException(e1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        buttons.add(getBackToParentDirButton());
        //反方向添加
        for (int i = (buttons.size()-1); i >0; i--) {
            JButton button = buttons.get(i);
            getLocationButtonGroupPanel().add(button);
        }
        updateUI();

    }

 

    public void generationLocationCombox(FileObject dirRes) {
        getLocationEditableCombox().removeAllItems();
        getLocationEditableCombox().addItem(dirRes.toString());

    }
    public JButton newButtonByDir(FileObject dir) {
        String dirName = dir.getName().getBaseName()+"";
        int wid =3;
        if (dir instanceof LocalFile) {
            if (!dirName.trim().equals("/")) {
                dirName += "/";
                wid = dirName.length() * 10;
            }
        }else{
            try {
                if (dir.getFileSystem().getRoot().toString().equals(dir.toString())) {
                    dirName=" "+new RESWallper(dir).getName()+"  ";

                }else{
                    dirName += "/  ";
                }

            } catch (Exception e) {
            }
             
        }
        JButton locationButton=new JButton(dirName);
        locationButton.setPreferredSize(new Dimension(wid, 35));
        locationButton.setBorder(null);
        addHoverColorToCommpoent(locationButton);

        return locationButton;
    }



    public static Opt<RESWallper> startFTP(String user,String password,String host,Integer port,String path) throws MalformedURLException {
        Opt<RESWallper> m=new Opt<>();

        StandardFileSystemManager manager = new StandardFileSystemManager();
    
        try {
    
            // props.load(new FileInputStream("properties/" +
            // propertiesFilename));
 
    
            // check if the file exists
            String filepath = "/";
            File file = new File(filepath);
            if (!file.exists())
                throw new RuntimeException("Error. Local file not found");
    
            // Initializes the file manager
            manager.init();
    
            // Setup our SFTP configuration
            FileSystemOptions opts = new FileSystemOptions();
            SftpFileSystemConfigBuilder instance = SftpFileSystemConfigBuilder.getInstance();
            instance.setStrictHostKeyChecking(opts, "no");
            instance.setUserDirIsRoot(opts, true);
            //instance.setTimeout(opts, 10000);
            instance.setConnectTimeout(opts, Duration.ofMillis(1000) );//10s超时
    
            // Create the SFTP URI using the host name, userid, password, remote
            // path and file name
            //String sftpUri = "sftp://" + user+":"+password + "@" + host + ":"+port+path;
            String pass="";
            if (password!=null) {
                pass=":"+password;
            }
            String sftpUri = "sftp://" + user+pass+ "@" + host + ":"+port+path;
    
            // Create local file object
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());
    
            // Create remote file object
            FileObject remoteFile = manager.resolveFile(sftpUri, opts);
            m.of(new RESWallper(remoteFile));

            // Copy local file to sftp server
            //remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
            System.out.println("File upload successful");
    
        } catch (Exception ex) {
            ex.printStackTrace();
            m.setException(ex);            
        }  
    
        return m;
    }

    public static void main(String[] args) throws Exception {
        //boolean startFTP = startFTP("root", "abc147268." , "122.114.250.153", 22, "/");
        java.net.URI uri = new java.net.URI("sss://w@adasd/adasd");
        URL parseUrl = HttpUtil.parseUrl("sss://w@adasd/adasd");
        System.out.println(parseUrl);
    }
    
}
