package github.acodervic.mod.io.compress;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Consumer;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;

public class ZipRes extends CompreserBase<ZipArchiveOutputStream> {

    public static void main(String[] args) throws IOException {
        // 从磁盘压缩
        ZipRes zip = new ZipRes();
        zip.getRootDir().addDir(new DirRes("/home/w/Downloads/111/apache-tomcat-8"), false);
        zip.getRootDir().addFile(new FileRAM("hahahah", "adadad".getBytes()));
        zip.packToDisk(new FileRes("/home/w/Downloads/111/test.zip"));// 导出压缩包
        // 解压
        ZipRes zip2 = new ZipRes(new FileRes("/home/w/Downloads/111/test.zip"));
        zip2.loadCompreserArchiveFIleToRAMTree();// 加载压缩包到文件树
        zip2.unPackToDir(new DirRes("/home/w/Downloads/112"));
        // 从内存文件树直接创建压缩文件到磁盘
        DirRAM dirRAM = new DirRAM(new DirRes("/home/w/Downloads/111/apache-tomcat-8"));
        dirRAM.fixChirdenFileAndDirFullPath();// 将磁盘文件树的绝对路径转换为相对路径
        ZipRes zipRes = new ZipRes(dirRAM);
        zipRes.packToDisk(new FileRes("/home/w/Downloads/111/test2.zip"));
    }


    @Override
    public int syncFilesToArchiveOutputStream() {
        // 遍历被压缩的文件夹,并将数据追加到原始输出流
        syncFilesToArchiveOutputStream(getZipArchiveOutputStream());
        return 0;
    }

    /**
     * 遍历被压缩的文件夹,并将数据追加到指定输出流
     *
     * @param zArchiveOutputStream
     * @return
     */
    public int syncFilesToArchiveOutputStream(ZipArchiveOutputStream zArchiveOutputStream) {
        // 便利被压缩的文件夹
        getRootDir().IteratorAll(dir -> {
        }, file -> {
            try {
                ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file.getFullPath());
                zArchiveOutputStream.putArchiveEntry(zipArchiveEntry);
                byte[] bytes = file.getByteLIst().toBytes();
                // print("文件:" + file.getFullPath() + "加入压缩," + bytes.length + "字节");
                zArchiveOutputStream.write(bytes);
                zArchiveOutputStream.closeArchiveEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        return 0;
    }

    /**
     * 构 构造一个压缩包对象,
     *
     * @param archiveFIle
     */
    public ZipRes(FileRes archiveFIle) {
        super(archiveFIle);
        try {
            // archiveFIle可能是已经存在的压缩文件,或者是不存在的压缩文件
            if (archiveFIle.exists()) {
                // 如果已存在,则打开输入流,并等待将磁盘数据读取到内存
                // archiveInputStream = new ZipArchiveInputStrea
            } else {
                // 如果不存在,则打开输出流,等待将压缩数据写入磁盘
                archiveOutputStream = new ZipArchiveOutputStream(archiveFIle.getFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构 构造一个压缩包对象,
     *
     * @param inputArchiveFIle
     */
    public ZipRes() {
    }

    /**
     * 读取压缩输出流
     *
     * @return
     */
    private ZipArchiveOutputStream getZipArchiveOutputStream() {
        return (ZipArchiveOutputStream) this.archiveOutputStream;
    }

    /**
     * 读取压缩输入流
     *
     * @return
     */
    private ZipArchiveInputStream getZipArchiveIntputStream() {
        return (ZipArchiveInputStream) this.archiveInputStream;
    }

    /**
     * 用于在执行压缩之间对archiveOutputStream进行设置操作等
     *
     * @param <T>
     * @param setFunction
     */

    @Override
    public void setOption(Consumer<ZipArchiveOutputStream> optionsFun) {
        optionsFun.accept(getZipArchiveOutputStream());
    }

    /**
     * 尝试从磁盘归档中加载压缩文件树到内存压缩根
     *
     * @throws IOException
     */
    @Override
    public void loadCompreserArchiveFIleToRAMTree() throws IOException {
        if (inputArchiveFIle != null && inputArchiveFIle.exists() && inputArchiveFIle.canRead()) {
            // 读取压缩文件到内存
            this.archiveInputStream = new ZipArchiveInputStream(
                    new BufferedInputStream(new FileInputStream(inputArchiveFIle.getFile()), 4096), "utf-8");
            ZipArchiveInputStream zipArchiveIntputStream = getZipArchiveIntputStream();
            ZipArchiveEntry entry = null;
            // 循环压缩包内文件
            while ((entry = zipArchiveIntputStream.getNextZipEntry()) != null) {
                unPackArchiveEntryFileAndDirToRootDir(entry);
            }
        }
    }

    /**
     * zip压缩不支持加密存档!
     */
    @Override
    public void loadCompreserArchiveFIleToRAMTree(String password) {
        printError("zip压缩不支持加密存档!");
    }

    /**
     * 尝试将内存中的压缩根的所有文件数据输出到压缩输入流,在解压写入磁盘前调用
     */
    @Override
    public int syncFilesToArchiveInputStream() {

        return 0;
    }

    @Override
    public void close() {
        try {
            getZipArchiveOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.close();
    }

    @Override
    public Exception packToDisk(FileRes file) {
        nullCheck(file);
        try {
            // 构造新的输出流
            ZipArchiveOutputStream newZipArchiveOutputStream = new ZipArchiveOutputStream(file.getFile());
            syncFilesToArchiveOutputStream(newZipArchiveOutputStream);
            newZipArchiveOutputStream.finish();// 写入磁盘
        } catch (IOException e) {
            e.printStackTrace();
            return e;
        }
        return null;
    }

    /**
     * 从一个已知的目录节点为根构造一个压缩包对象
     *
     * @param dirRAM
     */
    public ZipRes(DirRAM dirRAM) {
        super(dirRAM);
    }

}