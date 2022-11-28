package com.snesnopic.ermes;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    TabAdapter tabAdapter;
    FloatingActionButton fab;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();     //nasconde il nome sopra l'app
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

        //se fai swipe a destra o sinistra passa a quel tab (MADDAI??)
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        //il pulsante in basso a destra ti porta a creare un nuovo gruppo
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> {
            Intent groupIntent = new Intent(this,CreateGroupActivity.class);
            startActivity(groupIntent);
        });
    }

}

