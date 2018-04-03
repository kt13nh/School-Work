package ga.tsp;
import java.util.*;
/**
 *
 * @author Kevin Tram, 5459854
 */
public class GA {
    //user parameters
    double crossOverRate;
    double mutationRate;
    int populationSize;
    int seed;
    int maxGen;
    int tournamentKValue=3;
    boolean elitism = true;
    boolean unifCrossOver=false;
    boolean orderCrossover=false;
    //Global GA variables
    Chromosome[] population;
    double bestFitness=Integer.MAX_VALUE;
    int bestChromosome;
    int runs;
    int bestGeneration;
    int numberOfGenerations;
    Salesman salesman = new Salesman();
    Random rng = new Random(this.seed);
    //run the GA
    public void runGA(){
        //run from 1 to maxGen
        for(int z=1;z<=maxGen;z++){
            //LinkedList for the new population ( Next Generation )
            LinkedList<Chromosome> newPop = new LinkedList<Chromosome>();
            //elitism is always true as specified in assignment
            if(elitism==true){
                newPop.add(evaluateFitness());
            }
            /**simple GA loop as given in the assignment pdf.
            run popsize/2+1 since our Crossovers from class
            produce 2 children**/
            for(int i=1;i<populationSize/2+1;i++){
                evaluateFitness();
                Chromosome parent1 = tournamentSelection();
                Chromosome parent2 = tournamentSelection();
                double chanceOfCrossOver = Math.random()*1;
                double chanceOfMutation = Math.random()*1;
                if(chanceOfCrossOver < this.crossOverRate){
                    if(unifCrossOver==true){
                        if(i!=populationSize/2)
                            uniformOrderCrossover(parent1,parent2,newPop);
                        else{
                            uniformOrderCrossover(parent1,parent2,newPop);
                            newPop.remove(i+1);
                        }
                    }
                    else if(orderCrossover==true){
                        if(i!=populationSize/2)
                            orderCrossover(parent1,parent2,newPop);
                        else{
                            orderCrossover(parent1,parent2,newPop);
                            newPop.remove(i+1);
                        }
                    }
                }else{
                    if(i!=populationSize/2){
                        newPop.add(parent1);
                        newPop.add(parent2);
                    }else{newPop.add(parent1);}
                }
                if(chanceOfMutation < this.mutationRate){
                    InversionMutation(newPop.get(i),i,newPop);
                }
            }
            copyNewPop(newPop);
            print(z);
        }
        //increment number of runs
        this.runs++;
        printParameters();
        //reset best values after the loop and print
        this.bestFitness=Integer.MAX_VALUE;
        this.bestChromosome=-1;
        this.bestGeneration=-1;
    }
    
    public GA(double crossOverRate,double mutation,int crossOverType,int dataSet,int seed,int maxGen){
        //Initialize all of the user parameters
        salesman.initCityData(dataSet);
        this.populationSize = salesman.numberOfCities;
        this.crossOverRate = crossOverRate;
        this.mutationRate=mutation;
        this.tournamentKValue=3;
        this.seed = seed;
        this.maxGen=maxGen;
        initializePopulation();
        //if 1 then uniform order crossOver
        //if 2 then order crossover
        if(crossOverType ==1){
            this.unifCrossOver=true;
        }else if(crossOverType==2){
            this.orderCrossover=true;
        }
        //start the loop
        //implemented another method to keep from looking messy
        runGA();
    }
    
    //copy the new population to the current population
    public void copyNewPop(LinkedList<Chromosome> newPop){
        for(int i=0;i<this.populationSize;i++){
             for(int j=0;j<this.populationSize;j++){
                 this.population[i].chromosome[j]=newPop.get(i).chromosome[j];
                 this.population[i].chromosome[j].city=newPop.get(i).chromosome[j].city;
                 this.population[i].chromosome[j].x=newPop.get(i).chromosome[j].x;
                 this.population[i].chromosome[j].y=newPop.get(i).chromosome[j].y;
             }
        }
    }
    //initialize the population randomly
    public void initializePopulation(){
        this.population = new Chromosome[this.populationSize];
        for(int i=0;i<this.populationSize;i++){
                Chromosome chromosome = new Chromosome();
                chromosome.initChromosome(this.populationSize);
                chromosome.randomizePopulation();
                population[i]=chromosome;
            for(int j=0;j<this.populationSize;j++){
                int city = population[i].chromosome[j].city;
                double locationX=salesman.citiesToVisit[city].x;
                double locationY=salesman.citiesToVisit[city].y;
                
                population[i].chromosome[j].x = locationX;
                population[i].chromosome[j].y = locationY;
            }
        }
    }
    
