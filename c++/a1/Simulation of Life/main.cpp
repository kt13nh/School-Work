#include <iostream>
#include "simulation.h"
#include "randomNumberGenerator.h"
using namespace std;
//Kevin Tram , 5459854, assignment 1 

int main(int argc, char** argv) {
	
	int seed1;
	int seed2;
	int seed3;
	
	simulation game;
	Random generator;
	//initialize seeds for the number generator
	cout<<"Enter a value for seed1:";
	cin>>seed1;
	cout<<"\n";
	cout<<"Enter a value for seed2:";
	cin>>seed2;
	cout<<"\n";
	cout<<"Enter a value for seed3:";
	cin>>seed3;
	cout<<"\n";
	//initialize seed values 
	generator.init(seed1,seed2,seed3);
	
	// 20x20 array, if random number is less than 0.6, then dead. Otherwise alive
	for(int i=0;i<20;i++){
		for(int j=0;j<20;j++){
		
				double random;
				random = generator.unif();
				//dead
				if(random<0.6){
					game.setCell(i,j,' ');
				}
				else{
					game.setCell(i,j,'X');//alive
				} 	
		}
	}
	cout<<"initialized board with seeds "<<seed1<<","<<seed2<<","<<seed3<<"\n";
	//display initial board
	game.print();
	int countGeneration=1;
	//while loop to keep looping for new generations as long as 1 is entered
	while(true){
		
		char nextOrExit;
		//array to be copied to determine next generation
		char tempArrayForCopy[20][20];
		
		cout<<"This is generation: "<<countGeneration<<"\n";
		cout<<"\n"<<"To display next generation, enter 1. To exit, enter anything else"<<"\n";
		cin>>nextOrExit;
		if(nextOrExit=='1'){
			countGeneration++;
			//nested loop to check every cell in the array
			for(int i=0;i<20;i++){
				for(int j=0;j<20;j++){
					//if cell is dead
					if(game.checkDeath(i,j)==true){
						//check to see if the dead cell will be alive in next generation
						if(game.checkBirth(i,j)==true){
							tempArrayForCopy[i][j]='X';
						}
						else{
							tempArrayForCopy[i][j]=' ';
						}
					}
					//if cell is alive
					else if(game.checkDeath(i,j)==false){
						//check to see if cell will survive the next generation
						if(game.checkSurvive(i,j)==true){
							tempArrayForCopy[i][j]='X';
						}
						else{
							tempArrayForCopy[i][j]=' ';
						}
					}
				}
			}
			//copy the next generation into the simulation object
			game.copyArray(tempArrayForCopy);
			cout<<"New generation:"<<"\n";
			//print the new generation after it has been copied 
			game.print();
		}
		//end the while loop if anything other than a '1' has been entered
		else{
			cout<<"\n"<<"""Goodbye!";
			break;
		}
		
	}
	
}
