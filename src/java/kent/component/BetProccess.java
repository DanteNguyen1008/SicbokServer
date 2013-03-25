/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent.component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import kent.database.DatabaseHandler;
import org.json.simple.JSONObject;

/**
 *
 * @author Kent
 */
public class BetProccess {

    User user = null;
    RandomNumberGenerator rng = null;
    Pattern ptn = null;
    Spots spot = null;
    BetHistory history = null;
    private float currentBalance;
    private float amountBet;
    private DatabaseHandler databaseHandler;

    public BetProccess(User user) {
        this.user = user;
        this.rng = new RandomNumberGenerator();
        this.ptn = new Pattern();
        this.spot = new Spots();
        this.history = new BetHistory();
        databaseHandler = DatabaseHandler.getInstance();
    }

    public JSONObject play(int[] spots, float[] amounts) throws SQLException {

        this.currentBalance = this.user.getBalance();
        this.amountBet = 0;

        int numOfBet = spots.length;


        float totalAmount = this.getTotalAmount(amounts);

        // Check valid balance
        // If Balance is less than Bet amount. Cancel and return invalid message
        if (this.user.getBalance() < totalAmount || totalAmount > 100) {

            JSONObject data = new JSONObject();
            data.put("is_play_success", false);
            data.put("message", "Bet amount could not greater than current balance and 100 Zenny.");
            this.user.setResponseInfo("res_play_bet", data);
            return this.user.getResponseJson();

        } else { // If balance is valid

            ArrayList<BetHistoryDetail> tempBetHisDetailList = new ArrayList<BetHistoryDetail>();

            // For each Bet. Do Bet
            for (int iBet = 0; iBet < numOfBet; iBet++) {

                BetHistoryDetail bd = new BetHistoryDetail();

                float tempAmount = this.doBet(this.spot.getSpot(spots[iBet]), (float) amounts[iBet]);

                if (tempAmount > 0) {
                    bd.setIsWin(1);
                } else if (tempAmount < 0) {
                    bd.setIsWin(0);
                }
                bd.setBetSpotId(spots[iBet]);
                bd.setAmount(amounts[iBet]);
                boolean add = tempBetHisDetailList.add(bd);
            }

            this.currentBalance = this.currentBalance + this.amountBet;
            // Update to databse
            if (this.user.updateBalance(this.currentBalance)) {

                int isWin = 0;
                long betDate = System.currentTimeMillis() / 1000;
                if (this.amountBet > 0) {
                    isWin = 1;
                }
                boolean resultIsWin = (isWin == 0) ? false : true;
                // Get lastest balance
                this.currentBalance = this.user.getBalance();

                int insertedId = 0;
                    
                String dices = this.rng.getDice1() + "|" + 
                        this.rng.getDice2() + "|" + 
                        this.rng.getDice3();
                insertedId = this.databaseHandler.executeSQLAndGetId(
                        "kb_bet_history",
                        "bet_history_id",
                        "BET_HISTORY_INSERT",
                        new String[]{"userId", "betDate", "isWin", "balance", "dice"},
                        new Object[]{this.user.getUserId(), betDate, isWin, this.currentBalance, dices});


                // if bet history insert successfully
                if (insertedId == 0) {

                    JSONObject data = new JSONObject();
                    data.put("is_success", false);
                    data.put("message", "XXX. Some error occur in database.");
                    this.user.setResponseInfo("res_play_bet", data);
                    return this.user.getResponseJson();
                } else {
                    String strSpotsId = "";
                    String strIsWin = "";
                    String strAmount = "";
                    String strSportsIdIsWin = "";

                    BetHistoryDetail bhd = new BetHistoryDetail();
                    // Foreach bet history detail, insert it.
                    for (int i = 0; i < tempBetHisDetailList.size(); i++) {

                        bhd.add(insertedId, tempBetHisDetailList.get(i).getBetSpotId(),
                                tempBetHisDetailList.get(i).getIsWin(),
                                tempBetHisDetailList.get(i).getAmount(),
                                this.currentBalance);

                        strSpotsId = strSpotsId + "|" + tempBetHisDetailList.get(i).getBetSpotId();
                        strIsWin = strIsWin + "|" + tempBetHisDetailList.get(i).getIsWin();
                        strAmount = strAmount + "|" + tempBetHisDetailList.get(i).getAmount();
                        if (tempBetHisDetailList.get(i).getIsWin() == 1) {
                            strSportsIdIsWin = strSportsIdIsWin + "|" + tempBetHisDetailList.get(i).getBetSpotId();
                        }
                        
                    }
                    JSONObject data = new JSONObject();

                    data.put("is_success", true);
                    data.put("is_win", resultIsWin);
                    data.put("dice1", this.rng.getDice1());
                    data.put("dice2", this.rng.getDice2());
                    data.put("dice3", this.rng.getDice3());
                    data.put("current_balance", this.user.getBalance());
                    data.put("totalbetamount", totalAmount);
                    data.put("totalwinamount", this.amountBet);
                    data.put("message", "Play successfully.");
                    //data.put("str_spots_id", strSpotsId);
                    //data.put("str_spots_is_win", strIsWin);
                    //data.put("str_spots_amount", strAmount);
                    data.put("winpatterns", strSportsIdIsWin);
                    
                    this.user.setResponseInfo("res_play_bet", data);

                    return this.user.getResponseJson();
                }

            } else {
                JSONObject data = new JSONObject();
                data.put("is_success", false);
                data.put("message", "Some error occur in database.");
                this.user.setResponseInfo("res_play_bet", data);
                return this.user.getResponseJson();
            }
        }
    }

