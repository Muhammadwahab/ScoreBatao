package com.example.abdull.scorebatao.Activity;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.SmsManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abdull.scorebatao.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import Database.helper;
import pojo.localdata;
import utility.utilityConstant;

/**
 * Created by abdull on 7/4/17.
 */

public class EventService extends Service {
    // constant

    Database.helper helper;
    ArrayList timerCount;
    String CombineScore = "";
    PowerManager pm;
    PowerManager.WakeLock wl;
    // run on another Thread to avoid crash
    private Handler hand = new Handler();
    // timer handling
    private Timer timer;
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
            utilityConstant.showToast(getApplicationContext(),message);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        pm = (PowerManager) getSystemService(getApplicationContext().POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Tag");
        wl.acquire();
        utilityConstant.showToast(this,"On Create Wake Initialze");
        helper = new helper(getApplicationContext());
        timerCount = helper.getTimerCounts();
        if(timerCount.size()==0)
        {
            stopSelf();
            stopForeground(true);
        }
            if (timer != null) {
                timer.cancel();

            } else {

                timer = new Timer();
            }

            timer.scheduleAtFixedRate(new task(helper.getAllEvent()), 0, 2 * 60 * 1000);


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        utilityConstant.showToast(this,"on Start Command");
        // for forground service notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("ScoreBatao Running")
                .setContentText("Match Coverage Start")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setTicker("ScoreBatao")
                .build();
        startForeground(1, notification);
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    void gettingScore(final String MatchID, final ArrayList data) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://cricapi.com/api/ballByBall?unique_id=" + MatchID + "&&apikey=X13XvjoxgCbgGdtoqsWuYr0FeTC3";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        getScore(response, data);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void getScore(String response, ArrayList data) {
        JSONTokener jsonTokener = new JSONTokener(response);
        String Score = "";
        try {

            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            JSONArray oversArray=jsonObject.getJSONArray("data");
            for (int i = 0; i <oversArray.length() ; i++) {
                JSONObject singleOver=oversArray.getJSONObject(i);
                if (singleOver.getJSONArray("ball").length()>=6)
                {
                    // store all ball result
                    JSONArray balls=singleOver.getJSONArray("ball");

                    for (int j = 0; j <balls.length() ; j++) {
                        JSONObject ballDetail=balls.getJSONObject(i);
                        // get all details of over and event happend
                        String Event= (String) ballDetail.get("event");
                        checkEvent(Event,ballDetail);

                    }
                    // get match score
                    singleOver.getString("wickets");
                    singleOver.getString("runs");
                    singleOver.getString("team_id");

                    addScore(singleOver);

                    break;
                }
                else {
                    // store certain bowl result and continue
                    JSONArray balls=singleOver.getJSONArray("ball");

                    for (int j = 0; j <balls.length() ; j++) {
                        JSONObject ballDetail=balls.getJSONObject(i);
                        // get all details of event happend

                    }
                }

            }
            String Team_1 = jsonObject.getString("team-1");
            String Team_2 = jsonObject.getString("team-2");
            String matchType = jsonObject.getString("type");
            boolean matchStarted = jsonObject.getBoolean("matchStarted");
            if (matchType.equalsIgnoreCase(utilityConstant.ODI)) {
                matchType = "ODI ";
            }
            if (matchStarted) {
                Score = jsonObject.getString("score");
            } else {
                Score = "Match Not Start ";
            }
            String innings_requirement = jsonObject.getString("innings-requirement");
            CombineScore = matchType + "" + Team_1 + "VS" + Team_2 + "Score " + Score + "" + innings_requirement;
            utilityConstant.showToast(this,"Total" + CombineScore);
            for (int i = 0; i < data.size(); i++) {
                localdata localdata = (pojo.localdata) data.get(i);
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> parts = smsManager.divideMessage(CombineScore);
                try {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT"), 0);
                    ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
                    ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
                    for (int j = 0; j < parts.size(); j++) {
                        sentIntents.add(PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT"), 0));

                    }
                    smsManager.sendMultipartTextMessage(localdata.getPhonenumber(), null, parts, sentIntents, null);

                } catch (Exception e) {
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        registerReceiver(receiver, new IntentFilter("SMS_SENT"));  // SMS_SENT is a constant
    }

    private void addScore(JSONObject singleOver) throws JSONException {
        // get match score
//        singleOver.getString("wickets");
//        singleOver.getString("runs");
//        singleOver.getString("team_id");
        if(!utilityConstant.EVEN_FOUR_DETAIL.equals(""))
        {
            utilityConstant.EVEN_FOUR_DETAIL= utilityConstant.EVEN_FOUR_DETAIL+" "+singleOver.getString("runs")+"/"+singleOver.getString("wickets");

        }
        else  if(!utilityConstant.EVEN_OUT_DETAIL.equals(""))
        {
            utilityConstant.EVEN_OUT_DETAIL= utilityConstant.EVEN_OUT_DETAIL+" "+singleOver.getString("runs")+"/"+singleOver.getString("wickets");

        }
        else  if(!utilityConstant.EVEN_SIX_DETAIL.equals(""))
        {
            utilityConstant.EVEN_SIX_DETAIL= utilityConstant.EVEN_SIX_DETAIL+" "+singleOver.getString("runs")+"/"+singleOver.getString("wickets");

        }

    }

    private void checkEvent(String event,JSONObject ballDetail) throws JSONException {

        switch (event)
        {
            case utilityConstant.EVEN_FOUR:
                //this
                String delievery= (String) ballDetail.get("overs_actual");
                String player= (String) ballDetail.get("players") +" "+utilityConstant.EVEN_FOUR;
                utilityConstant.EVEN_FOUR_DETAIL=delievery+":"+player;
                case utilityConstant.EVEN_OUT:
                     delievery= (String) ballDetail.get("overs_actual");
                     player= (String) ballDetail.get("players") +" "+utilityConstant.EVEN_OUT;
                    utilityConstant.EVEN_OUT_DETAIL=delievery+":"+player;
                    // this
                    case utilityConstant.EVEN_SIX:
                        // this
                        delievery= (String) ballDetail.get("overs_actual");
                        player= (String) ballDetail.get("players") +" "+utilityConstant.EVEN_SIX;
                        utilityConstant.EVEN_SIX_DETAIL=delievery+":"+player;
                        default:
                            // this


        }
    }

    @Override
    public void onDestroy() {
//        for (int i = 0; i < timerArray.length; i++) {
//            timerArray[i].cancel();
//            utilityConstant.showToast(this,"Service Destroy timer number " + i);
//        }
        wl.release();
        stopForeground(true);
    }
    class task extends TimerTask {
        ArrayList Data;
        String Request;

        public task(ArrayList Data) {
            this.Data = Data;
            this.Request = Request;

        }
        @Override
        public void run() {
            hand.post(new Runnable() {
                @Override
                public void run() {
                    if(Data.size()>0)
                    {
                        localdata localdata = (localdata) Data.get(0);
                        gettingScore(localdata.getMatchID(), Data);
                    }
                    else
                    {
                        stopSelf();
                        utilityConstant.showToast(getApplication(),"NO User at the moment in Events List");
                    }

                }
            });

        }
    }
}
