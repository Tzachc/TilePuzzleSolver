import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

public class Astar {
    private ManhattanDistance heuristic = new ManhattanDistance();
    GameBoard goalBoard;
    Set<String> closedList = new HashSet<String>(); //for nodes that we open.
    Set<GameBoard> openList = new HashSet<GameBoard>(); //for nodes that we discover but not open yet.
    PriorityQueue<GameBoard> pQueue = new PriorityQueue<GameBoard>();
    GameBoard root;
    boolean withTime;// print with time
    boolean isPrintOpen; // print the list
    boolean gameState; // what state we in (one blank or 2 blanks).
    boolean found=false;

    public Astar(GameBoard myBoard, GameBoard goal, boolean chooseWithTime, boolean chooseWithOpen, boolean state) {
        this.goalBoard = goal;
        this.isPrintOpen = chooseWithOpen;
        this.withTime = chooseWithTime;
        this.gameState = state;
        this.root = myBoard;

        if (root.checkFinish(goalBoard.getBoard())) {
            System.out.println("Num: 1");
        } else {
            Astar22(myBoard);
        }
    }

    public boolean Astar22(GameBoard myBoard) {
        pQueue.add(myBoard);
        long START = System.currentTimeMillis();//Time

        while (!pQueue.isEmpty()) {
            if (isPrintOpen) printOpenList(pQueue);
            GameBoard currentBoard = pQueue.poll();

            if (currentBoard.checkFinish(goalBoard.getBoard())) {
                found = true;
               // System.out.println(currentBoard.getPath());
                String num = "Num: " + String.valueOf(openList.size() + closedList.size());
               // System.out.println(num);
               // System.out.println("Cost: " + currentBoard.getCost());
                long END =  System.currentTimeMillis();
                float seconds = (END-START) / 1000F;
                String time = seconds + " seconds";
                String Num = "Num: " + String.valueOf(openList.size() + closedList.size());
                saveToFile("output.txt",currentBoard,Num,time);
                if(withTime) {
                 //   System.out.println(seconds + " seconds");
                }
                return found;
            } else {
                openList.remove(currentBoard);
                closedList.add(currentBoard.searchForBoardUniqueKey());
                ArrayList<GameBoard> childrens = createAllOperations(currentBoard);
                for (GameBoard b : childrens) {
                    if (!closedList.contains(b.searchForBoardUniqueKey()) && (!openList.contains(b.searchForBoardUniqueKey()))) {
                        pQueue.add(b);
                    } else if (openList.contains(b)) {
                        GameBoard temp = findBoard(b);
                        int b_price = b.finalHuristicAndCost;
                        int temp_price = temp.finalHuristicAndCost;
                        if (temp_price > b_price && temp_price>0 && b_price>0) {
                            openList.remove(temp);
                            openList.add(b);
                        }
                    }
                }
            }
        }
        if(!found){
            String noPath = "no path";
            String Num = "Num: " + String.valueOf(openList.size() + closedList.size());
            saveToFileNoFound("output.txt",noPath,Num);
        }
        return found;
    }

