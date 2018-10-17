package com.singhal.iws_024.voicetime;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.provider.CalendarContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import ai.api.android.AIConfiguration;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.ui.AIButton;

public class MicActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextView textView;
    AIConfiguration config;
    AIButton aiButton;

    TextToSpeech engine;
    double pitch = 1.0;
    double speed = 1.0;

    Gson gson = new Gson();
    String finaldata;
    String json;
    JSONObject j;
    String str_date_day = "", str_date_month = "", str_time_hr = "", str_time_minampm, str_time_ampm, str_time_min = "";
    String[] parts, parts2, parts4;
    int i, c, k, d, e;
    int num_month = 1, num_day = 1,day_return,moth_return, num_hr = 0, num_min = 0;
    String task_name;
    int status_code = 0;
    String str_task_name;
    boolean listening_result=false;
    int ques_1, ques_2,ques_3,ques_4;
    String ans_1="",ans_2="",ans_3="",ans_4="";

    int rep=0;
    Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
    int MY_PERMISSIONS_CONTACTS=2000;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.Pink));
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        isNetworkAvailable();


        textView = (TextView) findViewById(R.id.textView);

        engine = new TextToSpeech(this, this);
        engine.setPitch((float) pitch);
        engine.setSpeechRate((float) speed);

        config = new AIConfiguration("229be44a877441be88819b6c64df4b1e",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiButton = (AIButton) findViewById(R.id.micButton);
        aiButton.initialize(config);
        aiButton.startListening();

        aiButton.onListeningFinished();

        aiButton.setResultsListener(new AIButton.AIButtonListener() {
            @Override
            public void onResult(final AIResponse result) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("NewApi")
                    @Override
                    public void run() {
                        json = gson.toJson(result);
                        Log.d("JSON", "" + json);
                        try {

                            j = new JSONObject(json);
                            finaldata = j.getJSONObject("result").getString("resolvedQuery");
                            textView.setText("" + finaldata);
                            status_code++;
                            ans_ques(finaldata);
                            rep=1;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(final AIError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ApiAi", String.valueOf(error));
                        // TODO process error here
                    }
                });
            }

            @Override
            public void onCancelled() {

            }
        });

    }

    @SuppressLint("NewApi")
    public void ans_ques(final String finaldata) {
        // Start listen on open App and Match Hello
        if (status_code == 1) {
            if ( (this.finaldata.charAt(0) == 'h' || this.finaldata.charAt(0) == 'H' ) && (this.finaldata.charAt(i+1)=='e' || this.finaldata.charAt(i+1)=='E') ) {
                ans_1=finaldata;
                speakques(status_code);
            }
        }

        // SET Meeting
        if (status_code == 2) {
            if ((this.finaldata.charAt(0) == 's' || this.finaldata.charAt(0) == 'S') && (this.finaldata.charAt(1)=='e' || this.finaldata.charAt(1)=='E')&& (this.finaldata.charAt(2)=='t' || this.finaldata.charAt(2)=='T')) {
                ans_2=finaldata;
                speakques(status_code);
                str_task_name = this.finaldata;
            }
        }

        // Date 6 july
        if (status_code == 3) {

            String str2=" of ";

            for (i = 1, e = 1, d = 1; i < this.finaldata.length(); i++) {
                char ch = this.finaldata.charAt(i);
                if (ch == ' ') {
                    e++;
                }
            }
            if (finaldata.toLowerCase().contains(str2.toLowerCase()))
            {
                    parts = finaldata.split(" of ", e);
                    str_date_day = parts[0];
                    str_date_month = parts[1];
                    date_day_check();
            }
            else
            {
                if (e==2) {

                    parts = finaldata.split(" ", e);
                    str_date_day = parts[0];
                    str_date_month = parts[1];
                    date_day_check();
                }
            }

        }

        // Time 6 a.m.
        if (status_code == 4) {


            String str_am="a.m.";
            String str_pm="p.m.";
            if (finaldata.toLowerCase().contains(str_am.toLowerCase())
                ||finaldata.toLowerCase().contains(str_pm.toLowerCase()) ) {

                if (this.finaldata.charAt(0) >= 1 || this.finaldata.charAt(0) <= 9) {
                    for (i = 1, c = 1, d = 1; i < this.finaldata.length(); i++) {
                        char ch = this.finaldata.charAt(i);
                        if ((ch == ' ' && this.finaldata.charAt(i + 1) == 'a') || (ch == ' ' && this.finaldata.charAt(i + 1) == 'p')) {
                            c++;
                        }
                        if (ch == ':') {
                            d++;
                        }
                    }
                    if (d >= 2) {
                        time_hrmin_check();
                    } else {
                        if (c >= 2) {
                            time_hr_womin_check();
                        }
                    }
                }
            }

        }

    }

    @SuppressLint("NewApi")
    public void speakques(final int status_code) {
        this.status_code=status_code;

        switch (this.status_code) {

            case 1:
                // After matching hello
                ques_1= engine.speak("Task Name ", TextToSpeech.QUEUE_FLUSH, null, null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        aiButton.startListening();
                    }
                }, 2000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ans_2.equalsIgnoreCase(""))
                        {
                            speakques(status_code);
                        }
                    }
                }, 7000);

                break;

            case 2:

                // After SET Meeting
                ques_2 =engine.speak("On Date", TextToSpeech.QUEUE_FLUSH, null, null);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        aiButton.startListening();
                    }
                }, 2000);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ans_3.equalsIgnoreCase(""))
                        {speakques(status_code); }
                    }
                }, 7000);

                break;

            case 3:
                ques_3=engine.speak("At Time", TextToSpeech.QUEUE_FLUSH, null, null);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        aiButton.startListening();
                    }
                }, 2000);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (ans_4.equalsIgnoreCase(""))
                        {
                            speakques(status_code);
                        }

                    }
                }, 7000);

                break;

            default:
                speakques(0);
                break;
        }

    }

    @SuppressLint("NewApi")
    public void date_day_check() {

        switch (str_date_day) {
                case "1":
                    num_day = 1;
                    date_month_check();
                    break;
                case "2":
                    num_day = 2;
                    date_month_check();
                    break;
                case "3":
                    num_day = 3;
                    date_month_check();
                    break;
                case "4":
                    num_day = 4;
                    date_month_check();
                    break;
                case "5":
                    num_day = 5;
                    date_month_check();
                    break;
                case "6":
                    num_day = 6;
                    date_month_check();
                    break;
                case "7":
                    num_day = 7;
                    date_month_check();
                    break;
                case "8":
                    num_day = 8;
                    date_month_check();
                    break;
                case "9":
                    num_day = 9;
                    date_month_check();
                    break;
                case "10":
                    num_day = 10;
                    date_month_check();
                    break;
                case "11":
                    num_day = 11;
                    date_month_check();
                    break;
                case "12":
                    num_day = 12;
                    date_month_check();
                    break;
                case "13":
                    num_day = 13;
                    date_month_check();
                    break;
                case "14":
                    num_day = 14;
                    date_month_check();
                    break;
                case "15":
                    num_day = 15;
                    date_month_check();
                    break;
                case "16":
                    num_day = 16;
                    date_month_check();
                    break;
                case "17":
                    num_day = 17;
                    date_month_check();
                    break;
                case "18":
                    num_day = 18;
                    date_month_check();
                    break;
                case "19":
                    num_day = 19;
                    date_month_check();
                    break;
                case "20":
                    num_day = 20;
                    date_month_check();
                    break;
                case "21":
                    num_day = 21;
                    date_month_check();
                    break;
                case "22":
                    num_day = 22;
                    date_month_check();
                    break;
                case "23":
                    num_day = 23;
                    date_month_check();
                    break;
                case "24":
                    num_day = 24;
                    date_month_check();
                    break;
                case "25":
                    num_day = 25;
                    date_month_check();
                    break;
                case "26":
                    num_day = 26;
                    date_month_check();
                    break;
                case "27":
                    num_day = 27;
                    date_month_check();
                    break;
                case "28":
                    num_day = 28;
                    date_month_check();
                    break;
                case "29":
                    num_day = 29;
                    date_month_check();
                    break;
                case "30":
                    num_day = 30;
                    date_month_check();
                    break;
                case "31":
                    num_day = 31;
                    date_month_check();
                    break;
                    case "one":
                        num_day = 1;
                        date_month_check();
                        break;
                    case "two":
                        num_day = 2;
                        date_month_check();
                        break;
                    case "1st":
                        num_day = 1;
                        date_month_check();
                        break;
                    case "first":
                        num_day = 1;
                        date_month_check();
                        break;
                    case "three":
                        num_day = 3;
                        date_month_check();
                        break;
                    case "3rd":
                        num_day = 3;
                        date_month_check();
                        break;
                    case "four":
                        num_day = 4;
                        date_month_check();
                        break;
                    case "five":
                        num_day = 5;
                        date_month_check();
                        break;
                    case "six":
                        num_day = 6;
                        date_month_check();
                        break;
                    case "seven":
                        num_day = 7;
                        date_month_check();
                        break;
                    case "eight":
                        num_day = 8;
                        date_month_check();
                        break;
                    case "nine":
                        num_day = 9;
                        date_month_check();
                        break;
                    case "ten":
                        num_day = 10;
                        date_month_check();
                        break;
                    case "eleven":
                        num_day = 11;
                        date_month_check();
                        break;
                    case "twelve":
                        num_day = 12;
                        date_month_check();
                        break;
                    case "thirteen":
                        num_day = 13;
                        date_month_check();
                        break;
                    case "fourteen":
                        num_day = 14;
                        date_month_check();
                        break;
                    case "fifteen":
                        num_day = 15;
                        date_month_check();
                        break;
                    case "sixteen":
                        num_day = 16;
                        date_month_check();
                        break;
                    case "seventeen":
                        num_day = 17;
                        date_month_check();
                        break;
                    case "eighteen":
                        num_day = 18;
                        date_month_check();
                        break;
                    case "nineteen":
                        num_day = 19;
                        date_month_check();
                        break;
                    case "twenty":
                        num_day = 20;
                        date_month_check();
                        break;
                    case "twentyone":
                        num_day = 21;
                        date_month_check();
                        break;
                    case "twentytwo":
                        num_day = 22;
                        date_month_check();
                        break;
                    case "twentythree":
                        num_day = 23;
                        date_month_check();
                        break;
                    case "twentyfour":
                        num_day = 24;
                        date_month_check();
                        break;
                    case "twentyfive":
                        num_day = 25;
                        date_month_check();
                        break;
                    case "twentysix":
                        num_day = 26;
                        date_month_check();
                        break;
                    case "twentyseven":
                        num_day = 27;
                        date_month_check();
                        break;
                    case "twentyeight":
                        num_day = 28;
                        date_month_check();
                        break;
                    case "twentynine":
                        num_day = 29;
                        date_month_check();
                        break;
                    case "thirty":
                        num_day = 30;
                        date_month_check();
                        break;
                    case "thirtyone":
                        num_day = 31;
                        date_month_check();
                        break;
                    case "too":
                        num_day = 2;
                        date_month_check();
                        break;
                    case "for":
                        num_day = 4;
                        date_month_check();
                        break;
                    case "5th":
                        num_day = 5;
                        date_month_check();
                        break;
                    case "6th":
                        num_day = 6;
                        date_month_check();
                        break;
                    case "7th":
                        num_day = 7;
                        date_month_check();
                        break;
                    case "8th":
                        num_day = 8;
                        date_month_check();
                        break;
                    case "9th":
                        num_day = 9;
                        date_month_check();
                        break;
                    case "10th":
                        num_day = 10;
                        date_month_check();
                        break;
                    case "11th":
                        num_day = 11;
                        date_month_check();
                        break;
                    case "12th":
                        num_day = 12;
                        date_month_check();
                        break;
                    case "13th":
                        num_day = 13;
                        date_month_check();
                        break;
                    case "14th":
                        num_day = 14;
                        date_month_check();
                        break;
                    case "15th":
                        num_day = 15;
                        date_month_check();
                        break;
                    case "16th":
                        num_day = 16;
                        date_month_check();
                        break;
                    case "17th":
                        num_day = 17;
                        date_month_check();
                        break;
                    case "18th":
                        num_day = 18;
                        date_month_check();
                        break;
                    case "19th":
                        num_day = 19;
                        date_month_check();
                        break;
                    case "20th":
                        num_day = 20;
                        date_month_check();
                        break;
                    case "2nd":
                        num_day = 2;
                        date_month_check();
                        break;
                    case "21th":
                        num_day = 21;
                        date_month_check();
                        break;
                    case "22th":
                        num_day = 22;
                        date_month_check();
                        break;
                    case "23rd":
                        num_day = 23;
                        date_month_check();
                        break;
                    case "24th":
                        num_day = 24;
                        date_month_check();
                        break;
                    case "25th":
                        num_day = 25;
                        date_month_check();
                        break;
                    case "26th":
                        num_day = 26;
                        date_month_check();
                        break;
                    case "27th":
                        num_day = 27;
                        date_month_check();
                        break;
                    case "28th":
                        num_day = 28;
                        date_month_check();
                        break;
                    case "29th":
                        num_day = 29;
                        date_month_check();
                        break;
                    default:
                        ans_3="";

                }
            }

    @SuppressLint("NewApi")
    public void date_month_check() {

        switch (str_date_month) {
            case "January":
                num_month = 0;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "February":
                num_month = 1;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "March":
                num_month = 2;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "April":
                num_month = 3;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "May":
                num_month = 4;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "June":
                num_month = 5;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "July":
                num_month = 6;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "August":
                num_month = 7;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "September":
                num_month = 8;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "October":
                num_month = 9;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "Oktober":
                num_month = 9;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "oktober":
                num_month = 9;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "November":
                num_month = 10;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            case "December":
                num_month = 11;
                ans_3 = finaldata;
                speakques(status_code);
                break;
            default:
                ans_3="";

        }
    }

    @SuppressLint("NewApi")
    public void time_hrmin_check() {

        parts = finaldata.split(":", c);
        str_time_hr = parts[0];
        Log.d("HOUR!", String.valueOf(str_time_hr));

        if (c >= 2) {
            str_time_minampm = parts[1];
            Log.d("TIME!", String.valueOf(str_time_minampm));
        }

        //              am pm check

        for (k = 1, d = 1; k < str_time_minampm.length(); k++) {
            char ch = str_time_minampm.charAt(k);
            if (ch == ' ') {
                d++;
                Log.d("counts", String.valueOf(d));
            }
        }

        parts2 = str_time_minampm.split(" ", d);
        str_time_min = parts2[0];
        Log.d("MINSS", String.valueOf(str_time_min));
        if (d >= 2) {
            str_time_ampm = parts2[1];
            Log.d("AMS", String.valueOf(str_time_ampm));

        }

        if (str_time_hr.length() <= 2) {
            switch (str_time_hr) {
                case "1":
                    num_hr = 1;
                    time_min_check();
                    break;
                case "2":
                    num_hr = 2;
                    time_min_check();
                    break;
                case "3":
                    num_hr = 3;
                    time_min_check();
                    break;
                case "4":
                    num_hr = 4;
                    time_min_check();
                    break;
                case "5":
                    num_hr = 5;
                    time_min_check();
                    break;
                case "6":
                    num_hr = 6;
                    time_min_check();
                    break;
                case "7":
                    num_hr = 7;
                    time_min_check();
                    break;
                case "8":
                    num_hr = 8;
                    time_min_check();
                    break;
                case "9":
                    num_hr = 9;
                    time_min_check();
                    break;
                case "10":
                    num_hr = 10;
                    time_min_check();
                    break;
                case "11":
                    num_hr = 11;
                    time_min_check();
                    break;
                case "12":
                    num_hr = 12;
                    time_min_check();
                    break;
                default:
                    ans_4="";
                    break;
            }
        }

    }

    @SuppressLint("NewApi")
    public void time_min_check() {

        switch (str_time_min) {

            case "1":
                num_min = 1;
                time_ampm_check();
                break;
            case "2":
                num_min = 2;
                time_ampm_check();
                break;
            case "3":
                num_min = 3;
                time_ampm_check();
                break;
            case "4":
                num_min = 4;
                time_ampm_check();
                break;
            case "5":
                num_min = 5;
                time_ampm_check();
                break;
            case "6":
                num_min = 6;
                time_ampm_check();
                break;
            case "7":
                num_min = 7;
                time_ampm_check();
                break;
            case "8":
                num_min = 8;
                time_ampm_check();
                break;
            case "9":
                num_min = 9;
                time_ampm_check();
                break;
            case "10":
                num_min = 10;
                time_ampm_check();
                break;
            case "11":
                num_min = 11;
                time_ampm_check();
                break;
            case "12":
                num_min = 12;
                time_ampm_check();
                break;
            case "13":
                num_min = 13;
                time_ampm_check();
                break;
            case "14":
                num_min = 14;
                time_ampm_check();
                break;
            case "15":
                num_min = 15;
                time_ampm_check();
                break;
            case "16":
                num_min = 16;
                time_ampm_check();
                break;
            case "17":
                num_min = 17;
                time_ampm_check();
                break;
            case "18":
                num_min = 18;
                time_ampm_check();
                break;
            case "19":
                num_min = 19;
                time_ampm_check();
                break;
            case "20":
                num_min = 20;
                time_ampm_check();
                break;
            case "21":
                num_min = 21;
                time_ampm_check();
                break;
            case "22":
                num_min = 22;
                time_ampm_check();
                break;
            case "23":
                num_min = 23;
                time_ampm_check();
                break;
            case "24":
                num_min = 24;
                time_ampm_check();
                break;
            case "25":
                num_min = 25;
                time_ampm_check();
                break;
            case "26":
                num_min = 26;
                time_ampm_check();
                break;
            case "27":
                num_min = 27;
                time_ampm_check();
                break;
            case "28":
                num_min = 28;
                time_ampm_check();
                break;
            case "29":
                num_min = 29;
                time_ampm_check();
                break;
            case "30":
                num_min = 30;
                time_ampm_check();
                break;
            case "31":
                num_min = 31;
                time_ampm_check();
                break;
            case "32":
                num_min = 32;
                time_ampm_check();
                break;
            case "33":
                num_min = 33;
                time_ampm_check();
                break;
            case "34":
                num_min = 34;
                time_ampm_check();
                break;
            case "35":
                num_min = 35;
                time_ampm_check();
                break;
            case "36":
                num_min = 36;
                time_ampm_check();
                break;
            case "37":
                num_min = 37;
                time_ampm_check();
                break;
            case "38":
                num_min = 38;
                time_ampm_check();
                break;
            case "39":
                num_min = 39;
                time_ampm_check();
                break;
            case "40":
                num_min = 40;
                time_ampm_check();
                break;
            case "41":
                num_min = 41;
                time_ampm_check();
                break;
            case "42":
                num_min = 42;
                time_ampm_check();
                break;
            case "43":
                num_min = 43;
                time_ampm_check();
                break;
            case "44":
                num_min = 44;
                time_ampm_check();
                break;
            case "45":
                num_min = 45;
                time_ampm_check();
                break;
            case "46":
                num_min = 46;
                time_ampm_check();
                break;
            case "47":
                num_min = 47;
                time_ampm_check();
                break;
            case "48":
                num_min = 48;
                time_ampm_check();
                break;
            case "49":
                num_min = 49;
                time_ampm_check();
                break;
            case "50":
                num_min = 50;
                time_ampm_check();
                break;
            case "51":
                num_min = 51;
                time_ampm_check();
                break;
            case "52":
                num_min = 52;
                time_ampm_check();
                break;
            case "53":
                num_min = 53;
                time_ampm_check();
                break;
            case "54":
                num_min = 54;
                time_ampm_check();
                break;
            case "55":
                num_min = 55;
                time_ampm_check();
                break;
            case "56":
                num_min = 56;
                time_ampm_check();
                break;
            case "57":
                num_min = 57;
                time_ampm_check();
                break;
            case "58":
                num_min = 58;
                time_ampm_check();
                break;
            case "59":
                num_min = 59;
                time_ampm_check();
                break;

            default:
                ans_4="";
                break;
        }
    }

    @SuppressLint("NewApi")
    public void time_hr_womin_check() {

        parts4 = finaldata.split(" ", c);
        num_min = 0;
        str_time_hr = parts4[0];

        if (c >= 2) {
            //Stroing a.m. and p.m. in String Min format like  7 a.m.
            str_time_ampm = parts4[1];
        }

        if (str_time_hr.length() <= 2) {
            switch (str_time_hr) {
                case "1":
                    num_hr = 1;
                    time_ampm_check();
                    break;
                case "2":
                    num_hr = 2;
                    time_ampm_check();
                    break;
                case "3":
                    num_hr = 3;
                    time_ampm_check();
                    break;
                case "4":
                    num_hr = 4;
                    time_ampm_check();
                    break;
                case "5":
                    num_hr = 5;
                    time_ampm_check();
                    break;
                case "6":
                    num_hr = 6;
                    time_ampm_check();
                    break;
                case "7":
                    num_hr = 7;
                    time_ampm_check();
                    break;
                case "8":
                    num_hr = 8;
                    time_ampm_check();
                    break;
                case "9":
                    num_hr = 9;
                    time_ampm_check();
                    break;
                case "10":
                    num_hr = 10;
                    time_ampm_check();
                    break;
                case "11":
                    num_hr = 11;
                    time_ampm_check();
                    break;
                case "12":
                    num_hr = 12;
                    time_ampm_check();
                    break;

                default:
                    ans_4 = "";
                    break;
            }
        }


    }

    @SuppressLint("NewApi")
    public void time_ampm_check() {

        switch (str_time_ampm) {
            case "a.m.":
                ans_4 = finaldata;
                engine.speak("Thank You", TextToSpeech.QUEUE_FLUSH, null, null);
                Log.d("Timingssss : ", str_time_hr + ":" + " " + str_time_ampm);
                addReminderInCalendar();
                break;
            case "p.m.":
                ans_4 = finaldata;
                engine.speak("Thank You" , TextToSpeech.QUEUE_FLUSH, null, null);
                Log.d("Timingssss : ", str_time_hr + ":" + " " + str_time_ampm);
                addReminderInCalendar();
                break;
            default:
                ans_4 ="";
                break;
        }

    }

    public void addReminderInCalendar() {

        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        if (num_min == 0) {

            if (str_time_ampm.equalsIgnoreCase("a.m.")) {
                beginTime.set(localCalendar.get(Calendar.YEAR), num_month , num_day , num_hr, 0);
                startMillis = beginTime.getTimeInMillis();
                endTime.set(localCalendar.get(Calendar.YEAR), num_month , num_day , num_hr, 0);
                endMillis = endTime.getTimeInMillis();
            } else {
                beginTime.set(localCalendar.get(Calendar.YEAR), num_month , num_day , num_hr+12, 0);
                startMillis = beginTime.getTimeInMillis();
                endTime.set(localCalendar.get(Calendar.YEAR), num_month , num_day , num_hr+12 , 0);
                endMillis = endTime.getTimeInMillis();
            }
        } else {
            if (str_time_ampm.equalsIgnoreCase("a.m.")) {
                beginTime.set(localCalendar.get(Calendar.YEAR), num_month , num_day , num_hr, num_min);
                startMillis = beginTime.getTimeInMillis();
                endTime.set(localCalendar.get(Calendar.YEAR), num_month , num_day , num_hr , num_min);
                endMillis = endTime.getTimeInMillis();
            } else {
                beginTime.set(localCalendar.get(Calendar.YEAR), num_month , num_day , num_hr+12 , num_min);
                startMillis = beginTime.getTimeInMillis();
                endTime.set(localCalendar.get(Calendar.YEAR), num_month , num_day , num_hr + 12, num_min);
                endMillis = endTime.getTimeInMillis();
            }

        }
// Insert Event
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.TITLE, str_task_name);
        values.put(CalendarContract.Events.DESCRIPTION, str_task_name);
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        String eventID = uri.getLastPathSegment();

        uri = CalendarContract.Calendars.CONTENT_URI;
        String[] projection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_COLOR
        };

        Cursor calendarCursor = managedQuery(uri, projection, null, null, null);
        Intent i=new Intent(MicActivity.this,MicActivity.class);
        startActivity(i);
        finishAffinity();
//        Intent intent = new Intent(Intent.ACTION_INSERT)
//                .setType("vnd.android.cursor.item/event")
//                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
//                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
//                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false) // just included for completeness
//                .putExtra(CalendarContract.Events.TITLE, str_task_name)
//                .putExtra(CalendarContract.Events.DESCRIPTION, str_task_name)
//                .putExtra(CalendarContract.Events.EVENT_LOCATION, "India")
//                .putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=10")
//                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
//                .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
//                .putExtra(Intent.EXTRA_EMAIL, "ss@gmail.com");


    }

    @Override
    public void onInit(int status) {
        Log.d("Speech", "OnInit - Status [" + status + "]");
        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speech", "Success!");
            engine.setLanguage(Locale.UK);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
