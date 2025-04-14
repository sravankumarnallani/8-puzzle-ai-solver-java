                                                                          Artificial Intelligence
                                                                
                                                                            
Name:   Sravan Kumar Nallani
UTA ID: 1002147954

## Programming Language:
  -Language Used: Java
  -Version: Java 17 (OpenJDK 17)

## Code Structure:
  - PuzzleState Class: Represents the state of the puzzle. It contains the current configuration, depth, cost, heuristic score, and the action taken to reach the state.
  - PuzzleSolver Class: Contains the main logic for solving the 8-puzzle problem using various search methods:
    - Breadth-First Search (BFS)
    - Uniform Cost Search (UCS)
    - Depth-First Search (DFS)
    - Depth Limited Search (DLS)
    - Iterative Deepening Search (IDS)
    - Greedy Search
    - A* Search
  It also manages file operations for dumping the search trace if required.
  
 - Expense_Puzzle_8 Class: This is the main class that handles command-line input, reads the start and goal states from files, and invokes the appropriate search method based on
  the command-line arguments.

## How to Run the Code:
 1. Compilation:
   - Open a terminal or command prompt in the project directory.
   - While running the code, make sure that the start and goal states are in the same directory.
   - Compile the code using the following command:
     javac Expense_Puzzle_8.java
     

 2.Execution:
   - The program expects four arguments: start file, goal file, search method, and dump flag (optional).
   - To run the program, use the following command:
     java Expense_Puzzle_8 <start-file> <goal-file> <method> <dump-flag>
   - Example:
     java Expense_Puzzle_8 start.txt goal.txt bfs true
    
   - Arguments:
     - <start-file>: File containing the start state of the puzzle.
     - <goal-file>: File containing the goal state of the puzzle.
     - <method>: The search method to use (options: `bfs`, `dfs`, `ucs`, `dls`, `ids`, `greedy`, `a*`).
     - <dump-flag>: Optional flag (`true` or `false`) indicating whether to generate a trace file of the search.

 3.Depth Limit for DLS:
   - If `dls` is chosen as the method, you will be prompted to enter the depth limit during runtime.

 4.Dump File:
   - If the dump flag is set to `true`, a trace file will be generated containing details of the search process. The filename follows the format `trace-YYYY-MM-DD-HH-MM-SS.txt`.


## Notes:
  - The code uses a 3x3 grid to represent the puzzle.
  - Manhattan distance is used as the heuristic for informed searches (`greedy` and `a*`).
  - If no method is specified, `a*` search is used as the default.
  - Please note that generating the dump file may take longer than expected. The file size can reach up to 7 GB, you may need extra applications like ultra edit or File Explorer
    to open the trace file.

