package com.example.orderfoodsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicHistogram;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orderfoodsapp.Common.Common;
import com.example.orderfoodsapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignIn extends AppCompatActivity {
    EditText edtPhone, edtPassword;
    FButton btnSignIn;
    CheckBox ckbRemember;
    TextView txtForgotPwd;
    FirebaseDatabase database;
    DatabaseReference table_user;
    RelativeLayout relativeLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Gothic.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_sign_in);

        relativeLayout = (RelativeLayout) findViewById(R.id.mhLogin);
        relativeLayout.setBackgroundResource(R.drawable.background);

        ckbRemember = (CheckBox) findViewById(R.id.ckbRemeber);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        btnSignIn = (FButton) findViewById(R.id.btnSignIn);
        txtForgotPwd = (TextView) findViewById(R.id.txtFogotpwd);

        // init paper
        Paper.init(this);
        // init firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFogotPwdDialog();
            }
        });


        // init paper
        Paper.init(this);
        // init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {


                    if (ckbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                    }
                    // save user & password
                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please waiting...");
                    mDialog.show();
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Check if user not exit in database
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                // Get User Information
                                mDialog.dismiss();
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString());
                                if (user.getPassword().equals(edtPassword.getText().toString())) {
                                    {
                                        Intent homeIntent = new Intent(SignIn.this, Home.class);
                                        Common.currentUser = user;
                                        startActivity(homeIntent);
                                        finish();

                                        table_user.removeEventListener(this);
                                    }

                                } else {
                                    Toast.makeText(SignIn.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "User not exist!!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SignIn.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void showFogotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");
        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout, null);
        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);
        MaterialEditText editPhone = (MaterialEditText) forgot_view.findViewById(R.id.edtPhone);
        final MaterialEditText editSecureCode = (MaterialEditText) forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // check if user avaliable
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        if (user.getSecureCode().equals(editSecureCode.getText().toString())) {
                            Toast.makeText(SignIn.this, "Your password : " + user.getPassword(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignIn.this, "Wrong sercure code", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();


    }
}
