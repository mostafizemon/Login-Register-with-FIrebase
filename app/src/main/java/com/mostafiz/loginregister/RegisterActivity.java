package com.mostafiz.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.hardware.camera2.CameraExtensionSession;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    CheckBox registercheckbox;
    ProgressBar registerprogressbar;
    TextInputEditText registerfullname, registeremail,registerphone,registerpassword,registerdob;
    TextView alreadyhaveanaccount;
    Button buttonregister;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        alreadyhaveanaccount=findViewById(R.id.alreadyihaveanaccount);
        buttonregister=findViewById(R.id.buttonregister);
        registerfullname=findViewById(R.id.registerfullname);
        registeremail=findViewById(R.id.registeremail);
        registerphone=findViewById(R.id.registerphone);
        registerpassword=findViewById(R.id.registerpassword);
        registerprogressbar=findViewById(R.id.registerprogressbar);
        registercheckbox=findViewById(R.id.registercheckbox);
        registerdob=findViewById(R.id.registerdob);
        registerdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar=Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);
                new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        registerdob.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day).show();
            }
        });

        alreadyhaveanaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
        registercheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Enable registerButton if CheckBox is checked, disable it otherwise
                buttonregister.setEnabled(isChecked);
            }
        });

        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname=registerfullname.getText().toString();
                String email=registeremail.getText().toString();
                String phone=registerphone.getText().toString();
                String password=registerpassword.getText().toString();
                String dob=registerdob.getText().toString();
                if (dob.isEmpty()){
                    registerdob.setError("Enter Your Date of Birth");
                }
                if (fullname.isEmpty()){
                    registerfullname.setError("Enter Your Full Name");
                    registerfullname.requestFocus();
                }
                if (email.isEmpty()){
                    registeremail.setError("Enter Your  Email");
                    registeremail.requestFocus();
                }
                if (phone.isEmpty()){
                    registerphone.setError("Enter Your phone number");
                    registerphone.requestFocus();
                }
                if (password.isEmpty()){
                    registerpassword.setError("Enter Your password");
                    registerpassword.requestFocus();
                }
                if (phone.length()!=11){
                    registerphone.setError("Please Enter Your Valid Phone Number");
                    registerphone.requestFocus();
                }

                else {
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        registerprogressbar.setVisibility(View.VISIBLE);
                        registeruser(fullname, email, phone, password,dob);
                    }
                    else {
                        registeremail.setError("Please Enter Your Valid Email");
                        registeremail.requestFocus();
                    }
                }
            }
        });






    }




    private  void registeruser(String name,String email,String phone, String password,String dob){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

                    ReadWriteuserDetails readWriteuserDetails=new ReadWriteuserDetails(name,phone,dob);
                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(firebaseUser.getUid()).setValue(readWriteuserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser.sendEmailVerification();
                                registerprogressbar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Register Successfully, Please Verify Your Email", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(RegisterActivity.this,"Register Failed,Please Try again",Toast.LENGTH_SHORT).show();
                                registerprogressbar.setVisibility(View.GONE);
                            }
                        }
                    });


                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e){
                        registerpassword.setError("Your Password is too weak");
                        registerpassword.requestFocus();
                        registerprogressbar.setVisibility(View.GONE);
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        registerpassword.setError("Your email or password is invalid, kindly re-check your email and password");
                        registerpassword.requestFocus();
                        registerprogressbar.setVisibility(View.GONE);
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        registeremail.setError("Your email is already registered");
                        registeremail.requestFocus();
                        registerprogressbar.setVisibility(View.GONE);

                    }
                    catch (Exception e){
                        Log.e("registeractivity", e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }



}