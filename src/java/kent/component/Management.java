/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent.component;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import kent.Utils;
import kent.database.DatabaseHandler;

/**
 *
 * @author Admin
 */
public class Management {

    public String username;

    public Management(String username) {
        this.username = username;
    }

    public static Management getManagementInstanceFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Management admin = (Management) session.getAttribute(Utils.ADMIN_SESSION_NAME);
        return admin;
    }

    public void setManagementInstanceToSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute(Utils.ADMIN_SESSION_NAME, this);
    }

    public static boolean Login(String username, String password, HttpServletRequest request) throws SQLException, Exception {
        ResultSet rs = DatabaseHandler.getInstance().executeQuery("ADMIN_SELECT_LOGIN",
                new String[]{"v_username`", "v_password"},
                new Object[]{username, password});

        if (rs.next()) {
            Management instance = new Management(username);
            instance.setManagementInstanceToSession(request);
            return true;
        }
        return false;
    }

    public ResultSet loadUserList() {
        return DatabaseHandler.getInstance().executeQuery("ADMIN_SELECT_ALL_USER", null, null);
    }

    public boolean DeleteUser(boolean isDelete, int userID) throws SQLException, Exception {
        int result = DatabaseHandler.getInstance().executeSQL("ADMIN_UPDATE_DELETE_USER", new String[]{"userId", "delete_value"}, new Object[]{userID, isDelete});
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean ActiveUser(boolean isActive, int userID)
    {
        return false;
    }
}

