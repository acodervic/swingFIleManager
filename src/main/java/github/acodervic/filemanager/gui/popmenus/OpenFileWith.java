package github.acodervic.filemanager.gui.popmenus;

import java.util.List;

import javax.swing.Icon;

import github.acodervic.filemanager.config.XdgOpenConfigManager;
import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;

public class OpenFileWith  extends MyPopMenuItem {
 
    XdgOpenConfigManager xdgOpenConfigManager=new XdgOpenConfigManager();  

    @Override
    public List<MyPopMenuItem> getSubItems() {
        return newList(new  MyPopMenuItem() {

            @Override
            public String getName() {
                return "Codium";
            }

            @Override
            public Icon getIcon() {
                return Icons.getIconByName(Icons.vscodium);
            }

            @Override
            public String getTip() {
                return "open with codium";
            }

            @Override
            public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                 for (int i = 0; i < seletedResWallpers.size(); i++) {
                    try {
                        xdgOpenConfigManager.getCodium().getAction().get().accept(seletedResWallpers.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                 }
            }
            
        },new  MyPopMenuItem() {
            @Override
            public String getName() {
                return "Onlyoffice";
            }

            @Override
            public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                return  seletedResWallpers.get(0).isFile();//只对文件显示
            }
            @Override
            public Icon getIcon() {
                return null;
            }

            @Override
            public String getTip() {
                return null;
            }

            @Override
            public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                for (int i = 0; i < seletedResWallpers.size(); i++) {
                    try {
                        xdgOpenConfigManager.getOnlyoffice().getAction().get().accept(seletedResWallpers.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                 }
            }
            
        },new  MyPopMenuItem() {

            @Override
            public String getName() {
                return "Google Chrome";
            }

            @Override
            public Icon getIcon() {
                return Icons.getIconByName(Icons.chrome);
            }

            @Override
            public String getTip() {
                return null;
            }
            @Override
            public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                return  seletedResWallpers.get(0).isFile();//只对文件显示
            }
            @Override
            public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                for (int i = 0; i < seletedResWallpers.size(); i++) {
                    try {
                        xdgOpenConfigManager.getChrome().getAction().get().accept(seletedResWallpers.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                 }
            }
            
        },new  MyPopMenuItem() {
            @Override
            public String getName() {
                return "Kate";
            }

            @Override
            public Icon getIcon() {
                return Icons.getIconByName(Icons.kate);
            }

            @Override
            public String getTip() {
                return null;
            }

            @Override
            public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                for (int i = 0; i < seletedResWallpers.size(); i++) {
                    try {
                        xdgOpenConfigManager.getKate().getAction().get().accept(seletedResWallpers.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                 }
            }

            @Override
            public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                return  seletedResWallpers.get(0).isFile();//只对文件显示
            }
            
        },new  MyPopMenuItem() {

            @Override
            public String getName() {
                return "Dpkg";
            }

            @Override
            public Icon getIcon() {
                return Icons.getIconByName(Icons.install);
            }

            @Override
            public String getTip() {
                return null;
            }


            @Override
            public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                RESWallper resWallper = seletedResWallpers.get(0);
                return  resWallper.isFile()&&resWallper.getFileExtName().toLowerCase().equals("deb");//只对文件显示
            }

            @Override
            public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                for (int i = 0; i < seletedResWallpers.size(); i++) {
                    try {
                        xdgOpenConfigManager.getInstalldeb().getAction().get().accept(seletedResWallpers.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                 }
            }
            
        },new  MyPopMenuItem() {

            @Override
            public String getName() {
                return "Video(totem)";
            }

            @Override
            public Icon getIcon() {
                return Icons.getIconByName(Icons.video);
            }

            @Override
            public String getTip() {
                return null;
            }

            @Override
            public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                RESWallper resWallper = seletedResWallpers.get(0);
                return  resWallper.isFile()&&str(resWallper.getFileExtName().toLowerCase()).eqAny("mp4","avi","3gp");//只对文件显示
            }
            @Override
            public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                for (int i = 0; i < seletedResWallpers.size(); i++) {
                    try {
                        xdgOpenConfigManager.getVideo_totem().getAction().get().accept(seletedResWallpers.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                 }
            }
            
        },new  MyPopMenuItem() {
            @Override
            public String getName() {
                return "FileRoller";
            }

            @Override
            public Icon getIcon() {
                return Icons.getIconByName(Icons.deCompression);
            }

            @Override
            public String getTip() {
                return null;
            }

            @Override
            public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                for (int i = 0; i < seletedResWallpers.size(); i++) {
                    try {
                        xdgOpenConfigManager.getFile_roller().getAction().get().accept(seletedResWallpers.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                 }
            }
            @Override
            public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                RESWallper resWallper = seletedResWallpers.get(0);
                return  resWallper.isFile()&&str(resWallper.getFileExtName().toLowerCase()).eqAny("deb","zip","jar","tar","7z","rar","xz","apk");//只对文件显示
            }
        });
    }

    @Override
    public String getName() {
        return "Open With..";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.openwith);
    }

    @Override
    public String getTip() {
        return "open file  with";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers,RESWallper nowDir,MainPanel mainPanel) {
        for (int i = 0; i < seletedResWallpers.size(); i++) {
            RESWallper resWallper = seletedResWallpers.get(i);
            xdgOpenConfigManager.open(resWallper);
        }
    }

    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        seletedResWallpers.removeIf(res  ->{
            return res.isDir(); 
        });
        return seletedResWallpers.size()>0;
    }
    
}
