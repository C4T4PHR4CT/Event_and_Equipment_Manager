package com.SovietHouseholdAppliances.EventManager.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.SovietHouseholdAppliances.EventManager.R;
import com.SovietHouseholdAppliances.EventManager.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    public static Context mainActivity;

    MainViewModel viewModel;

    ImageButton menu;
    DrawerLayout drawer;
    Button profile;
    Button events;
    Button equipments;
    Button logout;
    FragmentContainerView fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = getApplicationContext();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getActiveFragment().observe(this, newFragment -> {
            try {
                Fragment temp = newFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(fragment.getId(), temp);
                transaction.commit();
            } catch (IllegalAccessException|InstantiationException ignored) {}
        });

        viewModel.getAlert().observe(this, alert -> {
            if (alert.equals("logout"))
                logout();
        });

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
            viewModel.setActiveFragment(ProfileFragment.class);
        });

        events.setOnClickListener(e -> {
            closeDrawer();
            viewModel.setActiveFragment(EventFragment.class);
        });

        equipments.setOnClickListener(e -> {
            closeDrawer();
            viewModel.setActiveFragment(EquipmentFragment.class);
        });

        logout.setOnClickListener(e -> {
            logout();
        });

        if (savedInstanceState == null)
            viewModel.setActiveFragment(MainFragment.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        closeDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //returning from auth
        if (requestCode == 1) {
            viewModel.setActiveFragment(MainFragment.class);
        }
    }

    @Override
    public void onBackPressed () {
        if (!drawer.isDrawerOpen(GravityCompat.START))
            if (viewModel.getActiveFragment().getValue() == MainFragment.class) {
                if (!viewModel.getKeepLoggedIn())
                    viewModel.clearToken();
                finish();
            } else
                viewModel.setActiveFragment(MainFragment.class);
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

    private void logout()
    {
        closeDrawer();
        viewModel.clearToken();
        Intent temp = new Intent(this, LoginActivity.class);
        startActivityForResult(temp, 1);
    }

    public static void print(String content)
    {
        Toast.makeText(mainActivity, content, Toast.LENGTH_SHORT).show();
    }
}