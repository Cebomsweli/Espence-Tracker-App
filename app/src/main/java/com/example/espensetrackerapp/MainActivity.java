package com.example.espensetrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private NavigationView navView;

    private TextView tvUsername;
    FirebaseAuth auth;
    FirebaseUser user;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        // Set up the toolbar
        setSupportActionBar(toolbar);

        // Enable hamburger icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Access the header view to set username
        View headerView = navView.getHeaderView(0);
        tvUsername = headerView.findViewById(R.id.tvUsername);

        // Set username (example: from Firebase)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvUsername.setText(user.getEmail()); // or user.getDisplayName()
        }

        // Handle navigation item clicks
        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                return true;
            }
            return false;
        });

        // Hide default title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

      // Set custom title to user email
        if (user != null) {
            toolbar.setTitle("Espence Tracker");  // This will now be shown on the left
        }
       // Setup the drawer layout and navigation view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

      // Inflate logout icon in the menu
        toolbar.inflateMenu(R.menu.top_app_bar_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });


        FirebaseApp.initializeApp(this);

        //Handling the fragments
        //Loading initial fragment
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeDashboardFragment()).commit();
        }

        //Fr the activity tracking
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // New way (since Material Components 1.5.0+)
        bottomNav.setOnItemSelectedListener(navListener);
    }

    //Method to change fragments
    public void navigateToFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
               .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    //Navigating to fragments
    private final NavigationBarView.OnItemSelectedListener navListener =
            item -> {
                Fragment fragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    fragment = new HomeDashboardFragment();
                } else if (itemId == R.id.nav_history) {
                    fragment = new HistoryFragment();
                } else if (itemId == R.id.nav_stats) {
                    fragment = new StatisticsFragment();
                } else if (itemId == R.id.nav_add) {
                    fragment = new AddTransactionFragment();
                } else {
                    return false;
                }
                navigateToFragment(fragment);
                return true;
            };



}