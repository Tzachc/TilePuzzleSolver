import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Ex1 {
    static boolean TimeRun = false;
    static boolean ListOpen = false;
    static String algoWant; // chosen algo
    static String indexX;
    static int numRow; //index of blank X
    static int numCols; //index of blank Y
    static Node[][] TileBoard;
    static GameBoard OriginalBoard;
    static Node[][] GoalBoard;
    static Boolean gameState = true; // true == 1 missing block, false == 2 missing blocks.

    public static Node[][] readFromFile() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("input2.txt"));
            algoWant = reader.readLine();
            String time = reader.readLine();
            if (time.equals("with time")) {
                TimeRun = true;
            }
            if (reader.readLine().equals("with open")) {
                ListOpen = true;
            }
            indexX = reader.readLine();
            numRow = Integer.parseInt(indexX.substring(0, indexX.indexOf('x')));
            numCols = Integer.parseInt(indexX.substring(indexX.indexOf('x') + 1, indexX.length()));
            int counter = 0;
            TileBoard = new Node[numRow][numCols];
            for (int i = 0; i < numRow; i++) {
                String[] currentRow = reader.readLine().split(",");
                for (int j = 0; j < numCols; j++) {
                    if (currentRow[j].charAt(0) != '_') {
                        int number = Integer.parseInt(currentRow[j]);
                        TileBoard[i][j] = new Node(number);
                        counter++;
                    } else {
                        TileBoard[i][j] = new Node();
                    }
                }
            }
            boardStateMode(counter);
            String goal = reader.readLine();
            GoalBoard = new Node[numRow][numCols];
            for (int i = 0; i < numRow; i++) {
                String[] currentRow = reader.readLine().split(",");
                for (int j = 0; j < numCols; j++) {
                    if (currentRow[j].charAt(0) != '_') {
                        int number = Integer.parseInt(currentRow[j]);
                        GoalBoard[i][j] = new Node(number);
                        //counter++;
                    } else {
                        GoalBoard[i][j] = new Node();
                    }
                }
            }
            //OriginalBoard = new GameBoard(TileBoard, GoalBoard, numRow, numCols, TimeRun, ListOpen, gameState);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return TileBoard;
    }

    public static void main(String[] args) {
     Node [][] board = readFromFile();
     Node [][] goalBoard = GoalBoard;
     GameBoard goalBoard2 = new GameBoard(GoalBoard);
     GameBoard myBoard = new GameBoard(board,goalBoard,TimeRun, ListOpen,gameState);
    boolean gameState = myBoard.findGameState();

     switch (algoWant){
         case "BFS":
             BFS run_BFS = new BFS(myBoard,goalBoard2,TimeRun,ListOpen,gameState);

             break;
         case "DFID":
             DFID run_DFID = new DFID(myBoard,goalBoard2,TimeRun,ListOpen,gameState);
           //  run_DFID.DFID_Solve(myBoard);
             break;
         case "A*":
             Astar run_Astar = new Astar(myBoard,goalBoard2,TimeRun,ListOpen,gameState);
             break;
         case "IDA*":
             IDAstar run_IDAstar = new IDAstar(myBoard,goalBoard2,TimeRun,ListOpen,gameState);
             break;
         case "DFBnB":
             DFBnB run_Dfbnb = new DFBnB(myBoard,goalBoard2,TimeRun,ListOpen,gameState);
             break;
     }
    }
/*
helper method that print the current board.
 */
    public static void PrintBoard(Node[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                System.out.print(board[i][j].getNum() + " ");
            }
            System.out.println(" ");
        }
    }

    public static void boardStateMode(int count) {
        int totalNodes = numCols * numRow;
        if (totalNodes != count) {
            gameState = false; //change to state 2. (missing 2 blocks).
        }
    }


}
