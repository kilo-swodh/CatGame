package com.example.a45556.catgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static TextView tvScore;
    public static Button btnOption;
    public static Button btnRestart;
    public static int score = 0 ;

    private static int diff;
    private static boolean doubleCat;
    private static boolean allowMusic = true;

    private Sounder sounder;

    public static int getDiff() {
        return diff;
    }

    public static boolean isDoubleCat() {
        return doubleCat;
    }

    public static String getDiff(int diff){
        switch (diff){
            case 0:
                return "简单难度";
            case 1:
                return "普通难度";
            case 2:
                return "专家难度";
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        tvScore = (TextView)findViewById(R.id.score);
        btnOption = (Button)findViewById(R.id.btn_option);
        btnOption.setOnClickListener(this);
        btnRestart = (Button)findViewById(R.id.btn_restart);
        btnRestart.setOnClickListener(new MyListener(GameGround.getGameGround()));
        sounder = Sounder.getInstance(this);
        if (allowMusic){
            sounder.initSound();
            sounder.startBgSound();
            allowMusic = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 666){
            if (resultCode == RESULT_OK){
                Bundle bundle = data.getBundleExtra("bundle");
                doubleCat = bundle.getBoolean("doubleCat");
                diff = bundle.getInt("diff");
                Toast.makeText(this,"设置成功,下一局开始生效",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        startActivityForResult(new Intent(MainActivity.this,OptionActivity.class),666);
    }
}
