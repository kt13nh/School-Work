#include <iostream>
#include <math.h>
//Kevin Tram , 5459854, assignment 1 

class simulation{
	private:
		char dish[20][20];
	
	public:
		int checkNeighbors(int row,int column);//return number of neighbors
		bool checkSurvive(int row, int column);//if two or more neighbors, return true, else false
		bool checkBirth(int row,int column);//true if alive next generation, else false
		bool checkDeath(int row, int column);//return true if dead, false if alive
		void setCell(int row, int column,char status);//set living status of cell to array. ' ' for dead, 'X' for alive
		void copyArray(char array[][20]);//copy the parameter array for the next generation into dish 
		void print();//print the dish 
};


