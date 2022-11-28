package com.snesnopic.ermes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CreateGroupActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        getSupportActionBar().hide();     //nasconde il nome sopra l'app
        setContentView(R.layout.create_group_activity);

    }
}
