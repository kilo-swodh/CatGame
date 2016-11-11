package com.example.a45556.catgame;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 45556 on 2016-11-8.
 */

public class MyListener implements View.OnTouchListener,View.OnClickListener{
    private int WIDTH,DEVIATION;
    private GameGround gameGround;

    public MyListener(GameGround gameGround){
        this.gameGround = gameGround;
    }
    public void setWIDTH(int WIDTH){
        this.WIDTH = WIDTH;
        DEVIATION =  gameGround.getDeviation();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {        //此处XY轴相反
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_UP:
                int x,y;
                y = (int)((motionEvent.getY()- DEVIATION)/WIDTH);
                if (y%2 == 0){
                    x = (int)((motionEvent.getX()-WIDTH/2)/WIDTH );
                }else {
                    x = (int)(motionEvent.getX()/WIDTH );
                }
                if ( x+1 <= gameGround.COL && y+1 <= gameGround.ROW && motionEvent.getY()> DEVIATION) {
                    if (gameGround.getDot(x, y).getStauts() == Dot.STAUTS_OK) {
                        gameGround.getDot(x, y).setStauts(Dot.STAUTS_NO);
                        gameGround.move();
                        MainActivity.tvScore.setText(MainActivity.score+"步");
                        gameGround.redraw();
                    }
                }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        gameGround.restartGame();
    }
}