    /*
     * Do bets.
     * After do bets . update current_balance property
     */
    public float doBet(Spots spot, Float amount) throws SQLException {

        Pattern ptnOfSpot = this.ptn.getPattern(spot.getPatternId());

        float spotAmount = 0;

        //<editor-fold defaultstate="collapsed" desc="Big and Small">
        if (1 == spot.getSpotId()) { // Big
            System.out.println(this.rng.isBig());
            if (this.rng.isBig()) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }

        if (2 == spot.getSpotId()) { // Small
            System.out.println(this.rng.isSmall());
            if (this.rng.isSmall()) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Specific triple">
        if (3 == spot.getSpotId()) { // Triple 1
            if (this.rng.isSpecificTriple(1)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (4 == spot.getSpotId()) { // Triple 2
            if (this.rng.isSpecificTriple(2)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (5 == spot.getSpotId()) { // Triple 3
            if (this.rng.isSpecificTriple(3)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (6 == spot.getSpotId()) { // Triple 4
            if (this.rng.isSpecificTriple(4)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (7 == spot.getSpotId()) { // Triple 5
            if (this.rng.isSpecificTriple(5)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (8 == spot.getSpotId()) { // Triple 6
            if (this.rng.isSpecificTriple(6)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Any triple">
        if (15 == spot.getSpotId()) { // Any triple
            if (this.rng.isAnyTriple()) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Specific double">
        if (9 == spot.getSpotId()) { // Double 1
            if (this.rng.isSpecificDouble(1)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (10 == spot.getSpotId()) { // Double 2
            if (this.rng.isSpecificDouble(2)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (11 == spot.getSpotId()) { // Double 3
            if (this.rng.isSpecificDouble(3)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (12 == spot.getSpotId()) { // Double 4
            if (this.rng.isSpecificDouble(4)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (13 == spot.getSpotId()) { // Double 5
            if (this.rng.isSpecificDouble(5)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (14 == spot.getSpotId()) { // Double 6
            if (this.rng.isSpecificDouble(6)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Total == 4 - 17">
        if (16 == spot.getSpotId()) { // Three Dice = 4
            if (this.rng.isTotalEqualTo(4)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (20 == spot.getSpotId()) { // Three Dice = 5
            if (this.rng.isTotalEqualTo(5)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (18 == spot.getSpotId()) { // Three Dice = 6
            if (this.rng.isTotalEqualTo(6)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (22 == spot.getSpotId()) { // Three Dice = 7
            if (this.rng.isTotalEqualTo(7)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (24 == spot.getSpotId()) { // Three Dice = 8
            if (this.rng.isTotalEqualTo(8)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (26 == spot.getSpotId()) { // Three Dice = 9
            if (this.rng.isTotalEqualTo(9)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (34 == spot.getSpotId()) { // Three Dice = 10
            if (this.rng.isTotalEqualTo(10)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (35 == spot.getSpotId()) { // Three Dice = 11
            if (this.rng.isTotalEqualTo(11)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (27 == spot.getSpotId()) { // Three Dice = 12
            if (this.rng.isTotalEqualTo(12)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (25 == spot.getSpotId()) { // Three Dice = 13
            if (this.rng.isTotalEqualTo(13)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (23 == spot.getSpotId()) { // Three Dice = 14
            if (this.rng.isTotalEqualTo(14)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (19 == spot.getSpotId()) { // Three Dice = 15
            if (this.rng.isTotalEqualTo(15)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (21 == spot.getSpotId()) { // Three Dice = 16
            if (this.rng.isTotalEqualTo(16)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (17 == spot.getSpotId()) { // Three Dice = 17
            if (this.rng.isTotalEqualTo(17)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Single Dice = 1,2,3,4,5,6">
        if (28 == spot.getSpotId()) { // Single = 1
            if (this.rng.isSingle(1)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (29 == spot.getSpotId()) { // Single = 2
            if (this.rng.isSingle(2)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (30 == spot.getSpotId()) { // Single = 3
            if (this.rng.isSingle(3)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (31 == spot.getSpotId()) { // Single = 4
            if (this.rng.isSingle(4)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (32 == spot.getSpotId()) { // Single = 5
            if (this.rng.isSingle(5)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        if (33 == spot.getSpotId()) { // Single = 6
            if (this.rng.isSingle(6)) {
                spotAmount = amount * ptnOfSpot.getOdds();
                this.amountBet = this.amountBet + spotAmount;

            } else {
                spotAmount = 0 - amount;
                this.amountBet = this.amountBet + spotAmount;
            }
            return spotAmount;
        }
        //</editor-fold>

        return spotAmount;

    }

    public float getTotalAmount(float[] amounts) {
        int numOfBet = amounts.length;
        float totalAmount = 0;
        for (int iBet = 0; iBet < numOfBet; iBet++) {
            totalAmount = totalAmount + amounts[iBet];
        }

        return totalAmount;
    }
}
