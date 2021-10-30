
public class Node {
    private int num;
    private int price;
    private String path;
    private String father;

    public Node(){
        this.num = 0;
        this.price = 5;
        this.path = "";
    }
    public Node (int num){
        this.num = num;
        this.price = 5;
        this.path = "";
    }
    public Node (int num,String path){
        this.num = num;
        this.price = 5;
        this.path = path;
    }
    public int getNum(){
        return this.num;
    }
    public int getPrice(){return this.price;}
    public void setPrice(int price){this.price = price;}
    public String getFather(){return this.father;}
    public void setFather(String father){
        this.father = father;
    }
    public String getPath(){return path;}
    public void setPath(String path){this.path = path;}
    public void setNum(int num){
        this.num = num;
    }
}
