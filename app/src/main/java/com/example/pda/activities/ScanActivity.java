package com.example.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pda.Models.CreateInvoiceRequest;
import com.example.pda.Models.CreateInvoiceResponse;
import com.example.pda.R;
import com.example.pda.api.ApiClient;
import com.example.pda.api.InvoiceApi;
import com.zebra.rfid.api3.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;

public class ScanActivity extends AppCompatActivity {

    private static final String TAG = "ScanActivity";

    private boolean isReaderConnecting = false;
    private boolean isScanning = false;

    private RFIDReader rfidReader = null;
    private Readers readers;
    private Context appContext;

    private final List<String> scannedTags = new ArrayList<>();
    private ListView tagListView;
    private ArrayAdapter<String> tagAdapter;
    private InvoiceApi invoiceApi;
    private Button sendButton;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        appContext = getApplicationContext();

        tagListView = findViewById(R.id.tagListView);
        sendButton = findViewById(R.id.sendButton);

        tagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scannedTags);
        tagListView.setAdapter(tagAdapter);

        invoiceApi = ApiClient.getClient().create(InvoiceApi.class);

        initializeRFIDReader();

        sendButton.setOnClickListener(v -> sendInvoice());
    }

    private void initializeRFIDReader() {
        Log.d(TAG, "Initializing RFID Reader");
        try {
            readers = new Readers(this, ENUM_TRANSPORT.SERVICE_SERIAL);
            handler.postDelayed(this::safeConnectToReader, 2000);
        } catch (Exception e) {
            Log.e(TAG, "RFID initialization error", e);
            updateStatus("RFID init error");
        }
    }

    private void safeConnectToReader() {
        if (isFinishing() || isDestroyed()) return;

        try {
            connectToReader();
        } catch (Exception e) {
            Log.e(TAG, "Safe connect error", e);
            updateStatus("Connection failed");
            safeDisposeReaders();
        }
    }

    private void safeDisposeReaders() {
        try {
            if (readers != null) {
                readers.Dispose();
                readers = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error disposing readers", e);
        }
    }

    private void connectToReader() throws Exception {
        Log.d(TAG, "Attempting to connect to reader");
        isReaderConnecting = true;

        if (readers == null) throw new Exception("Readers object is null");

        List<ReaderDevice> availableReaders = readers.GetAvailableRFIDReaderList();
        if (availableReaders == null || availableReaders.isEmpty()) throw new Exception("No RFID readers available");

        ReaderDevice device = availableReaders.get(0);
        rfidReader = device.getRFIDReader();

        if (rfidReader == null) throw new Exception("RFIDReader object is null");

        if (!rfidReader.isConnected()) {
            rfidReader.connect();
            configureReader();
            setupEventListeners();
            updateStatus("Connected: " + device.getName());
            Toast.makeText(this, "Reader Connected", Toast.LENGTH_SHORT).show();
        }

        isReaderConnecting = false;
    }

    private void configureReader() throws Exception {
        if (rfidReader == null || !rfidReader.isConnected()) {
            throw new Exception("Reader not connected");
        }

        rfidReader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true);

        try {
            int antennaId = 1;
            Antennas.AntennaRfConfig config = rfidReader.Config.Antennas.getAntennaRfConfig(antennaId);
            config.setrfModeTableIndex(0);
            config.setTari(0);
            config.setTransmitPowerIndex(270);
            rfidReader.Config.Antennas.setAntennaRfConfig(antennaId, config);
            Log.d(TAG, "Antenna configured successfully.");
        } catch (InvalidUsageException | OperationFailureException e) {
            Log.e(TAG, "Error configuring antenna", e);
            throw e;
        }
    }

    private void setupEventListeners() {
        try {
            rfidReader.Events.addEventsListener(new EventHandler());
            rfidReader.Events.setTagReadEvent(true);
            rfidReader.Events.setInventoryStartEvent(true);
            rfidReader.Events.setInventoryStopEvent(true);
        } catch (Exception e) {
            Log.e(TAG, "Event listener setup error", e);
        }
    }

    private void updateStatus(String message) {
        Log.d(TAG, "Status: " + message);
    }

    private void startScanning() {
        if (!isScanning && rfidReader != null && rfidReader.isConnected()) {
            try {
                rfidReader.Actions.Inventory.perform();
                isScanning = true;
                updateStatus("Scanning started");
            } catch (InvalidUsageException | OperationFailureException e) {
                Log.e(TAG, "Erreur démarrage scan", e);
                Toast.makeText(this, "Erreur démarrage scan: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void stopScanning() {
        if (isScanning && rfidReader != null && rfidReader.isConnected()) {
            try {
                rfidReader.Actions.Inventory.stop();
                isScanning = false;
                updateStatus("Scanning stopped");
            } catch (InvalidUsageException | OperationFailureException e) {
                Log.e(TAG, "Erreur arrêt scan", e);
                Toast.makeText(this, "Erreur arrêt scan: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class EventHandler implements RfidEventsListener {
        @Override
        public void eventReadNotify(RfidReadEvents events) {
            try {
                TagData tag = events.getReadEventData().tagData;
                if (tag != null) {
                    final String epc = tag.getTagID();
                    handler.post(() -> {
                        if (!scannedTags.contains(epc)) {
                            scannedTags.add(0, epc);
                            tagAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Tag read error", e);
            }
        }

        @Override
        public void eventStatusNotify(RfidStatusEvents events) {
            if (events.StatusEventData != null) {
                String status = events.StatusEventData.getStatusEventType().toString();
                Log.d(TAG, "Status event: " + status);
            } else {
                Log.d(TAG, "Status event received but StatusEventData is null");
            }
        }

    }

    private void sendInvoice() {
        if (scannedTags.isEmpty()) {
            Toast.makeText(this, "Aucun tag scanné", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setRfidTagIdList(new ArrayList<>(scannedTags));
        request.setSubTotal(100);
        request.setTvaTotal(19);
        request.setDiscount(0);
        request.setTotal(119);
        request.setClientId(1);

        invoiceApi.createInvoice(request).enqueue(new Callback<CreateInvoiceResponse>() {
            @Override
            public void onResponse(Call<CreateInvoiceResponse> call, Response<CreateInvoiceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int invoiceId = response.body().getInvoiceId();
                    Intent intent = new Intent(ScanActivity.this, InvoiceDetailsActivity.class);
                    intent.putExtra("invoiceId", invoiceId);
                    startActivity(intent);
                } else {
                    Toast.makeText(ScanActivity.this, "Erreur création facture", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CreateInvoiceResponse> call, Throwable t) {
                Toast.makeText(ScanActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScanning();
        if (rfidReader != null && rfidReader.isConnected()) {
            try {
                rfidReader.disconnect();
            } catch (InvalidUsageException | OperationFailureException e) {
                Log.e(TAG, "Erreur déconnexion RFID", e);
            }
        }
        safeDisposeReaders();
    }
}
