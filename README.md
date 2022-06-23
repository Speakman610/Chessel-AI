# Chessel AI and Deep Reinforcement Learning

### Learning Outcomes
- Understand and know how to implement deep reinforcement learning algorithms based on the RL Course by David Silver and the accompanying book
- Gain experience creating a chess ai that uses reinforcement learning to decide which moves to make (Similar to AlphaZero)
- Use good software design practices in order to make the code easily maintainable and well organized
- Apply test driven development to create the different parts of the ai

#### Resources for Learning
- RL Course by David Silver
- Reinforcement Learning An Introduction: 2nd Edition | Richard S. Sutton and Andrew G. Barto

## Grading and Reporting

The end of the semester grade will be based on the number of hours put into the project each week and on the demonstration/showcase of the project at the end of the semester. 

Each week, the plan is to work at minimum 12 hours each week on the project. A weekly grade will be given based on the number of hours worked. If the student worked 11 hours that week, a weekly grade of 11/12 would be given. This will repeat each week, and the summed up scores on these weekly reports will determine the final grade for the course. Any hours beyond 12 hours for the week will not be counted as extra credit.

Time for the project will be spent studying the material from the RL Course and book, developing and writing tests for the chess ai, creating the ai itself, meeting with the mentor for the project, and training the neural network.

Each week, the student and mentor will meet on Friday as an opportunity to discuss the project and ask questions. Each Saturday night before 11:59 pm a report will be due summarizing the number of hours worked that week. At the end of the semester, another final report will be submitted containing all of the weekly hours (for ease of grading and a confirmation of hours worked).

Additionally, at the end of the semester the student and mentor will meet one final time for the student to showcase the project and discuss all that was learned from the semester. 

## Chessel AI 
The “main brain” of Chessel is a facade class for the user to interact with that takes chess moves as input and outputs a move in response, playing a game of chess with the user. It does this using the following underlying classes/packages (each with their own underlying classes):
- Board State (BS) keeps track of what the current board looks like
- Long Term Memory (LTM) is a Monte Carlo Search Tree that keeps track of the win rate of each move 
- Reward Network (RN) is a neural network that calculates potential reward from a given board state and move
- Thought Processor (TP) gets the current board state from BS and uses LTM to determine which move branches to focus on and calculates the best move to make using RN recursively

## How It Works

When Chessel plays a game of chess, it will take input from the terminal in the form of a chess move notation (i.e. Bf7 or Nxa5) and parses the move using its Board State instance. Chessel will then update the Long Term Memory search tree and generate all of the possible moves that Chessel could make from the current board state. From this list of moves, Chessel will look at its Long Term Memory tree and pick the top ten moves with the highest win rates and pass them to the Thought Processor for reward estimation. The Thought Processor will call the Reward Network on each of the given moves and determine which move has the best reward potential both in the short term and the long term, and will save the two reward values of the chosen move out for back-propagation later. Next, Chessel will output the chosen move to the terminal and update its Board State to reflect the given move. Finally, once the player inputs their next move, Chessel will update its Board State once again, calculate the reward it gained (see paragraph below), and back-propagate the Reward Network to improve its decision making. 

Chessel using a Deep Reinforcement Learning algorithm in order to improve the decisions it makes in a given game. The image below shows this algorithm in a simple form. An agent takes action on an environment, and the environment then returns the state of the environment and any reward to the agent. This happens over and over again until the end of the program. In our AI, Chessel acts as the agent and the chess board is the environment. When Chessel captures an enemy piece or gets a checkmate, Chessel is given a positive reward, but when Chessel loses one of its own pieces or gets put in checkmate, Chessel is given a negative reward. Chessel then back-propagates the Reward Network with the actual reward it was given compared to the estimated reward that the Reward Network expected. This is done in a short term manner, but the Reward Network also calculates for future moves (estimating potential reward five moves ahead), and will back-propagate that reward value every five turns instead of every turn.

