package com.example.pda.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pda.R;
import com.example.pda.cart.InvoiceLine;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<InvoiceLine> invoiceLines;

    public CartAdapter(List<InvoiceLine> invoiceLines) {
        this.invoiceLines = invoiceLines;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        InvoiceLine line = invoiceLines.get(position);
        holder.name.setText(line.product.getName());
        holder.quantity.setText("Quantité : " + line.quantity);
        holder.price.setText(String.format("%.2f TND", line.getLineTotalTTC()));
    }

    @Override
    public int getItemCount() {
        return invoiceLines.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, price;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_title);
            quantity = itemView.findViewById(R.id.item_quantity);
            price = itemView.findViewById(R.id.item_price);
        }
    }
}
