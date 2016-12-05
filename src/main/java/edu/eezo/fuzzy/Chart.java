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


/**
 * Class for diagram building.
 * Created by Eezo on 27.11.2016.
 */
public class Chart extends ApplicationFrame {
    java.util.List<LinguisticTerm> linguisticTerms;

    public Chart(String applicationTitle, String chartTitle, java.util.List<LinguisticTerm> linguisticTerms) {
        super(applicationTitle);
        this.linguisticTerms = linguisticTerms;
        JFreeChart xylineChart = ChartFactory.createXYLineChart(chartTitle, "Условные единицы", "Функция принадлежности", createDataset(),
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
    private XYDataset createDataset() {
        final XYSeriesCollection dataset = new XYSeriesCollection();

        for (int i = 0; i < linguisticTerms.size(); i++) {
            XYSeries series = new XYSeries(linguisticTerms.get(i).getShortName());
            double[] points = linguisticTerms.get(i).getPoints();

            points = normalizeData(points);

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

    private double[] normalizeData(double[] points) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        for (int i = 0; i < points.length; i++) {
            if (min > points[i]) min = points[i];
            if (max < points[i]) max = points[i];
        }

        double[] result = new double[points.length];

        for (int i = 0; i < result.length; i++) {
            if (Double.compare(points[i], 0.0d) == 0) {
                result[i] = 0.0d;
                continue;
            }

            result[i] = (points[i] - min) / (max - min);
        }

        return result;
    }

    // ! Need to override this to avoid main window closing.
    @Override
    public void windowClosing(WindowEvent event) {
        if (event.getWindow() == this) {
            this.dispose();
        }
    }

    public static void main(String criteriaMark, String criteriaName, java.util.List<LinguisticTerm> linguisticTerms) {
        Chart chart = new Chart(criteriaMark, criteriaName, linguisticTerms);
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }
}
