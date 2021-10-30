import java.util.Arrays;

public class GameBoard implements Comparable<GameBoard> {
    private Node[][] myBoard;
    private Node[][] goalBoard;
    private int blankRow; // index of empty blank
    private int blankCol; // index of empty blank
    private boolean state;//true == 1 missing block, false == 2 missing blocks.
    private String path; // path of this board
    int depth = 0;
    int huristicsResult = 0; //used for huristics algorithms - return the result of the huristic function.
    int finalHuristicAndCost = 0; // sum of huristicsResult and cost.
    String out = ""; //used in IDA*
    int count = 1; //help for decleare what game state we play.
    int secondCount = 1;
    private boolean isCutOff = false; // we will use this for DFID algorithm.
    boolean finalGoal = false;
    private boolean markedOut;
    private int cost = 0; //cost of this board so far.
    private boolean withTime;
    private boolean isListPrint;


    public int compareTo(GameBoard other) {
        if (this.finalHuristicAndCost == other.finalHuristicAndCost) {
            return 0;
        } else if (this.finalHuristicAndCost > other.finalHuristicAndCost)
            return 1;
        else
            return -1;
    }

    public GameBoard(Node[][] board, Node[][] GoalBoard, boolean time, boolean open, boolean state) {
        this.myBoard = board;
        this.goalBoard = GoalBoard;
        this.state = state;
        this.withTime = time;
        this.isListPrint = open;
        find2BlanksIndex();
        if (count == 2) {
            this.state = false;
        } else
            findBlankIndex();
        this.path = "";
        this.markedOut = false;

    }

    public GameBoard(Node[][] tempBoard, String path) {
        this.setPath(path);
        int x = tempBoard.length;
        int y = tempBoard[0].length;
        myBoard = new Node[x][y];

        for (int i = 0; i < tempBoard.length; i++) {
            for (int j = 0; j < tempBoard[i].length; j++) {
                if (tempBoard[i][j].getNum() == 0) { //found the empty blank
                    this.blankRow = i;
                    this.blankCol = j;
                    secondCount++;
                }
                myBoard[i][j] = tempBoard[i][j];
            }
        }
        this.markedOut = false;

    }

    public GameBoard(Node[][] tempBoard) {
        int x = tempBoard.length;
        int y = tempBoard[0].length;
        myBoard = new Node[x][y];
        int insideCount2 = 1;
        for (int i = 0; i < tempBoard.length; i++) {
            for (int j = 0; j < tempBoard[i].length; j++) {
                if (tempBoard[i][j].getNum() == 0) { //found the empty blank
                    if (insideCount2 == 1) {
                        this.blankRow = i;
                        this.blankCol = j;
                        this.setPath(tempBoard[i][j].getPath());
                        this.path = tempBoard[i][j].getPath();
                        insideCount2++;
                        secondCount++;
                    } else {
                        String tempPath = this.getPath();
                        this.setPath(tempPath + "-" + tempBoard[i][j].getPath());
                    }
                }
                myBoard[i][j] = tempBoard[i][j];
            }
        }
        this.markedOut = false;
    }

    public boolean getisCutOff() {
        return this.isCutOff;
    }

    public void setIsCutOff(boolean cutOff) {
        this.isCutOff = cutOff;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost += cost;
    }

    public int getRow() {
        return this.blankRow;
    }

    public void setFindTheGoal(boolean what) {
        this.finalGoal = what;
    }

    public int getCol() {
        return this.blankCol;
    }

    public GameBoard copy(String path) {
        GameBoard newBoard = new GameBoard(myBoard, path);
        return newBoard;
    }

    public boolean isMarkedOut() {
        return markedOut;
    }

    public void setMarkedOut(boolean markedOut) {
        this.markedOut = markedOut;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Node[][] getBoard() {
        return this.myBoard;
    }

    public Node[][] getGoalBoard() {
        return this.goalBoard;
    }

    public boolean getStateBoard() {
        return this.state;
    }

    public void PrintBoard() {
        for (int i = 0; i < this.myBoard.length; i++) {
            for (int j = 0; j < this.myBoard[0].length; j++) {
                System.out.print(myBoard[i][j].getNum() + " ");
            }
            System.out.println(" ");
        }
    }

    public boolean checkFinish(Node[][] goalBoard2) {
        for (int i = 0; i < myBoard.length; i++) {
            for (int j = 0; j < myBoard[0].length; j++) {
                if (myBoard[i][j].getNum() == goalBoard2[i][j].getNum()) {
                    continue;
                } else {
                    return false;
                }
            }
        }
        return true;
    }


    public String searchForBoardUniqueKey() {
        String id = "";
        for (int i = 0; i < myBoard.length; i++) {
            for (int j = 0; j < myBoard[i].length; j++) {
                id = id + myBoard[i][j].getNum() + ",";
            }
        }
        //this.myBoardIdendifer = id;
        return id;
    }

    public int[] findBlankIndex() {
        int[] index = new int[2];
        boolean found = false;
        for (int i = 0; i < myBoard.length && found == false; i++) {
            for (int j = 0; j < myBoard[0].length; j++) {
                if (myBoard[i][j].getNum() == 0) {
                    index[0] = i;
                    index[1] = j;
                    this.blankRow = i;
                    this.blankCol = j;
                    found = true;
                }
            }
        }
        if (found == false) {
            index[0] = -1;
        }
        return index;
    }

    public int[] find2BlanksIndex() {
        int insideCount = 1;
        int[] indexs = new int[4];
        boolean found = false;
        for (int i = 0; i < myBoard.length && !found; i++) {
            for (int j = 0; j < myBoard[0].length; j++) {
                if (myBoard[i][j].getNum() == 0) {
                    if (insideCount == 1) {
                        indexs[0] = i;
                        indexs[1] = j;
                        count++;
                        insideCount++;
                    } else {
                        indexs[2] = i;
                        indexs[3] = j;
                        found = true;
                    }
                }
            }
        }

        return indexs;
    }

    public boolean findGameState() {
        int insideCount = 1;
        int[] indexs = new int[4];
        boolean found = false; //state 1 .
        for (int i = 0; i < myBoard.length && !found; i++) {
            for (int j = 0; j < myBoard[0].length; j++) {
                if (myBoard[i][j].getNum() == 0) {
                    if (insideCount == 1) {
                        indexs[0] = i;
                        indexs[1] = j;
                        count++;
                        insideCount++;
                    } else {
                        indexs[2] = i;
                        indexs[3] = j;
                        found = true;
                    }
                }
            }
        }

        return found;
    }


    private Node[][] copyMat(Node[][] mat) {
        Node[][] result = new Node[mat.length][mat[0].length];
        //copy the matrix
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                result[i][j] = mat[i][j];
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameBoard board1 = (GameBoard) o;
        return Arrays.deepEquals(myBoard, board1.getBoard());
    }

    public void setHuristics(int H) {
        this.huristicsResult = H;
    }

    public void setFcost(int F) {
        this.finalHuristicAndCost = F;
    }

    public String getOut() {
        return this.out;
    }

    public void setOut(String s) {
        this.out = s;
    }
}
