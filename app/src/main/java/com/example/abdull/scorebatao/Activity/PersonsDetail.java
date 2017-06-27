package com.example.abdull.scorebatao.Activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdull.scorebatao.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import utility.utilityConstant;

public class PersonsDetail extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener,CompoundButton.OnCheckedChangeListener {
    String name[] = {"wahab", "wahab", "wahab", "wahab", "wahab", "wahab"};
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    Intent intent;
    SharedPreferences storeUserRequest;
    ArrayList numbers;
    ArrayAdapter<String> arrayAdapter;
    private SharedPreferences sharedpreferences;
    LinearLayout linearLayout;
    TextView textView=null;
    Spinner intervalSpinner,eventSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_detailed);
        intent = getIntent();
        storeUserRequest = getSharedPreferences(utilityConstant.MyPREFERENCES, Context.MODE_PRIVATE);
        numbers = new ArrayList<String>();


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
                    startActivity(new Intent(PersonsDetail.this, MainActivity.class));
                    LoginManager.getInstance().logOut();
                    SharedPreferences preferences = getSharedPreferences(utilityConstant.MyPREFERENCES, 0);
                    preferences.edit().remove(utilityConstant.requestCatche).commit();
                    preferences.edit().remove(utilityConstant.emailRequest).commit();
                    preferences.edit().remove(utilityConstant.email).commit();
                    preferences.edit().remove(utilityConstant.signInMethod).commit();
                } else if (verify.equalsIgnoreCase(utilityConstant.custom)) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signOut();
                    SharedPreferences preferences = getSharedPreferences(utilityConstant.MyPREFERENCES, 0);
                    preferences.edit().remove(utilityConstant.requestCatche).commit();
                    preferences.edit().remove(utilityConstant.emailRequest).commit();
                    preferences.edit().remove(utilityConstant.email).commit();
                    preferences.edit().remove(utilityConstant.signInMethod).commit();
                    startActivity(new Intent(PersonsDetail.this, MainActivity.class));
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
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_add_persons, null);
        final EditText PhoneNumber = (EditText) dialogView.findViewById(R.id.phoneNumber); //here
        final Button save = (Button) dialogView.findViewById(R.id.Save); //here
        final Button discard = (Button) dialogView.findViewById(R.id.Discard); //here
        // Spinner element
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);
        // radio button
        RadioButton Interval= (RadioButton) dialogView.findViewById(R.id.radio_interval);
        RadioButton Event= (RadioButton) dialogView.findViewById(R.id.radio_event);
        RadioButton OffRadio= (RadioButton) dialogView.findViewById(R.id.radio_Of);
       // Interval.setOnClickListener((View.OnClickListener) this);
        Interval.setOnCheckedChangeListener(this);
        Event.setOnCheckedChangeListener(this);
        OffRadio.setOnCheckedChangeListener(this);
//        Event.setOnClickListener((View.OnClickListener) this);
//        OffRadio.setOnClickListener((View.OnClickListener) this);

        // linear layout for adding child

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.insertCoverge);







// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.timeofMatch, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

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
                if (phoneNumber.equalsIgnoreCase("")) {
                    Toast.makeText(PersonsDetail.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();

                    Toast.makeText(PersonsDetail.this, "item is " + spinner.getItemAtPosition(utilityConstant.spinnerItemPosition), Toast.LENGTH_SHORT).show();
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
                                        setMatchID.child("matchID-" + intent.getLongExtra("matchId", -2)).child(phoneNumber).setValue(spinner.getItemAtPosition(utilityConstant.spinnerItemPosition) + "-OFF");
                                        Toast.makeText(PersonsDetail.this, "Referecne is " + databaseReference.toString(), Toast.LENGTH_LONG).show();
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
                        setMatchID.child("matchID-" + intent.getLongExtra("matchId", -2)).child(phoneNumber).setValue(spinner.getItemAtPosition(utilityConstant.spinnerItemPosition) + "-OFF");

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
    public void onClick(View v) {
        if(v.getId()==R.id.radio_interval)
        {
            boolean checked = ((RadioButton) v).isChecked();
            Toast.makeText(this, ""+checked, Toast.LENGTH_SHORT).show();
            if(!checked)
            {
                intervalSpinner=new Spinner(this);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.timeofMatch, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                intervalSpinner.setAdapter(adapter);
                linearLayout.removeView(eventSpinner);
                linearLayout.addView(intervalSpinner);
            }

            Toast.makeText(this, "Interval", Toast.LENGTH_SHORT).show();
        }
        else if(v.getId()==R.id.radio_event)
        {
            boolean checked = ((RadioButton) v).isChecked();
            Toast.makeText(this, ""+checked, Toast.LENGTH_SHORT).show();
            if(!checked)
            {
                eventSpinner=new Spinner(this);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.EventOfMatch, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                eventSpinner.setAdapter(adapter);
                linearLayout.removeView(intervalSpinner);
                linearLayout.addView(eventSpinner);
            }


            Toast.makeText(this, "Event", Toast.LENGTH_SHORT).show();
        }

        else
        {
            Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show();
            linearLayout.removeView(eventSpinner);
            linearLayout.removeView(intervalSpinner);

        }

    }



    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {

        if(v.getId()==R.id.radio_interval)
        {

            if(isChecked)
            {
                intervalSpinner=new Spinner(this);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.timeofMatch, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                intervalSpinner.setAdapter(adapter);
                linearLayout.removeView(eventSpinner);
                linearLayout.addView(intervalSpinner);
            }

            Toast.makeText(this, "Interval", Toast.LENGTH_SHORT).show();
        }
        else if(v.getId()==R.id.radio_event)
        {

            if(isChecked)
            {
                eventSpinner=new Spinner(this);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.EventOfMatch, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                eventSpinner.setAdapter(adapter);
                linearLayout.removeView(intervalSpinner);
                linearLayout.addView(eventSpinner);
            }


            Toast.makeText(this, "Event", Toast.LENGTH_SHORT).show();
        }

        else
        {
            if(isChecked)
            {
                linearLayout.removeView(eventSpinner);
                linearLayout.removeView(intervalSpinner);
            }
            Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show();



        }


    }
}
