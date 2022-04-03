package github.acodervic.filemanager.model;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.local.LocalFile;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.thread.ThumbnailatorThreadManager;
import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.filemanager.treetable.TreeNameColumnPanel;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;

public class RESWallper implements GuiUtil {
    static ThumbnailatorThreadManager imageIconCacheManager=new ThumbnailatorThreadManager(); 


    
    RESWallper me;
    FileObject res;// filere或者dirres
    String name;
    ImageIcon icon;
    Boolean iconShowDir = false;// 默认icon不在文件夹时候显示
    RESWallperChildsList childs;
    BasicFileAttributes attributes;// 当前文件/夹的属性
    Path path;
    RESWallper parentResWallper;//父资源包装器
    TreeNameColumnPanel treeNameColumnPanel;//显示在表格列的panel
    UserDefinedFileAttributeView userDefinedFileAttributeView;
    Opt<String> userComment;
    Opt<ImageIcon> userIcon;
    Boolean isBookMark=false;
    String  pathMd5;
    Opt<JTreeTable> table=new Opt<>();
    List<Consumer<RESWallper>> onNewChildAddedListener=new ArrayList<>();
    FileName fileName;
    FileType fileType;
    FileContent fileContent;
    String baseName;
    Boolean isFile;
    Date lastModifiedTime;
    Long byteSize;

    public void addOnNewChildAddedListener(Consumer<RESWallper> action) {
        if (!onNewChildAddedListener.contains(action)) {
            onNewChildAddedListener.add(action);
        }
        
    }
    public void removeOnNewChildAddedListener(Consumer<RESWallper> action) {
        onNewChildAddedListener.remove(action);
    }
    
    /**
     * @return the table
     */
    public Opt<JTreeTable> getTable() {
        return table;
    }
 
    public RESWallper() {

    }




    /**
     * @return the pathMd5
     */
    public synchronized String getPathMd5() {
        if (isNull(pathMd5)) {
            pathMd5=new str(getAbsolutePath()).hashMd5();
        }
        return pathMd5;
    }
    /**
     * @param isBookMark the isBookMark to set
     */
    public void setIsBookMark(Boolean isBookMark) {
        this.isBookMark = isBookMark;
    }
    /**
     * @param parentResWallper the parentResWallper to set
     */
    public void setParentResWallper(RESWallper parentResWallper) {
        this.parentResWallper = parentResWallper;
    }

    /**
     * @return the parentResWallper
     */
    public RESWallper getParentResWallper() {
        return parentResWallper;
    }
    public synchronized boolean isFile() {
        if (isNull(isFile)) {
        try {
            isFile= res.isFile();
        } catch (Exception e) {
            e.printStackTrace();
        }   
        }
        return  isFile;
    }

    public boolean isDir() {
        return !isFile();
    }

    /**
     * @param res
     * @param name
     * @param icon
     * @throws FileSystemException
     */
    public RESWallper(FileObject res, String name, ImageIcon icon) throws FileSystemException {
        this.res = res;
        this.fileName=this.res.getName();
        this.fileType=this.res.getType();
        this.fileContent=this.res.getContent();
        this.name = name;
        this.icon = icon;


        if (isNull(this.attributes)) {
            return;
        }
    }


 

    /**
     * @param res
     * @param name
     * @param icon
     * @throws FileSystemException
     */
    public RESWallper(FileObject res, String name) throws FileSystemException {
        this(res, name, null);
    }


        /**
     * @param res
     * @param name
     * @param icon
     * @throws FileSystemException
     */
    public RESWallper(FileObject res) throws FileSystemException {
        this(res, res.getName().toString(), null);
    }



