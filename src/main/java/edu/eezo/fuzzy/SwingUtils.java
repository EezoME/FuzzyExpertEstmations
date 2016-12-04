package edu.eezo.fuzzy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Created by Eezo on 04.12.2016.
 */
public class SwingUtils {

    public static void jTableInitiation(JTable table, String[] columnIdentifiers, int rowCount) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setColumnIdentifiers(columnIdentifiers);
        model.setRowCount(rowCount);

        for (int i = 0; i < table.getRowCount(); i++) {
            table.setValueAt("E" + (i + 1), i, 0);
        }
    }

    public static boolean checkTableForNonEmpty(JTable table){
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                if (table.getValueAt(i, j) == null || table.getValueAt(i, j).toString().isEmpty()){
                    return false;
                }
            }
        }

        return true;
    }


    public static boolean checkTF(JTextField textField, String tfDesc) {
        if (!textField.isValid()) {
            JOptionPane.showMessageDialog(null, "Text field '" + tfDesc + "' is not valid.");
            return false;
        }

        if (!textField.isEnabled()) {
            JOptionPane.showMessageDialog(null, "Text field '" + tfDesc + "' is not enabled.");
            return false;
        }

        if (textField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Text field '" + tfDesc + "' is empty.");
            return false;
        }

        return true;
    }

    public static boolean checkTFForPositiveInteger(JTextField textField, String fieldDesc) {
        if (!checkTF(textField, fieldDesc)) return false;

        try {
            if (Integer.parseInt(textField.getText()) <= 0) {
                JOptionPane.showMessageDialog(null, "Value in field '" + fieldDesc + "' must be > 0");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "You typed non integer value in filed '" + fieldDesc + "'");
            return false;
        }

        return true;
    }

    public static boolean checkTFForDouble(JTextField textField, String fieldDesc) {
        if (!checkTF(textField, fieldDesc)) return false;

        try {
            Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "You typed non double value in filed '" + fieldDesc + "'");
            return false;
        }

        return true;
    }
}
