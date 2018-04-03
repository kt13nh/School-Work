#include <iostream>
#include <math.h>
#include "simulation.h"
#include "randomNumberGenerator.cpp"
using namespace std;
//Kevin Tram , 5459854, assignment 1 

bool simulation::checkBirth(int row, int column){
	/*check if cell is dead. if cell is dead, check if there are 
	exactly 3 neighbors. Return true if cell will come alive next generation
	else return false
	*/
	if(checkDeath(row,column)==true){
		if(checkNeighbors(row,column)==3){
			return true;
		}
	}
	return false;
}

int simulation::checkNeighbors(int row, int column){
	int countNeighbors=0;
	/*check number of alive neighbors, if 3 neighbors 
	*/
	try{
	
		//check north west diagonal
		if(this->dish[row-1][column-1]=='X')
			countNeighbors++;
		//check north 	
		if(this->dish[row-1][column]=='X')
			countNeighbors++;
		//check north east diagonal	
		if(this->dish[row-1][column+1]=='X')
			countNeighbors++;
		//check east
		if(this->dish[row][column+1]=='X')
			countNeighbors++;
		//check south east	
		if(this->dish[row+1][column+1]=='X')
			countNeighbors++;
		//check south
		if(this->dish[row+1][column]=='X')
			countNeighbors++;
		//check south west
		if(this->dish[row+1][column-1]=='X')
			countNeighbors++;
		//check west
		if(this->dish[row][column-1]=='X')
			countNeighbors++;
			
			
	}
	catch(int e){
		throw;	
	}
	return countNeighbors;
}
bool simulation::checkDeath(int row, int column){
	//return true if dead, false if alive
	if(this->dish[row][column]=='X')
		return false;
	else return true;
	
}
bool simulation::checkSurvive(int row, int column){
	/*
	check number of neighbors of cell. If there are 2 or 
	3 neighbors to the cell, then return true, cell will survive.
	Otherwise return false, the cell will die.
	*/
	int numNeighbors = checkNeighbors(row,column);
	
	if(checkDeath(row,column)==false){
		if(numNeighbors==2||numNeighbors==3)
			return true;
	}
	return false;
}

void simulation::setCell(int row, int column, char status){
	/*
	set the status of the cell of the array to either ' ' for dead 
	and 'X' for alive 
	*/
	this->dish[row][column]=status;
	
}

void simulation::print(){
	//nested loop to print the array 
	cout<<"\n";	
	for(int i=0;i<20;i++){
			for(int j=0;j<20;j++){
				cout<<this->dish[i][j];
			}
			cout<<"\n";	
	}
	
}

void simulation::copyArray(char array[][20]){
	/*
	copy the parameter array to the dish array for the next generation
	*/
	for(int i=0;i<20;i++){
		for(int j=0;j<20;j++){
			this->dish[i][j] = array[i][j];
		}
	}
}

