/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent.component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import kent.Hash;
import kent.Portal;
import kent.ResponseAbstract;
import kent.SendMail;
import kent.Utils;
import kent.database.DatabaseHandler;
import org.json.simple.JSONObject;

/**
 *
 * @author Kent
 */
public class User extends ResponseAbstract {

    private int userId;
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String dateCreate;
    private double balance;
    private String bitcoinId;
    private String registerConfirmCode;
    private String forgotPassConfirmCode;
    private boolean isActive;
    private DatabaseHandler databaseHandler;

    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public User() {
        this.databaseHandler = DatabaseHandler.getInstance();
    }

    public User(
            int userId,
            String username,
            String password,
            String email,
            String fullname,
            String dateCreate,
            float balance,
            String bitcoinId,
            String registerConfirmCode,
            boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.dateCreate = dateCreate;
        this.balance = balance;
        this.bitcoinId = bitcoinId;
        this.registerConfirmCode = registerConfirmCode;
        this.isActive = isActive;
        this.databaseHandler = DatabaseHandler.getInstance();
    }
    //</editor-fold>

    public enum UserError {

        FBEMAIL_EXIST, FBEMAIL_NOT_EXIST, FBEMAIL_INVALID
    }

    /*
     For facebook
     */
    public void facebookResponseRegister(String email) {
        JSONObject data = new JSONObject();
        data.put("is_success", false);
        data.put("is_allow_facebook_register", true);
        data.put("email", email);
        data.put("message", "This facebook account is not exist on system");
        this.setResponseInfo("res_sign_in", data);
    }

    public void facebookSignInInvalid() {
        JSONObject data = new JSONObject();
        data.put("is_success", false);
        data.put("is_allow_facebook_register", false);

        data.put("message", "This facebook account is invalid!");
        this.setResponseInfo("res_sign_in", data);
    }

    public User facebookSignin(String email) throws SQLException {
        ResultSet rs = DatabaseHandler.getInstance().executeQuery("USER_SELECT_FACEBOOK_LOGIN",
                new String[]{"email"},
                new Object[]{email});

        return signInHandle(rs, true);
    }

    public UserError checkEmailFacebookExist(String email) throws SQLException {
        ResultSet rs = DatabaseHandler.getInstance().executeQuery("USER_CHECK_FB_EMAIL_EXIST",
                new String[]{"email"},
                new Object[]{email});
        if (rs.next()) {
            if (rs.getInt("result") == 1) {
                //Facebook email is not exist => Register
                return UserError.FBEMAIL_NOT_EXIST;
            } else if (rs.getInt("result") == 2) {
                //This email is not a facebook account ->Invalid
                return UserError.FBEMAIL_INVALID;
            } else if (rs.getInt("result") == 3) {
                //This email is exist => valid => Login
                return UserError.FBEMAIL_EXIST;
            }
        }

        return UserError.FBEMAIL_INVALID;
    }

