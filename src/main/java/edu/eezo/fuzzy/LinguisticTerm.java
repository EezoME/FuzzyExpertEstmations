package edu.eezo.fuzzy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eezo on 26.11.2016.
 */
public class LinguisticTerm implements Cloneable {
    private String name;
    private String shortName;
    private LTType type;
    private double[] points;

    /**
     * Default linguistic term with trapezoidal type.
     */
    public LinguisticTerm() {
        this.name = "";
        this.shortName = "";
        this.type = LTType.TRAPEZOIDAL;
        this.points = new double[4];
    }

    public LinguisticTerm(String name, String shortName, LTType type, double[] points) {
        this.name = name;
        this.shortName = shortName;
        this.type = type;
        this.points = points;
    }

    /**
     * Returns points of linguistic term.
     * Always returns 4 points (transfers from triangular to trapezoidal automatically if necessary).
     *
     * @return an array of 4 doubles
     */
    public double[] getTrapezodialPoints() {
        if (type == LTType.TRAPEZOIDAL) {
            return points;
        } else {
            double[] points = new double[4];
            points[0] = this.points[0];
            points[1] = this.points[1];
            points[2] = this.points[1];
            points[3] = this.points[2];
            return points;
        }
    }


    /* STATIC GENERATORS */

    /**
     * Generates default term list.
     * Depending on the <code>termsCount</code> uses:
     * <li>for 3 terms - <code>generateListWithThreeTerms()</code> - "Н, С, В"</li>
     * <li>for 4 terms - <code>generateListWithFourTerms()</code> - "Н, НС, ВС, В"</li>
     * <li>for 5 terms - <code>generateListWithFiveTerms()</code> - "ОН, Н, С, В, ОВ"</li>
     * <li>for 6 terms - <code>generateListWithSixTerms()</code> - "ОН, Н, НС, ВС, В, ОВ"</li>
     * <li>for 7 terms - <code>generateListWithSevenTerms()</code> - "ОН, Н, НС, С, ВС, В, ОВ"</li>
     *
     * @param termsCount a number of terms to generate
     * @return generated linguistic term list
     */
    public static List<LinguisticTerm> generateTermList(int termsCount) {
        if (termsCount == 3) return generateListWithThreeTerms();
        if (termsCount == 4) return generateListWithFourTerms();
        if (termsCount == 5) return generateListWithFiveTerms();
        if (termsCount == 6) return generateListWithSixTerms();
        if (termsCount == 7) return generateListWithSevenTerms();

        return null;
    }

    /**
     * Generates a list of 3 linguistic terms: "Н, С, В"
     *
     * @return a list of linguistic terms
     */
    public static List<LinguisticTerm> generateListWithThreeTerms() {
        List<LinguisticTerm> list = new ArrayList<>();

        list.add(new LinguisticTerm("Низкий", "Н", LTType.TRIANGULAR, new double[]{0.0, 0.0, 0.5}));
        list.add(new LinguisticTerm("Средний", "С", LTType.TRIANGULAR, new double[]{0.0, 0.5, 1.0}));
        list.add(new LinguisticTerm("Высокий", "В", LTType.TRIANGULAR, new double[]{0.5, 1.0, 1.0}));

        return list;
    }

    /**
     * Generates a list of 4 linguistic terms: "Н, НС, ВС, В"
     *
     * @return a list of linguistic terms
     */
    public static List<LinguisticTerm> generateListWithFourTerms() {
        List<LinguisticTerm> list = new ArrayList<>();

        list.add(new LinguisticTerm("Низкий", "Н", LTType.TRIANGULAR, new double[]{0.0, 0.0, 0.33}));
        list.add(new LinguisticTerm("Ниже среднего", "НС", LTType.TRIANGULAR, new double[]{0.0, 0.33, 0.67}));
        list.add(new LinguisticTerm("Выше среднего", "ВС", LTType.TRIANGULAR, new double[]{0.33, 0.67, 1.0}));
        list.add(new LinguisticTerm("Высокий", "В", LTType.TRIANGULAR, new double[]{0.67, 1.0, 1.0}));

        return list;
    }

