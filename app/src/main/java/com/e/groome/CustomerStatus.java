package com.e.groome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerStatus extends AppCompatActivity {

    private List<Seats> seatsList = new ArrayList<>();
    private Seats seats;
    private RecyclerView recyclerView;
    private SeatsAdapter seatsAdapter;
    private TextView tv_que;
    private List<String> keysList = new ArrayList<>();
    private String seat;
    private boolean free, notFree;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_status);
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               checkStatus();
                pullToRefresh.setRefreshing(false);
            }
        });

        mDialog = new ProgressDialog(CustomerStatus.this, R.style.AppCompatAlertDialogStyle);
        mDialog.setMessage("Please wait...");
        mDialog.show();

        checkStatus();

        recyclerView = findViewById(R.id.customer_recyclerView);
        tv_que = findViewById(R.id.que_customer);

        seatsAdapter = new SeatsAdapter(seatsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(seatsAdapter);
    }

    private void checkStatus() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Shop_Status");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue().toString();
                if (status.equals("Close")){
                    mDialog.dismiss();
                    Intent intent = new Intent(CustomerStatus.this, ShopStatus.class);
                    startActivity(intent);
                    finish();
                }else {
                    prepareSeatsData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateQue(){
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Que");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_que.setText("Customers in que : "+ dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void prepareSeatsData() {
        seatsList.clear();
        seatsAdapter.notifyDataSetChanged();
        updateQue();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Saloon_Seats");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                seatsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    mDialog.show();
                    String entry = ds.getKey();
                    keysList.add(entry);
                    prepareSeatsDataEntries(dbRef, entry);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void prepareSeatsDataEntries(DatabaseReference dbRef, String entry) {
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
                        seats = new Seats(seat, free, notFree);
                        seatsList.add(seats);

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

                    seatsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
