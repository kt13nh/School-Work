/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pso;
import java.util.*;
/**
 *
 * @author Kevin Tram, 5459854
 */
public class Particle {
    //store particle information
    double fitness;
    Solution personalBest = new Solution();
    Vector currPosition = new Vector();
    Vector velocity = new Vector();
    
    //initialize the particle
    public void initialize(Problem problem){
        //set random positions, and set velocity to 0.0
        for(int i=0;i<problem.dimension;i++){
            //between -5.14 and 5.14
            double randomPos = Math.random() * (5.12 - (-5.12)) + (-5.12);
            currPosition.add(randomPos);
            personalBest.position.add(randomPos);
            velocity.add(0.0);
        }
    }
    
    //compare current fitness and personal bets fitness
    public void compare(){
        if(fitness<personalBest.fitness){
            personalBest.fitness = fitness;
            for(int i=0;i<currPosition.size();i++){
                personalBest.position.set(i, Double.parseDouble(currPosition.get(i).toString()));
            }
        }
    }
    
}
