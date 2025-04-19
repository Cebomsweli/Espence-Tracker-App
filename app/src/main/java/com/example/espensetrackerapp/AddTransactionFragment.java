package com.example.espensetrackerapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTransactionFragment extends Fragment {

    private TextInputEditText textViewDate,textNote,txtMoney;
    private MaterialAutoCompleteTextView spinnerCategory;
    private MaterialButtonToggleGroup radioGroupType;
    private String selectedType = "Income";

    private final String[] incomeCategories = {"Salary", "Investments", "Bonus", "Other"};
    private final String[] expenseCategories = {"Food", "Transport", "Bills", "Entertainment", "Other"};

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        // Initialize views
        textViewDate = view.findViewById(R.id.textView_date);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        radioGroupType = view.findViewById(R.id.radioGroup_type);
        textNote=view.findViewById(R.id.editText_notes);
        txtMoney=view.findViewById(R.id.editText_amount);
        Button btnSave = view.findViewById(R.id.button_submit);

        // Show Date Picker
        textViewDate.setOnClickListener(v -> showDatePicker());

        // Initialize with Income categories
        setCategoryAdapter(incomeCategories);

        // The Button for choosing whether Income or Expense
        radioGroupType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.radio_income) {
                    selectedType = "Income";
                    setCategoryAdapter(incomeCategories);
                } else if (checkedId == R.id.radio_expense) {
                    selectedType = "Expense";
                    setCategoryAdapter(expenseCategories);
                }
            }
        });

        //Adding firebase


        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        textViewDate.setOnClickListener(v -> showDatePicker());

        setCategoryAdapter(incomeCategories);

        radioGroupType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.radio_income) {
                    selectedType = "Income";
                    setCategoryAdapter(incomeCategories);
                } else if (checkedId == R.id.radio_expense) {
                    selectedType = "Expense";
                    setCategoryAdapter(expenseCategories);
                }
            }
        });

        //The button that saves to the DB
        btnSave.setOnClickListener(v -> {
            String note=textNote.getText().toString();
            String amount = txtMoney.getText().toString();
            String date = textViewDate.getText().toString().trim();
            String category = spinnerCategory.getText().toString().trim();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String userId = currentUser != null ? currentUser.getUid() : null;

            if (date.isEmpty() || category.isEmpty() || userId == null ||note==null) {
                Toast.makeText(requireContext(), "Please complete all fields or login", Toast.LENGTH_SHORT).show();
                return;
            }

            // Data to store
            Map<String, Object> transaction = new HashMap<>();
                transaction.put("date", date);
                transaction.put("Note",note);
                transaction.put("Amount",amount);
                transaction.put("category", category);
                transaction.put("type", selectedType);
                transaction.put("userId", userId); // associate with logged-in user

            db.collection("Transactions")
                    .add(transaction)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(requireContext(), "Transaction saved", Toast.LENGTH_SHORT).show();
                        textViewDate.setText("");
                        spinnerCategory.setText("");
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });


        return view;
    }

    //Set Category options
    private void setCategoryAdapter(String[] categories) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categories);
        spinnerCategory.setAdapter(adapter);
    }

    //Prepares calender
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    textViewDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }
}
