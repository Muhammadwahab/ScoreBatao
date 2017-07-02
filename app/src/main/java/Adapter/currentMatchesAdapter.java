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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.abdull.scorebatao.Activity.PersonsDetail;
import com.example.abdull.scorebatao.Activity.services;
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
import pojo.localdata;
import utility.utilityConstant;

/**
 * Created by abdull on 3/23/17.
 */


public class currentMatchesAdapter extends ArrayAdapter implements View.OnClickListener {
    public boolean checkOnOf;
    public boolean checkOFF;
    ArrayList liveMatches = new ArrayList();
    Button setCoverage;
    Context context;
    long matchID;
    SharedPreferences storeUserRequest;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    currentLiveMatches matches;
    Switch OnOff;

    String name;
    String request;
    String Status;
    String update;

    public currentMatchesAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList live) {
        super(context, resource, live);
        this.context = context;
        liveMatches = live;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        localdata check = null;
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.matches_view_adapter_layout, parent, false);

        storeUserRequest = ((Activity) context).getSharedPreferences(utilityConstant.MyPREFERENCES, Context.MODE_PRIVATE);
        TextView oneVsTwo = (TextView) convertView.findViewById(R.id.oneVsTwo);
        matches = (currentLiveMatches) (currentLiveMatches) liveMatches.get(position);
        matchID = matches.getUnique_ID();

        oneVsTwo.setText(matches.getUnique_ID() + matches.getTeamOne() + "VS" + matches.getTeamTwo());
        setCoverage = (Button) convertView.findViewById(R.id.setCoverage);
        setCoverage.setOnClickListener(this);
        setCoverage.setTag(position);
        // Spinner element
        OnOff = (Switch) (Switch) convertView.findViewById(R.id.scset);
        final helper helper = new helper(context);
        ArrayList local = helper.showRecord();


        if (local.size() != 0) {
            check = (localdata) local.get(0);
            if (check.getMatchID().equalsIgnoreCase(String.valueOf(matchID))) {

                OnOff.setChecked(true);
                checkOnOf = true;


            } else {

            }
        }

        OnOff.setTag(position);

        OnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final currentLiveMatches matches = (currentLiveMatches) liveMatches.get((Integer) buttonView.getTag());
                utilityConstant.showToast(context, "onoff id ");
                if (isChecked) {
                    if (!checkOnOf) {
                        String Request;
                        if ((Request = storeUserRequest.getString(utilityConstant.requestCatche, "null")).equalsIgnoreCase("null")) {

                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
                                    Iterable<DataSnapshot> childrenData = dataSnapshot.getChildren();

                                    for (DataSnapshot child : childrenData) {
                                        HashMap hashMap = (HashMap) child.getValue();
                                        final String email = (String) hashMap.get("email");

                                        if (email.equalsIgnoreCase(((Activity) context).getIntent().getStringExtra("Email"))) {
                                            DatabaseReference databaseReference = child.getRef();
                                            String id = "matchID-" + matches.getUnique_ID();
                                            String reference = databaseReference.toString() + "/" + "id" + "/" + id;

                                            // storing in sharedprefference

                                            String storeLocalReference = databaseReference.toString() + "/" + "id";
                                            StringBuilder stringLocalBuilder = new StringBuilder(storeLocalReference);
                                            SharedPreferences.Editor editor = storeUserRequest.edit();
                                            editor.putString(utilityConstant.requestCatche, stringLocalBuilder.replace(0, 40, "").toString());
                                            editor.commit();
                                            // end storing in shared prefference


                                            StringBuilder stringBuilder = new StringBuilder(reference);
                                            String refvalue = stringBuilder.replace(0, 40, "").toString();
                                            DatabaseReference getMatchID = database.getReference(refvalue);
                                            getMatchID.child("userstatus").setValue("on");
                                            getMatchID.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    Iterable<DataSnapshot> childPhoneNumber = dataSnapshot.getChildren();
                                                    ArrayList numbers = new ArrayList();
                                                    for (DataSnapshot phone : childPhoneNumber) {
                                                        String key = phone.getKey();
                                                        if (key.equalsIgnoreCase("userstatus") || key.equalsIgnoreCase("time")) {

                                                        } else {
                                                            HashMap numberDetails = (HashMap) phone.getValue();
                                                            name = (String) numberDetails.get("name");
                                                            request = (String) numberDetails.get("request");
                                                            Status = (String) numberDetails.get("status");
                                                            update = (String) numberDetails.get("update");
                                                            numbers.add(new localdata(matches.getUnique_ID() + "", Status, key, ((Activity) context).getIntent().getStringExtra("Email"), request, utilityConstant.ON, update, name));
                                                        }

                                                    }
                                                    utilityConstant.showToast(context, "service check");

                                                    helper insert = new helper(context);
                                                    long idCheck = insert.insertData(numbers);
                                                    utilityConstant.showToast(context, "" + idCheck);
                                                    if (idCheck != -1) {
                                                        utilityConstant.showToast(context, "Service Start");
                                                        ((Activity) context).startService(new Intent(getContext(), services.class));

                                                    } else {
                                                        utilityConstant.showToast(context, "Error in Database");
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                            break;
                                            // break when email address find

                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    Log.w("Read Failed", "Failed to read value.", error.toException());
                                }
                            });
                        } else {
                            utilityConstant.showToast(context, "service check");

                            String id = "matchID-" + matches.getUnique_ID();
                            DatabaseReference databaseReference = database.getReference(Request + "/" + id);
                            databaseReference.child("userstatus").setValue("on");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    Iterable<DataSnapshot> childPhoneNumber = dataSnapshot.getChildren();
                                    ArrayList numbers = new ArrayList();
                                    String status = "", times = "";
                                    for (DataSnapshot phone : childPhoneNumber) {
                                        String key = phone.getKey();
                                        if (key.equalsIgnoreCase("userstatus")) {
                                            status = phone.getValue().toString();

                                        } else if (key.equalsIgnoreCase("time")) {
                                            times = phone.getValue().toString();

                                        } else {
                                            HashMap numberDetails = (HashMap) phone.getValue();
                                            name = (String) numberDetails.get("name");
                                            request = (String) numberDetails.get("request");
                                            Status = (String) numberDetails.get("status");
                                            update = (String) numberDetails.get("update");
                                            numbers.add(new localdata(matches.getUnique_ID() + "", Status, key, ((Activity) context).getIntent().getStringExtra("Email"), request, utilityConstant.ON, update, name));
                                        }
                                    }
                                    helper insert = new helper(context);
                                    long idCheck = insert.insertData(numbers);
                                    utilityConstant.showToast(context, "" + idCheck);

                                    if (idCheck != -1) {
                                        utilityConstant.showToast(context, "Service Start");

                                        ((Activity) context).startService(new Intent(getContext(), services.class));

                                    } else {
                                        utilityConstant.showToast(context, "Error in Database");

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }


                } else {
                    // The toggle is disabled
                    if (!checkOFF) {

                    }
                    checkOnOf = false;
                    utilityConstant.CHECKCOUNT = 0;
                    utilityConstant.showToast(context, "Disable");
                    String Request;
                    if ((Request = storeUserRequest.getString(utilityConstant.requestCatche, "null")).equalsIgnoreCase("null")) {
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                Iterable<DataSnapshot> childrenData = dataSnapshot.getChildren();

                                for (DataSnapshot child : childrenData) {
                                    HashMap hashMap = (HashMap) child.getValue();
                                    final String email = (String) hashMap.get("email");

                                    if (email.equalsIgnoreCase(((Activity) context).getIntent().getStringExtra("Email"))) {
                                        DatabaseReference databaseReference = child.getRef();
                                        String id = "matchID-" + matches.getUnique_ID();
                                        String reference = databaseReference.toString() + "/" + "id" + "/" + id;

                                        // storing in sharedprefference

                                        String storeLocalReference = databaseReference.toString() + "/" + "id";
                                        StringBuilder stringLocalBuilder = new StringBuilder(storeLocalReference);
                                        SharedPreferences.Editor editor = storeUserRequest.edit();
                                        editor.putString(utilityConstant.requestCatche, stringLocalBuilder.replace(0, 40, "").toString());
                                        editor.commit();
                                        // end storing in shared prefference


                                        StringBuilder stringBuilder = new StringBuilder(reference);
                                        String refvalue = stringBuilder.replace(0, 40, "").toString();
                                        DatabaseReference getMatchID = database.getReference(refvalue);
                                        getMatchID.child("userstatus").setValue("OFF");
                                        helper insert = new helper(context);
                                        ((Activity) context).stopService(new Intent(getContext(), services.class).putExtra("SERVICE", "STOP"));
                                        insert.deleteAll();


                                        break;
                                        // break when email address find

                                    }

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.w("Read Failed", "Failed to read value.", error.toException());
                            }
                        });
                    } else {
                        String id = "matchID-" + matches.getUnique_ID();
                        DatabaseReference databaseReference = database.getReference(Request + "/" + id);
                        databaseReference.child("userstatus").setValue("OFF");
                        helper insert = new helper(context);
                        ((Activity) context).stopService(new Intent(getContext(), services.class));
                        insert.deleteAll();
                    }

                }
            }
        });
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;

        if (button.getId() == setCoverage.getId()) {
            currentLiveMatches matches = (currentLiveMatches) liveMatches.get((Integer) v.getTag());
            utilityConstant.showToast(context, "Match id");

            Intent intent = ((Activity) context).getIntent();
            utilityConstant.showToast(context, "set Coverage");

            Intent addPerson = new Intent(getContext(), PersonsDetail.class);
            addPerson.putExtra("Email", intent.getStringExtra("Email"));
            addPerson.putExtra("matchId", matches.getUnique_ID());
            getContext().startActivity(addPerson);
        }

    }
}
