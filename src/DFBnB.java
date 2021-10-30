import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

public class DFBnB {
    GameBoard root;
    Set<GameBoard> hashTable = new HashSet<GameBoard>(); // (L)  Represent an hash table that hold the node that we find but nod extend alredy
    Stack<GameBoard> myStack = new Stack<GameBoard>(); // (H) Represent a stack in order do search in "DFS" approach
    Stack<String> hashLoopID = new Stack<String>(); // for not searching the parent board
    Set<String> numbersOfCreatedNodes = new HashSet<String>();//in order to count the number of the created nodes.
    Set<String> openListOfNodes = new HashSet<String>();//in order to count the number of the created nodes.
    GameBoard winner;
    GameBoard winBoard;
    boolean isPrintOpen;
    boolean withTime;
    boolean gameState;
    boolean found=false;
    private ManhattanDistance heuristic = new ManhattanDistance();

    public DFBnB(GameBoard myBoard, GameBoard goalBoard, boolean chooseWithTime, boolean printOpen, boolean state) {
        this.root = myBoard;
        this.winBoard = goalBoard;
        this.withTime = chooseWithTime;
        this.gameState = state;
        this.isPrintOpen = printOpen;
        if (root.checkFinish(goalBoard.getBoard())) {
            System.out.println("Num: 1");
            System.out.println("Cost: " + root.getCost());
        } else
            runDfbnb(myBoard);
    }

    public boolean runDfbnb(GameBoard myBoard) {
        int num = 0;
        long START = System.currentTimeMillis();//Time
        int threshold = Math.min(Integer.MAX_VALUE, factorial(myBoard.getBoard().length + myBoard.getBoard()[0].length));
        String pathResult = "";
        int limit = heuristic.getHuristicCost(myBoard,winBoard);
        myBoard.setHuristics(limit);
        myBoard.setFcost(limit);
        hashTable.add(myBoard);//loop avoidance
        hashLoopID.add(myBoard.searchForBoardUniqueKey());//loop avoidance
        myStack.add(myBoard);//DFS
        numbersOfCreatedNodes.add(myBoard.searchForBoardUniqueKey());
        openListOfNodes.add(myBoard.searchForBoardUniqueKey());

        if(isPrintOpen) printOpenList(openListOfNodes);
        while (!myStack.isEmpty()) { //while stack is not empty
            GameBoard current = myStack.pop(); //remove front
            if (current.getOut().equals("out")) { //if mark as out
                current.setOut("");
                hashTable.remove(current);
                hashLoopID.remove(current.searchForBoardUniqueKey());

            } else {
                openListOfNodes.remove(current.searchForBoardUniqueKey());
                current.setOut("out");
                myStack.add(current);
                ArrayList<GameBoard> childrens = createAllOperations(current);
                PriorityQueue<GameBoard> pQueue = new PriorityQueue<GameBoard>();
                for (GameBoard b : childrens) {
                    pQueue.add(b);
                    numbersOfCreatedNodes.add(b.searchForBoardUniqueKey());
                    openListOfNodes.add(b.searchForBoardUniqueKey());

                }
                if(isPrintOpen) printOpenList(openListOfNodes);
                for (GameBoard b : pQueue) {
                    if (b.finalHuristicAndCost >= threshold) {
                        pQueue.remove(b);
                        pQueue.clear();
                        continue;
                    } else if (hashTable.contains(b) && b.getOut().equals("out")) {
                        pQueue.remove(b);
                        continue;
                    } else if (hashTable.contains(b.searchForBoardUniqueKey()) && !b.getOut().equals("out")) {
                        GameBoard temp = findBoard(b);
                        if (temp.finalHuristicAndCost <= b.finalHuristicAndCost) {
                            pQueue.remove(b);
                            continue;
                        } else {
                            hashTable.remove(temp);
                            hashLoopID.remove(temp.searchForBoardUniqueKey());
                            myStack.remove(b);
                            hashTable.add(b);
                            hashLoopID.add(b.searchForBoardUniqueKey());
                            continue;
                        }
                    } else if (b.checkFinish(winBoard.getBoard())) {
                        threshold = b.finalHuristicAndCost;
                        winner = b;
                        pQueue.remove(b);
                        pQueue.clear();
                        for (GameBoard delit : pQueue) {
                            pQueue.remove(delit);
                        }
                        continue;
                    }
                }
                Stack<GameBoard> forReversTheElement = new Stack<GameBoard>();
                while (!pQueue.isEmpty()) {
                    forReversTheElement.add(pQueue.poll());

                }
                while (!forReversTheElement.isEmpty()) {
                    myStack.add(forReversTheElement.peek());
                    hashLoopID.add(forReversTheElement.peek().searchForBoardUniqueKey());
                    hashTable.add(forReversTheElement.pop());
                }
            }
        }
        if (winner != null) {
            found = true;
           // System.out.println(winner.getPath());

           // System.out.println("Num: " + numbersOfCreatedNodes.size());
           // System.out.println("Cost: " + winner.getCost());
            long END =  System.currentTimeMillis();
            float seconds = (END-START) / 1000F;
            String time = seconds + " seconds";
            String Num = "Num: " + numbersOfCreatedNodes.size();
            saveToFile("output.txt",winner,Num,time);
            if(withTime) {
              //  System.out.println(seconds + " seconds");
            }
            return found;
        }
        if(!found){
            String noPath = "no path";
            String Num = "Num: " + num;
            saveToFileNoFound("output.txt",noPath,Num);
        }
        return found;
    }

