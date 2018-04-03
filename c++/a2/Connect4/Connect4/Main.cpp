/*
November 9, 2017
Kevin Tram, 5459854

Revision 10
*/
#include "stdafx.h"
#include <thread>
#include "Board.h"
#include <limits>
#include <iostream>
#include <mutex>
#include <queue>
using namespace std;
//global variables to be used by the game thread and main thread
mutex m;
queue<int> playerQueue;
queue<int> aiQueue;
Board board;
int rankings[8];
bool gameOver = false;
bool running=true;
//forward declaration
void gameStart(int turn);
int getHighestRankedMove();
void thinking();
void resetRanking();
/////////////////////

/*
This function is used for the game thread which will
first determine whether the AI goes first, or the player
goes first. If the player goes first, then the game will 
wait until the player has made the move. The thread will
then accept the move and notifying the main thread to update
the display of the board. All while this is happening, the 
game thread will mimic the "thinking" process a human has, 
and thinks about the next potential move constantly while
the human is making their move. Next, the thread will then 
commit to a move, letting the main thread know that the AI
has made a move.
*/
void gameStart(int turn) {
	bool aiTurn=false;
	//determine who is to go first
	if (turn == 1) {
		aiTurn = false;
	}
	else if (turn == 2) {
		aiTurn = true;
	}
	while (running == true) {
		if (running == false)
			break;
		while(aiTurn==false){
			//think about the next move while human is making their move
			thinking();
			//accept the move of human player while staying concurrent
			m.lock();
			if(!playerQueue.empty()){
				int playerMove = playerQueue.front();
				board.dropToken(playerMove, '1');
				playerQueue.pop();
				aiTurn = true;
			}
			m.unlock();
			//end the loop of this thread
			if (running == false)
				break;
		}
		//rethink move after human made its move
		thinking();
		//final decision on the next move
		int highestRankedMove = getHighestRankedMove();
		//inform the main thread about the move
		m.lock();
		aiQueue.push(highestRankedMove);
		int aiMove = aiQueue.front();
		board.dropToken(aiMove, '2');
		resetRanking();
		m.unlock();
		aiTurn = false;
	}
}



/**
The best rankings will be the highest value in the rankings array(unsorted).
This is the thought process for before the human drops his token.
This will consider all the possible moves the robot can take and ranks them
based on:
1. If it will cause the AI to win next move
2. How many pieces the next move will be adjacent to.
**/
void thinking(){
	int neighbors=0;

	//test each coloumn of the hypothetical board and rank based on the
	//above criteria
	for (int column = 0; column < 8; column++) {
		//make a hypothetical board to check rankings
		Board testBoardForAi;
		//make hypothetical board for player's next potential move
		Board testBoardForPlayer;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
					testBoardForAi.board[i][j] = board.board[i][j];
					testBoardForPlayer.board[i][j] = board.board[i][j];
			}
		}
		//drop a token into the hypothetical boards to check
		if (board.fullColumn(column)==false) {
			testBoardForAi.dropToken(column, '2');
			testBoardForPlayer.dropToken(column, '1');
		}
		/**
		always rank a spot the highest if it will win the game
		and rank a spot the second highest if it prevents player
		from winning game. Added a special case to consider
		based on simple connect 4 strategy.
		Otherwise go with a spot to get the most amount of adjacent pieces
		**/
		/*
		This will check and make sure that if the AI's next move will result
		in a win, then it will take this move
		*/
		if (testBoardForAi.evaluateBoard('2')==true) {
			rankings[column] = 90;
		}
		/*
		This will check and make sure that if it can stop the opponent's
		next move from winning, that will be the next move if the AI 
		does not win itself.
		*/
		else if (testBoardForPlayer.evaluateBoard('1') == true) {
			rankings[column] = 89;
		}
		/*
		check to see if there is a special case, read the board.cpp
		function for more detail.
		*/
		else if (testBoardForPlayer.specialCase(column, '1')==true) {
			rankings[column - 1] = 88;
		}
		else {
			neighbors = testBoardForAi.countNeighbors(column, '2');
			/*
			This section of the code will make sure if the AI makes the
			move, if the human drops a piece in the same column as the AI,
			it will not result in a win for the human.
			*/
			if (testBoardForAi.fullColumn(column) == false) {
				testBoardForAi.dropToken(column, '1');
				if (testBoardForAi.evaluateBoard('1')) {
					rankings[column] = -1;
				}
			}
			/////////////////////////////////////////
			//otherwise just let the column equal adjacent neighbors
			else
				rankings[column] = neighbors;
		}
	}
}

