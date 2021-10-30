public class ManhattanDistance {

    public int getHuristicCost(GameBoard B, GameBoard G) {
        int huristicCount = 0;
        int x_node, y_node;
        int placeNode;
        int boardX = B.getBoard().length;
        int boardY = B.getBoard()[0].length;
        for (int i = 0; i < boardX; i++) {
            for (int j = 0; j < boardY; j++) {
                if ((B.getBoard()[i][j].getNum() != G.getBoard()[i][j].getNum()) && (G.getBoard()[i][j].getNum() != 0)) {
                    x_node = i;
                    y_node = j;
                    placeNode = G.getBoard()[i][j].getNum();
                    huristicCount += getHuristicCostHelp(x_node, y_node, placeNode, B);
                }
            }
        }
        return huristicCount;
    }

    public int getHuristicCostHelp(int x_node, int y_node, int placeNode, GameBoard B) {
        int huristicCost;
        int boardX = B.getBoard().length;
        int boardY = B.getBoard()[0].length;
        for (int i = 0; i < boardX; i++) {
            for (int j = 0; j < boardY; j++) {
                if (B.getBoard()[i][j].getNum() == placeNode) {
                    huristicCost = caculateCost(i,j,x_node,y_node);
                    return huristicCost;
                }
            }
        }
        return 0;
    }

    public int caculateCost(int final_x,int final_y, int x_node,int y_node){
        int x2_x1 = (Math.abs(final_x - x_node));
        int y2_y1 = (Math.abs(final_y - y_node));
        int huristicCost2 = 3*(x2_x1 + y2_y1);
        return huristicCost2;
    }


}
