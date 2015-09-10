package solvers;

/**
 *
 * @author Sourabh Bhat
 */
public interface TimeSteppingSolver {

    /**
     * Solves the equation upto the stopping time
     * @return solution at stopping time as a double[]
     */
    public double[] solve();

    /**
     * Steps once in time by amount dt
     * @return true if next solution is calculated,  
     * false if stopping time is already reached
     */
    public boolean stepSolution();
    
    /**
     * Method for returning the current time of simulation, 
     * so that it can be used to display by calling function or plot
     * @return time that the simulation has reached currently
     */
    public double getCurrentTime();
}
