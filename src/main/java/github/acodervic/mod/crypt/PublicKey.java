package github.acodervic.mod.crypt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

import github.acodervic.mod.data.BaseUtil;

/**
 * publicKey
 */
public class PublicKey extends Key {


    /**
     * 构建一个非对称加密的公钥
     *
     * @param keyData
     * @param keyType
     */
    public PublicKey(byte[] keyData, KeyType keyType) {
        super(keyData, keyType);
    }



    public byte[] getKeyData() {
        return keyData;
    }

    public void setKeyData(byte[] keyData) {
        this.keyData = keyData;
    }

    public KeyType getKeyType() {
        return KeyType;
    }

    public void setKeyType(KeyType keyType) {
        KeyType = keyType;
    }

    /**
     * 根据公钥算法类型加密数据
     * 
     * @param data
     * @return
     */
    public byte[] Encrypt(byte[] data) {
        switch (this.getKeyType().getKeyTypeStr()) {
            case "RSA":
                // 进行rsa加密
                return rsaEncrypt(data);

            default:
                break;
        }
        // 出现错误则返回 长度为0的
        return BaseUtil.nullByteArry();
    }

    /**
     * 公钥加密
     * 
     * @param bytes 需要被加密的数据
     * @return
     */
    private byte[] rsaEncrypt(byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(this.getKeyType().getKeyTypeStr());
            RSAPublicKey rsaPublicKey = getRSAPublicKey();
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            return rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, bytes, rsaPublicKey.getModulus().bitLength());
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + bytes + "]时遇到异常", e);
        }
    }



    /**
     * 得到公钥
     * 
     * @throws Exception
     */
    private RSAPublicKey getRSAPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(this.getKeyType().getKeyTypeStr());
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(this.keyData);
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

}