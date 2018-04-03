#include <iostream>
#include <stdlib.h>
#include "approximations.h"
#include "randomNumberGenerator.h"
using namespace std;
//Kevin Tram , 5459854, assignment 1 
int main(int argc, char** argv) {
	
	Random r;
	approximations approx;
	
	double result; // result to calculate approximation value
	double rng; // 
	double centreX=0.5;//value of the X value in the centre
	double centreY=0.5;//value of the Y value in the centre
	double centre=0.5; //value of the centre
	double x; //value for the x value being generated randomly
	double y;//value for the y value being generated randomly 
	int numShots=32000;//set the total number of shots to take place
	int shot=0; //keep track of the number of shots taken
	int landed=0; //track number of shots landing within the circle
	
	//initialize the generator seeds
	r.init(5,10000,3000);
	//initialize the centre X and centre Y
	approx.init(centreX,centreY);
	int approximationNum=0;//use to enumerate each approximation
	/*
	while loop to run until the number of total shots taken is equal
	to variable numShots
	*/
	while(true){
		if(shot<numShots){
			for(int j=0;j<1000;j++){
				//increment the number of shots taken
				shot++;
				//generate random x where the shot lands
				x=r.unif();
				//generate random y where the shot lands
				y=r.unif();
				/*
				check to see how far the shot is from the centre, if
				it is within the circle, then increment a landed shot 
				*/
				if(approx.distance(x,y)<=centre){
					landed++;
				}
			}
			approximationNum++;
			//given expression by prof. Vlad to approximate pi
			result = 4*(double)landed/(double)shot;
			//print the approximation after every 1000 shots
			cout<<"Approximation "<<approximationNum<<": "<<result<<"\n";
		}
		else break;
	}
	
}
