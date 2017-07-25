package com.example.abdull.scorebatao.Activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import pojo.currentLiveMatches;
import pojo.localdata;
import utility.utilityConstant;
public class SmsManager extends BroadcastReceiver {
    private String TAG = SmsManager.class.getSimpleName();
    private String messageBody;
    private ArrayList arrayList;
    Context context;
    public SmsManager() {
    }

    String PhoneNumber;
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();
        this.context=context;

        SmsMessage[] msgs = null;

        String str = "";

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i=0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Sender's phone number
                str += "SMS from " + msgs[i].getOriginatingAddress() + " : ";
                PhoneNumber=msgs[i].getOriginatingAddress();
                // Fetch the text message
                str += msgs[i].getMessageBody().toString();
                messageBody=msgs[i].getMessageBody().toString();
                // Newline <img draggable="false" class="emoji" alt="🙂" src="https://s.w.org/images/core/emoji/72x72/1f642.png">
                str += "\n";
            }

            if (messageBody.trim().equalsIgnoreCase("ScoreBatao"))
            {
                SharedPreferences sharedPreferences=context.getSharedPreferences(utilityConstant.MyPREFERENCES,Context.MODE_PRIVATE);

                if (sharedPreferences.getLong("expireMatchesTime",-1)>System.currentTimeMillis())
                {
                    ArrayList currentMatches=getStringArrayPref(context,"livematchesArray");
                    Toast.makeText(context, "Hello from Score Batao Please Select Match to get Score", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, "Hello from Score Batao Please Select Match to get Score", Toast.LENGTH_SHORT).show();
                    gettingMatches();
                }


            }
            else if(messageBody.trim().contains("scorebatao-"))
            {
                StringBuilder builder=new StringBuilder(messageBody.trim());
                Toast.makeText(context, "index of "+  builder.substring(builder.indexOf("-")+1), Toast.LENGTH_SHORT).show();
             //   builder.substring(builder.indexOf("-")+1,builder.length());

            }


            // Display the entire SMS Message
         //   Toast.makeText(context, "Score Batao"+str, Toast.LENGTH_SHORT).show();
            Log.d(TAG, str);
        }
    }

    void gettingMatches() {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://cricapi.com/api/matches?apikey=X13XvjoxgCbgGdtoqsWuYr0FeTC3&&v=2";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        arrayList = getData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(stringRequest);
    }

    public ArrayList getData(String response) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy:MM:dd");
        String matches="Hello from Score Batao Please Type scorebatao-matchID to get Score";
        arrayList = new ArrayList();
        JSONTokener jsonTokener = new JSONTokener(response);
        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            JSONArray arrayOfMatches = jsonObject.getJSONArray("matches");
            Log.e("response", "array of object is " + arrayOfMatches);

            for (int i = 0; i < arrayOfMatches.length(); i++) {
                JSONObject localData = (JSONObject) arrayOfMatches.get(i);
                if (localData.has("matchStarted")) {

                    if (!localData.getBoolean("matchStarted")) {
                        continue;
                    }

                    StringBuilder dateGMTApi=new StringBuilder(localData.getString("dateTimeGMT"));


                    dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

                    if(!dateGMTApi.substring(0,10).equalsIgnoreCase(dateFormatGmt.format(new Date()).trim().toString()))
                    {
                        continue;
                    }

                }
                else
                {
                    continue;
                }


                 matches+=localData.getLong("unique_id")+" "+localData.getString("team-1")+"VS "+localData.getString("team-2")+"\n";
                currentLiveMatches data = new currentLiveMatches();
                data.setUnique_ID(localData.getLong("unique_id"));
                data.setDate(localData.getString("dateTimeGMT"));
                data.setTeamOne(localData.getString("team-1"));
                data.setTeamTwo(localData.getString("team-2"));
                data.setMatchStart(localData.getBoolean("matchStarted"));
                arrayList.add(data);
            }


            if (arrayList.size()==0) {
            matches="Hello From Score Batao Sorry No Match Live At the Moment";
                SharedPreferences expiredPreference=context.getSharedPreferences(utilityConstant.MyPREFERENCES,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=expiredPreference.edit();
                editor.putLong("expireMatchesTime",System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30));
                editor.commit();
                setStringArrayPref(context,"livematchesArray",arrayList);

            }
            else
            {
                SharedPreferences expiredPreference=context.getSharedPreferences(utilityConstant.MyPREFERENCES,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=expiredPreference.edit();
                editor.putLong("expireMatchesTime",System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30));
                editor.commit();
                setStringArrayPref(context,"livematchesArray",arrayList);

            }


            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                ArrayList<String> parts = smsManager.divideMessage(matches);
                try {
                   // PendingIntent pendingIntent = PendingIntent.getBroadcast(context(), 0, new Intent("SMS_SENT"), 0);
                    ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
                    ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
                    for (int j = 0; j < parts.size(); j++) {
                        sentIntents.add(PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0));
                    }
                    smsManager.sendMultipartTextMessage(PhoneNumber, null, parts, sentIntents, null);

                } catch (Exception e) {
                    Toast.makeText(context, "Exception is "+e, Toast.LENGTH_SHORT).show();
                }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public  void setStringArrayPref(Context context, String key, ArrayList values) {
        SharedPreferences prefs = context.getSharedPreferences(utilityConstant.MyPREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson=new Gson();
        Type type = new TypeToken<ArrayList<currentLiveMatches>>(){}.getType(); // array list type
        String serializeJSon = gson.toJson(values, type);
        editor.putString(key,serializeJSon);

        editor.commit();
    }
    public static ArrayList<currentLiveMatches> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(utilityConstant.MyPREFERENCES,Context.MODE_PRIVATE);
        String json = prefs.getString(key, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<currentLiveMatches>>(){}.getType(); // array list type
        ArrayList<currentLiveMatches> currentLiveMatches=gson.fromJson(json,type);

        return currentLiveMatches;
        }

    }
