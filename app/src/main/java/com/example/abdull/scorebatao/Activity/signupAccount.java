package com.example.abdull.scorebatao.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import utility.utilityConstant;

public class signupAccount extends AppCompatActivity implements View.OnClickListener {

    TextView email, password, confirmpassword;
    Button createAccount;
    private FirebaseAuth mAuth;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_account);

        mAuth = FirebaseAuth.getInstance();
        email = (TextView) findViewById(R.id.EmailIDSignup);
        password = (TextView) findViewById(R.id.passwordsignup);
        confirmpassword = (TextView) findViewById(R.id.confirmpasswordsignup);

        //intialize listners
        createAccount = (Button) findViewById(R.id.createAccount);
        createAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        progress = new ProgressDialog(signupAccount.this);
        progress.setTitle("Loading");
        progress.setMessage("Creating Account Please Wait...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

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
                                        progress.dismiss();
                                        utilityConstant.showToast(getApplicationContext(),"week");

                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        progress.dismiss();
                                        utilityConstant.showToast(getApplicationContext(),"invalid" + e);

                                    } catch (FirebaseAuthUserCollisionException e) {
                                        progress.dismiss();
                                        utilityConstant.showToast(getApplicationContext(),"usercollison"+e);

                                    } catch (Exception e) {
                                        progress.dismiss();
                                        utilityConstant.showToast(getApplicationContext(),"extra "+e);

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
                                                progress.dismiss();
                                                utilityConstant.showToast(getApplicationContext(),"email send to recipeient ");
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        } else {
            progress.dismiss();
            utilityConstant.showToast(getApplicationContext(),"password did not match");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}

