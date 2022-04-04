package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import github.acodervic.mod.Constant;
import github.acodervic.mod.code.Encode;
import github.acodervic.mod.data.list.ListUtil;
import github.acodervic.mod.io.FileReadUtil;

/**
 * ByteLIst,可变的byte数组
 */
public class ByteLIst {

    /**
     * 用来存储byte的List
     */
    List<Byte> bytesList = new ArrayList<Byte>();

    /**
     * 用来存储当前bytesList对应的字节数组,用于在转换的时候提升性能
     */
    byte[] byteArray = null;

    /**
     * 构建
     *
     * @param bytes 字节数组
     */
    public ByteLIst(byte[] bytes) {
        this.put(bytes);
    }

    /**
     * 构建
     *
     * @param bytes 字节数组
     */
    public ByteLIst(Byte[] bytes) {
            this.put(bytes);
    }

    /**
     * 构建
     * 
     * @param bytes 字节数组
     */
    public ByteLIst(List<Byte> bytes) {
        nullCheck(bytes);
        if (bytes != null) {
            this.bytesList = bytes;
        }

    }

    /**
     * 从文件构建字节列表
     * 
     * @param file 读取文件数据
     */
    public ByteLIst(File file) {
        nullCheck(file);
        if (file != null && file.exists()) {
            this.put(FileReadUtil.readFileToByteArray(file));
        } else {
            System.out.println("文件" + file.getAbsolutePath() + "不存在或者为null!");
        }

    }

    /**
     * 获取固定下标的字节
     * 
     * @param index 下标偏移
     * @return 字节
     */
    public byte getByte(int index) {
        return this.bytesList.get(index).byteValue();
    }

    /**
     * 从集合中读取一段数据
     * 
     * @param startIndex 开始下标
     * @param endIndex   结束下标
     * @return 字节数组
     */
    public byte[] getBytes(int startIndex, int endIndex) {
        int size = endIndex - startIndex;
        if (size > 0 && size <= size()) {
            // 返回数据
            byte[] bytes = new byte[size];
            for (int i = 0; i < bytes.length; i++) {
                try {
                    bytes[i] = this.getByte(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;// 如果下标超出则直接返回已经取到的数据
                }
            }
            return bytes;
        }
        System.out.println("取集合数据出错!size小于等于0,返回长度为0的bytes");
        return new byte[0];
    }

    /**
     * 获得集合中存储的字节数
     * 
     * @return
     */
    public int size() {
        return this.bytesList.size();
    }

    /**
     * 构建
     * 
     * @param byteBuffer 缓冲区
     */
    public ByteLIst(ByteBuffer byteBuffer) {
        nullCheck(byteBuffer);
        if (byteBuffer != null) {
            byteBuffer.flip();
            byte[] buffData = new byte[byteBuffer.limit()];
            byteBuffer.get(buffData);
            this.put(buffData);
        }
    }

    /**
     * 添加字节
     * 
     * @param b 字节
     */
    public  synchronized void put(byte b) {
        this.bytesList.add(new Byte(b));
    }

    /**
     * 添加字节
     *
     * @param bytes 字节数组
     */
    public void put(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            this.put(bytes[i]);
        }
    }



    /**
     * 添加字节
     *
     * @param bytes 字节数组
     */
    public void put(Byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            this.put(bytes[i]);
        }
    }

    public ByteLIst() {

    }

    /**
     * 将listByte转为arrayByte
     *
     * @return
     */
    public byte[] toBytes() {
        // 根据长度返回
        if (this.byteArray != null && this.byteArray.length == this.bytesList.size()) {
            return byteArray;
        }
        // 否则重新计算当前的byteArray
        this.byteArray = ListUtil.listByteTobytes(this.bytesList);
        return this.byteArray;
    }

        /**
     * 将listByte转为arrayByte
     *
     * @return
     */
    public Byte[] toWarpperBytes() {
        byte[] bytes = toBytes();
        Byte[] bs=new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            bs[i]=bytes[i];
        }
        return bs;
    }
    /**
     * 转换为字符串
     * @param charset_opt 编码,可以为null
     * @return
     */
    public String toString( String charset_opt) {
        try {
            if (charset_opt==null) {
                charset_opt = Constant.defultCharsetStr;
                
            }
            return new String(toBytes(),charset_opt);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用默认编码转换为字符串
     */
    public String toString() {
        return toString(null);
    }

    /**
     * 转换为str对象
     * @param charset_opt
     * @return
     */
    public str toStr(String charset_opt) {
        return new str(toString(charset_opt));
    }
    

    /**
     * 转换为str对象
     */
    public str toStr() {
        return toStr(null);
    }

    /**
     * 转为base64字符串
     * @return
     */
    public str toBase64Str() {
        return  new str(Encode.bytesToBase64SafeStr(toBytes()));
    }

    /**
     * 清空内部存储的所有数据
     */
    public void clearAllBytes() {
        this.bytesList.clear();
        this.byteArray = null;
    }

}