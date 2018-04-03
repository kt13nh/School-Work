package pso;

/**
 *
 * @author Kevin Tram, 5459854
 */
public class PSO {
    
    //run the PSO algorithm
    public Solution run(Swarm swarm){
        //initialize particles in the swarm and velocities
        swarm.initialize();
        int j=0;
        boolean run = true;
        while(run){
            //run for 5000 iterations
            if(j==5000)
                break;
            j++;
            //for every particle
            for(int i=0;i<swarm.particles.size();i++){
                Particle currentParticle = swarm.particles.get(i);
                //evaluate fitness
                currentParticle.fitness = swarm.psoProblem.evaluate(currentParticle.currPosition);
                //update personal best
                swarm.updatePersonalBest(currentParticle);
            }
            //update neighboorhood best
            swarm.updateGlobalBest();
            //for every particle
            for(int i=0;i<swarm.particles.size();i++){
                Particle currentParticle = swarm.particles.get(i);
                Solution gBest = swarm.globalBest;
                //calculate new velocities and update
                swarm.vCalc.calculateVelocity(currentParticle,gBest);
                //update particle position
                swarm.positionUpdate(currentParticle);
            }
        }
        return swarm.globalBest;
    }
}
