/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kent.component;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;
import org.json.simple.JSONObject;

/**
 *
 * @author Kent
 */
public class RandomNumberGenerator {

    private int dice1;
    private int dice2;
    private int dice3;

    public RandomNumberGenerator() {
        RandomData randomData = new RandomDataImpl();
        this.setDice1(randomData.nextInt(1, 6));
        this.setDice2(randomData.nextInt(1, 6));
        this.setDice3(randomData.nextInt(1, 6));
    }

    public int[] getRandom() {
        int[] dices = {0, 0, 0};
        dices[0] = this.getDice1();
        dices[1] = this.getDice2();
        dices[2] = this.getDice3();

        return dices;
    }

    public boolean isBig() {
        if (this.getSum() >= 11 && this.getSum() <= 17 && isTriple() == false) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isSmall() {
        if (this.getSum() >= 4 && this.getSum() <= 10 && isTriple() == false) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isSpecificTriple(int triple) {
        boolean result = false;
        if (this.getDice1() == this.getDice2() && this.getDice2() == this.getDice3() && this.getDice3() == triple) {
            result = true;
        }
        return result;
    }

    public boolean isSpecificDouble(int dbl) {
        boolean result = false;
        if (this.getDice1() == this.getDice2() && this.getDice2() == dbl
                || this.getDice1() == this.getDice3() && this.getDice3() == dbl
                || this.getDice2() == this.getDice3() && this.getDice3() == dbl) {
            result = true;
        }
        return result;
    }

    public boolean isAnyTriple() {
        boolean result = false;
        if (this.getDice1() == this.getDice2()
                && this.getDice2() == this.getDice3()) {
            result = true;
        }
        return result;
    }

    public boolean isTotalEqualTo(int total) {
        boolean result = false;
        if ((this.getDice1() + this.getDice2() + this.getDice3()) == total) {
            result = true;
        }
        return result;
    }

    public boolean isSingle(int number) {
        boolean result = false;
        if (this.getDice1() == number
                || this.getDice2() == number
                || this.getDice3() == number) {
            result = true;
        }
        return result;
    }

    

    public boolean isTriple() {
        if (this.getDice1() == this.getDice2() && this.getDice2() == this.getDice3()) {
            return true;
        } else {
            return false;
        }
    }

    public int getSum() {
        int sum = 0;
        sum = this.getDice1() + this.getDice2() + this.getDice3();
        return sum;
    }
    
    public static JSONObject getThreeDiceRandom() {

        RandomData randomData = new RandomDataImpl();
        JSONObject jsonResult = new JSONObject();
        JSONObject jsonDices = new JSONObject();

        jsonDices.put("dice1", randomData.nextInt(1, 6));
        jsonDices.put("dice2", randomData.nextInt(1, 6));
        jsonDices.put("dice3", randomData.nextInt(1, 6));

        jsonResult.put("three_dice_random", jsonDices);
        return jsonResult;
    }

    //<editor-fold defaultstate="collapsed" desc="Encapsulate fields">
    /**
     * @return the dice1
     */
    public int getDice1() {
        return dice1;
    }

    /**
     * @param dice1 the dice1 to set
     */
    public void setDice1(int dice1) {
        this.dice1 = dice1;
    }

    /**
     * @return the dice2
     */
    public int getDice2() {
        return dice2;
    }

    /**
     * @param dice2 the dice2 to set
     */
    public void setDice2(int dice2) {
        this.dice2 = dice2;
    }

    /**
     * @return the dice3
     */
    public int getDice3() {
        return dice3;
    }

    /**
     * @param dice3 the dice3 to set
     */
    public void setDice3(int dice3) {
        this.dice3 = dice3;
    }
    //</editor-fold>
}
