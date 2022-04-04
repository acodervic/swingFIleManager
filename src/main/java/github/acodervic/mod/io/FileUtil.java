package github.acodervic.mod.io;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent.Kind;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import github.acodervic.mod.Constant;
import github.acodervic.mod.utilFun;
import github.acodervic.mod.data.str;
import github.acodervic.mod.shell.SystemUtil;

/**
 * FileUtil,用来进行文件的基本操作,部分函数基础与common-io
 */
public class FileUtil {

    /**
     * 新建一个file对象,可以使用绝对路径,也可以使用相对路径
     * 
     * @param path 文件路径
     * @return
     */
    public static File newFile(String path) {
        nullCheck(path);
        // 判断是相对路径还是绝对路径
        // 如果是相对路径,则自动拼接为绝对路径
        // 绝对路径
        if (FileSystems.getDefault().getPath(path).isAbsolute()) {
            return new File(path);
        } else {
            // 相对路径
            return new File(SystemUtil.getMyDir() + File.separator + path);
        }
    }

    /**
     * 获取文件/夹名后缀名(.a=a 不包含.) 当没有文件扩展名的时候返回空字符串
     * 
     * @param fileOrDir 文件/夹
     * @return 返回扩展名
     */
    public static String getExtensionName(File fileOrDir) {
        nullCheck(fileOrDir);
        return FileNameUtil.extName(fileOrDir);
    }

    /**
     * 获取文件/夹名后缀名 当没有文件扩展名的时候返回空字符串
     * 
     * @param url 文件/夹
     * @return 返回扩展名
     */
    public static String getExtensionName(URL url) {
        nullCheck(url);
        String extension = FileNameUtil.extName(url.toString());
        int indexOf = extension.indexOf("?");
        if (indexOf!=-1) {
            //截取
            return extension.substring(0, indexOf);
        }
        return extension;
    }

    /**
     * 获取文件/夹名,不包含后缀
     *
     * @param fileOrDir 文件/夹
     * @return 文件名,不包含后缀
     */
    public static String getBaseName(File fileOrDir) {
        nullCheck(fileOrDir);
        if (fileOrDir.toString().equals("/")) {
            return "/";
        }
        return FileNameUtil.mainName(fileOrDir.toString());
    }

    /**
     * 获取文件/夹名,不包含后缀
     * 
     * @param url 文件/夹
     * @return 文件/夹名,不包含后缀
     */
    public static String getBaseName(URL url) {
        nullCheck(url);
        return FileNameUtil.mainName(url.getFile());
    }

    /**
     * 获取文件/夹名,包含后缀
     * 
     * @param url 文件/夹
     * @return 文件/夹名,包含后缀
     */
    public static String getName(URL url) {
        nullCheck(url);
        return FileNameUtil.getName(url.getFile());
    }

    /**
     * 获取文件/夹名,包含后缀
     * 
     * @param fileOrDir 文件/夹
     * @return 文件/夹名,包含后缀
     */
    public static String getName(File fileOrDir) {
        nullCheck(fileOrDir);
        return FileNameUtil.getName(fileOrDir.toString());
    }

    /**
     * 读取文件/夹所在的目录
     * 
     * @param fileOrDir 文件/夹
     * @return 文件/夹所在的目录
     */
    public static String getFullPath(File fileOrDir) {
        nullCheck(fileOrDir);
        return fileOrDir.getParentFile().toString();
    }

    /**
     * 读取文件/夹所在的目录
     *
     * @param DirRes 文件/夹
     * @return 文件/夹名所在的目录
     */
    public static String getFullPath(URL url) {
        // return cn.hutool.core.io.FileUtil.getpa
        return "";
    }

    /**
     * 从路径中读取目录名
     *
     * @param fileOrDir 文件/夹名
     * @return 目录名
     */
    public static String getDirName(File fileOrDir) {
        nullCheck(fileOrDir);
        return cn.hutool.core.io.FileUtil.getName(fileOrDir.getParentFile());
    }

