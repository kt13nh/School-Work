#include <iostream>
#include <stdlib.h>
#include <math.h>
//Kevin Tram , 5459854, assignment 1 

class Random{
	private:
		int seed1;
		int seed2;
		int seed3;
	public:
		double unif();
		void init(int s1,int s2,int s3);
};