To understand all of this more in depth, the following sections are given below.

## Board State (BS)

### Game Logic Class:
- char turn tracker
- int turn count
- boolean check
- boolean checkmate
- checkmate verify method
- move parser method

### Chess Board Class:
- map<string, piece[]> piece notation, piece[]
- map<string, string> position, piece notation

### Chess Piece Abstract Class:
- char color/team
- int position x
- int position y
- string piece notation
- abstract getCurrentlyAttacking method
- abstract getPossibleMoves method

### King Piece (extends Piece class)
- constructor { piece notation = K }
- boolean hasMoved
- boolean inCheck

### Queen Piece (extends Piece class)
- constructor { piece notation = Q )

### Rook Piece (extends Piece class)
- constructor { piece notation = R }
- boolean hasMoved

### Bishop Piece (extends Piece class)
- constructor { piece notation = B }

### Knight Piece (extends Piece class)
- constructor { piece notation = N }

### Pawn Piece (implements Interface)
- constructor { piece notation = P } // This is not the official notation for a pawn, just what the program will use internally
- boolean hasMoved

## Long Term Memory (LTM)
- Monte Carlo Search Tree
- Node Structure
    - List of child nodes
    - Number of times won
    - Number of times chosen
    - Color
    - Move notation
- Back-propagate after someone wins
    - Update # of times won (i.e. if black won then all black nodes get + 1)
    - Update # of times chosen 

## Reward Network (RN)
- 68 input nodes
    - 64 of the nodes represent a specific piece which contains its location on either the x or y coordinate (a1 = (1, 1), d5 = (4, 5), h8 = (8, 8), etc.) and if both of the position nodes are 0, 0 then the piece is no longer on the board
    - 1 node is whose turn it is (1 is white, -1 is black)
    - 1 node represents the piece Chessel wants to move (numbers 1-16, black is negative, white is positive)
    - 2 nodes representing where it wants to move it as x and y coordinates (d5 = (4, 5))
- 2 output nodes
    - Estimated immediate reward received from making the input move
    - Estimated long term reward received from making the input move

## Thought Processor (TP)

After using LTM to pick the ten best moves based on the current state and the list of possible moves, use the following algorithm to decide which of the ten is the best. If there are less than ten possible moves, do this for each possible move.

### Algorithm for Finding the Best Move

The following algorithm will not think ahead programmatically, but will instead rely on RN to estimate the future value of a given move

// Written in pseudo-code
Q(boardState, move)
    estimate the immediate reward and the future reward of a move
    save the reward values out for back-propagation
    at the end return the immediate reward value + the future reward value

In the function that Q was originally called from (in the Thought Processor), pick the move that was passed in that has the highest reward value returned.

## Additional Functionality

Print
If the user wishes to print the current state of the board, Chessel will also have a print function that prints to the terminal in a fashion like the one below:

            ----==| CHESSEL |==----
  
        |---|---|---|---|---|---|---|---|
      8 | r ||n|| b ||q|| k ||b|| n ||r||
        |---|---|---|---|---|---|---|---|
      7 ||p|| p ||p|| p ||p|| p ||p|| p |
        |---|---|---|---|---|---|---|---|
      6 |   |||||   |||||   |||||   |||||
        |---|---|---|---|---|---|---|---|
      5 |||||   |||||   |||||   |||||   |
        |---|---|---|---|---|---|---|---|
      4 |   |||||   |||||   |||||   |||||
        |---|---|---|---|---|---|---|---|
      3 |||||   |||||   |||||   |||||   |
        |---|---|---|---|---|---|---|---|
      2 | P ||P|| P ||P|| P ||P|| P ||P||
        |---|---|---|---|---|---|---|---|
      1 ||R|| N ||B|| Q ||K|| B ||N|| R |
        |---|---|---|---|---|---|---|---|
          a   b   c   d   e   f   g   h