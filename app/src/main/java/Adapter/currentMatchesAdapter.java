package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdull.scorebatao.Activity.services;
import com.example.abdull.scorebatao.Activity.PersonsDetail;
import com.example.abdull.scorebatao.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import Database.helper;
import pojo.currentLiveMatches;
import pojo.userLocal;
import utility.utilityConstant;

/**
 * Created by abdull on 3/23/17.
 */


public class currentMatchesAdapter extends ArrayAdapter implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    ArrayList liveMatches=new ArrayList();
    Button setCoverage;
    Context context;
    long matchID;
    SharedPreferences storeUserRequest;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    currentLiveMatches matches;
    public currentMatchesAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList live) {
        super(context, resource, live);
        this.context=context;
        liveMatches=live;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        userLocal check=null;

        if(convertView==null)
        {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.matches_view_adapter_layout,parent,false);
        }



        storeUserRequest= ((Activity)context).getSharedPreferences(utilityConstant.MyPREFERENCES, Context.MODE_PRIVATE);
           TextView oneVsTwo=(TextView)convertView.findViewById(R.id.oneVsTwo);
         matches= (currentLiveMatches) (currentLiveMatches) liveMatches.get(position);
         matchID=matches.getUnique_ID();

           // oneVsTwo.setText(matches.getTeamOne()+" VS "+matches.getTeamTwo());
        oneVsTwo.setText(matches.getTeamOne()+"VS"+matches.getTeamTwo());
          setCoverage=(Button) convertView.findViewById(R.id.setCoverage);
       setCoverage.setOnClickListener(this);
        setCoverage.setTag(position);
        // Spinner element
        final Spinner spinner = (Spinner) convertView.findViewById(R.id.spinner2);
        Switch OnOff= (Switch) (Switch) convertView.findViewById(R.id.scset);
        final helper helper=new helper(context);
        ArrayList local=helper.showRecord();
        if(local.size()!=0)
        {
            check= (userLocal) local.get(0);
            if(check.getMatchID().equalsIgnoreCase(String.valueOf(matchID)))
            {
                OnOff.setChecked(true);

            }
        }

        OnOff.setTag(position);
        OnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final currentLiveMatches matches= (currentLiveMatches) liveMatches.get((Integer) buttonView.getTag());
                Toast.makeText(context, "onoff id "+matches.getUnique_ID(), Toast.LENGTH_SHORT).show();
                if (isChecked) {

                    // this is for validation

//                    // The toggle is enabled
//                    helper checkUniqueCoverage=new helper(context);
//                    ArrayList data=checkUniqueCoverage.showRecord();
//                    if(data.size()>0)
//                    {
//                        Toast.makeText(context, "You Only Allowed To set One Coveerage OF match", Toast.LENGTH_SHORT).show();
//
//                    }
//                    else if(data.size()==0)
//                    {
//                        Toast.makeText(context, "Please Insert Some Number to Start Coverage", Toast.LENGTH_SHORT).show();
//                    }
//                    else
//                    {
//
//                    }
                    // yaha se copy kia he hum ne
                    final String time= (String) spinner.getItemAtPosition(utilityConstant.spinnerItemPosition);
                    String Request;
                    if((Request=storeUserRequest.getString(utilityConstant.requestCatche,"null")).equalsIgnoreCase("null"))
                    {

                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                Iterable<DataSnapshot> childrenData = dataSnapshot.getChildren();

                                for (DataSnapshot child : childrenData) {
//                    DatabaseReference databaseReference=child.getRef();
//                    databaseReference.
                                    HashMap hashMap = (HashMap) child.getValue();
                                    final String email = (String) hashMap.get("email");

                                    if (email.equalsIgnoreCase(((Activity)context).getIntent().getStringExtra("Email"))) {
                                        DatabaseReference databaseReference = child.getRef();
                                        String id="matchID-" + matches.getUnique_ID();
                                        String reference = databaseReference.toString() + "/" + "id"+ "/"+id;

                                        // storing in sharedprefference

                                        String storeLocalReference=databaseReference.toString()+"/"+"id";
                                        StringBuilder stringLocalBuilder=new StringBuilder(storeLocalReference);
                                        SharedPreferences.Editor editor=storeUserRequest.edit();
                                        editor.putString(utilityConstant.requestCatche,stringLocalBuilder.replace(0, 40, "").toString());
                                        editor.commit();
                                        // end storing in shared prefference


                                        StringBuilder stringBuilder = new StringBuilder(reference);
                                        String refvalue = stringBuilder.replace(0, 40, "").toString();
                                        DatabaseReference getMatchID = database.getReference(refvalue);
                                        getMatchID.child("time").setValue(time);
                                        getMatchID.child("status").setValue("on");
                                        getMatchID.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                Iterable<DataSnapshot> childPhoneNumber = dataSnapshot.getChildren();
                                                ArrayList numbers=new ArrayList();
                                                for(DataSnapshot phone:childPhoneNumber)
                                                {
                                                    String key=phone.getKey();
                                                    if(key.equalsIgnoreCase("status") || key.equalsIgnoreCase("time"))
                                                    {

                                                    }
                                                    else
                                                    {
                                                        numbers.add(key);
                                                    }

                                                }
                                                Toast.makeText(context, "service check", Toast.LENGTH_SHORT).show();

                                                helper insert=new helper(context);
                                                Intent intent=((Activity)context).getIntent();
                                                long idCheck=insert.insertData(matches.getUnique_ID()+"","On",numbers,intent.getStringExtra("Email"),time);
                                                if(idCheck!=-1)
                                                {
                                                    Toast.makeText(context, "Service Start", Toast.LENGTH_SHORT).show();
                                                    ((Activity)context).startService(new Intent(getContext(), services.class));

                                                }
                                                else
                                                {
                                                    Toast.makeText(context, "Error in Database", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        // storing in sharedprefference


                                        // end storing in shared prefference

                                        break;
                                        // break when email address find

                                    }

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("Read Failed", "Failed to read value.", error.toException());
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(context, "service check", Toast.LENGTH_SHORT).show();
                        String id="matchID-" + matches.getUnique_ID();
                        DatabaseReference databaseReference=database.getReference(Request+"/"+id);
                        databaseReference.child("time").setValue(time);
                        databaseReference.child("status").setValue("on");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Iterable<DataSnapshot> childPhoneNumber = dataSnapshot.getChildren();
                                ArrayList numbers=new ArrayList();
                                String status="",times="";
                                for(DataSnapshot phone:childPhoneNumber)
                                {
                                    String key=phone.getKey();
                                    if(key.equalsIgnoreCase("status"))
                                    {
                                        status=phone.getValue().toString();

                                    }
                                    else if (key.equalsIgnoreCase("time"))
                                    {
                                        times=phone.getValue().toString();

                                    }
                                    else
                                    {
                                        numbers.add(key);

                                    }

                                }
                                helper insert=new helper(context);
                                Intent intent=((Activity)context).getIntent();
                                long idCheck=insert.insertData(matches.getUnique_ID()+"",status,numbers,intent.getStringExtra("Email"),times);
                                if(idCheck!=-1)
                                {
                                    Toast.makeText(context, "Service Start", Toast.LENGTH_SHORT).show();
                                    ((Activity)context).startService(new Intent(getContext(), services.class));

                                }
                                else
                                {
                                    Toast.makeText(context, "Error in Database", Toast.LENGTH_SHORT).show();
                                }
                                // insert.insertData("wahab","sss");

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    // yaha tak copy kia he hum ne  niche wale else se





                } else {
                    // The toggle is disabled
                    Toast.makeText(context, "Disable", Toast.LENGTH_SHORT).show();
                    String Request;
                    if((Request=storeUserRequest.getString(utilityConstant.requestCatche,"null")).equalsIgnoreCase("null"))
                    {
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                Iterable<DataSnapshot> childrenData = dataSnapshot.getChildren();

                                for (DataSnapshot child : childrenData) {
//                    DatabaseReference databaseReference=child.getRef();
//                    databaseReference.
                                    HashMap hashMap = (HashMap) child.getValue();
                                    final String email = (String) hashMap.get("email");

                                    if (email.equalsIgnoreCase(((Activity)context).getIntent().getStringExtra("Email"))) {
                                        DatabaseReference databaseReference = child.getRef();
                                        String id="matchID-" + matches.getUnique_ID();
                                        String reference = databaseReference.toString() + "/" + "id"+ "/"+id;

                                        // storing in sharedprefference

                                        String storeLocalReference=databaseReference.toString()+"/"+"id";
                                        StringBuilder stringLocalBuilder=new StringBuilder(storeLocalReference);
                                        SharedPreferences.Editor editor=storeUserRequest.edit();
                                        editor.putString(utilityConstant.requestCatche,stringLocalBuilder.replace(0, 40, "").toString());
                                        editor.commit();
                                        // end storing in shared prefference


                                        StringBuilder stringBuilder = new StringBuilder(reference);
                                        String refvalue = stringBuilder.replace(0, 40, "").toString();
                                        DatabaseReference getMatchID = database.getReference(refvalue);
                                        getMatchID.child("status").setValue("OFF");
                                        helper insert=new helper(context);
                                        ((Activity)context).stopService(new Intent(getContext(), services.class).putExtra("SERVICE","STOP"));
                                        insert.deleteAll();


                                        // insert.insertData("wahab","sss");





                                        break;
                                        // break when email address find

                                    }

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("Read Failed", "Failed to read value.", error.toException());
                            }
                        });
                    }
                    else
                    {
                        String id="matchID-" + matches.getUnique_ID();
                        DatabaseReference databaseReference=database.getReference(Request+"/"+id);
                        databaseReference.child("status").setValue("OFF");
                        helper insert=new helper(context);
                        ((Activity)context).stopService(new Intent(getContext(), services.class));
                        insert.deleteAll();
                    }

                }
            }
        });

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.timeofMatch, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);


        return convertView;
    }

    @Override
    public void onClick(View v) {
        Button button= (Button) v;

        if(button.getId()==setCoverage.getId())
        {
            currentLiveMatches matches= (currentLiveMatches) liveMatches.get((Integer) v.getTag());
            Toast.makeText(context, "Match id"+matches.getUnique_ID(), Toast.LENGTH_SHORT).show();

            Intent intent=((Activity)context).getIntent();
            Toast.makeText(getContext(),"set Coverage",Toast.LENGTH_LONG).show();
           Intent addPerson=new Intent(getContext(), PersonsDetail.class);
            addPerson.putExtra("Email",intent.getStringExtra("Email"));
            addPerson.putExtra("matchId",matches.getUnique_ID());
           getContext().startActivity(addPerson);
            ((Activity)context).finish();
        }

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      //  Toast.makeText(getContext(), "Position is "+position, Toast.LENGTH_SHORT).show();
        utilityConstant.spinnerItemPosition=position;
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
