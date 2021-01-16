package com.example.pricetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.pricetracker.MainActivity.siteTogglers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    RecyclerView recyclerView;
    WebSiteToggleAdapter webSiteToggleAdapter;
    Button searchButton;
    Button comparisonButton;
    SearchView searchView;
    ArrayList<SiteToggler> checkedSiteTogglers;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_site_selector);
        recyclerView.setAdapter(webSiteToggleAdapter);
        searchView = view.findViewById(R.id.searchView);
        searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateBeforeClick()) {
                    Intent intent = new Intent(getContext(), SearchTabsActivity.class);
                    intent.putExtra("site_togglers_array", checkedSiteTogglers);
                    intent.putExtra("query", searchView.getQuery().toString());
                    startActivity(intent);
                }
            }
        });
        comparisonButton = view.findViewById(R.id.comparison_button);
        comparisonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateBeforeClick()) {
                    Intent intent = new Intent(getContext(), QuickComparisonActivity.class);
                    intent.putExtra("site_togglers_array", checkedSiteTogglers);
                    intent.putExtra("query", searchView.getQuery().toString());
                    startActivity(intent);
                }
            }
        });
        if (siteTogglers.size() >=1) {
            webSiteToggleAdapter = new WebSiteToggleAdapter(getContext(), siteTogglers);
            recyclerView.setAdapter(webSiteToggleAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }

    private boolean validateBeforeClick() {
        if (searchView.getQuery().toString().isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "Please enter a search query", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        checkedSiteTogglers = new ArrayList<>();
        for (SiteToggler siteToggler : siteTogglers) {
            if (siteToggler.isChecked()) {
                checkedSiteTogglers.add(siteToggler);
            }
        }
        if (checkedSiteTogglers.isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "Please select a site", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        return true;
    }
}