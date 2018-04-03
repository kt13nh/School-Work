package ga.tsp;
import java.util.*;
/**
 *
 * @author Kevin Tram,5459854
 */
//this class is used to store information on each chromosome including
//their fitness, path, and population
public class Chromosome {
    double fitness;
    City[] chromosome;
    int population;
    Random rng = new Random();
    
    
    //initialize chromosome with population bound, and set all
    //the chromosomes to include "-1" as a placeholder for blank city
    public void initChromosome(int population){
        this.population = population;
        this.chromosome = new City[population];
        clearChromosome();
    }
    //randomize the population based on the population size
    public void randomizePopulation(){
       for(int i=0;i<population;i++){    
            while(this.chromosome[i].city== -1){
                int randomCity = rng.nextInt(population);
                if(contains(this.chromosome,randomCity)==false){
                    this.chromosome[i].city=randomCity;
                }
            }
        } 
    }
    //init all values to -1 to to use array for random
    //generation. Loop to initialize the cities run while there are still
    //-1 values in the array
    public void clearChromosome(){
        //System.out.println(chromosome.length);
        for(int i=0;i<this.population;i++){
            //System.out.println(i);
            this.chromosome[i]= new City(-1);
        }
    }
    //check to see if the chromosome already contains a specific city
    //return true if it does, otherwise return false
    public boolean contains(City[] chromosome, int city){
        for(int i=0;i<chromosome.length;i++){
            if(chromosome[i].city==city){
                return true;
            }
        }
        return false;
    }
    //for debugging purposes
    public void print(){
        for(int i=0;i<this.population;i++){
            System.out.print(chromosome[i].city);
        }
        System.out.println();
    }
}
