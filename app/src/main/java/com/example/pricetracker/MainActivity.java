package com.example.pricetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<SiteToggler> siteTogglers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_search:
                        selectedFragment = new SearchFragment();
                        setTitle("Price Tracker");
                        break;
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        setTitle("Home");
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
        siteTogglers = getSiteTogglers();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        /*OneTimeWorkRequest scrapNUpdate = new OneTimeWorkRequest.Builder(ScraperWorker.class)
                .setConstraints(constraints)
                //.setInitialDelay(1, TimeUnit.MINUTES)
                .addTag("scraper")
                .build();
        WorkManager.getInstance(this).enqueue(scrapNUpdate);
        */

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(ScraperWorker.class, 6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag("Update")
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("Update", ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
        //WorkManager.getInstance(this).cancelUniqueWork("Update");

    }

    private ArrayList<SiteToggler> getSiteTogglers() {
        ArrayList<SiteToggler> temp = new ArrayList<>();
        temp.add(new SiteToggler("Amazon", "https://www.amazon.in/", "", true, R.drawable.ic_amazon));
        temp.add(new SiteToggler("Flipkart", "https://www.flipkart.com/", "", true, R.drawable.ic_flipkart));
        temp.add(new SiteToggler("Bigbasket", "https://www.bigbasket.com/", "", false, R.drawable.ic_bigbasket));
        temp.add(new SiteToggler("JioMart", "https://www.jiomart.com/", "", false, R.drawable.ic_jiomart));
        //temp.add(new SiteToggler("Myntra", "https://www.myntra.com/", "", false, R.drawable.ic_myntra)); // TODO myntra scraping issue
        temp.add(new SiteToggler("Paytm Mall", "https://paytmmall.com/", "", false, R.drawable.ic_paytmmall));
        temp.add(new SiteToggler("Snapdeal", "https://www.snapdeal.com/", "", false, R.drawable.ic_snapdeal));
        return temp;
    }
}