package com.e.groome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Category extends AppCompatActivity {

    private Button btn_customer, btn_admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        btn_customer = findViewById(R.id.button_customer);
        btn_admin = findViewById(R.id.button_admin);

        btn_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Category.this, CustomerStatus.class);
                startActivity(intent);
            }
        });

        btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Category.this, AdminLogin.class);
                startActivity(intent);
            }
        });
    }
}
