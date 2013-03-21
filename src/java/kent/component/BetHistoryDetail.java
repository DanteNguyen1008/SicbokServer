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
public class BetHistoryDetail {

    private int betHistoryDetailId;
    private int betHistoryId;
    private int betSpotId;
    private int isWin;
    private float amount;
    private float balance;
    private DatabaseHandler databaseHandler;

    public BetHistoryDetail() {
        this.databaseHandler = new DatabaseHandler();
    }

    public ArrayList<BetHistoryDetail> getHistoryDetailList(int betHistoryId) throws SQLException {

        ArrayList<BetHistoryDetail> result = null;
        BetHistoryDetail temp = null;

        ResultSet rs = this.databaseHandler.executeQuery(
                "BET_HISTORY_DETAIL_SELECT",
                new String[]{"betHistoryId"},
                new Object[]{betHistoryId});
        if (!rs.next()) {
            System.out.println("No records found");
        } else {
            result = new ArrayList<BetHistoryDetail>();
            do {
                temp = new BetHistoryDetail();
                // Get data from the current row and use it
                temp.setBetHistoryId(rs.getInt("bet_history_id"));
                temp.setBetSpotId(rs.getInt("bet_spot_id"));
                temp.setIsWin(rs.getInt("is_win"));
                temp.setAmount(rs.getFloat("amount"));
                temp.setBalance(rs.getFloat("balance"));
                result.add(temp);

            } while (rs.next());
        }
        return result;
    }

    public boolean add(
            int betHistoryId,
            int betSpotId,
            int isWin,
            float amount,
            float balance) {
        /*
         * Execute SQL
         */
        int rowAffected = 0;
        try {
            rowAffected = this.databaseHandler.executeSQL(
                    "BET_HISTORY_DETAIL_INSERT",
                    new String[]{"betHistoryId", "betSpotId", "isWin", "amount", "balance"},
                    new Object[]{betHistoryId, betSpotId, isWin, amount, balance});
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
     * @return the bet_history_detail_id
     */
    public int getBetHistoryDetailId() {
        return betHistoryDetailId;
    }

    /**
     * @param betHistoryDetailId the bet_history_detail_id to set
     */
    public void setBet_history_detail_id(int bet_history_detail_id) {
        this.betHistoryDetailId = bet_history_detail_id;
    }
    
    /**
     * @return the bet_history_id
     */
    public int getBetHistoryId() {
        return betHistoryId;
    }

    /**
     * @param bet_history_id the bet_history_id to set
     */
    public void setBetHistoryId(int bet_history_id) {
        this.betHistoryId = bet_history_id;
    }

    /**
     * @return the bet_spot_id
     */
    public int getBetSpotId() {
        return betSpotId;
    }

    /**
     * @param bet_spot_id the bet_spot_id to set
     */
    public void setBetSpotId(int bet_spot_id) {
        this.betSpotId = bet_spot_id;
    }

    /**
     * @return the isWin
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
     * @return the amount
     */
    public float getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * @return the balance
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
