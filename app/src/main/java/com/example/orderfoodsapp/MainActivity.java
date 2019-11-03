package com.example.orderfoodsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

    public class MainActivity extends AppCompatActivity {

        Button btnSignUp, btnSignIn;
        TextView txtSlogan;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            btnSignIn = (Button) findViewById(R.id.btnSignIn);
            btnSignUp = (Button) findViewById(R.id.btnSignUp);
            txtSlogan = (TextView) findViewById(R.id.txtSlogan);

//            btnSignUp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent SignUp = new Intent(MainActivity.this, SignUp.class);
//                    startActivity(SignUp);
//                }
//            });
//
//            btnSignIn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent SignIN = new Intent(MainActivity.this, SignIn.class);
//                    startActivity(SignIN);
//                }
//            });

        }
    }