/*
Simple function to reset the rankings of all of the columns
*/
void resetRanking(){
	for(int i=0;i<8;i++){
		rankings[i] = -1;
	}
}

/*
This will iterate through the global variable "rankings"
and determine which column has the most value based on 
the AI's thinking process. The function will return 
the column that has the highest priority.
*/
int getHighestRankedMove() {
	int highestIndex=0;
	int highestRank=0;
	//random variable used if there is no adjacent neighbors for all moves
	int random;
	for (int i = 0; i < 8; i++) {
		if (rankings[i] > highestRank) {
			highestRank = rankings[i];
			highestIndex = i;
		}
	}
	if (highestRank == 0) {
		random = rand() % 7;
		highestIndex = random;
	}
	return highestIndex;
}

/*
This print function just displays a simple title to the console, welcoming the user
and letting them know which piece is theirs 
*/
void printTitle() {
	cout << "\n" << "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||" << "\n";
	cout << "\n" << "|| Hello, welcome to Kevin Tram's Cylinder Connect 4 Game!! ||" << "\n";
	cout << "\n" << "|| Human Piece: 1                                           ||" << "\n";
	cout << "\n" << "|| AI    Piece: 2                                           ||" << "\n";
	cout << "\n" << "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||" << "\n";
}

/**
this main thread will intialize the board, game thread,
and provide dialogues and game interface. It will also announce
the winner as well as terminating the game. Game thread will be 
initialized here and started, communicating primarily through
the global queue, and the mutex to ensure concurrency. 
**/
int main() {
	
	bool keepPlaying = true;
	while(keepPlaying == true){
		printTitle();
		bool playerMoves = false;
		char whoGoesFirst;
		int whosGoingFirst;
		//initialize board and print 
		board.initBoard();
		board.print();
		cout <<"\n"<<"Who goes first?" << "\n";
		cout << "Enter 1 for Human move first." << "\n" << "Enter 2 for AI move first." << "\n";
		cin >> whoGoesFirst;
		while (whoGoesFirst != '1' && whoGoesFirst != '2') {
			cout << "Invalid input!"<<"\n"<<"Enter 1 for Human move first.";
			cout << "\n" << "Enter 2 for AI move first." << "\n";
			cin >> whoGoesFirst;
			cout << "\n";
			if (whoGoesFirst == '1' || whoGoesFirst == '2') {
				break;
			}
		}
		
		//initialize the game thread
		bool run = true;
		running = true;
		whosGoingFirst = whoGoesFirst - '0';//convert this value to an int
		/*
		This is the thread that is responsible for the game 
		and the AI. More indepth description is given in the
		gameStart function.
		*/
		thread game(gameStart, whosGoingFirst);
		//ai thread now running
		
		//print the empty board
		board.print();
		
		//run the main thread now
		while (run) {
			//this is for if the human player goes first
			if (whosGoingFirst == 1) {
				char c = '.';
				while (c != 'a' || c != 'b' || c != 'c' || c != 'd' || c != 'e' || c != 'f' || c != 'g' || c != 'h' ||
					c != 'A' || c != 'B' || c != 'C' || c != 'D' || c != 'E' || c != 'F' || c != 'G' || c != 'H') {
					cout << "Your move: ";
					std::cin >> c;
					cout << "\n";
					if (board.fullColumn(board.getColumnVal(c)) == false) {
						if (c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f' || c == 'g' || c == 'h' ||
							c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E' || c == 'F' || c == 'G' || c == 'H') {
							break;
						}
						else {
							cout << "Invalid input! Please enter only column letter!" << "\n";
						}
					}
					else if(board.fullColumn(board.getColumnVal(c))!=false) {
						cout << "That column is already full!" << "\n";
					}
					else {
						cout << "Invalid input! Please enter only letter!" << "\n";
					}
				}
				int columnToDropToken = board.getColumnVal(c);
				//lock mutex and send the move for the game thread to accept
				m.lock();
				//tally move and send the move to the game to accept
				playerQueue.push(columnToDropToken);
				m.unlock();
				//update the board
				board.print();
				//check to see if human player won after placing a token
				if (board.evaluateBoard('1') == true) {
					cout << "Human wins!" << "\n";
					running = false;
					break;
				}
				//update the display if there was a tetris effect
				else if (board.checkTetrisDrop() == true) {
					cout << "\n" << "Tetris Effect!" << "\n";
					board.tetrisDrop();
					board.print();
				}
				int aiMove = 0;
				//wait for the AI input 
				while (playerMoves == false) {
					//making sure we are staying concurrent
					m.lock();
					if (!aiQueue.empty()) {
						//tally the move of the AI
							aiMove = aiQueue.front();
							aiQueue.pop();
					}
					playerMoves = true;
					m.unlock();
				}
				cout <<"\n"<< "Ai Move: " << board.numberToCharColumn(aiMove) << "\n";
				//check for winner or tetris effect
				if (board.evaluateBoard('2') == true) {
					cout << "AI wins!" << "\n";
					running = false;
					break;
				}
				if (board.checkTetrisDrop() == true) {
					cout << "\n" << "Tetris Effect!" << "\n";
					board.tetrisDrop();
					board.print();
				}
				playerMoves = false;
			}
			//this is for if the ai moves first
			else if (whosGoingFirst == 2) {
				int aiMove = 0;
				//wait for the game thread to make a move
				while (playerMoves == false) {
					m.lock();
					if (!aiQueue.empty()) {
						//tally the move of the Ai
							aiMove = aiQueue.front();
							aiQueue.pop();
					}
					playerMoves = true;
					m.unlock();
				}

				cout << "\n"<<"Ai Move: " << board.numberToCharColumn(aiMove) << "\n";
				//check if the game thread AI's move won the game
				if (board.evaluateBoard('2') == true) {
					cout << "AI wins!" << "\n";
					running = false;
					break;
				}
				if (board.checkTetrisDrop() == true) {
					cout << "\n" << "Tetris Effect!" << "\n";
					board.tetrisDrop();
					board.print();
				}
				playerMoves = false;
				/**
				wait for user move now
				**/
				//column variable
				char c='.';
				while (c!='a'||c!='b'||c!='c'||c!='d'||c!='e'||c!='f'||c!='g'||c!='h'||
					c!='A'||c!='B'||c!='C'||c!='D'||c!='E'||c!='F'||c!='G'||c!='H') {
					cout << "Your move: ";
					std::cin >> c;
					cout << "\n";
					if (board.fullColumn(board.getColumnVal(c)) == false) {
						if (c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e' || c == 'f' || c == 'g' || c == 'h' ||
							c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E' || c == 'F' || c == 'G' || c == 'H') {
							break;
						}
						else {
							cout << "Invalid input! Please enter only column letter!" << "\n";
						}
					}
					else {
						cout << "Invalid input!" << "\n";
					}
				}
				int columnToDropToken = board.getColumnVal(c);
				//let the gamethread know a move was made
				m.lock();
				//tally move, let gamethread accept the move
				playerQueue.push(columnToDropToken);
				m.unlock();
				//update display of the board
				board.print();
				//check to see if the player won
				if (board.evaluateBoard('1') == true) {
					cout << "Human wins!" << "\n";
					running = false;
					break;
				}
				else if (board.checkTetrisDrop() == true) {
					cout << "\n" << "Tetris Effect!" << "\n";
					board.tetrisDrop();
					board.print();
				}
			}
		}
		//let the user have the option to play again, or quit
		cout<<"\n" << "Play Again (y/n) ?"<<"\n";
		char isContinue='.';
		while (isContinue!='y'&&isContinue!='n') {
			std::cin>>isContinue;
			if (isContinue != 'y'&&isContinue != 'n')
				cout << "Please enter y to play again, or n to quit"<<"\n";
			if (isContinue == 'n'||isContinue=='N') {
				keepPlaying = false;
				break;
			}
		}
		//reset the Queue for the AI 
		aiQueue = {};
		//join the game thread
		game.join();
		cout << "\n"<<"\n";
	}
	return 0;
}