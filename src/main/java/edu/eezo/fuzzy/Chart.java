package edu.eezo.fuzzy;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;


/**
 * Class for diagram building.
 * This class requires jFreeChart and jCommon libraries.
 * <p>
 * Created by Eezo on 27.11.2016.
 */
public class Chart extends ApplicationFrame {
    private java.util.List<LinguisticTerm> linguisticTerms;

    public Chart(String applicationTitle, String chartTitle, java.util.List<LinguisticTerm> linguisticTerms, boolean isNormalize) {
        super(applicationTitle);
        this.linguisticTerms = new ArrayList<>();

        for (int i = 0; i < linguisticTerms.size(); i++) {
            this.linguisticTerms.add(linguisticTerms.get(i).makeClone());
        }

        JFreeChart xylineChart = ChartFactory.createXYLineChart(chartTitle, "Условные единицы", "Функция принадлежности", createDataset(isNormalize),
                PlotOrientation.VERTICAL, true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new Dimension(560, 367));
        XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setStroke(new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }

    /**
     * Reads data from <code>linguisticTerms</code> field.
     *
     * @return data set
     */
    private XYDataset createDataset(boolean isNormalize) {
        final XYSeriesCollection dataset = new XYSeriesCollection();

        if (!isNormalize) {
            linguisticTerms = LinguisticTerm.normalizeData(linguisticTerms);
        }
        for (int i = 0; i < linguisticTerms.size(); i++) {
            XYSeries series = new XYSeries(linguisticTerms.get(i).getShortName());


            double[] points = linguisticTerms.get(i).getPoints();
            if (points.length == 3) {
                series.add(points[0], 0.0);
                series.add(points[1], 1.0);
                series.add(points[2], 0.0);
            } else if (points.length == 4) {
                series.add(points[0], 0.0);
                series.add(points[1], 1.0);
                series.add(points[2], 1.0);
                series.add(points[3], 0.0);
            }

            dataset.addSeries(series);
        }


        return dataset;
    }

    // ! Need to override this to avoid main window closing.
    @Override
    public void windowClosing(WindowEvent event) {
        if (event.getWindow() == this) {
            this.dispose();
        }
    }

    public static void main(String criteriaMark, String criteriaName, java.util.List<LinguisticTerm> linguisticTerms) {
        Chart chart = new Chart(criteriaMark + " - original", criteriaName, linguisticTerms, true);
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setLocation(chart.getX() - 288, chart.getY());
        chart.setVisible(true);

        Chart chart2 = new Chart(criteriaMark + " - normalized", criteriaName, linguisticTerms, false);
        chart2.pack();
        RefineryUtilities.centerFrameOnScreen(chart2);
        chart2.setLocation(chart2.getX() + 288, chart2.getY());
        chart2.setVisible(true);
    }
}
