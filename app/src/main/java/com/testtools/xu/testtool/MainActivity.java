package com.testtools.xu.testtool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import unitil.LogCollect;
import unitil.LogCollector;
import unitil.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button1 = (Button)findViewById(R.id.start);
        final Button button2 = (Button)findViewById(R.id.end);
        button2.setEnabled(false);
        final LogCollect logStart = new LogCollect();
        button1.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                logStart.launch();
                Logger.d("test123");
                MainActivity.this.startLog(button1,button2);
                button2.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        logStart.cancel();
                        MainActivity.this.endLog(button1,button2);
                    }
                });
            }
        });

    }
    public void startLog(Button button1,Button button2){
        button1.setText("collecting");
        button1.setEnabled(false);
        button2.setEnabled(true);
    }
    public void endLog(Button button1,Button button2){
        button2.setEnabled(false);
        button2.setText("stop");
        button1.setEnabled(true);
    }

}
