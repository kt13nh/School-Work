#include <iostream>
#include "randomNumberGenerator.h"

using namespace std;
void Random::init(int s1,int s2,int s3){
	this->seed1=s1;
	this->seed2=s2;
	this->seed3=s3;
}
double Random::unif(){
	double tmp;
	int x=this->seed1;
	int y=this->seed2;
	int z=this->seed3;
	this->seed1 = 171*(this->seed1%177) - 2*(this->seed1/177);
	if (this->seed1<0){
		this->seed1 = this->seed1 + 30269;
	}
			
	this->seed2 = 172*(this->seed2%176) -35*(this->seed2/176);
	if (this->seed2<0) {
		this->seed2 = this->seed2 + 30307;
	}
			 
	this->seed3= 170*(this->seed3%178) -63*(this->seed3/178);
	if (this->seed3<0){
		this->seed3 = this->seed3 + 30323;
	}
	tmp=this->seed1/30269.0 + this->seed2/30307.0 + this->seed3/30323.0;
	tmp = tmp - trunc(tmp);
	return tmp;
}




