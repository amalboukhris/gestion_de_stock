package com.example.pda;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.pda.inventory.MainActivity;

public class SaidBar extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected LinearLayout menuTags, menuSearch, menuProfil;
    protected ImageView menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ne pas setContentView ici ! Ce sera fait dans setContentView override.
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // Charge la structure globale drawer avec content_frame
        super.setContentView(R.layout.activity_said_bar);

        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.iv_menu);

        // Injecte le layout spécifique de l'activité dans content_frame
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, contentFrame, true);

        // Initialisation menu drawer
        menuTags = findViewById(R.id.menu_tags);
        menuSearch = findViewById(R.id.menu_search);
        menuProfil = findViewById(R.id.menu_profil);

        // Menu bouton ouvre/ferme le drawer
        if (menuButton != null && drawerLayout != null) {
            menuButton.setOnClickListener(v -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        // Clic sur items menu
        if (menuTags != null) {
            menuTags.setOnClickListener(v -> {
                if (!(this instanceof MainActivity)) { // éviter de recharger la même activité
                    startActivity(new Intent(this, MainActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    finish();
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            });
        }

        if (menuSearch != null) {
            menuSearch.setOnClickListener(v -> {
                // TODO: Démarrer SearchActivity
                drawerLayout.closeDrawer(GravityCompat.START);
            });
        }

        if (menuProfil != null) {
            menuProfil.setOnClickListener(v -> {
                // TODO: Démarrer ProfileActivity
                drawerLayout.closeDrawer(GravityCompat.START);
            });
        }
    }
}