    /**
     * 从路径中读取目录名
     *
     * @param fileOrDir 文件/夹名
     * @return 目录名
     */
    public static String getDirName(URL url) {
        nullCheck(url);
        return cn.hutool.core.io.FileUtil.getName(url.getFile());
    }

    /**
     * 从路径中文件夹路径,如果路径以/结尾则返回自身
     * 
     * @param fileOrDir 文件/夹名
     * @return 目录名
     */
    public static URL getUrlDirPath(URL url) {
        nullCheck(url);
        str path = new str(url.getPath());
        path.ifEmptySetString("/");
        if (path.eq("/")) {
            return url;
        }
        // 提取目录路径
        str dirPath = path.sub(0, path.lastIndexOf("/"));
        String newurl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + dirPath;
        return utilFun.parseUrl(newurl);
    }


    /**
     * 判断文件是否匹配后缀,如果匹配成功则返回true,不区分大小写
     *
     * @param file      文件
     * @param extension 扩展名
     * @return 成功则返回true
     */
    public static boolean isExtension(File file, String extension) {
        nullCheck(file, extension);
        return FileNameUtil.isType(getName(file), extension);
    }


    /**
     * 判断url是否匹配后缀,如果匹配成功则返回true,不区分大小写
     *
     * @param DirRes    文件
     * @param extension 扩展名
     * @return 成功则返回true
     */
    public static boolean isExtension(String url, String... extension) {
        nullCheck(url, extension);
        String urlExt = FileNameUtil.extName(utilFun.parseUrl(url).getPath()).toLowerCase();
        for (int i = 0; i < extension.length; i++) {
            if (extension[i].toLowerCase().equals(urlExt)) {
                return true;
            }

        }
        return false;
    }

    /**
     * 判断文件名是否匹配后缀 数组,如果包含一个则返回true,不区分大小写
     *
     * @param file      文件
     * @param extension 扩展名
     * @return true成功false失败
     */
    public static boolean isExtension(File file, String... extension) {
        nullCheck(file, extension);
        return FileNameUtil.isType(getName(file), extension);
    }



