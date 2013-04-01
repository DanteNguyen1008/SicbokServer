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
    
    //public static final String SERVER_URL = "http://sicbogame.jelastic.servint.net/sicbogame/Portal"; 
    public static final String SERVER = "http://10.0.1.15:8084/SicbokServer/";
    public static final String SERVER_URL = SERVER + "Portal"; 
    public static final String SERVER_URL_ADMIN = SERVER + "ManagementHandler"; 
    public static final float DEFAULT_BALANCE = 1000;
    public static final int DEFAULT_HISTORY_LIMIT = 20;
    public static final String ADMIN_SESSION_NAME = "admin";

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
