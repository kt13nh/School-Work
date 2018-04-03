package ga.tsp;
import java.util.*;
import java.io.*;
/**
 *
 * @author Kevin Tram, 5459854
 */
//this class is used for the initialization of the files provided,
//as well as determining what cities the salesman must visit
public class Salesman {
    City[] citiesToVisit;
    int numberOfCities;
    
    Salesman(){
    }
    //count the number of cities the salesman must visit
    public void countNumberOfCities(String fileName){
        int count=0;
        try{
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            String index;
            while(true){
                index = scanner.nextLine();
                if(index.equals("NODE_COORD_SECTION"))
                    break;
            }
            while(true){
                index = scanner.nextLine();
                if(index.equals("EOF")){
                    break;
                }
                else{
                    count++;
                }
            }
        }catch(Exception ex){}
        this.citiesToVisit = new City[count];
        this.numberOfCities = count;
    }
    //initialize all of the city data 
    public void initCityData(int dataSet){
        double x;
        double y;
        String fileName="";
        if(dataSet==1){
            fileName= "berlin52.tsp";
        }else if(dataSet==2){
            fileName="dj38.tsp";
        }
        countNumberOfCities(fileName);
        citiesToVisit = new City[numberOfCities];
        try{
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            int i=0;
            String next;
            while(true){
                next = scanner.nextLine();
                if(next.equals("NODE_COORD_SECTION"))
                    break;
            }
            while(true){
                next = ""+scanner.next();
                if(next.equals("EOF")){
                    break;
                }
                else{
                    x = Double.parseDouble(scanner.next());
                    y = Double.parseDouble(scanner.next());
                    City city = new City(x,y,i);
                    citiesToVisit[i]=city;
                    scanner.nextLine();
                }
                i++;
            }
        }catch(Exception ex){}
    }
    //for debugging purposes
    public void print(){
        for(int i=0;i<this.numberOfCities;i++){
            System.out.println(citiesToVisit[i].x+","+citiesToVisit[i].y);
        }
    }
}
