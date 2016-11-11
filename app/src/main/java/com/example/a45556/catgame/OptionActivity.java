package com.example.a45556.catgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

/**
 * Created by 45556 on 2016-11-10.
 */

public class OptionActivity extends AppCompatActivity {
    private Spinner spinner;
    private Button btnOk;
    private Button btnBack;
    private CheckBox checkBox1;
    private CheckBox checkBox2;

    private String[] diff = new String[]{"简单","普通","专家"};

    private Intent intent = new Intent();
    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        bundle = new Bundle();

        btnOk = (Button)findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("bundle",bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        btnBack = (Button)findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spinner = (Spinner)findViewById(R.id.s_choice);
        spinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,diff));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bundle.putInt("diff", i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        checkBox1 = (CheckBox)findViewById(R.id.cb_twocat);
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    bundle.putBoolean("doubleCat",b);
                }else if (bundle.getBoolean("doubleCat")){
                    bundle.putBoolean("doubleCat",false);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
