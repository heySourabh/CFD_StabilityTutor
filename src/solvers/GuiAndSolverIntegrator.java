package solvers;

import gui.ActionButtonsPanel;
import gui.InputPanel;
import gui.PlotPanel;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import main.DifferencingType;
import solvers.explicit.AdvectionEquationExplicitSolver;
import solvers.explicit.DiffusionEquationExplicitSolver;
import solvers.implicit.AdvectionEquationImplicitSolver;
import solvers.implicit.DiffusionEquationImplicitSolver;

/**
 *
 * @author Sourabh Bhat
 */
public class GuiAndSolverIntegrator {

    InputPanel inputPanel;
    ActionButtonsPanel actionButtonsPanel;
    PlotPanel plotPanel;
    // Input parameters
    public static String pde = "Advection Equation";
    public static double pdeConstant = 1.0;
    public static double courantNumber = 0.5;
    public static int numGridPoints = 100;
    public static double minX = -1.0;
    public static double maxX = 1.0;
    public static double stopTime = 5.0;
    public static DifferencingType spaceDiff = DifferencingType.BACKWARD_DIFF;
    public static String timeDiff = "Explicit Method";
    public static double[] u;
    public static TimeSteppingSolver currentlyRunningSolver;
    public static boolean solverPlaying = false;
    public static final long MIN_DELAY = 10;
    public static final long MAX_DELAY = 1000;
    public static long delayInMillis = (MIN_DELAY + MAX_DELAY) / 2;
    SteppingSolverThread solverThread;

    public GuiAndSolverIntegrator(InputPanel inputPanel, ActionButtonsPanel actionButtonsPanel, PlotPanel plotPanel) {
        this.inputPanel = inputPanel;
        this.actionButtonsPanel = actionButtonsPanel;
        this.plotPanel = plotPanel;
    }

