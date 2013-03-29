/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.json.JSONObject;

/**
 *
 * @author Kent
 */
public class JsonReader {

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    public static JSONObject readJsonFromUrl(String url) throws Exception {
        JSONObject json = null;
        json = new JSONObject(JsonReader.readUrl(url));
        return json;
    }
}
