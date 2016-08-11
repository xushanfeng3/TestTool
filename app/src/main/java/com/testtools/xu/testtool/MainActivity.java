package com.testtools.xu.testtool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import unitil.LogCollector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button1 = (Button)findViewById(R.id.start);
        final Button button2 = (Button)findViewById(R.id.end);
        button2.setClickable(false);
        final LogCollector logThread = LogCollector.getInstance(this);
        button1.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                button1.setText("Collecting.");
                button1.setClickable(false);
                logThread.launch();
                button2.setClickable(true);
                button2.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        logThread.cancel();
                        button1.setClickable(true);
                        button1.setText("startLog");
                        button2.setClickable(false);
                        button2.setText("endLog");
                    }
                });
            }
        });
    }

}
