package com.example.pricetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

public class ItemClickWebViewCompareActivity extends AppCompatActivity {
    private static Context context;
    WebView webView;
    FloatingActionButton track;
    String url;
    String marketPlace;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_click_web_view_compare);
        context = this;
        webView = findViewById(R.id.compareItemClickWebView);
        track = findViewById(R.id.compareItemClickActivityTrackBtn);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidProductPage()) {
                    new doItTracking(url, marketPlace).execute();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please click on a product page to track", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        getIntentMethod();
        webView = findViewById(R.id.compareItemClickWebView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutCompare);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webView.canGoBack())
                {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webView.loadUrl(url);
    }

    private void getIntentMethod() {
        this.url = getIntent().getStringExtra("url");
        this.marketPlace = getIntent().getStringExtra("marketplace");
    }

    public boolean isValidProductPage() {
        String currentUrlOpen = webView.getUrl();
        switch (marketPlace) {
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
                if (product.getName().equals("NA")) {
                    return null;
                }
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

}