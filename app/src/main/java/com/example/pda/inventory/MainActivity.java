package com.example.pda.inventory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pda.R;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.zebra.rfid.api3.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EMDKManager.EMDKListener {

    private static final String TAG = "ZebraRFIDScanner";
    private RFIDReader rfidReader;
    private Readers readers;
    private EMDKManager emdkManager;
    private boolean isReaderConnecting = false;

    private TextView tvStatus;
    private ListView lvTags;
    private ArrayList<String> tagList;
    private ArrayAdapter<String> adapter;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate()");

        initializeUI();
        initializeEMDK();
    }

    private void initializeUI() {
        tvStatus = findViewById(R.id.tv_status);
        lvTags = findViewById(R.id.lv_tags);
        Button btnStart = findViewById(R.id.btn_start);
        Button btnStop = findViewById(R.id.btn_stop);

        tagList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagList);
        lvTags.setAdapter(adapter);

        btnStart.setOnClickListener(v -> {
            if (isRFIDReady()) {
                startInventory();
            } else if (!isReaderConnecting) {
                updateStatus("Connecting to reader...");
                Log.i(TAG, "Attempting to connect to reader");
                initializeRFIDReader();
            } else {
                updateStatus("Reader connection in progress...");
            }
        });

        btnStop.setOnClickListener(v -> stopInventory());
    }

    private void initializeEMDK() {
        Log.d(TAG, "Initializing EMDK");
        try {
            EMDKResults results = EMDKManager.getEMDKManager(this, this);
            if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
                updateStatus("EMDK init failed");
                Log.e(TAG, "EMDKManager initialization failed");
            }
        } catch (Exception e) {
            Log.e(TAG, "EMDK initialization error", e);
            updateStatus("EMDK init error");
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;
        updateStatus("EMDK initialized");
        initializeRFIDReader();
    }

    private void initializeRFIDReader() {
        Log.d(TAG, "Initializing RFID Reader");
        try {
            readers = new Readers(getApplicationContext(), ENUM_TRANSPORT.SERVICE_SERIAL);
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

    private void connectToReader() {
        Log.d(TAG, "Attempting to connect to reader");
        isReaderConnecting = true;

        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Reader connection error", e);
            updateStatus("Connection error: " + e.getMessage());
        } finally {
            isReaderConnecting = false;
        }
    }

    private void configureReader() throws Exception {
        if (rfidReader == null || !rfidReader.isConnected()) {
            throw new Exception("Reader not connected");
        }

        // Mettre le lecteur en mode déclencheur automatique
        rfidReader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true);

        // Configuration de l'antenne 1 (par défaut sur la plupart des Zebra MC339R)
        try {
            int antennaId = 1;
            Antennas.AntennaRfConfig config = rfidReader.Config.Antennas.getAntennaRfConfig(antennaId);

            // Puissance maximale : 300 (30dBm)
            config.setrfModeTableIndex(0); // Mode lecture rapide
            config.setTari(0); // Valeur recommandée
            config.setTransmitPowerIndex(270); // 270 = environ 27 dBm, 300 = max

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

    private void startInventory() {
        if (!isRFIDReady()) {
            updateStatus("Reader not ready");
            return;
        }

        try {
            handler.post(() -> {
                tagList.clear();
                adapter.notifyDataSetChanged();
            });

            rfidReader.Actions.Inventory.perform();
            updateStatus("Scanning started");
        } catch (Exception e) {
            Log.e(TAG, "Inventory start error", e);
            updateStatus("Start error");
        }
    }

    private void stopInventory() {
        if (isRFIDReady()) {
            try {
                rfidReader.Actions.Inventory.stop();
                updateStatus("Scanning stopped");
            } catch (Exception e) {
                Log.e(TAG, "Inventory stop error", e);
                updateStatus("Stop error");
            }
        }
    }

    private boolean isRFIDReady() {
        return rfidReader != null && rfidReader.isConnected();
    }

    private void updateStatus(String message) {
        handler.post(() -> tvStatus.setText(message));
    }

    private class EventHandler implements RfidEventsListener {
        @Override
        public void eventReadNotify(RfidReadEvents events) {
            try {
                TagData tag = events.getReadEventData().tagData;
                if (tag != null) {
                    String epc = tag.getTagID();
                    handler.post(() -> {
                        if (!tagList.contains(epc)) {
                            tagList.add(0, epc);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Tag read error", e);
            }
        }

        @Override
        public void eventStatusNotify(RfidStatusEvents events) {
            String status = events.StatusEventData.getStatusEventType().toString();
            updateStatus("Status: " + status);
        }
    }

    @Override
    public void onClosed() {
        try {
            if (emdkManager != null) {
                emdkManager.release();
                emdkManager = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "onClosed error", e);
        }
    }

    @Override
    protected void onDestroy() {
        cleanup();
        super.onDestroy();
    }

    private void cleanup() {
        try {
            if (rfidReader != null && rfidReader.isConnected()) {
                rfidReader.disconnect();
            }
            safeDisposeReaders();
            if (emdkManager != null) {
                emdkManager.release();
                emdkManager = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Cleanup error", e);
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
}
