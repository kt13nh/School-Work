#include <iostream>
#include <math.h>
#include "pairing.h"
#include <stack>
//Kevin Tram , 5459854, assignment 1 
using namespace std;

//initialize the array to all false
void pairing::init(){
	for(int i=0;i<30000;i++){
		this->primes[i]=false;
	}
}

void pairing::sieveOfErathostenes(){
	//stack to record all of the primes 
	stack<int> stack;
	
	for(int i=2;i<29999;i++){
		if(this->primes[i]==false){
			cout<<i<<" ";
			stack.push(i);//push to stack if it is false to record
			numPrimes++;//count the number of primes
			increment(i);//increment the factor to make the multiple marked on the array
		}
	}

	int size = stack.size();//record the size of the stack
	int primePairs[size];//initialize new prime array to display pairs from stack
	int t = stack.top();//reference top of stack for the largest prime
	for(int i=0;i<size;i++){
		int temp=stack.top();//temporary reference to top of the stack 
		primePairs[i]=temp;//assign array index to the temporary reference to store the prime
		numPrimes++;//count number of primes
		stack.pop();//pop the prime value since it is already copied to the array
	}
	cout<<"\n";
	//loop the array that transfered from the stack from the farthest index to get the smallest primes
	for(int i=size-1;i>0;i--){
		for(int j=size-1;j>0;j--){
			if(primePairs[i]-primePairs[j]==2){//Pair the primes if the subtraction is equal to 2
				cout<<"("<<primePairs[i]<<","<<primePairs[j]<<")";//print the pair if difference is 2
			}
		}
	}
}

void pairing::increment(int factor){
	int fac = factor;//temporary reference to the parameter to increment by that multiple
		while(fac<30000){//array dimension
			this->primes[fac]=true;//assign the factor that was given to the arrays because we know not prime
			fac=fac+factor;//increment by the factor passed from parameter
		}
}


