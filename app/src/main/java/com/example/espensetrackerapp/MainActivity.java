package com.example.espensetrackerapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this); // ✅ Important for Firebase to initialize

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