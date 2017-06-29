package com.example.abdull.scorebatao.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.abdull.scorebatao.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Fragment.currentMatch;
import utility.utilityConstant;

public class listOfMatch extends AppCompatActivity {
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_match);

        Intent intent=getIntent();
        Bundle bundle=new Bundle();
        bundle.putString("Email",intent.getStringExtra("Email"));

        currentMatch currentMatch=new currentMatch();
        currentMatch.setArguments(bundle);
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.currentMatch,currentMatch);
        fragmentTransaction.commit();


        //getSupportFragmentManager().beginTransaction().add().commit();
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
                String verify=verifySignInMethod();
                if(verify.equalsIgnoreCase(utilityConstant.facebook))
                {

                    LoginManager.getInstance().logOut();
                    clearData();
                }
                else if(verify.equalsIgnoreCase(utilityConstant.custom))
                {
                    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                    firebaseAuth.signOut();
                    clearData();

                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public String verifySignInMethod()
    {
        sharedpreferences = getSharedPreferences(utilityConstant.MyPREFERENCES, Context.MODE_PRIVATE);
        if( sharedpreferences.getString(utilityConstant.signInMethod,"null").equalsIgnoreCase(utilityConstant.facebook))
        {
            return utilityConstant.facebook;
        }
        else  if( sharedpreferences.getString(utilityConstant.signInMethod,"null").equalsIgnoreCase(utilityConstant.custom))
        {
            return utilityConstant.custom;
        }


        return "null";
    }
    void clearData()
    {
        SharedPreferences preferences = getSharedPreferences(utilityConstant.MyPREFERENCES, 0);
        preferences.edit().remove(utilityConstant.requestCatche).commit();
        preferences.edit().remove(utilityConstant.emailRequest).commit();
        preferences.edit().remove(utilityConstant.email).commit();
        preferences.edit().remove(utilityConstant.signInMethod).commit();
        startActivity(new Intent(listOfMatch.this, MainActivity.class));
    }
}


