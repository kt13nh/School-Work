package pso;
import java.util.*;
/**
 *
 * @author Kevin Tram, 5459854
 */
public class Problem {
    
    int dimension;
    double upperXBound;
    double lowerXBound;
    double maxVelocity;
    //inintialize bounds and dimensions
    public Problem(double lower, double upper, int dimension){
        this.dimension = dimension;
        this.lowerXBound = lower;
        this.upperXBound = upper;
        setMaxVelocity();
    }
    
    public void setMaxVelocity(){
        this.maxVelocity = ((upperXBound - lowerXBound)/2);
    }
    
    //calculate the fitness, and return the fitness with Rastrigin function
    public double evaluate(Vector position){
        double fitness=0.0;
        for(int i=0;i<dimension;i++){
            Double pos = Double.parseDouble(position.get(i).toString());
            //rastrigin function
            fitness = fitness + pos*pos - (10.0*Math.cos(2.0*Math.PI*pos));
        }
        return 10*dimension+fitness;
    }
    
}
