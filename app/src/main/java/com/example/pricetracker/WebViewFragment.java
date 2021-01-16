package com.example.pricetracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebViewFragment extends Fragment {
    WebView webView;
    SwipeRefreshLayout swipeRefreshLayout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "marketPlace";
    private static final String ARG_PARAM2 = "query";
    private static final String ARG_PARAM3 = "isTrackButton";
    private static final String ARG_PARAM4 = "isCompareButton";
    private static final String ARG_PARAM5 = "isDirectUrl";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean mParam3;
    private boolean mParam4;
    private boolean mParam5;

    public WebViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WebViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebViewFragment newInstance(String param1, String param2) {
        WebViewFragment fragment = new WebViewFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        mParam1 = bundle.getString(ARG_PARAM1);
        mParam2 = bundle.getString(ARG_PARAM2);
        mParam3 = bundle.getBoolean(ARG_PARAM3);
        mParam4 = bundle.getBoolean(ARG_PARAM4);
        mParam5 = bundle.getBoolean(ARG_PARAM5);
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        webView = view.findViewById(R.id.webView);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
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
        webView.loadUrl(getSearchQuery());
        return view;
    }

    public String getSearchQuery() {
        if (mParam5) return mParam2;
        switch (mParam1) {
            case "Amazon":
                return "https://www.amazon.in/s?k=" + mParam2.replace(' ', '+') + "&ref=nb_sb_noss";
            case "Flipkart":
                return "https://www.flipkart.com/search?q=" + mParam2.replace(' ', '+');
            case "Bigbasket":
                return "https://www.bigbasket.com/ps/?q=" + mParam2.replace(" ", "%20");
            case "JioMart":
                return "https://www.jiomart.com/catalogsearch/result?q=" + mParam2.replace(" ", "%20");
            case "Myntra":
                return "https://www.myntra.com/" + mParam2.replace(' ', '-');
            case "Paytm Mall":
                return "https://paytmmall.com/shop/search?userQuery=" + mParam2.replace(" ", "%20");
            case "Snapdeal":
                return "https://m.snapdeal.com/search?keyword=" + mParam2.replace(" ", "%20") + "&categoryXPath=ALL";
            default:
                return "https://www.amazon.in/s?k=" + mParam2.replace(' ', '+') + "&ref=nb_sb_noss";
        }
    }
}