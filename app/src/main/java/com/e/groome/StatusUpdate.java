package com.e.groome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatusUpdate extends AppCompatActivity {

    private Button btn_updateSeats, btn_logout;
    private CheckBox cb_closed;
//    private CheckBox cb_open;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Shop_Status");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_update);

        btn_updateSeats = findViewById(R.id.button_updateSeats);
        btn_logout = findViewById(R.id.button_logout_update);
        cb_closed = findViewById(R.id.checkbox_close);
//        cb_open   =findViewById(R.id.checkbox_Open);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("CodeCoy_GROOME", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean("CodeCoy_GROOME_Status", false);
                myEdit.apply();
                Intent intent = new Intent(StatusUpdate.this, AdminLogin.class);
                startActivity(intent);
                finish();
            }
        });

        btn_updateSeats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusUpdate.this, SeatsUpdate.class);
                startActivity(intent);
                finish();
            }
        });

        cb_closed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    dbRef.setValue("Close");
                    cb_closed.setText("Saloon is close");
                }
                else {
                    dbRef.setValue("Open");
                    cb_closed.setText("Saloon is open");
                }
            }
        });
//        cb_open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    dbRef.setValue("Open");
//                    cb_closed.setText("Saloon is open");
//                }
//                else{
//
//                }
//            }
//        });

        checkStatus();
    }

    private void checkStatus() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue().toString();
                if (status.equals("Close")){
                    cb_closed.setChecked(true);
                }else {
                    cb_closed.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
