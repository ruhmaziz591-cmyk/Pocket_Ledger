// C:/Users/sorcim computers/AndroidStudioProjects/MyApplication3/app/src/main/java/com/example/myapplication/TransactionAdapter.java
package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<TransactionModel> list;
    private OnDeleteClickListener deleteClickListener;

    // Listener for the delete button click
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public TransactionAdapter(Context context, ArrayList<TransactionModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the new transaction item layout
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionModel model = list.get(position);

        holder.tvCategory.setText(model.getCategory());
        holder.tvNote.setText(model.getNote());
        holder.tvDate.setText(model.getDate());

        // Set amount and color based on transaction type
        if (model.getType().equalsIgnoreCase("Income")) {
            String incomeText = String.format(Locale.getDefault(), "+ Rs %.2f", model.getAmount());
            holder.tvAmount.setText(incomeText);
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green)); // You'll need to define this color
            holder.ivTransactionIcon.setImageResource(R.drawable.ic_income);
            holder.ivTransactionIcon.setBackgroundResource(R.drawable.icon_background_green);
        } else {
            String expenseText = String.format(Locale.getDefault(), "- Rs %.2f", model.getAmount());
            holder.tvAmount.setText(expenseText);
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red)); // You'll need to define this color
            holder.ivTransactionIcon.setImageResource(R.drawable.ic_expense);
            holder.ivTransactionIcon.setBackgroundResource(R.drawable.icon_background_red);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategory, tvNote, tvAmount, tvDate;
        ImageView ivTransactionIcon;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views from transaction_item_layout.xml
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivTransactionIcon = itemView.findViewById(R.id.ivTransactionIcon);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            // Set click listener for the delete button
            btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deleteClickListener.onDeleteClick(position);
                    }
                }
            });
        }
    }
}
