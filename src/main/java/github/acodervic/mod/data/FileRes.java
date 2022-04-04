package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent.Kind;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import github.acodervic.mod.Constant;
import github.acodervic.mod.crypt.Digest;
import github.acodervic.mod.data.list.ListUtil;
import github.acodervic.mod.io.FileReadUtil;
import github.acodervic.mod.io.FileUtil;
import github.acodervic.mod.io.FileWriteUtil;

/**
 * FileRes
 */
public class  FileRes {
    Opt<File> file = new Opt<File>();

    /**
     * @return the file
     */
    public File getFile() {
        if (this.file.isNull_()) {
            return null;
        }
        return this.file.get();
    }

    /**
     * @param file the file to set
     */
    public void setFile( File file) {
        this.file = this.file.of(file);
    }

    /**
     * 读取path对象
     * 
     * @return
     */
    public Path toPath() {
        return getFile().toPath();
    }
    /**
     * @param file
     */
    public FileRes( File file) {
        this.file = this.file.of(new File(file.getAbsolutePath()));
    }

    /**
     * @param filePath 此路径可以是绝对也可以是相对路径,相对路径的函数来源于SystemUtil.getMyDir()
     */
    public FileRes(String filePath) {
        nullCheck(filePath);
        this.file = this.file.of(FileUtil.newFile(filePath));
    }

    /**
     * 判断文件是否存在
     *
     * @return
     */
    public boolean exists() {
        return getFile().exists();
    }


    /**
     * 判断文件是否不存在
     *
     * @return
     */
    public boolean notExists() {
        return !getFile().exists();
    }


    // 权限相关===========================
    public boolean canExecute() {
        return this.getFile().canExecute();
    }

    public boolean canRead() {
        return this.getFile().canRead();
    }

    public boolean canWrite() {
        return this.getFile().canWrite();
    }

    @Override
    public String toString() {
        return getAbsolutePath();
    }
    /**
     * 设置文件是否可执行
     * 
     * @param canExecute
     * @return
     */
    public boolean setExecutable(boolean canExecute) {
        return this.getFile().setExecutable(canExecute);
    }

    /**
     * 设置文件是否可读
     *
     * @param canRead
     * @return
     */
    public boolean setReadable(boolean canRead) {
        return this.getFile().setReadable(canRead);
    }

    /**
     * 设置文件是否可写
     * 
     * @param canWrite
     * @return
     */
    public boolean setWritable(boolean canWrite) {
        return this.getFile().setWritable(canWrite);
    }

    public boolean setWritable(boolean canWrite, boolean onlyMeCanWrite) {
        return this.getFile().setWritable(canWrite, onlyMeCanWrite);
    }

    public boolean setOnlyMeCanWrite(boolean onlyMeCanWrite) {
        return this.getFile().setWritable(canWrite(), onlyMeCanWrite);
    }

    /**
     * 删除文件
     * 
     * @return
     */
    public boolean delete() {
        return getFile().delete();
    }

    /**
     * 在jvm推出的时候删除
     * 
     * @return
     */
    public void deleteOnExit() {
        this.getFile().deleteOnExit();
        ;
    }

    public boolean isHidden() {
        return this.getFile().isHidden();
    }

    /**
     * 读取文件相对文件夹的路径
     * 
     * @param dir
     * @return
     */
    public String getRelativelyPath(String dir) {
        nullCheck(dir);
        Path pathAbsolute = toPath();
        Path pathBase = Paths.get(dir);
        Path pathRelative = pathBase.relativize(pathAbsolute);
        return pathRelative.toString();
    }

    /**
     * 读取文件相对文件夹的路径
     * 
     * @param dir
     * @return
     */
    public String getRelativelyPath(DirRes dir) {
        nullCheck(dir);
        Path pathAbsolute = toPath();
        Path pathBase = dir.toPath();
        Path pathRelative = pathBase.relativize(pathAbsolute);
        return pathRelative.toString();
    }

    /**
     * 返回文件的绝对路径
     * 
     * @return
     */
    public String getAbsolutePath() {
        return this.getFile().getAbsolutePath();
    }

    /**
     * 获取文件后缀名 当没有文件扩展名的时候返回空字符串
     * 
     * @return 返回扩展名
     */
    public String getFileExtensionName() {
        return FileUtil.getExtensionName(getFile());
    }