    public GameBoard findBoard(GameBoard b) {
        for (Iterator<GameBoard> it = openList.iterator(); it.hasNext(); ) {
            GameBoard f = it.next();
            if (f.searchForBoardUniqueKey().equals(b.searchForBoardUniqueKey()))
                return f;
        }
        return null;
    }
    public GameBoard findBoard2(GameBoard b){
        for(GameBoard board : openList){
            if(board.searchForBoardUniqueKey().equals(b.searchForBoardUniqueKey())){
                return board;
            }
        }
        return null;
    }
    /*
    helper method that get all the possible operation-meaning we check if you can move
    left,up,right,down . if possible we add it to an Arraylist.
    if we in game mode 2 (with 2 blanks) then we also check the cases when
    we can move 2 blanks together.

     */
    public ArrayList<GameBoard> createAllOperations(GameBoard currentBoard) {
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
                        if (!closedList.contains(DOWN.searchForBoardUniqueKey()) && !openList.contains(DOWN.searchForBoardUniqueKey())) {
                            operations.add(DOWN);
                            DOWN.huristicsResult = heuristic.getHuristicCost(DOWN,goalBoard);
                            DOWN.finalHuristicAndCost = DOWN.huristicsResult + DOWN.getCost();
                            openList.add(DOWN);
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
                        if (!closedList.contains(UP.searchForBoardUniqueKey()) && !openList.contains(UP.searchForBoardUniqueKey())) {
                            operations.add(UP);
                            UP.huristicsResult = heuristic.getHuristicCost(UP,goalBoard);
                            UP.finalHuristicAndCost = UP.huristicsResult + UP.getCost();

                            openList.add(UP);
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

                        if (!closedList.contains(LEFT.searchForBoardUniqueKey()) && !openList.contains(LEFT.searchForBoardUniqueKey())) {
                            operations.add(LEFT);

                            LEFT.huristicsResult = heuristic.getHuristicCost(LEFT,goalBoard);
                            LEFT.finalHuristicAndCost = LEFT.huristicsResult + LEFT.getCost();
                            openList.add(LEFT);
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
                        if (!closedList.contains(RIGHT.searchForBoardUniqueKey()) && !openList.contains(RIGHT.searchForBoardUniqueKey())) {
                            operations.add(RIGHT);
                            RIGHT.huristicsResult = heuristic.getHuristicCost(RIGHT,goalBoard);
                            RIGHT.finalHuristicAndCost = RIGHT.huristicsResult + RIGHT.getCost();
                            openList.add(RIGHT);
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
                newBoard.setPath(newBoard.getBoard()[x][y + 1].getPath());

            }
            tileArray[x][y].setFather("y+1");

            tileArray[x][y + 1].setPrice(tempPrice + 5);
            newBoard.setCost(currentCost + 5);
            if (!closedList.contains(newBoard.searchForBoardUniqueKey()) && !openList.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
                newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
                openList.add(newBoard);

            }
        }

        if (x + 1 < boardX && !"x-1".equals(currentBoard.getBoard()[x + 1][y].getFather())) {
            GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
            Node[][] tileArray = newBoard.getBoard();
            String tempPath = currentBoard.getPath();
            int currentCost = currentBoard.getCost();
            int tempPrice = newBoard.getBoard()[x][y].getPrice();

            tileArray[x][y] = tileArray[x + 1][y];
            if (tempPath.equals("")) {
                tileArray[x + 1][y] = new Node(0, newBoard.getBoard()[x][y].getNum() + "U");
                newBoard.setPath(newBoard.getBoard()[x][y].getNum() + "U" + "-" + newBoard.getBoard()[z][t].getPath());

            } else {
                tileArray[x + 1][y] = new Node(0, currentBoard.getPath() + "-" + newBoard.getBoard()[x][y].getNum() + "U");
                newBoard.setPath(newBoard.getBoard()[x + 1][y].getPath());

            }
            tileArray[x][y].setFather("x+1");

            tileArray[x + 1][y].setPrice(tempPrice + 5);
            newBoard.setCost(currentCost + 5);

            if (!closedList.contains(newBoard.searchForBoardUniqueKey()) && !openList.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
                newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
                openList.add(newBoard);

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
                newBoard.setPath(newBoard.getBoard()[x][y - 1].getPath());


            }
            tileArray[x][y].setFather("y-1");
            newBoard.setCost(currentCost + 5);

            tileArray[x][y - 1].setPrice(tempPrice + 5);
            if (!closedList.contains(newBoard.searchForBoardUniqueKey()) && !openList.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
                newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
                openList.add(newBoard);

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
                newBoard.setPath(newBoard.getBoard()[x - 1][y].getPath());

            }
            tileArray[x][y].setFather("x-1");
            newBoard.setCost(currentCost + 5);

            tileArray[x - 1][y].setPrice(tempPrice + 5);
            if (!closedList.contains(newBoard.searchForBoardUniqueKey()) && !openList.contains(newBoard.searchForBoardUniqueKey()) && newBoard.getBoard()[x][y].getNum() != 0) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
                newBoard.finalHuristicAndCost = newBoard.huristicsResult +newBoard.getCost();
                openList.add(newBoard);

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
                if (!closedList.contains(newBoard.searchForBoardUniqueKey()) && !openList.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
                    newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
                    openList.add(newBoard);

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

                if (!closedList.contains(newBoard.searchForBoardUniqueKey()) && !openList.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
                    newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
                    openList.add(newBoard);

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
                if (!closedList.contains(newBoard.searchForBoardUniqueKey()) && !openList.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
                    newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
                    openList.add(newBoard);

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
                if (!closedList.contains(newBoard.searchForBoardUniqueKey()) && !openList.contains(newBoard.searchForBoardUniqueKey()) && newBoard.getBoard()[z][t].getNum() != 0) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
                    newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();
                    openList.add(newBoard);

                }
            }
        }

        return operations;
    }

    public void printOpenList(PriorityQueue<GameBoard> queue) {
        for (GameBoard b : queue) {
            System.out.print(b.searchForBoardUniqueKey() + ", ");
        }
        System.out.println();
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
