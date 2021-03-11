package com.SovietHouseholdAppliances.EventManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageButton menu;
    DrawerLayout drawer;
    Button profile;
    Button events;
    Button equipments;
    Button logout;
    FragmentContainerView fragment;

    Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = findViewById(R.id.menu_button);
        drawer = findViewById(R.id.menu_drawer);
        profile = findViewById(R.id.menu_profile);
        events = findViewById(R.id.menu_events);
        equipments = findViewById(R.id.menu_equipments);
        logout = findViewById(R.id.menu_logout);
        fragment = findViewById(R.id.fragment);

        menu.setOnClickListener(e -> {
            openDrawer();
        });

        profile.setOnClickListener(e -> {
            closeDrawer();
            navigate(new ProfileFragment());
        });

        events.setOnClickListener(e -> {
            closeDrawer();
            navigate(new EventFragment());
        });

        equipments.setOnClickListener(e -> {
            closeDrawer();
            navigate(new EquipmentFragment());
        });

        logout.setOnClickListener(e -> {
            closeDrawer();
            startActivityForResult(new Intent(this, LoginActivity.class), 1);
        });

        if (savedInstanceState == null)
            navigate(new MainFragment());

        maininstance = getApplicationContext();
    }

    @Override
    protected void onStart() {
        super.onStart();
        closeDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //returning from login
        if (requestCode == 1) {
            navigate(new MainFragment());
        }
    }

    @Override
    public void onBackPressed () {
        if (!drawer.isDrawerOpen(GravityCompat.START))
            if (activeFragment instanceof MainFragment)
                finish();
            else
                navigate(new MainFragment());
        else
            closeDrawer();
    }

    private void closeDrawer()
    {
        drawer.closeDrawer(GravityCompat.START);
    }

    private void openDrawer()
    {
        drawer.openDrawer(GravityCompat.START);
    }

    private void navigate(Fragment newFragment)
    {
        if (activeFragment == null || newFragment.getClass() != activeFragment.getClass()) {
            activeFragment = newFragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(fragment.getId(), newFragment);
            transaction.commit();
        }
    }

    private static Context maininstance;
    public static void print(String content)
    {
        Toast.makeText(maininstance, content, Toast.LENGTH_SHORT).show();
    }
}