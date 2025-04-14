import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

class PuzzleState {
    int[][] currentState;   
    String moveTaken;        
    int currentDepth;        
    PuzzleState previousState;  
    int moveCost;            
    int heuristicScore;      
    public PuzzleState(int[][] grid, PuzzleState previous, String action, int depth, int heuristic, int cost) {
        currentState = grid;
        previousState = previous;
        moveTaken = action;
        currentDepth = depth;
        moveCost = cost;
        heuristicScore = heuristic;
    }
}


class PuzzleSolver {
    int[][] initialState, targetState;
    boolean dump;
    int maxDepth;
    FileWriter fWriter;
    PrintWriter pwOb;

    public PuzzleSolver(ArrayList<ArrayList<Integer>> start, ArrayList<ArrayList<Integer>> goal, boolean traceFlag) {
        initialState = new int[start.size()][start.get(0).size()];
        targetState = new int[goal.size()][goal.get(0).size()];
        dump = traceFlag;
        
        try {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            String filename = "trace-" + timeStamp + ".txt";
            
            fWriter = new FileWriter(filename, false);  
            pwOb = new PrintWriter(fWriter);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        for (int i = 0; i < start.size(); i++) {
            for (int j = 0; j < start.get(i).size(); j++) {
                initialState[i][j] = start.get(i).get(j);
            }
        }
    
        for (int i = 0; i < goal.size(); i++) {
            for (int j = 0; j < goal.get(i).size(); j++) {
                targetState[i][j] = goal.get(i).get(j);
            }
        }
    }

    public void cleanUpResources() {
        try {
            if (pwOb != null) {
                pwOb.flush(); 
                pwOb.close();
            }
            if (fWriter != null) {
                fWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int calculateManhattanDistance(int tileValue, int row, int col) {
        int targetRow = -1;
        int targetCol = -1;

        for (int i = 0; i < targetState.length; i++) {
            for (int j = 0; j < targetState[i].length; j++) {
                if (targetState[i][j] == tileValue) {
                    targetRow = i;
                    targetCol = j;
                    break;
                }
            }
        }
        return Math.abs(targetRow - row) + Math.abs(targetCol - col);
    }

    public int computeManhattanDistance(int[][] puzzleState) {
        int totalDistance = 0;
        for (int i = 0; i < puzzleState.length; i++) {
            for (int j = 0; j < puzzleState[i].length; j++) {
                if (puzzleState[i][j] != targetState[i][j]) {
                    totalDistance += (puzzleState[i][j] * calculateManhattanDistance(puzzleState[i][j], i, j));
                }
            }
        }
        return totalDistance;
    }

    public ArrayList<PuzzleState> generateChildStates(PuzzleState currentState, int depth, int heuristic, int moveCost) {
        int blankRow = -1, blankCol = -1;
        int[][] grid = currentState.currentState;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                    break;
                }
            }
        }

        ArrayList<PuzzleState> children = new ArrayList<>();

        // Try moving tiles up, down, left, and right
        // Move up
        if (blankRow - 1 >= 0) {
            int tempCost = grid[blankRow - 1][blankCol];
            swapTiles(grid, blankRow, blankCol, blankRow - 1, blankCol);
            int[][] newState = cloneGrid(grid);
            swapTiles(grid, blankRow, blankCol, blankRow - 1, blankCol); 
            int newHeuristic = (heuristic >= 1) ? computeManhattanDistance(newState) : heuristic;
            children.add(new PuzzleState(newState, currentState, "Move " + grid[blankRow - 1][blankCol] + " Down", depth, newHeuristic, moveCost + tempCost));
        }

        // Move down
        if (blankRow + 1 < grid.length) {
            int tempCost = grid[blankRow + 1][blankCol];
            swapTiles(grid, blankRow, blankCol, blankRow + 1, blankCol);
            int[][] newState = cloneGrid(grid);
            swapTiles(grid, blankRow, blankCol, blankRow + 1, blankCol); 
            int newHeuristic = (heuristic >= 1) ? computeManhattanDistance(newState) : heuristic;
            children.add(new PuzzleState(newState, currentState, "Move " + grid[blankRow + 1][blankCol] + " Up", depth, newHeuristic, moveCost + tempCost));
        }

        // Move left
        if (blankCol - 1 >= 0) {
            int tempCost = grid[blankRow][blankCol - 1];
            swapTiles(grid, blankRow, blankCol, blankRow, blankCol - 1);
            int[][] newState = cloneGrid(grid);
            swapTiles(grid, blankRow, blankCol, blankRow, blankCol - 1); 
            int newHeuristic = (heuristic >= 1) ? computeManhattanDistance(newState) : heuristic;
            children.add(new PuzzleState(newState, currentState, "Move " + grid[blankRow][blankCol - 1] + " Right", depth, newHeuristic, moveCost + tempCost));
        }

        // Move right
        if (blankCol + 1 < grid[0].length) {
            int tempCost = grid[blankRow][blankCol + 1];
            swapTiles(grid, blankRow, blankCol, blankRow, blankCol + 1);
            int[][] newState = cloneGrid(grid);
            swapTiles(grid, blankRow, blankCol, blankRow, blankCol + 1); 
            int newHeuristic = (heuristic >= 1) ? computeManhattanDistance(newState) : heuristic;
            children.add(new PuzzleState(newState, currentState, "Move " + grid[blankRow][blankCol + 1] + " Left", depth, newHeuristic, moveCost + tempCost));
        }

        return children;
    }

    private void swapTiles(int[][] grid, int row1, int col1, int row2, int col2) {
        int temp = grid[row1][col1];
        grid[row1][col1] = grid[row2][col2];
        grid[row2][col2] = temp;
    }

    private int[][] cloneGrid(int[][] grid) {
        int[][] clone = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            clone[i] = grid[i].clone();
        }
        return clone;
    }

    
    public boolean isGoalState(int[][] puzzleState) {
        for (int i = 0; i < puzzleState.length; i++) {
            for (int j = 0; j < puzzleState[i].length; j++) {
                if (puzzleState[i][j] != targetState[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public void traceSolutionPath(PuzzleState finalState) {
        Stack<PuzzleState> solutionPath = new Stack<>();
        PuzzleState currentState = finalState;
        
        
        while (currentState != null) {
            solutionPath.push(currentState);
            currentState = currentState.previousState;
        }
        solutionPath.pop(); 

        if (solutionPath.isEmpty()) {
            System.out.println("     None");
        }

        while (!solutionPath.isEmpty()) {
            PuzzleState state = solutionPath.pop();
            System.out.println("     " + state.moveTaken);
        }
    }

  
    public String puzzleStateToString(int[][] puzzleState) {
        StringBuilder str = new StringBuilder();
        for (int[] row : puzzleState) {
            for (int tile : row) {
                str.append(tile);
            }
        }
        return str.toString();
    }


    public void logTrace(String message) {
        if (dump && pwOb != null) {
            pwOb.println(message);  
        }
    }

    public String formatState(int[][] state) {
        StringBuilder stateText = new StringBuilder("[");
        for (int i = 0; i < state.length; i++) {
            stateText.append("[");
            for (int j = 0; j < state[i].length; j++) {
                stateText.append(state[i][j]);
                if (j < state[i].length - 1) {
                    stateText.append(",");
                }
            }
            stateText.append("]");
        }
        return stateText.append("]").toString();
    }

    public String describeGoalState(PuzzleState node) {
        if (node == null) {
            return "None";
        } else {
            return "< state = " + formatState(node.currentState) + ", action = {" + node.moveTaken + "}, g(n) = " 
                   + node.moveCost + " d = " + node.currentDepth + ", f(n) = " + (node.heuristicScore + node.moveCost)
                   + ", Parent = Pointer to " + describeGoalState(node.previousState) + " >";
        }
    }

    public String describeGoal(PuzzleState goalNode) {
        return "Goal Found: " + describeGoalState(goalNode);
    }

    public String describeSuccessorState(PuzzleState node) {
        if (node == null) {
            return "None";
        } else {
            return "< State: " + formatState(node.currentState) + ", Action: " + node.moveTaken 
                + ", Cost: g(n)=" + node.moveCost + ", Depth: " + node.currentDepth 
                + ", Heuristic: f(n)=" + (node.heuristicScore + node.moveCost)
                + ", Parent: " + describeFringe(node.previousState) + " >";
        }
    }

    public String describeSuccessor(PuzzleState currentState) {
        return "Generating successors to " + describeSuccessorState(currentState) + "\n";
    }

    public String convertSetToText(String stateSet) {
        StringBuilder formattedSet = new StringBuilder("[");
        int setLength = (int) Math.sqrt(stateSet.length());
        for (int i = 0; i < stateSet.length(); i += setLength) {
            formattedSet.append("[");
            for (int j = 0; j < setLength; j++) {
                formattedSet.append(stateSet.charAt(i + j));
                if (j < setLength - 1) {
                    formattedSet.append(",");
                }
            }
            formattedSet.append("]");
        }
        return formattedSet.append("]").toString();
    }

   
    public String describeFringe(PuzzleState node) {
        if (node == null) {
            return "None";
        } else {
            return "< state = " + formatState(node.currentState) + ", action = {" + node.moveTaken + "}, g(n) = " 
                   + node.moveCost + " d = " + node.currentDepth + ", f(n) = " + (node.heuristicScore + node.moveCost)
                   + ", Parent = Pointer to " + describeFringe(node.previousState) + " >";
        }
    }

    
    public String describeFringeStack(Stack<PuzzleState> stack) {
        StringBuilder fringeText = new StringBuilder();
        for (PuzzleState state : stack) {
            fringeText.append("\t\t").append(describeFringe(state)).append("\n");
        }
        return fringeText.toString();
    }

    public String describeFringeQueue(Queue<PuzzleState> queue) {
        StringBuilder fringeText = new StringBuilder();
        for (PuzzleState state : queue) {
            fringeText.append("\t\t").append(describeFringe(state)).append("\n");
        }
        return fringeText.toString();
    }


    public boolean isPuzzleSolvable(int[][] puzzleState) {
        int[] puzzleArray = new int[puzzleState.length * puzzleState.length];
        int index = 0;
        
        for (int[] row : puzzleState) {
            for (int tile : row) {
                puzzleArray[index++] = tile;
            }
        }

       
        boolean isParityEven = true;
        int gridWidth = (int) Math.sqrt(puzzleArray.length);
        boolean isBlankOnEvenRow = true;
        for (int i = 0; i < puzzleArray.length; i++) {
            if (puzzleArray[i] == 0) {
                isBlankOnEvenRow = (i / gridWidth) % 2 == 0;
                continue;
            }
            for (int j = i + 1; j < puzzleArray.length; j++) {
                if (puzzleArray[i] > puzzleArray[j] && puzzleArray[j] != 0) {
                    isParityEven = !isParityEven;
                }
            }
        }
    
        if (gridWidth % 2 == 0 && isBlankOnEvenRow) {
            return !isParityEven;
        }
    
        return isParityEven;
    }


    public void bfs(String startFile, String goalFile, String searchMethod, boolean dumpFlag) {
        if (!isPuzzleSolvable(initialState)) {
            System.out.println("No Solution Found.");
        } else {
            if (dumpFlag) {
                logTrace("Command-Line Arguments : ['" + startFile + "', '" + goalFile + "', '" + searchMethod + "', 'true']\n"
                        + "Method Selected: " + searchMethod + "\nRunning " + searchMethod + "\n");
            }

            int maxFringeSize = Integer.MIN_VALUE;
            Queue<PuzzleState> openQueue = new LinkedList<>();
            HashSet<String> visitedStates = new LinkedHashSet<>();

            int nodesPopped = 0, nodesExpanded = 0, nodesGenerated = 1;
            PuzzleState currentState = new PuzzleState(initialState, null, "Start", 0, computeManhattanDistance(initialState), 0);
            openQueue.add(currentState);

            boolean solutionFound = false;
            String closedSetText = "";

            while (!openQueue.isEmpty()) {
                maxFringeSize = Math.max(openQueue.size(), maxFringeSize);
                currentState = openQueue.poll();
                nodesPopped++;

                if (isGoalState(currentState.currentState)) {
                    if (dumpFlag) {
                        logTrace(describeGoal(currentState));
                        logTrace("\n\tNodes Popped: " + nodesPopped + "\n\tNodes Expanded: " + nodesExpanded + "\n\tNodes Generated: " + nodesGenerated + "\n\tMax Fringe Size: " + maxFringeSize);
                    }
                    System.out.println("Nodes Popped: " + nodesPopped + "\nNodes Expanded: " + nodesExpanded + "\nNodes Generated: " + nodesGenerated + "\nMax Fringe Size: " + maxFringeSize);
                    System.out.println("Solution Found at depth " + currentState.currentDepth + " with a cost of " + currentState.moveCost + ".");
                    System.out.println("Steps: ");
                    traceSolutionPath(currentState);
                    solutionFound = true;
                    break;
                }

                String stateString = puzzleStateToString(currentState.currentState);
                if (!visitedStates.contains(stateString)) {
                    visitedStates.add(stateString);
                    if (dumpFlag) logTrace(describeSuccessor(currentState));

                    ArrayList<PuzzleState> successors = generateChildStates(currentState, currentState.currentDepth + 1, 1, currentState.moveCost);
                    nodesExpanded++;

                    if (dumpFlag) {
                        logTrace("\t" + successors.size() + " successors generated\n");
                        logTrace("\tClosed: [" + closedSetText + "]\n");
                    }

                    for (PuzzleState successor : successors) {
                        openQueue.add(successor);
                        nodesGenerated++;
                    }

                    if (dumpFlag) {
                        String fringeDescription = describeFringeQueue(openQueue);
                        logTrace("\tFringe: [\n" + fringeDescription + "\t]\n\n");
                    }
                }
            }

            if (!solutionFound) {
                System.out.println("No Solution Found.");
            }
            cleanUpResources();
        }
    }

  
    public void ucs(String startFile, String goalFile, String searchMethod, boolean dumpFlag) {
        if (!isPuzzleSolvable(initialState)) {
            System.out.println("No Solution Found.");
        } else {
            if (dumpFlag) {
                logTrace("Command-Line Arguments : ['" + startFile + "', '" + goalFile + "', '" + searchMethod + "', 'true']\n"
                        + "Method Selected: " + searchMethod + "\nRunning " + searchMethod + "\n");
                flushTrace();
            }
    
            int maxFringeSize = Integer.MIN_VALUE;
            PriorityQueue<PuzzleState> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(state -> state.moveCost));
            HashSet<String> visitedStates = new LinkedHashSet<>();
    
            int nodesPopped = 0, nodesExpanded = 0, nodesGenerated = 1;
            PuzzleState currentState = new PuzzleState(initialState, null, "Start", 0, 0, 0);
            priorityQueue.add(currentState);
    
            while (!priorityQueue.isEmpty()) {
                maxFringeSize = Math.max(priorityQueue.size(), maxFringeSize);
                currentState = priorityQueue.poll();
                nodesPopped++;
    
                if (isGoalState(currentState.currentState)) {
                    if (dumpFlag) {
                        logTrace(describeGoal(currentState));
                        logTrace("\n\tNodes Popped: " + nodesPopped + "\n\tNodes Expanded: " + nodesExpanded 
                                + "\n\tNodes Generated: " + nodesGenerated + "\n\tMax Fringe Size: " + maxFringeSize);
                        flushTrace();
                    }
                    System.out.println("Nodes Popped: " + nodesPopped + "\nNodes Expanded: " + nodesExpanded 
                                    + "\nNodes Generated: " + nodesGenerated + "\nMax Fringe Size: " + maxFringeSize);
                    System.out.println("Solution Found at depth " + currentState.currentDepth 
                                    + " with a cost of " + currentState.moveCost + ".");
                    traceSolutionPath(currentState);
                    cleanUpResources();
                    return;
                }
    
                String stateString = puzzleStateToString(currentState.currentState);
                if (!visitedStates.contains(stateString)) {
                    visitedStates.add(stateString);
                    nodesExpanded++;
    
                    if (dumpFlag) {
                        logTrace(describeSuccessor(currentState));
                        flushTrace();  
                    }
    
                    ArrayList<PuzzleState> successors = generateChildStates(currentState, currentState.currentDepth + 1, 0, currentState.moveCost);
                    if (dumpFlag) {
                        logTrace("\t" + successors.size() + " successors generated\n");
                        flushTrace();  
                    }
    
                    for (PuzzleState successor : successors) {
                        priorityQueue.add(successor);
                        nodesGenerated++;
                    }
    
                    if (dumpFlag) {
                        String fringeDescription = describeFringeQueue(priorityQueue);
                        logTrace("\tFringe: [\n" + fringeDescription + "\t]\n\n");
                        flushTrace();  
                    }
                }
            }
    
            System.out.println("No Solution Found.");
            cleanUpResources();
        }
    }

   
    public void dfs(String startFile, String goalFile, String searchMethod, boolean dumpFlag) {
        if (!isPuzzleSolvable(initialState)) {
            System.out.println("No Solution Found.");
        } else {
            if (dumpFlag) {
                logTrace("Command-Line Arguments : ['" + startFile + "', '" + goalFile + "', '" + searchMethod + "', 'true']\n"
                        + "Method Selected: " + searchMethod + "\nRunning " + searchMethod + "\n");
                flushTrace();
            }

            int maxFringeSize = Integer.MIN_VALUE;
            Stack<PuzzleState> openStack = new Stack<>();
            HashSet<String> visitedStates = new HashSet<>();

            int nodesPopped = 0, nodesExpanded = 0, nodesGenerated = 1;
            PuzzleState currentState = new PuzzleState(initialState, null, "Start", 0, 0, 0);
            openStack.push(currentState);

            while (!openStack.isEmpty()) {
                maxFringeSize = Math.max(openStack.size(), maxFringeSize);
                currentState = openStack.pop();
                nodesPopped++;

                if (dumpFlag) {
                    logTrace("Generating successors to " + describeSuccessorState(currentState) + "\n");
                    flushTrace(); 
                }

                if (isGoalState(currentState.currentState)) {
                    if (dumpFlag) {
                        logTrace(describeGoal(currentState));
                        logTrace("\n\tNodes Popped: " + nodesPopped + "\n\tNodes Expanded: " + nodesExpanded
                                + "\n\tNodes Generated: " + nodesGenerated + "\n\tMax Fringe Size: " + maxFringeSize);
                        flushTrace();
                    }
                    System.out.println("Nodes Popped: " + nodesPopped + "\nNodes Expanded: " + nodesExpanded
                            + "\nNodes Generated: " + nodesGenerated + "\nMax Fringe Size: " + maxFringeSize);
                    System.out.println("Solution Found at depth " + currentState.currentDepth + " with a cost of " + currentState.moveCost + ".");
                    traceSolutionPath(currentState);
                    cleanUpResources();
                    return;
                }

                String stateString = puzzleStateToString(currentState.currentState);
                if (!visitedStates.contains(stateString)) {
                    visitedStates.add(stateString);
                    nodesExpanded++;

                    if (dumpFlag) {
                        logTrace(describeSuccessor(currentState));
                        flushTrace();  
                    }

                    ArrayList<PuzzleState> successors = generateChildStates(currentState, currentState.currentDepth + 1, 0, currentState.moveCost);
                    nodesGenerated += successors.size();

                    if (dumpFlag) {
                        logTrace("\t" + successors.size() + " successors generated\n");
                        flushTrace();  
                    }

                    for (PuzzleState successor : successors) {
                        openStack.push(successor);
                    }

                    if (dumpFlag) {
                        String fringeDescription = describeFringeStack(openStack);
                        logTrace("\tFringe: [\n" + fringeDescription + "\t]\n\n");
                        flushTrace(); 
                    }
                }
            }

            System.out.println("No Solution Found.");
            cleanUpResources();
        }
    }
    public void depthLimitedSearch(String startFile, String goalFile, String searchMethod, boolean dumpFlag, int depthLimit) {
        if (!isPuzzleSolvable(initialState)) {
            System.out.println("No Solution Found.");
        } else {
            if (dumpFlag) {
                logTrace("Command-Line Arguments : ['" + startFile + "', '" + goalFile + "', '" + searchMethod + "', 'true']\n" +
                        "Method Selected: " + searchMethod + "\nRunning " + searchMethod + "\n");
            }
    
            int maxFringeSize = Integer.MIN_VALUE;
            Stack<PuzzleState> stateStack = new Stack<>();
            Set<String> closedSet = new HashSet<>(); 
            int nodesPopped = 0, nodesExpanded = 0, nodesGenerated = 1;
    
            PuzzleState currentState = new PuzzleState(initialState, null, "Start", 0, 0, 0);
            stateStack.push(currentState);
            closedSet.add(Arrays.deepToString(currentState.currentState)); 
            boolean foundSolution = false;
    
            while (!stateStack.isEmpty()) {
                maxFringeSize = Math.max(stateStack.size(), maxFringeSize);
                currentState = stateStack.pop();
                nodesPopped++;
    
                if (isGoalState(currentState.currentState)) {
                    if (dumpFlag) {
                        logTrace(describeGoal(currentState));
                        logTrace("\n\tNodes Popped: " + nodesPopped + "\n\tNodes Expanded: " + nodesExpanded + "\n\tNodes Generated: " + nodesGenerated + "\n\tMax Fringe Size: " + maxFringeSize);
                    }
                    System.out.println("Nodes Popped: " + nodesPopped + "\nNodes Expanded: " + nodesExpanded + "\nNodes Generated: " + nodesGenerated + "\nMax Fringe Size: " + maxFringeSize);
                    System.out.println("Solution Found at depth " + currentState.currentDepth + " with a cost of " + currentState.moveCost + ".");
                    traceSolutionPath(currentState);
                    foundSolution = true;
                    break;
                }
    
                if (dumpFlag) {
                    logTrace(describeSuccessor(currentState));
                }
                nodesExpanded++;
    
                ArrayList<PuzzleState> successors = generateChildStates(currentState, currentState.currentDepth + 1, 0, currentState.moveCost);
                if (dumpFlag) logTrace("\t" + successors.size() + " successors generated\n");
    
                for (PuzzleState child : successors) {
                    String childStateString = Arrays.deepToString(child.currentState); 
    
                    if (child.currentDepth <= depthLimit && !closedSet.contains(childStateString)) {
                        stateStack.push(child);
                        closedSet.add(childStateString); 
                        nodesGenerated++;
                    }
                }
    
                if (dumpFlag) {
                    String fringeDescription = describeFringeStack(stateStack);
                    logTrace("\tFringe: [\n" + fringeDescription + "\t]\n\n");
                }
            }
    
            if (!foundSolution) {
                System.out.println("No Solution Found.");
            }
    
            cleanUpResources();
        }
    }
    
    public void iterativeDeepeningSearch(String startFile, String goalFile, String searchMethod, boolean dumpFlag, int depthLimit) {
        if (!isPuzzleSolvable(initialState)) {
            System.out.println("No Solution Found.");
        } else {
            if (dumpFlag) {
                logTrace("Command-Line Arguments : ['" + startFile + "', '" + goalFile + "', '" + searchMethod + "', 'true']\n" +
                        "Method Selected: " + searchMethod + "\nRunning " + searchMethod + " with depth limit " + depthLimit + "\n");
            }
    
            boolean solutionFound = false;
    
            while (!solutionFound) {
                Stack<PuzzleState> stateStack = new Stack<>();
                int maxFringeSize = Integer.MIN_VALUE;
                int nodesPopped = 0, nodesExpanded = 0, nodesGenerated = 1;
    
                PuzzleState currentState = new PuzzleState(initialState, null, "Start", 0, 0, 0);
                stateStack.push(currentState);
    
                while (!stateStack.isEmpty()) {
                    maxFringeSize = Math.max(stateStack.size(), maxFringeSize);
                    currentState = stateStack.pop();
                    nodesPopped++;
    
                    if (isGoalState(currentState.currentState)) {
                        if (dumpFlag) {
                            logTrace(describeGoal(currentState));
                            logTrace("\n\tNodes Popped: " + nodesPopped + "\n\tNodes Expanded: " + nodesExpanded + 
                                     "\n\tNodes Generated: " + nodesGenerated + "\n\tMax Fringe Size: " + maxFringeSize);
                           
                            flushTrace();
                        }
                        System.out.println("Nodes Popped: " + nodesPopped + "\nNodes Expanded: " + nodesExpanded + 
                                           "\nNodes Generated: " + nodesGenerated + "\nMax Fringe Size: " + maxFringeSize);
                        System.out.println("Solution Found at depth " + currentState.currentDepth + 
                                           " with a cost of " + currentState.moveCost + ".");
                        traceSolutionPath(currentState);
                        solutionFound = true;
                        break;
                    }
    
                    
                    if (dumpFlag) {
                        logTrace(describeSuccessor(currentState));
                        flushTrace(); 
                    }
    
                    nodesExpanded++;
                    ArrayList<PuzzleState> successors = generateChildStates(currentState, currentState.currentDepth + 1, 0, currentState.moveCost);
                    
                    
                    if (dumpFlag) {
                        logTrace("\t" + successors.size() + " successors generated\n");
                        flushTrace(); 
                    }
    
                    for (PuzzleState child : successors) {
                        if (child.currentDepth <= depthLimit) {
                            stateStack.push(child);
                            nodesGenerated++;
                        }
                    }
    
                   
                    if (dumpFlag) {
                        String fringeDescription = describeFringeStack(stateStack);
                        logTrace("\tFringe: [\n" + fringeDescription + "\t]\n\n");
                        flushTrace();  
                    }
                }
    
                if (!solutionFound) {
                    System.out.println("Incrementing depth to " + (++depthLimit));
                }
            }
            cleanUpResources();
        }
    }
    
    private void flushTrace() {
        try {
            fWriter.flush(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Greedy Best-First Search implementation

    public void greedySearch(String startFile, String goalFile, String searchMethod, boolean dumpFlag) {
        if (!isPuzzleSolvable(initialState)) {
            System.out.println("No Solution Found.");
        } else {
            if (dumpFlag) {
                logTrace("Command-Line Arguments : ['" + startFile + "', '" + goalFile + "', '" + searchMethod + "', 'true']\n" +
                        "Method Selected: " + searchMethod + "\nRunning " + searchMethod + "\n");
            }

            int maxFringeSize = Integer.MIN_VALUE;
            PriorityQueue<PuzzleState> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(state -> state.heuristicScore));
            HashSet<String> visitedStates = new HashSet<>();

            int nodesPopped = 0, nodesExpanded = 0, nodesGenerated = 1;
            PuzzleState currentState = new PuzzleState(initialState, null, "Start", 0, computeManhattanDistance(initialState), 0);
            priorityQueue.add(currentState);

            while (!priorityQueue.isEmpty()) {
                maxFringeSize = Math.max(priorityQueue.size(), maxFringeSize);
                currentState = priorityQueue.poll();
                nodesPopped++;

                
                if (dumpFlag) {
                    logTrace("Polling from priority queue: " + describeFringe(currentState));
                }

                if (isGoalState(currentState.currentState)) {
                    if (dumpFlag) {
                        logTrace(describeGoal(currentState));
                        logTrace("\n\tNodes Popped: " + nodesPopped + "\n\tNodes Expanded: " + nodesExpanded + "\n\tNodes Generated: " + nodesGenerated + "\n\tMax Fringe Size: " + maxFringeSize);
                    }
                    System.out.println("Nodes Popped: " + nodesPopped + "\nNodes Expanded: " + nodesExpanded + "\nNodes Generated: " + nodesGenerated + "\nMax Fringe Size: " + maxFringeSize);
                    System.out.println("Solution Found at depth " + currentState.currentDepth + " with a cost of " + currentState.moveCost + ".");
                    traceSolutionPath(currentState);
                    break;
                }

                String stateString = puzzleStateToString(currentState.currentState);
                if (!visitedStates.contains(stateString)) {
                    visitedStates.add(stateString);
                    nodesExpanded++;

                    if (dumpFlag) {
                        logTrace(describeSuccessor(currentState));
                    }

                    ArrayList<PuzzleState> successors = generateChildStates(currentState, currentState.currentDepth + 1, 1, currentState.moveCost);

                    if (dumpFlag) {
                        logTrace("\t" + successors.size() + " successors generated\n");
                    }

                    for (PuzzleState successor : successors) {
                        priorityQueue.add(successor);
                        nodesGenerated++;
                        if (dumpFlag) {
                            logTrace("Adding to priority queue: " + describeFringe(successor));
                        }
                    }

                    if (dumpFlag) {
                        String fringeDescription = describeFringeQueue(priorityQueue);
                        logTrace("\tFringe: [\n" + fringeDescription + "\t]\n\n");
                    }
                }
            }

            if (priorityQueue.isEmpty()) {
                System.out.println("No Solution Found.");
            }

            
            cleanUpResources();
        }
    }

    // A* Search implementation
    public void aStarSearch(String startFile, String goalFile, String searchMethod, boolean dumpFlag) {
        if (!isPuzzleSolvable(initialState)) {
            System.out.println("No Solution Found.");
        } else {
            if (dumpFlag) {
                logTrace("Command-Line Arguments : ['" + startFile + "', '" + goalFile + "', '" + searchMethod + "', 'true']\n" +
                        "Method Selected: " + searchMethod + "\nRunning " + searchMethod + "\n");
            }

            int maxFringeSize = Integer.MIN_VALUE;

            PriorityQueue<PuzzleState> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(
                state -> (state.heuristicScore + state.moveCost)  
            ));

            HashSet<String> visitedStates = new HashSet<>();
            int nodesPopped = 0, nodesExpanded = 0, nodesGenerated = 1;

            
            PuzzleState currentState = new PuzzleState(initialState, null, "Start", 0, computeManhattanDistance(initialState), 0);
            priorityQueue.add(currentState);

            while (!priorityQueue.isEmpty()) {
                maxFringeSize = Math.max(priorityQueue.size(), maxFringeSize);
                currentState = priorityQueue.poll(); 
                nodesPopped++;

                if (dumpFlag) {
                    logTrace("Polling from priority queue: " + describeFringe(currentState));
                }

                if (isGoalState(currentState.currentState)) {
                    if (dumpFlag) {
                        logTrace(describeGoal(currentState));
                        logTrace("\n\tNodes Popped: " + nodesPopped + "\n\tNodes Expanded: " + nodesExpanded + "\n\tNodes Generated: " + nodesGenerated + "\n\tMax Fringe Size: " + maxFringeSize);
                    }
                    System.out.println("Nodes Popped: " + nodesPopped + "\nNodes Expanded: " + nodesExpanded + "\nNodes Generated: " + nodesGenerated + "\nMax Fringe Size: " + maxFringeSize);
                    System.out.println("Solution Found at depth " + currentState.currentDepth + " with a cost of " + currentState.moveCost + ".");
                    System.out.println("Steps: ");
                    traceSolutionPath(currentState);
                    break;
                }

                String stateString = puzzleStateToString(currentState.currentState);
                if (!visitedStates.contains(stateString)) {
                    visitedStates.add(stateString);
                    nodesExpanded++;

                    if (dumpFlag) {
                        logTrace(describeSuccessor(currentState));
                    }

                    ArrayList<PuzzleState> successors = generateChildStates(currentState, currentState.currentDepth + 1, 1, currentState.moveCost);  // Heuristic flag = 1 for A*

                    if (dumpFlag) {
                        logTrace("\t" + successors.size() + " successors generated\n");
                    }

                    for (PuzzleState successor : successors) {
                        priorityQueue.add(successor); 
                        nodesGenerated++;
                        if (dumpFlag) {
                            logTrace("Adding to priority queue: " + describeFringe(successor));
                        }
                    }

                    if (dumpFlag) {
                        String fringeDescription = describeFringeQueue(priorityQueue);
                        logTrace("\tFringe: [\n" + fringeDescription + "\t]\n\n");
                    }
                }
            }

    
            if (priorityQueue.isEmpty()) {
                System.out.println("No Solution Found.");
            }

            cleanUpResources();
        }
    }
}


public class Expense_Puzzle_8 {

    public static void main(String[] args) {
        try {
        
            Scanner scanner = new Scanner(System.in);
            File startFile = new File(args[0]);
            File goalFile = new File(args[1]);

            String method = "a*";  
            boolean dump = false;  

           
            if (args.length == 3) {
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                    dump = Boolean.parseBoolean(args[2]);
                } else {
                    method = args[2];
                }
            } else if (args.length == 4) {
                method = args[2];
                dump = Boolean.parseBoolean(args[3]);
            }

        
            ArrayList<ArrayList<Integer>> startState = parseFile(startFile);
            ArrayList<ArrayList<Integer>> goalState = parseFile(goalFile);

           
            
            PuzzleSolver puzzleSolver = new PuzzleSolver(startState, goalState, dump);

           
            if (method.equals("bfs")) {
                puzzleSolver.bfs(args[0], args[1], "bfs", dump);
            } else if (method.equals("dfs")) {
                puzzleSolver.dfs(args[0], args[1], "dfs", dump);
            } else if (method.equals("ucs")) {
                puzzleSolver.ucs(args[0], args[1], "ucs", dump);
            } else if (method.equals("dls")) {
                System.out.print("Enter depth limit: ");
                int depthLimit = scanner.nextInt();
                puzzleSolver.depthLimitedSearch(args[0], args[1], "dls", dump, depthLimit);
            } else if (method.equals("ids")) {
                puzzleSolver.iterativeDeepeningSearch(args[0], args[1], "ids", dump, 0);
            } else if (method.equals("greedy")) {
                puzzleSolver.greedySearch(args[0], args[1], "greedy", dump);
            } else if (method.equals("a*")) {
                puzzleSolver.aStarSearch(args[0], args[1], "a*", dump);
            } else {
                System.out.println("Unknown method. Please choose from bfs, dfs, ucs, dls, ids, greedy, or a*.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static ArrayList<ArrayList<Integer>> parseFile(File file) throws Exception {
        ArrayList<ArrayList<Integer>> state = new ArrayList<>();
        Scanner fileReader = new Scanner(file);

        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            if (!line.equals("END OF FILE")) {
                ArrayList<Integer> row = new ArrayList<>();
                for (String number : line.split(" ")) {
                    row.add(Integer.parseInt(number));
                }
                state.add(row);
            }
        }
        fileReader.close();
        return state;
    }
}