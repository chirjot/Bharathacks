package com.codebusters.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText et1,et2;
  String e1,e2;
    Button bn;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et1=(EditText)findViewById(R.id.userl);
        et2=(EditText)findViewById(R.id.passl);
        auth=FirebaseAuth.getInstance();
        bn=(Button)findViewById(R.id.loginl);
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e1=et1.getText().toString();
                e2=et2.getText().toString();
                auth.signInWithEmailAndPassword(e1,e2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(Login.this,Main2Activity.class));
                        }else
                            Toast.makeText(getApplicationContext(),"Not Valid",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });



    }
}
