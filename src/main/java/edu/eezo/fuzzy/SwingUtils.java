package edu.eezo.fuzzy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Custom swing most frequently used utils.
 * Created by Eezo on 04.12.2016.
 */
public class SwingUtils {

    /**
     * Sets column identifiers and row count for specified table.
     * Also sets first column values to E(i).
     *
     * @param table             specified table
     * @param columnIdentifiers an array of column identifiers (usually, strings)
     * @param rowCount          a number of rows
     */
    public static void jTableInitiation(JTable table, Object[] columnIdentifiers, int rowCount) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setColumnIdentifiers(columnIdentifiers);
        model.setRowCount(rowCount);

        for (int i = 0; i < table.getRowCount(); i++) {
            table.setValueAt("E" + (i + 1), i, 0);
        }
    }

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


    /**
     * Checks specified text field for validity, availability and non empty.
     * Show error message if it found some problem.
     *
     * @param textField specified text field
     * @param tfDesc    text field description (for error messages)
     * @return <b>true</b> if it's all OK, <b>false</b> - otherwise
     */
    public static boolean checkTF(JTextField textField, String tfDesc) {
        if (!textField.isValid()) {
            JOptionPane.showMessageDialog(null, "Текстовое поле '" + tfDesc + "' невалидно.");
            return false;
        }

        if (!textField.isEnabled()) {
            JOptionPane.showMessageDialog(null, "Текстовое поле '" + tfDesc + "' недоступно.");
            return false;
        }

        if (textField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Текстовое поле '" + tfDesc + "' пустое.");
            return false;
        }

        return true;
    }

    /**
     * Checks specified text field for positive integer value ( > 0 ).
     * Also make checks for validity, availability and non empty.
     * Show error message if it found some problem.
     *
     * @param textField specified text field
     * @param fieldDesc text field description (for error messages)
     * @return <b>true</b> if it's all OK, <b>false</b> - otherwise
     */
    public static boolean checkTFForPositiveInteger(JTextField textField, String fieldDesc) {
        if (!checkTF(textField, fieldDesc)) return false;

        try {
            if (Integer.parseInt(textField.getText()) <= 0) {
                JOptionPane.showMessageDialog(null, "Значение в поле '" + fieldDesc + "' должно быть больше 0");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Вы введи нецелочисленное значение в поле '" + fieldDesc + "'");
            return false;
        }

        return true;
    }

    /**
     * Checks specified text field for double value.
     * Also make checks for validity, availability and non empty.
     * Show error message if it found some problem.
     *
     * @param textField specified text field
     * @param fieldDesc text field description (for error messages)
     * @return <b>true</b> if it's all OK, <b>false</b> - otherwise
     */
    public static boolean checkTFForDouble(JTextField textField, String fieldDesc) {
        if (!checkTF(textField, fieldDesc)) return false;

        try {
            Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Вы ввели нечисловое знаение в поле '" + fieldDesc + "'");
            return false;
        }

        return true;
    }
}
