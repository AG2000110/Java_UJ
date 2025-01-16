package uj.wmii.pwj.zd9;

public class Point {
    private Integer x;
    private Integer y;

    public Point(Integer x1, Integer y2) {
        x = x1;
        y = y2;
    }

    public Integer getX() {return x;}
    public Integer getY() {return y;}

    public Point (String stringPoint){
        x = stringPoint.charAt(0) - 'A';
        y = Integer.parseInt(stringPoint.trim().substring(1));
    }

    public String toString(){
        return "" + (char) ( x + 'A') + y.toString();
    }
}
