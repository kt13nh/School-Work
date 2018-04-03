package pso;
import java.util.*;
/**
 *
 * @author Kevin Tram, 5459854
 */
public class VelocityCalculator {
    double w;
    double c1;
    double c2;
    
    //set user parameters
    public VelocityCalculator(double w, double c1, double c2){
        this.w = w;
        this.c1 = c1;
        this.c2 = c2;
    }
    
    //calculate and set new velocity of a specified particle, based on the 
    //global best 
    public void calculateVelocity(Particle particle, Solution globalBest){
        Random rng = new Random();
        for(int i=0;i<particle.currPosition.size();i++){
            double r1 = rng.nextDouble();
            double r2 = rng.nextDouble();
            double particleVelocity = Double.parseDouble(particle.velocity.get(i).toString());
            double particleBest = Double.parseDouble(particle.personalBest.position.get(i).toString());
            double particlePosition = Double.parseDouble(particle.currPosition.get(i).toString());
            double gBest = Double.parseDouble(globalBest.position.get(i).toString());
            double vel =0; 
            vel = vel + (w*particleVelocity + c1*r1*(particleBest-particlePosition)
                    +c2*r2*(gBest - particlePosition));
            particle.velocity.set(i,vel);
        }
    }
}
