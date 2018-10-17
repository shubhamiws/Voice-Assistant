package com.singhal.iws_024.voicetime;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.Pink));
        }
        CalenderPermission();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MicActivity.class));
                finish();
            }
        }, 5000);

    }

    public void CalenderPermission() {

        if (
                ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(SplashActivity.this,Manifest.permission.WRITE_CALENDAR)!= PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(SplashActivity.this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED

                )
        {
            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.READ_CALENDAR,Manifest.permission.WRITE_CALENDAR,Manifest.permission.RECORD_AUDIO}, 100);
        }



    }

}
