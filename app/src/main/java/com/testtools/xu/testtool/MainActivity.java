package com.testtools.xu.testtool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import unitil.ThreadStart;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ThreadStart t1 = new ThreadStart();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button1 = (Button)findViewById(R.id.start);
        button1.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                button1.setText("开始日志");
                t1.run();
            }
        });
        final Button button2 = (Button)findViewById(R.id.end);
        button2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                button2.setText("结束日志");
                if(t1!=null){

                }
            }
        });
    }

}
