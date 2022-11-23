package com.snesnopic.ermes;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity{
    FloatingActionButton fab;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            Intent groupIntent = new Intent(this,CreateGroupActivity.class);
            startActivity(groupIntent);
        });
    }
}