    /**
     * Generates a list of 5 linguistic terms: "ОН, Н, С, В, ОВ"
     *
     * @return a list of linguistic terms
     */
    public static List<LinguisticTerm> generateListWithFiveTerms() {
        List<LinguisticTerm> list = new ArrayList<>();

        list.add(new LinguisticTerm("Очень низкий", "ОН", LTType.TRIANGULAR, new double[]{0.0, 0.0, 0.25}));
        list.add(new LinguisticTerm("Низкий", "Н", LTType.TRIANGULAR, new double[]{0.0, 0.25, 0.5}));
        list.add(new LinguisticTerm("Средний", "С", LTType.TRIANGULAR, new double[]{0.25, 0.5, 0.75}));
        list.add(new LinguisticTerm("Высокий", "В", LTType.TRIANGULAR, new double[]{0.5, 0.75, 1.0}));
        list.add(new LinguisticTerm("Очень высокий", "ОВ", LTType.TRIANGULAR, new double[]{0.75, 1.0, 1.0}));

        return list;
    }

    /**
     * Generates a list of 6 linguistic terms: "ОН, Н, НС, ВС, В, ОВ"
     *
     * @return a list of linguistic terms
     */
    public static List<LinguisticTerm> generateListWithSixTerms() {
        List<LinguisticTerm> list = new ArrayList<>();

        list.add(new LinguisticTerm("Очень низкий", "ОН", LTType.TRIANGULAR, new double[]{0.0, 0.0, 0.2}));
        list.add(new LinguisticTerm("Низкий", "Н", LTType.TRIANGULAR, new double[]{0.0, 0.2, 0.4}));
        list.add(new LinguisticTerm("Ниже среднего", "НС", LTType.TRIANGULAR, new double[]{0.2, 0.4, 0.6}));
        list.add(new LinguisticTerm("Выше среднего", "ВС", LTType.TRIANGULAR, new double[]{0.4, 0.6, 0.8}));
        list.add(new LinguisticTerm("Высокий", "В", LTType.TRIANGULAR, new double[]{0.6, 0.8, 1.0}));
        list.add(new LinguisticTerm("Очень высокий", "ОВ", LTType.TRIANGULAR, new double[]{0.8, 1.0, 1.0}));

        return list;
    }

    /**
     * Generates a list of 7 linguistic terms: "ОН, Н, НС, С, ВС, В, ОВ"
     *
     * @return a list of linguistic terms
     */
    public static List<LinguisticTerm> generateListWithSevenTerms() {
        List<LinguisticTerm> list = new ArrayList<>();

        list.add(new LinguisticTerm("Очень низкий", "ОН", LTType.TRIANGULAR, new double[]{0.0, 0.0, 0.17}));
        list.add(new LinguisticTerm("Низкий", "Н", LTType.TRIANGULAR, new double[]{0.0, 0.17, 0.33}));
        list.add(new LinguisticTerm("Ниже среднего", "НС", LTType.TRIANGULAR, new double[]{0.17, 0.33, 0.5}));
        list.add(new LinguisticTerm("Средний", "С", LTType.TRIANGULAR, new double[]{0.33, 0.5, 0.67}));
        list.add(new LinguisticTerm("Выше среднего", "ВС", LTType.TRIANGULAR, new double[]{0.5, 0.67, 0.83}));
        list.add(new LinguisticTerm("Высокий", "В", LTType.TRIANGULAR, new double[]{0.67, 0.83, 1.0}));
        list.add(new LinguisticTerm("Очень высокий", "ОВ", LTType.TRIANGULAR, new double[]{0.83, 1.0, 1.0}));

        return list;
    }

    public static java.util.List<LinguisticTerm> normalizeData(java.util.List<LinguisticTerm> terms) {
        double max = -Double.MAX_VALUE;

        for (int i = 0; i < terms.size(); i++) {
            for (int j = 0; j < terms.get(i).getPoints().length; j++) {
                if (max < terms.get(i).getPoints()[j]) {
                    max = terms.get(i).getPoints()[j];
                }
            }
        }

        if (Double.compare(max, 0.0) == 0) return terms;

        double coeff = 1 / max;

        for (int i = 0; i < terms.size(); i++) {
            for (int j = 0; j < terms.get(i).getPoints().length; j++) {
                terms.get(i).getPoints()[j] *= coeff;
            }
        }

        return terms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public LTType getType() {
        return type;
    }

    public void setType(LTType type) {
        this.type = type;
    }

    public double[] getPoints() {
        return points;
    }

    public void setPoints(double[] points) {
        this.points = points;
    }

    protected LinguisticTerm makeClone() {
        return new LinguisticTerm(this.name, this.shortName, this.type, this.points.clone());
    }
}