    /*
     End facebook implementation
     */
    public JSONObject signUp(
            String username,
            String password,
            String email,
            String fullname,
            long dateCreate,
            float balance,
            String bitcoinId,
            String registerConfirmCode,
            boolean isActive,
            boolean is_facebook_account) {

        JSONObject data = new JSONObject();

        // Check username Exists
        if (this.isExists(username, email)) {
            data.put("is_success", false);
            data.put("message", "Username or email already exists.");
            this.setResponseInfo("res_signup", data);
            return this.getResponseJson();
        }

        /*
         * Execute SQL
         */
        int rowAffected = 1;
        isActive = is_facebook_account;
        try {
            rowAffected = this.databaseHandler.executeSQL(
                    "USER_INSERT",
                    new String[]{"inUsername", "inPassword", "inEmail", "inFullname", "inDateCreate",
                        "inBalance", "inBitcoinId", "inRegisterConfirmCode", "inIsActive", "is_facebook_connected"},
                    new Object[]{username, password, email, fullname, dateCreate,
                        balance, bitcoinId, registerConfirmCode, isActive, is_facebook_account});
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (rowAffected > 0) {
            // If success
            data.put("is_success", true);
            data.put("message", "Sign up success.");
            data.put("is_facebook_account", is_facebook_account);
            data.put("balance", balance);
            this.setResponseInfo("res_signup", data);
            // Send email attach with confirmation code
            if (!is_facebook_account) {
                try {
                    String confirmLink = Utils.SERVER_URL;
                    confirmLink += "?type_of_request=confirm_sign_up";
                    confirmLink += "&email=" + email;
                    confirmLink += "&code=" + registerConfirmCode;

                    String exEmailTitle = "Sicbo game!!! Sign up confirmation";
                    String exEmailContent = "<h1>Welcome to Sicbo game!!!</h1>";
                    exEmailContent += "<p>Let's play game to change the world.</p>";
                    exEmailContent += "<p>PLease click link below to finsh register proccess.</p>";
                    exEmailContent += "<p><a href=\"" + confirmLink + "\">";
                    exEmailContent += confirmLink + "</a></p>";
                    exEmailContent += "<p>Thank you very much!</p>";

                    SendMail sendMail = new SendMail(email);
                    sendMail.sendMail(exEmailTitle, exEmailContent);

                } catch (MessagingException ex) {
                    Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            // If fail
            data.put("is_success", false);
            data.put("message", "Sign up fail for some reason.");
            this.setResponseInfo("res_signup", data);
        }

        return this.getResponseJson();
    }

    public JSONObject signUpConfirm(String email, String confirmCode) {

        JSONObject data = new JSONObject();

        if (this.isValidConfirmCode(email, confirmCode)) {
            // If valid confirm code
            // Then active user
            int rowAffected = 0;
            try {
                rowAffected = this.databaseHandler.executeSQL(
                        "USER_UPDATE_ACTIVATION",
                        new String[]{"email_to_confirm"},
                        new Object[]{email});
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (rowAffected > 0) {
                data.put("is_success", true);
                data.put("message", "Successfully active account.");
                this.setResponseInfo("res_signup", data);
                return this.getResponseJson();
            } else {
                data.put("is_success", false);
                data.put("message", "Fail! Email and code are right but there are some sql problem.");
                this.setResponseInfo("res_signup", data);
                return this.getResponseJson();
            }

        } else {
            data.put("is_success", false);
            data.put("message", "Confirm code is not valid.");
            this.setResponseInfo("res_sign_up_confirm", data);
            return this.getResponseJson();
        }
    }

    private User signInHandle(ResultSet rs, boolean isFacebookAccount) throws SQLException {
        User u = new User();
        JSONObject data = new JSONObject();
        int count = 0;
        rs.next();
        count = rs.getRow();

        if (count == 1) {
            System.out.println("Sign in successfully!");

            u.setUserId(rs.getInt("user_id"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setEmail(rs.getString("email"));
            u.setFullname(rs.getString("fullname"));
            u.setDateCreate(rs.getString("date_create"));
            u.setBalance(rs.getFloat("balance"));
            u.setBitcoinId(rs.getString("bitcoin_id"));
            u.setRegisterConfirmCode(rs.getString("register_confirm_code"));
            u.setForgotPassConfirmCode(rs.getString("forgot_pass_confirm_code"));
            u.setIsActive(true);

            data.put("is_success", true);
            data.put("message", "Sign in successfully.");
            data.put("user_id", u.getUserId());
            data.put("username", u.getUsername());
            data.put("email", u.getEmail());
            data.put("fullname", u.getFullname());
            data.put("date_create", u.getDateCreate());
            data.put("balance", u.getBalance());
            data.put("bitcoin_id", u.getBitcoinId());
            data.put("is_facebook_account", isFacebookAccount);
            u.setResponseInfo("res_sign_in", data);

            return u;
        } else {
            data.put("is_success", false);
            data.put("message", "Fail! No username and password fail.");
            u.setResponseInfo("res_sign_in", data);
            return u;
        }
    }

    public User signIn(String username, String password) throws SQLException {
        ResultSet rs = this.databaseHandler.executeQuery(
                "USER_SELECT_SIGN_IN",
                new String[]{"username_to_check", "pass_to_check"},
                new Object[]{username, password});

        return signInHandle(rs, false);
    }

    // Forgot password
    public JSONObject forgotPassowrd(
            String email) {

        JSONObject data = new JSONObject();

        // Check username Exists
        if (this.isExistsEmail(email)) {

            String tempForgotPassConfirmCode = Hash.getHashSHA256(Utils.randomString(10));

            int rowAffected = 0;
            try {
                rowAffected = this.databaseHandler.executeSQL(
                        "USER_UPDATE_FORGOT_PASS_CONFIRM_CODE",
                        new String[]{"forgotPassConfirmCode", "email"},
                        new Object[]{tempForgotPassConfirmCode, email});
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (rowAffected > 0) {
                try {
                    String resetPassLink = "http://localhost:8080/SicbokServer/Portal";
                    resetPassLink += "?type_of_request=reset_password";
                    resetPassLink += "&email=" + email;
                    resetPassLink += "&code=" + tempForgotPassConfirmCode;

                    String exEmailTitle = "Sicbo game!!! Reset password";
                    String exEmailContent = "<h1>Reset passwod in Sicbo game system.</h1>";
                    exEmailContent += "<p>You have request Sicbo game system to reset your password.</p>";
                    exEmailContent += "<p>If you do. Click following link to reset password:</p>";
                    exEmailContent += "<p>" + resetPassLink + "</p>";
                    exEmailContent += "<p>If not, bypass this email.</p>";
                    exEmailContent += "<p>Thank you very much!</p>";
                    SendMail sendMail = new SendMail(email);
                    sendMail.sendMail(exEmailTitle, exEmailContent);

                } catch (MessagingException ex) {
                    Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
                }

                data.put("is_success", true);
                data.put("message", "Email for reset password has been sent to your email.");
                this.setResponseInfo("res_forgot_password", data);
                return this.getResponseJson();
            } else {
                data.put("is_success", false);
                data.put("message", "Some database error occurred..");
                this.setResponseInfo("res_forgot_password", data);
                return this.getResponseJson();
            }

        } else {
            data.put("is_success", false);
            data.put("message", "No email found.");
            this.setResponseInfo("res_forgot_password", data);
            return this.getResponseJson();
        }
    }
    // Forgot password

    public JSONObject resetPassowrd(
            String email, String code) {

        JSONObject data = new JSONObject();

        // Check username Exists
        if (this.isValidConfirmForgotPass(email, code)) {

            String newPassword = Utils.randomString(10);

            int rowAffected = 0;
            try {
                rowAffected = this.databaseHandler.executeSQL(
                        "USER_UPDATE_PASS_BY_EMAIL",
                        new String[]{"new_password", "email_to_change"},
                        new Object[]{newPassword, email});
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (rowAffected > 0) {
                try {
                    String exEmailTitle = "Sicbo game!!! Reset password";
                    String exEmailContent = "<h1>Reset passwod in Sicbo game system.</h1>";
                    exEmailContent += "<p>You have been reset passowrd in Sibok game system.</p>";
                    exEmailContent += "<p>You new password is: " + newPassword + "</p>";
                    exEmailContent += "<p>Thank you very much!</p>";
                    SendMail sendMail = new SendMail(email);
                    sendMail.sendMail(exEmailTitle, exEmailContent);

                } catch (MessagingException ex) {
                    Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
                }

                data.put("is_success", true);
                data.put("message", "Password have been changed and sent to user email.");
                this.setResponseInfo("res_forgot_password", data);
                return this.getResponseJson();
            }
        } else {
            data.put("is_success", false);
            data.put("message", "No email found. Or code wrong.");
            this.setResponseInfo("res_forgot_password", data);
            return this.getResponseJson();
        }
        return this.getResponseJson();
    }

    // Change password
    public JSONObject changePassowrd(
            User user, String oldPassword, String newPassword) {

        JSONObject data = new JSONObject();

        // Check username Exists
        if (this.isValidPassword(oldPassword, user.getUserId())) {
            int rowAffected = 0;
            try {
                rowAffected = this.databaseHandler.executeSQL(
                        "USER_UPDATE_PASS_BY_EMAIL",
                        new String[]{"new_password", "email_to_change"},
                        new Object[]{newPassword, user.getEmail()});
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (rowAffected > 0) {
                data.put("is_success", true);
                data.put("message", "Password have been changed.");
                this.setResponseInfo("res_change_password", data);
                return this.getResponseJson();
            } else {
                data.put("is_success", false);
                data.put("message", "Db problem.");
                this.setResponseInfo("res_change_password" + user.getEmail(), data);
                return this.getResponseJson();
            }
        } else {
            data.put("is_success", false);
            data.put("message", "Old password wrong.");
            this.setResponseInfo("res_change_password", data);
            return this.getResponseJson();
        }
    }

    public boolean updateBalance(double newBalance) {
        int affectedRow = 0;
        try {
            affectedRow = this.databaseHandler.executeSQL(
                    "USER_UPDATE_BALANCE",
                    new String[]{"newBalance", "userId"},
                    new Object[]{newBalance, this.getUserId()});
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (affectedRow > 0) {
            this.setBalance(newBalance);
            return true;
        }
        return false;
    }

    public double getCurrentBalance() throws SQLException {

        ResultSet rs = this.databaseHandler.executeQuery(
                "USER_SELECT_USER_BY_ID",
                new String[]{"userId"},
                new Object[]{this.getUserId()});

        rs.next();
        double currentBalance = rs.getDouble("balance");
        return currentBalance;    
    }

    public boolean isExists(String username, String email) {

        ResultSet rs = this.databaseHandler.executeQuery(
                "USER_SELECT_CHECK_EXISTS",
                new String[]{"username_to_check", "email_to_check"},
                new Object[]{username, email});

        int count = 0;
        try {
            while (rs.next()) {
                count++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }


        if (count == 0) {
            System.out.println("No username, email exists!");
            return false;
        } else {
            System.out.println("username or email exists!");
            return true;
        }
    }

    public boolean isValidConfirmCode(String email, String confirmCode) {

        ResultSet rs = this.databaseHandler.executeQuery(
                "USER_SELECT_SIGNUP_CONFIRM",
                new String[]{"email_to_check", "code_to_check"},
                new Object[]{email, confirmCode});

        int count = 0;
        try {
            while (rs.next()) {
                count++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }


        if (count == 0) {
            System.out.println("False! Confirm code not exists!");
            return false;
        } else {
            System.out.println("Ok! Confirm code is right.");
            return true;
        }
    }

    // Check validation of Forgot password Confirm Code
    public boolean isValidConfirmForgotPass(String email, String confirmCode) {

        ResultSet rs = this.databaseHandler.executeQuery(
                "USER_SELECT_FORGOT_PASS_CONFIRM_CODE",
                new String[]{"email_to_check", "code_to_check"},
                new Object[]{email, confirmCode});

        int count = 0;
        try {
            while (rs.next()) {
                count++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }


        if (count == 0) {
            System.out.println("False! Confirm code not exists!");
            return false;
        } else {
            System.out.println("Ok! Confirm code is right.");
            return true;
        }
    }

    public boolean isExistsEmail(String email) {

        ResultSet rs = this.databaseHandler.executeQuery(
                "USER_SELECT_FORGOT_PASSWORD",
                new String[]{"email_to_check"},
                new Object[]{email});

        int count = 0;
        try {
            while (rs.next()) {
                count++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }


        if (count == 0) {
            System.out.println("No email exists!");
            return false;
        } else {
            System.out.println("email already exists!");
            return true;
        }
    }

    public boolean isValidPassword(String oldPassword, int userId) {

        ResultSet rs = this.databaseHandler.executeQuery(
                "USER_SELECT_IS_VALID_PASS",
                new String[]{"old_pass_to_check", "userId"},
                new Object[]{oldPassword, userId});

        int count = 0;
        try {
            while (rs.next()) {
                count++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }


        if (count == 0) {
            System.out.println("No record found!");
            return false;
        } else {
            System.out.println("Already exists!");
            return true;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Encapsulate fields">
    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the fullname
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * @param fullname the fullname to set
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * @return the dateCreate
     */
    public String getDateCreate() {
        return dateCreate;
    }

    /**
     * @param dateCreate the dateCreate to set
     */
    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    /**
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * @return the bitcoinId
     */
    public String getBitcoinId() {
        return bitcoinId;
    }

    /**
     * @param bitcoinId the bitcoinId to set
     */
    public void setBitcoinId(String bitcoinId) {
        this.bitcoinId = bitcoinId;
    }

    /**
     * @return the registerConfirmCode
     */
    public String getRegisterConfirmCode() {
        return registerConfirmCode;
    }

    /**
     * @param registerConfirmCode the registerConfirmCode to set
     */
    public void setRegisterConfirmCode(String registerConfirmCode) {
        this.registerConfirmCode = registerConfirmCode;
    }

    /**
     * @return the forgotPassConfirmCode
     */
    public String getForgotPassConfirmCode() {
        return forgotPassConfirmCode;
    }

    /**
     * @param forgotPassConfirmCode the forgotPassConfirmCode to set
     */
    public void setForgotPassConfirmCode(String forgotPassConfirmCode) {
        this.forgotPassConfirmCode = forgotPassConfirmCode;
    }

    /**
     * @return the isActive
     */
    public boolean isIsActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    //</editor-fold>
}
