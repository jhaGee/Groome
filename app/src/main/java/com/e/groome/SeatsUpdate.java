package com.e.groome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatsUpdate extends AppCompatActivity {

    private  Updates updates;
    private List<Updates> updatesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UpdatesAdapter updatesAdapter;

    private Button btn_newSeat, btn_save, btn_deleteSeat, btn_logout;
    private ElegantNumberButton et_que;
    private List<String> keysList = new ArrayList<>();
    int count;
    private String seat;
    private boolean free, notFree;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seats_update);

        mDialog = new ProgressDialog(SeatsUpdate.this, R.style.AppCompatAlertDialogStyle);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);

        recyclerView = findViewById(R.id.update_recyclerView);

        updatesAdapter = new UpdatesAdapter(updatesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(updatesAdapter);

        btn_newSeat = findViewById(R.id.button_newSeat);
        btn_save = findViewById(R.id.button_save);
        btn_deleteSeat = findViewById(R.id.button_deleteSeat);
        btn_logout = findViewById(R.id.button_logout_seats);
        et_que = findViewById(R.id.editText_que);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("CodeCoy_GROOME", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean("CodeCoy_GROOME_Status", false);
                myEdit.apply();
                Intent intent = new Intent(SeatsUpdate.this, AdminLogin.class);
                startActivity(intent);
                finish();
            }
        });

        btn_newSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();

                count = recyclerView.getAdapter().getItemCount() + 1;
                updatesList.clear();
                updatesAdapter.notifyDataSetChanged();

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Saloon_Seats");

                String key = dbRef.push().getKey();

                Map userInfo = new HashMap();
                userInfo.put("seat" , "Seat "+ count);
                userInfo.put("free", true);
                userInfo.put("notFree", false);
                dbRef.child(key).updateChildren(userInfo);

                prepareUpdatesData();
            }
        });

        btn_deleteSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = keysList.size();
                String lastKey = keysList.get(index - 1);

                Log.println(Log.ASSERT, "index", index+" "+ lastKey);

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Saloon_Seats").child(lastKey);
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                prepareUpdatesData();
                            }
                        }, 500);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
                final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Que");
                dbRef.setValue(et_que.getNumber());
                final DatabaseReference dbRefMain = FirebaseDatabase.getInstance().getReference().child("Saloon_Seats");

                for (int i = 0; i < updatesList.size(); i++){
                    String key = keysList.get(i);
                    Updates newUpdates = updatesList.get(i);

                    boolean uFree = newUpdates.isFree();
                    boolean uNotfree = newUpdates.isNotFree();

                    Map userInfo = new HashMap();
                    userInfo.put("free", uFree);
                    userInfo.put("notFree", uNotfree);
                    dbRefMain.child(key).updateChildren(userInfo);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDialog.dismiss();
                        }
                    }, 1000);
                }
            }
        });

        prepareUpdatesData();
    }

    private void updateQue(){
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Que");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                et_que.setNumber(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void prepareUpdatesData() {
        updatesList.clear();
        keysList.clear();
        updatesAdapter.notifyDataSetChanged();
        updateQue();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Saloon_Seats");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updatesList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    mDialog.show();

                    String entry = ds.getKey();
                    keysList.add(entry);
                    prepareUpdatesDataEntries(dbRef, entry);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void prepareUpdatesDataEntries(DatabaseReference dbRef, final String entry) {
        dbRef.child(entry).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.getKey().equals("seat")){
                        seat = ds.getValue().toString();
                    }
                    if (ds.getKey().equals("free")){
                        free = (boolean) ds.getValue();
                    }
                    if (ds.getKey().equals("notFree")){
                        notFree = (boolean) ds.getValue();
                    }
                    if (seat != null){
                        updates = new Updates(seat, entry, free, notFree);
                        updatesList.add(updates);

                        seat = null;
                        free = false;
                        notFree = false;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                            }
                        }, 1500);
                    }

                    updatesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SeatsUpdate.this, StatusUpdate.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
