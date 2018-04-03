package pso;
import java.util.*;
/**
 *
 * @author Kevin Tram, 5459854
 */
public class Solution {
    //store a solutions's fitness and position
    Vector position = new Vector();
    double fitness=Double.MAX_VALUE;
    
    public void setPosition(int i,double pos){
        this.position.set(i, pos);
    }
    double getFitness(){
        return this.fitness;
    }
    
    public void setFitness(double fitness){
        this.fitness=fitness;
    }
}
