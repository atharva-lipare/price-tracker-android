package com.example.pricetracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebViewHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebViewHomeFragment extends Fragment {
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton remFromFav;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "url";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public WebViewHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebViewHomeFragment.
     */

    public static WebViewHomeFragment newInstance(String param1, String param2) {
        WebViewHomeFragment fragment = new WebViewHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        mParam1 = bundle.getString(ARG_PARAM1);
        View view = inflater.inflate(R.layout.fragment_web_view_home, container, false);
        remFromFav = view.findViewById(R.id.floatingActionButtonDeleteFav);
        remFromFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MyDBHandler myDBHandler = new MyDBHandler(getContext());
                    String url = webView.getOriginalUrl();
                    boolean b1 = myDBHandler.deleteFromTableA(url);
                    Log.e("db_testing_remove", String.valueOf(b1));
                    boolean b2 = myDBHandler.deleteFromTableB(url);
                    Log.e("db_testing_remove", String.valueOf(b2));
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        webView = view.findViewById(R.id.webViewHome);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_home);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
        webView.setOnKeyListener(new View.OnKeyListener(){
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
        webView.loadUrl(mParam1);
        return view;
    }
}