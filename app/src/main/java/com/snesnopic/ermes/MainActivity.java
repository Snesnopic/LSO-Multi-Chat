package com.snesnopic.ermes;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    MainActivity(Bundle savedInstanceState, Context context)
    {
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            setContentView(R.layout.create_group_activity);
        });
    }
}