    /**
     * 复制磁盘文件到目标磁盘文件
     * 
     * @param srcFile
     * @param destFile
     * @return 成功true
     */
    public static boolean copyFileTo(File srcFile, File destFile) {
        nullCheck(srcFile, destFile);
        try {
            cn.hutool.core.io.FileUtil.copyFile(srcFile, destFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制磁盘文件到某个目录
     * 
     * @param srcFile 源文件
     * @param destDir 文件目录
     * @return true成功false失败
     */
    public static boolean copyFileToDirectory(File srcFile, File destDir) {
        nullCheck(srcFile, destDir);
        try {
            cn.hutool.core.io.FileUtil.copyFile(srcFile, destDir);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取远程url资源的数据流，可以是一个文件,也可以是一堆字符
     * 
     * @param uri 远程url地址
     * @return 返回读取到的字节流，如果读取异常则返回null
     */
    public static byte[] getURIFileArrayBytes(URL uri) {
        nullCheck(uri);
        try {
            InputStream in = uri.openStream();
            return IoUtil.readBytes(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 删除磁盘文件/夹
     * 
     * @param srcFileOrDir 文件
     * @return true成功false失败
     */
    public static boolean deleteFileOrDir(File srcFileOrDir) {
        try {
            nullCheck(srcFileOrDir);
            if (srcFileOrDir.isDirectory()) {
                Constant.dbg("删除文件夹" + srcFileOrDir.getPath());
                cn.hutool.core.io.FileUtil.del(srcFileOrDir);
                return true;

            } else {
                Constant.dbg("删除文件" + srcFileOrDir.getPath());
                cn.hutool.core.io.FileUtil.del(srcFileOrDir);
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 移动文件或者目录,移动前后文件完全一样,如果目标文件夹不存在则创建。如果已经存在则会覆盖
     * 
     * @param resFileOrDir 源文件路径
     * @param distDir      目标文件夹
     * @return true成功false失败
     */
    public static boolean moveFileOrDir(File resFileOrDir, File distDir) throws IOException {
        try {
            if (resFileOrDir.isDirectory()) {
                cn.hutool.core.io.FileUtil.move(resFileOrDir, distDir, true);
            } else if (resFileOrDir.isFile()) {
                cn.hutool.core.io.FileUtil.move(resFileOrDir, distDir, true);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 创建空文件夹,如果文件夹已经存在则返回false,如果不存在则创建文件夹,成功返回true不成功则返回false
     * 
     * @param dir 目录
     * @return true成功false失败
     */
    public static boolean createDir(File dir) {
        nullCheck(dir);
        if (dir.exists()) {
            Constant.dbg("文件" + dir.getPath() + "已经存在,放弃创建!!");
            return false;
        }
        try {
            dir.mkdir();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建一个文件,Override参数决定是否进行覆盖,如果文件真实被创建成功则返回true,其它情况一律返回false
     * 在写入之前确保文件的中父文件夹是存在的,否则无法创建文件并返回false
     * 
     * @param file     被写入的文件对象
     * @param Override 是否覆盖磁盘已有文件
     * @return
     */
    public static boolean createFile(File file, boolean override) {
        nullCheck(file);
        try {
            if (override) {
                if (file.exists()) {
                    // 删除原来的
                    if (deleteFileOrDir(file)) {
                        return file.createNewFile();
                    } else {
                        Constant.dbg("无法删除u原有文件" + file.getPath() + "可能没有删除权限,createFile失败!");
                    }
                } else {
                    // 文件不存在直接创建
                    return file.createNewFile();
                }

            }
            // 如果不重写
            if (file.exists()) {
                return false;
            } else {
                // 直接创建
                return file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 清空磁盘目录中的文件
     * 
     * @param dir 文件夹
     * @return true成功false失败
     */
    public static boolean deleteDirFiles(File dir) {
        nullCheck(dir);

        try {
            cn.hutool.core.io.FileUtil.clean(dir);
            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
    public static void getAllDirAndFileByDir(List<File> outDirAndFiles, File dir, int setDeep, int deep, String pattern,
            boolean onlyMathFilename, IoType ioType) {
                getAllDirAndFileByDir(outDirAndFiles, dir, setDeep, deep, pattern, onlyMathFilename, ioType,-1);
            }
    /**
     * 获得某个文件夹下面的文件夹或者文件
     * 
     * @param outDirAndFiles   输出参数,使用完毕之后可以直接遍历传入的outPutFiles
     * @param dir              起始目录
     * @param setDeep          设置发现深度
     * @param deep             起始深度值,必须为-1!
     * @param pattern          匹配文件或文件夹的的正则表达式,*代表所有 例如,匹配后缀
     *                         ([^\\s]+(\\.(?i)(jpg|png))$),
     * @param onlyMathFilename 正则表达式是否只匹配文件/夹名称(包含后缀)默认匹配文件的完整限定名 ,当匹配正则为 *
     *                         的时候,这个参数true和false都不会影响到程序的逻辑
     * @param ioType           设置想要得到的io类型.可选的值FILE文件,DIR文件夹,,FILE_AND_DIR文件夹和文件
     * @param maxResultCount 最大的就结果数量
     */
    public static void getAllDirAndFileByDir(List<File> outDirAndFiles, File dir, int setDeep, int deep, String pattern,
            boolean onlyMathFilename, IoType ioType,Integer maxResultCount) {
        nullCheck(outDirAndFiles, dir, pattern, ioType);
        if (maxResultCount!=-1&&outDirAndFiles.size()>maxResultCount) {
            System.out.println("结果数量已经达到"+outDirAndFiles.size()+" 超出最大值"+maxResultCount+"停止搜索");
            return ;
        }
        if (dir != null) {
            // 是否
            if (dir.isDirectory() && deep < setDeep) {
                deep++;// 往深一层
                File f[] = dir.listFiles();
                if (f != null) {
                    int length = f.length;
                    for (int i = 0; i < length; i++) {
                        // 添加文件
                        // *号默认匹配所有
                        File file = f[i];
                        if (pattern.equals("*")) {
                            if (ioType == ioType.FILE & !file.isDirectory()) {
                                outDirAndFiles.add(file);
                            }
                            if (ioType == ioType.DIR & file.isDirectory()) {
                                outDirAndFiles.add(file);
                            }
                            if (ioType == ioType.FILE_AND_DIR & file != null) {
                                outDirAndFiles.add(file);
                            }

                        } else {
                            // 判断是否匹配正则表达式
                            if (onlyMathFilename) {
                                if (Pattern.matches(pattern, getName(file))) {
                                    if (ioType == ioType.FILE & !file.isDirectory()) {
                                        outDirAndFiles.add(file);
                                    }
                                    if (ioType == ioType.DIR & file.isDirectory()) {
                                        outDirAndFiles.add(file);
                                    }
                                    if (ioType == ioType.FILE_AND_DIR & file != null) {
                                        outDirAndFiles.add(file);
                                    }
                                }
                            } else {
                                if (Pattern.matches(pattern, file.toPath().toString())) {
                                    if (ioType == ioType.FILE & !file.isDirectory()) {
                                        outDirAndFiles.add(file);
                                    }
                                    if (ioType == ioType.DIR & file.isDirectory()) {
                                        outDirAndFiles.add(file);
                                    }
                                    if (ioType == ioType.FILE_AND_DIR & file != null) {
                                        outDirAndFiles.add(file);
                                    }
                                }
                            }
                        }
                        getAllDirAndFileByDir(outDirAndFiles, file, setDeep, deep, pattern, onlyMathFilename, ioType,maxResultCount);
                    }
                    deep--;// 往上一层
                }
            }

        }
    }

    /**
     * 重命名文件或文件夹
     * 
     * @param oldFileOrDir 源文件路径
     * @param newName      新名字
     * @return
     * @return 操作成功标识
     */
    public static boolean renameFileOrDir(File oldFileOrDir, String newName) {
        nullCheck(oldFileOrDir, newName);
        if (oldFileOrDir.isDirectory()) {
            String newFilePath = oldFileOrDir.getParent().toString() + "/" + newName;
            File newFile = new File(newFilePath);
            return oldFileOrDir.renameTo(newFile);
        } else {
            String newFilePath = getFullPath(oldFileOrDir) + "/" + newName;
            File newFile = new File(newFilePath);
            return oldFileOrDir.renameTo(newFile);
        }

    }


    /**
     * 根据过滤器来监听dir中的dir和文件,注意此目录下文件不可过多
     * 
     * @param dir                           被监听的目录
     * @param fileAndDirFilterNameRegex_opt 匹配正则表达式.true则被监控
     * @param onChange                      监听器
     * @param intervalMs                    轮讯时间
     * @return
     */
    public static boolean watch(File dir, BiConsumer<Kind, File> onChange, String fileAndDirFilterNameRegex_opt) {
        nullCheck(dir, onChange);
        if (!dir.exists() || dir.isFile()) {
            Constant.dbg("文件:" + dir.getAbsolutePath() + " 不是一个目录!");
            return false;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Path path = Paths.get(dir.getPath());

                try {
                    new FileSystemWatcher(path, true, fileAndDirFilterNameRegex_opt).processEvents(onChange);
                } catch (Exception e) {
                }

                Constant.dbg("监听已经启动");

            }
        }).start();
        return false;
    }


    /**
     * 判断文件名,或文件路径是否有效
     * @param fileOrFilepath
     * @return
     */
    public static boolean isFilenameValid(String fileOrFilepath) {
        File f = new File(fileOrFilepath);
        try {
          f.getCanonicalPath();
          return true;
        } catch (IOException e) {
          return false;
        }
    }

}
