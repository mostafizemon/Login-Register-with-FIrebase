package com.mostafiz.loginregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgetpassActivity extends AppCompatActivity {
    EditText forgetemail;
    Button forgetbutton;
    private String email;
    FirebaseAuth forgetauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpass);
        forgetbutton=findViewById(R.id.forgetbutton);
        forgetemail=findViewById(R.id.forgetemail);

        forgetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=forgetemail.getText().toString();
                if (email.isEmpty()){
                    forgetemail.setError("Enter Your Email");
                    forgetemail.requestFocus();
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    forgetemail.setError("Valid Email Required");
                    forgetemail.requestFocus();
                }
                else {
                    resetpassword(email);
                }
            }
        });



    }

    private void resetpassword(String email) {
        forgetauth=FirebaseAuth.getInstance();
        forgetauth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgetpassActivity.this,"Please Check your email for reset link",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ForgetpassActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        forgetemail.setError("user does not exists");
                        forgetemail.requestFocus();
                    }
                    catch (Exception e){
                        Log.e("resetpass", e.getMessage());
                        Toast.makeText(ForgetpassActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }


}