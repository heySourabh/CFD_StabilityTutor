package solvers.explicit;

import main.DifferencingType;
import solvers.TimeSteppingSolver;

/**
 *
 * @author Sourabh Bhat
 */
public class AdvectionEquationExplicitSolver implements TimeSteppingSolver {

    final int N;
    public double[] un;
    double[] unp1;
    double a, CFL, dx;
    DifferencingType diff;
    double stopingTime;
    double dt;
    double time;

    public AdvectionEquationExplicitSolver(double[] initU, double a, double dx,
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
        if(diff == DifferencingType.UPWINDING)
            if(a > 0) diff = DifferencingType.BACKWARD_DIFF;
            else diff = DifferencingType.FORWARD_DIFF;
        switch (diff) {
            case BACKWARD_DIFF:
                for (int i = 1; i < N; i++) {
                    unp1[i] = un[i] - dt * a / dx * (un[i] - un[i - 1]);
                }
                // Periodic BC
                unp1[0] = unp1[N - 1];
                break;
            case FORWARD_DIFF:
                for (int i = 0; i < N - 1; i++) {
                    unp1[i] = un[i] - dt * a / dx * (un[i + 1] - un[i]);
                }
                // Periodic BC
                unp1[N - 1] = unp1[0];
                break;
            case CENTRAL_DIFF:
                for (int i = 1; i < N - 1; i++) {
                    unp1[i] = un[i] - dt * a / dx / 2.0 * (un[i + 1] - un[i - 1]);
                }
                // Extrapolated BC
                unp1[0] = unp1[1];
                unp1[N - 1] = unp1[N - 2];
                break;
            default:
                throw new IllegalArgumentException("Differencing type not defined.");
        }

        // Copy solution to un
        System.arraycopy(unp1, 0, un, 0, N);
    }

    @Override
    public double getCurrentTime() {
        return time;
    }
}