    /**
     * 获取文件名,不包含后缀
     * 
     * @return 文件名,不包含后缀
     */
    public String getFileBaseName() {
        return FileUtil.getBaseName(getFile());
    }

    /**
     * 获取文件名,包含后缀
     * 
     * @return 文件名,包含后缀
     */
    public String getFileName() {
        return FileUtil.getName(getFile());
    }

    /**
     * 读取文件/夹所在的目录
     * 
     * @return 文件所在的目录
     */
    public String getFileFullPath() {
        return FileUtil.getFullPath(getFile());
    }

    /**
     * 从路径中读取目录名
     * 
     * @return 目录名
     */
    public String getDirName() {
        return FileUtil.getDirName(getFile());
    }

    /**
     * 读取文件夹的父文件夹,如果file是一个文件则返回文件所在的文件夹
     * 
     * @return 父文件夹
     */
    public DirRes getDir() {
        return new DirRes(getFile().getParentFile());
    }


    /**
     * 判断文件是否匹配后缀,如果匹配成功则返回true
     * 
     * @param extension 扩展名
     * @return 成功则返回true
     */
    public boolean isExtension(String extension) {
        nullCheck(extension);
        return FileUtil.isExtension(getFile(), extension);
    }

    /**
     * 复制磁盘文件到目标磁盘文件
     * 
     * @param destFile
     * @return 成功true
     */
    public boolean copyFileTo(File destFile) {
        nullCheck(destFile);
        return FileUtil.copyFileTo(getFile(), destFile);
    }

    /**
     * 复制磁盘文件到某个目录
     *
     * @param destDir 文件目录
     * @return true成功false失败
     */
    public boolean copyFileToDirectory(File destDir) {
        nullCheck(destDir);
        return FileUtil.copyFileToDirectory(getFile(), destDir);
    }

    /**
     * 删除磁盘文件/夹
     * 
     * @param srcFileOrDir 文件
     * @return true成功false失败
     */
    public boolean forceDelete() {
        return FileUtil.deleteFileOrDir(getFile());
    }

    /**
     * 移动文件或者目录,移动前后文件完全一样,如果目标文件夹不存在则创建。
     * 
     * @param distDir 目标文件夹
     * @return true成功false失败
     */
    public boolean move(DirRes distDir) throws IOException {
        nullCheck(distDir);
        return FileUtil.moveFileOrDir(getFile(), distDir.getDir());
    }

    /**
     * 创建一个文件,Override参数决定是否进行覆盖,如果文件真实被创建成功则返回true,其它情况一律返回false
     * 在写入之前确保文件的中父文件夹是存在的,否则无法创建文件并返回false
     * 
     * @param Override 是否覆盖磁盘已有文件
     * @return 新创建的文件
     */
    public FileRes mkFile(boolean override) {
        if (FileUtil.createFile(getFile(), override)) {
            return new FileRes(getFile());
        }
        return null;
    } 
    /**
     * 重命名文件,返回新的文件,如果命名失败则返回null
     * 
     * @param oldFileOrDir 源文件路径
     * @param newName      新名字
     * @return
     * @return 操作成功标识
     */
    public FileRes rename(String newName) {
        nullCheck(newName);
        if (FileUtil.renameFileOrDir(getFile(), newName)) {
            return new FileRes(getDir()  + newName);
        }
        return null;
    }

    /**
     * 监听文件改变(新建一个线程),注意此文件所属目录下文件不可过多.因为会遍历文件夹影响性能,循环时间也不要太短
     * 
     * @param onChange   监听器
     * @param intervalMs 轮讯时间
     * @return
     */
    public boolean watchFile(BiConsumer<Kind, File> onChange, long intervalMs) {
        nullCheck(onChange);
        //判断是否为null
        if (!this.file.isNull_()) {
            return false;
        }
        if (!getFile().exists() || getFile().isDirectory()) {
            Constant.dbg("文件:" + getFile().getAbsolutePath() + "不是文件!");
            return false;
        }
        FileUtil.watch(getFile(), onChange, getAbsolutePath());
        return false;
    }

