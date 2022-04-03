package github.acodervic.filemanager.gui.popmenus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Icon;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.mod.utilFun;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;

public class Scripts extends MyPopMenuItem {

    DirRes scriptDir = new DirRes("scripts");

    @Override
    public String getName() {
        return "Scripts";
    }

    @Override
    public List<MyPopMenuItem> getSubItems() {
        List<MyPopMenuItem> subPops = new ArrayList<>();
        List<FileRes> scripts = getScripts();
        for (int i = 0; i < scripts.size(); i++) {
            FileRes sctiptFile = scripts.get(i);
            if (sctiptFile.exists() && sctiptFile.canRead()) {
                String ext = sctiptFile.getFileExtensionName().toLowerCase();
                if (str(ext).eqAny("sh", "rb", "py")) {
                    subPops.add(new MyPopMenuItem() {

                        @Override
                        public String getName() {
                            return sctiptFile.getFileName();
                        }

                        @Override
                        public Icon getIcon() {
                            return null;
                        }

                        @Override
                        public String getTip() {
                            return "run " + sctiptFile.getFileName();
                        }

                        @Override
                        public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir,
                                MainPanel mainPanel) {
                            for (int j = 0; j < seletedResWallpers.size(); j++) {
                                RESWallper resWallper = seletedResWallpers.get(j);
                                String scriptAbsolutePath = sctiptFile.getAbsolutePath();
                                String fileAbsolutePath = resWallper.getAbsolutePath();
                                try {
                                    switch (ext) {
                                        case "sh":
                                            // bash
                                            new ProcessBuilder("/usr/bin/bash", scriptAbsolutePath,fileAbsolutePath).start();
                                            break;
                                        case "rb":
                                            // ruby
                                            new ProcessBuilder("/usr/bin/ruby", scriptAbsolutePath,fileAbsolutePath).start();
                                            break;
                                        case "py":
                                            // python
                                                    new ProcessBuilder("/usr/bin/python3",scriptAbsolutePath, fileAbsolutePath).start();
                                            break;
                                        default:
                                            break;
                                    }
                                } catch (Exception e) {
                                    logInfo("启动脚本"+sctiptFile.getFileName()+"对"+fileAbsolutePath+"出错: "+e.getMessage());
                                }
                            }

                        }

                    });

                } else {
                    logInfo("无法加载脚本" + sctiptFile.getAbsolutePath() + "不是有效的扩展后缀!");

                }

            } else {
                logInfo(sctiptFile.getAbsolutePath() + " 不存在或者不可读");
            }

        }
        return subPops;
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.script);
    }

    @Override
    public String getTip() {
        return "run scripts";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {

    }

    public List<FileRes> getScripts() {
        return scriptDir.getFiles();
    }

    public static void main(String[] args) {
        String exec2String = utilFun.exec2String ("python3 /home/w/mytool/systemScripts/sendFileToVps.py  /home/w/Downloads/apache-maven-3.8.4-bin.tar.gz");
        System.out.println("adad");
    }
}
