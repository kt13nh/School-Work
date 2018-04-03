package ga.tsp;

/**
 *
 * @author Kevin Tram, 5459854
 */
//this class is used to store data on the x and y 
//coordinates of each cities
public class City {
    double x;
    double y;
    int city;
    //city constructors
    public City(){}
    public City(int city){
        this.city =city;
    }
    public City(double x, double y, int city){
        initCity(x,y,city);
    }
    //initialize the x and y of the city
    public void initCity(double x,double y,int city){
        this.x = x;
        this.y = y;
        this.city = city;
    }
}
