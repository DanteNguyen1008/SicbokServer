/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent.component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import kent.database.DatabaseHandler;

/**
 *
 * @author Kent
 */
public class BetHistory {

    private int userId;
    private String betDate;
    private int isWin;
    private float balance;
    private DatabaseHandler databaseHandler;
    
    public BetHistory() {
        this.databaseHandler = new DatabaseHandler();
    }

    public ArrayList<BetHistory> getHistoryList(int userId) throws SQLException {
        ArrayList<BetHistory> result = null;
        BetHistory temp = null;

        ResultSet rs = this.databaseHandler.executeQuery(
                "BET_HISTORY_SELECT_BY_USER_ID",
                new String[]{"userId"},
                new Object[]{userId});
        if (!rs.next()) {
            System.out.println("No records found");
        } else {
            result = new ArrayList<BetHistory>();
            
            do {
                temp = new BetHistory();
                // Get data from the current row and use it
                temp.userId = rs.getInt("user_id");
                temp.betDate = rs.getString("bet_date");
                temp.isWin = rs.getInt("is_win");
                temp.balance = rs.getFloat("balance");
                result.add(temp);

            } while (rs.next());
        }
        return result;
    }

    public boolean add(
            int userId,
            long betDate,
            int isWin,
            float balance) {
        /*
         * Execute SQL
         */
        int rowAffected = 0;
        try {
            rowAffected = this.databaseHandler.executeSQL(
                    "BET_HISTORY_INSERT",
                    new String[]{"userId", "betDate", "isWin", "balance"},
                    new Object[]{userId, betDate, isWin, balance});
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (rowAffected > 0) {
            return true;
        }
        return false;
    }

    //<editor-fold defaultstate="collapsed" desc="Encapsulate fields">
    /**
     * @return the getUserId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param getUserId the getUserId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the patternName
     */
    public int getIsWin() {
        return isWin;
    }

    /**
     * @param isWin the isWin to set
     */
    public void setIsWin(int isWin) {
        this.isWin = isWin;
    }

    /**
     * @return the probability
     */
    public String getBetDate() {
        return betDate;
    }

    /**
     * @param betDay the betDay to set
     */
    public void setBetDate(String betDay) {
        this.betDate = betDay;
    }

    /**
     * @return the getBalance
     */
    public float getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(float balance) {
        this.balance = balance;
    }
    //</editor-fold>
}
