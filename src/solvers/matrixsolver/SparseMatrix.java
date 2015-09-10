package solvers.matrixsolver;

import java.util.ArrayList;

/**
 *
 * @author Sourabh Bhat
 */
public class SparseMatrix {

    private ArrayList<MatrixElement> matrixElements = new ArrayList<>();

    public SparseMatrix() {
    }

    public void setMatrixElement(int row, int column, double value) {
        matrixElements.add(new MatrixElement(row, column, value));
    }

    public ArrayList<MatrixElement> getMatrix() {
        return matrixElements;
    }

    /**
     * Check whether the sparse matrix is having unique set of rows and columns.
     * This function must be normally used for debugging purposes only.
     * The code written for creation of matrices must make sure that there 
     * are no repeated row and column combinations, or else the 
     * matrix solver using this matrix may fail.
     * @return true only if no repeated row and column combination, 
     * false if repeated row and column combination exists in the matrix
     */
    public boolean isSparseMatrixOkay() {
        // find max row size, and max column size
        int numColumns = 0;
        int numRows = 0;
        for (int i = 0; i < matrixElements.size(); i++) {
            MatrixElement e = matrixElements.get(i);
            if (e.column > numColumns - 1) {
                numColumns = e.column + 1;
            }
            if (e.row > numRows - 1) {
                numRows = e.row + 1;
            }
        }

        // Check if same row and column is set twice
        for (int i = 0; i < numRows; i++) {
            int[] markerRow = new int[numColumns]; // create empty row with zeros
            for (int j = 0; j < matrixElements.size(); j++) {
                MatrixElement e = matrixElements.get(j);
                if (e.row == i) {
                    if (markerRow[e.column] != 0) {
                        return false;
                    } else {
                        markerRow[e.column] = 1;
                    }
                }
            }
        }

        return true;
    }
}