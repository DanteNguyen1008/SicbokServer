/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent;

import java.util.Random;

/**
 *
 * @author Kent
 */
public class Utils {

    public static String randomString(int length) {
        String chars = "0123456789!@#$%^&*()abcdefghygklmnouprstvwxyz";
        Random rand = new Random();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            buf.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return buf.toString();
    }
}
