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
import javax.servlet.http.HttpSession;
import kent.component.User;
import org.json.simple.JSONObject;

/**
 *
 * @author Kent
 */
@WebServlet(name = "Portal", urlPatterns = {"/Portal"})
public class Portal extends HttpServlet {

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

        response.setContentType("application/json");
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(1800);
        PrintWriter out = response.getWriter();

        String typeOfRequest = "";
        typeOfRequest = request.getParameter("type_of_request");
        JSONObject jsonResponse = new JSONObject();

        // Sign up
        if ("sign_up".equals(typeOfRequest)) {

            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String fullname = request.getParameter("fullname");
            long dateCreate = System.currentTimeMillis() / 1000;
            float balance = 1000;
            String bitcoinId = request.getParameter("bitcoin_id");
            String registerConfirmCode = Hash.getHashSHA256(username + dateCreate);
            boolean isActive = false;

            User u = new User();
            jsonResponse = u.signUp(username, password, email, fullname,
                    dateCreate, balance, bitcoinId, registerConfirmCode, isActive);
        }

        if ("confirm_sign_up".equals(typeOfRequest)) {

            String email = request.getParameter("email");
            String code = request.getParameter("code");


            User u = new User();
            jsonResponse = u.signUpConfirm(email, code);
        }

        // SIgn in
        if ("sign_in".equals(typeOfRequest)) {

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            User utemp = new User();
            User u = new User();
            try {
                u = utemp.signIn(username, password);
            } catch (SQLException ex) {
                Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println(u.getUserId() + "-------------");
            if (u.getUserId() != 0) {
                session.setAttribute("user", u);
            }

            jsonResponse = u.getResponseJson();
        }


        // FOrgot password
        if ("forgot_password".equals(typeOfRequest)) {

            if (session == null) {
                JSONObject data = new JSONObject();
                User u = new User();
                data.put("message", "You are not Sign yet!");
                u.setResponseInfo("res_forgot_password", data);
                jsonResponse = u.getResponseJson();
            } else {
                out.println(Utils.randomString(10));
                String email = request.getParameter("email");

                User utemp = new User();
                jsonResponse = utemp.forgotPassowrd(email);

            }
        }

        // Change password.
        if ("change_password".equals(typeOfRequest)) {
            
            User currentUser = null;
            String passwordOld = request.getParameter("old_pass");
            String passwordNew = request.getParameter("new_pass");

            currentUser = (User) session.getAttribute("user");
            if (session != null && currentUser != null) {
                
                jsonResponse = currentUser.changePassowrd(currentUser, passwordOld, passwordNew);

            } else {
                JSONObject data = new JSONObject();
                User u = new User();
                data.put("message", "You are not Sign yet!");
                u.setResponseInfo("res_change_password", data);
                jsonResponse = u.getResponseJson();
            }
        }

        // Sign out.
        if ("sign_out".equals(typeOfRequest)) {

            if (session != null) {
                session.invalidate();
            }
            JSONObject data = new JSONObject();
            User u = new User();
            data.put("message", "You have been sign out!");
            u.setResponseInfo("res_sign_out", data);
            jsonResponse = u.getResponseJson();
        }






        /*
         * Response back result in JSON format
         */
        try {
            System.out.println(jsonResponse.toJSONString());
            out.println(jsonResponse.toJSONString());
        } finally {
            out.close();
        }
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
