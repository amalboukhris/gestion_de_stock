//package com.example.pda.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//import com.example.pda.Models.Invoice;
//import com.example.pda.R;
//
//import java.util.List;
//
//public class InvoiceAdapter extends ArrayAdapter<Invoice> {
//
//    public InvoiceAdapter(Context context, List<Invoice> invoices) {
//        super(context, 0, invoices);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        Invoice invoice = getItem(position);
//
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_invoice, parent, false);
//        }
//
//        TextView invoiceIdText = convertView.findViewById(R.id.invoiceIdText);
//        TextView invoiceTotalText = convertView.findViewById(R.id.invoiceTotalText);
//
//        invoiceIdText.setText("Facture #" + invoice.getId());
//        invoiceTotalText.setText(String.format("%.2f €", invoice.getTotal()));
//
//        return convertView;
//    }
//}
