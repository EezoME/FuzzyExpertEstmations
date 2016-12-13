package edu.eezo.fuzzy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Multi-Criteria Decision-Making Method Based On Fuzzy Expert Estimations.
 * <p>
 * Created by Eezo on 26.11.2016.
 */
public class MainGUI extends JFrame {
    private JPanel rootPanel;
    private JTabbedPane tabbedPane1;
    private JTextField textFieldAlternativesCount;
    private JTextField textFieldCriteriaCount;
    private JButton buttonAcceptCounts;
    private JTable tableDecisionMatrixInitial;
    private JButton buttonNextTab;
    private JComboBox comboBoxCriteria;
    private JTextField textFieldCriteriaName;
    private JTextField textFieldCriteriaLTCount;
    private JTextField textFieldLTLongName;
    private JTextField textFieldLTShortName;
    private JComboBox comboBoxLTType;
    private JTextField textFieldLTP1;
    private JTextField textFieldLTP2;
    private JTextField textFieldLTP3;
    private JTextField textFieldLTP4;
    private JButton buttonPrevLT;
    private JButton buttonNextLT;
    private JLabel labelLTNo;
    private JButton buttonCriteriaGraph;
    private JButton buttonLTAccept;
    private JTable tableLT;
    private JTable tableLTFull;
    private JTable tableAggregation;
    private JTextField textFieldAlpha;
    private JTable tableAlpha;
    private JButton buttonSaveLT;
    private JButton buttonSaveCriteria;
    private JCheckBox checkBoxGenerateLT;
    private JProgressBar progressBarDone;
    private JButton buttonDoAlpha;
    private JLabel labelOptimisticResult;
    private JTable tableConvolution;
    private JLabel labelPessimisticResult;
    private JLabel labelGeneralResult;
    private JTable tableAggregationGeneral;
    private JTable tableAlphaGeneral;
    private JButton buttonNextCriteria;
    private JButton buttonEnableDataInput;
    private JTable tableResult;
    private JTextArea textAreaResult;

    private int alternativesCount;
    private int criteriaCount;
    private Criteria[] criterias;
    private boolean[] criteriaChecked;

    /**
     * Data input stage.<br>
     * 0 - no data entered <br>
     * 1 - counts entered, criteria list initialized <br>
     * 2 - criteria's name and LT count entered, LT list initialized <br>
     * 3 - all LT data entered correctly <br>
     * 4 - saved stage 3 (criteria saved)<br>
     * 5 - second tab before alpha input<br>
     * 6 - second tab after alpha input<br>
     */
    private static int stage = 0;


