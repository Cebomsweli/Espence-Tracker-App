package com.example.espensetrackerapp;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    // UI Components
    private RecyclerView recyclerView;
    private ProgressBar loadingProgress;
    private TextView emptyStateText;
    private Spinner filterCategorySpinner, filterTypeSpinner;
    private EditText filterDateEditText;
    private Button applyFilterButton;

    // Data
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions = new ArrayList<>();

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction_history_fragment, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize Views
        recyclerView = view.findViewById(R.id.recyclerView);
        loadingProgress = view.findViewById(R.id.loading_progress);
        emptyStateText = view.findViewById(R.id.empty_view);
        filterCategorySpinner = view.findViewById(R.id.filterCategorySpinner);
        filterTypeSpinner = view.findViewById(R.id.filterTypeSpinner);
        filterDateEditText = view.findViewById(R.id.filterDateEditText);
        applyFilterButton = view.findViewById(R.id.applyFilterButton);




        return view;
    }

}