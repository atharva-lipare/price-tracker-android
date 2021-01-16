package com.example.pricetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class QuickComparisonActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ItemViewAdapterForComparison itemViewAdapter;
    private ArrayList<SiteToggler> siteTogglers;
    private ArrayList<Product> products;
    private String query;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_comparison);
        context = this;
        setTitle("Quick Compare");
        getIntentMethod();
        doItCompare doItCompare = new doItCompare();
        doItCompare.execute();
    }

    private void getIntentMethod() {
        this.siteTogglers = (ArrayList<SiteToggler>) getIntent().getSerializableExtra("site_togglers_array");
        this.query = getIntent().getStringExtra("query");
        Log.e("compare_testing", query);
        products = new ArrayList<>();
    }

    public class doItCompare extends AsyncTask<Void, Void, Void> {

        public MyScraper myScraper;
        ArrayList<Product> productsTemp;

        @Override
        protected Void doInBackground(Void... voids) {
            products = new ArrayList<>();
            Log.e("compare_testing", query);
            for (int i = 0; i < siteTogglers.size(); i++) {
                myScraper = new MyScraper(query, siteTogglers.get(i).getSiteName());
                try {
                    productsTemp = myScraper.scrapeProductsCompare();
                    if (productsTemp != null) {
                        products.addAll(productsTemp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (products == null) return;
            for (Product product : products) {
                Log.e("compare_testing", product.getName());
                Log.e("compare_testing", String.valueOf(product.getPrice()));
                Log.e("compare_testing", product.getUrl());
                Log.e("compare_testing", product.getImageUrl());
                Log.e("compare_testing", product.getRating());
            }
            recyclerView = findViewById(R.id.recyclerViewQuickComparison);
            layoutManager = new GridLayoutManager(context, 2);
            recyclerView.setLayoutManager(layoutManager);
            Collections.sort(products, new Comparator<Product>() {
                public int compare(Product p1, Product p2) {
                    return p1.getPrice().compareTo(p2.getPrice());
                }
            });
            itemViewAdapter = new ItemViewAdapterForComparison(context, products);
            recyclerView.setAdapter(itemViewAdapter);
            recyclerView.setHasFixedSize(true);
        }
    }
}