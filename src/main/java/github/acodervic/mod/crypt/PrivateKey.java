package github.acodervic.mod.crypt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;

import github.acodervic.mod.data.BaseUtil;

/**
 * ProvateKey
 */
public class PrivateKey extends Key {


    public byte[] getKeyData() {
        return keyData;
    }

    public KeyType getKeyType() {
        return KeyType;
    }

    /**
     * 根据私钥算法解密数据
     *
     * @param bytes 被解密的数据
     * @return 解密之后的数据
     */
    public byte[] Decrypt(byte[] bytes) {
        switch (this.KeyType.getKeyTypeStr()) {
            case "RSA":
                return rsaDecrypt(bytes);

            default:
                break;
        }
        //出现错误则返回 长度为0的
        return BaseUtil.nullByteArry();
    }

    /**
     * 使用当前密钥数据,进行RSA解密
     *
     * @param bytes 被解密的数据
     * @return 解密后的数据
     */
    public byte[] rsaDecrypt(byte[] bytes) {

        try {
            Cipher cipher = Cipher.getInstance(this.KeyType.getKeyTypeStr());
            RSAPrivateKey rsaPrivateKey = getRSAPrivateKey();
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            return rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, bytes, rsaPrivateKey.getModulus().bitLength());
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + bytes + "]时遇到异常", e);
        }
    }

    /**
     * 生成一个PKCS#8编码RSA私钥
     *
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public RSAPrivateKey getRSAPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(this.KeyType.getKeyTypeStr());
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(this.keyData);
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 构建一个非对称加密的私钥
     *
     * @param keyData
     * @param keyType
     */
    public PrivateKey(byte[] keyData, KeyType keyType) {
        super(keyData, keyType);
    }

}