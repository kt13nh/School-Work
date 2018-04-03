package ga.tsp;
import java.util.*;
/**
 *
 * @author Kevin Tram, 5459854
 */
public class Main {
    
    public static void main(String[] args){
        //GA ga = new GA();
        Scanner scanner = new Scanner(System.in);
        try{
            //user parameters
            double crossoverRate;
            double mutation;
            int crossoverType;
            int dataSet;
            int seed;
            int maxGen;
            System.out.println("Please enter 1 for Berlin 52 dataset");
            System.out.println("Please enter 2 for Djibouti 38 dataset");
            dataSet = scanner.nextInt();
            System.out.println("Please enter crossover rate in decimal form: ");
            crossoverRate = scanner.nextDouble();
            System.out.println("Please enter mutation rate in decimal form: ");
            mutation = scanner.nextDouble();
            System.out.println("Please enter 1 for Uniform Order Crossover");
            System.out.println("Please enter 2 for Order Crossover");
            crossoverType = scanner.nextInt();
            System.out.println("Please enter a seed you would like to use:");
            seed = scanner.nextInt();
            System.out.println("Please enter max generation:");
            maxGen = scanner.nextInt();
            GA ga = new GA(crossoverRate, mutation, crossoverType,dataSet,seed,maxGen);
        
            while(true){
                System.out.println("Would you like to produce another "+maxGen+" generations?");
                System.out.println("Enter y if yes, otherwise enter n");
                String input = scanner.next();
                if(input.equals("n")){
                    break;
                }else if(input.equals("y")){
                    ga.runGA();
                }else{
                    System.out.println("Invalid input!");
                }
            }
        }catch(Exception e){
            System.out.println("Invalid input!");
        }
            
    }
    
}
