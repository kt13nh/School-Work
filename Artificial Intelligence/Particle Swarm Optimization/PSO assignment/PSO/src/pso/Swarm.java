package pso;
import java.util.*;
/**
 *
 * @author Kevin Tram, 5459854
 */
public class Swarm {
    
    Problem psoProblem;
    Solution globalBest = new Solution();
    Vector<Particle> particles = new Vector<Particle>();
    VelocityCalculator vCalc;
    //set swarm with user parameters
    public Swarm(double w, double c1, double c2){
        this.vCalc = new VelocityCalculator(w,c1,c2);
        this.psoProblem = new Problem(-5.12,5.12,30);
    }
    //initialize swarm and particles in the swarm
    public void initialize(){
        //initialize the swarm
        for(int i=0;i<psoProblem.dimension;i++){
            Particle particle = new Particle();
            particle.initialize(psoProblem);
            double fitness = psoProblem.evaluate(particle.currPosition);
            particle.fitness = fitness;
            particles.add(particle);
        }
        initializeGlobalBest();
    }
    
    //initialize the globalbest fitness and position to 0.0
    public void initializeGlobalBest(){
        int index=-1;
        double fitness = Double.MAX_VALUE;
        for(int i=0;i<psoProblem.dimension;i++){
            double particleFitness = psoProblem.evaluate(particles.get(i).currPosition);
            if(particleFitness<fitness){
                fitness = particleFitness;
                index = i;
            }    
            //initialize positions to 0 for copying after
            globalBest.position.add(0.0);
        }
        if(index!=-1){
            Solution particleSolution = tempSolution(particles.get(index),fitness);
            //System.out.println("Hello I am here: "+globalBest.fitness);
            copySolutions(particleSolution,globalBest);
            
        }
    }
    
    //update personal best solution of specified particle
    public void updatePersonalBest(Particle particle){
        double currentFitness = particle.fitness;
        if(particle.personalBest.fitness>currentFitness){
            Solution particleSolution = tempSolution(particle,currentFitness);
            copySolutions(particleSolution,particle.personalBest);
        }
    }
    
    //update the global best fitness and position after a run, if necessary
    public void updateGlobalBest(){
        int index =-1;
        double fitness = globalBest.getFitness();
        for(int i=0;i<psoProblem.dimension;i++){
            double particleFitness=psoProblem.evaluate(particles.get(i).currPosition);
            if(particleFitness<fitness){
                fitness = particleFitness;
                index = i;
            }
        }
        //if a particle solution better than global then copy that solution to global
        if(index!=-1){
            Solution particleSolution = tempSolution(particles.get(index),fitness);
            copySolutions(particleSolution,globalBest);
        }
    }
    
    //utility function to copy a solution of a particle, and return the solution
    public Solution tempSolution(Particle particle,double fitness){
        Solution solution = new Solution();
        solution.setFitness(fitness);
        for(int i=0;i<psoProblem.dimension;i++){
            solution.position.add(Double.parseDouble(particle.currPosition.get(i).toString()));
        }
        return solution;
    }
    
    //function to copy a solution from a specified solution, to another solution
    public void copySolutions(Solution copyFromThis, Solution copyToThis){
        copyToThis.setFitness(copyFromThis.getFitness());
        for(int i=0;i<psoProblem.dimension;i++){     
            copyToThis.setPosition(i,Double.parseDouble(copyFromThis.position.get(i).toString()));
        }
    }
    
    //update the position of a specified particle 
    public void positionUpdate(Particle particle){
        for(int i=0;i<psoProblem.dimension;i++){
            double velocity = Double.parseDouble(particle.velocity.get(i).toString());
            double oldPosition = Double.parseDouble(particle.currPosition.get(i).toString());
            double newPosition =0;
            if(velocity > psoProblem.maxVelocity){
                velocity = psoProblem.maxVelocity;
            }
            else if(velocity<-psoProblem.maxVelocity){
                velocity = -psoProblem.maxVelocity;
            }
            newPosition = newPosition + oldPosition + velocity;
            particle.currPosition.set(i, newPosition);
            //compare new position with personal best
            particle.compare();
        }
    }
}
