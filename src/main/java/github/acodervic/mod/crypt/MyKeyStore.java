package github.acodervic.mod.crypt;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.Opt;

/**
 * 密钥库工具
 */
public class MyKeyStore {
    KeyStore keystore;
    String password;
    final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[] {};
        }
    } };
    /**
     * 从文件构造一个 密钥库对象
     *
     * @param password_opt
     * @param file
     * @param type         JKS PKCS12 JCEKS BKS UBER BCFKS
     * @throws Exception
     */
    public MyKeyStore(String password_opt, FileRes file, String type) throws Exception {
        nullCheck(file, type);
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            if (password_opt != null) {
                 this.password = password_opt;
            }
            FileInputStream fis = new FileInputStream(file.getFile());
            ks.load(fis, getPasswordCharArray());
            this.keystore = ks;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 读取密钥信任工厂
     *
     * @return
     */
    public Opt<TrustManagerFactory> getTrustManagerFactory() {
        Opt<TrustManagerFactory> trustManagerFactory = new Opt<TrustManagerFactory>();
        try {
            TrustManagerFactory instance = TrustManagerFactory.getInstance("SunX509");
            instance.init(keystore);
            trustManagerFactory.of(instance);
            return trustManagerFactory;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trustManagerFactory;
    }

    /**
     * 读取密钥信任器数组
     *
     * @return
     */
    public TrustManager[] getTrustManagers() {
        Opt<TrustManagerFactory> trustManagerFactory = getTrustManagerFactory();
        if (trustManagerFactory.isNull_()) {
            new RuntimeException("读取trustManagerFactory异常!").printStackTrace();
            return new TrustManager[0];
        } else {
            return trustManagerFactory.get().getTrustManagers();
        }
    }

    /**
     * /** 读取密钥管理器数组
     *
     * @return
     */
    public KeyManager[] getKeyManagers() {
        Opt<KeyManagerFactory> keyManagerFactory = getKeyManagerFactory();
        if (keyManagerFactory.isNull_()) {
            new RuntimeException("读取keyManagerFactory异常!").printStackTrace();
            return new KeyManager[0];
        } else {
            return keyManagerFactory.get().getKeyManagers();
        }
    }

    /**
     * 读取密钥管理器工厂
     *
     * @return
     */
    public Opt<KeyManagerFactory> getKeyManagerFactory() {
        Opt<KeyManagerFactory> keyManagerFactory = new Opt<KeyManagerFactory>();
        try {
            KeyManagerFactory instance = KeyManagerFactory.getInstance("SunX509");
            instance.init(this.keystore, getPasswordCharArray());
            keyManagerFactory.of(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyManagerFactory;
    }

    /**
     * 读取密码数组,如果副哦密码为null则返回长度为0的字符数组
     * 
     * @return
     */
    char[] getPasswordCharArray() {
        return this.password == null ? new char[0] : (char[]) password.toCharArray();
    }

    /**
     * 读取一个SSL上下文通过当前密钥库
     *
     * @param useTrustStore 是否将当前密钥库作为信任库使用,如果为true,则会使用当前的密钥库作为信任库(里面的可信证书,你需要将客户端的证书导入到密钥库中)
     * @param protocol      协议
     * @return
     */
    public Opt<SSLContext> getSSLContext(String protocol, boolean useTrustStore) {
        nullCheck(protocol);
        Opt<SSLContext> Context = new Opt<SSLContext>();
        try {
            // 读取一个工厂,用来创建密钥管理器
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            // 初始化工厂
            char[] pass = getPasswordCharArray();
            kmf.init(getRawKeyStore(), pass);
            // 创建密钥管理器
            KeyManager[] km = kmf.getKeyManagers();
            SSLContext sslContext = SSLContext.getInstance(protocol);
            sslContext.init(km, useTrustStore ? this.getTrustManagers() : null, null);
            Context.of(sslContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Context;
    }

    /**
     * 读取一个信任所有证书的SSL上下文通过当前密钥库
     *
     * @param useTrustStore 是否将当前密钥库作为信任库使用,如果为true,则会使用当前的密钥库作为信任库(里面的可信证书,你需要将客户端的证书导入到密钥库中)
     * @param protocol      协议
     * @return
     */
    public Opt<SSLContext> getTrustAllSSLContext(String protocol) {
        nullCheck(protocol);
        Opt<SSLContext> Context = new Opt<SSLContext>();
        try {
            // 读取一个工厂,用来创建密钥管理器
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            // 初始化工厂
            kmf.init(getRawKeyStore(), getPasswordCharArray());
            // 创建密钥管理器ad
            KeyManager[] km = kmf.getKeyManagers();
            SSLContext sslContext = SSLContext.getInstance(protocol);
            sslContext.init(km, trustAllCerts, null);
            Context.of(sslContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Context;
    }

    /**
     * 读取一个SSL上下文通过当前密钥库
     * 
     * @param protocol 协议
     *
     * @return
     */
    public Opt<SSLContext> getSSLContext(String protocol) {
        nullCheck(protocol);
        return getSSLContext(protocol, false);
    }
    /**
     * 读取密钥库中的证书
     *
     * @param keyName
     * @return
     */
    public Opt<Certificate> getCertificate(String keyName) {
        nullCheck(keyName);
        try {
            return new Opt<Certificate>(this.keystore.getCertificate(keyName));
        } catch (KeyStoreException e) {
            e.printStackTrace();
            System.out.println("读取keyName=" + keyName + "证书不存在!");
        }
        return new Opt<Certificate>();
    }
    /**
     * 读取原始keystore对象
     *
     * @return
     */
    public KeyStore getRawKeyStore() {
        return this.keystore;
    }

    public Opt<PublicAndPrivateKey> getKeyPair(String keyName, String password_opt) {
        nullCheck(keyName);
        Opt<PublicAndPrivateKey> publicAndPrivateKey = new Opt<PublicAndPrivateKey>();
        if (!hasKey(keyName)) {
            return publicAndPrivateKey;
        }

        PublicAndPrivateKey result = new PublicAndPrivateKey();
        try {
            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(
                    password_opt != null ? password_opt.toCharArray() : new char[0]);
            Entry entry = this.keystore.getEntry(keyName, protParam);
            if (entry instanceof PrivateKeyEntry) {
                PrivateKey privateKey = null;
                PublicKey publicKey = null;
                PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry) entry;
                try {
                    result.setCertificateChain(privateKeyEntry.getCertificateChain());
                    result.setCertificate(privateKeyEntry.getCertificate());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 构建私钥
                java.security.PrivateKey privateKey2 = privateKeyEntry.getPrivateKey();
                if (privateKey2 != null) {
                    privateKey = new PrivateKey(privateKey2.getEncoded(),
                            KeyType.getKeyTypeByStr(privateKey2.getAlgorithm()));
                    privateKey.setFromat(privateKey2.getFormat());
                    result.setPrivateKey(privateKey);
                }
                // 构建公钥
                Certificate certificate = privateKeyEntry.getCertificate();
                if (certificate != null && certificate.getPublicKey() != null) {
                    java.security.PublicKey publicKey2 = certificate.getPublicKey();
                    String algorithm = publicKey2.getAlgorithm();
                    publicKey = new PublicKey(publicKey2.getEncoded(), KeyType.getKeyTypeByStr(algorithm));
                    publicKey.setFromat(publicKey2.getFormat());
                    result.setPublicKey(publicKey);
                }
                publicAndPrivateKey.of(result);
            }

        } catch (KeyStoreException e) {
            e.printStackTrace();
            System.out.println("密钥库没有被加载!");
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
            System.out.println("读取keyName=" + keyName + "密码错误!");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("读取keyName=" + keyName + "密码算法但在环境中不可用!");
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        }
        return publicAndPrivateKey;
    }

    /**
     * 是否有key
     * 
     * @param keyName
     * @return
     */
    public boolean hasKey(String keyName) {
        try {
            return this.keystore.isKeyEntry(keyName);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取密钥库中的对称加密密钥,
     *
     * @param keyName
     * @param password_opt
     * @return
     */
    public Opt<Key> getKey(String keyName, String password_opt) {
        nullCheck(keyName);
        Opt<Key> key = new Opt<Key>();
        try {
            if (password_opt != null && hasKey(keyName)) {
                java.security.Key key2 = this.keystore.getKey(keyName, password_opt.toCharArray());
                Key myKey = new Key(key2.getEncoded(), KeyType.getKeyTypeByStr(key2.getAlgorithm()));
                myKey.setFromat(key2.getFormat());
                key.of(myKey);
            } else {
                java.security.Key key2 = this.keystore.getKey(keyName, null);
                Key myKey = new Key(key2.getEncoded(), KeyType.getKeyTypeByStr(key2.getAlgorithm()));
                myKey.setFromat(key2.getFormat());
                key.of(myKey);
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
            System.out.println("密钥库没有被加载!");
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
            System.out.println("读取keyName=" + keyName + "密码错误!");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("读取keyName=" + keyName + "密码算法但在环境中不可用!");
        }
        return key;
    }

    /**
     * 读取密钥库内实体名称列表
     * 
     * @return
     */
    public List<String> getEntryNameList() {
        List<String> names = new ArrayList<String>();
        try {
            Enumeration<String> aliases = this.keystore.aliases();
            String itemName;
            while (aliases.hasMoreElements()) {
                itemName = aliases.nextElement();
                names.add(itemName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return names;
    }

}
