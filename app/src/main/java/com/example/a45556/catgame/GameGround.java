package com.example.a45556.catgame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.a45556.catgame.MainActivity.DLC;


/**
 * Created by 45556 on 2016-11-8.
 */

public class GameGround extends SurfaceView implements View.OnClickListener{

    int ROW = 9;
    int COL = 9;
    private int BLOCKS;
    private Dot matrix[][];
    Dot cat,cat2;

    public static GameGround gameGround;
    private static int failCount = 0;

    private MyListener myListener;
    private boolean justFirst;
    private Bitmap bmNO,bmOK,bmCat1,bmCat2,bg;
    private Sounder sounder;

    public GameGround(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameGround = this;
        getHolder().addCallback(callback);
        myListener = new MyListener(this);
        setOnTouchListener(myListener);
        bmNO = BitmapFactory.decodeResource(getResources(),R.drawable.trash);
        bmOK = BitmapFactory.decodeResource(getResources(),R.drawable.grass);
        bmCat1 = BitmapFactory.decodeResource(getResources(), R.drawable.cat1);
        bmCat2 = BitmapFactory.decodeResource(getResources(), R.drawable.cat2);
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        sounder = Sounder.getInstance();
        if (MainActivity.doubleCat)
            initGameD();
        else
            initGame();
    }

    public Dot getDot(int x,int y){                    //方便访问矩阵数组
        return matrix[x][y];
    }

    public int getDeviation(){
        if (mWidth > mHeight)
            return mHeight/2-4*WIDTH;
        else
            return mHeight/2-3*WIDTH;
    }

    private int WIDTH;                              //点的参考宽度
    public void redraw(){
        Rect rect = null;
        Canvas canvas = getHolder().lockCanvas();
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(bg,rect,new RectF(0,0,mWidth,mHeight),mPaint);
        for (int i=0 ; i<ROW ; i++){
            int offset = 0;
            if (i%2 == 0){
                offset = WIDTH/2;
            }
            for (int j=0 ; j<COL ; j++){
                Dot dot = getDot(j,i);
                switch (dot.getStauts()){
                    case Dot.STAUTS_OK:
                        canvas.drawBitmap(bmOK,rect,new RectF(dot.getX()*WIDTH +offset,dot.getY()*WIDTH+getDeviation(),
                                (dot.getX()+1)*WIDTH +offset,(dot.getY()+1)*WIDTH+getDeviation()),mPaint);
                        break;
                    case Dot.STAUTS_NO:
                        canvas.drawBitmap(bmNO,rect,new RectF(dot.getX()*WIDTH +offset,dot.getY()*WIDTH+getDeviation(),
                                (dot.getX()+1)*WIDTH +offset,(dot.getY()+1)*WIDTH+getDeviation()),mPaint);
                        break;
                    case Dot.STAUTS_IN:
                        canvas.drawBitmap(bmCat1,rect,new RectF(dot.getX()*WIDTH +offset,dot.getY()*WIDTH+getDeviation(),
                                (dot.getX()+1)*WIDTH +offset,(dot.getY()+1)*WIDTH+getDeviation()),mPaint);
                        break;
                    case Dot.STAUTS_IN2:
                        canvas.drawBitmap(bmCat2,rect,new RectF(dot.getX()*WIDTH +offset,dot.getY()*WIDTH+getDeviation(),
                                (dot.getX()+1)*WIDTH +offset,(dot.getY()+1)*WIDTH+getDeviation()),mPaint);
                        break;
                }
            }
        }
        getHolder().unlockCanvasAndPost(canvas);
    }

