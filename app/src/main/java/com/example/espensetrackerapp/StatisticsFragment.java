package com.example.espensetrackerapp;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Random;

public class StatisticsFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private HorizontalBarChart categoryChart;
    private MaterialButtonToggleGroup timePeriodGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_fragment, container, false);

        pieChart = view.findViewById(R.id.pie_chart);
        barChart = view.findViewById(R.id.bar_chart);
        categoryChart = view.findViewById(R.id.category_chart);
        timePeriodGroup = view.findViewById(R.id.time_period_group);

        setupCharts();
        loadSampleData();

        return view;
    }

    private void setupCharts() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.getLegend().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setPinchZoom(false);
        barChart.setDrawValueAboveBar(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            @Override
            public String getFormattedValue(float value) {
                return (value >= 0 && value < months.length) ? months[(int) value] : "";
            }
        });

        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(1000);

        categoryChart.getDescription().setEnabled(false);
        categoryChart.setDrawValueAboveBar(true);
        categoryChart.setPinchZoom(false);
        categoryChart.setDrawBarShadow(false);
        categoryChart.setDrawGridBackground(false);

        XAxis catXAxis = categoryChart.getXAxis();
        catXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        catXAxis.setDrawGridLines(false);
        catXAxis.setGranularity(1f);
        catXAxis.setLabelCount(5);

        categoryChart.getAxisLeft().setDrawGridLines(true);
        categoryChart.getAxisRight().setEnabled(false);
        categoryChart.getLegend().setEnabled(false);
        categoryChart.animateY(1000);
    }

    private void loadSampleData() {
        setupPieChart(4500f, 3200f);
        setupBarChart();
        setupCategoryChart();
    }

    private void setupPieChart(float income, float expense) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(income, "Income"));
        entries.add(new PieEntry(expense, "Expense"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(getResources().getColor(R.color.green_500), getResources().getColor(R.color.red_500));
        dataSet.setValueTextSize(14f);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void setupBarChart() {
        int months = 12;
        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < months; i++) {
            incomeEntries.add(new BarEntry(i, random.nextFloat() * 5000 + 2000));
            expenseEntries.add(new BarEntry(i, random.nextFloat() * 4000 + 1000));
        }

        BarDataSet incomeSet = new BarDataSet(incomeEntries, "Income");
        incomeSet.setColor(getResources().getColor(R.color.green_500));
        incomeSet.setValueTextSize(10f);
        incomeSet.setValueFormatter(new CurrencyFormatter());

        BarDataSet expenseSet = new BarDataSet(expenseEntries, "Expense");
        expenseSet.setColor(getResources().getColor(R.color.red_500));
        expenseSet.setValueTextSize(10f);
        expenseSet.setValueFormatter(new CurrencyFormatter());

        BarData data = new BarData(incomeSet, expenseSet);
        data.setBarWidth(0.3f);
        barChart.setData(data);
        barChart.groupBars(0f, 0.1f, 0.05f);
        barChart.invalidate();
    }

    private void setupCategoryChart() {
        String[] categories = {"Food", "Transport", "Shopping", "Bills", "Entertainment"};
        ArrayList<BarEntry> entries = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < categories.length; i++) {
            entries.add(new BarEntry(i, random.nextFloat() * 2000 + 500));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Categories");
        dataSet.setColors(
                Color.parseColor("#FF5722"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#9C27B0"),
                Color.parseColor("#607D8B"),
                Color.parseColor("#FFC107")
        );
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new CurrencyFormatter());

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);
        categoryChart.setData(data);

        XAxis xAxis = categoryChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(categories));

        categoryChart.invalidate();
    }

    public class CurrencyFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return "$" + (int) value;
        }
    }

    public class PercentFormatter extends ValueFormatter {
        private final PieChart chart;

        public PercentFormatter(PieChart chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value) {
            return (int) value + "%";
        }
    }
}
