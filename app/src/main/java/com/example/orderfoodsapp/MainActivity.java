package com.example.orderfoodsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

        Button btnSignUp, btnSignIn;
        TextView txtSlogan;

        @Override
        protected void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            btnSignIn = (Button) findViewById(R.id.btnSignIn);
            btnSignUp = (Button) findViewById(R.id.btnSignUp);
            txtSlogan = (TextView) findViewById(R.id.txtSlogan);
            Typeface face = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
            txtSlogan.setTypeface(face);

            btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);


                }
            });
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,SignUp.class);
                    startActivity(intent);
                }
            });



        }
    }