package com.example.pda.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pda.Models.InvoiceLineAndTagsDto;
import com.example.pda.R;
import com.example.pda.api.ApiClient;
import com.example.pda.api.InvoiceApi;

import retrofit2.*;

import java.util.List;

public class InvoiceDetailsActivity extends AppCompatActivity {

    private TextView invoiceIdText;
    private ListView invoiceLinesView;
    private InvoiceApi invoiceApi;
    private int invoiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_details);

        invoiceId = getIntent().getIntExtra("invoiceId", -1);
        invoiceIdText = findViewById(R.id.invoiceIdText);
        invoiceLinesView = findViewById(R.id.invoiceLinesView);

        invoiceIdText.setText("Facture #" + invoiceId);

        invoiceApi = ApiClient.getClient().create(InvoiceApi.class);
        loadInvoiceDetails(invoiceId);
    }

    private void loadInvoiceDetails(int invoiceId) {
        invoiceApi.getInvoiceDetails(invoiceId).enqueue(new Callback<List<InvoiceLineAndTagsDto>>() {
            @Override
            public void onResponse(Call<List<InvoiceLineAndTagsDto>> call, Response<List<InvoiceLineAndTagsDto>> response) {
                if (response.isSuccessful()) {
                    List<InvoiceLineAndTagsDto> lines = response.body();
                    ArrayAdapter adapter = new ArrayAdapter<>(InvoiceDetailsActivity.this, android.R.layout.simple_list_item_1, lines);
                    invoiceLinesView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<InvoiceLineAndTagsDto>> call, Throwable t) {
                Toast.makeText(InvoiceDetailsActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
