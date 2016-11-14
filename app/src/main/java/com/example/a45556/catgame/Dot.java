package com.example.a45556.catgame;

/**
 * Created by 45556 on 2016-11-8.
 */

public class Dot {
    int x,y;
    int stauts;

    public static final int STAUTS_OK = 1;
    public static final int STAUTS_NO = 0;
    public static final int STAUTS_IN = 2;
    public static final int STAUTS_IN2 = 3;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getStauts() {
        return stauts;
    }

    public void setStauts(int stauts) {
        this.stauts = stauts;
    }

    public Dot(int y, int x) {
        this.y = y;
        this.x = x;
        stauts = STAUTS_NO;
    }

    public void setXY(int x,int y){
        this.x = x;
        this.y = y;
    }

    public boolean isEdge(int COL,int ROW){
        if (getX()*getY() == 0 || getX()+1 == COL || getY()+1 == ROW){
            return true;
        }
        return false;
    }
}
