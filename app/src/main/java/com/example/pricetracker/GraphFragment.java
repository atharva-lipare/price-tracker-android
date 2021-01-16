package com.example.pricetracker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphFragment extends Fragment {
    private LineChart lineChart;
    ArrayList<Entry> values;
    ArrayList<String> xAxisValues;
    Context context;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "url";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public GraphFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GraphFragment.
     */

    public static GraphFragment newInstance(String param1, String param2) {
        GraphFragment fragment = new GraphFragment();
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
        Bundle bundle = this.getArguments();
        mParam1 = bundle.getString(ARG_PARAM1);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        context = getContext();
        lineChart = view.findViewById(R.id.line_chart);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        try {
            setData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (values.size() == 0) return view;

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-90);
        xAxis.setLabelCount(xAxisValues.size(), true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setSpaceTop(70f);
        yAxis.setSpaceBottom(30f);
        lineChart.getAxisRight().setEnabled(false);

        LineDataSet lineDataSet = new LineDataSet(values, "DS1");
        lineDataSet.setDrawIcons(false);
        lineDataSet.setColor(Color.BLACK);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10);
        lineDataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return lineChart.getAxisLeft().getAxisMinimum();
            }
        });

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.fade_blue);
            lineDataSet.setFillDrawable(drawable);
        }
        else {
            lineDataSet.setFillColor(Color.BLACK);
        }
        lineDataSet.setDrawFilled(true);

        // create a data object with the data sets
        LineData data = new LineData(lineDataSet);
        // set data
        lineChart.setData(data);
        //lineChart.animateX(1500);
        return view;
    }

    private void setData() throws ParseException {
        MyDBHandler myDBHandler = new MyDBHandler(context);
        ArrayList<Product> products = myDBHandler.getAllProductsFromTableBForGraph(mParam1);
        xAxisValues = new ArrayList<>();
        values = new ArrayList<>();
        if (products == null) return;

        Collections.sort(products, new Comparator<Product>() {
            DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return f.parse(p1.getName()).compareTo(f.parse(p2.getName()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        for (int i = 0; i < products.size(); i++) {
            SimpleDateFormat pre_format = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat new_format = new SimpleDateFormat("dd-MMM");
            Date date = pre_format.parse(products.get(i).getName());
            xAxisValues.add(new_format.format(date));
            double temp = products.get(i).getPrice();
            float temp1 = (float) temp;
            values.add(new Entry(i, temp1));
        }
    }
}