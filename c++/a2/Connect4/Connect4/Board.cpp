/*
November 9, 2017
Kevin Tram, 5459854

Revision 10
*/
#include "stdafx.h"
#include <iostream>
#include <thread>
#include "Board.h"
#include <mutex>
using namespace std;

/*
This method will drop a player token into the specified 
column, with the specified player's piece. This will be 
achieved by searching the board array for empty spaces,
once it encounters an empty space, then it will make the
previous row in that column a piece.
*/
void Board::dropToken(int column, char playerPiece) {
	int row;
	for (int i = 0; i < 8; i++) {
		if (this->board[i][column] != '.') {
			row = i - 1;
			this->board[row][column] = playerPiece;
			break;
		}
		else if (i == 7 && this->board[i][column] == '.') {
			this->board[i][column] = playerPiece;
		}
	}
	
}

/*
This method will check to see if a column is full.
The boolean returned will be true if it is full,
otherwise it will return false.
*/
bool Board::fullColumn(int column) {
	/**
	if the top spot in the column is not empty,
	means it is then full
	**/
	if (this->board[0][column] != '.'){
		return true;
	}
	else return false; 
}

/*
This method will initialize the board to 
'.' and those will represent blank values
*/
void Board::initBoard() {
	for (int i = 0; i < 8; i++) {
		for (int j = 0; j < 8; j++) {
			this->board[i][j] = '.';
		}
	}
}

/*
Simple method to print the current state of the 
board, where "|" is used for the sides of the board
*/
void Board::print() {
	//print the board
	cout << "    ";
	for (int i = 0; i < 8; i++) {
		cout << "_" << " ";
	}
	cout << "\n";
	int num = 7;
	for (int i = 0; i < 8; i++) {
		cout <<num<<" |"<<" ";
		for (int j = 0; j < 8; j++) {
			cout << this->board[i][j]<<" ";
		}
		cout<< "|";
		cout << "\n";
		num--;
	}
	cout << "    ";
	for (int i = 0; i < 8; i++) {
		cout << "-"<< " ";
	}
	cout << "\n"<<"    ";
	//print the row labels
	for (int i = 0; i < 8; i++) {
		cout << numberToCharColumn(i) << " ";
	}
	cout << "\n";
}

/*
Simple function to convert an integer value to
a char. This will be used for the displaying
of the AI's move.
*/
char Board::numberToCharColumn(int column) {
	if (column == 0)
		return 'A';
	else if (column == 1)
		return 'B';
	else if (column == 2)
		return 'C';
	else if (column == 3)
		return 'D';
	else if (column == 4)
		return 'E';
	else if (column == 5)
		return 'F';
	else if (column == 6)
		return 'G';
	else if (column == 7)
		return 'H';
}

/*
This simple function will return an integer
value based on what char column is inputed.
*/
int Board::getColumnVal(char column) {
	if (column == 'a' || column == 'A')
		return 0;
	else if (column == 'b' || column == 'B')
		return 1;
	else if (column == 'c' || column == 'C')
		return 2;
	else if (column == 'd' || column == 'D')
		return 3;
	else if (column == 'e' || column == 'E')
		return 4;
	else if (column == 'f' || column == 'F')
		return 5;
	else if (column == 'g' || column == 'G')
		return 6;
	else if (column == 'h' || column == 'H')
		return 7;
}

/*
This function will mimic the "tetris effect" where every piece on
the bottom will disappear and all of the pieces on the board will
shift down once. This is achieved by taking the previous row's values
and inputing them into the current row, starting from the bottom of
the board array.
*/
void Board::tetrisDrop() {
	for (int i = 7; i >= 0; i--) {
		for (int j = 0; j < 8; j++) {
			if (i == 0) {
				this->board[i][j] = '.';
			}
			else {
				this->board[i][j] = this->board[i - 1][j];
			}
		}
	}
}

