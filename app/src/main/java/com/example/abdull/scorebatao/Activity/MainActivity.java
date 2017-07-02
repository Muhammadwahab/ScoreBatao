package com.example.abdull.scorebatao.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.abdull.scorebatao.R;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.d("Splash Screen","Exception is "+e);
                }
                Intent intent=new Intent(MainActivity.this,StartScreen.class);
                startActivity(intent);
                finish();
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
    }
}
