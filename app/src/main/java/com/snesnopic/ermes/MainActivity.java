package com.snesnopic.ermes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    TabAdapter tabAdapter;
    FloatingActionButton options;
    FloatingActionButton createGroup;
    FloatingActionButton exit;
    int count = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tabAdapter = new TabAdapter(this);
        viewPager.setAdapter(tabAdapter);
        //se clicchi su un tab, passa a quel tab (MADDAI??)
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        //se fai swipe a destra o sinistra passa a quel tab (MADDAI?? O sce ij nun o sapev)
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        //il pulsante in basso a destra ti porta a creare un nuovo gruppo
        options = findViewById(R.id.floatingActionButton);
        exit = findViewById(R.id.floatingActionButton3);
        createGroup = findViewById(R.id.floatingActionButton2);

        options.setOnClickListener(view -> {
            if(count == 0) {
                count++;
                exit.setVisibility(View.VISIBLE);
                createGroup.setVisibility(View.VISIBLE);
                exit.setOnClickListener(view2 -> {
                    File path = getFilesDir();
                    File resources = new File(path, "resources");
                    count--;

                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new FileReader(resources));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        if(br.readLine().equals("1")) {
                            //bisogna sovrascrivere quell'1 con 0
                            Intent exitIntent = new Intent(this,LoginActivity.class);
                            startActivity(exitIntent);
                            finish();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                createGroup.setOnClickListener(view1 -> {
                    count--;
                    createGroup.setVisibility(View.INVISIBLE);
                    exit.setVisibility(View.INVISIBLE);
                    Intent groupIntent = new Intent(this,CreateGroupActivity.class);
                    startActivity(groupIntent); });
            }
            else {
                count--;
                createGroup.setVisibility(View.INVISIBLE);
                exit.setVisibility(View.GONE);
            }
        });
    }
}

