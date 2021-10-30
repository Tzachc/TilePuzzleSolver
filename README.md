# TilePuzzleSolver
The program solve NxN tile puzzle with 1 blank OR 2 blanks.

This program is solving Tile puzzle with search algorithms such: BFS, DFID, Astar, IDAstar, DFBnB.

### Tile Puzzle:

![alt text](https://github.com/Tzachc/TilePuzzleSolver/blob/main/data/tilepuzzle.png)

Example of tile puzzle board with 1 blank.\
The puzzle can be recevied in any random combination \
the objective is to reach the goal state with a minimum number of steps.

The program can also solve tile puzzle with 2 blanks blocks\

each move will cost differently:
Single block move, cost: 5. (It dosent matter which direction). 

2 Block at the same time:
UP || DOWN cost = 7 
LEFT || RIGHT cost = 6

### Input and outputs explantion:

**[ each input file should have these attributes: ]**
* On the first line you will choose your desired algorithm: { BFS, DFID, IDA*, A*, DFBnB }
* On the second line you will choose if you want the output to print the time taken {with time/no time}.
* On the third line you will choose if you want the output to print current state of the board-{with open/no open}.
* On the Forth line is the size of the board (size of the matrix NxN).
* Initial state board.
* Your goal board.

***Example***

![alt text](https://github.com/Tzachc/TilePuzzleSolver/blob/main/data/fileInput.png)

***Output example:***
![alt text](https://github.com/Tzachc/TilePuzzleSolver/blob/main/data/output.png)


