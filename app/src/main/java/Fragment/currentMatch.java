package Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import Adapter.currentMatchesAdapter;
import pojo.currentLiveMatches;
import utility.utilityConstant;

/**
 * Created by abdull on 3/23/17.
 */

public class currentMatch extends Fragment {
    ListView listView;
    currentLiveMatches liveMatches[];
    private ArrayList arrayList=new ArrayList();
    View view;
    ViewGroup container;
    currentMatchesAdapter currentMatchesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.container=container;
        Toast.makeText(getActivity(), "Email in Fragment "+getArguments().getString("Email","Email Not Found In Frament"), Toast.LENGTH_SHORT).show();
        view=LayoutInflater.from(getContext()).inflate(R.layout.current_match,container,false);
        listView=(ListView)view.findViewById(R.id.curretListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentLiveMatches matches= (currentLiveMatches) arrayList.get(position);

                Toast.makeText(getContext(), "id is "+matches.getUnique_ID(), Toast.LENGTH_SHORT).show();
            }
        });
      // currentMatchesAdapter=new currentMatchesAdapter(getActivity(),0,arrayList);
     // profileadapter profileadapter=new profileadapter();




        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
        gettingMatches();
        currentMatchesAdapter=new currentMatchesAdapter(getActivity(),0,arrayList);
        listView.setAdapter(currentMatchesAdapter);
    }


    void gettingMatches()
    {
       // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="http://cricapi.com/api/matches?apikey=X13XvjoxgCbgGdtoqsWuYr0FeTC3";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        currentMatchesAdapter.clear();
                        arrayList=getData(response);
                        currentMatchesAdapter.addAll(arrayList);
                        currentMatchesAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public ArrayList getData(String response) {
        arrayList = new ArrayList();
        JSONTokener jsonTokener = new JSONTokener(response);

        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            JSONArray arrayOfMatches = jsonObject.getJSONArray("matches");
            Log.e("response", "array of object is " + arrayOfMatches);

            for (int i = 0; i < arrayOfMatches.length(); i++) {
                JSONObject localData = (JSONObject) arrayOfMatches.get(i);
                if(localData.getBoolean("matchStarted")==false)
                {
                    continue;
                }
                currentLiveMatches data = new currentLiveMatches();
                data.setUnique_ID(localData.getLong("unique_id"));
                data.setDate(localData.getString("date"));
                data.setTeamOne(localData.getString("team-1"));
                data.setTeamTwo(localData.getString("team-2"));
                data.setMatchStart(localData.getBoolean("matchStarted"));
                arrayList.add(data);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;
    }
    class profileadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return  arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
          if(convertView==null)
            {
                convertView= LayoutInflater.from(getContext()).inflate(R.layout.matches_view_adapter_layout,parent,false);
            }
            TextView oneVsTwo=(TextView)convertView.findViewById(R.id.oneVsTwo);
            currentLiveMatches matches= (currentLiveMatches) (currentLiveMatches) arrayList.get(position);
            oneVsTwo.setText((int) matches.getUnique_ID()+"");
//            setCoverage=(Button) convertView.findViewById(R.id.setCoverage);
//            setCoverage.setOnClickListener(this);
            return convertView;
        }
    }
}
