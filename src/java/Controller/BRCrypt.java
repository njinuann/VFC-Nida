package Controller;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import com.sun.crypto.provider.SunJCE;
import java.io.Serializable;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class BRCrypt implements Serializable
{

    private static Cipher encipher = null;
    private static Cipher decipher = null;

    private static final byte salt[]
            =
            {
                -87, -101, -56, 50, 86, 53, -29, 3
            };

    public BRCrypt()
    {
        initialize();
    }

    public static void initialize()
    {
        try
        {
            Security.addProvider(new SunJCE());
            java.security.spec.KeySpec keySpec = new PBEKeySpec(String.valueOf("This is supernova project !").toCharArray());
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            java.security.spec.AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, 19);
            encipher = Cipher.getInstance(key.getAlgorithm());
            decipher = Cipher.getInstance(key.getAlgorithm());
            encipher.init(1, key, paramSpec);
            decipher.init(2, key, paramSpec);
        }
        catch (Exception ex)
        {

            ex.printStackTrace();

        }
    }

    public static String encrypt(String plainStr)
    {
        try
        {
            if (plainStr == null)
            {
                return plainStr;
            }
            if (encipher == null)
            {
                initialize();
            }
            return (new BASE64Encoder()).encode(encipher.doFinal(plainStr.getBytes("UTF8")));
        }
        catch (Exception ex)
        {

            ex.printStackTrace();

        }
        return plainStr;
    }

    public static String decrypt(String encryptedStr)
    {
        try
        {
            if (encryptedStr == null)
            {
                return encryptedStr;
            }
            if (decipher == null)
            {
                initialize();
            }
            return new String(decipher.doFinal((new BASE64Decoder()).decodeBuffer(encryptedStr)), "UTF8");
        }
        catch (Exception ex)
        {

            ex.printStackTrace();

        }
        return encryptedStr;
    }

//    public static void  main(String[] args) {
//        BRCrypt crypt = new BRCrypt();
//        String decr = BRCrypt.decrypt("k6MjR8pvTyRrwjLHPRk5FA==");
//        String Ecnr = BRCrypt.encrypt("centurylive");
//        System.err.println("decrypt "+decr);
//        System.err.println("Ecrypt "+Ecnr);
//    }
}
