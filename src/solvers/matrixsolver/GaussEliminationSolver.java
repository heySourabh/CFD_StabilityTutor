package solvers.matrixsolver;

/**
 *
 * @author Sourabh Bhat
 */
public class GaussEliminationSolver implements MatrixSolver {

    double[][] A;
    double[] b;

    public GaussEliminationSolver(double[][] A, double[] b) {
        this.A = A;
        this.b = b;
    }

    // ** Warning: Matrix A and B are changed inside this function
    @Override
    public double[] solve() {
        final double EPS = 1e-6;
        double a, sum;
        int size = b.length;
        double[] X = new double[size];
        int i, j, k;
        int pivotRow, row, col;
        double maxValue, tempValue;

        // Elimination with pivoting
        // Can be used even for non-dominat diagonal matrix
        for (i = 0; i < size; i++) {
            // Pivoting procedure starts here
            pivotRow = i;
            maxValue = Math.abs(A[i][i]);
            for (row = i + 1; row < size; row++) {
                if (maxValue < Math.abs(A[row][i])) {
                    maxValue = Math.abs(A[row][i]);
                    pivotRow = row;
                }
            }
            if (pivotRow != i) {
                for (col = i; col < size; col++) {
                    tempValue = A[i][col];
                    A[i][col] = A[pivotRow][col];
                    A[pivotRow][col] = tempValue;
                }
                tempValue = b[i];
                b[i] = b[pivotRow];
                b[pivotRow] = tempValue;
            } // Pivoting procedure ends here

            for (j = i + 1; j < size; j++) {
                if (Math.abs(A[i][i]) < EPS) { // Check sigularity
                    System.out.println("The matrix is singular..");
                    throw new RuntimeException("The matrix is singular");
                }
                a = A[j][i] / A[i][i];
                A[j][i] = 0.0;
                for (k = i + 1; k < size; k++) {
                    A[j][k] = A[j][k] - A[i][k] * a;
                }
                b[j] = b[j] - b[i] * a;
            }
        }

        // Back substitution
        for (i = size - 1; i >= 0; i--) {
            sum = b[i];
            for (j = i + 1; j < size; j++) {
                sum -= A[i][j] * X[j];
            }
            X[i] = sum / A[i][i];
        }

        // Setting matrix B as the solution
        // for (i = 0; i < size; i++)
        // b[i] = X[i];

        return X;
    }
}
