package edu.eezo.fuzzy;

import javax.swing.*;
import java.util.List;

/**
 * Representation of criteria in fuzzy logic system.
 * Created by Eezo on 26.11.2016.
 */
public class Criteria {

    /**
     * Short name.
     * Usually, contains one letter and one number (f.e.: "Q1").
     */
    private String mark;

    /**
     * A description of the criteria.
     * Usually, one word or combination of words.
     */
    private String name;

    /**
     * A list of linguistic terms.
     */
    private List<LinguisticTerm> lts;

    public Criteria(int index) {
        this.mark = "Q" + index;
    }

    /**
     * Updates linguistic term data.
     * Set data parameter to null if you don't want to update this parameter.
     *
     * @param ltIndex   index of the linguistic term
     * @param longName  the full name of the linguistic term
     * @param shortName the short name of the linguistic term
     * @param type      the type of the linguistic term
     * @param points    points of the linguistic term
     */
    public void updateLT(int ltIndex, String longName, String shortName, LTType type, double[] points) {
        if (this.lts.size() == 0 || ltIndex < 0 || ltIndex >= this.lts.size()) {
            System.out.println("WARN: term's index is out of range. Term has not been updated.");
            return;
        }

        if (longName != null) this.lts.get(ltIndex).setName(longName);
        if (shortName != null) this.lts.get(ltIndex).setShortName(shortName);
        if (type != null) this.lts.get(ltIndex).setType(type);
        if (points != null) this.lts.get(ltIndex).setPoints(points);

    }

