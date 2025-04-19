package com.example.espensetrackerapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private HorizontalBarChart categoryChart;
    private TextView tvIncomeTotal, tvExpenseTotal, tvMonthSelected;
    private MaterialButtonToggleGroup toggleGroup;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_fragment, container, false);

        pieChart = view.findViewById(R.id.pie_chart);
        barChart = view.findViewById(R.id.bar_chart);
        categoryChart = view.findViewById(R.id.category_chart);
        tvIncomeTotal = view.findViewById(R.id.tv_income_total);
        tvExpenseTotal = view.findViewById(R.id.tv_expense_total);
        tvMonthSelected = view.findViewById(R.id.tv_month_selected);
        toggleGroup = view.findViewById(R.id.time_period_group);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loadAllData();
        }

        return view;
    }

    private void loadAllData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return;

        String uid = user.getUid();

        db.collection("Transactions")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    float income = 0f;
                    float expense = 0f;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String type = doc.getString("type");
                        String amountStr = doc.getString("Amount");

                        if (type == null || amountStr == null || amountStr.isEmpty()) continue;

                        try {
                            float amount = Float.parseFloat(amountStr);
                            if (type.equalsIgnoreCase("Income")) {
                                income += amount;
                            } else if (type.equalsIgnoreCase("Expense")) {
                                expense += amount;
                            }
                        } catch (NumberFormatException e) {
                            Log.e("StatisticsFragment", "Invalid amount: " + amountStr);
                        }
                    }

                    tvIncomeTotal.setText("R" + income);
                    tvExpenseTotal.setText("R" + expense);
                    setupPieChart(income, expense);
                    setupBarChart();
                    setupCategoryChart();
                })
                .addOnFailureListener(e -> pieChart.setNoDataText("Error loading data."));
    }

    private void setupPieChart(float income, float expense) {
        Context context = getContext();
        if (context == null) return;

        if (income == 0 && expense == 0) {
            pieChart.setNoDataText("No income or expense data available.");
            pieChart.invalidate();
            return;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (income > 0) entries.add(new PieEntry(income, "Income"));
        if (expense > 0) entries.add(new PieEntry(expense, "Expenses"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                ContextCompat.getColor(context, R.color.green_500),
                ContextCompat.getColor(context, R.color.red_500)
        );
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart)); // Use built-in formatter

        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setCenterText("Income vs Expenses");
        pieChart.setCenterTextSize(16f);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(true);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void setupBarChart() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.e("BarChart", "User not logged in");
            return;
        }

        String uid = user.getUid();

        // Clear any existing data
        barChart.clear();
        barChart.setNoDataText("Loading expense data...");
        barChart.invalidate();

        // Get data from Firebase Firestore
        db.collection("Transactions")
                .whereEqualTo("userId", uid)
                .whereEqualTo("type", "Expense")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<Integer, Float> monthlyExpenses = new HashMap<>();
                    int totalDocuments = 0;
                    int processedDocuments = 0;

                    Log.d("BarChart", "Found " + queryDocumentSnapshots.size() + " expense transactions");

                    // Initialize all months with 0
                    for (int i = 0; i < 12; i++) {
                        monthlyExpenses.put(i, 0f);
                    }

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        totalDocuments++;
                        try {
                            // Safely parse amount (handle both string and number fields)
                            float amount = 0;
                            if (doc.get("Amount") instanceof Number) {
                                amount = ((Number) doc.get("Amount")).floatValue();
                            } else if (doc.get("Amount") != null) {
                                amount = Float.parseFloat(doc.getString("Amount"));
                            }

                            // Parse date
                            String dateStr = doc.getString("date");
                            if (dateStr == null || dateStr.isEmpty()) continue;

                            String[] dateParts = dateStr.split("-");
                            if (dateParts.length >= 2) {
                                int month = Integer.parseInt(dateParts[1]) - 1; // Convert to 0-11
                                if (month >= 0 && month < 12) {
                                    float current = monthlyExpenses.get(month);
                                    monthlyExpenses.put(month, current + amount);
                                    processedDocuments++;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("BarChart", "Error processing document " + doc.getId(), e);
                        }
                    }

                    Log.d("BarChart", "Processed " + processedDocuments + "/" + totalDocuments + " documents");

                    // Prepare chart data
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    for (int i = 0; i < 12; i++) {
                        entries.add(new BarEntry(i, monthlyExpenses.get(i)));
                    }

                    if (entries.isEmpty()) {
                        barChart.setNoDataText("No expense data available");
                        barChart.invalidate();
                        return;
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "Monthly Expenses");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    dataSet.setValueTextSize(10f);
                    dataSet.setValueTextColor(Color.BLACK);
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.format(Locale.getDefault(), "R%.2f", value);
                        }
                    });

                    BarData data = new BarData(dataSet);
                    data.setBarWidth(0.8f);

                    // Configure X axis
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(
                            new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}));
                    xAxis.setLabelCount(12);
                    xAxis.setDrawGridLines(false);

                    // Configure Y axis
                    YAxis leftAxis = barChart.getAxisLeft();
                    leftAxis.setAxisMinimum(0f);
                    leftAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.format(Locale.getDefault(), "R%.0f", value);
                        }
                    });

                    barChart.getAxisRight().setEnabled(false);
                    barChart.getDescription().setEnabled(false);
                    barChart.getLegend().setEnabled(false);
                    barChart.setFitBars(true);
                    barChart.setData(data);
                    barChart.animateY(1000);
                    barChart.invalidate();

                    Log.d("BarChart", "Chart updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("BarChart", "Error loading data", e);
                    barChart.setNoDataText("Error loading expense data");
                    barChart.invalidate();
                });
    }


    private void setupCategoryChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        Map<String, Float> categoryValues = new HashMap<>();
        categoryValues.put("Food", 500f);
        categoryValues.put("Transport", 300f);
        categoryValues.put("Rent", 1200f);
        categoryValues.put("Entertainment", 450f);

        int i = 0;
        for (Map.Entry<String, Float> entry : categoryValues.entrySet()) {
            entries.add(new BarEntry(i++, entry.getValue()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Categories");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(dataSet);

        categoryChart.setData(data);
        categoryChart.getDescription().setEnabled(false);
        categoryChart.getLegend().setEnabled(false);
        categoryChart.getXAxis().setDrawLabels(false);
        categoryChart.invalidate();
    }
}
