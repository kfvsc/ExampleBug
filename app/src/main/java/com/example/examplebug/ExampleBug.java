package com.example.examplebug;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ExampleBug extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getSupportActionBar().hide();
        setContentView(new MainView(this));
    }
}
