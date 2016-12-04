package edu.eezo.fuzzy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JLabel labelAggregationResult;
    private JTable tableAggregationSecondWay;
    private JTable tableAlphaSecondWay;

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
     * 4 - saved stage 3 <br>
     */
    private static int stage = 0;


    public MainGUI() {
        super("Fuzzy Expert Estimations");
        setSize(840, 540);
        setLocationRelativeTo(null);
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        comboBoxLTType.addItem(LTType.TRAPEZOIDAL);
        comboBoxLTType.addItem(LTType.TRIANGULAR);
        tableDecisionMatrixInitial.setDragEnabled(false);
        tableLT.setEnabled(false);
        tableLTFull.setEnabled(false);
        tableAggregation.setEnabled(false);
        tableAlpha.setEnabled(false);
        tableConvolution.setEnabled(false);


        /* Disable until count entered */
        toggleCriteriaComponents(false);
        /* Disable until LT count accepted */
        toggleLTComponents(false);
        /* Disable until criteria accepted */
        toggleDecisionMatrixComponents(false);
        /* Disable until decision matrix accepted */
        toggleSecondTabComponents(false);

        buttonAcceptCounts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkTFForPositiveInteger(textFieldAlternativesCount, "Alternatives Count") ||
                        checkTFForPositiveInteger(textFieldCriteriaCount, "Criteria Count")) {
                    alternativesCount = Integer.parseInt(textFieldAlternativesCount.getText());
                    criteriaCount = Integer.parseInt(textFieldCriteriaCount.getText());
                    toggleCriteriaComponents(true);
                    criterias = new Criteria[criteriaCount];

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
        });

        buttonLTAccept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stage = 2;
                saveCriteria(comboBoxCriteria.getSelectedIndex(), stage);
                toggleLTComponents(true);
            }
        });

        buttonSaveLT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stage = 3;
                saveLTProperties(comboBoxCriteria.getSelectedIndex(), Integer.parseInt(labelLTNo.getText()), stage);
            }
        });

        buttonSaveCriteria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stage < 2) return;
                stage = 4;
                saveCriteria(comboBoxCriteria.getSelectedIndex(), stage);
                criteriaChecked[comboBoxCriteria.getSelectedIndex()] = true;
                updateProgressBar();

                if (progressBarDone.getValue() == progressBarDone.getMaximum()) {
                    toggleDecisionMatrixComponents(true);

                    standardTableInitialization(tableDecisionMatrixInitial);
                }
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
                showCriteria(comboBoxCriteria.getSelectedIndex(), stage);
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
    }

    /* CRITERIA METHODS */

    private void showCriteria(int index, int stage) {
        if (stage == 1) return;

        textFieldCriteriaName.setText(criterias[index].getName());

        if (criterias[index].getLts() != null) {
            textFieldCriteriaLTCount.setText(criterias[index].getLts().size() + "");
            showLTProperties(index, 0, stage);
        }
    }

    private void showLTProperties(int index, int ltIndex, int stage) {
        if (stage < 2) return;

        if (criterias[index].getLts().size() > 0) {
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

    private void saveCriteria(int index, int stage) {
        if (stage < 1) return;

        criterias[index].setName(textFieldCriteriaName.getText());

        if (checkTFForPositiveInteger(textFieldCriteriaLTCount, "LT count")) {
            int ltCount = Integer.parseInt(textFieldCriteriaLTCount.getText());

            if (checkBoxGenerateLT.isSelected()) {
                criterias[index].setLts(LinguisticTerm.generateTermList(ltCount));
            } else {
                criterias[index].setLts(new ArrayList<LinguisticTerm>());

                for (int i = 0; i < ltCount; i++) {
                    criterias[index].getLts().add(new LinguisticTerm());
                }
            }

            labelLTNo.setText("0");

            if (stage == 2) return;

            if (!checkBoxGenerateLT.isSelected()) {
                saveLTProperties(index, 0, stage);
            }
        }
    }

    private void saveLTProperties(int index, int ltIndex, int stage) {
        if (!(checkTFForPositiveDouble(textFieldLTP1, "LT point 1") || checkTFForPositiveDouble(textFieldLTP2, "LT point 2") ||
                checkTFForPositiveDouble(textFieldLTP3, "LT point 3"))) {
            return;
        }

        //boolean isTrapezoidal = criterias[index].getLts().get(ltIndex).getType() != LTType.TRIANGULAR;
        boolean isTrapezoidal = comboBoxLTType.getSelectedItem().equals(LTType.TRAPEZOIDAL);
        if (isTrapezoidal && !checkTFForPositiveDouble(textFieldLTP4, "LT point 4")) {
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

    private void switchToSecondTab() {
        toggleSecondTabComponents(true);

        standardTableInitialization(tableLT);
        standardTableInitialization(tableLTFull);
        standardTableInitialization(tableAggregation);
        standardTableInitialization(tableAlpha);

        ((DefaultTableModel) tableConvolution.getModel()).setColumnIdentifiers(new String[]{"E", "I_op", "I_pes", "I_agg"});
        ((DefaultTableModel) tableConvolution.getModel()).setRowCount(alternativesCount);

        for (int i = 0; i < tableConvolution.getRowCount(); i++) {
            tableConvolution.setValueAt("E" + (i + 1), i, 0);
        }

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

    }

    private void doNextOnSecondTab() {
        if (textFieldAlpha.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Input alpha.");
            return;
        }

        pessimisticAndOptimistic();
    }

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

        try {
            // alpha
            double alpha = Double.parseDouble(textFieldAlpha.getText());

            for (int i = 0; i < tableAggregation.getRowCount(); i++) {
                for (int j = 1; j < tableAggregation.getColumnCount(); j++) {
                    tableAlpha.setValueAt(
                            Criteria.alphaCut(tableAggregation.getValueAt(i, j).toString(), alpha),
                            i, j);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Alpha is not a number!");
            e.printStackTrace();
            return;
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
        double pOpt = Integer.MIN_VALUE;
        double pPes = Integer.MIN_VALUE;

        for (int i = 0; i < convolutionOptimisticResults.length; i++) {
            if (pOpt < Math.max(1.0 - Math.max(1.0 - convolutionOptimisticResults[i][0], 0.0), 0.0)) {
                winOptimistic = i + 1;
            }

            if (pPes < Math.max(1.0 - Math.max(1.0 - convolutionOptimisticResults[i][0], 0.0), 0.0)) {
                winPessimistic = i + 1;
            }
        }

        long duration = System.currentTimeMillis() - startTime;

        labelOptimisticResult.setText("Optimistic winner: E" + winOptimistic + " (duration: " + duration + ")");
        labelPessimisticResult.setText("Pessimistic winner: E" + winPessimistic + " (duration: " + duration + ")");
    }

    private void aggregational(){
        long startTime = System.currentTimeMillis();
        // AGGREGATION
        for (int i = 0; i < tableLTFull.getRowCount(); i++) {
            for (int j = 1; j < tableLTFull.getColumnCount(); j++) {
                tableAggregation.setValueAt(
                        criterias[j - 1].aggregateLTs(tableLTFull.getValueAt(i, j).toString()),
                        i, j);
            }
        }

        try {
            // alpha
            double alpha = Double.parseDouble(textFieldAlpha.getText());

            for (int i = 0; i < tableAggregation.getRowCount(); i++) {
                for (int j = 1; j < tableAggregation.getColumnCount(); j++) {
                    tableAlpha.setValueAt(
                            Criteria.alphaCut(tableAggregation.getValueAt(i, j).toString(), alpha),
                            i, j);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Alpha is not a number!");
            e.printStackTrace();
            return;
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
        double pOpt = Integer.MIN_VALUE;
        double pPes = Integer.MIN_VALUE;

        for (int i = 0; i < convolutionOptimisticResults.length; i++) {
            if (pOpt < Math.max(1.0 - Math.max(1.0 - convolutionOptimisticResults[i][0], 0.0), 0.0)) {
                winOptimistic = i + 1;
            }

            if (pPes < Math.max(1.0 - Math.max(1.0 - convolutionOptimisticResults[i][0], 0.0), 0.0)) {
                winPessimistic = i + 1;
            }
        }

        long duration = System.currentTimeMillis() - startTime;

        labelAggregationResult.setText("Aggregation winner: E");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainGUI();
            }
        });
    }

    private void standardTableInitialization(JTable table) {
        String[] identifiers = new String[criteriaCount + 1];
        identifiers[0] = "E\\Q";

        for (int i = 1; i < identifiers.length; i++) {
            identifiers[i] = criterias[i - 1].toString();
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setColumnIdentifiers(identifiers);
        model.setRowCount(alternativesCount);

        for (int i = 0; i < alternativesCount; i++) {
            model.setValueAt("E" + (i + 1), i, 0);
        }
    }

    private void updateProgressBar() {
        int checkedCount = 0;

        for (int i = 0; i < criteriaChecked.length; i++) {
            if (criteriaChecked[i]) checkedCount++;
        }

        progressBarDone.setValue(checkedCount);
    }


    /* TOGGLES */

    private void toggleCriteriaComponents(boolean isEnable) {
        comboBoxCriteria.setEnabled(isEnable);
        textFieldCriteriaName.setEnabled(isEnable);
        textFieldCriteriaLTCount.setEnabled(isEnable);
        buttonLTAccept.setEnabled(isEnable);
    }

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
    }

    private void toggleDecisionMatrixComponents(boolean isEnable) {
        tableDecisionMatrixInitial.setEnabled(isEnable);
        buttonNextTab.setEnabled(isEnable);
    }

    private void toggleSecondTabComponents(boolean isEnable) {
        textFieldAlpha.setEnabled(isEnable);
        buttonDoAlpha.setEnabled(isEnable);
    }
}
