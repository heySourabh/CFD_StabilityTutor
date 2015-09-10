package solvers.implicit;

import javax.swing.JOptionPane;
import main.DifferencingType;
import solvers.TimeSteppingSolver;
import solvers.matrixsolver.ConjugateGradientSolver;
import solvers.matrixsolver.MatrixSolver;
import solvers.matrixsolver.SparseMatrix;

/**
 *
 * @author Sourabh Bhat
 */
public class DiffusionEquationImplicitSolver implements TimeSteppingSolver {

    final int N;
    public double[] un;
    double[] unp1;
    double alpha, CFL, dx;
    DifferencingType diff;
    double stopingTime;
    double dt;
    double time;
    SparseMatrix sparseMatrix;
    double[] b;

    public DiffusionEquationImplicitSolver(double[] initU, double alpha, double dx,
            DifferencingType diff, double CFL, double stopingTime) {
        un = initU;
        unp1 = new double[initU.length];
        N = un.length;
        this.alpha = alpha;
        this.dx = dx;
        this.diff = DifferencingType.valueOf(diff.name());
        this.CFL = CFL;
        dt = CFL * dx * dx / Math.abs(alpha) / 2.0;
        this.stopingTime = stopingTime;

        b = new double[N];
    }

    @Override
    public double[] solve() {
        while (stepSolution()) {
        }
        return un;
    }

    @Override
    public boolean stepSolution() {
        if (time > stopingTime) {
            return false;
        } else {
            calculateNextSolution();
            time += dt;
            return true;
        }
    }

    private void calculateNextSolution() {
        sparseMatrix = new SparseMatrix();
        switch (diff) {
            case FORWARD_DIFF:
                for (int i = 0; i < N - 2; i++) {
                    sparseMatrix.setMatrixElement(i, i, (1 - alpha * dt / dx / dx));
                    
                    sparseMatrix.setMatrixElement(i, i + 1, 2 * alpha * dt / dx / dx);
                    sparseMatrix.setMatrixElement(i, i + 2, (-alpha * dt / dx / dx));
                    b[i] = un[i];
                }
                // BC Extrapolated
                sparseMatrix.setMatrixElement(N - 2, N - 2, 1);
                sparseMatrix.setMatrixElement(N - 2, N - 3, -1);
                b[N - 2] = 0;
                sparseMatrix.setMatrixElement(N - 1, N - 1, 1);
                sparseMatrix.setMatrixElement(N - 1, N - 3, -1);
                b[N - 1] = 0;
                break;
            case BACKWARD_DIFF:
                for (int i = 2; i < N; i++) {
                    sparseMatrix.setMatrixElement(i, i, (1 - alpha * dt / dx / dx));
                    sparseMatrix.setMatrixElement(i, i - 1, 2 * alpha * dt / dx / dx);
                    sparseMatrix.setMatrixElement(i, i - 2, (-alpha * dt / dx / dx));
                    b[i] = un[i];
                }
                // BC Extrapolated
                sparseMatrix.setMatrixElement(0, 0, 1);
                sparseMatrix.setMatrixElement(0, 2, -1);
                b[0] = 0;
                sparseMatrix.setMatrixElement(1, 1, 1);
                sparseMatrix.setMatrixElement(1, 2, -1);
                b[1] = 0;
                break;
            case CENTRAL_DIFF:
                for (int i = 1; i < N - 1; i++) {
                    sparseMatrix.setMatrixElement(i, i, (1 + 2 * alpha * dt / dx / dx));
                    sparseMatrix.setMatrixElement(i, i - 1, (-alpha * dt / dx / dx));
                    sparseMatrix.setMatrixElement(i, i + 1, (-alpha * dt / dx / dx));
                    b[i] = un[i];
                }
                // BC Extrapolated
                sparseMatrix.setMatrixElement(0, 0, 1);
                sparseMatrix.setMatrixElement(0, 1, -1);
                b[0] = 0;
                sparseMatrix.setMatrixElement(N - 1, N - 1, 1);
                sparseMatrix.setMatrixElement(N - 1, N - 2, -1);
                b[N - 1] = 0;
                break;
            case UPWINDING:
                MsgDisplayThread msgThread = new MsgDisplayThread(
                        "Upwinding cannot be applied to diffusion equation, as it is Elliptic PDE.",
                        "Upwinding not possible");
                msgThread.t.start();

                time = stopingTime + dt; // Imposing that the solution should not proceed.
                break;
            default:
                throw new IllegalArgumentException("Differencing type not defined.");
        }
        MatrixSolver solver = new ConjugateGradientSolver(sparseMatrix, b, un, 1e-6);
        unp1 = solver.solve();
        System.arraycopy(unp1, 0, un, 0, N);
    }

    @Override
    public double getCurrentTime() {
        return time;
    }
}

class MsgDisplayThread implements Runnable {

    Thread t;
    String msg, title;

    public MsgDisplayThread(String msg, String title) {
        this.msg = msg;
        this.title = title;
        t = new Thread(this);
    }

    @Override
    public void run() {
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
    }
}