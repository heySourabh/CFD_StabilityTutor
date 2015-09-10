package solvers.explicit;

import javax.swing.JOptionPane;
import main.DifferencingType;
import solvers.TimeSteppingSolver;


// This thread is required as the main thread is being blocked by the message
/**
 *
 * @author Sourabh Bhat
 */
public class DiffusionEquationExplicitSolver implements TimeSteppingSolver {

    final int N;
    public double[] un;
    double[] unp1;
    double alpha, CFL, dx;
    DifferencingType diff;
    double stopingTime;
    double dt;
    double time;

    public DiffusionEquationExplicitSolver(double[] initU, double alpha, double dx,
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
        switch (diff) {
            case BACKWARD_DIFF:
                for (int i = 2; i < N; i++) {
                    unp1[i] = un[i] + dt * alpha / dx / dx * (un[i - 2] - 2 * un[i - 1] + un[i]);
                }
                // Extrapolated BC
                unp1[0] = unp1[2];
                unp1[1] = unp1[2];
                break;
            case FORWARD_DIFF:
                for (int i = 0; i < N - 2; i++) {
                    unp1[i] = un[i] + dt * alpha / dx / dx * (un[i] - 2 * un[i + 1] + un[i + 2]);
                }
                // Extrapolated BC
                unp1[N - 1] = unp1[N - 3];
                unp1[N - 2] = unp1[N - 3];
                break;
            case CENTRAL_DIFF:
                for (int i = 1; i < N - 1; i++) {
                    unp1[i] = un[i] + dt * alpha / dx / dx * (un[i - 1] - 2 * un[i] + un[i + 1]);
                }
                // Extrapolated BC
                unp1[0] = unp1[1];
                unp1[N - 1] = unp1[N - 2];
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

        // Copy solution to un
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