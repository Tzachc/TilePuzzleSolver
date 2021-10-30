import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DFID {

    private GameBoard st;
    private Node[][] goalBoard;
    private GameBoard goalBoard2;
    boolean gameState;
    private String path = ""; // from the goal state
    private boolean isWithTime, isWithOpen, hasResult = false;
    private int num = 1, cost = 0; // cost is the weight of the goal state
    private long timeToGoal = 0;
    GameBoard lastSearch;
    int counter =0;
    boolean found=false;

    public DFID(GameBoard startBoard, GameBoard goalBoard, boolean time, boolean open, boolean state) {
        this.st = startBoard;
        this.isWithTime = time;
        this.isWithOpen = open;
        this.goalBoard2 = goalBoard;
        this.gameState = state;
        //this.goalBoard = startBoard.getGoalBoard();
        if(st.checkFinish(goalBoard2.getBoard())){
            System.out.println("Num: 1");
            System.out.println("Cost: " + st.getCost());
        }
        else {
            DFID_Solve(startBoard);
        }
    }

    public boolean DFID_Solve(GameBoard myBoard) {
        long START = System.currentTimeMillis();//Time
        for(int i=1; i<Integer.MAX_VALUE; i++){ //first iteration i=1.
            Set<String> hash = new HashSet<String>();
            GameBoard result = limitedDFID(myBoard, i , hash);
            if(!result.getisCutOff()){
                if(result.finalGoal){
                    found = true;
                    int[] arr = new int[2];
                    arr = result.findBlankIndex();
                    int x = arr[0];
                    int y = arr[1];
                    String path = result.getBoard()[x][y].getPath();
                    //System.out.println(result.getPath());
                   // System.out.println("Num :" + num);
                   // System.out.println("Cost: " + result.getCost());
                    String Num = "Num: " + num;
                    long END =  System.currentTimeMillis();
                    float seconds = (END-START) / 1000F;
                    String time = seconds + " seconds";
                    if(isWithTime) {
                       // System.out.println(seconds + " seconds");
                    }
                    saveToFile("output.txt",result,Num,time);
                    return found;
                }
                else{
                    if(!found){
                        String noPath = "no path";
                        String Num = "Num: " + num;
                        saveToFileNoFound("output.txt",noPath,Num);
                    }
                    return found;
                }
            }
        }
        return found;
    }

    private GameBoard limitedDFID(GameBoard startBoard, int limit,Set<String> hash) {
        counter++;
        if(startBoard.checkFinish(goalBoard2.getBoard())){
            startBoard.setFindTheGoal(true);
            this.path = startBoard.getPath();
            return startBoard;
        }
        if(limit == 0){
            startBoard.setIsCutOff(true);
            return startBoard;
        }
        else{

            hash.add(startBoard.searchForBoardUniqueKey());
            if(isWithOpen) printOpenList(hash);

            startBoard.setIsCutOff(false);
            ArrayList<GameBoard> childrens = createAllOperations(startBoard,hash);
            if(childrens.isEmpty()){
                return startBoard;
            }
            limit--;
            for(GameBoard b : childrens){
                this.num++;
                this.lastSearch = limitedDFID(b, limit,hash);
                if(lastSearch.getisCutOff()){
                    lastSearch.setIsCutOff(true);
                }
                else if (lastSearch.finalGoal){
                    lastSearch.setFindTheGoal(true);
                    return lastSearch;
                }
            }
            hash.remove(startBoard.searchForBoardUniqueKey());
            if(lastSearch.getisCutOff()){
                return lastSearch;
            }
            else {
                return startBoard;
            }
        }
    }
    /*
    helper method that get all the possible operation-meaning we check if you can move
    left,up,right,down . if possible we add it to an Arraylist.
    if we in game mode 2 (with 2 blanks) then we also check the cases when
    we can move 2 blanks together.

     */
    public ArrayList<GameBoard> createAllOperations(GameBoard currentBoard,Set<String> currentPath) {
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
        if(gameState) {
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
                        if (!currentPath.contains(DOWN.searchForBoardUniqueKey())) {
                            operations.add(DOWN);
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
                        if (!currentPath.contains(UP.searchForBoardUniqueKey())) {
                            operations.add(UP);
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

                        if (!currentPath.contains(LEFT.searchForBoardUniqueKey())) {
                            operations.add(LEFT);
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
                        if (!currentPath.contains(RIGHT.searchForBoardUniqueKey())) {
                            operations.add(RIGHT);
                        }
                    }
                }
            }
        }
        if (y + 1 < boardY && !"y-1".equals(currentBoard.getBoard()[x][y+1].getFather())) {
            GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
            Node[][] tileArray = newBoard.getBoard();
            // String tempPath = newBoard.getBoard()[x][y].getPath();
            String tempPath = currentBoard.getPath();
            int currentCost = currentBoard.getCost();

            int tempPrice = newBoard.getBoard()[x][y].getPrice();

            tileArray[x][y] = tileArray[x][y + 1];
            if (tempPath.equals("")) {
                tileArray[x][y + 1] = new Node(0, newBoard.getBoard()[x][y].getNum() + "L");
                newBoard.setPath(newBoard.getBoard()[x][y].getNum() + "L" +newBoard.getBoard()[z][t].getPath());
            } else {
                tileArray[x][y + 1] = new Node(0, currentBoard.getPath() + "-" + newBoard.getBoard()[x][y].getNum() + "L");
                // newBoard.setPath(newBoard.getBoard()[x][y+1].getPath() + newBoard.getBoard()[z][t].getPath());
                newBoard.setPath(newBoard.getBoard()[x][y+1].getPath() );

            }
            tileArray[x][y].setFather("y+1");

            tileArray[x][y + 1].setPrice(tempPrice + 5);
            newBoard.setCost(currentCost+5);
            if (!currentPath.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);

            }
        }

        if (x + 1 < boardX && !"x-1".equals(currentBoard.getBoard()[x+1][y].getFather())) {
            GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
            Node[][] tileArray = newBoard.getBoard();
            //String tempPath = newBoard.getBoard()[x][y].getPath();
            String tempPath = currentBoard.getPath();
            int currentCost = currentBoard.getCost();
            int tempPrice = newBoard.getBoard()[x][y].getPrice();

            tileArray[x][y] = tileArray[x + 1][y];
            if (tempPath.equals("")) {
                tileArray[x + 1][y] = new Node(0, newBoard.getBoard()[x][y].getNum() + "U");
                newBoard.setPath(newBoard.getBoard()[x][y].getNum() + "U" + "-" +newBoard.getBoard()[z][t].getPath());

            } else {
                tileArray[x + 1][y] = new Node(0, currentBoard.getPath()+ "-" + newBoard.getBoard()[x][y].getNum() + "U");
                // newBoard.setPath(newBoard.getBoard()[x+1][y].getPath() + "-" +newBoard.getBoard()[z][t].getPath());
                newBoard.setPath(newBoard.getBoard()[x+1][y].getPath());

            }
            tileArray[x][y].setFather("x+1");

            tileArray[x + 1][y].setPrice(tempPrice + 5);
            newBoard.setCost(currentCost+5);

            if (!currentPath.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);

            }
        }
        if (y - 1 >= 0 && !"y+1".equals(currentBoard.getBoard()[x][y-1].getFather())) {
            GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
            Node[][] tileArray = newBoard.getBoard();
            //String tempPath = newBoard.getBoard()[x][y].getPath();
            int currentCost = currentBoard.getCost();

            String tempPath = currentBoard.getPath();
            int tempPrice = newBoard.getBoard()[x][y].getPrice();

            tileArray[x][y] = tileArray[x][y - 1];
            if (tempPath.equals("")) {
                tileArray[x][y - 1] = new Node(0, newBoard.getBoard()[x][y].getNum() + "R");
                newBoard.setPath(newBoard.getBoard()[x][y].getNum() + "R"  +newBoard.getBoard()[z][t].getPath());

            } else {
                tileArray[x][y - 1] = new Node(0, currentBoard.getPath()+ "-" + newBoard.getBoard()[x][y].getNum() + "R");
                // newBoard.setPath(newBoard.getBoard()[x][y-1].getPath() + "-" +newBoard.getBoard()[z][t].getPath());
                newBoard.setPath(newBoard.getBoard()[x][y-1].getPath() );


            }
            tileArray[x][y].setFather("y-1");
            newBoard.setCost(currentCost+ 5);

            tileArray[x][y - 1].setPrice(tempPrice + 5);
            if (!currentPath.contains(newBoard.searchForBoardUniqueKey())) {
                operations.add(newBoard);

            }
        }


        if (x - 1 >= 0 && !"x+1".equals(currentBoard.getBoard()[x-1][y].getFather())) {
            GameBoard newBoard = currentBoard.copy(currentBoard.getPath());
            Node[][] tileArray = newBoard.getBoard();
            // String tempPath = newBoard.getBoard()[x][y].getPath();
            String tempPath = currentBoard.getPath();
            int currentCost = currentBoard.getCost();

            int tempPrice = newBoard.getBoard()[x][y].getPrice();
            tileArray[x][y] = tileArray[x - 1][y];
            if (tempPath.equals("")) {
                tileArray[x - 1][y] = new Node(0, newBoard.getBoard()[x][y].getNum() + "D");
                newBoard.setPath(newBoard.getBoard()[x][y].getNum() + "D" + "-" +newBoard.getBoard()[z][t].getPath());
            } else {
                tileArray[x - 1][y] = new Node(0, currentBoard.getPath()+"-"  + newBoard.getBoard()[x][y].getNum() + "D");
                //  newBoard.setPath(newBoard.getBoard()[x-1][y].getPath() + "-" +newBoard.getBoard()[z][t].getPath());
                newBoard.setPath(newBoard.getBoard()[x-1][y].getPath());

            }
            tileArray[x][y].setFather("x-1");
            newBoard.setCost(currentCost+5);

            tileArray[x - 1][y].setPrice(tempPrice + 5);
            if (!currentPath.contains(newBoard.searchForBoardUniqueKey()) && newBoard.getBoard()[x][y].getNum() != 0) {
                operations.add(newBoard);

            }
        }

//---------------------------------------------------------------------------
        if(gameState) {
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
                    newBoard.setPath(newBoard.getBoard()[z][t + 1].getPath() + "-" + newBoard.getBoard()[z][t].getPath());

                }
                tileArray[z][t].setFather("t+1");
                newBoard.setCost(currentCost + 5);

                tileArray[z][t + 1].setPrice(tempPrice + 5);
                if (!currentPath.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);

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
                    newBoard.setPath(newBoard.getBoard()[z + 1][t].getPath() + "-" + newBoard.getBoard()[z][t].getPath());

                }
                tileArray[z][t].setFather("z+1");

                tileArray[z + 1][t].setPrice(tempPrice + 5);
                newBoard.setCost(currentCost + 5);

                if (!currentPath.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);

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
                if (!currentPath.contains(newBoard.searchForBoardUniqueKey())) {
                    operations.add(newBoard);

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
                if (!currentPath.contains(newBoard.searchForBoardUniqueKey()) && newBoard.getBoard()[z][t].getNum() != 0) {
                    operations.add(newBoard);

                }
            }
        }

        return operations;
    }
    public void printOpenList(Set<String> openList) {
        for(String s : openList) {
            System.out.print(s + ", ");
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
            if(this.isWithTime)
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