    /**
     * @return the name
     */
    public String getName() {
        if (isNull(me)) {
            me=this;
        }
        if (isDir()&&isLocalFile()&&getAbsolutePathWithoutProtocol().equals("/")) {
            return "Root Path";
        }
        if (!isLocalFile()&&fileName.getPath().equals("/")) {
            URI uri;
            try {
                String retString="";
                uri = new URI( fileName.getURI());
                retString+=(uri.getScheme()+"://");
                String userInfo = uri.getUserInfo();
                if (notNull(userInfo)) {
                    int indexOf =userInfo.indexOf(":");
                    if ( indexOf>0) {
                        retString+=userInfo.substring(0,indexOf);
                    }
                }

                retString+="@"+uri.getHost();
                int port =uri.getPort();
                if (uri.getPort()==-1) {
                    switch (uri.getScheme()) {
                        case "sftp":
                        port=22;
                            break;
                        case "smb":
                        port=445;
                            break;
                        case "ftp":
                        port=21;
                            break;
                        case "tftp":
                        port=21;
                            break;
                        case "webdav":
                        port=443;
                            break;
                        case "jar":

                            break;
                        case "zip":

                            break;
                        case "tar":

                            break;

                        default:
                            break;
                    }
                }else{
                    retString+=":"+port;
                }
                retString+=uri.getPath();
                return retString ;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return fileName.getPath();
        }
        return fileName.getBaseName();
    }


    public synchronized String getBaseName() {
        if (this instanceof SearchRootRESWallper) {
            return "search Root";
        }
        if (isNull(baseName)) {
            if (notNull(fileName.getExtension())&&fileName.getExtension().length()>0) {
                baseName=str(getName()).sub(0,getName().lastIndexOf(".")).toString();
            }else{
                baseName=getName();
            }
        }
        return baseName;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the icon
     */
    public synchronized ImageIcon getIcon() {
        if (isNull(this.icon)) {
            // 尝试
            Opt<ImageIcon> ui = getUserIcon();
            if (ui.notNull_()) {
                this.icon = ui.get();
            } else {

                if (isLocalFile()&&( isImage()||isVideo())) {
                    if (getFileExtName().toLowerCase().equals("gif")) {
                        //直接返回gif图片作为图标
                        this.icon=new ImageIcon(getAbsolutePathWithoutProtocol());
                        icon.setImage(icon.getImage().getScaledInstance(18, 18,Image.SCALE_DEFAULT ));//调整gif大小

                    }else{
                        // 生产略所图任务
                        Opt<ImageIcon> imageIconByResWallper = imageIconCacheManager.getImageIconByResWallper(this);
                        if (imageIconByResWallper.notNull_()) {
                            this.icon = imageIconByResWallper.get();
                        }
                    }

                }
                if (isNull(this.icon)) {
                    // 根据文件类型自动构造icon
                    this.icon = Icons.getIconByFileExtName(getFileExtName());
                    if (isNull(this.icon)) {
                        if (isDir()) {
                            if (isBookMark) {
                                this.icon = Icons.bookmarkFolder;
                            } else {
                                this.icon = Icons.folder;
                            }
                        } else {
                            this.icon = Icons.whiteFile;
                        }
                    }
                }

            }
        }
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public boolean hasChild() {
        return getChildResList().size() > 0;
    }

    public synchronized void  resetChildsRes() {
        childs=null;
        try {
            res.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件和目录 目录在前面 dirEndIndex用于保存当前目录和
     * 
     * @return
     */
    public synchronized List<RESWallper> getChildResList() {
        if (isNull(childs)) {
            childs = new RESWallperChildsList();
            if (isDir()) {
                                
                // dir放在前面
                FileObject[] children = new FileObject[0];
                try {
                    children=res.getChildren();
                } catch (Exception e) {
                    logInfo("getChildResList error"+e.getMessage());
                }


                for (int i = 0; i < children.length; i++) {
                    try {
                        FileObject fileObj = children[i];
                        RESWallper resWallper = new RESWallper(fileObj, fileObj.getName().getBaseName());
                        if (fileObj.isFolder()) {
                            resWallper.setParentResWallper(this);
                            childs.getDirs().add(resWallper);
    
                        }else if(fileObj.isFile()){
                            resWallper.setParentResWallper(this);
                            childs.getFiles().add(resWallper);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sortChilds();
            }
            itemCount=childs.getDirsAndFiles().size();
        }
        return childs.getDirsAndFiles();
    }
    public void sortChilds() {
        getChildResList();
        //根据访问时间排序.最近放的拍在最前面
        childs.getDirs().sort((o1, o2) -> {
            if(isNull(o1)&&notNull( o2)){
                return 1;
            }
            if(isNull(o2)&&notNull(o1 )){
                return 1;
            }
            
            RESWallper r1 = (RESWallper) o1;
            RESWallper r2 = (RESWallper) o2;
            return  r1.getName().compareTo(r2.getName());
        });

        // 根据访问时间排序.最近放的拍在最前面
        childs.getFiles().sort((o1, o2) -> {
            if(isNull(o1)&&notNull( o2)){
                return 1;
            }
            if(isNull(o2)&&notNull(o1 )){
                return 1;
            }
            
            RESWallper r1 = (RESWallper) o1;
            RESWallper r2 = (RESWallper) o2;
            return  r1.getName().compareTo(r2.getName());
        });
        childs.getDirsAndFiles().clear();
        childs.resetDirsAnfFiles();
        System.out.println("adasd");
    }

    public List<RESWallper> getChildFileList() {
        try {
            getChildResList();
            return childs.getFiles();
        } catch (Exception e) {
            System.out.println("12313");
        }
        return new ArrayList<>();
    }


    public List<RESWallper> getChildDirList() {
        getChildResList();
        return childs.getDirs();
    }

 

    @Override
    public String toString() {
        try {
            return res.getName().getBaseName();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return "Ukonw";
    }

 
    public String getFileExtName() {
        if (isFile()) {
            return fileName.getExtension();
        }
        return null;
    }

 

    public  synchronized Date getLastModified() {
        if (isNull(lastModifiedTime)) {
            try {
                lastModifiedTime=new Date(fileContent.getLastModifiedTime());
            } catch (Exception e) {
                logInfo("无法 读取最后修改时间"+e.getMessage());
            }
        }
        return lastModifiedTime;
    }
 
  
    

    /**
     * 是否是链接
     * 
     * @return
     */
    public boolean isSymbolicLink() {
        try {
            return res.isSymbolicLink();
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return  false;
    }

    public synchronized long getByteSize() {
        if (isNull(byteSize)) {
            try {
                byteSize = fileContent.getSize();
            } catch (Exception e) {
                e.printStackTrace();
                byteSize=new Long("-1");
            }
        }
        return byteSize;
    }

    public boolean isExecutable() {
        try {
            return res.isExecutable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isReadable() {
        try {
            return res.isReadable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isWritable() {
        try {
            return res.isWriteable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getOwner() {
        //暂时未实现
        return "ukonw";
    }

    public String getPermissionsDesString() {
        String ret = "";
        //本地文件系统
        if (isLocalFile() && res instanceof LocalFile lf) {
            try {
                Set<PosixFilePermission> posixFilePermissions = Files.getPosixFilePermissions( lf.getPath() );
                // 构建权限字符串
                if (posixFilePermissions.contains(PosixFilePermission.OWNER_READ)) {
                    ret += "r";
                } else {
                    ret += "-";
                }
                if (posixFilePermissions.contains(PosixFilePermission.OWNER_WRITE)) {
                    ret += "w";
                } else {
                    ret += "-";
                }
                if (posixFilePermissions.contains(PosixFilePermission.OWNER_EXECUTE)) {
                    ret += "x";
                } else {
                    ret += "-";
                }
                ret += " ";
                if (posixFilePermissions.contains(PosixFilePermission.GROUP_READ)) {
                    ret += "r";
                } else {
                    ret += "-";
                }
                if (posixFilePermissions.contains(PosixFilePermission.GROUP_WRITE)) {
                    ret += "w";
                } else {
                    ret += "-";
                }
                if (posixFilePermissions.contains(PosixFilePermission.GROUP_EXECUTE)) {
                    ret += "x";
                } else {
                    ret += "-";
                }
                ret += " ";
                if (posixFilePermissions.contains(PosixFilePermission.OTHERS_READ)) {
                    ret += "r";
                } else {
                    ret += "-";
                }
                if (posixFilePermissions.contains(PosixFilePermission.OTHERS_WRITE)) {
                    ret += "w";
                } else {
                    ret += "-";
                }
                if (posixFilePermissions.contains(PosixFilePermission.OTHERS_EXECUTE)) {
                    ret += "x";
                } else {
                    ret += "-";
                }
    
            } catch (Exception e) {
                ret = "unknow";
            }
        }

        return ret;
    }

    public boolean isHidden() {
        try {
           return res.isHidden();
        } catch (Exception e) {
        }
        return false;
    }

    Integer itemCount=null;
    /**
     * 目录下的项目数量
     * 
     * @return
     */
    public synchronized int itemCount() {
        if (isNull(itemCount)) {
            if (isNull(res)) {
                itemCount=0;
            }else{
                if (isDir()) {
                    if (isLocalFile()) {
                        try {
                            itemCount=res.getChildren().length;
                        } catch (Exception e) {
                            itemCount=0;
                        }
                    }else{
                         //如果不是本地文件系统,则尝试异步加载
                         return  -1;//TODO 异步加载 
                    }

                }

            }
        }
        return itemCount;
    }

    /**
     * @return the iconShowDir
     */
    public Boolean getIconShowDir() {
        return iconShowDir;
    }

    /**
     * @param iconShowDir the iconShowDir to set
     */
    public void setIconShowDir(Boolean iconShowDir) {
        this.iconShowDir = iconShowDir;
    }
  
    

    public void xdgOpen() {
          //使用xdg-open打开文件
          try {
            //Runtime.getRuntime().exec(newArray("/usr/bin/xdg-open",getFile().getAbsolutePath()));
        } catch (Exception e) {
          e.printStackTrace();
        }
        
    }

    public Opt<Boolean> delete() {
        Opt<Boolean> ret=new Opt<Boolean>(false);
        try {
            if (res .delete()) {
                if (exists()) {
                    ret.of(false);
                }else{
                        ret.of(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Opt<Boolean> putToTrash() {
        Opt ret = new Opt<>(false);
        try {
            if (!exists()) {
                return ret;
            }
            Runtime.getRuntime().exec(newArray("/usr/bin/trash-put", getAbsolutePathWithoutProtocol()));
            if (exists()) {
            } else {
                // 删除成功
                removeChildFromParent();

                //logInfo("节点:" + res.getFile().getAbsolutePath() + " 已经被删除");
                getTable().ifNotNull_(table  ->{
                    SwingUtilities.invokeLater(() -> {
                        // 要删除这个节点
                        table.fireTreeNodesRemoved(newList(me));
                        table.refreshUi();
                    });
                });


            }
        } catch (Exception e) {
            e.printStackTrace();
            ret.setException(e);
        }
        return ret;
    }


     public void removeChildFromParent() {
         if (isNull(parentResWallper)) {
             throw new RuntimeException("无法删除parentResWallper为null! ");
         }else{
             getParentResWallper().getChildResList().remove(this);
             
             logInfo("从父节点中删除"+getAbsolutePath());
         }
    }

 
    public boolean sameRefObj(RESWallper res) {
        if (res.hashCode()==this.hashCode()) {
            return true;
        }
        return false;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RESWallper resw) {
            return this.getAbsolutePath().equals(resw.getAbsolutePath());
        }
        return false;
    }

 

    public String getAbsolutePath() {
        return  res.toString();
    }

    public String getAbsolutePathWithoutProtocol() {
        try {
            URI uri = this.res.getURI();
            return  uri.getPath();
        } catch (IllegalArgumentException e) {
            //尝试进行url编码返回
            URI uri;
            try {
                uri = new URI(en_strToUrlStr(res.toString()) );
                return  uri.getPath();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 添加子节点
     * @param resWallper
     * @return 成功添加返回true,否则false
     */
    public synchronized  Boolean addChild(RESWallper resWallper) {
        //先判断资源是否存在
        if (getChildResList().contains(resWallper)) {
            logInfo("资源"+resWallper.getAbsolutePath()+"已经存在");
            return false;
        }else{
            resWallper.setParentResWallper(this);
            getChildResList();
            childs.addRes(resWallper);
            for (int i = 0; i < onNewChildAddedListener.size(); i++) {
                Consumer<RESWallper> consumer = onNewChildAddedListener.get(i);
                try {
                    consumer.accept(resWallper);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //进行一次重新排序
            sortChilds();
            System.out.println("插入位置"+childs.getFiles().indexOf(resWallper));
            return true;
        }

    }

    public String getMatchString() {
        return getName();
    }


    /**
     * @return the treeNameColumnPanel
     */
    public synchronized TreeNameColumnPanel getTreeNameColumnPanel() {
        if (isNull(treeNameColumnPanel)) {
            treeNameColumnPanel=new TreeNameColumnPanel(this);
        }
        return treeNameColumnPanel;
    }


    public synchronized void reloadTreeNameColumnPanel () {
        icon=null;
        userComment=null;
        userIcon=null;
        icon=getIcon();
        userComment=getUserComment();
        userIcon=getUserIcon();
        getTreeNameColumnPanel().reloadPanel();
    }

    public void copyToDir(RESWallper dir) {
        
    }


 
    
    public Opt<String> getXattr(String name) {
        Opt<String> ret = new Opt<>();
        try {
            byte[] attribute =  (byte[])Files.getAttribute(path, name);
            if (notNull(attribute)) {
                ret.of(new String(attribute));
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return ret;
    }


    public boolean setXattr(String name,String value) {
        Opt<String> ret = new Opt<>();
        try {
            Files.setAttribute(path, name,  ByteBuffer.wrap((value).getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
        return true;
    }



    public Boolean setUserIcon(String filePath) {
         Boolean set=setXattr("user:cocustIcon",filePath);
        //修改节点面板图标
        if (notNull(treeNameColumnPanel)) {
            ImageIcon iconByFile = Icons.getIconByFile(new File(filePath));
            if (notNull(iconByFile)) {
                getTreeNameColumnPanel().getBaseIconLable().setIcon(iconByFile);
                getTreeNameColumnPanel().updateUI();
            }
        }
         return  set;
    }

    public synchronized Opt<ImageIcon> getUserIcon() {
        if (isNull(userIcon)) {
            userIcon=new Opt<>();
            Opt<String> xattr = getXattr("user:cocustIcon");
            if (xattr.notNull_()) {
                //尝试读取icon
                userIcon.of(Icons.getIconByFile(new File(xattr.get())));
            }
        }
        return  userIcon;
    }

    public synchronized Opt<String> getUserComment() {
        if (isNull(userComment)) {
            userComment=new Opt<>();
            userComment.of(getXattr("user:comment").get());
        }
        return userComment ;
    }

    public Boolean setUserComment(String comment) {
        
        boolean setXattr = setXattr("user:comment",comment);
        userComment=null;//为了重新加载
        return setXattr;
    }

    public void resetIcon() {
        this.icon=null;
        this.userIcon=null;
    }

    public static void main(String[] args) throws IOException {
        Path path2 = new File("/home/w/Music/用户注册.html").toPath();
        String attr="user:cocustIcon";
        byte[]  data="/home/w/mytool/images/bricks.png".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.wrap(data);
        //Files.setAttribute(path2, attr,  ByteBuffer.wrap(data));
        byte[] attribute = (byte[])Files.getAttribute(path2, attr);
        System.out.println(new String(attribute));
    }


    public boolean removeFIleObjFromThis(RESWallper file) {
        return childs.remove(file.getFileObj());
    }

    Boolean isIamge;

    public synchronized boolean isImage() {
        if (isNull(isIamge)) {
            isIamge= str(getFileExtName()).eqAnyIgnoreCase("png","jpg","jpeg","bmp","gif");            
        }
        return isIamge;
    }
 
    Boolean isVideo;
    public synchronized boolean isVideo() {
        if (isNull(isVideo)) {
            isVideo= str(getFileExtName()).eqAnyIgnoreCase("avi","3gp","mp4");            
        }
        return isVideo;
    }
 
    public Opt<RESWallper> getChildResByName(String name,Boolean  isFIle) {
        Opt<RESWallper> ret=new Opt<>();
        if (isFIle) {
            List<RESWallper> childFileList = getChildFileList();
            for (int i = 0; i < childFileList.size(); i++) {
                RESWallper file = childFileList.get(i);
                if (file.getName().equals(name)) {
                    ret.of(file);
                    break;
                }
            }
        }else{
            List<RESWallper> childFileList = getChildDirList();
            for (int i = 0; i < childFileList.size(); i++) {
                RESWallper dir = childFileList.get(i);
                if (dir.getName().equals(name)) {
                    ret.of(dir);
                    break;
                }
            }
        }
        return ret;
    }

    
    /**
     * 代表是否在磁盘存在
     * @return
     */
    public Boolean exists() {
        try {
            return  res.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否是本地文件系统文件
     */
    public boolean isLocalFile() {
        return res instanceof LocalFile;
    }
    

    /**
     * @return the fileContent
     */
    public FileObject getFileObj() {
        return res;
    }


    public Path getNioPath() {
        if (isLocalFile()) {
           try {
            return res.getPath();
           } catch (Exception e) {
               e.printStackTrace();
           }
        }
        return null;
    }

    public LocalFile toLocalFIle() {
        return (LocalFile)res;
    }

    public Opt<RESWallper> getRESWallperByAbsolutePath  (String absolutePath) {
        getChildResList();
        return childs.getRESWallperByAbsolutePath(absolutePath);
    }

    public LocalFile getLocalFIle() {
        return  (LocalFile)res;
    }
    /**
     * @param isFile the isFile to set
     */
    public void setIsFile(Boolean isFile) {
        this.isFile = isFile;
    }
    /**
     * @param lastModifiedTime the lastModifiedTime to set
     */
    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    /**
     * @param byteSize the byteSize to set
     */
    public void setByteSize(Long byteSize) {
        this.byteSize = byteSize;
    }

    public RESWallper cloneRes() throws Exception {
        res.refresh();
            return new RESWallper(res);
    }

    /**
     * 克隆,不复制子节点
     * @param childs the childs to set
     */
    public void setChilds(RESWallperChildsList childs) {
        this.childs = childs;
    }

    /**
     *深度克隆,复制子节点
     * @return
     * @throws Exception
     */
    public RESWallper cloneResDeep() throws Exception {
        RESWallper resWallper = new RESWallper(res);
        setChilds(childs);
        return resWallper;
}

public boolean reName(String newName) throws FileSystemException {
    if (exists()) {
        FileObject resolveFile = getFileObj().getParent().resolveFile(newName);
        if (resolveFile.exists()) {
            throw new  FileSystemException(newName+"已经存在!换一个名称?");
        }else{
            getFileObj().moveTo(resolveFile);
        }
        return true;
    }
    return false;
}

/**
 * @return the childs
 */
public RESWallperChildsList getChilds() {
    return childs;
}

public boolean allowNavigation() {
    if (isDir()) {
        return true;
    }
    if (isLocalFile()) {
        return str(getFileExtName()).toLowerCase().eqAny("jar","zip","tar","gz","xz");
    }
    return false;
}

}
