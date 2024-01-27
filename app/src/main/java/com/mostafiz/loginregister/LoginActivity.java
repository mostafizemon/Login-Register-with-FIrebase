package com.mostafiz.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextView ihavenoaccount;
    TextInputEditText loginemail,loginpassword;
    ProgressBar loginprogressbar;
    FirebaseAuth loginfirebaseauth;
    Button loginbutton;
    CheckBox logincheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ihavenoaccount=findViewById(R.id.ihavenoaccount);
        loginemail=findViewById(R.id.loginemail);
        loginpassword=findViewById(R.id.loginpassword);
        loginprogressbar=findViewById(R.id.loginprogressbar);
        loginbutton=findViewById(R.id.buttonlogin);
        logincheckbox=findViewById(R.id.logincheckbox);
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        logincheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Enable registerButton if CheckBox is checked, disable it otherwise
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("rememberMe", isChecked);
                editor.apply();

            }
        });
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        if (rememberMe) {
            // Directly go to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        ihavenoaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        loginfirebaseauth=FirebaseAuth.getInstance();
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=loginemail.getText().toString();
                String password=loginpassword.getText().toString();

                if (email.isEmpty()){
                    loginemail.setError("Enter Your Email");
                    loginemail.requestFocus();
                }
                if (password.isEmpty()){
                    loginpassword.setError("Enter Your Password");
                    loginpassword.requestFocus();
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    loginemail.setError("Valid Email Required");
                    loginemail.requestFocus();
                }
                else {
                    loginprogressbar.setVisibility(View.VISIBLE);
                    loginuser(email,password);
                }
            }
        });







    }



    private void loginuser(String email, String password) {
        loginfirebaseauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser=loginfirebaseauth.getCurrentUser();
                    if (firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivity.this,"Login Successfull",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    else {
                        firebaseUser.sendEmailVerification();
                        loginfirebaseauth.signOut();
                        showalertdialog();
                    }
                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        Toast.makeText(LoginActivity.this,"User Does not Exists, Please Register",Toast.LENGTH_SHORT).show();
                        loginemail.setError("User Does not Exists");
                        loginemail.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        Toast.makeText(LoginActivity.this,"Email or password is Invalid",Toast.LENGTH_SHORT).show();
                        loginemail.setError("Email and password is invalid");
                        loginemail.requestFocus();
                    }
                    catch (Exception e){
                        Log.e("loginexception",e.getMessage());
                        Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

                loginprogressbar.setVisibility(View.GONE);

            }
        });
    }

    private void showalertdialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Verification Required");
        builder.setMessage("Please Verify Your Email now, You can not login without email verification");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (loginfirebaseauth.getCurrentUser() !=null) {
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
//        }
//    }


}