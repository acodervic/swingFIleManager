package github.acodervic.mod.crypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import github.acodervic.mod.code.Encode;
import github.acodervic.mod.data.BaseUtil;

/**
 * key
 */
public class Key implements java.security.Key {

  byte[] keyData = null;
  String fromat;// 密钥格式
  KeyType KeyType; // 密钥算法类型

  /**
   * 使用当前密钥数据进行AES加密
   *
   *
   * 加密 1.构造密钥生成器 2.根据ecnodeRules规则初始化密钥生成器 3.产生密钥 4.创建和初始化密码器 5.内容加密 6.返回字符串
   * 出现错误则返回 长度为0的BaseUtil.nullByteArry()
   * 
   * @param bytes 输入字节
   * @return 加密后的输入字节
   */
  public byte[] AESEncrypt(final byte[] bytes) {
    try {
      // 1.构造密钥生成器，指定为AES算法,不区分大小写
      final KeyGenerator keygen = KeyGenerator.getInstance("AES");
      // 2.根据ecnodeRules规则初始化密钥生成器
      // 生成一个128位的随机源,根据传入的字节数组
      final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
      secureRandom.setSeed(this.keyData);
      keygen.init(128, secureRandom);
      // 3.产生原始对称密钥
      final SecretKey original_key = keygen.generateKey();
      // 4.获得原始对称密钥的字节数组
      final byte[] raw = original_key.getEncoded();
      // 5.根据字节数组生成AES密钥
      final SecretKey key = new SecretKeySpec(raw, "AES");
      // 6.根据指定算法AES自成密码器
      final Cipher cipher = Cipher.getInstance("AES");
      // 7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
      cipher.init(Cipher.ENCRYPT_MODE, key);
      // 8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
      // 9.根据密码器的初始化方式--加密：将数据加密
      final byte[] byte_AES = cipher.doFinal(bytes);
      // 10.将加密后的数据转换为字符串
      // 这里用Base64Encoder中会找不到包
      // 解决办法：
      // 在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
      return byte_AES;
    } catch (final NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (final NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (final InvalidKeyException e) {
      e.printStackTrace();
    } catch (final IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (final BadPaddingException e) {
      e.printStackTrace();
    }

    // 如果有错就返加nulll 字节数组
    return BaseUtil.nullByteArry();
  }

  /**
   * 使用当前密钥数据进行AES解密
   *
   *
   * 解密 解密过程： 1.同加密1-4步 2.将加密后的字符串反纺成byte[]数组 3.将加密内容解密
   *
   * @param bytes 需要被解密的内容
   *
   * @return 解密后的数据
   */
  public byte[] AESDecrypt(final byte[] bytes) {
    try {
      // 1.构造密钥生成器，指定为AES算法,不区分大小写
      final KeyGenerator keygen = KeyGenerator.getInstance("AES");
      // 2.根据ecnodeRules规则初始化密钥生成器
      // 生成一个128位的随机源,根据传入的字节数组
      final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
      secureRandom.setSeed(this.keyData);
      keygen.init(128, secureRandom);
      // 3.产生原始对称密钥
      final SecretKey original_key = keygen.generateKey();
      // 4.获得原始对称密钥的字节数组
      final byte[] raw = original_key.getEncoded();
      // 5.根据字节数组生成AES密钥
      final SecretKey key = new SecretKeySpec(raw, "AES");
      // 6.根据指定算法AES自成密码器
      final Cipher cipher = Cipher.getInstance("AES");
      // 7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY

      cipher.init(Cipher.DECRYPT_MODE, key);
      // 8.将加密并编码后的内容解码成字节数组
      /*
       * 解密
       */
      final byte[] byte_decode = cipher.doFinal(bytes);
      return byte_decode;
    } catch (final NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (final NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (final InvalidKeyException e) {
      e.printStackTrace();
    } catch (final IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (final BadPaddingException e) {
      e.printStackTrace();
    }

    // 如果有错就返加nulll 字节数组
    return BaseUtil.nullByteArry();
  }

  /**
   * 根据当前key类型自动解密数据
   * 
   * @param bytes
   * @return
   */
  public byte[] Decrypt(byte[] bytes) {
    switch (this.KeyType.getKeyTypeStr()) {
      case "AES":
        return AESDecrypt(bytes);
      default:
        break;
    }
    // 出现错误则返回 长度为0的字节
    return BaseUtil.nullByteArry();
  }

  /**
   * 根据当前key类型自动加密数据
   * 
   * @param bytes
   * @return
   */
  public byte[] Encrypt(byte[] bytes) {
    switch (this.KeyType.getKeyTypeStr()) {
      case "AES":
        return AESEncrypt(bytes);
      default:
        break;
    }
    // 出现错误则返回 长度为0的字节
    return BaseUtil.nullByteArry();
  }

  /**
   * 读取密钥以base64编码
   * 
   * @return
   */
  public String getB64StrKey() {
    return Encode.bytesToBase64SafeStr(this.keyData);
  }

  /**
   * //rsa切割解码 , ENCRYPT_MODE,加密数据 ,DECRYPT_MODE,解密数据
   *
   * @param cipher  cipher
   * @param opmode  opmode
   * @param datas   datas
   * @param keySize keySize
   * @return
   */
  protected static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
    int maxBlock = 0; // 最大块
    if (opmode == Cipher.DECRYPT_MODE) {
      maxBlock = keySize / 8;
    } else {
      maxBlock = keySize / 8 - 11;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int offSet = 0;
    byte[] buff;
    int i = 0;
    try {
      while (datas.length > offSet) {
        if (datas.length - offSet > maxBlock) {
          // 可以调用以下的doFinal（）方法完成加密或解密数据：
          buff = cipher.doFinal(datas, offSet, maxBlock);
        } else {
          buff = cipher.doFinal(datas, offSet, datas.length - offSet);
        }
        out.write(buff, 0, buff.length);
        i++;
        offSet = i * maxBlock;
      }
    } catch (Exception e) {
      throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
    }
    byte[] resultDatas = out.toByteArray();
    try {
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultDatas;
  }

  public byte[] getKeyData() {
    return keyData;
  }

  /**
   * @param keyData the keyData to set
   */
  public void setKeyData(byte[] keyData) {
    this.keyData = keyData;
  }

  /**
   * @return the keyType
   */
  public KeyType getKeyType() {
    return KeyType;
  }

  /**
   * @param keyType the keyType to set
   */
  public void setKeyType(KeyType keyType) {
    KeyType = keyType;
  }

  /**
   * @param keyData
   * @param keyType
   */
  public Key(byte[] keyData, github.acodervic.mod.crypt.KeyType keyType) {
    this.keyData = keyData;
    KeyType = keyType;
  }

  /**
   * 读取算法
   * 
   * @return
   */
  @Override
  public String getAlgorithm() {
    return this.getKeyType().getKeyTypeStr();
  }

  /**
   * 读取格式
   * 
   * @return
   */
  @Override
  public String getFormat() {
    return this.fromat;
  }

  /**
   * @param fromat the fromat to set
   */
  public void setFromat(String fromat) {
    this.fromat = fromat;
  }

  @Override
  public byte[] getEncoded() {
    return this.keyData;
  }

  //TODO DES加密解密
  public void DES() {
  }
}