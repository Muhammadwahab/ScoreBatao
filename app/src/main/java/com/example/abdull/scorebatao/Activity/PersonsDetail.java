package com.example.abdull.scorebatao.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.abdull.scorebatao.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pojo.Detail;
import utility.utilityConstant;

public class PersonsDetail extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    Intent intent;
    SharedPreferences storeUserRequest;
    ArrayList numbers;
    ArrayAdapter<String> arrayAdapter;
    LinearLayout linearLayout, horizontalLinearButtons;
    Spinner intervalSpinner, eventSpinner;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_detailed);
        intent = getIntent();
        storeUserRequest = getSharedPreferences(utilityConstant.MyPREFERENCES, Context.MODE_PRIVATE);
        numbers = new ArrayList<String>();
        // adding back button in activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ListView listView = (ListView) findViewById(R.id.addPerson);
        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, numbers);
        listView.setAdapter(arrayAdapter);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.NewNumber);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteNumber(id);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDailog();

            }
        });

        String Request;
        if ((Request = storeUserRequest.getString(utilityConstant.requestCatche, "null")).equalsIgnoreCase("null")) {

            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Iterable<DataSnapshot> childrenData = dataSnapshot.getChildren();

                    for (DataSnapshot child : childrenData) {
//                    DatabaseReference databaseReference=child.getRef();
//                    databaseReference.
                        HashMap hashMap = (HashMap) child.getValue();
                        String email = (String) hashMap.get("email");
                        if (email.equalsIgnoreCase(intent.getStringExtra("Email"))) {
                            DatabaseReference databaseReference = child.getRef();
                            String id = "matchID-" + intent.getLongExtra("matchId", -2);
                            String reference = databaseReference.toString() + "/" + "id" + "/" + id;

                            // storing in sharedprefference

                            String storeLocalReference = databaseReference.toString() + "/" + "id";
                            StringBuilder stringLocalBuilder = new StringBuilder(storeLocalReference);
                            SharedPreferences.Editor editor = storeUserRequest.edit();
                            editor.putString(utilityConstant.requestCatche, stringLocalBuilder.replace(0, 40, "").toString());
                            editor.putString(utilityConstant.emailRequest, new StringBuilder(databaseReference.toString()).replace(0, 40, "").toString());
                            editor.commit();
                            // end storing in shared prefference

                            StringBuilder stringBuilder = new StringBuilder(reference);
                            String refvalue = stringBuilder.replace(0, 40, "").toString();
                            DatabaseReference getMatchID = database.getReference(refvalue);
                            getMatchID.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    Iterable<DataSnapshot> childPhoneNumber = dataSnapshot.getChildren();
                                    numbers.clear();

                                    for (DataSnapshot phone : childPhoneNumber) {
                                        String key = phone.getKey();

                                        if (key.equalsIgnoreCase("status")) {

                                        } else if (key.equalsIgnoreCase("time")) {

                                        } else {
                                            numbers.add(key);
                                        }

                                    }
//                                    arrayAdapter.clear();
//                                    arrayAdapter.addAll(numbers);
                                    arrayAdapter.notifyDataSetChanged();


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            //arrayAdapter.clear();

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

        } else {
            String id = "/matchID-" + intent.getLongExtra("matchId", -2);
            DatabaseReference getMatchID = database.getReference(Request + id);
            getMatchID.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> childPhoneNumber = dataSnapshot.getChildren();
                    numbers.clear();
                    for (DataSnapshot phone : childPhoneNumber) {

                        String key = phone.getKey();
                        if (key.equalsIgnoreCase("status")) {

                        } else if (key.equalsIgnoreCase("time")) {

                        } else {
                            numbers.add(key);
                        }

                    }
//                                    arrayAdapter.clear();
//                                    arrayAdapter.addAll(numbers);
                    arrayAdapter.notifyDataSetChanged();


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.Logout:
                String verify = verifySignInMethod();
                if (verify.equalsIgnoreCase(utilityConstant.facebook)) {

                    LoginManager.getInstance().logOut();
                    clearData(); // clear all temporay data urls
                } else if (verify.equalsIgnoreCase(utilityConstant.custom)) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signOut();
                   clearData();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String verifySignInMethod() {
        sharedpreferences = getSharedPreferences(utilityConstant.MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.getString(utilityConstant.signInMethod, "null").equalsIgnoreCase(utilityConstant.facebook)) {
            return utilityConstant.facebook;
        } else if (sharedpreferences.getString(utilityConstant.signInMethod, "null").equalsIgnoreCase(utilityConstant.custom)) {
            return utilityConstant.custom;
        }


        return "null";
    }

    private void postDailog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // get layout inflator for setting layout in dailog
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_add_persons, null);

        final EditText name = (EditText) dialogView.findViewById(R.id.phoneName); //here
        final EditText PhoneNumber = (EditText) dialogView.findViewById(R.id.phoneNumber); //here
        final Button save = (Button) dialogView.findViewById(R.id.Save); //here
        final Button discard = (Button) dialogView.findViewById(R.id.Discard); //here
        // Spinner element
        // final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);
        // radio buttons
        RadioButton Interval = (RadioButton) dialogView.findViewById(R.id.radio_interval);
        RadioButton Event = (RadioButton) dialogView.findViewById(R.id.radio_event);
        RadioButton OffRadio = (RadioButton) dialogView.findViewById(R.id.radio_Of);

        // adding onCheck Listener in radio button

        Interval.setOnCheckedChangeListener(this);
        Event.setOnCheckedChangeListener(this);
        OffRadio.setOnCheckedChangeListener(this);

        // linear layout for adding child
        linearLayout = (LinearLayout) dialogView.findViewById(R.id.insertCoverge);
        // horizontal layout for buttons
        horizontalLinearButtons = (LinearLayout) dialogView.findViewById(R.id.horizontalButton);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//
//                ProgressDialog progress = new ProgressDialog(PersonsDetail.this);
//                progress.setTitle("Loading");
//                progress.setMessage("Wait while loading...");
//                progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
//                progress.show();
//// To dismiss the dialog
//              //  progress.dismiss();
                final String phoneNumber = PhoneNumber.getText().toString().trim();
                Pattern p = Pattern.compile("^[+]?[0-9]{11,13}$");
                Matcher m = p.matcher(phoneNumber);
                if (!m.matches()) {
                    showToast("Invalid PhoneNumber");

                } else {
                    String Request;
                    if ((Request = storeUserRequest.getString(utilityConstant.requestCatche, "null")).equalsIgnoreCase("null")) {
                        // Read from the database
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                Iterable<DataSnapshot> childrenData = dataSnapshot.getChildren();

                                for (DataSnapshot child : childrenData) {
//                    DatabaseReference databaseReference=child.getRef();
//                    databaseReference.
                                    HashMap hashMap = (HashMap) child.getValue();
                                    String email = (String) hashMap.get("email");
                                    if (email.equalsIgnoreCase(intent.getStringExtra("Email"))) {
                                        DatabaseReference databaseReference = child.getRef();
                                        String reference = databaseReference.toString() + "/" + "id";
                                        StringBuilder stringBuilder = new StringBuilder(reference);
                                        String refvalue = stringBuilder.replace(0, 40, "").toString();
                                        DatabaseReference setMatchID = database.getReference(refvalue);
                                        setMatchID.child("matchID-" + intent.getLongExtra("matchId", -2)).child(phoneNumber).setValue(new Detail(name.getText().toString().trim(), utilityConstant.UPDATE, utilityConstant.STATUS, getSpinner() != null ? getSpinner().getItemAtPosition(utilityConstant.spinnerItemPosition) + "" : "requestOFF"));
                                        // Toast.makeText(PersonsDetail.this, "Referecne is " + databaseReference.toString(), Toast.LENGTH_LONG).show();
                                        showToast("Referecne is " + databaseReference.toString());
                                        SharedPreferences.Editor editor = storeUserRequest.edit();
                                        editor.putString(utilityConstant.requestCatche, refvalue);
                                        editor.commit();
                                        break;

                                    }

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("Read Failed", "Failed to read value.", error.toException());
                            }
                        });
                    } else {
                        DatabaseReference setMatchID = database.getReference(Request);
                        setMatchID.child("matchID-" + intent.getLongExtra("matchId", -2)).child(phoneNumber).setValue(new Detail(name.getText().toString().trim(), utilityConstant.UPDATE, utilityConstant.STATUS, getSpinner() != null ? getSpinner().getItemAtPosition(utilityConstant.spinnerItemPosition) + "" : "request off"));
                    }
                }
            }
        });
        builder.setView(dialogView);
        AlertDialog dialogUpdate = builder.create();
        dialogUpdate.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(this, "Position is " + position, Toast.LENGTH_SHORT).show();
        utilityConstant.spinnerItemPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void deleteNumber(final long idDelete) {
        final AlertDialog.Builder optionBuilder = new AlertDialog.Builder(this);


        optionBuilder.setTitle("Options");
        optionBuilder.setMessage("Want to  Delete Record");
        optionBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String Request;
                Request = storeUserRequest.getString(utilityConstant.requestCatche, "null");
                String idlocal = "/matchID-" + intent.getLongExtra("matchId", -2) + "/" + numbers.get((int) idDelete);
                DatabaseReference DeleteNumber = database.getReference(Request + idlocal);
                DeleteNumber.removeValue();


            }
        });

        optionBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog optionAlert = optionBuilder.create();
        optionAlert.show();


    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {

        if (v.getId() == R.id.radio_interval) {

            if (isChecked) {
                intervalSpinner = new Spinner(this);
                ArrayAdapter<CharSequence> adapter = getAdapter(R.array.timeofMatch);
                utilityConstant.UPDATE = utilityConstant.INTEVAL;
                utilityConstant.STATUS = utilityConstant.ON;
                intervalSpinner.setAdapter(adapter);
                linearLayout.removeView(eventSpinner);
                eventSpinner = null;
                linearLayout.addView(intervalSpinner);
                linearLayout.removeView(horizontalLinearButtons);
                linearLayout.addView(horizontalLinearButtons);
                horizontalLinearButtons.setVisibility(View.VISIBLE);
                intervalSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
            }

            showToast("Interval");
        } else if (v.getId() == R.id.radio_event) {

            if (isChecked) {
                eventSpinner = new Spinner(this);
                ArrayAdapter<CharSequence> adapter = getAdapter(R.array.EventOfMatch);
                utilityConstant.UPDATE = utilityConstant.EVENT;
                utilityConstant.STATUS = utilityConstant.ON;
                eventSpinner.setAdapter(adapter);
                linearLayout.removeView(intervalSpinner);
                intervalSpinner = null;
                linearLayout.addView(eventSpinner);
                linearLayout.removeView(horizontalLinearButtons);
                linearLayout.addView(horizontalLinearButtons);
                horizontalLinearButtons.setVisibility(View.VISIBLE);
                eventSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

            }
        } else {

            if (isChecked) {
                linearLayout.removeView(eventSpinner);
                linearLayout.removeView(intervalSpinner);
                eventSpinner = null;
                intervalSpinner = null;
                horizontalLinearButtons.setVisibility(View.VISIBLE);
                utilityConstant.STATUS = utilityConstant.OFF;
                utilityConstant.UPDATE = "update " + utilityConstant.OFF;
            }
        }
    }

    Spinner getSpinner() {
        if (intervalSpinner == null)
            return eventSpinner;
        return intervalSpinner;
    }

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    ArrayAdapter getAdapter(int arrayID) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayID, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
    void clearData()
    {
        SharedPreferences preferences = getSharedPreferences(utilityConstant.MyPREFERENCES, 0);
        preferences.edit().remove(utilityConstant.requestCatche).commit();
        preferences.edit().remove(utilityConstant.emailRequest).commit();
        preferences.edit().remove(utilityConstant.email).commit();
        preferences.edit().remove(utilityConstant.signInMethod).commit();
        startActivity(new Intent(PersonsDetail.this, MainActivity.class));
    }
}
