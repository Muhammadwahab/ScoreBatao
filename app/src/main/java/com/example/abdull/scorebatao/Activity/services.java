package com.example.abdull.scorebatao.Activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import Database.helper;
import pojo.currentLiveMatches;
import pojo.userLocal;
import utility.utilityConstant;

/**
 * Created by abdull on 4/3/17.
 */

public class services extends Service {
    // constant

    helper helper;
    userLocal user;
    ArrayList userData;
    String CombineScore="";

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    PowerManager pm;
    PowerManager.WakeLock wl;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            helper=new helper(getApplicationContext());
            userData=helper.showRecord();
            userLocal user1= (userLocal) userData.get(0);
            utilityConstant.NOTIFY_INTERVAL=Long.parseLong(user1.getTime())*60*1000;

            mTimer.cancel();
        } else {
            // recreate new
            helper=new helper(getApplicationContext());
            userData=helper.showRecord();
            userLocal user1= (userLocal) userData.get(0);
            utilityConstant.NOTIFY_INTERVAL=Long.parseLong(user1.getTime())*60*1000;
            mTimer = new Timer();
            pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Tag");
            wl.acquire();
            Toast.makeText(this, "Wake Initialze", Toast.LENGTH_SHORT).show();
//do what you need to do
        }
        // schedule task
       mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, utilityConstant.NOTIFY_INTERVAL);
     //   mHandler.postDelayed(new runnable(getApplicationContext(),mHandler),1000);

    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
//
                    userLocal userLocal= (pojo.userLocal) userData.get(0);
                    gettingScore(userLocal.getMatchID());

                }


            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "on Start Command", Toast.LENGTH_SHORT).show();
         helper=new helper(getApplicationContext());
        userData=helper.showRecord();
        userLocal user1= (userLocal) userData.get(0);
        utilityConstant.NOTIFY_INTERVAL=Long.parseLong(user1.getTime())*60*1000;


        return super.onStartCommand(intent, flags, startId);

    }

    void gettingScore(String MatchID)
    {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="http://cricapi.com/api/cricketScore?unique_id="+MatchID+"&&apikey=X13XvjoxgCbgGdtoqsWuYr0FeTC3";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        getScore(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = null;

            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    message = "Message sent!";
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    message = "Error. Message not sent.";
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    message = "Error: No service.";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    message = "Error: Null PDU.";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    message = "Error: Radio off.";
                    break;
            }

            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
        }
    };
    public void getScore(String response) {
        JSONTokener jsonTokener = new JSONTokener(response);
        String Score="";
        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                String Team_1=jsonObject.getString("team-1");
            String Team_2=jsonObject.getString("team-2");
            String matchType=jsonObject.getString("type");
            boolean matchStarted=jsonObject.getBoolean("matchStarted");
            if(matchType.equalsIgnoreCase(utilityConstant.ODI))
            {
                matchType="ODI";
            }
            if(matchStarted)
            {
                 Score=jsonObject.getString("score");
            }
            else
            {
                Score="Match Not Start";
            }

            String innings_requirement=jsonObject.getString("innings-requirement");
             CombineScore=matchType+""+Team_1+"VS"+Team_2+"Score "+Score+""+innings_requirement;
            ArrayList check=new ArrayList();
            Toast.makeText(this, "Total"+CombineScore, Toast.LENGTH_SHORT).show();
            for (int i = 0; i <userData.size() ; i++) {
                user = (pojo.userLocal) userData.get(i);
                String[] numbers={"+923471218967","+923471218967"};
                SmsManager smsManager=SmsManager.getDefault();
                ArrayList<String> parts =smsManager.divideMessage(CombineScore);



                try {

                    PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),0,new Intent("SMS_SENT"),0);


//                        SmsManager.getDefault().sendTextMessage(user.getNumber(),null,
//                                ""+CombineScore, pendingIntent, null);
                    ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
                    ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

                    for (int j = 0; j < parts.size(); j++)
                    {
                        sentIntents.add(PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT"), 0));

                    }
                    smsManager.sendMultipartTextMessage(user.getNumber(),null,parts,sentIntents,null);

                } catch (Exception e) {
                    AlertDialog.Builder alertDialogBuilder = new
                            AlertDialog.Builder(getApplicationContext());

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        registerReceiver(receiver, new IntentFilter("SMS_SENT"));  // SMS_SENT is a constant
    }
    @Override
    public void onDestroy() {
        mTimer.cancel();
        wl.release();
        Toast.makeText(this, "Service Destroy", Toast.LENGTH_SHORT).show();
    }

}