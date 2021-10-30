import java.io.PrintWriter;
import java.util.*;

public class IDAstar {
    GameBoard root;
    Set<GameBoard> hash = new HashSet<GameBoard>(); // (L)  nodes that found but not open
    Stack<GameBoard> myStack = new Stack<GameBoard>(); // (H) stack
    Stack<String> parent = new Stack<String>(); // for not searching the parent board
    boolean isPrintOpen;
    GameBoard goalBoard;
    boolean withTime;
    boolean gameState;
    boolean found=false;
    private ManhattanDistance heuristic = new ManhattanDistance();

    public IDAstar(GameBoard myBoard, GameBoard goalBoard, boolean withTime, boolean withPrint, boolean state) {
        this.isPrintOpen = withPrint;//
        this.withTime = withTime;
        this.gameState = state;
        this.root = myBoard;
        this.goalBoard = goalBoard;
        if (root.checkFinish(goalBoard.getBoard())) {
            System.out.println("Num: 1");
            System.out.println("Cost: " + root.getCost());
        } else
            runIdAstar(myBoard);
    }

    public boolean runIdAstar(GameBoard myBoard) {
        int num = 1;
        long START = System.currentTimeMillis();//Time
        int limit = heuristic.getHuristicCost(myBoard,goalBoard);
        myBoard.setHuristics(limit);
        while (limit < Integer.MAX_VALUE) {
            int minF = Integer.MAX_VALUE;
            hash.add(myBoard);
            parent.add(myBoard.searchForBoardUniqueKey());
            myStack.add(myBoard);

            while (!myStack.isEmpty()) {
                GameBoard current = myStack.pop();
                if (current.getOut().equals("out")) {
                    current.setOut("");
                    hash.remove(current);
                    parent.remove(current.searchForBoardUniqueKey());

                } else {
                    current.setOut("out");
                    myStack.add(current);
                    if (isPrintOpen) {
                        printOpenList(myStack);
                    }
                    ArrayList<GameBoard> childrens = createAllOperations(current);
                    for (GameBoard b : childrens) {
                        num++;
                        if (b.finalHuristicAndCost > limit) {
                            minF = Math.min(b.finalHuristicAndCost, minF);
                            continue;
                        }
                        if (hash.contains(b) && b.getOut().equals("out")) {
                            continue;
                        }
                        if (hash.contains(b) && !b.getOut().equals("out")) {

                            GameBoard temp = findBoard(b);
                            if (isBigger(temp,b)) {
                                continue;
                            } else {
                                continue;
                            }
                        }
                        if (b.checkFinish(goalBoard.getBoard())) {
                            found = true;
                           // System.out.println(b.getPath());
                           // System.out.println("Num: " + num);
                           // System.out.println("Cost: " + b.getCost());
                            long END =  System.currentTimeMillis();
                            float seconds = (END-START) / 1000F;
                            String time = seconds + " seconds";
                            String Num = "Num: " + num;
                            saveToFile("output.txt",b,Num,time);
                            if(withTime) {
                             //   System.out.println(seconds + " seconds");
                            }
                            return found;
                        } else {
                            myStack.add(b);
                            hash.add(b);
                            parent.add(b.searchForBoardUniqueKey());
                        }
                    }
                }
            }
            limit = minF;

        }
        if(!found){
            String noPath = "no path";
            String Num = "Num: " + num;
            saveToFileNoFound("output.txt",noPath,Num);
        }
        return found;

    }

    public boolean isBigger(GameBoard temp,GameBoard b){
        int tempFinal = temp.finalHuristicAndCost;
        int bFinal = b.finalHuristicAndCost;
        if(tempFinal > bFinal) {
            hash.remove(temp);
            parent.remove(temp.searchForBoardUniqueKey());
            myStack.remove(b);
            hash.add(b);
            parent.add(b.searchForBoardUniqueKey());
            return true;
        }
        return false;
    }
    public GameBoard findBoard(GameBoard b) {
        for (Iterator<GameBoard> it = hash.iterator(); it.hasNext(); ) {
            GameBoard f = it.next();
            if (f.searchForBoardUniqueKey().equals(b.searchForBoardUniqueKey()))
                return f;
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
                if (x + 1 < boardX && !currentBoard.getBoard()[x + 1][y].getFather().equals(currentBoard.getBoard()[x + 1][y].getNum() + "&" + currentBoard.getBoard()[z + 1][t].getNum() + "D")) {
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
                        if (!parent.contains(DOWN.searchForBoardUniqueKey())) {
                            operations.add(DOWN);
                            DOWN.huristicsResult = heuristic.getHuristicCost(DOWN,goalBoard);
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
                        if (!parent.contains(UP.searchForBoardUniqueKey())) {
                            operations.add(UP);
                            UP.huristicsResult = heuristic.getHuristicCost(UP,goalBoard);
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

                        if (!parent.contains(LEFT.searchForBoardUniqueKey())) {
                            operations.add(LEFT);
                            LEFT.huristicsResult = heuristic.getHuristicCost(LEFT,goalBoard);
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
                        if (!parent.contains(RIGHT.searchForBoardUniqueKey())) {
                            operations.add(RIGHT);
                            RIGHT.huristicsResult = heuristic.getHuristicCost(RIGHT,goalBoard);
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
            if (!parent.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
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
                newBoard.setPath(newBoard.getBoard()[x + 1][y].getPath());

            }
            tileArray[x][y].setFather("x+1");

            tileArray[x + 1][y].setPrice(tempPrice + 5);
            newBoard.setCost(currentCost + 5);

            if (!parent.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
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
                newBoard.setPath(newBoard.getBoard()[x][y].getNum() + "R"  + newBoard.getBoard()[z][t].getPath());

            } else {
                tileArray[x][y - 1] = new Node(0, currentBoard.getPath() + "-" + newBoard.getBoard()[x][y].getNum() + "R");
                // newBoard.setPath(newBoard.getBoard()[x][y-1].getPath() + "-" +newBoard.getBoard()[z][t].getPath());
                newBoard.setPath(newBoard.getBoard()[x][y - 1].getPath());


            }
            tileArray[x][y].setFather("y-1");
            newBoard.setCost(currentCost + 5);

            tileArray[x][y - 1].setPrice(tempPrice + 5);
            if (!parent.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
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
            if (!parent.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);
                newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
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
                if (!parent.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
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

                if (!parent.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
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
                if (!parent.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
                    newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();

                }
            }

            if (z - 1 >= 0 && !"z+1".equals(currentBoard.getBoard()[z - 1][t].getFather())) {
                GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
                Node[][] tileArray = newBoard.getBoard();
                int currentCost = currentBoard.getCost();
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
                if (!parent.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);
                    newBoard.huristicsResult = heuristic.getHuristicCost(newBoard,goalBoard);
                    newBoard.finalHuristicAndCost = newBoard.huristicsResult + newBoard.getCost();

                }
            }
        }

        return operations;
    }

    public void printOpenList(Stack<GameBoard> queue) {
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