    /**
     * 判断文件名是否匹配后缀 数组,如果包含一个则返回true
     * 
     * @param extensions 扩展名
     * @return true成功false失败
     */
    public boolean isExtension(String... extensions) {
        return FileUtil.isExtension(getFile(), extensions);
    }

    /**
     * 读取文本文件全部行，执行失败则返回一个长度为0的集合
     * 
     * @param encoding_opt 编码格式null=utf8
     * @return 列表行
     * @throws IOException
     */
    public List<String> readLines(String encoding_opt) {
        return FileReadUtil.readFilesLinesToStringList(getFile(), encoding_opt);
    }

    /**
     * 读取文本文件全部行，执行失败则返回一个长度为0的集合
     * 
     * @param encoding_opt 编码格式null=utf8
     * @return 列表行
     * @throws IOException
     */
    public List<str> readLinesStr(String encoding_opt) {
        return FileReadUtil.readFilesLinesToStrList(getFile(), encoding_opt);
    }

    /**
     * 读取文本文件全部行，执行失败则返回一个长度为0的集合
     * 
     * @return 列表行
     * @throws IOException
     */
    public List<String> readLines() {
        return  readLines(null);
    }

    /**
     * 读取文本文件全部行，执行失败则返回一个长度为0的集合
     * 
     * @return 列表行
     * @throws IOException
     */
    public List<str> readLinesStr() {
        return readLinesStr(null);
    }
    

    /**
     * 将文件读取到一个字符串中，执行失败则返回一个长度为0的字符串
     *
     * @param encoding_opt 编码格式null=系统默认编码
     * @return 字符串
     * @throws IOException
     */
    public String readString(String encoding_opt) {
        return FileReadUtil.readFileToString(getFile(), encoding_opt);
    }


    /**
     * 将文件读取到一个字符串中，执行失败则返回一个长度为0的字符串
     *
     * @param encoding_opt 编码格式null=系统默认编码
     * @return 字符串
     * @throws IOException
     */
    public str readStr() {
        return new str(readBytes());
    }

 

/**
     * 将文件读取到一个字符串中，执行失败则返回一个长度为0的字符串
     * 
     * @return 字符串
     * @throws IOException
     */
    public String readString( ) {
        return readString(null);
    }
    
    /**
     * 读取文本文件的倒序第几行,执行失败则返回一个长度为0的字符串列表
     * 
     * @param encoding_opt 编码格式null=系统默认编码
     * @return
     * @throws IOException
     */
    public List<String> readLineIndex(int lineCount, String encoding_opt) {
        try {
            return FileReadUtil.readFileLastLineIndex(getFile(), lineCount, encoding_opt);
        } catch (Exception e) {
            e.printStackTrace();
         };
         return new ArrayList<String>();
    }

        /**
     * 读取文本文件的倒序第几行,执行失败则返回一个长度为0的字符串列表
     * 
     * @return
     * @throws IOException
     */
    public List<String> readLineIndex(int lineCount) {
        return readLineIndex(lineCount, null);
    }

    /**
     * 读取文件到二进制字节数组,执行失败则返回一个长度为0的字节数组
     * 
     * @return 字节数组
     * @throws IOException
     */
    public byte[] readBytes() {
        return FileReadUtil.readFileToByteArray(getFile());
    }

    /**
     * 写入字符串到磁盘文件,注意文件将会被重写覆盖,如果该文件不存在，则创建该文件
     * 
     * @param str          文本
     * @param encoding_opt 字符编码(可选null=UTF-8)
     * @return 成功true,失败false
     */
    public boolean writeStr(String str, String encoding_opt) {
        return FileWriteUtil.writeStrToFile(str, getFile(), encoding_opt);
    }

        /**
     * 写入字符串到磁盘文件,注意文件将会被重写覆盖,如果该文件不存在，则创建该文件
     * 
     * @param str          文本
     * @return 成功true,失败false
     */
        public boolean writeStr(String str) {
            return writeStr(str, null);
    }


    /**
     * 写入字符串到磁盘文件,每个元素自动换行,注意文件将会被重写覆盖,如果该文件不存在，则创建该文件
     * 
     * @param strs         字符串列表
     * @param encoding_opt 字符编码(可选null=UTF-8)
     * @return 成功true,失败false
     */
    public boolean writeStr(List<String> strs, String encoding_opt) {
        nullCheck(strs);
        return FileWriteUtil.writeStrToFile(strs, getFile(), encoding_opt);
    }


