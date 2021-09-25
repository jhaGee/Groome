package com.e.groome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.e.groome.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class AdminLogin extends AppCompatActivity {

    private MaterialEditText et_name, et_pwd;
    private Button btn_signin;
    private CheckBox cb_remember;
    private String name, pwd, userName, userPwd;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        et_name = findViewById(R.id.SignIn_editText_user);
        et_pwd = findViewById(R.id.SignIn_editText_pwd);
        btn_signin = findViewById(R.id.button_login);
        cb_remember = findViewById(R.id.remember);

        mDialog = new ProgressDialog(AdminLogin.this, R.style.AppCompatAlertDialogStyle);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        SharedPreferences sh = getSharedPreferences("CodeCoy_GROOME", MODE_PRIVATE);
        if (sh.contains("CodeCoy_GROOME_Status")){
            mDialog.show();
            Boolean status = sh.getBoolean("CodeCoy_GROOME_Status", false);
            if (status == true){
                name = sh.getString("CodeCoy_GROOME_Status_Name", "");
                pwd = sh.getString("CodeCoy_GROOME_Status_Pwd", "");

                adminCredentials(table_user);
                /*Intent intent = new Intent(AdminLogin.this, StatusUpdate.class);
                startActivity(intent);
                finish();*/
            }else {
                mDialog.dismiss();
            }
        }

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(et_name.getText().toString()).exists()){
                            //Get User information
                            mDialog.dismiss();
                            User user = dataSnapshot.child(et_name.getText().toString()).getValue(User.class);
                            if (user.getPassword().equals(et_pwd.getText().toString())){
                                Toast.makeText(AdminLogin.this, "Sign in successfully !", Toast.LENGTH_SHORT).show();

                                if (cb_remember.isChecked()){
                                    SharedPreferences sharedPreferences = getSharedPreferences("CodeCoy_GROOME", MODE_PRIVATE);
                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                    myEdit.putBoolean("CodeCoy_GROOME_Status", true);
                                    myEdit.putString("CodeCoy_GROOME_Status_Name", et_name.getText().toString());
                                    myEdit.putString("CodeCoy_GROOME_Status_Pwd", et_pwd.getText().toString());
                                    myEdit.apply();
                                }

                                Intent intent = new Intent(AdminLogin.this, StatusUpdate.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(AdminLogin.this, "Wrong Password !!!", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            mDialog.dismiss();
                            Toast.makeText(AdminLogin.this, "User not exist !!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    private void adminCredentials(DatabaseReference table_user) {
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(name).exists()){
                    //Get User information
                    User user = dataSnapshot.child(name).getValue(User.class);
                    if (user.getPassword().equals(pwd)){
                        Toast.makeText(AdminLogin.this, "Sign in successfully !", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminLogin.this, StatusUpdate.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(AdminLogin.this, "Password is changed !!!", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }else {
                    Toast.makeText(AdminLogin.this, "User is changed !!!", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
