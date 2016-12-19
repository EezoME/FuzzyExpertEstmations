package edu.eezo.fuzzy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
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
    private JButton buttonEnableDataInput;
    private JTextArea textAreaResult;
    private JLabel labelExample;
    private JTable tableResult;

    private int alternativesCount;
    private int criteriaCount;
    private Criteria[] criterias;
    private Criteria[] criteriasNormalized;
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
        super("Многокритерийный метод принятия решения на основе нечётких экспертных оценок");
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
                if (checkTFForPositiveInteger(textFieldAlternativesCount, "Число альтернатив") ||
                        checkTFForPositiveInteger(textFieldCriteriaCount, "Число критериев")) {
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
        });
        buttonLTAccept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stage < 1) return;

                saveCriteria(comboBoxCriteria.getSelectedIndex(), stage);

                toggleLTComponents(true);

                stage = 2;
            }
        });
        buttonSaveLT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stage < 2) return;

                saveLTProperties(comboBoxCriteria.getSelectedIndex(), Integer.parseInt(labelLTNo.getText()), stage);

                stage = 3;
            }
        });
        buttonSaveCriteria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stage < 2) return;

                saveCriteria(comboBoxCriteria.getSelectedIndex(), stage);
                criteriaChecked[comboBoxCriteria.getSelectedIndex()] = true;
                updateProgressBar();

                if (progressBarDone.getValue() == progressBarDone.getMaximum()) {
                    toggleDecisionMatrixComponents(true);
                    toggleLTComponents(false);
                    toggleCriteriaComponents(false);

                    standartTableInitialization(tableDecisionMatrixInitial);
                }

                int nextIndex = comboBoxCriteria.getSelectedIndex() + 1;
                if (nextIndex >= criterias.length) nextIndex = 0;
                comboBoxCriteria.setSelectedIndex(nextIndex);
                showCriteria(nextIndex, stage);

                stage = 4;
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
                if (stage >= 3 && criterias[comboBoxCriteria.getSelectedIndex()].getLts() != null)
                    Chart.main(criterias[comboBoxCriteria.getSelectedIndex()].toString(),
                            criterias[comboBoxCriteria.getSelectedIndex()].getName(),
                            criterias[comboBoxCriteria.getSelectedIndex()].getLts());
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

        tableDecisionMatrixInitial.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (tableDecisionMatrixInitial.getSelectedColumn() - 1 >= 0 && tableDecisionMatrixInitial.getSelectedColumn() - 1 < criterias.length) {
                    setExample(tableDecisionMatrixInitial.getSelectedColumn() - 1);
                }
            }
        });
        tableDecisionMatrixInitial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tableDecisionMatrixInitial.getSelectedColumn() - 1 >= 0 && tableDecisionMatrixInitial.getSelectedColumn() - 1 < criterias.length) {
                    setExample(tableDecisionMatrixInitial.getSelectedColumn() - 1);
                }
            }
        });
    }

    /* CRITERIA METHODS */

    private void showCriteria(int index, int stage) {
        if (stage < 2 || index < 0) return;

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

            textFieldLTP1.setText(round(criterias[index].getLts().get(ltIndex).getPoints()[0], 3) + "");
            textFieldLTP2.setText(round(criterias[index].getLts().get(ltIndex).getPoints()[1], 3) + "");
            textFieldLTP3.setText(round(criterias[index].getLts().get(ltIndex).getPoints()[2], 3) + "");

            if (comboBoxLTType.getSelectedItem().equals(LTType.TRAPEZOIDAL)) {
                textFieldLTP4.setText(round(criterias[index].getLts().get(ltIndex).getPoints()[3], 3) + "");
            }
        }
    }

    private void saveCriteria(int index, int stage) {
        if (stage != 1 && stage < 2) return;

        criterias[index].setName(textFieldCriteriaName.getText());

        if (checkTFForPositiveInteger(textFieldCriteriaLTCount, "Число термов")) {
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

    private void saveLTProperties(int index, int ltIndex, int stage) {
        if (stage < 2) return;

        if (checkBoxGenerateLT.isSelected()) {
            int ltCount = Integer.parseInt(textFieldCriteriaLTCount.getText());
            criterias[index].setLts(LinguisticTerm.generateTermList(ltCount));
            return;
        }

        if (!(checkTFForPositiveDouble(textFieldLTP1, "Первая точка терма") || checkTFForPositiveDouble(textFieldLTP2, "Вторая точка терма") ||
                checkTFForPositiveDouble(textFieldLTP3, "Третья точка терма"))) {
            return;
        }

        boolean isTrapezoidal = comboBoxLTType.getSelectedItem().equals(LTType.TRAPEZOIDAL);
        if (isTrapezoidal && !checkTFForPositiveDouble(textFieldLTP4, "Червётрая точка терма")) {
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
            JOptionPane.showMessageDialog(null, "Ошибка в значении точек терма");
            return;
        }

        criterias[index].updateLT(ltIndex, textFieldLTLongName.getText(), textFieldLTShortName.getText(),
                (LTType) comboBoxLTType.getSelectedItem(), points);
    }

    /* SECOND TAB METHODS */

    private void switchToSecondTab() {
        if (stage < 2) return;

        if (!checkTableForNonEmpty(tableDecisionMatrixInitial)) {
            JOptionPane.showMessageDialog(null, "Заполните матрицу решений.");
            return;
        }

        criteriasNormalized = new Criteria[criterias.length];

        for (int i = 0; i < criterias.length; i++) {
            criteriasNormalized[i] = criterias[i].makeClone();
            LinguisticTerm.normalizeData(criteriasNormalized[i].getLts());
        }

        toggleSecondTabComponents(true);

        standartTableInitialization(tableLT);
        standartTableInitialization(tableLTFull);
        standartTableInitialization(tableAggregation);
        standartTableInitialization(tableAlpha);
        generateEmptyResultTable();

        ((DefaultTableModel) tableConvolution.getModel()).setColumnIdentifiers(new String[]{"E", "I опт", "I песс"});
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
                        criteriasNormalized[j - 1].getLTWithIntermediateValues(tableLT.getValueAt(i, j).toString()),
                        i, j);
            }
        }

        // aggregation
        for (int i = 0; i < tableLTFull.getRowCount(); i++) {
            for (int j = 1; j < tableLTFull.getColumnCount(); j++) {
                tableAggregation.setValueAt(
                        criteriasNormalized[j - 1].aggregateLTs(tableLTFull.getValueAt(i, j).toString()),
                        i, j);
            }
        }

        stage = 5;
    }

    private void doNextOnSecondTab() {
        if (stage < 2) return;

        if (textFieldAlpha.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Введите значение альфа.");
            return;
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
                        "[ " + round(convolutionOptimisticResults[i][0], 3) + " ; " + round(convolutionOptimisticResults[i][1], 3) + " ]",
                        i, 1);
                convolutionPessimisticResults[i] = Criteria.pessimisticConvolution(aggrExps);
                tableConvolution.setValueAt(
                        "[ " + round(convolutionPessimisticResults[i][0], 3) + " ; " + round(convolutionPessimisticResults[i][1], 3) + " ]",
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

            textAreaResult.append("По оптимистической: ");

            for (int j = 1; j < tableResult.getColumnCount(); j++) {
                tableResult.setValueAt(round(optRes[j - 1], 3), 0, j);
                tableResult.setValueAt(round(pesRes[j - 1], 3), 1, j);
                textAreaResult.append("E" + optimisticIndexes[j - 1] + " > ");
            }

            textAreaResult.replaceRange("", textAreaResult.getText().length() - 3, textAreaResult.getText().length());
            textAreaResult.append("\nПо пессимистической: ");

            for (int i = 0; i < pessimisticIndexes.length; i++) {
                textAreaResult.append("E" + pessimisticIndexes[i] + " > ");
            }

            textAreaResult.replaceRange("", textAreaResult.getText().length() - 3, textAreaResult.getText().length());
            textAreaResult.append("\n");


            labelOptimisticResult.setText("Победитель по оптиместической: E" + winOptimistic);
            labelPessimisticResult.setText("Победитель по пессимистической: E" + winPessimistic);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Альфа должно быть числом.");
            return;
        }

        stage = 6;
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainGUI();
            }
        });
    }

    private void setExample(int selectedIndex) {
        String example = criterias[selectedIndex].getMark() + " є {";

        for (int i = 0; i < criterias[selectedIndex].getLts().size(); i++) {
            example += criterias[selectedIndex].getLts().get(i).getShortName() + ",";
        }

        example = example.substring(0, example.lastIndexOf(',')) + "}";

        labelExample.setText(example);
    }

    private void standartTableInitialization(JTable table) {
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

    /**
     * Generates a result table with initial data.
     */
    private void generateEmptyResultTable() {
        DefaultTableModel model = (DefaultTableModel) tableResult.getModel();
        String[] identifiers = new String[alternativesCount + 1];
        identifiers[0] = "Метод";
        for (int i = 1; i < identifiers.length; i++) {
            identifiers[i] = "E" + i;
        }
        model.setColumnIdentifiers(identifiers);
        model.setRowCount(3);
        model.setValueAt("По оптимистической", 0, 0);
        model.setValueAt("По пессимистической", 1, 0);
    }

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

    /* TOGGLES */

    private void toggleCriteriaComponents(boolean isEnable) {
        comboBoxCriteria.setEnabled(isEnable);
        textFieldCriteriaName.setEnabled(isEnable);
        textFieldCriteriaLTCount.setEnabled(isEnable);
        checkBoxGenerateLT.setEnabled(isEnable);
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
        buttonSaveCriteria.setEnabled(isEnable);
    }

    private void toggleDecisionMatrixComponents(boolean isEnable) {
        tableDecisionMatrixInitial.setEnabled(isEnable);
        buttonNextTab.setEnabled(isEnable);
        buttonEnableDataInput.setEnabled(isEnable);
    }

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
        tableAlpha.setEnabled(isEnable);
        tableConvolution.setEnabled(isEnable);
    }

    /* COMPONENTS CHECKS */

    /**
     * Checks specified table for empty cells.
     *
     * @param table specified table
     * @return <b>true</b> if table has no empty cells, <b>false</b> - otherwise
     */
    public static boolean checkTableForNonEmpty(JTable table) {
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                if (table.getValueAt(i, j) == null || table.getValueAt(i, j).toString().isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkTFForPositiveInteger(JTextField textField, String fieldDesc) {
        if (textField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Заполните поле: '" + fieldDesc + "'");
            return false;
        }
        try {
            if (Integer.parseInt(textField.getText()) <= 0) {
                JOptionPane.showMessageDialog(null, "Значение в поле '" + fieldDesc + "' должно быть > 0");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Вы ввели не целочисленное значение в поле: '" + fieldDesc + "'");
            return false;
        }
        return true;
    }

    private boolean checkTFForPositiveDouble(JTextField textField, String fieldDesc) {
        if (textField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Заполните поле: '" + fieldDesc + "'");
            return false;
        }
        try {
            Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Вы ввели не числовое значение в поле: '" + fieldDesc + "'");
            return false;
        }
        return true;
    }

    private double round(double number, int dec) {
        number *= Math.pow(10, dec);
        number = Math.round(number);
        return number / Math.pow(10, dec);
    }
}
