package com.example.a45556.catgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    public static TextView tvScore;
    private Button btnOption,btnRestart,btnStopMu;
    public static int score = 0;

    private static int diff;
    private static MyPref myPref;
    public static int easBest;
    public static int norBest;
    public static int expBest;

    public static boolean doubleCat;
    public static boolean BGM ;

    private Sounder sounder;

    public static int getDiff() {
        return diff;
    }

    public static boolean isDoubleCat() {
        return doubleCat;
    }

    public static String getDiffString(int diff){
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
        btnStopMu = (Button)findViewById(R.id.btn_stopMu);
        btnStopMu.setOnClickListener(this);

        prepareConfig();
        if (!BGM){
            btnStopMu.setBackgroundResource(R.drawable.mu_off);
        }
        sounder = Sounder.getInstance();
        sounder.setContext(this);
        if (BGM){
            sounder.initSound();
        }
    }

    public static void myPrefClear(){
        easBest = 88;
        expBest = 88;
        norBest =88;
        myPref.clear();
    }

    private void prepareConfig(){
        myPref = new MyPref(this);
        BGM = myPref.getConfig();
        int data[] = myPref.getBestScore();
        for (int i=0; i<3 ;i++){
            switch (i){
                case 0:
                    if (data[i]!=88)
                        easBest = data[i];
                    else
                        easBest = 88;
                    break;
                case 1:
                    if (data[i]!=88)
                        norBest = data[i];
                    else
                        norBest = 88;
                    break;
                case 2:
                    if (data[i]!=88)
                        expBest = data[i];
                    else
                        expBest = 88;
                    break;
            }
        }
    }

    public static int[] getBest(){
        return new int[]{easBest,norBest,expBest};
    }

    public static void saveBest(){
        switch (diff){
            case 0:
                if (score < easBest){
                    myPref.saveBestScore(diff,score);
                    easBest = score;
                }
                break;
            case 1:
                if (score < norBest){
                    myPref.saveBestScore(diff,score);
                    norBest = score;
                }
                break;
            case 2:
                if (score < expBest){
                    myPref.saveBestScore(diff,score);
                    expBest = score;
        }
                break;
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
        switch (view.getId()){
            case R.id.btn_option:
                startActivityForResult(new Intent(MainActivity.this,OptionActivity.class),666);
                break;
            case R.id.btn_stopMu:
                if (BGM == true){
                    btnStopMu.setBackgroundResource(R.drawable.mu_off);
                    sounder.stopClickSound();
                    sounder.stopEndSound();
                    BGM = false;
                    myPref.saveConfig();
                }
                else{
                    btnStopMu.setBackgroundResource(R.drawable.mu_on);
                    BGM = true;
                    myPref.saveConfig();
                }
                break;
        }
    }
}
