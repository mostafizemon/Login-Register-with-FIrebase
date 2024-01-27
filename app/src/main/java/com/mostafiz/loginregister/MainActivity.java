package com.mostafiz.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    TextView userfullname,useremail,userphone,userdob;
    private String fullname,email,phone,dob;
    FirebaseAuth firebaseuserauth;
    Button buttonlogout,buttonforgetpass;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userfullname=findViewById(R.id.userfullname);
        useremail=findViewById(R.id.useremail);
        userphone=findViewById(R.id.userphone);
        userdob=findViewById(R.id.userdob);
        buttonlogout=findViewById(R.id.buttonlogout);
        buttonforgetpass=findViewById(R.id.buttonforgetpass);

        buttonforgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ForgetpassActivity.class));
            }
        });
        sharedPreferences = getSharedPreferences("loginPrefs", MainActivity.this.MODE_PRIVATE);

        buttonlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();

            }
        });
        firebaseuserauth=FirebaseAuth.getInstance();
        FirebaseUser firebaseuser=firebaseuserauth.getCurrentUser();
        if (firebaseuser==null){
            Toast.makeText(MainActivity.this,"Something Went Wrong!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }
        else {
            showuserdetails(firebaseuser);
        }
    }

    private void showuserdetails(FirebaseUser firebaseuser) {
        String userid=firebaseuser.getUid();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteuserDetails readuserdetails=snapshot.getValue(ReadWriteuserDetails.class);
                if (readuserdetails!=null){
                    email=firebaseuser.getEmail();
                    dob=readuserdetails.dob;
                    phone=readuserdetails.phone;
                    fullname=readuserdetails.fullname;

                    userfullname.setText(fullname);
                    useremail.setText(email);
                    userphone.setText(phone);
                    userdob.setText(dob);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"Something Went Wrong!",Toast.LENGTH_SHORT).show();

            }
        });
    }
}