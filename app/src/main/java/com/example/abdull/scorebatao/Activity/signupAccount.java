package com.example.abdull.scorebatao.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdull.scorebatao.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pojo.user;

public class signupAccount extends AppCompatActivity implements View.OnClickListener {

    TextView email, password, confirmpassword;
    Button createAccount;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
     DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_account);

        // initilaze firebase

        mAuth = FirebaseAuth.getInstance();



        // initialize views

        email = (TextView) findViewById(R.id.EmailIDSignup);
        password = (TextView) findViewById(R.id.passwordsignup);
        confirmpassword = (TextView) findViewById(R.id.confirmpasswordsignup);

        //intialize listners
        createAccount = (Button) findViewById(R.id.createAccount);
        createAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // checn of create account
        if (v.equals(createAccount)) {
            // checnk password match or not
            if (password.getText().toString().trim().equals(confirmpassword.getText().toString().trim())) {
                // create new account
                mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                //  throw certain Exceptions
                                if (!task.isSuccessful()) {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        Toast.makeText(signupAccount.this, "week" + e, Toast.LENGTH_LONG).show();

                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        Toast.makeText(signupAccount.this, "invalid" + e, Toast.LENGTH_LONG).show();
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        Toast.makeText(signupAccount.this, "usercollison" + e, Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(signupAccount.this, "extra" + e, Toast.LENGTH_LONG).show();
                                    }
                                } else {       // get current user
                                    final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    // send email to user
                                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                final DatabaseReference myRef = database.getReference("users");

                                                String userId = myRef.push().getKey();

                                                // user insert
                              String emailOfFB=firebaseUser.getEmail();
                                user userData = new user(emailOfFB,"ID-3333");
                                                myRef.child(userId).setValue(userData);

                                                Toast.makeText(signupAccount.this, "email send to recipeient " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(signupAccount.this, "password did not match",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
