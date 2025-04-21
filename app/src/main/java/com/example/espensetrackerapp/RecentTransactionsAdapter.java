package com.example.espensetrackerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentTransactionsAdapter extends RecyclerView.Adapter<RecentTransactionsAdapter.TransactionViewHolder> {
    private List<Transaction> transactions;

    public RecentTransactionsAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.typeTextView.setText(transaction.getType());
        holder.amountTextView.setText("R" + transaction.getAmount());
        holder.descriptionTextView.setText(transaction.getDescription());
        holder.dateTextView.setText(transaction.getDate());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView typeTextView, amountTextView, descriptionTextView, dateTextView;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}

