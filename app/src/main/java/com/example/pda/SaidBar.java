package com.example.pda;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class SaidBar extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private LinearLayout  menuTags, menuSearch;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);

        menuTags = findViewById(R.id.menu_tags);
        menuSearch = findViewById(R.id.menu_search);



        menuTags.setOnClickListener(v -> {

        });

        menuSearch.setOnClickListener(v -> {

        });
    }
}
