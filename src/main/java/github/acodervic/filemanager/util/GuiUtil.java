package github.acodervic.filemanager.util;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.local.LocalFile;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.mod.UtilFunInter;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.swing.MyComponent;

public interface GuiUtil extends UtilFunInter {
    Color hoverColor = Color.blue;

    /**
     * @return the localFileSystemManager
     */
    public  static     FileSystemManager getLocalFileSystemManager() {
            try {
                return VFS.getManager();//目标文件系统类型
            } catch (Exception e) {
                
            }
        return null;//
    }

    public default JButton newIconToggleButton(Icon icon) {
        JButton toggleButton = new JButton(icon);
        addHoverColorToCommpoent(toggleButton);
        return toggleButton;
    }

    public default JButton newIconToggleButtonName(String name) {
        JButton toggleButton = new JButton(Icons.getIconByName(name));
        addHoverColorToCommpoent(toggleButton);
        return toggleButton;
    }


    public default JButton newIconToggleButtonText(String name) {
        JButton toggleButton = new JButton(name);
        addHoverColorToCommpoent(toggleButton);
        setLinetBoder(toggleButton);
        return toggleButton;
    }

    public default void onClick(JComponent button, Consumer consumer) {
        new MyComponent<>(button).onClick(consumer);
    }

    public default void addHoverColorToCommpoent(JComponent component) {
        Color background = component.getBackground();
        MouseAdapter mp = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                component.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                component.setBackground(background);
            }

        };
        component.addMouseListener(mp);
    }

    /**
     * 把文本设置到剪贴板（复制）
     */
    public default void setClipboardString(String text) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装文本内容
        Transferable trans = new StringSelection(text);
        // 把文本内容设置到系统剪贴板
        clipboard.setContents(trans, null);
    }

    /**
     * 从剪贴板中获取文本（粘贴）
     */
    public default String getClipboardString() {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // 获取剪贴板中的内容
        Transferable trans = clipboard.getContents(null);

        if (trans != null) {
            // 判断剪贴板中的内容是否支持文本
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    // 获取剪贴板中的文本内容
                    String text = (String) trans.getTransferData(DataFlavor.stringFlavor);
                    return text;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 计算两个数字的百分比,返回0-100的int
     */
    public default int calc(int a, int b) {
        String result =  github.acodervic.mod.data.NumberUtil.percentage(a, b, 0);
        return Integer.parseInt(result);
    }

    public default void delete(File dirOrFile) {
        if (dirOrFile.isDirectory()) {
            Path rootPath = Paths.get(dirOrFile.getAbsolutePath());

            try {
                try (Stream<Path> walk = Files.walk(rootPath)) {
                    walk.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .peek(System.out::println)
                            .forEach(File::delete);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
				Files.deleteIfExists(dirOrFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

    }


    public default void move(File dirOrFile, File distDirOrFile) {
        if (distDirOrFile.isDirectory()) {
            Path distFilePath = new DirRes(dirOrFile).newFile(dirOrFile.getName()).toPath();
            try {
                Files.move(Paths.get(dirOrFile.toURI()), distFilePath, StandardCopyOption.COPY_ATTRIBUTES,
                StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
             //复制文件
            try {
                Files.move(Paths.get(dirOrFile.toURI()), distDirOrFile.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public default String getUserHome() {
        return System.getProperty("user.home");
    }
    public default String getUser() {
        return System.getProperty("user.name");
    }

    public default String getUserDIr() {
        return getUserDIrStatic();
    }

    public static String getUserDIrStatic() {
        return System.getProperty("user.dir");
    }

    public default Opt<LocalFile> newLocalFIle(String path) {
        return newLocalFIleStatic(path);
    }
    public default Opt<FileObject> newVFS2FIle(String path) {
        return newVFS2FIleStatic(path);
    }

    public static  Opt<LocalFile> newLocalFIleStatic(String path) {
        Opt<LocalFile> ret=new Opt<>();
        try {
            if (path.startsWith("file://")) {
                path=path.replace("file://", "");
            }
            ret.of((LocalFile)getLocalFileSystemManager().resolveFile(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
          return ret;
    }

    public static  Opt<FileObject> newVFS2FIleStatic(String path) {
        Opt<FileObject> ret=new Opt<>();
        try {
            if (path.startsWith("file://")) {
                path=path.replace("file://", "");
            }
            ret.of(getLocalFileSystemManager().resolveFile(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
          return ret;
    }
    public default void setLinetBoder(JComponent com) {
        com.setBorder(BorderFactory.createLineBorder(Color.gray));
    }
}