    public MainGUI() {
        super("Fuzzy Expert Estimations");
        setSize(1050, 660);
        setLocationRelativeTo(null);
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        comboBoxLTType.addItem(LTType.TRAPEZOIDAL);
        comboBoxLTType.addItem(LTType.TRIANGULAR);


        /* Disable until count entered */
        toggleCriteriaComponents(false);
        /* Disable until LT count accepted */
        toggleLTComponents(false);
        /* Disable until criteria accepted */
        toggleDecisionMatrixComponents(false);
        /* Disable until decision matrix accepted */
        toggleSecondTabComponents(false);
        /* Always disable */
        togglePermanentSecondTabComponents(false);

        buttonAcceptCounts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acceptCounts();
            }
        });

        buttonLTAccept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acceptLT();
            }
        });

        buttonSaveLT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveLT();
            }
        });

        buttonSaveCriteria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCriteria();
            }
        });

        buttonNextTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToSecondTab();
            }
        });

        buttonDoAlpha.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doNextOnSecondTab();
            }
        });

        comboBoxLTType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboBoxLTType.getSelectedItem().equals(LTType.TRIANGULAR)) {
                    textFieldLTP4.setVisible(false);
                } else {
                    textFieldLTP4.setVisible(true);
                }
            }
        });

        comboBoxCriteria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stage > 1) showCriteria(comboBoxCriteria.getSelectedIndex(), stage);
            }
        });

        buttonPrevLT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentLtIndex = Integer.parseInt(labelLTNo.getText());
                int ltIndex = (currentLtIndex > 0) ? currentLtIndex - 1 : currentLtIndex;

                if (ltIndex != currentLtIndex) {
                    showLTProperties(comboBoxCriteria.getSelectedIndex(), ltIndex, stage);
                    labelLTNo.setText(ltIndex + "");
                }
            }
        });

        buttonNextLT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentLtIndex = Integer.parseInt(labelLTNo.getText());
                int ltIndex = (currentLtIndex >= criterias[comboBoxCriteria.getSelectedIndex()].getLts().size() - 1) ? currentLtIndex : currentLtIndex + 1;

                if (ltIndex != currentLtIndex) {
                    showLTProperties(comboBoxCriteria.getSelectedIndex(), ltIndex, stage);
                    labelLTNo.setText(ltIndex + "");
                }
            }
        });

        buttonCriteriaGraph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stage >= 3)
                    Chart.main(criterias[comboBoxCriteria.getSelectedIndex()].toString(),
                            criterias[comboBoxCriteria.getSelectedIndex()].getName(),
                            criterias[comboBoxCriteria.getSelectedIndex()].getLts());
            }
        });

        buttonNextCriteria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int nextIndex = comboBoxCriteria.getSelectedIndex() + 1;
                if (nextIndex >= criterias.length) nextIndex = 0;
                comboBoxCriteria.setSelectedIndex(nextIndex);
            }
        });

        textFieldLTP1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                checkBoxGenerateLT.setSelected(false);
            }
        });

        textFieldLTP2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                checkBoxGenerateLT.setSelected(false);
            }
        });

        textFieldLTP3.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                checkBoxGenerateLT.setSelected(false);
            }
        });

        textFieldLTP4.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                checkBoxGenerateLT.setSelected(false);
            }
        });

        textFieldLTLongName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                checkBoxGenerateLT.setSelected(false);
            }
        });

        textFieldLTShortName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                checkBoxGenerateLT.setSelected(false);
            }
        });
        buttonEnableDataInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleDecisionMatrixComponents(false);
                toggleLTComponents(true);
                toggleCriteriaComponents(true);
                progressBarDone.setValue(0);
            }
        });
    }

    /**
     * Stage 1.<br>
     * Install <code>alternativesCount</code> and <code>criteriaCount</code> values.
     * Creates new criteria instants.
     */
    private void acceptCounts() {
        if (SwingUtils.checkTFForPositiveInteger(textFieldAlternativesCount, "Alternatives Count") ||
                SwingUtils.checkTFForPositiveInteger(textFieldCriteriaCount, "Criteria Count")) {
            alternativesCount = Integer.parseInt(textFieldAlternativesCount.getText());
            criteriaCount = Integer.parseInt(textFieldCriteriaCount.getText());
            criterias = new Criteria[criteriaCount];

            toggleCriteriaComponents(true);

            comboBoxCriteria.removeAllItems();

            for (int i = 0; i < criteriaCount; i++) {
                criterias[i] = new Criteria(i + 1);
                comboBoxCriteria.addItem(criterias[i]);
            }

            criteriaChecked = new boolean[criteriaCount];
            Arrays.fill(criteriaChecked, false);
            progressBarDone.setMinimum(0);
            progressBarDone.setMaximum(criteriaCount);

            stage = 1;
        }
    }

    /**
     * Stage 2.<br>
     * Makes first pre-saving for criteria.
     */
    private void acceptLT() {
        if (stage < 1) return;

        saveCriteria(comboBoxCriteria.getSelectedIndex(), stage);

        toggleLTComponents(true);

        stage = 2;
    }

    /**
     * Stage 3.<br>
     * Save linguistic terms data.
     */
    private void saveLT() {
        if (stage < 2) return;

        saveLTProperties(comboBoxCriteria.getSelectedIndex(), Integer.parseInt(labelLTNo.getText()), stage);

        stage = 3;
    }

    /**
     * Stage 4.<br>
     * Saves whole criteria data.
     * Also updates progress bar status.
     */
    private void saveCriteria() {
        if (stage < 2) return;

        saveCriteria(comboBoxCriteria.getSelectedIndex(), stage);
        criteriaChecked[comboBoxCriteria.getSelectedIndex()] = true;
        updateProgressBar();

        if (progressBarDone.getValue() == progressBarDone.getMaximum()) {
            toggleDecisionMatrixComponents(true);
            toggleLTComponents(false);
            toggleCriteriaComponents(false);

            standardTableInitialization(tableDecisionMatrixInitial);
        }

        stage = 4;
    }

    /**
     * Stage 5.<br>
     * Check the decision table and makes linguistic terms unwrapped.
     */
    private void switchToSecondTab() {
        if (stage < 2) return;

        if (!SwingUtils.checkTableForNonEmpty(tableDecisionMatrixInitial)) {
            JOptionPane.showMessageDialog(null, "Fill decision matrix.");
            return;
        }

        for (int i = 0; i < criterias.length; i++) {
            LinguisticTerm.normalizeData(criterias[i].getLts());
        }


        toggleSecondTabComponents(true);

        standardTableInitialization(tableLT);
        standardTableInitialization(tableLTFull);
        standardTableInitialization(tableAggregation);
        standardTableInitialization(tableAlpha);

        SwingUtils.jTableInitiation(tableAggregationGeneral, new String[]{"E\\Q", "Q"}, alternativesCount);
        SwingUtils.jTableInitiation(tableAlphaGeneral, new String[]{"E\\Q", "Q"}, alternativesCount);
        SwingUtils.jTableInitiation(tableConvolution, new String[]{"E", "I_op", "I_pes", "I_agg"}, alternativesCount);
        generateEmptyResultTable();

        tabbedPane1.setSelectedIndex(1);

        // copy Decision table from tab 1 to LT table on tab 2
        for (int i = 0; i < tableDecisionMatrixInitial.getRowCount(); i++) {
            for (int j = 1; j < tableDecisionMatrixInitial.getColumnCount(); j++) {
                tableLT.setValueAt(tableDecisionMatrixInitial.getValueAt(i, j), i, j);
            }
        }

        // Intermediate LT
        for (int i = 0; i < tableLT.getRowCount(); i++) {
            for (int j = 1; j < tableLT.getColumnCount(); j++) {
                tableLTFull.setValueAt(
                        criterias[j - 1].getLTWithIntermediateValues(tableLT.getValueAt(i, j).toString()),
                        i, j);
            }
        }

        stage = 5;
    }

    /**
     * Stage 6.<br>
     * Makes pessimistic, optimistic and aggregation way calculations.
     */
    private void doNextOnSecondTab() {
        if (stage < 2) return;

        if (!SwingUtils.checkTFForDouble(textFieldAlpha, "Alpha value")) return;

        pessimisticAndOptimistic();
        general();

        stage = 6;
    }

    /* CRITERIA METHODS */

    /**
     * Shows criteria on the form.
     *
     * @param index index of criteria
     * @param stage current stage
     */
    private void showCriteria(int index, int stage) {
        if (stage < 2) return;

        textFieldCriteriaName.setText(criterias[index].getName());

        if (criterias[index] != null) {
            if (criterias[index].getLts() != null && criterias[index].getLts().size() != 0) {
                textFieldCriteriaLTCount.setText(criterias[index].getLts().size() + "");
                showLTProperties(index, 0, stage);
                labelLTNo.setText("0");
            } else {
                textFieldLTLongName.setText("");
                textFieldLTShortName.setText("");
                comboBoxLTType.setSelectedIndex(0);
                textFieldLTP1.setText("");
                textFieldLTP2.setText("");
                textFieldLTP3.setText("");
                textFieldLTP4.setText("");
            }
        }
    }

    /**
     * Shows linguistic data on the form.
     *
     * @param index   index of criteria
     * @param ltIndex index of linguistic term
     * @param stage   current stage
     */
    private void showLTProperties(int index, int ltIndex, int stage) {
        if (stage < 2 || criterias[index].getLts() == null) return;

        if (criterias[index].getLts().size() > 0) {
            labelLTNo.setText(ltIndex + "");

            textFieldLTLongName.setText(criterias[index].getLts().get(ltIndex).getName());
            textFieldLTShortName.setText(criterias[index].getLts().get(ltIndex).getShortName());

            if (criterias[index].getLts().get(ltIndex).getType() != null) {
                comboBoxLTType.setSelectedItem(criterias[index].getLts().get(ltIndex).getType());
            }

            if (criterias[index].getLts().get(ltIndex).getPoints() == null) return;

            textFieldLTP1.setText(criterias[index].getLts().get(ltIndex).getPoints()[0] + "");
            textFieldLTP2.setText(criterias[index].getLts().get(ltIndex).getPoints()[1] + "");
            textFieldLTP3.setText(criterias[index].getLts().get(ltIndex).getPoints()[2] + "");

            if (comboBoxLTType.getSelectedItem().equals(LTType.TRAPEZOIDAL)) {
                textFieldLTP4.setText(criterias[index].getLts().get(ltIndex).getPoints()[3] + "");
            }
        }
    }

    /**
     * Saves criteria.
     *
     * @param index index of criteria
     * @param stage current stage
     */
    private void saveCriteria(int index, int stage) {
        if (stage != 1 && stage < 2) return;

        criterias[index].setName(textFieldCriteriaName.getText());

        if (SwingUtils.checkTFForPositiveInteger(textFieldCriteriaLTCount, "LT count")) {
            int ltCount = Integer.parseInt(textFieldCriteriaLTCount.getText());

            if (checkBoxGenerateLT.isSelected()) {
                criterias[index].setLts(LinguisticTerm.generateTermList(ltCount));
            } else {
                if (criterias[index].getLts() == null || criterias[index].getLts().size() == 0) {
                    criterias[index].setLts(new ArrayList<LinguisticTerm>());

                    for (int i = 0; i < ltCount; i++) {
                        criterias[index].getLts().add(new LinguisticTerm());
                    }

                }
            }

            labelLTNo.setText("0");
        }
    }

    /**
     * Saves linguistic terms data.
     *
     * @param index   index of criteria
     * @param ltIndex index of linguistic term
     * @param stage   current stage
     */
    private void saveLTProperties(int index, int ltIndex, int stage) {
        if (stage < 2) return;

        if (checkBoxGenerateLT.isSelected()) {
            int ltCount = Integer.parseInt(textFieldCriteriaLTCount.getText());
            criterias[index].setLts(LinguisticTerm.generateTermList(ltCount));
            return;
        }

        if (!(SwingUtils.checkTFForDouble(textFieldLTP1, "LT point 1") || SwingUtils.checkTFForDouble(textFieldLTP2, "LT point 2") ||
                SwingUtils.checkTFForDouble(textFieldLTP3, "LT point 3"))) {
            return;
        }

        boolean isTrapezoidal = comboBoxLTType.getSelectedItem().equals(LTType.TRAPEZOIDAL);
        if (isTrapezoidal && !SwingUtils.checkTFForDouble(textFieldLTP4, "LT point 4")) {
            return;
        }

        double[] points;
        points = isTrapezoidal ? new double[4] : new double[3];
        points[0] = Double.parseDouble(textFieldLTP1.getText());
        points[1] = Double.parseDouble(textFieldLTP2.getText());
        points[2] = Double.parseDouble(textFieldLTP3.getText());

        if (isTrapezoidal) {
            points[3] = Double.parseDouble(textFieldLTP4.getText());
        }

        if ((points[0] > points[1] || points[1] > points[2]) || (isTrapezoidal && points[2] > points[3])) {
            JOptionPane.showMessageDialog(null, "Wrong points values");
            return;
        }

        criterias[index].updateLT(ltIndex, textFieldLTLongName.getText(), textFieldLTShortName.getText(),
                (LTType) comboBoxLTType.getSelectedItem(), points);
    }


    /* SECOND TAB METHODS */

    private void pessimisticAndOptimistic() {
        long startTime = System.currentTimeMillis();

        // AGGREGATION
        for (int i = 0; i < tableLTFull.getRowCount(); i++) {
            for (int j = 1; j < tableLTFull.getColumnCount(); j++) {
                tableAggregation.setValueAt(
                        criterias[j - 1].aggregateLTs(tableLTFull.getValueAt(i, j).toString()),
                        i, j);
            }
        }

        double alpha = Double.parseDouble(textFieldAlpha.getText());

        for (int i = 0; i < tableAggregation.getRowCount(); i++) {
            for (int j = 1; j < tableAggregation.getColumnCount(); j++) {
                tableAlpha.setValueAt(
                        Criteria.alphaCut(tableAggregation.getValueAt(i, j).toString(), alpha),
                        i, j);
            }
        }

        // convolution
        double[][] convolutionOptimisticResults = new double[tableAlpha.getRowCount()][2];
        double[][] convolutionPessimisticResults = new double[tableAlpha.getRowCount()][2];

        for (int i = 0; i < tableAlpha.getRowCount(); i++) {
            String[] aggrExps = new String[tableAlpha.getColumnCount() - 1];

            for (int j = 1; j < tableAlpha.getColumnCount(); j++) {
                aggrExps[j - 1] = tableAlpha.getValueAt(i, j).toString();
            }

            convolutionOptimisticResults[i] = Criteria.optimisticConvolution(aggrExps);
            tableConvolution.setValueAt(
                    "[ " + convolutionOptimisticResults[i][0] + " ; " + convolutionOptimisticResults[i][1] + " ]",
                    i, 1);

            convolutionPessimisticResults[i] = Criteria.pessimisticConvolution(aggrExps);
            tableConvolution.setValueAt(
                    "[ " + convolutionPessimisticResults[i][0] + " ; " + convolutionPessimisticResults[i][1] + " ]",
                    i, 2);
        }

        // results
        int winOptimistic = -1;
        int winPessimistic = -1;
        double optResMax = Integer.MIN_VALUE;
        double pesResMax = Integer.MIN_VALUE;
        double[] optRes = new double[convolutionOptimisticResults.length];
        double[] pesRes = new double[convolutionOptimisticResults.length];

        for (int i = 0; i < convolutionOptimisticResults.length; i++) {
            optRes[i] = Math.max(1.0 - Math.max(1.0 - convolutionOptimisticResults[i][0], 0.0), 0.0);
            pesRes[i] = Math.max(1.0 - Math.max(1.0 - convolutionOptimisticResults[i][0], 0.0), 0.0);

            if (optRes[i] > optResMax) { // change > to >= for get always last max instead of first
                optResMax = optRes[i];
                winOptimistic = i + 1;
            }

            if (pesRes[i] > pesResMax) {
                pesResMax = pesRes[i];
                winPessimistic = i + 1;
            }
        }

        int[] optimisticIndexes = getSortedIndexes(optRes);
        int[] pessimisticIndexes = getSortedIndexes(pesRes);

        textAreaResult.append("Optimistic: ");

        for (int j = 1; j < tableResult.getColumnCount(); j++) {
            tableResult.setValueAt(optRes[j - 1], 0, j);
            tableResult.setValueAt(pesRes[j - 1], 1, j);
            textAreaResult.append("E" + optimisticIndexes[j - 1] + " > ");
        }

        textAreaResult.replaceRange("", textAreaResult.getText().length() - 3, textAreaResult.getText().length());
        textAreaResult.append("\nPessimistic: ");

        for (int i = 0; i < pessimisticIndexes.length; i++) {
            textAreaResult.append("E" + pessimisticIndexes[i] + " > ");
        }

        textAreaResult.replaceRange("", textAreaResult.getText().length() - 3, textAreaResult.getText().length());
        textAreaResult.append("\n");

        long duration = System.currentTimeMillis() - startTime;

        labelOptimisticResult.setText("Optimistic winner: E" + winOptimistic + " (duration: " + duration + " ms)");
        labelPessimisticResult.setText("Pessimistic winner: E" + winPessimistic + " (duration: " + duration + " ms)");
    }

    private void general() {
        long startTime = System.currentTimeMillis();

        // AGGREGATION
        for (int i = 0; i < tableLTFull.getRowCount(); i++) {
            for (int j = 1; j < tableLTFull.getColumnCount(); j++) {
                tableAggregation.setValueAt(
                        criterias[j - 1].aggregateLTs(tableLTFull.getValueAt(i, j).toString()),
                        i, j);
            }
        }

        for (int i = 0; i < tableAggregationGeneral.getRowCount(); i++) {
            String[] aggExpr = new String[tableAggregation.getColumnCount() - 1];

            for (int j = 1; j < tableAggregation.getColumnCount(); j++) {
                aggExpr[j - 1] = tableAggregation.getValueAt(i, j).toString();
            }

            tableAggregationGeneral.setValueAt(Criteria.rowAggregation(aggExpr), i, 1);
        }

        double alpha = Double.parseDouble(textFieldAlpha.getText());

        for (int i = 0; i < tableAggregationGeneral.getRowCount(); i++) {
            tableAlphaGeneral.setValueAt(
                    Criteria.alphaCut(tableAggregationGeneral.getValueAt(i, 1).toString(), alpha),
                    i, 1);
        }

        // convolution
        double[][] convolutionGeneralResults = new double[tableAlphaGeneral.getRowCount()][2];

        for (int i = 0; i < tableAlphaGeneral.getRowCount(); i++) {
            String aggrExps = tableAlphaGeneral.getValueAt(i, 1).toString();

            convolutionGeneralResults[i] = Criteria.optimisticConvolution(new String[]{aggrExps});
            tableConvolution.setValueAt(
                    "[ " + convolutionGeneralResults[i][0] + " ; " + convolutionGeneralResults[i][1] + " ]",
                    i, 3);
        }

        // results
        int win = -1;
        double agrResMax = Integer.MIN_VALUE;
        double[] agrRes = new double[convolutionGeneralResults.length];

        for (int i = 0; i < convolutionGeneralResults.length; i++) {
            agrRes[i] = Math.max(1.0 - Math.max(1.0 - convolutionGeneralResults[i][0], 0.0), 0.0);
            if (agrRes[i] > agrResMax) {
                agrResMax = agrRes[i];
                win = i + 1;
            }
        }

        int[] aggrIndexes = getSortedIndexes(agrRes);

        textAreaResult.append("General: ");

        for (int j = 1; j < tableResult.getColumnCount(); j++) {
            tableResult.setValueAt(agrRes[j - 1], 2, j);
            textAreaResult.append("E" + aggrIndexes[j - 1] + " > ");
        }

        textAreaResult.replaceRange("", textAreaResult.getText().length() - 3, textAreaResult.getText().length());
        textAreaResult.append("\n");

        long duration = System.currentTimeMillis() - startTime;

        labelGeneralResult.setText("General winner: E" + win + " (duration: " + duration + " ms)");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainGUI();
            }
        });
    }


    /**
     * Initiates specified table with default identifiers ("E\Q", "Q1", "Q2", ..., "Qn")
     * And default first column ("E1", "E2", ..., "En").
     *
     * @param table specified table
     */
    private void standardTableInitialization(JTable table) {
        String[] identifiers = new String[criteriaCount + 1];
        identifiers[0] = "E\\Q";

        for (int i = 1; i < identifiers.length; i++) {
            identifiers[i] = criterias[i - 1].toString();
        }

        SwingUtils.jTableInitiation(table, identifiers, alternativesCount);

        for (int i = 0; i < alternativesCount; i++) {
            table.setValueAt("E" + (i + 1), i, 0);
        }
    }

    /**
     * Updates progress bar status.
     * Uses <code>criteriaChecked</code> as basis.
     */
    private void updateProgressBar() {
        int checkedCount = 0;

        for (int i = 0; i < criteriaChecked.length; i++) {
            if (criteriaChecked[i]) checkedCount++;
        }

        progressBarDone.setValue(checkedCount);
    }


    /**
     * Sorts indexes of specified array in ordinary order (from more to less).
     * This method do not sorts specified array itself, it sorts only indexes and return a new array
     *
     * @param array specified array
     * @return an array of indexes
     */
    public static int[] getSortedIndexes(double[] array) {
        if (array == null || array.length == 0) {
            return new int[0];
        }

        int[] indexes = new int[array.length];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i + 1;
        }

        for (int i = 0; i < array.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < array.length - i - 1; j++) {
                if (Double.compare(array[j], array[j + 1]) > 0) {
                    int tmp = indexes[j];
                    indexes[j] = indexes[j + 1];
                    indexes[j + 1] = tmp;
                    swapped = true;
                }
            }

            if (!swapped)
                break;
        }

        return indexes;
    }

    /**
     * Generates a result table with initial data.
     */
    private void generateEmptyResultTable() {
        DefaultTableModel model = (DefaultTableModel) tableResult.getModel();
        String[] identifiers = new String[alternativesCount + 1];
        identifiers[0] = "Method";
        for (int i = 1; i < identifiers.length; i++) {
            identifiers[i] = "E" + i;
        }
        model.setColumnIdentifiers(identifiers);
        model.setRowCount(3);
        model.setValueAt("Optimistic", 0, 0);
        model.setValueAt("Pessimistic", 1, 0);
        model.setValueAt("General", 2, 0);
    }

    /* TOGGLES */

    /**
     * Changes components enable status.
     * Opens/closes components for stage 2.
     *
     * @param isEnable component enable status
     */
    private void toggleCriteriaComponents(boolean isEnable) {
        comboBoxCriteria.setEnabled(isEnable);
        textFieldCriteriaName.setEnabled(isEnable);
        textFieldCriteriaLTCount.setEnabled(isEnable);
        checkBoxGenerateLT.setEnabled(isEnable);
        buttonLTAccept.setEnabled(isEnable);
    }

    /**
     * Changes components enable status.
     * Opens/closes components for stage 3.
     *
     * @param isEnable component enable status
     */
    private void toggleLTComponents(boolean isEnable) {
        textFieldLTLongName.setEnabled(isEnable);
        textFieldLTShortName.setEnabled(isEnable);
        comboBoxLTType.setEnabled(isEnable);
        textFieldLTP1.setEnabled(isEnable);
        textFieldLTP2.setEnabled(isEnable);
        textFieldLTP3.setEnabled(isEnable);
        textFieldLTP4.setEnabled(isEnable);
        buttonPrevLT.setEnabled(isEnable);
        buttonNextLT.setEnabled(isEnable);
        buttonSaveLT.setEnabled(isEnable);
        buttonCriteriaGraph.setEnabled(isEnable);
        buttonSaveCriteria.setEnabled(isEnable);
        buttonNextCriteria.setEnabled(isEnable);
    }

    /**
     * Changes components enable status.
     * Opens/closes components for stage 4.
     *
     * @param isEnable component enable status
     */
    private void toggleDecisionMatrixComponents(boolean isEnable) {
        tableDecisionMatrixInitial.setEnabled(isEnable);
        buttonNextTab.setEnabled(isEnable);
        buttonEnableDataInput.setEnabled(isEnable);
    }

    /**
     * Changes components enable status.
     * Opens/closes components for stage 5.
     *
     * @param isEnable component enable status
     */
    private void toggleSecondTabComponents(boolean isEnable) {
        textFieldAlpha.setEnabled(isEnable);
        buttonDoAlpha.setEnabled(isEnable);
    }

    /**
     * Changes components enable status.
     * Opens/closes tables on tab 2.
     *
     * @param isEnable component enable status
     */
    private void togglePermanentSecondTabComponents(boolean isEnable) {
        tableLT.setEnabled(isEnable);
        tableLTFull.setEnabled(isEnable);
        tableAggregation.setEnabled(isEnable);
        tableAggregationGeneral.setEnabled(isEnable);
        tableAlpha.setEnabled(isEnable);
        tableAlphaGeneral.setEnabled(isEnable);
        tableConvolution.setEnabled(isEnable);
    }
}
