/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent.component;

import java.sql.ResultSet;
import java.sql.SQLException;
import kent.database.DatabaseHandler;


/**
 *
 * @author Kent
 */
public class Pattern {
    private int patternId;
    private String patternName;
    private float probability;
    private float odds;
    private float houseEdge;
    private float RTP;
    
    private DatabaseHandler databaseHandler;

    public Pattern() {
        this.databaseHandler = new DatabaseHandler();
    }
    
    public Pattern getPattern(int patternId) throws SQLException {
        Pattern result = null;
        
        int countResult = 0;
        ResultSet rs = this.databaseHandler.executeQuery(
                "BET_PATTERN_SELECT_BY_ID",
                new String[]{"patternId"},
                new Object[]{patternId});
        rs.next();
        countResult = rs.getRow();
        if (countResult == 1) {
            result = new Pattern();
            result.patternId = rs.getInt("bet_pattern_id");
            result.patternName = rs.getString("name");
            result.probability = rs.getFloat("probability");
            result.odds = rs.getFloat("odds");
            result.houseEdge = rs.getFloat("house_edge");
            result.RTP = rs.getFloat("rtp");                        
        }
        return result;
    }            
    
    
    //<editor-fold defaultstate="collapsed" desc="Encapsulate fields">
    /**
     * @return the patternId
     */
    public int getPatternId() {
        return patternId;
    }
    
    /**
     * @param patternId the patternId to set
     */
    public void setPatternId(int patternId) {
        this.patternId = patternId;
    }
    
    /**
     * @return the patternName
     */
    public String getPatternName() {
        return patternName;
    }
    
    /**
     * @param patternName the patternName to set
     */
    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }
    
    /**
     * @return the probability
     */
    public float getProbability() {
        return probability;
    }
    
    /**
     * @param probability the probability to set
     */
    public void setProbability(float probability) {
        this.probability = probability;
    }
    
    /**
     * @return the odds
     */
    public float getOdds() {
        return odds;
    }
    
    /**
     * @param odds the odds to set
     */
    public void setOdds(float odds) {
        this.odds = odds;
    }
    
    /**
     * @return the houseEdge
     */
    public float getHouseEdge() {
        return houseEdge;
    }
    
    /**
     * @param houseEdge the houseEdge to set
     */
    public void setHouseEdge(float houseEdge) {
        this.houseEdge = houseEdge;
    }
    
    /**
     * @return the RTP
     */
    public float getRTP() {
        return RTP;
    }
    
    /**
     * @param RTP the RTP to set
     */
    public void setRTP(float RTP) {
        this.RTP = RTP;
    }
    //</editor-fold>
}
