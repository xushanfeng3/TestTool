package com.testtools.xu.testtool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import unitil.LogCollector;

public class MainActivity extends AppCompatActivity {
    private String TAG = "HOME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LogCollector log = LogCollector.getInstance(MainActivity.this);
        final Button button1 = (Button)findViewById(R.id.start);
        final Button button2 = (Button)findViewById(R.id.end);
        button2.setEnabled(false);
        button1.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                log.launch();
                button1.setEnabled(false);
                button1.setText("Collecting.");
                button2.setEnabled(true);
            }
        });
        button2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                log.cancel();
                button2.setEnabled(false);
                button2.setText("Stop.");
                button1.setEnabled(true);
            }
        });
    }
}
