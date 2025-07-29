package com.example.pda.cart;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pda.Models.Product;
import com.example.pda.R;
import com.example.pda.SaidBar;
import com.example.pda.database.CartAdapter;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends SaidBar implements RFIDHandler.RFIDListener {

    private static final String TAG = "CartActivity";

    private RFIDHandler rfidHandler;
    private List<String> scannedTagsList = new ArrayList<>();

    private Invoice invoice;
    private CartAdapter adapter;
    private TextView totalPrice;
    private Button btnPay;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_cart);

        invoice = new Invoice();

        recyclerView = findViewById(R.id.recyclerViewCart);
        totalPrice = findViewById(R.id.total_price);
        btnPay = findViewById(R.id.btn_pay);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(invoice.getLines());
        recyclerView.setAdapter(adapter);

        updateTotalPrice();

        btnPay.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Facture confirmée, Total TTC : " + formatPrice(invoice.getTotalTTC()),
                    Toast.LENGTH_LONG).show();
            Log.i(TAG, "Facture confirmée:");
            Log.i(TAG, "Total HT: " + invoice.getTotalHT() + " TND");
            Log.i(TAG, "Total TVA: " + invoice.getTotalTax() + " TND");
            Log.i(TAG, "Total TTC: " + invoice.getTotalTTC() + " TND");
        });

        // Initialiser et démarrer le scan RFID
        rfidHandler = new RFIDHandler(this, this);
        rfidHandler.initScanner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rfidHandler != null) {
            rfidHandler.closeScanner();
            rfidHandler = null;
        }
    }

    @Override
    public void onTagRead(String epc) {
        runOnUiThread(() -> {
            if (epc == null || epc.isEmpty()) {
                Toast.makeText(this, "Tag RFID invalide reçu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (scannedTagsList.contains(epc)) {
                Log.d(TAG, "Tag déjà scanné : " + epc);
                return;
            }

            scannedTagsList.add(epc);
            Log.i(TAG, "Tag lu: " + epc);

            Product p = getProductByReference(epc);
            if (p != null) {
                invoice.addProduct(p);
                adapter.notifyDataSetChanged();
                updateTotalPrice();
            } else {
                Toast.makeText(this, "Produit introuvable pour le tag : " + epc, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalPrice() {
        totalPrice.setText("Total : " + formatPrice(invoice.getTotalTTC()));
    }

    private String formatPrice(double price) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        return nf.format(price).replace("€", "TND");
    }

    private Product getProductByReference(String reference) {
        // TODO: Remplacer par une vraie recherche en base de données ou API
        Product p = new Product();
        p.setReference(reference);
        p.setName("Produit " + reference);
        p.setPrice(10.0);
        p.setTaxRate(0.2);
        p.setId(1);
        p.setReorderThreshold(5);
        return p;
    }
}