    private int mWidth,mHeight;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
    }

    public Dot getNeighbor(Dot dot, int dir){                //以1-6代表6个方向
        switch (dir){
            case 1:
                return getDot(dot.getX()-1,dot.getY());
            case 2:
                if (dot.getY()%2 == 0)
                    return getDot(dot.getX(),dot.getY()-1);
                else
                    return getDot(dot.getX()-1,dot.getY()-1);
            case 3:
                if (dot.getY()%2 == 0)
                    return getDot(dot.getX()+1,dot.getY()-1);
                else
                    return getDot(dot.getX(),dot.getY()-1);
            case 4:
                return getDot(dot.getX()+1,dot.getY());
            case 5:
                if (dot.getY()%2 == 0)
                    return getDot(dot.getX()+1,dot.getY()+1);
                else
                    return getDot(dot.getX(),dot.getY()+1);
            case 6:
                if (dot.getY()%2 == 0)
                    return getDot(dot.getX(),dot.getY()+1);
                else
                    return getDot(dot.getX()-1,dot.getY()+1);
            default:
                return null;
        }
    }

    public int getDistance(Dot dot,int dir){
        int distance = 0;
        if (dot.isEdge(COL,ROW)){
            return 1;
        }
        Dot oriDot = dot,nextDot;
        while (true){
            nextDot = getNeighbor(oriDot,dir);
            if (nextDot.getStauts() == Dot.STAUTS_NO || nextDot.getStauts() == Dot.STAUTS_IN
                    || nextDot.getStauts() == Dot.STAUTS_IN2){
                return distance*-1;
            }
            if (nextDot.isEdge(COL,ROW)){
                distance++;
                return distance;
            }
            distance++;
            oriDot = nextDot;
        }
    }

    public void moveTo(Dot dot){
        dot.setStauts(Dot.STAUTS_IN);
        getDot(cat.getX(),cat.getY()).setStauts(Dot.STAUTS_OK);
        cat.setXY(dot.getX(),dot.getY());
    }
    
    public void move(){
        if (cat.isEdge(COL,ROW)){
            lose();
        }
        List<Dot> avaliable = new ArrayList<>();
        List<Dot> positive = new ArrayList<>();
        HashMap<Dot,Integer> allLine = new HashMap<>();
        for (int i=1 ; i<7 ;i++){
            Dot nDot = getNeighbor(cat,i);
            if (nDot.getStauts() == Dot.STAUTS_OK){
                avaliable.add(nDot);
                allLine.put(nDot,i);
                if (getDistance(nDot,i) > 0){
                    positive.add(nDot);
                }
            }
        }
        MainActivity.score++;
        if (avaliable.size() == 0 ) {
            win();
        }else if(avaliable.size() == 1){
            moveTo(avaliable.get(0));
        }else if(justFirst && BLOCKS == 15){
            moveTo(avaliable.get((int)(Math.random()*1000% avaliable.size())));
            justFirst = false;
        }else {
            Dot bestDot = null;
            if (positive.size() != 0){              //存在可以直达边缘的路径
                int min = 99;
                for (int i =0; i<positive.size();i++){
                    int temp = getDistance(positive.get(i),allLine.get(positive.get(i)));
                    if (temp < min) {
                        min = temp;
                        bestDot = positive.get(i);
                    }
                }
            }else {                                 //六个方向都有路障
                int max = 0;
                for (int i = 0; i< avaliable.size(); i++){
                    int temp = getDistance(avaliable.get(i),allLine.get(avaliable.get(i)));
                    if (temp < max){
                        max = temp;
                        bestDot = avaliable.get(i);
                    }
                }
            }
            if (bestDot == null)
                moveTo(avaliable.get((int)(Math.random()*1000% avaliable.size())));
            else
                moveTo(bestDot);
        }
    }

    private void lose(){
        failCount++;
        if (failCount == 1){
            Toast.makeText(getContext(),"警惕的小猫已经先行一步",Toast.LENGTH_SHORT).show();
        } else if (failCount == 4){
            Toast.makeText(getContext(),"tip:点击左下角可调节难度",Toast.LENGTH_SHORT).show();
        }else if (failCount == 10){
            Toast.makeText(getContext(),"tip:点击右下角可以刷新小猫的警惕",Toast.LENGTH_SHORT).show();
        }else{
            sounder.startEndSound();
            Toast.makeText(getContext(),"You Lose",Toast.LENGTH_SHORT).show();
        }
        restartGame();
    }

    private int[] bestScore;

    private void win(){
        MainActivity.saveBest();
        bestScore = MainActivity.getBest();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (bestScore[MainActivity.getDiff()] == 88){
            builder.setMessage("恭喜你,成功围住了小猫\n你总共用了"+MainActivity.score+
                    "步\n当前难度为"+MainActivity.getDiffString(MainActivity.getDiff()));
        }else {
            Log.d("KiLO", bestScore[MainActivity.getDiff()]+"");
            builder.setMessage("恭喜你,成功围住了"+MainActivity.getDiffString(MainActivity.getDiff())
                    +"小猫\n\t\t你总共用了"+MainActivity.score+ "步"+ "\n\t\t\t该难度个人最佳成绩是"
                    +bestScore[MainActivity.getDiff()]+ "步\n\t\t\t\t打败了全国"+((96-
                    bestScore[MainActivity.getDiff()])+(int)(MainActivity.getDiff()*1.4))+ "%的人");
        }

        builder.setCancelable(false);
        builder.setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });
        builder.setPositiveButton("重来", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                restartGame();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            if (i<i1)
                WIDTH = Math.min(i1,i2)/(ROW+1);
            else
                WIDTH = Math.min(i1,i2)/(ROW+2);
            myListener.setWIDTH(WIDTH);
            redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        }
    };

    public void restartGame(){
        if (MainActivity.doubleCat)
            initGameD();
        else
            initGame();
        redraw();
        MainActivity.tvScore.setText("0步");
    }

    private void checkConfig(){
        switch (MainActivity.getDiff()){
            case 0:
                BLOCKS = 15;
                break;
            case 2:
                BLOCKS = 7;
                break;
            default:
                BLOCKS = 11;
                break;
        }
    }

    public void initGame(){
        checkConfig();
        matrix = new Dot[ROW][COL];
        for (int i = 0 ; i < ROW ; i++){
            for (int j = 0 ; j < COL ;j++){
                matrix[i][j] = new Dot(j,i);
            }
        }

        for (int i = 0 ; i < ROW ; i++){
            for (int j = 0 ; j < COL ;j++){
                matrix[i][j].stauts = Dot.STAUTS_OK;
            }
        }
        justFirst = true;
        MainActivity.score = 0;

        cat = new Dot(ROW/2,COL/2);
        getDot(ROW/2,COL/2).setStauts(Dot.STAUTS_IN);
        for (int i=0; i < BLOCKS ;){
            int x = (int)((Math.random()*1000)%COL);
            int y = (int)((Math.random()*1000)%ROW);
            if (getDot(y,x).getStauts() == Dot.STAUTS_OK){
                getDot(y,x).setStauts(Dot.STAUTS_NO);
                i++;
            }
        }
    }

    @Override
    public void onClick(View view) {
        restartGame();
    }

    public static GameGround getGameGround(){
        return gameGround;
    }

    //双猫相关方法
    public void moveD(){
        if (cat.isEdge(COL,ROW) || cat2.isEdge(COL,ROW)){
            lose();
        }
        List<Dot> avaliable = new ArrayList<>();
        List<Dot> positive = new ArrayList<>();
        HashMap<Dot,Integer> allLine = new HashMap<>();
        List<Dot> avaliable2 = new ArrayList<>();
        List<Dot> positive2 = new ArrayList<>();
        HashMap<Dot,Integer> allLine2 = new HashMap<>();
        for (int i=1 ; i<7 ;i++){
            Dot nDot = getNeighbor(cat,i);
            if (nDot.getStauts() == Dot.STAUTS_OK){
                avaliable.add(nDot);
                allLine.put(nDot,i);
                if (getDistance(nDot,i) > 0 ){
                    positive.add(nDot);
                }
            }
            Dot nDot2 = getNeighbor(cat2,i);
            if (nDot2.getStauts() == Dot.STAUTS_OK){
                avaliable2.add(nDot2);
                allLine2.put(nDot2,i);
                if (getDistance(nDot2,i) > 0 ){
                    positive2.add(nDot2);
                }
            }
        }
        MainActivity.score++;
        if (avaliable.size() == 0 && avaliable2.size() == 0){
            winD();
        }else if (avaliable.size() == 0 && avaliable2.size() == 1){
            moveToD(avaliable2.get(0),2);
        }else if (avaliable.size() == 1 && avaliable2.size() == 0){
            moveToD(avaliable.get(0),1);
        }else if (avaliable.size() == 1 && avaliable2.size() == 1){
            if (Math.random() > 0.5)
                moveToD(avaliable.get(0),1);
            else
                moveToD(avaliable2.get(0),2);
        }else {
            Dot bestDot = null;
            int bestCat = 1;
            if (positive.size() != 0 && positive2.size() != 0){
                int min = 99;
                for (int i =0; i<positive.size();i++){
                    int temp = getDistance(positive.get(i),allLine.get(positive.get(i)));
                    if (temp < min){
                        min = temp;
                        bestDot = positive.get(i);
                        bestCat = 1;
                    }
                }
                for (int i =0; i<positive2.size();i++){
                    int temp = getDistance(positive2.get(i),allLine2.get(positive2.get(i)));
                    if (temp < min){
                        min = temp;
                        bestDot = positive2.get(i);
                        bestCat = 2;
                    }
                }
            }else if (positive.size() != 0 || positive2.size() != 0){
                if (positive.size()> positive2.size()){
                    int min = 99;
                    for (int i =0; i<positive.size();i++){
                        int temp = getDistance(positive.get(i),allLine.get(positive.get(i)));
                        if (temp < min){
                            min = temp;
                            bestDot = positive.get(i);
                            bestCat = 1;
                        }
                    }
                }else {
                    int min = 99;
                    for (int i =0; i<positive2.size();i++){
                        int temp = getDistance(positive2.get(i),allLine2.get(positive2.get(i)));
                        if (temp < min){
                            min = temp;
                            bestDot = positive2.get(i);
                            bestCat = 2;
                        }
                    }
                }
            }else {
                int max = 0;
                for (int i = 0;i <avaliable.size();i++){
                    int temp = getDistance(avaliable.get(i),allLine.get(avaliable.get(i)));
                    if (temp < max){
                        max = temp;
                        bestDot = avaliable.get(i);
                        bestCat = 1;
                    }
                }
                for (int i = 0;i <avaliable2.size();i++){
                    int temp = getDistance(avaliable2.get(i),allLine2.get(avaliable2.get(i)));
                    if (temp < max){
                        max = temp;
                        bestDot = avaliable2.get(i);
                        bestCat = 2;
                    }
                }
            }
            if (bestDot == null){
                if (Math.random() > 0.5)
                    moveToD(avaliable.get((int)(Math.random()*1000% avaliable.size())),1);
                else
                    moveToD(avaliable2.get((int)(Math.random()*1000% avaliable2.size())),2);
            }else
                moveToD(bestDot,bestCat);
        }
    }

    public void moveToD(Dot dot,int i){
        switch (i){
            case 1:
                dot.setStauts(Dot.STAUTS_IN);
                getDot(cat.getX(),cat.getY()).setStauts(Dot.STAUTS_OK);
                cat.setXY(dot.getX(),dot.getY());
                break;
            case 2:
                dot.setStauts(Dot.STAUTS_IN2);
                getDot(cat2.getX(),cat2.getY()).setStauts(Dot.STAUTS_OK);
                cat2.setXY(dot.getX(),dot.getY());
        }
    }


    private void winD(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("恭喜你,成功围住了基友猫\n你总共用了"+MainActivity.score+
                    "步\n当前难度为"+MainActivity.getDiffString(MainActivity.getDiff()));
        builder.setCancelable(false);
        builder.setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityManager.finishAll();
            }
        });
        builder.setPositiveButton("解锁点什么", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (MainActivity.getDiff() > 0){
                    Toast.makeText(getContext(),"新功能现在已经可以使用了",Toast.LENGTH_SHORT).show();
                    DLC = true;
                }else {
                    Toast.makeText(getContext(),"解锁失败,试试更高难度吧",Toast.LENGTH_SHORT).show();
                }
                restartGame();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void initGameD(){
        checkConfig();
        matrix = new Dot[ROW][COL];
        for (int i =0; i< ROW ;i++){
            for (int j =0; j<COL; j++){
                matrix[i][j] = new Dot(j,i);
            }
        }
        for (int i =0; i< ROW ;i++){
            for (int j =0; j<COL; j++){
                matrix[i][j].stauts = Dot.STAUTS_OK;
            }
        }
        MainActivity.score = 0;
        cat = new Dot(ROW/2,COL/2);
        cat2 = new Dot(ROW/2+1,COL/2);

        getDot(ROW/2,COL/2).setStauts(Dot.STAUTS_IN);
        getDot(ROW/2,COL/2+1).setStauts(Dot.STAUTS_IN2);
        for (int i = 0;i <BLOCKS;){
            int x = (int)((Math.random()*1000)%COL);
            int y = (int)((Math.random()*1000)%ROW);
            if (getDot(y,x).getStauts() == Dot.STAUTS_OK){
                getDot(y,x).setStauts(Dot.STAUTS_NO);
                i++;
            }
        }
    }
}
