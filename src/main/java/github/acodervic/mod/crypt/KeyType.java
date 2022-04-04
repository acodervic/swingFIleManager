package github.acodervic.mod.crypt;

/**
 * 密钥类型
 */
public enum  KeyType {

    RSA("RSA"), ELGAMAL("ELGAMAL"), AES("AES"), DES("DES"), RC4("RC4");

    public  String keyTypeStr;

    // 构造方法
     KeyType(String keyType) {
         this.keyTypeStr = keyType.toUpperCase();
     }

     /**
      * 通过算法字符串读取枚举,自动删除空格和忽略大小写
      * 
      * @param str
      * @return
      */
     public static KeyType getKeyTypeByStr(String str) {
         switch (str.trim().toUpperCase()) {
             case "RSA":
                 return KeyType.RSA;
             case "ELGAMAL":
                 return KeyType.ELGAMAL;
             case "AES":
                 return KeyType.AES;
             case "RC4":
                 return KeyType.RC4;
             case "DES":
                 return KeyType.DES;
             default:
                 return null;
         }
    }
    public String getKeyTypeStr() {
        return keyTypeStr;
    }

}