    //tournament selection with k value 3
    //winner among the 3 is selected
    public Chromosome tournamentSelection(){
        Chromosome bestChromosome=this.population[0];
        Chromosome[] tournamentPool = new Chromosome[this.tournamentKValue];
        double bestFit=Double.MAX_VALUE;
        for(int i=0;i<this.tournamentKValue;i++){
            tournamentPool[i]=null;
        }
        for(int i=0;i<this.tournamentKValue;i++){ 
            while(tournamentPool[i]== null){
                int randomChromosome= (int) Math.floor(Math.random()*this.populationSize);
                if(contains(tournamentPool,this.population[randomChromosome])==false){
                    tournamentPool[i]=this.population[randomChromosome];
                }
            }
        } 
        //check the tournament pool for the most fit 
        for(int i=0;i<this.tournamentKValue;i++){
            if(tournamentPool[i].fitness<bestFit){
                bestFit = tournamentPool[i].fitness;
                bestChromosome = tournamentPool[i];
                
            }
        }
        for(int i=0;i<this.populationSize;i++){
            int city = bestChromosome.chromosome[i].city;
            double locationX=salesman.citiesToVisit[city].x;
            double locationY=salesman.citiesToVisit[city].y;
                
            bestChromosome.chromosome[i].x = locationX;
            bestChromosome.chromosome[i].y = locationY;
        }
        return bestChromosome;
    }
    //check to see if the current chromosome pool already contains 
    //a specific chromosome
    public boolean contains(Chromosome[] pool, Chromosome route){
        for(int i=0;i<pool.length;i++){
            if(pool[i]==route){
                return true;
            }
        }
        return false;
    }
    //perform uniform order crossover and adding the 2 children to the new population
    public void uniformOrderCrossover(Chromosome parent1, Chromosome parent2,LinkedList newPop){    
        boolean[] bitMask = new boolean[this.populationSize];
        Chromosome child1=new Chromosome();
        Chromosome child2=new Chromosome();
        child1.initChromosome(this.populationSize);
        child2.initChromosome(this.populationSize);
        
        
        for(int i=0;i<this.populationSize;i++){
            boolean randomNum = rng.nextBoolean();
            bitMask[i]=randomNum;
        }
        for(int i=0;i<this.populationSize;i++){
            if(bitMask[i]==true){
                child1.chromosome[i].city=parent1.chromosome[i].city;
                child1.chromosome[i].x=parent1.chromosome[i].x;
                child1.chromosome[i].y=parent1.chromosome[i].y;
                child2.chromosome[i].x=parent2.chromosome[i].x;
                child2.chromosome[i].city=parent2.chromosome[i].city;
                child2.chromosome[i].y=parent2.chromosome[i].y;
            }
            else{
                child1.chromosome[i].city=-1;
                child2.chromosome[i].city=-1;
            }
        }
        for(int i=0;i<child1.chromosome.length;i++){
            if(child1.chromosome[i].city==-1){
               int j=0;
               while(child1.chromosome[i].city==-1){
                   if(contains(child1,parent2.chromosome[j].city)==false){
                       child1.chromosome[i].city=parent2.chromosome[j].city;
                       child1.chromosome[i].x=parent2.chromosome[j].x;
                       child1.chromosome[i].y=parent2.chromosome[j].y;
                   }
                   j++;
               }
            }
            if(child2.chromosome[i].city==-1){
               int j=0;
               while(child2.chromosome[i].city==-1){
                   if(contains(child2,parent1.chromosome[j].city)==false){
                       child2.chromosome[i].city=parent1.chromosome[j].city;
                       child2.chromosome[i].x=parent1.chromosome[j].x;
                       child2.chromosome[i].y=parent1.chromosome[j].y;
                   }
                   j++;
               }
            }   
        }
        newPop.add(child1);
        newPop.add(child2);
    }
    //perform order crossover and adding the 2 children to the new population
    public void orderCrossover(Chromosome parent1, Chromosome parent2, LinkedList newPop){
        int index1;
        int index2=-5;
        int temp;
        Chromosome child1=new Chromosome();
        Chromosome child2=new Chromosome();
        child1.initChromosome(this.populationSize);
        child2.initChromosome(this.populationSize);
        index1 = rng.nextInt(this.populationSize);
        while(index2 == -5 || index2==index1){
            index2= rng.nextInt(this.populationSize);
        }
        if(index1>index2){
            temp=index2;
            index2=index1;
            index1=temp;
        }
        for(int i=index1;i<index2;i++){
            child1.chromosome[i]=parent1.chromosome[i];
            child2.chromosome[i]=parent2.chromosome[i];
            child1.chromosome[i].city=parent1.chromosome[i].city;
            child2.chromosome[i].city=parent2.chromosome[i].city;
            child1.chromosome[i].x = parent1.chromosome[i].x;
            child2.chromosome[i].x=parent2.chromosome[i].x;
            child1.chromosome[i].y = parent1.chromosome[i].y;
            child2.chromosome[i].y=parent2.chromosome[i].y;
        }
        for(int i=0;i<child1.chromosome.length;i++){
            if(child1.chromosome[i].city==-1){
               int j=0;
               while(child1.chromosome[i].city==-1){
                   if(contains(child1,parent2.chromosome[j].city)==false){
                       child1.chromosome[i].city=parent2.chromosome[j].city;
                       child1.chromosome[i].x=parent2.chromosome[j].x;
                       child1.chromosome[i].y=parent2.chromosome[j].y;
                   }
                   j++;
               }
            }
            if(child2.chromosome[i].city==-1){
               int j=0;
               while(child2.chromosome[i].city==-1){
                   if(contains(child2,parent1.chromosome[j].city)==false){
                       child2.chromosome[i].city=parent1.chromosome[j].city;
                       child2.chromosome[i].x=parent1.chromosome[j].x;
                       child2.chromosome[i].y=parent1.chromosome[j].y;
                   }
                   j++;
               }
            }   
        }
        newPop.add(child1);
        newPop.add(child2);
    }
    //inversion mutation by 2 random index, and inverting the values between them
    public void InversionMutation(Chromosome chromosome,int index, LinkedList newPop){
        int temp;
        int index1;
        int index2=-5;
        Stack subset = new Stack();
        Chromosome child=new Chromosome();
        child.initChromosome(chromosome.population);
        for(int i=0;i<chromosome.population;i++){
            child.chromosome[i]=chromosome.chromosome[i];
        }
        index1 = rng.nextInt(chromosome.population);
        while(index2 == -5 || index2==index1){
            index2= rng.nextInt(chromosome.population);
        }
        if(index1>index2){
            temp=index2;
            index2=index1;
            index1=temp;
        }
        for(int i=index1;i<=index2;i++){
            subset.push(child.chromosome[i]);
        }
        for(int i=index1;i<=index2;i++){
            City city = (City)subset.pop();
            child.chromosome[i]=city;
            child.chromosome[i].x = city.x;
            child.chromosome[i].y=city.y;
            this.population[index].chromosome = child.chromosome;
            this.population[index].chromosome[i]=child.chromosome[i];
            this.population[index].chromosome[i].x=child.chromosome[i].x;
            this.population[index].chromosome[i].y=child.chromosome[i].y;
        }
    }
    
