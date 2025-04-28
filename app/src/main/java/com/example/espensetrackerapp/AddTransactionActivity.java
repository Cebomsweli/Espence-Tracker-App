package com.example.espensetrackerapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTransactionActivity extends AppCompatActivity {

    private MaterialButtonToggleGroup radioGroupType;
    private TextInputEditText editTextAmount, editTextNotes, textViewDate;
    private AutoCompleteTextView spinnerCategory;
    private MaterialButton buttonSubmit;

    private TextInputLayout amountContainer, categoryContainer, dateContainer;
    private FirebaseFirestore db;
    private String selectedType = "Income";

    private final String[] categories = { "Gifts", "Pets", "Home Maintenance"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_transaction);

        db = FirebaseFirestore.getInstance();

        // Initialize Views
        radioGroupType = findViewById(R.id.radioGroup_type);
        editTextAmount = findViewById(R.id.editText_amount);
        spinnerCategory = findViewById(R.id.spinner_category);
        textViewDate = findViewById(R.id.textView_date);
        editTextNotes = findViewById(R.id.editText_notes);
        buttonSubmit = findViewById(R.id.button_submit);

        amountContainer = findViewById(R.id.amount_container);
        categoryContainer = findViewById(R.id.category_container);
        dateContainer = findViewById(R.id.date_container);

        // Toggle Button Listener
        radioGroupType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                selectedType = (checkedId == R.id.radio_income) ? "Income" : "Expense";
            }
        });
        // Load Categories from strings.xml

        // Setup Category Dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        spinnerCategory.setAdapter(adapter);

        // Date picker
        textViewDate.setOnClickListener(v -> showDatePicker());

        // Submit
        buttonSubmit.setOnClickListener(v -> saveTransaction());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    textViewDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveTransaction() {
        String amountStr = editTextAmount.getText().toString().trim();
        String category = spinnerCategory.getText().toString().trim();
        String date = textViewDate.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        boolean valid = true;

        // Amount validation
        if (amountStr.isEmpty()) {
            amountContainer.setError("Amount is required");
            valid = false;
        } else {
            try {
                Double.parseDouble(amountStr);
                amountContainer.setError(null);
            } catch (NumberFormatException e) {
                amountContainer.setError("Invalid amount format");
                valid = false;
            }
        }

        // Category validation
        boolean categoryValid = false;
        for (String cat : categories) {
            if (cat.equalsIgnoreCase(category)) {
                categoryValid = true;
                break;
            }
        }
        if (!categoryValid) {
            categoryContainer.setError("Please select a valid category");
            valid = false;
        } else {
            categoryContainer.setError(null);
        }

        // Date validation
        if (date.isEmpty()) {
            dateContainer.setError("Date is required");
            valid = false;
        } else {
            dateContainer.setError(null);
        }

        if (!valid) return;

        // Save to Firestore
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("type", selectedType);
        transaction.put("amount", Double.parseDouble(amountStr));
        transaction.put("category", category);
        transaction.put("date", date);
        transaction.put("notes", notes);

        db.collection("transactions")
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show());
    }

    private void clearForm() {
        editTextAmount.setText("");
        spinnerCategory.setText("");
        textViewDate.setText("");
        editTextNotes.setText("");
        radioGroupType.check(R.id.radio_income);
    }
}
