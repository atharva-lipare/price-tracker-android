package com.example.pricetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;

public class SearchTabsActivity extends AppCompatActivity {
    private static Context context;
    private ArrayList<SiteToggler> siteTogglers;
    private String query;
    private String currentSiteOpen;
    private String currentUrlOpen;
    WebViewFragment webViewFragment;
    FloatingActionButton floatingActionButtonTrack;
    FloatingActionButton floatingActionButtonCompare;
    TabLayout tabLayout;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tabs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Quick Search");
        getIntentMethod();
        initViewPager();
        context = this;
        floatingActionButtonTrack = findViewById(R.id.floatingActionButton_track);
        floatingActionButtonTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewFragment fragment = viewPagerAdapter.getCurrentFragment();
                currentUrlOpen = fragment.webView.getUrl();
                Log.e("url_testing", currentUrlOpen);
                Log.e("url_testing", String.valueOf(isValidProductPage()));
                if (isValidProductPage()) {
                    new doItTracking(currentUrlOpen, currentSiteOpen).execute();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please click on a product page to track", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        floatingActionButtonCompare = findViewById(R.id.floatingActionButtonCompare);
        floatingActionButtonCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewFragment fragment = viewPagerAdapter.getCurrentFragment();
                currentUrlOpen = fragment.webView.getUrl();
                Log.e("url_testing", currentUrlOpen);
                Log.e("url_testing", String.valueOf(isValidProductPage()));
                if (isValidProductPage()) {
                    doItCompare doItCompare = new doItCompare();
                    doItCompare.execute();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please click on a product page to compare", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    private void getIntentMethod() {
        this.siteTogglers = (ArrayList<SiteToggler>) getIntent().getSerializableExtra("site_togglers_array");
        this.query = getIntent().getStringExtra("query");
    }

    private void initViewPager() {
        CustomViewPager viewPager = findViewById(R.id.search_view_pager);
        viewPager.setPagingEnabled(false);
        tabLayout = findViewById(R.id.search_tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentSiteOpen = tab.getText().toString();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (SiteToggler siteToggler : siteTogglers) {
            Bundle bundle = new Bundle();
            bundle.putString("marketPlace", siteToggler.getSiteName());
            bundle.putString("query", query);
            bundle.putBoolean("isTrackButton", false);
            bundle.putBoolean("isCompareButton", false);
            webViewFragment = new WebViewFragment();
            webViewFragment.setArguments(bundle);
            viewPagerAdapter.addFragment(webViewFragment, siteToggler.getSiteName());
        }
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private WebViewFragment currentFragment;
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (getCurrentFragment() != object) {
                currentFragment = (WebViewFragment) object;
            }
            super.setPrimaryItem(container, position, object);
        }

        public WebViewFragment getCurrentFragment() {
            return currentFragment;
        }
    }

    public boolean isValidProductPage() {
        switch (currentSiteOpen) {
            case "Amazon":
                return currentUrlOpen.contains("/dp/") || currentUrlOpen.contains("/gp/");
            case "Flipkart":
                return currentUrlOpen.contains("/p/") && currentUrlOpen.contains("pid=");
            case "Bigbasket":
                return currentUrlOpen.contains("/pd/");
            case "JioMart":
                return currentUrlOpen.contains("/p/");
            case "Myntra":
                return currentUrlOpen.contains("/buy");
            case "Paytm Mall":
                return currentUrlOpen.contains("?product_id=") || currentUrlOpen.contains("?sid=") || currentUrlOpen.contains("pdp") ;
            case "Snapdeal":
                return currentUrlOpen.contains("/product/");
            default:
                return false;
        }
    }

    private static class doItTracking extends AsyncTask<Void, Void, Void> {
        String currentUrlOpen;
        String currentSiteOpen;
        MyScraper myScraper;
        Product product;

        public doItTracking(String currentUrlOpen, String currentSiteOpen) {
            this.currentUrlOpen = currentUrlOpen;
            this.currentSiteOpen = currentSiteOpen;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                myScraper = new MyScraper(this.currentUrlOpen, this.currentSiteOpen);
                myScraper.scrapeProductInfo();
                product = myScraper.getProduct();
                MyDBHandler myDBHandler = new MyDBHandler(context);
                boolean b = myDBHandler.insertIntoTableB(product);
                Log.e("db_testing", String.valueOf(b));
                boolean b1 = myDBHandler.insertIntoTableA(product);
                Log.e("db_testing", String.valueOf(b1));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    private class doItCompare extends AsyncTask<Void, Void, Void> {

        public MyScraper myScraper;
        public Product product;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                myScraper = new MyScraper(currentUrlOpen, currentSiteOpen);
                myScraper.scrapeProductInfo();
                product = myScraper.getProduct();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("jsoup_testing", product.getName());
            Intent intent = new Intent(SearchTabsActivity.this, QuickComparisonActivity.class);
            intent.putExtra("site_togglers_array", siteTogglers);
            intent.putExtra("query", product.getName());
            startActivity(intent);
        }
    }
}