    //check to see if a city is already in the chromosome
    //return true if it does, false otherwise
    public boolean contains(Chromosome chromosome, int city){
        for(int i=0;i<chromosome.chromosome.length;i++){
            if(chromosome.chromosome[i].city==city){
                return true;
            }
        }
        return false;
    }
    //evaluates the fitness of the population, return the best chromosome
    public Chromosome evaluateFitness(){
        double distance=0.0;
        Chromosome bestChromosome=this.population[0];
        bestChromosome.fitness=this.population[0].fitness;
        for(int i=0;i<population.length;i++){
            for(int j=0;j<population.length-1;j++){
                double city1X = population[i].chromosome[j].x;
                double city2X = population[i].chromosome[j+1].x;
                double city1Y = population[i].chromosome[j].y;
                double city2Y = population[i].chromosome[j+1].y;
                double xDifference = city1X-city2X;
                double yDifference = city1Y-city2Y;
                distance = distance + Math.sqrt((xDifference*xDifference)+(yDifference*yDifference));
            }
            population[i].fitness = distance;
            if(population[i].fitness<bestChromosome.fitness){
                bestChromosome = population[i];
                bestChromosome.fitness=population[i].fitness;
            }
            distance=0;
        }
        return bestChromosome;
    }
    //method to print all of the relevant information of each generation
    public void print(int generationNum){
        double generationAverage=0;
        double bestFitness = Integer.MAX_VALUE;
        int bestChromosome=0;
        for(int i=0;i<this.populationSize;i++){
            generationAverage = generationAverage + this.population[i].fitness;
            if(this.population[i].fitness<bestFitness){
                bestFitness=this.population[i].fitness;
                bestChromosome = i;
                if(bestFitness<=this.bestFitness){
                    this.bestFitness=bestFitness;
                    this.bestChromosome = bestChromosome;
                    this.bestGeneration=generationNum;
                }
            }
        }
        generationAverage = generationAverage/this.populationSize;
        System.out.println();
        System.out.println("Generation "+generationNum);
        System.out.println("---------------------------------------------------------------");
        System.out.println("Chromosome "+this.bestChromosome+" has the best fitness of: "+ this.bestFitness);
        System.out.println("Average fitness for generation "+generationNum+": "+ generationAverage);
        System.out.println("---------------------------------------------------------------");
    }
    //print all of the user's parameters
    public void printParameters(){
        
        System.out.println();
        System.out.println("Crossover Rate used: "+this.crossOverRate);
        System.out.println("Mutation Rate used: "+this.mutationRate);
        System.out.println("Population Size: "+this.populationSize);
        System.out.println("Seed used: "+this.seed);
        System.out.println("Max generation: "+this.maxGen);
        System.out.println("After "+(maxGen*this.runs)+" generations:");
        System.out.println("The best fitness is "+this.bestFitness+", best chromosome: "+this.bestChromosome+" from this run's generation "+bestGeneration+".");
        System.out.println();
    }
}