/*
This method will return a boolean value based on the very 
bottom row as specified in the assignment. If the row is filled
with all of the AI's pieces and the player's pieces then the function
will return true, otherwise it will return false.
*/
bool Board::checkTetrisDrop() {
	int bottomRowCount = 0;
	//check to see if bottom row filled
	for (int i = 0; i < 8; i++) {
		//if not filled then end the loop
		if (this->board[7][i] != '.') {
			bottomRowCount++;
			if (bottomRowCount == 8)
				return true;
		}
	}
}
/*
This is a special case based on simple connect 4 strategy that the AI will take
into consideration. The function will check when the player has 2 adjacent pieces
on the bottom row, with one open space to the left of the 2 adjacent pieces, and 
2 empty spaces to the right of the 2 adjacent pieces. This will prevent the user
from winning by simply entering sequence 1,2,3,4. 
*/
bool Board::specialCase(int column, char playerPiece) {
	if (column > 0 && column < 5) {
		if (isPiece(7, column, playerPiece) == true) {
			if (isOpenPosition(7, column - 1) == true && checkEast(7, column, playerPiece) == true
				&& isOpenPosition(7, column + 2) == true && isOpenPosition(7, column + 3) == true) {
				return true;
			}
		}
	}
	else return false;
}

/*
This method is used to count the number of adjacent friendly pieces there
are on the board, return the number after the function is complete.
Checking functions will be used, if they return true then neighbors
will be incremented. Number of neighbors will then be returned
as an integer value.
*/
int Board::countNeighbors(int column,char playerPiece) {
	//variable to count neighbors
	int neighbors=0;
	//check where the last token landed after it was dropped
	//given the column and which player the piece belonged to
	int row=0;
	for (int i = 0; i < 8; i++) {
		if (isPiece(i,column,playerPiece)==true) {
			row = i;
			break;
		}
	}
	//make sure to check in bounds
	//this will make sure that if in the first row, we account for the bounds
	if (row == 0) {
		if (checkSouth(row, column, playerPiece) == true)
			neighbors++;
		if (column == 0) {
			//check right side diagonal wrapped around cylinder
			if (checkRightDiagonal(row, column, playerPiece) == true)
				neighbors++;
			if (checkEast(row, column, playerPiece) == true)
				neighbors++;
			//check the left side diagonal wrapped around cylinder
			if (isPiece(row+1, 7, playerPiece) == true)
				neighbors++;
			//check the west wrapped around cylinder
			if (isPiece(row, 7, playerPiece) == true)
				neighbors++;
		}
		else if (column == 7) {
			if (checkWest(row, column, playerPiece) == true)
				neighbors++;
			if (checkLeftDiagonal(row, column, playerPiece) == true)
				neighbors++;
			if (isPiece(row + 1, 0, playerPiece) == true)
				neighbors++;
			if (isPiece(row, 0, playerPiece) == true)
				neighbors++;
		}
		else {
			if (checkWest(row, column, playerPiece) == true)
				neighbors++;
			if (checkEast(row, column, playerPiece) == true)
				neighbors++;
			if (checkLeftDiagonal(row, column, playerPiece) == true)
				neighbors++;
			if (checkRightDiagonal(row, column, playerPiece) == true)
				neighbors++;
		}
	}
	//if bottom row, no need to check north
	else if (row == 7) {
		if (column == 0) {
			//check north east
			if (isPiece(row - 1, column + 1, playerPiece) == true)
				neighbors++;
			if (checkEast(row, column + 1, playerPiece) == true)
				neighbors++;
			//north west, must wrap around cylinder
			if (isPiece(row - 1, 7, playerPiece) == true)
				neighbors++;
			//west, must wrap around cylinder
			if (isPiece(row, 7, playerPiece) == true)
				neighbors++;
		}
		else if (column == 7) {
			if (checkWest(row, column, playerPiece) == true)
				neighbors++;
			//north west
			if (isPiece(row - 1, column - 1, playerPiece) == true)
				neighbors++;
			//check north east, must wrap around cylinder
			if (isPiece(row - 1, 0, playerPiece) == true)
				neighbors++;
			//check east, must wrap around cylinder
			if (isPiece(row, 0, playerPiece) == true)
				neighbors++;
		}
		else {
			if (checkWest(row, column, playerPiece) == true)
				neighbors++;
			if (checkEast(row, column, playerPiece) == true)
				neighbors++;
			//north east
			if (isPiece(row + 1, column + 1, playerPiece) == true)
				neighbors++;
			//north west
			if (isPiece(row - 1, column - 1, playerPiece) == true)
				neighbors++;
		}
	}
	//if row is neither the first or last
	else {
		//can always check the south position because the row is never the bottom here
		if (checkSouth(row, column, playerPiece) == true)
			neighbors++;
		if (column == 0) {
			if (checkEast(row, column, playerPiece) == true)
				neighbors++;
			if (checkRightDiagonal(row, column, playerPiece) == true)
				neighbors++;
			//check the south west, must wrap around cylinder
			if (isPiece(row + 1, 7, playerPiece) == true)
				neighbors++;
			//check west, must wrap around cylinder
			if (isPiece(row, 7, playerPiece) == true)
				neighbors++;
			//check north West, must wrap around cylinder
			if (isPiece(row - 1, 7, playerPiece) == true)
				neighbors++;
			//check north east
			if (isPiece(row - 1, column + 1, playerPiece) == true)
				neighbors++;
		}
		else if (column == 7){
			if (checkWest(row, column, playerPiece) == true)
				neighbors++;
			if (checkLeftDiagonal(row, column, playerPiece) == true)
				neighbors++;
			//check south east, wrap around cylinder
			if (isPiece(row + 1, 0, playerPiece) == true)
				neighbors++;
			//check east, must wrap around cylinder
			if (isPiece(row, 0, playerPiece) == true)
				neighbors++;
			//check north east, must wrap around cylinder
			if (isPiece(row - 1, 0, playerPiece) == true)
				neighbors++;
			//check north west
			if (isPiece(row - 1, column - 1, playerPiece) == true)
				neighbors++;
		}
		//if neither the first or last row, and neither the side columns, then check normally
		else {
			if (checkWest(row, column, playerPiece) == true)
				neighbors++;
			if (checkEast(row, column, playerPiece) == true)
				neighbors++;
			if (checkLeftDiagonal(row, column, playerPiece) == true)
				neighbors++;
			if (checkRightDiagonal(row, column, playerPiece) == true)
				neighbors++;
			//check north east
			if (isPiece(row - 1, column + 1, playerPiece) == true)
				neighbors++;
			//check north west
			if (isPiece(row - 1, column - 1, playerPiece) == true)
				neighbors++;
		}
	}
	return neighbors;
}

