/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kent.component.BetHistory;
import kent.component.BetHistoryDetail;
import kent.component.BetProccess;
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
                u = utemp.signIn(username, password); //This method u can change to static, dont need to create a new user temp instance
            } catch (SQLException ex) {
                Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println(u.getUserId() + "-------------");
            if (u.getUserId() != 0) {
                session.setAttribute("user", u);
            }

            jsonResponse = u.getResponseJson();
        }

        if ("sign_in_facebook".equals(typeOfRequest)) {
            User userOperation = new User();
            User user = null;
            try {
                String email = request.getParameter("email");
                User.UserError error = userOperation.checkEmailFacebookExist(email);
                if (error.equals(User.UserError.FBEMAIL_EXIST)) {
                    //Email facebook exist => login without password
                    user = userOperation.facebookSignin(email);
                    if (user.getUserId() != 0) {
                        session.setAttribute("user", user);
                    }
                    jsonResponse = user.getResponseJson();
                } else if (error.equals(User.UserError.FBEMAIL_NOT_EXIST)) {
                    //Fb email not exist => make a register for the fb email
                    userOperation.facebookResponseRegister(email);
                    jsonResponse = userOperation.getResponseJson();
                } else if (error.equals(User.UserError.FBEMAIL_INVALID)) {
                    //Fb email invalid => reason : the email exist on the system but not login as a facebook account before
                    userOperation.facebookSignInInvalid();
                    jsonResponse = userOperation.getResponseJson();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
            }
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
                String email = request.getParameter("email");

                User utemp = new User();
                jsonResponse = utemp.forgotPassowrd(email);

            }
        }

        // Reset password after send forgot password request
        if ("reset_password".equals(typeOfRequest)) {

            String email = request.getParameter("email");
            String code = request.getParameter("code");

            User utemp = new User();
            jsonResponse = utemp.resetPassowrd(email, code);

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
        if ("play_bet".equals(typeOfRequest)) {

            String[] betSpots = request.getParameterValues("betspots");
            String[] betAmounts = request.getParameterValues("betamounts");
            int numOfSpots = betSpots.length;
            User currentUser = null;

            int[] spots = new int[numOfSpots];
            float[] amounts = new float[numOfSpots];

            // Transfer params in type of String into type in int, Float
            for (int ibet = 0; ibet < numOfSpots; ibet++) {
                spots[ibet] = Integer.parseInt(betSpots[ibet]);
                amounts[ibet] = Float.parseFloat(betAmounts[ibet]);
            }

            currentUser = (User) session.getAttribute("user");
            if (session != null && currentUser != null) {

                BetProccess betProc = new BetProccess(currentUser);
                try {
                    jsonResponse = betProc.play(spots, amounts);
                } catch (SQLException ex) {
                    Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {

                JSONObject data = new JSONObject();
                User u = new User();
                data.put("message", "You are not Sign in yet!");
                u.setResponseInfo("res_play_bet", data);
                jsonResponse = u.getResponseJson();
            }
        }


        if ("view_bet_history".equals(typeOfRequest)) {

            User currentUser = (User) session.getAttribute("user");

            if (session != null && currentUser != null) {

                BetHistory betHistory = new BetHistory();
                BetHistoryDetail betHistoryDetail = new BetHistoryDetail();
                ArrayList<BetHistory> betHistoryList = null;
                try {
                    betHistoryList = betHistory.getBetHistoryList(currentUser.getUserId());
                } catch (SQLException ex) {
                    Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
                }


                JSONObject data = new JSONObject();
                User u = new User();
                data.put("num_of_item", betHistoryList.size());

                for (int i = 0; i < betHistoryList.size(); i++) {
                    JSONObject historyjs = new JSONObject();
                    boolean iswin = (betHistoryList.get(i).getIsWin() == 1) ? true : false;

                    historyjs.put("dices", betHistoryList.get(i).getDices());
                    historyjs.put("iswin", iswin);
                    historyjs.put("betdate", betHistoryList.get(i).getBetDate());
                    historyjs.put("balance", betHistoryList.get(i).getBalance());

                    // Get history detail
                    ArrayList<BetHistoryDetail> betHistoryDetailList = null;
                    try {
                        betHistoryDetailList = betHistoryDetail.getHistoryDetailList(
                                betHistoryList.get(i).getBetHistoryId());
                    } catch (SQLException ex) {
                        Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String betSpots = "";
                    String betSpotsAmount = "";
                    String betSpotsWin = "";
                    String betSpotsWinAmount = "";
                    for (int ihd = 0; ihd < betHistoryDetailList.size(); ihd++) {
                        betSpots = betSpots + "|" + betHistoryDetailList.get(ihd).getBetSpotId();
                        betSpotsAmount = betSpotsAmount + "|" + betHistoryDetailList.get(ihd).getAmount();
                        // If win
                        if ((int) betHistoryDetailList.get(ihd).getIsWin() == 1) {
                            betSpotsWin = betSpotsWin + "|" + betHistoryDetailList.get(ihd).getBetSpotId();
                            betSpotsWinAmount = betSpotsWinAmount + "|" + betHistoryDetailList.get(ihd).getAmount();
                        }
                    }
                    historyjs.put("bet_spots", betSpots);
                    historyjs.put("bet_spots_amount", betSpotsAmount);
                    historyjs.put("bet_spots_win", betSpotsWin);
                    historyjs.put("bet_spots_win_amount", betSpotsWinAmount);

                    data.put(i + "", historyjs);
                }


                u.setResponseInfo("res_play_bet", data);
                jsonResponse = u.getResponseJson();

            } else {
                JSONObject data = new JSONObject();
                User u = new User();
                data.put("message", "You are not Sign yet!");
                u.setResponseInfo("res_change_password", data);
                jsonResponse = u.getResponseJson();
            }

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
