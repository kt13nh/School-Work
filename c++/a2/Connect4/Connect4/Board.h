/*
November 9, 2017
Kevin Tram, 5459854

Revision 10

This is the header file for the Board class.
*/
#pragma once
#include <thread>
#include "stdafx.h"
#include <iostream>
/*
This board class will be used as an object by both the main thread and the 
AI thread. The purpose of this class is to provide the board with utility functions
to evaluate different aspects of the board, and perform certain manipulations.
This class also includes initializing the board to be played on, as well as
printing the board to the display.
*/
class Board {

private:
	
public:
	char board[8][8];
	int  countNeighbors(int column,char playerPiece);
	void initBoard();
	void print();
	void dropToken(int column,char playerPiece);
	void tetrisDrop();
	bool checkTetrisDrop();
	bool evaluateBoard(char piece);
	bool fullColumn(int column);
	int getColumnVal(char column);
	char numberToCharColumn(int column);
	bool specialCase(int column, char playerPiece);
	bool checkEast(int row, int column, char playerPiece);
	bool checkWest(int row, int column, char playerPiece);
	bool checkSouth(int row, int column, char playerPiece);
	bool checkLeftDiagonal(int row, int column, char playerPiece);
	bool checkRightDiagonal(int row, int column, char playerPiece);
	bool isPiece(int row, int column, char playerPiece);
	bool isOpenPosition(int row, int column);

};