    /**
     * 写入字符串到磁盘文件,每个元素自动换行,注意文件将会被重写覆盖,如果该文件不存在，则创建该文件
     * 
     * @param strs         字符串列表
     * @param encoding_opt 字符编码(可选null=UTF-8)
     * @return 成功true,失败false
     */
    public boolean writeStrByStrList(List<str> strs, String encoding_opt) {
        nullCheck(strs);
        // 将strs转换为字符串
        List<String> lines = ListUtil.cast(strs, str -> {
            return str.to_s();
        });
        return FileWriteUtil.writeStrToFile(lines, getFile(), encoding_opt);
    }

    /**
     * 写入字符串到磁盘文件,每个元素自动换行,注意文件将会被重写覆盖,如果该文件不存在，则创建该文件
     * 
     * @param strs         字符串列表
     * @return 成功true,失败false
     */
    public boolean writeStr(List<String> strs) {
        nullCheck(strs);
        return writeStr(strs, null);
    }

 

    /**
     * 追加写入字符串到磁盘文件中,如果该文件不存在，则创建该文件
     * 
     * @param str          字符串
     * @param encoding_opt 字符编码(可选null=UTF-8)
     * @return 成功true,失败false
     */
    public boolean appendWriteStr(String str, String encoding_opt) {
        nullCheck(str);
        return FileWriteUtil.appendWriteStrToFile(str, getFile(), encoding_opt);
    }


/**
     * 追加写入字符串到磁盘文件中,如果该文件不存在，则创建该文件
     * 
     * @param str          字符串
     * @return 成功true,失败false
     */
public boolean appendWriteStr(String str) {
    nullCheck(str);
    return FileWriteUtil.appendWriteStrToFile(str, getFile(), null);
    }



    /**
     * 追加写入字符串到磁盘文件中,如果该文件不存在，则创建该文件
     * 
     * @param lines           字符串列表
     * @param lineSpitStr_opt 写入的换行符号 null=系统默认值
     * @return 成功true,失败false
     */
    public boolean appendWriteStr(List<String> lines, String lineSpitStr_opt, String charset_opt) {
        nullCheck(lines);
        return FileWriteUtil.appendWriteStrToFile(lines, getFile(), lineSpitStr_opt, charset_opt);
    }


    /**
     * 读取文件字节大小,如果文件不存在则返回0
     * @return
     */
    public  long size() {
        if (notExists()) {
            return 0;
        }
        return file.get().length();
    }
    
    /**
     * 追加写入字符串到磁盘文件中,如果该文件不存在，则创建该文件
     * 
     * @param lines           字符串列表
     * @return 成功true,失败false
     */
    public boolean appendWriteStr(List<String> lines) {
        nullCheck(lines);
        return appendWriteStr(lines, null, null);
    }


    

    /**
     * 写入字节数组到磁盘文件中,如果文件存在文件将被重写覆盖, 如果该文件不存在，则创建该文件
     * 
     * @param byteData 字节数组
     * @return 成功true,失败false
     */
    public boolean writeByteArray(byte[] byteData) {
        return FileWriteUtil.writeByteArrayToFile(byteData, getFile());
    }

    /**
     * 追加写入字节数组到磁盘文件中 如果该文件不存在，则创建该文件
     *
     * @param byteData 字节数组
     * @return 成功true,失败false
     */
    public boolean appendWriteBytes(byte[] byteData) {
        return FileWriteUtil.appendWriteBytesToFile(byteData, getFile());
    }

    public Opt<String> sumMD5() {
        Opt<String> md5=new Opt<>();
        if (exists()&&canRead()) {
            try {
                String bytesToMd5HexStr = Digest.bytesToMd5HexStr(new FileInputStream(getFile()));
                md5.of(bytesToMd5HexStr);
            } catch (Exception e) {
                md5.setException(e);
            }
        }
        return md5;
    }
    public void setUid() {
        // TODO setUid
    }

    public void setGroupId() {
        // TODO setGroupId

    }

    public void setLastModifyTime() {
        // TODO setLastModifyTime

    }

}