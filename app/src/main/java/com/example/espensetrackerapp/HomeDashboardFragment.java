package com.example.espensetrackerapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeDashboardFragment extends Fragment {
    private TextView tvBalanceTotal, tvIncomeTotal, tvExpenseTotal;
    private RecyclerView recyclerView;
    private RecentTransactionsAdapter adapter;
    private List<Transaction> recentTransactions = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home_dashboard, container, false);

        // Initialize TextViews and RecyclerView
        tvBalanceTotal = rootView.findViewById(R.id.total_balance);
        tvIncomeTotal = rootView.findViewById(R.id.total_income);
        tvExpenseTotal = rootView.findViewById(R.id.total_expense);
        recyclerView = rootView.findViewById(R.id.recent_transactions);

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecentTransactionsAdapter(recentTransactions);
        recyclerView.setAdapter(adapter);

        // Load data from Firebase
        loadAllData();

        return rootView;
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
                    recentTransactions.clear(); // Clear existing data before adding new ones

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String type = doc.getString("type");
                        String amountStr = doc.getString("Amount");
                        String description = doc.getString("description");
                        String date = doc.getString("date");

                        if (type == null || amountStr == null || amountStr.isEmpty()) continue;

                        try {
                            float amount = Float.parseFloat(amountStr);
                            if (type.equalsIgnoreCase("Income")) {
                                income += amount;
                            } else if (type.equalsIgnoreCase("Expense")) {
                                expense += amount;
                            }

                            // Add recent transactions (limit to the last 5 transactions)
                            if (recentTransactions.size() < 5) {
                                recentTransactions.add(new Transaction(type, amount, description, date));
                            }
                        } catch (NumberFormatException e) {
                            Log.e("HomeDashboardFragment", "Invalid amount: " + amountStr);
                        }
                    }

                    // Update the UI
                    tvBalanceTotal.setText("R" + (income - expense)); // Total Balance = Income - Expense
                    tvIncomeTotal.setText("R" + income);  // Income
                    tvExpenseTotal.setText("R" + expense);  // Expense

                    // Notify the adapter to update the RecyclerView
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeDashboardFragment", "Error loading data", e);
                    // Optionally, you can display an error message or handle the failure scenario
                });
    }
}