    /**
     * Adds to specified terms all missing terms, that are between specified.<br>
     * Number of terms depends on each particular criteria.<br>
     * F.e.: "L,H" -> "L,M,H"; "VL,M,VH" -> "VL,L,M,H,VH"
     *
     * @param lts specified linguistic terms
     * @return supplemented term list as String
     */
    public String getLTWithIntermediateValues(String lts) {
        if (lts == null || lts.isEmpty()) {
            return "";
        }

        try {
            String[] localLTs = getLocalLTs();
            String[] separatedLTs = lts.split(",| ");
            int startIndex = -1;
            int endIndex = -1;

            for (int i = 0; i < localLTs.length; i++) {
                if (localLTs[i].equals(separatedLTs[0])) {
                    startIndex = i;
                }
                if (localLTs[i].equals(separatedLTs[separatedLTs.length - 1])) {
                    endIndex = i;
                    break;
                }
            }

            String resultLTs = "";
            for (int i = startIndex; i <= endIndex; i++) {
                resultLTs += localLTs[i] + ',';
            }


            return resultLTs.substring(0, resultLTs.lastIndexOf(","));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERR: Inputted value does not match specified LTs.");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Makes aggregation of fuzzy number (linguistic term(s)).<br>
     * F.e.: <code>"L" -> "[ 0 ; 0 ; 0.5 ]"</code><br>
     * Also converts triangular number to trapezoidal.<br>
     * F.e.: <code>"( 0 ; 0.5 ; 1 )" -> "( 0 ; 0.5 ; 0.5 ; 1 )"</code>
     *
     * @param lts linguistic terms
     * @return an aggregation expression
     */
    public String aggregateLTs(String lts) {
        if (lts == null || lts.isEmpty()) {
            return "( ; ; ; )";
        }

        // "L" | "L,M,H"
        String[] separatedLTs = lts.split(",| ");
        double[][] allLTsPoints = new double[separatedLTs.length][4];

        for (int i = 0; i < separatedLTs.length; i++) {
            for (int j = 0; j < this.lts.size(); j++) {
                if (this.lts.get(j).getShortName().equals(separatedLTs[i])) {
                    allLTsPoints[i] = this.lts.get(j).getTrapezodialPoints();
                }
            }
        }

        double[] points = new double[4];
        points[0] = Double.MAX_VALUE;
        points[1] = Double.MAX_VALUE;
        points[2] = -Double.MAX_VALUE;
        points[3] = -Double.MAX_VALUE;

        for (int i = 0; i < allLTsPoints.length; i++) {
            if (points[0] > allLTsPoints[i][0]) points[0] = allLTsPoints[i][0];
            if (points[1] > allLTsPoints[i][1]) points[1] = allLTsPoints[i][1];
            if (points[2] < allLTsPoints[i][2]) points[2] = allLTsPoints[i][2];
            if (points[3] < allLTsPoints[i][3]) points[3] = allLTsPoints[i][3];
        }

        return "( " + points[0] + " ; " + points[1] + " ; " + points[2] + " ; " + points[3] + " )";
    }

    /**
     * Calculates an alpha cut for specified aggregation expression (linguistic term after aggregation).<br>
     * Returns a short aggregation expression like "[ 0 ; 0.5 ]".<br>
     * Returns an empty string if: aggregation expression is empty or alpha value is out of range.
     *
     * @param aggregationExpression specified aggregation expression
     * @param alpha                 alpha value (in range [0..1])
     * @return a short aggregation expression
     */
    public static String alphaCut(String aggregationExpression, double alpha) {
        if (aggregationExpression == null || aggregationExpression.isEmpty()) {
            return "";
        }

        if (alpha < 0 || alpha > 1) {
            System.out.println("WARN: alpha value is out of range.");
            return "";
        }

        // "( 0.0 ; 0.5 ; 0.5 ; 1.0 )"
        double[] numbers = splitAggregationExpression(aggregationExpression);

        double left = numbers[0] + alpha * (numbers[1] - numbers[0]);
        double right = numbers[3] - alpha * (numbers[3] - numbers[2]);

        return "[ " + left + " ; " + right + " ]";
    }

    /**
     * Makes a convolution through optimistic way (using MAX function).<br>
     * Returns a short aggregation expression (called the interval) like in <code>alphaCut</code>, but this one applies to the alternative.
     *
     * @param aggrExpressions aggregation expressions of the alternative
     * @return array of two doubles
     */
    public static double[] optimisticConvolution(String[] aggrExpressions) {
        if (aggrExpressions == null || aggrExpressions.length == 0) {
            return null;
        }

        double[][] numbers = new double[aggrExpressions.length][2];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = splitAggregationExpression(aggrExpressions[i]);
        }

        double maxLeft = -Double.MAX_VALUE;
        double maxRight = -Double.MAX_VALUE;

        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i][0] > maxLeft) maxLeft = numbers[i][0];
            if (numbers[i][1] > maxRight) maxRight = numbers[i][1];
        }

        return new double[]{maxLeft, maxRight};
    }

    /**
     * Makes a convolution through pessimistic way (using MIN function).<br>
     * Returns a short aggregation expression (called the interval) like in <code>alphaCut</code>, but this one applies to the alternative.
     *
     * @param aggrExpressions aggregation expressions of the alternative
     * @return array of two doubles
     */
    public static double[] pessimisticConvolution(String[] aggrExpressions) {
        if (aggrExpressions == null || aggrExpressions.length == 0) {
            return null;
        }

        double[][] numbers = new double[aggrExpressions.length][2];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = splitAggregationExpression(aggrExpressions[i]);
        }

        double minLeft = Double.MAX_VALUE;
        double minRight = Double.MAX_VALUE;

        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i][0] < minLeft) minLeft = numbers[i][0];
            if (numbers[i][1] < minRight) minRight = numbers[i][1];
        }

        return new double[]{minLeft, minRight};
    }

    /**
     * Splits and converts specified aggregation expression to doubles it contains.
     * Result array contains three doubles if that aggregation expression from triangular number,
     * four - if it from trapezoidal.
     *
     * @param aggregationExpression aggregation expression
     * @return an array of doubles
     */
    private static double[] splitAggregationExpression(String aggregationExpression) {
        if (aggregationExpression == null || aggregationExpression.isEmpty()) {
            return null;
        }

        // "( 0.0 ; 0.5 ; 0.5 ; 1.0 )"
        String[] separateNumbers = aggregationExpression.split(";| |\\(|\\)|\\[|\\]");
        int count = 0;

        for (int i = 0; i < separateNumbers.length; i++) {
            if (!separateNumbers[i].equals("")) count++;
        }

        String[] nonEmptyNumbers = new String[count];
        count = 0;

        for (int i = 0; i < separateNumbers.length; i++) {
            if (!separateNumbers[i].equals("")) {
                nonEmptyNumbers[count] = separateNumbers[i];
                count++;
            }
        }

        double[] numbers = new double[nonEmptyNumbers.length];

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Double.parseDouble(nonEmptyNumbers[i]);
        }

        return numbers;
    }

    /**
     * Returns a full list of linguistic terms of this criteria.
     *
     * @return an array of short names of linguistic terms
     */
    private String[] getLocalLTs() {
        String[] local = new String[lts.size()];

        for (int i = 0; i < local.length; i++) {
            local[i] = lts.get(i).getShortName();
        }

        return local;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LinguisticTerm> getLts() {
        return lts;
    }

    public void setLts(List<LinguisticTerm> lts) {
        this.lts = lts;
    }

    @Override
    public String toString() {
        return this.mark;
    }
}
