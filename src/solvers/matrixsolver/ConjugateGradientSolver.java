package solvers.matrixsolver;

import java.util.ArrayList;

/**
 *
 * @author Sourabh Bhat
 */
public class ConjugateGradientSolver implements MatrixSolver {

    private final int MAX_ITER;
    private ArrayList<MatrixElement> sparseMatrixElements;
    private double[] b;
    private int numEqns;
    private int numVars;
    /**
     * Vector storing Ax-b
     */
    private double[] residue;
    /**
     * Direction of search
     */
    private double[] d_k;
    private double[] d_km1; // direction at previous iteration (k-1)
    /**
     * Flag to decide whether steepest descent is used or conjugate gradient is used.
     * It is set to true after 2 iterations of steepest descent in the code.
     */
    private boolean conjugateGradients = false;
    private double[] grad_km1; // gradient at previous iteration (k-1)
    private double[] grad_k;
    private double[] currentSolution;
    private double alpha;
    private double errorTolerance;

    public ConjugateGradientSolver(SparseMatrix sparseMatrix, double[] b, double[] initialSolution, double errorTolerance) {
        this.sparseMatrixElements = sparseMatrix.getMatrix();
        this.b = b;
        numEqns = b.length;
        numVars = initialSolution.length;
        this.errorTolerance = errorTolerance;

        MAX_ITER = numEqns * 10;
        residue = new double[numEqns];
        d_k = new double[numVars];
        grad_k = new double[numVars];
        currentSolution = new double[numVars];
        System.arraycopy(initialSolution, 0, currentSolution, 0, numVars);
    }

    @Override
    public double[] solve() {
        for (int iter = 0; iter <= MAX_ITER; iter++) {
            populateResidue();
            populateGradient();
            if (iter == 2) {
                conjugateGradients = true;
            }
            populateDirection();
            calculateAlpha();
            calculateNextSolution();
            if (calculateFunc() < errorTolerance) {
                break;
            }
            if(iter == MAX_ITER){
                System.out.println("Unable to converge to required accuracy.");
                throw new ArithmeticException("Unable to converge to required accuracy.");
            }
        }
        return currentSolution;
    }

    private void populateResidue() {
        for (int i = 0; i < numEqns; i++) {
            residue[i] = 0.0;
        }
        for (int i = 0; i < sparseMatrixElements.size(); i++) {
            MatrixElement e = sparseMatrixElements.get(i);
            residue[e.row] += e.value * currentSolution[e.column];
        }
        for (int i = 0; i < numEqns; i++) {
            residue[i] -= b[i];
        }
    }

    private void populateGradient() {
        grad_km1 = grad_k;
        grad_k = new double[numVars];
        for (int i = 0; i < sparseMatrixElements.size(); i++) {
            MatrixElement e = sparseMatrixElements.get(i);
            grad_k[e.column] += e.value * residue[e.row];
        }

        for (int i = 0; i < numVars; i++) {
            grad_k[i] *= 2.0;
        }
    }

    private void populateDirection() {
        d_km1 = d_k;
        d_k = new double[numVars];
        if (!conjugateGradients) {
            for (int i = 0; i < numVars; i++) {
                d_k[i] = (-grad_k[i]);
            }
        } else {
            double beta = calculateBeta(grad_k, grad_km1);
            for (int i = 0; i < numVars; i++) {
                d_k[i] = (-grad_k[i] + beta * d_km1[i]);
            }
        }
    }

    private void calculateAlpha() {
        double[] vect = new double[numEqns];
        for (int i = 0; i < sparseMatrixElements.size(); i++) {
            MatrixElement e = sparseMatrixElements.get(i);
            vect[e.row] += e.value * d_k[e.column];
        }
        double numer = 0.0;
        for (int i = 0; i < numEqns; i++) {
            numer += residue[i] * vect[i];
        }
        double denom = 0.0;
        for (int i = 0; i < numEqns; i++) {
            denom += vect[i] * vect[i];
        }
        alpha = (-numer / denom);
    }

    private void calculateNextSolution() {
        for (int i = 0; i < numVars; i++) {
            currentSolution[i] = currentSolution[i] + alpha * d_k[i];
        }
    }

    private double calculateFunc() {
        double f = 0.0;
        for (int i = 0; i < numEqns; i++) {
            f += residue[i] * residue[i];
        }
        return f;
    }

    private double calculateBeta(double[] grad_k, double[] grad_km1) {
        double numer = 0.0;
        double denom = 0.0;
        for (int i = 0; i < numVars; i++) {
            numer += grad_k[i] * grad_k[i];
            denom += grad_km1[i] * grad_km1[i];
        }
        return numer / denom;
    }
}
