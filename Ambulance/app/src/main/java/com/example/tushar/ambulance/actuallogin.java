package com.example.tushar.ambulance;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class actuallogin extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText name,pass;
    Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actuallogin);
        mAuth=FirebaseAuth.getInstance();
        name=(EditText)findViewById(R.id.user2);
        pass=(EditText)findViewById(R.id.pass2);
        btn=(Button)findViewById(R.id.login2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
            }
        });

    }
    public void post(){
        String use=name.getText().toString();
        String pas=pass.getText().toString();
        mAuth.signInWithEmailAndPassword(use,pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(actuallogin.this,Checkuser.class));

                }
                else Toast.makeText(getApplicationContext(),"Invalid login",Toast.LENGTH_LONG).show();

            }
        });
    }
}
