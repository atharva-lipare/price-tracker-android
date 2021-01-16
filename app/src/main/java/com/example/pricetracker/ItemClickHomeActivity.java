package com.example.pricetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ItemClickHomeActivity extends AppCompatActivity {
    String url;
    String marketPlace;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_item_click_home);
        getIntentMethod();
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar_item_click_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                Bundle bundle;
                switch (item.getItemId()) {
                    case R.id.nav_web_view:
                        bundle = new Bundle();
                        bundle.putString("url", url);
                        selectedFragment = new WebViewHomeFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.nav_graph:
                        bundle = new Bundle();
                        bundle.putString("url", url);
                        selectedFragment = new GraphFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.item_click_container, selectedFragment).commit();
                return true;
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        Fragment selectedFragment1 = new WebViewHomeFragment();
        selectedFragment1.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.item_click_container, selectedFragment1).commit();
    }

    private void getIntentMethod() {
        this.url = getIntent().getStringExtra("url");
        this.marketPlace = getIntent().getStringExtra("marketplace");
    }
}