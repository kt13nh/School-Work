package pso;
import java.util.*;

/**
 *
 * @author Kevin Tram, 5459854
 */
public class Main {
    //main method to provide parameters for PSO and random search through user
    //input
    public static void main(String[] args) {
        try{
            Vector fitnessVector = new Vector(); // to hold all of the generated fitness values
            Scanner sc = new Scanner(System.in);
            //bounds for random search
            double upperBound = 5.14; double lowerBound = -5.14; int dimension = 30;
            
            
            System.out.print("Enter 1 for PSO, Enter 2 for random: ");
            int choice = sc.nextInt();
            System.out.print("Enter how many runs you would like to perform: ");
            int num = sc.nextInt();
            
            double mean=0;
            int count=0;
            //if user chooses PSO 
            if(choice==1){
                System.out.print("Enter your w value: ");
                double w = sc.nextDouble();
                System.out.print("Enter your c1 value: ");
                double c1 = sc.nextDouble();
                System.out.print("Enter your c2 value: ");
                double c2 = sc.nextDouble();
                System.out.println("Calculating, please wait...");
                for(int i=0;i<num;i++){
                    Swarm swarm = new Swarm(w,c1,c2);
                    PSO pso = new PSO();
                    Solution res = pso.run(swarm);
                    fitnessVector.add(res.fitness);
                    mean = mean+res.fitness;
                    count++;
                    System.out.println("Best Fitness of run "+count+": "+res.fitness);
                    if(i!=num-1)System.out.println("Calculating next run...");
                }
            }
            //if user chooses random
            else if(choice==2){
                //run "num" number times, 1000 number of iterations, and produce 30 random positions each iteration
                double globalBestRandom = Double.MAX_VALUE;
                for(int z=0;z<num;z++){
                    double bestFitness=Double.MAX_VALUE;
                    for(int j=0;j<1000;j++){
                        Vector rand = new Vector();
                        for(int i=0;i<dimension;i++){
                            double randomPos = Math.random() * (5.12 - (-5.12)) + (-5.12);
                            rand.add(randomPos);
                        }
                        Problem p = new Problem(lowerBound,upperBound,dimension);
                        double fitness = p.evaluate(rand);
                        
                        if(fitness<bestFitness)bestFitness =fitness;
                    }
                    if(bestFitness<globalBestRandom)globalBestRandom=bestFitness;
                    fitnessVector.add(bestFitness);
                    mean = mean + bestFitness;
                    count++;
                    System.out.println("Best Random Fitness of run "+z+": "+bestFitness);
                }
                System.out.println("Global Best Random Fitness: "+globalBestRandom);
            }
            mean = mean/count;
            double standardDev=0.0;
            for(int i=0;i<fitnessVector.size();i++){
                double value = Double.parseDouble(fitnessVector.get(i).toString());
                standardDev = standardDev + Math.pow(value-mean,2);
            }
            standardDev=standardDev/count;
            standardDev = Math.sqrt(standardDev);
            Collections.sort(fitnessVector);
            int medianIndex = fitnessVector.size()/2;
            double median = Double.parseDouble(fitnessVector.get(medianIndex).toString());
            if(choice==1)
                System.out.println("After 5000 iterations and "+num+" runs:");
            System.out.println("Mean: "+mean);
            System.out.println("Standard Deviation: "+standardDev);
            System.out.println("Median: "+median);
        }catch(Exception ex){
            System.out.println("Invalid Input!");
        }
    }
}
