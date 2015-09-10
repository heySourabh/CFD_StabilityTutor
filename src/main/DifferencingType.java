/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author spbhat
 */
public enum DifferencingType  {
    FORWARD_DIFF, BACKWARD_DIFF, CENTRAL_DIFF, UPWINDING;
    
    public String getName(){
        switch (this) {
            case FORWARD_DIFF: return "Forward Differencing";
            case BACKWARD_DIFF: return "Backward Differencing";
            case CENTRAL_DIFF: return "Central Differencing";
            case UPWINDING: return "Upwinding";
            default: return "Not defined";
        }
    }
    
    public static DifferencingType getDifferencingType(String name){
        DifferencingType[] dt = DifferencingType.values();
        for (int i = 0; i < dt.length; i++) {
            if(dt[i].getName().equals(name)){
                return dt[i];
            }
        }
        return null;
    }
}
