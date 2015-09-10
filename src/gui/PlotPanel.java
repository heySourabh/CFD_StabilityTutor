package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import org.math.plot.Plot2DPanel;
import solvers.GuiAndSolverIntegrator;

/**
 *
 * @author Sourabh Bhat
 */
public class PlotPanel extends Plot2DPanel {
    //Data array

    private double[] x;
    NumberFormat nf;
    int plotNumber = -1;

    public PlotPanel() {
        setBorder(BorderFactory.createTitledBorder("Plot Area"));
        removePlotToolBar();
        setAxisLabel(1, "U");
        setDoubleBuffered(true);
        nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(5);
        addInitializationPlot();
        addLegend(NORTH);
    }

    @Override
    public void paint(Graphics gr) {
        if (GuiAndSolverIntegrator.u == null) {
            return;
        }
        if (x == null || x.length != GuiAndSolverIntegrator.u.length) {
            x = new double[GuiAndSolverIntegrator.u.length];

            double dx = (GuiAndSolverIntegrator.maxX - GuiAndSolverIntegrator.minX)
                    / (GuiAndSolverIntegrator.numGridPoints - 1);
            for (int i = 0; i < x.length; i++) {
                x[i] = GuiAndSolverIntegrator.minX + i * dx;
            }
        }
        gr.setColor(Color.white);
        gr.fillRect(0, 0, getWidth(), getHeight());
        if (plotNumber != -1) {
            removePlot(plotNumber);
        }
        String legendText = "";
        if (GuiAndSolverIntegrator.currentlyRunningSolver != null) {
            legendText = "Time: " + nf.format(GuiAndSolverIntegrator.currentlyRunningSolver.getCurrentTime());
        }
        plotNumber = addLinePlot(legendText, x, GuiAndSolverIntegrator.u);
        // System.out.println("Plot Number: " + plotNumber);
        removePlotToolBar();
    }

    private void addInitializationPlot() {
        int numOfPointsInInitialPlot = 500;
        double[] uInit = new double[numOfPointsInInitialPlot];
        double[] xInit = new double[numOfPointsInInitialPlot];

        double dx = (GuiAndSolverIntegrator.maxX - GuiAndSolverIntegrator.minX)
                / (numOfPointsInInitialPlot - 1);
        for (int i = 0; i < xInit.length; i++) {
            xInit[i] = GuiAndSolverIntegrator.minX + i * dx;
        }
        GuiAndSolverIntegrator.initializeU(uInit);
        addLinePlot("Time: 0", xInit, uInit);
    }
}
