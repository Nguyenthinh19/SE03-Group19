package com.example.orderfoodsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orderfoodsapp.Common.Common;
import com.example.orderfoodsapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

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

            //init paper
            Paper.init(this);

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
            // Check remember
            String user =Paper.book().read(Common.USER_KEY);
            String pwd = Paper.book().read(Common.PWD_KEY);
            if (user != null && pwd != null) {
                if (!user.isEmpty()  && !pwd.isEmpty()) {
                    login(user,pwd);
                }
            }



        }

    private void login(final String phone, final String pwd) {
        // init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        if (Common.isConnectedToInternet(getBaseContext())) {
            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please waiting...");
            mDialog.show();
            table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Check if user not exit in database

                    if (dataSnapshot.child(phone).exists()) {
                        // Get User Information
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPassword().equals(pwd)) {
                            {
                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                Common.currentUser = user;
                                startActivity(homeIntent);
                                finish();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User not exist!!!", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(MainActivity.this,"Please check Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}