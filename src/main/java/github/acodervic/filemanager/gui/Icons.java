package github.acodervic.filemanager.gui;

import java.awt.Image;
import java.io.File;
import java.util.HashMap;

import javax.swing.ImageIcon;

import github.acodervic.mod.data.str;
import github.acodervic.mod.io.FileUtil;
import github.acodervic.mod.shell.SystemUtil;

/**
 * 图标
 */
public class Icons {

    public static ImageIcon back=new ImageIcon(Icons.class.getResource("/icons/back.png"));
    public static ImageIcon go=new ImageIcon(Icons.class.getResource("/icons/go.png"));
    public static ImageIcon up=new ImageIcon(Icons.class.getResource("/icons/up.png"));
    public static ImageIcon down=new ImageIcon(Icons.class.getResource("/icons/down.png"));
    public static ImageIcon terminal=new ImageIcon(Icons.class.getResource("/icons/terminal.png"));
    public static ImageIcon newFolder=new ImageIcon(Icons.class.getResource("/icons/create_folder.png"));
    public static ImageIcon userHome=new ImageIcon(Icons.class.getResource("/icons/user-home.png"));
    public static ImageIcon userDesktop=new ImageIcon(Icons.class.getResource("/icons/user-desktop.png"));
    public static ImageIcon docment=new ImageIcon(Icons.class.getResource("/icons/folder-documents.png"));
    public static ImageIcon folderLock=new ImageIcon(Icons.class.getResource("/icons/folder-locked.png"));
    public static ImageIcon folderOpen=new ImageIcon(Icons.class.getResource("/icons/folder-open.png"));
    public static ImageIcon folder=new ImageIcon(Icons.class.getResource("/icons/folder.png"));
    public static ImageIcon whiteFile=new ImageIcon(Icons.class.getResource("/icons/white-file.png"));
    public static ImageIcon refresh=new ImageIcon(Icons.class.getResource("/icons/refresh.png"));
    public static ImageIcon bookmarkFolder=new ImageIcon(Icons.class.getResource("/icons/bookmark-folder.png"));
    public static ImageIcon location=new ImageIcon(Icons.class.getResource("/icons/location.png"));
    public static ImageIcon collapsed=new ImageIcon(Icons.class.getResource("/icons/collapsed.png"));
    public static ImageIcon expanded=new ImageIcon(Icons.class.getResource("/icons/expanded.png"));
    public static Image loading=new ImageIcon(Icons.class.getResource("/icons/loading.gif")).getImage().getScaledInstance(15, 15,Image.SCALE_DEFAULT );
 
    
    static HashMap<String,ImageIcon> cionsMap=new HashMap<>();

    public static String refreshIconName="refresh";
    public static String diskRootIconName="disk";
    public static String hideIconName="disk";
    public static String notHideIconName="disk";
    public static String delete="delete";
    public static String add="add";
    public static String putTrash="put-trash";
    public static String update="update";
    public static String tab="tab";
    public static String icon="icon";
    public static String stop="stop";
    public static String pause="pause";
    public static String resume="resume";
    public static String comment="comment";
    public static String deCompression="de-compression";
    public static String compression="compressionfile";
    public static String script="script";
    public static String uninstall="uninstall";
    public static String vdisk="vdisk";
    public static String vscodium="vscodium";
    public static String chrome="chrome";
    public static String kate="kate";
    public static String openwith="openwith";
    public static String install="install";
    public static String video="video";
    public static String siteBar="sitebar";
    
    
    
    //public static String refreshIconName="home";
    public static ImageIcon getIconByFileExtName (String ext) {
        if (cionsMap.containsKey(ext)) {
            return cionsMap.get(ext);
        }
        String iconFile = "/icons/filetype/"+ext+".png";
        ImageIcon icon=null;
        str extStr = new str(ext);
        if (extStr.eqAnyIgnoreCase("zip","7z","tar","rar","deb","rpm","xz")) {
            iconFile="/icons/archive.png";
        } else if(extStr.eqAnyIgnoreCase("png","jpg","jpeg","svg","icon","gif")){
            iconFile="/icons/image.png";
        }else if(extStr.eqAnyIgnoreCase("mp4","avi","flv","mkv","3gp","mov","wmv","rmvb")){
            iconFile="/icons/video.png";
        }else if(extStr.eqAnyIgnoreCase("mp3","wav","flac","ape","acc","opus","wavpack","alac")){
            iconFile="/icons/music.png";
        }


        try {
            icon=new ImageIcon(Icons.class.getResource(iconFile));
            //转换为16x16
            icon.setImage(icon.getImage().getScaledInstance(18, 18,Image.SCALE_DEFAULT ));//可以用下面三句代码来代替
            cionsMap.put(ext, icon);
        } catch (Exception e) {
            cionsMap.put(ext, null);
        }            
        return  getIconByFileExtName(ext);
    }

    public static  ImageIcon getIconByName(String name) {
        if (cionsMap.containsKey(name)) {
            return cionsMap.get(name);
        }
        String iconFile = "/icons/"+name+".png";
        ImageIcon icon=null;
        try {
            icon=new ImageIcon(Icons.class.getResource(iconFile));
            //转换为16x16
            icon.setImage(icon.getImage().getScaledInstance(18, 18,Image.SCALE_DEFAULT ));//可以用下面三句代码来代替
            cionsMap.put(name, icon);
        } catch (Exception e) {
            cionsMap.put(name, null);
        }            
        return  getIconByName(name);
    }


    /**
     * 从磁盘文件生成图标
     * @param file
     * @return
     */
    public static  ImageIcon getIconByFile(File file) {
        String absolutePath = file.getAbsolutePath();
        if (cionsMap.containsKey(absolutePath)) {
            return cionsMap.get(absolutePath);
        }
         ImageIcon icon=null;
        try {
            icon=new ImageIcon(absolutePath);
            //转换为16x16
            icon.setImage(icon.getImage().getScaledInstance(18, 18,Image.SCALE_DEFAULT ));//可以用下面三句代码来代替
            cionsMap.put(absolutePath, icon);
        } catch (Exception e) {
            System.out.println("读取图标失败!"+absolutePath+"  异常: "+e.getClass().getName()+""+e.getMessage());
            cionsMap.put(absolutePath, null);
        }            
        return  getIconByFile(file);
    }


    
}
