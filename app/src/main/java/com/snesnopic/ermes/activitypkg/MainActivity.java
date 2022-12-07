package com.snesnopic.ermes.activitypkg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.snesnopic.ermes.R;
import com.snesnopic.ermes.ctrlpkg.TabAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    TabAdapter tabAdapter;

    Animation rotateOpen;
    Animation rotateClose;
    Animation fromBottom;
    Animation toBottom;

    FloatingActionButton expandButton;
    FloatingActionButton newGroupButton;
    FloatingActionButton logoutButton;

    boolean actionButtonClicked = false;

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
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        //animazioni dei floating action button
        rotateOpen = AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim);
        //trovo i floating action button
        newGroupButton = findViewById(R.id.newGroupActionButton);
        logoutButton = findViewById(R.id.logoutActionButton);
        expandButton = findViewById(R.id.expandableActionButton);
        //pulsante col +, premendolo si espande o ritrae
        expandButton.setOnClickListener(view -> {
            if(!actionButtonClicked)
            {
                newGroupButton.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.VISIBLE);

                newGroupButton.startAnimation(fromBottom);
                logoutButton.startAnimation(fromBottom);
                expandButton.startAnimation(rotateOpen);
            }
            else
            {
                newGroupButton.startAnimation(toBottom);
                logoutButton.startAnimation(toBottom);
                expandButton.startAnimation(rotateClose);

                newGroupButton.setVisibility(View.GONE);
                logoutButton.setVisibility(View.GONE);
            }
            actionButtonClicked = !actionButtonClicked;
        });
        //pulsante di nuovo gruppo
        newGroupButton.setOnClickListener(view -> {
            Intent createGroupIntent = new Intent(this,CreateGroupActivity.class);
            startActivity(createGroupIntent);
        });
        //pulsante di logout
        logoutButton.setOnClickListener(view -> {
            File resources = new File(getFilesDir(), "resources");
            try {
                BufferedReader br = new BufferedReader(new FileReader(resources));
                if(br.readLine().equals("1")) {
                    //bisogna sovrascrivere quell'1 con 0
                    //o magari eliminiamo il file direttamente || bravo mi piace come scelta, ora lo implemento
                    resources.delete();
                    Intent exitIntent = new Intent(this,LoginActivity.class);
                    startActivity(exitIntent);
                    finish();
                }
            } catch (Exception e) {
                Intent exitIntent = new Intent(this,LoginActivity.class);
                startActivity(exitIntent);
                finish();
            }
        });

    }
}

