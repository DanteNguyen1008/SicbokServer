/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Kent
 */
public class Hash {

    static String getHashSHA256(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
        }
        digest.reset();
        byte[] bytes = digest.digest(password.getBytes());
        
        return String.format("%0" + (bytes.length * 2) + "X", new BigInteger(1, bytes));
    }
    
    public byte[] getHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }

    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }
}