/**
This boolean function checks to see if it is the player's or ai's piece. 
function returns true if it is the player's piece passed through the parameter,
otherwise return false
**/
bool Board::isPiece(int row, int column,char playerPiece) {
	//return true if it is the playerPiece parameter, otherwise false
	if (this->board[row][column] == playerPiece)
		return true;
	else
		return false;
}

/**
This boolean function checks to see if a specified position is empty.
If the position is not filled by a player piece, then it will return true,
otherwise it will return false.
**/
bool Board::isOpenPosition(int row, int column) {
	if (this->board[row][column] == '.')
		return true;
	else return false;
}

/*
This boolean function will check to see if there is a friendly 
piece south of the player piece. If there is, then return true,
false otherwise
*/
bool Board::checkSouth(int row,int column, char playerPiece) {
	if (this->board[row + 1][column] == playerPiece)
		return true;
	else return false;
}
/*
This boolean function will check to see if there is a friendly
piece west of the player. If there is, then return true, false
otherwise
*/
bool Board::checkWest(int row, int column, char playerPiece) {
	if (this->board[row][column - 1] == playerPiece)
		return true;
	else return false;
}
/*
This boolean function will check to see if there is a friendly
piece east of the player. If there is, then return true, false
otherwise
*/
bool Board::checkEast(int row, int column, char playerPiece) {
	if (this->board[row][column + 1] == playerPiece)
		return true;
	else return false;
}
/*
This boolean function will check to see if there is a friendly
piece south west of the player. If there is, return true,
false otherwise
*/
bool Board::checkLeftDiagonal(int row, int column, char playerPiece) {
	if (this->board[row + 1][column - 1] == playerPiece)
		return true;
	else return false;
}

