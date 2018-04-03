#include <iostream>
#include <math.h>
#include "approximations.h"
//Kevin Tram , 5459854, assignment 1 
/*
Find the distance between the shot taken, and the centre
*/
double approximations::distance(double x,double y){
	double distance;
	double X;
	double Y;
	X = x-this->centreX;
	Y= y-this->centreY;
	distance = sqrt((X*X)+(Y*Y));
	return distance;
}

/*
initialize the centre X and Y values
*/
void approximations::init(double x, double y){
	this->centreX=x;
	this->centreY=y;
}




