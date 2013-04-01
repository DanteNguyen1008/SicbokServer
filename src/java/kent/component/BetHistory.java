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

    private int BetHistoryId;
    private int userId;
    private String betDate;
    private String dices;
    private int isWin;
    private float balance;
    private DatabaseHandler databaseHandler;

    public BetHistory() {
        this.databaseHandler = DatabaseHandler.getInstance();
    }

    public ArrayList<BetHistory> getBetHistoryList(int userId, String lastDate, int limit) throws SQLException {
        ArrayList<BetHistory> result = null;
        BetHistory temp = null;

        ResultSet rs = this.databaseHandler.executeQuery(
                "BET_HISTORY_SELECT_BY_USER_ID",
                new String[]{"userId","lastDate","mlimit"},
                new Object[]{userId,lastDate,limit});
        if (!rs.next()) {
            System.out.println("No records found");
            return null;
        } else {
            result = new ArrayList<BetHistory>();

            do {
                temp = new BetHistory();
                // Get data from the current row and use it
                temp.BetHistoryId = rs.getInt("bet_history_id");
                temp.userId = rs.getInt("user_id");
                temp.betDate = rs.getString("date_of_bet");
                temp.dices = rs.getString("dices");
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
            float balance,
            String dices) {
        /*
         * Execute SQL
         */
        int rowAffected = 0;
        try {
            rowAffected = this.databaseHandler.executeSQL(
                    "BET_HISTORY_INSERT",
                    new String[]{"userId", "betDate", "isWin", "balance", "dice"},
                    new Object[]{userId, betDate, isWin, balance, dices});
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
     * @return the BetHistoryId
     */
    public int getBetHistoryId() {
        return this.BetHistoryId;
    }

    /**
     * @param betHistoryId the betHistoryId to set
     */
    public void setBetHistoryId(int betHistoryId) {
        this.BetHistoryId = betHistoryId;
    }

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
     * @return the probability
     */
    public String getDices() {
        return this.dices;
    }

    /**
     * @param betDay the betDay to set
     */
    public void setDices(String dices) {
        this.dices = dices;
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
