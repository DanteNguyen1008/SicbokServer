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
public class Spots {
    private int spotId;
    private String spotName;
    private int patternId;
    
    private DatabaseHandler databaseHandler;

    public Spots() {
        this.databaseHandler = new DatabaseHandler();
    }
    
    public Spots getSpot(int spotId) throws SQLException {
        Spots result = null;
        
        int countResult = 0;
        ResultSet rs = this.databaseHandler.executeQuery(
                "BET_SPOT_SELECT_BY_ID",
                new String[]{"spotId"},
                new Object[]{spotId});
        rs.next();
        countResult = rs.getRow();
        if (countResult == 1) {
            result = new Spots();
            result.setSpotId(rs.getInt("bet_spot_id"));
            result.setSpotName(rs.getString("name"));
            result.setPatternId(rs.getInt("bet_pattern_id"));
        }
        return result;
    }            
    
    
    //<editor-fold defaultstate="collapsed" desc="Encapsulate fields">
    /**
     * @return the spotId
     */
    public int getSpotId() {
        return spotId;
    }

    /**
     * @param spotId the spotId to set
     */
    public void setSpotId(int spotId) {
        this.spotId = spotId;
    }

    /**
     * @return the spotName
     */
    public String getSpotName() {
        return spotName;
    }

    /**
     * @param spotName the spotName to set
     */
    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

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
    //</editor-fold>

    
}
