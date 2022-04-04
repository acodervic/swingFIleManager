package github.acodervic.mod.crypt;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

import github.acodervic.mod.data.FileRes;

/**
 * publicAndProvateKey
 */
public class PublicAndPrivateKey {

    /**
     * InnerpublicAndProvateKey
     */

    PublicKey publicKey = null;
    PrivateKey privateKey = null;
    Certificate certificate;// 证书
    Certificate[] certificateChain;// 证书链
    KeyType keyType = null;

    public PublicAndPrivateKey(PublicKey publicKey, PrivateKey privateKey, KeyType keyType) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.keyType = keyType;
    }

    /**
     * 将证书导出到目标
     * 
     * @param targetFile
     * @return
     */
    public boolean exportCertificate(FileRes targetFile) {
        nullCheck(targetFile);
        if (this.certificate != null) {
            try {
                return targetFile.writeByteArray(this.certificate.getEncoded());
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @return the certificateChain
     */
    public Certificate[] getCertificateChain() {
        return certificateChain;
    }

    /**
     * @param certificateChain the certificateChain to set
     */
    public void setCertificateChain(Certificate[] certificateChain) {
        this.certificateChain = certificateChain;
    }

    /**
     * @return the certificate
     */
    public Certificate getCertificate() {
        return certificate;
    }

    /**
     * @param certificate the certificate to set
     */
    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }
    /**
     * 生成加密的公钥和私钥
     * 
     * @param keySize 密钥长度
     * @param keytype 密钥类型
     * @return
     */
    public static PublicAndPrivateKey createKeys(KeyType keytype, int keySize) {
        // 为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try {
            // 创建一个Key生成器
            kpg = KeyPairGenerator.getInstance(keytype.getKeyTypeStr());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("生成key失败!-->[" + keytype.getKeyTypeStr() + "]");
        }

        // 初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        // 生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        // 得到公钥
        Key publicKey = keyPair.getPublic();
        // 得到私钥
        Key privateKey = keyPair.getPrivate();
        // 装载公钥和私钥
        return new PublicAndPrivateKey(new PublicKey(publicKey.getEncoded(), keytype),
                new PrivateKey(privateKey.getEncoded(), keytype), keytype);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public KeyType getKeyType() {
        return this.keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    /**
     * 构造一个空的对象
     */
    public PublicAndPrivateKey() {
    }

}