/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent;

import org.json.simple.JSONObject;

/**
 *
 * @author Kent
 */
public abstract class ResponseAbstract {

    JSONObject jsonResponse = new JSONObject();
    JSONObject jsonTask = new JSONObject();
    JSONObject jsonData = new JSONObject();

    public void setResponseInfo(String responseId, JSONObject data) {
        jsonTask.put("taskId", responseId);
        jsonTask.put("data", data);
        jsonResponse.put("request", jsonTask);
    }

    public JSONObject getResponseJson() {
        return jsonResponse;
    }
}
