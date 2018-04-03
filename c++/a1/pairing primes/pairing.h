#include <iostream>
#include <math.h>
//Kevin Tram , 5459854, assignment 1 
class pairing{
	private:
		int numPrimes;
		bool primes[30000];
		
		
	public:
		void sieveOfErathostenes();
		void init();
		void increment(int factor);
		void copyArray();
};


