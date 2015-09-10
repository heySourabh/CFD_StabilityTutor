package solvers.implicit;

import main.DifferencingType;
import solvers.TimeSteppingSolver;
import solvers.matrixsolver.ConjugateGradientSolver;
import solvers.matrixsolver.MatrixSolver;
import solvers.matrixsolver.SparseMatrix;

/**
 *
 * @author Sourabh Bhat
 */
public class AdvectionEquationImplicitSolver implements TimeSteppingSolver {

    final int N;
    public double[] un;
    double[] unp1;
    double a, CFL, dx;
    DifferencingType diff;
    double stopingTime;
    double dt;
    double time;
    SparseMatrix A;
    double[] b;

    public AdvectionEquationImplicitSolver(double[] initU, double a, double dx,
            DifferencingType diff, double CFL, double stopingTime) {
        un = initU;
        unp1 = new double[initU.length];
        N = un.length;
        this.a = a;
        this.dx = dx;
        this.diff = DifferencingType.valueOf(diff.name());
        this.CFL = CFL;
        dt = CFL * dx / Math.abs(a);
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
        A = new SparseMatrix();
        if (diff == DifferencingType.UPWINDING) {
            if (a > 0) {
                diff = DifferencingType.BACKWARD_DIFF;
            } else {
                diff = DifferencingType.FORWARD_DIFF;
            }
        }

        switch (diff) {
            case FORWARD_DIFF:
                for (int i = 0; i < N - 1; i++) {
                    A.setMatrixElement(i, i, (1 - a * dt / dx));
                    A.setMatrixElement(i, i + 1, a * dt / dx);
                    b[i] = un[i];
                }
                // BC periodic
                A.setMatrixElement(N - 1, N - 1, 1);
                b[N - 1] = un[0];
                break;
            case BACKWARD_DIFF:
                for (int i = 1; i < N; i++) {
                    A.setMatrixElement(i, i, (1 + a * dt / dx));
                    A.setMatrixElement(i, i - 1, (-a * dt / dx));
                    b[i] = un[i];
                }
                // BC periodic
                A.setMatrixElement(0, 0, 1);
                b[0] = un[N - 1];
                break;
            case CENTRAL_DIFF:
                for (int i = 1; i < N - 1; i++) {
                    A.setMatrixElement(i, i, 1);
                    A.setMatrixElement(i, i + 1, a * dt / dx);
                    A.setMatrixElement(i, i - 1, (-a * dt / dx));
                    b[i] = un[i];
                }
                // BC Extrapolated
                A.setMatrixElement(0, 0, 1);
                A.setMatrixElement(0, 1, -1);
                b[0] = 0;
                A.setMatrixElement(N - 1, N - 1, 1);
                A.setMatrixElement(N - 1, N - 2, -1);
                b[N - 1] = 0;
                break;
            default:
                throw new IllegalArgumentException("Differencing type not defined.");
        }
        MatrixSolver solver = new ConjugateGradientSolver(A, b, un, 1e-6);
        unp1 = solver.solve();
        System.arraycopy(unp1, 0, un, 0, N);
    }

    @Override
    public double getCurrentTime() {
        return time;
    }
}
