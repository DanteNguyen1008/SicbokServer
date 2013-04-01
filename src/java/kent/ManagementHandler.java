/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kent.component.Management;

/**
 *
 * @author Admin
 */
@WebServlet(name = "ManagementHandler", urlPatterns = {"/ManagementHandler"})
public class ManagementHandler extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String typeOfRequest = "";
        typeOfRequest = request.getParameter("request_name");
        /**
         * Admin login
         */
        if (typeOfRequest.equals("admin_sign_in")) {
            String username = request.getParameter("txtUsername");
            String passwrod = request.getParameter("txtPassword");
            boolean loginResult = false;
            try {
                loginResult = Management.Login(username, passwrod, request);
            } catch (SQLException ex) {
                Logger.getLogger(ManagementHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ManagementHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (loginResult) {
                //Success
                response.sendRedirect(Utils.SERVER + "manage/user-management.jsp");
            } else {
                //fail
                response.sendRedirect(Utils.SERVER + "manage/login.jsp?status=false");
            }
        }
        /**
         * De-Active User
         */
        if (typeOfRequest.equals("deactive_user")) {
        }

        /**
         * Active User
         */
        if (typeOfRequest.equals("active_user")) {
        }

        /**
         * Delete User
         */
        if (typeOfRequest.equals("delete_user")) {
            deleteUser(true, request, response);
        }

        /**
         * Enable User
         */
        if (typeOfRequest.equals("enable_user")) {
            deleteUser(false, request, response);
        }
    }
    
    private void deleteUser(boolean isDelete, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
         if (request.getParameter("user_id") != null) {
                Management admin = Management.getManagementInstanceFromSession(request);
                if (admin != null) {
                    boolean result = false;
                    try {
                        result = admin.DeleteUser(isDelete, Integer.parseInt(request.getParameter("user_id")));
                    } catch (SQLException ex) {
                        Logger.getLogger(ManagementHandler.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(ManagementHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    if(result)
                    {
                        reloadUserManagmentPage(response, "?error=Delete user suscces");
                    }else
                    {
                        reloadUserManagmentPage(response, "?error=Delete user Fail");
                    }
                }
                else{
                    backLoginPage(response);
                }
            }else
            {
                backLoginPage(response);
            }
    }

    private void reloadUserManagmentPage(HttpServletResponse response, String arg) throws IOException {
        response.sendRedirect(Utils.SERVER + "manage/user-management.jsp" + arg);
    }

    private void backLoginPage(HttpServletResponse response) throws IOException {
        response.sendRedirect(Utils.SERVER + "manage/login.jsp");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