/*
This boolean function will check to see if there is a friendly
piece south east of the player. If there is, return true,
false otherwise
*/
bool Board::checkRightDiagonal(int row, int column, char playerPiece) {
	if (this->board[row + 1][column + 1] == playerPiece)
		return true;
	else return false;
}

/*
This function will evaluate the whole board, given a player piece to see if the
player is victorious in this situation. If there is a victory from the player
piece, then the function will return boolean = true, otherwise it will return
false if there is no victory. This will include checking the adjacent sides of the
cylinder in all cases. The function will achieve this by always making sure to be
checking in bounds of the array. If it is ever at the end of the array bounds, 
then it will then check the adjacent sides of the cylinders. This is all done
iteratively.
*/
bool Board::evaluateBoard(char playerPiece) {
	int count = 0;
	for (int i = 0; i < 8; i++) {
		for (int j = 0; j < 8; j++) {
			if (this->board[i][j] == playerPiece) {
				count++;
				//vertical
				if (checkSouth(i, j, playerPiece) == true) {
					count++;
					//make sure to not check out of bounds
					if (!(i + 2 > 7)) {
						for (int z = 1; z <= 2; z++) {
							if (checkSouth(i + z, j, playerPiece) == true) {
								count++;
							}
							if (count == 4) {
								//for debugging purposes
								//cout << "Win by vertical" << "\n";
								return true;
							}
						}
					}
					/**
					no solution yet, reset the count back to 1
					and continue to check the rest of the positions
					**/
					count = 1;
				}
				
				//horizontal

				//if starting from the left-most side, check
				//the opposite side of the cylinder
				if (j == 0) {
					if (checkEast(i,j,playerPiece) == true) {
						count++;
						for (int z = 1; z <= 2; z++) {
							if (checkEast(i, j + z, playerPiece) == true) {
								count++;
								if (count == 4) {
									//for debugging purposes
									//cout << "Win by horizontal!"<<"\n";
									return true;
								}
							}
							//break since tokens must be in line 
							else {
								break;
							}
						}
					}
					
					if (isPiece(i, 7, playerPiece) == true) {
						
						count++;
						//cout << count;
						for (int z = 0; z <= 1; z++) {
							if (checkWest(i, 7 - z, playerPiece) == true) {
								count++;
								//cout << count;
								if (count == 4){
									//for debugging purposes
									//cout << "Win by horizontal!" << "\n";
									return true;
								}
							}
							//break since tokens must be in line
							else {
								break;
							}
						}
					}
					//reset the count to 1 since there was no solution found yet 
					count = 1;
				}
				//if starting from the right-most side, check
				//the opposite side of the cylinder
				
				else if (j == 7) {
					if (checkWest(i, j, playerPiece) == true) {
						count++;
						//cout << count;
						for (int z = 1; z <= 2; z++) {
							if (checkWest(i, j - z, playerPiece) == true) {
								count++;
								if (count == 4) {
									//for debugging purposes
									//cout << "Win by right most cylinder!" << "\n";
									return true;
								}
							}
							//break here since token must be in line
							else {
								break;
							}
							
						}
					}
					if (isPiece(i, 0, playerPiece)==true) {
						count++;
						for (int z = 0; z <= 1; z++) {
							if (checkEast(i, z, playerPiece) == true) {
								count++;
								if (count == 4) {
									//for debugging purposes
									//cout << "Win by rightmost cylinder 2!" << "\n";
									return true;
								}
							}
							//break here since the tokens must be lined up
							else {
								break;
							}
						}
					}
					//reset the count to 1 since there was no solution
					count = 1;
				}
				//just check the whole row now 
				else {
					//check the left of the piece as long as it is still in bounds
					for (int z = j; z < 0; z--) {
						if (checkWest(i, z, playerPiece) == true) {
							count++;
							if (count == 4) {
								//for debugging purposes
								//cout << "Win by horizontal 3!" << "\n";
								return true;
							}
						}
						//no adjacent token to the left
						else {
							break; 
						}
					}
					//check the right of the piece as long as it is still in bounds
					for (int z = j; z < 7; z++) {
						if (checkEast(i, z, playerPiece) == true) {
							count++;
							if (count == 4) {
								//for debugging purposes
								//cout << "Win by horizontal 4!" << "\n";
								return true;
							}
						}
						//no adjacent token to the right
						else {
							break;
						}
					}
					//no solution yet, reset the count back to 1
					count = 1;
				}
				//left diagonal
				int diagonalRow=i;
				int diagonalColumn=j;
				while (diagonalColumn>0 && diagonalRow<7) {
					
					//go left diagonal as long as it is in bounds
					if (checkLeftDiagonal(diagonalRow, diagonalColumn, playerPiece) == true) {
						//cout << count<<"1     ";
						count++;
						if (count == 4) {
							//for debugging purposes
							//cout << "Win by left diagonal!" << "\n";
							return true;
						}
					}
					else {
						break;
					}
					diagonalRow++;
					diagonalColumn--;
					
				}
				//wrap around the cylinder
				int wrapColumn = diagonalColumn;
				int wrapRow = diagonalRow+1;
				if (diagonalRow != 7 && wrapColumn==0) {
					//go to the adjacent side of the cylinder
					wrapColumn = 7;
					//continue searching the adjacent diagonal only if the adjacent
					//diagonal is the player's piece
					if (isPiece(wrapRow, wrapColumn, playerPiece) == true) {
						count++;
						while (wrapRow < 7) {
							
							if (checkLeftDiagonal(wrapRow, wrapColumn, playerPiece)) {
								count++;
								if (count == 4) {
									//for debugging purposes
									//cout << "Win by wrapped left diagonal!" << "\n";
									return true;
								}
							}
							else {
								break;
							}
							//cout << count;
							wrapRow++;
							wrapColumn--;

						}
					}
				}
				//reset back to 1 since no solution found even after wrapped
				//around the cylinder
				count = 1;

				//right diagonal
				int diagonalRow2 = i;
				int diagonalColumn2 = j;
				while (diagonalColumn2<7 && diagonalRow2<7) {
					//go right diagonal as long as it is in bounds
					if (checkRightDiagonal(diagonalRow2, diagonalColumn2, playerPiece) == true) {
						count++;
						if (count == 4) {
							//for debugging purposes
							//cout << "Win by right diagonal!" << "\n";
							return true;
						}
					}
					else {
						break;
					}
					diagonalRow2++; diagonalColumn2++;
				}
				//wrap around the cylinder
				int wrapColumn2;
				int wrapRow2 = diagonalRow2+1;
				if (diagonalRow2 != 7 && diagonalColumn2 == 7) {
					//go to the adjacent side of the cylinder
					wrapColumn2 = 0;
					//continue search adjacent side of 
					//diagonal only if it is the player's piece
					if (isPiece(wrapRow2, wrapColumn2, playerPiece) == true) {
						count++;
						if (count == 4)
							return true;
						while (diagonalRow2 < 7) {
							if (checkRightDiagonal(wrapRow2, wrapColumn2, playerPiece)) {
								count++;
								if (count == 4) {
									//for debugging purposes
									//cout << "Win by wrapped right diagonal!";
									return true;
								}
							}
							else {
								break;
							}
							wrapRow2++; wrapColumn2++;
						}
					}
				}
				//reset back to 1 since no solution found even after wrapped
				//around the cylinder
				count = 1;
			}
			count = 0;
		}
	}
}