    public GameBoard findBoard(GameBoard b) {
        for (Iterator<GameBoard> it = hashTable.iterator(); it.hasNext(); ) {
            GameBoard f = it.next();
            if (f.searchForBoardUniqueKey().equals(b.searchForBoardUniqueKey()))
                return f;
        }
        return null;
    }

    public int factorial(int n) {
        if (n == 0)
            return 1;

        return n * factorial(n - 1);
    }

    /*
    helper method that get all the possible operation-meaning we check if you can move
    left,up,right,down . if possible we add it to an Arraylist.
    if we in game mode 2 (with 2 blanks) then we also check the cases when
    we can move 2 blanks together.

     */
    public ArrayList<GameBoard> createAllOperations(GameBoard currentBoard) {
        //currentBoard.PrintBoard();
        ArrayList<GameBoard> operations = new ArrayList<GameBoard>();
        int[] indexs = currentBoard.find2BlanksIndex();
        int x = indexs[0];
        int y = indexs[1];
        int z = indexs[2];
        int t = indexs[3];
        int boardX = currentBoard.getBoard().length;
        int boardY = currentBoard.getBoard()[0].length;
        int max = Math.max(x, z);
        if (gameState) {
            if (x == z) {
                if (x + 1 < boardX) {
                    if (currentBoard.getBoard()[x][y + 1].getNum() == 0) {
                        GameBoard DOWN = currentBoard.copy(currentBoard.getPath());
                        int currentCost = currentBoard.getCost();

                        Node[][] tileArray = DOWN.getBoard();
                        int tempPrice = DOWN.getBoard()[x][y].getPrice();
                        String tempPath = DOWN.getBoard()[x][y].getPath();
                        tileArray[x][y] = tileArray[x + 1][y];
                        tileArray[z][t] = tileArray[z + 1][t];


                        tileArray[x + 1][y] = new Node(0, DOWN.getPath() + "-" + DOWN.getBoard()[x][y].getNum() + "&" + DOWN.getBoard()[z][t].getNum() + "U");
                        tileArray[z + 1][t] = new Node(0, DOWN.getPath() + "-" + DOWN.getBoard()[z][t].getNum() + "&" + DOWN.getBoard()[x][y].getNum() + "U");
                        tileArray[x + 1][y].setPrice(tempPrice + 7);
                        DOWN.setCost(currentCost + 7);
                        DOWN.setPath(DOWN.getBoard()[x + 1][y].getPath());
                        tileArray[x + 1][y].setFather(DOWN.getBoard()[x][y].getNum() + "&" + DOWN.getBoard()[z][t].getNum() + "U");
                        tileArray[z + 1][t].setFather(DOWN.getBoard()[x][y].getNum() + "&" + DOWN.getBoard()[z][t].getNum() + "U");
                        if (!hashLoopID.contains(DOWN.searchForBoardUniqueKey())) {
                            operations.add(DOWN);
                            DOWN.huristicsResult = heuristic.getHuristicCost(DOWN,winBoard);
                            DOWN.finalHuristicAndCost = DOWN.huristicsResult + DOWN.getCost();
                        }
                    }
                } else if (x - 1 >= 0 && !currentBoard.getBoard()[x - 1][y].getFather().equals(currentBoard.getBoard()[x - 1][y].getNum() + "&" + currentBoard.getBoard()[z - 1][t].getNum() + "U")) {
                    if (currentBoard.getBoard()[x][y + 1].getNum() == 0) {
                        //System.out.println("HERE in x-1 OR y+1");
                        GameBoard UP = currentBoard.copy(currentBoard.getPath());
                        Node[][] tileArray = UP.getBoard();
                        int tempPrice = UP.getBoard()[x][y].getPrice();
                        String tempPath = UP.getBoard()[x][y].getPath();
                        int currentCost = currentBoard.getCost();

                        tileArray[x][y] = tileArray[x - 1][y];
                        tileArray[z][t] = tileArray[z - 1][t];

                        tileArray[x - 1][y] = new Node(0, UP.getPath() + "-" + UP.getBoard()[x][y].getNum() + "&" + UP.getBoard()[z][t].getNum() + "D");
                        tileArray[z - 1][t] = new Node(0, UP.getPath() + "-" + UP.getBoard()[z][t].getNum() + "&" + UP.getBoard()[x][y].getNum() + "D");
                        tileArray[x - 1][y].setPrice(tempPrice + 7);
                        UP.setPath(UP.getBoard()[x - 1][y].getPath());
                        UP.setCost(currentCost + 7);
                        tileArray[x - 1][y].setFather(UP.getBoard()[x][y].getNum() + "&" + UP.getBoard()[z][t].getNum() + "D");
                        if (!hashLoopID.contains(UP.searchForBoardUniqueKey())) {
                            operations.add(UP);
                            UP.huristicsResult = heuristic.getHuristicCost(UP,winBoard);
                            UP.finalHuristicAndCost = UP.huristicsResult + UP.getCost();
                        }
                    }
                }
            } else if (y == t) {
                if (y + 1 < boardY) {
                    if (currentBoard.getBoard()[x + 1][y].getNum() == 0) {
                        GameBoard LEFT = currentBoard.copy(currentBoard.getPath());
                        Node[][] tileArray = LEFT.getBoard();
                        int tempPrice = LEFT.getBoard()[x][y].getPrice();
                        int currentCost = currentBoard.getCost();

                        //String tempPath = LEFT.getBoard()[x][y].getPath();
                        String tempPath = currentBoard.getPath();
                        tileArray[x][y] = tileArray[x][y + 1];
                        tileArray[z][t] = tileArray[z][t + 1];

                        tileArray[x][y + 1] = new Node(0, tempPath + "-" + LEFT.getBoard()[x][y].getNum() + "&" + LEFT.getBoard()[z][t].getNum() + "L");
                        tileArray[z][t + 1] = new Node(0, tempPath + "-" + LEFT.getBoard()[z][t].getNum() + "&" + LEFT.getBoard()[x][y].getNum() + "L");
                        tileArray[x][y + 1].setPrice(tempPrice + 6);
                        LEFT.setPath(LEFT.getBoard()[x][y + 1].getPath());
                        LEFT.setCost(currentCost + 6);
                        tileArray[x][y + 1].setFather(LEFT.getBoard()[x][y].getNum() + "&" + LEFT.getBoard()[z][t].getNum() + "L");

                        if (!hashLoopID.contains(LEFT.searchForBoardUniqueKey())) {
                            operations.add(LEFT);
                            LEFT.huristicsResult = heuristic.getHuristicCost(LEFT,winBoard);
                            LEFT.finalHuristicAndCost = LEFT.huristicsResult + LEFT.getCost();
                        }
                    }
                } else if (y - 1 >= 0) {
                    if (currentBoard.getBoard()[x + 1][y].getNum() == 0) {
                        GameBoard RIGHT = currentBoard.copy(currentBoard.getPath());
                        Node[][] tileArray = RIGHT.getBoard();
                        int currentCost = currentBoard.getCost();

                        int tempPrice = RIGHT.getBoard()[x][y].getPrice();
                        //String tempPath = RIGHT.getBoard()[x][y].getPath();
                        String tempPath = currentBoard.getPath();
                        tileArray[x][y] = tileArray[x][y - 1];
                        tileArray[z][t] = tileArray[z][t - 1];

                        tileArray[x][y - 1] = new Node(0, tempPath + "-" + RIGHT.getBoard()[x][y].getNum() + "&" + RIGHT.getBoard()[z][t].getNum() + "R");
                        tileArray[z][t - 1] = new Node(0, tempPath + "-" + RIGHT.getBoard()[z][t].getNum() + "&" + RIGHT.getBoard()[x][y].getNum() + "R");
                        tileArray[x][y - 1].setPrice(tempPrice + 6);
                        RIGHT.setPath(RIGHT.getBoard()[x][y - 1].getPath());
                        RIGHT.setCost(currentCost + 6);
                        if (!hashLoopID.contains(RIGHT.searchForBoardUniqueKey())) {
                            operations.add(RIGHT);
                            RIGHT.huristicsResult = heuristic.getHuristicCost(RIGHT,winBoard);
                            RIGHT.finalHuristicAndCost = RIGHT.huristicsResult + RIGHT.getCost();
                        }
                    }
                }
            }
        }
        if (y + 1 < boardY && !"y-1".equals(currentBoard.getBoard()[x][y + 1].getFather())) {
            GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
            Node[][] tileArray = newBoard.getBoard();
            // String tempPath = newBoard.getBoard()[x][y].getPath();
            String tempPath = currentBoard.getPath();
            int currentCost = currentBoard.getCost();

            int tempPrice = newBoard.getBoard()[x][y].getPrice();

            tileArray[x][y] = tileArray[x][y + 1];
            if (tempPath.equals("")) {
                tileArray[x][y + 1] = new Node(0, newBoard.getBoard()[x][y].getNum() + "L");
                newBoard.setPath(newBoard.getBoard()[x][y].getNum() + "L" + newBoard.getBoard()[z][t].getPath());
            } else {
                tileArray[x][y + 1] = new Node(0, currentBoard.getPath() + "-" + newBoard.getBoard()[x][y].getNum() + "L");
                // newBoard.setPath(newBoard.getBoard()[x][y+1].getPath() + newBoard.getBoard()[z][t].getPath());
                newBoard.setPath(newBoard.getBoard()[x][y + 1].getPath());

            }
            tileArray[x][y].setFather("y+1");

            tileArray[x][y + 1].setPrice(tempPrice + 5);
            newBoard.setCost(currentCost + 5);
            if (!hashLoopID.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,winBoard);
                newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
            }
        }
        if (x + 1 < boardX && !"x-1".equals(currentBoard.getBoard()[x + 1][y].getFather())) {
            GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
            Node[][] tileArray = newBoard.getBoard();
            //String tempPath = newBoard.getBoard()[x][y].getPath();
            String tempPath = currentBoard.getPath();
            int currentCost = currentBoard.getCost();
            int tempPrice = newBoard.getBoard()[x][y].getPrice();

            tileArray[x][y] = tileArray[x + 1][y];
            if (tempPath.equals("")) {
                tileArray[x + 1][y] = new Node(0, newBoard.getBoard()[x][y].getNum() + "U");
                newBoard.setPath(newBoard.getBoard()[x][y].getNum() + "U" + "-" + newBoard.getBoard()[z][t].getPath());

            } else {
                tileArray[x + 1][y] = new Node(0, currentBoard.getPath() + "-" + newBoard.getBoard()[x][y].getNum() + "U");
                // newBoard.setPath(newBoard.getBoard()[x+1][y].getPath() + "-" +newBoard.getBoard()[z][t].getPath());
                newBoard.setPath(newBoard.getBoard()[x + 1][y].getPath());

            }
            tileArray[x][y].setFather("x+1");

            tileArray[x + 1][y].setPrice(tempPrice + 5);
            newBoard.setCost(currentCost + 5);

            if (!hashLoopID.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,winBoard);
                newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
            }
        }
        if (y - 1 >= 0 && !"y+1".equals(currentBoard.getBoard()[x][y - 1].getFather())) {
            GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
            Node[][] tileArray = newBoard.getBoard();
            //String tempPath = newBoard.getBoard()[x][y].getPath();
            int currentCost = currentBoard.getCost();

            String tempPath = currentBoard.getPath();
            int tempPrice = newBoard.getBoard()[x][y].getPrice();

            tileArray[x][y] = tileArray[x][y - 1];
            if (tempPath.equals("")) {
                tileArray[x][y - 1] = new Node(0, newBoard.getBoard()[x][y].getNum() + "R");
                newBoard.setPath(newBoard.getBoard()[x][y].getNum() + "R" + newBoard.getBoard()[z][t].getPath());

            } else {
                tileArray[x][y - 1] = new Node(0, currentBoard.getPath() + "-" + newBoard.getBoard()[x][y].getNum() + "R");
                // newBoard.setPath(newBoard.getBoard()[x][y-1].getPath() + "-" +newBoard.getBoard()[z][t].getPath());
                newBoard.setPath(newBoard.getBoard()[x][y - 1].getPath());


            }
            tileArray[x][y].setFather("y-1");
            newBoard.setCost(currentCost + 5);

            tileArray[x][y - 1].setPrice(tempPrice + 5);
            if (!hashLoopID.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,winBoard);
                newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
            }
        }

        if (x - 1 >= 0 && !"x+1".equals(currentBoard.getBoard()[x - 1][y].getFather())) {
            GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
            Node[][] tileArray = newBoard.getBoard();
            // String tempPath = newBoard.getBoard()[x][y].getPath();
            String tempPath = currentBoard.getPath();
            int currentCost = currentBoard.getCost();

            int tempPrice = newBoard.getBoard()[x][y].getPrice();
            tileArray[x][y] = tileArray[x - 1][y];
            if (tempPath.equals("")) {
                tileArray[x - 1][y] = new Node(0, newBoard.getBoard()[x][y].getNum() + "D");
                newBoard.setPath(newBoard.getBoard()[x][y].getNum() + "D" + "-" + newBoard.getBoard()[z][t].getPath());
            } else {
                tileArray[x - 1][y] = new Node(0, currentBoard.getPath() + "-" + newBoard.getBoard()[x][y].getNum() + "D");
                //  newBoard.setPath(newBoard.getBoard()[x-1][y].getPath() + "-" +newBoard.getBoard()[z][t].getPath());
                newBoard.setPath(newBoard.getBoard()[x - 1][y].getPath());

            }
            tileArray[x][y].setFather("x-1");
            newBoard.setCost(currentCost + 5);

            tileArray[x - 1][y].setPrice(tempPrice + 5);
            if (!hashLoopID.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,winBoard);
                newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
            }
        }
