package com.example.abdull.scorebatao.Activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import pojo.currentLiveMatches;

/**
 * Created by abdull on 7/22/17.
 */

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
               // Toast.makeText(context, "Hello from Score Batao Please Select Match to get Score", Toast.LENGTH_SHORT).show();
                gettingMatches();
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
        String url = "http://cricapi.com/api/matches?apikey=X13XvjoxgCbgGdtoqsWuYr0FeTC3";
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
        String matches="Hello from Score Batao Please Type scorebatao-matchID to get Score";
        arrayList = new ArrayList();
        JSONTokener jsonTokener = new JSONTokener(response);
        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            JSONArray arrayOfMatches = jsonObject.getJSONArray("matches");
            Log.e("response", "array of object is " + arrayOfMatches);

            for (int i = 0; i < arrayOfMatches.length(); i++) {
                JSONObject localData = (JSONObject) arrayOfMatches.get(i);
                if (!localData.getBoolean("matchStarted")) {
                    continue;
                }

                 matches+=localData.getLong("unique_id")+" "+localData.getString("team-1")+"VS "+localData.getString("team-2")+"\n";
//

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
                }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}