    public void integrate() {
        actionButtonsPanel.finalSolutionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finalSolution();
            }
        });

        actionButtonsPanel.playOrPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playOrPause();
            }
        });

        actionButtonsPanel.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });

        actionButtonsPanel.speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeSpeed(actionButtonsPanel.speedSlider.getValue());
            }
        });
    }

    private void playOrPause() {
        if (u == null || currentlyRunningSolver == null) {
            reset();
        }
        if (solverPlaying) {
            solverPlaying = false;
            System.out.println("Solver Paused");
            actionButtonsPanel.playOrPauseButton.setText("Play");
            actionButtonsPanel.playOrPauseButton.setIcon(ActionButtonsPanel.playIcon);
        } else {
            solverPlaying = true;
            System.out.println("Solver Played");
            if (solverThread != null && solverThread.t.getState() == Thread.State.NEW) {
                solverThread.t.start();
            }
            actionButtonsPanel.playOrPauseButton.setText("Pause");
            actionButtonsPanel.playOrPauseButton.setIcon(ActionButtonsPanel.pauseIcon);
        }
    }

    private void reset() {
        System.out.println("Reset");
        readAllParametersFromGui();
        // Stop previously running solver
        if (currentlyRunningSolver != null) {
            solverPlaying = false;
            if (solverThread != null) {
                solverThread.stopThread();
                solverThread.t.interrupt();
                try {
                    solverThread.t.join();
                    solverThread = null;
                } catch (InterruptedException ex) {
                }
            }
        }
        // define new u and Initialize u
        u = new double[numGridPoints];
        initializeU(u);

        // Repaint
        plotPanel.repaint();

        if (timeDiff.equals("Explicit Method")) {
            switch (pde) {
                case "Advection Equation":
                    currentlyRunningSolver = new AdvectionEquationExplicitSolver(
                            u, pdeConstant, (maxX - minX) / (numGridPoints - 1),
                            spaceDiff, courantNumber, stopTime);
                    break;
                case "Diffusion Equation":
                    currentlyRunningSolver = new DiffusionEquationExplicitSolver(
                            u, pdeConstant, (maxX - minX) / (numGridPoints - 1),
                            spaceDiff, courantNumber, stopTime);
                    break;
                default:
                    throw new UnsupportedOperationException("Other equations are not supported yet.");
            }
        } else {
            switch (pde) {
                case "Advection Equation":
                    currentlyRunningSolver = new AdvectionEquationImplicitSolver(
                            u, pdeConstant, (maxX - minX) / (numGridPoints - 1),
                            spaceDiff, courantNumber, stopTime);
                    break;
                case "Diffusion Equation":
                    currentlyRunningSolver = new DiffusionEquationImplicitSolver(
                            u, pdeConstant, (maxX - minX) / (numGridPoints - 1),
                            spaceDiff, courantNumber, stopTime);
                    break;
                default:
                    throw new UnsupportedOperationException("Other equations are not supported yet.");
            }
        }

        solverThread = new SteppingSolverThread(currentlyRunningSolver, plotPanel);
        actionButtonsPanel.playOrPauseButton.setText("Play");
        actionButtonsPanel.playOrPauseButton.setIcon(ActionButtonsPanel.playIcon);
    }

    private void finalSolution() {
        System.out.println("Final Solution");
        reset();
        solverThread = null;
        FinalSolverThread finalSolverThread = new FinalSolverThread(currentlyRunningSolver);
        finalSolverThread.t.start();
        inputPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        plotPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        actionButtonsPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            finalSolverThread.t.join(); // Wait for solution to complete
        } catch (InterruptedException ex) {
            // Ignore
        }
        inputPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        plotPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        actionButtonsPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        plotPanel.repaint();
        System.out.println("Plotting final solution.");
    }

    private void changeSpeed(int speed) {
        System.out.println("Speed Changed to " + speed);
        int minSliderValue = actionButtonsPanel.speedSlider.getMinimum();
        int maxSliderValue = actionButtonsPanel.speedSlider.getMaximum();
        delayInMillis = (long) (MAX_DELAY + (double) (speed - minSliderValue)
                / (double) (maxSliderValue - minSliderValue)
                * (double) (MIN_DELAY - MAX_DELAY));
    }

    private void readAllParametersFromGui() {
        try {
            // Read Variables
            pde = (String) inputPanel.pdeComboBox.getSelectedItem();
            pdeConstant = Double.parseDouble(inputPanel.pdeConstantTextField.getText());
            courantNumber = Double.parseDouble(inputPanel.cflNumberTextField.getText());
            numGridPoints = Integer.parseInt(inputPanel.numGridPointsTextField.getText());
            minX = Double.parseDouble(inputPanel.minXTextField.getText());
            maxX = Double.parseDouble(inputPanel.maxXTextField.getText());
            stopTime = Double.parseDouble(inputPanel.stopTimeTextField.getText());
            spaceDiff = DifferencingType.getDifferencingType((String) inputPanel.spaceDiffTypeComboBox.getSelectedItem());
            timeDiff = (String) inputPanel.explicitImplicitComboBox.getSelectedItem();
        } catch (Exception ex) {
            resetAllParameters(inputPanel);
        }
    }

    public static void resetAllParameters(InputPanel inputPanel) {
        if (inputPanel != null) {
            inputPanel.pdeComboBox.setSelectedItem(pde);
            inputPanel.pdeConstantTextField.setText("" + pdeConstant);
            inputPanel.cflNumberTextField.setText("" + courantNumber);
            inputPanel.numGridPointsTextField.setText("" + numGridPoints);
            inputPanel.minXTextField.setText("" + minX);
            inputPanel.maxXTextField.setText("" + maxX);
            inputPanel.stopTimeTextField.setText("" + stopTime);
            inputPanel.spaceDiffTypeComboBox.setSelectedItem(spaceDiff.getName());
            inputPanel.explicitImplicitComboBox.setSelectedItem(timeDiff);
        }
    }

    public static void initializeU(double[] u) {
        for (int i = 0; i < u.length; i++) {
            double ratio = (double) i / (u.length - 1);
            double x = minX + ratio * (maxX - minX);
            if (ratio >= 0.1 && ratio <= 0.2) {
                u[i] = Math.exp(-1111.111111 * Math.log(2.0) * Math.pow(x + 0.7, 2.0));
            } else if (ratio >= 0.3 && ratio <= 0.4) {
                u[i] = 1.0;
            } else if (ratio >= 0.5 && ratio <= 0.6) {
                u[i] = 1.0 - 10 * Math.abs(x - 0.1);
            } else if (ratio >= 0.7 && ratio <= 0.8) {
                u[i] = 1.0 - 100 * Math.pow(x - 0.5, 2.0);
            } else {
                u[i] = 0.0;
            }
        }
    }
}

class SteppingSolverThread implements Runnable {

    Thread t;
    TimeSteppingSolver solver;
    PlotPanel plotPanel;
    private boolean stopThread = false;

    public SteppingSolverThread(TimeSteppingSolver solver, PlotPanel plotPanel) {
        this.solver = solver;
        this.plotPanel = plotPanel;
        t = new Thread(this);
    }

    @Override
    public void run() {
        while (!stopThread) {
            while (!GuiAndSolverIntegrator.solverPlaying && !stopThread) {
                delay(100);
            }
            delay(GuiAndSolverIntegrator.delayInMillis);
            boolean solutionAvailable = solver.stepSolution();
            plotPanel.repaint();
            System.out.println("Plotting next solution");
            if (!solutionAvailable) {
                System.out.println("Reached final solution.");
                break;
            }
        }
    }

    private void delay(long timeInMillis) {
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException ex) {
        }
    }

    public void stopThread() {
        stopThread = true;
    }
}

class FinalSolverThread implements Runnable {

    TimeSteppingSolver solver;
    Thread t;

    public FinalSolverThread(TimeSteppingSolver solver) {
        this.solver = solver;
        t = new Thread(this);
    }

    @Override
    public void run() {
        solver.solve();
    }
}