//---------------------------------------------------------------------------
        if (gameState) {
            if (t + 1 < boardY && !"t-1".equals(currentBoard.getBoard()[z][t + 1].getFather())) {
                GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
                Node[][] tileArray = newBoard.getBoard();
                // String tempPath = newBoard.getBoard()[z][t].getPath();
                String tempPath = currentBoard.getPath();
                int tempPrice = newBoard.getBoard()[z][t].getPrice();
                int currentCost = currentBoard.getCost();

                tileArray[z][t] = tileArray[z][t + 1];
                if (tempPath.equals("")) {
                    tileArray[z][t + 1] = new Node(0, newBoard.getBoard()[z][t].getNum() + "L");
                    if (newBoard.getBoard()[x][y].getPath().equals("")) {
                        newBoard.setPath(newBoard.getBoard()[z][t + 1].getPath());
                    } else {
                        newBoard.setPath(newBoard.getBoard()[x][y].getPath() + newBoard.getBoard()[z][t + 1].getPath() + "L");
                    }

                } else {
                    tileArray[z][t + 1] = new Node(0, tempPath + "-" + newBoard.getBoard()[z][t].getNum() + "L");
                    newBoard.setPath(newBoard.getBoard()[z][t + 1].getPath() + newBoard.getBoard()[z][t].getPath());

                }
                tileArray[z][t].setFather("t+1");
                newBoard.setCost(currentCost + 5);

                tileArray[z][t + 1].setPrice(tempPrice + 5);
                if (!hashLoopID.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,winBoard);
                    newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
                }
            }
            if (z + 1 < boardX && !"z-1".equals(currentBoard.getBoard()[z + 1][t].getFather())) {
                GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
                Node[][] tileArray = newBoard.getBoard();
                // String tempPath = newBoard.getBoard()[z][t].getPath();
                String tempPath = currentBoard.getPath();
                int tempPrice = newBoard.getBoard()[z][t].getPrice();
                int currentCost = currentBoard.getCost();

                tileArray[z][t] = tileArray[z + 1][t];
                if (tempPath.equals("")) {
                    tileArray[z + 1][t] = new Node(0, newBoard.getBoard()[z][t].getNum() + "U");
                    if (newBoard.getBoard()[x][y].getPath().equals("")) {
                        newBoard.setPath(newBoard.getBoard()[z + 1][t].getPath());

                    } else {
                        newBoard.setPath(newBoard.getBoard()[x][y].getPath() + "-" + newBoard.getBoard()[z + 1][t].getPath() + "U");

                    }
                } else {
                    tileArray[z + 1][t] = new Node(0, tempPath + "-" + newBoard.getBoard()[z][t].getNum() + "U");
                    newBoard.setPath(newBoard.getBoard()[z + 1][t].getPath() + newBoard.getBoard()[z][t].getPath());

                }
                tileArray[z][t].setFather("z+1");

                tileArray[z + 1][t].setPrice(tempPrice + 5);
                newBoard.setCost(currentCost + 5);

                if (!hashLoopID.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,winBoard);
                    newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
                }
            }
            if (t - 1 >= 0 && !"t+1".equals(currentBoard.getBoard()[z][t - 1].getFather())) {
                GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
                Node[][] tileArray = newBoard.getBoard();
                //String tempPath = newBoard.getBoard()[z][t].getPath();
                String tempPath = currentBoard.getPath();
                int tempPrice = newBoard.getBoard()[z][t].getPrice();
                int currentCost = currentBoard.getCost();

                tileArray[z][t] = tileArray[z][t - 1];
                if (tempPath.equals("")) {
                    tileArray[z][t - 1] = new Node(0, newBoard.getBoard()[z][t].getNum() + "R");
                    if (newBoard.getBoard()[x][y].getPath().equals("")) {
                        newBoard.setPath(newBoard.getBoard()[z][t - 1].getPath());
                    } else {
                        newBoard.setPath(newBoard.getBoard()[x][y].getPath() + "-" + newBoard.getBoard()[z][t - 1].getPath() + "R");
                    }

                } else {
                    tileArray[z][t - 1] = new Node(0, tempPath + "-" + newBoard.getBoard()[z][t].getNum() + "R");
                    newBoard.setPath(newBoard.getBoard()[z][t - 1].getPath() + newBoard.getBoard()[z][t].getPath());

                }
                tileArray[z][t].setFather("t-1");
                newBoard.setCost(currentCost + 5);

                tileArray[z][t - 1].setPrice(tempPrice + 5);
                if (!hashLoopID.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,winBoard);
                    newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
                }
            }

            if (z - 1 >= 0 && !"z+1".equals(currentBoard.getBoard()[z - 1][t].getFather())) {
                GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
                Node[][] tileArray = newBoard.getBoard();
                int currentCost = currentBoard.getCost();

                // String tempPath = newBoard.getBoard()[z][t].getPath();
                String tempPath = currentBoard.getPath();
                int tempPrice = newBoard.getBoard()[z][t].getPrice();
                tileArray[z][t] = tileArray[z - 1][t];
                if (tempPath.equals("")) {
                    tileArray[z - 1][t] = new Node(0, newBoard.getBoard()[z][t].getNum() + "D");
                    if (newBoard.getBoard()[x][y].getPath().equals("")) {
                        newBoard.setPath(newBoard.getBoard()[z - 1][t].getPath());
                    } else {
                        newBoard.setPath(newBoard.getBoard()[x][y].getPath() + "-" + newBoard.getBoard()[z - 1][t].getPath() + "D");

                    }
                } else {

                    tileArray[z - 1][t] = new Node(0, tempPath + "-" + newBoard.getBoard()[z][t].getNum() + "D");
                    newBoard.setPath(newBoard.getBoard()[z - 1][t].getPath() + newBoard.getBoard()[z][t].getPath());
                }
                tileArray[z][t].setFather("z-1");
                newBoard.setCost(currentCost + 5);

                tileArray[z - 1][t].setPrice(tempPrice + 5);
                if (!hashLoopID.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,winBoard);
                    newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
                }
            }
        }
        return operations;
    }

    private void saveToFile(String fileName, GameBoard solve,String num, String time) {
        try
        {
            PrintWriter pw = new PrintWriter(fileName);
            pw.write(solve.getPath());
            pw.write("\n");
            pw.write(num);
            pw.write("\n");
            pw.write("Cost: " + solve.getCost());
            pw.write("\n");
            if(this.withTime)
                pw.write(time);
            pw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void printOpenList(Set<String> nodes) {
        for(String b : nodes) {
            System.out.print(b + ", ");
        }
        System.out.println();
    }

    private void saveToFileNoFound(String fileName,String path,String num) {
        try
        {
            PrintWriter pw = new PrintWriter(fileName);
            pw.write(path);
            pw.write("\n");
            pw.write(num);
            pw.write("\n");
            pw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
