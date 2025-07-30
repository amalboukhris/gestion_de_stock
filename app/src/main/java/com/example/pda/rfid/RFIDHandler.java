package com.example.pda.rfid;

import android.content.Context;
import android.util.Log;

import com.zebra.rfid.api3.*;

import java.util.List;

public class RFIDHandler implements RfidEventsListener {

    private static final String TAG = "RFIDHandler";

    private Context context;
    private RFIDReader rfidReader;
    private Readers readers;
    private RFIDListener listener;

    public RFIDHandler(Context context, RFIDListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void initScanner() {
        try {
            readers = new Readers(context, ENUM_TRANSPORT.SERVICE_SERIAL);
            List<ReaderDevice> availableReaders = readers.GetAvailableRFIDReaderList();

            if (availableReaders != null && !availableReaders.isEmpty()) {
                rfidReader = availableReaders.get(0).getRFIDReader();
                rfidReader.connect();

                rfidReader.Events.addEventsListener(this);
                rfidReader.Events.setTagReadEvent(true);
                rfidReader.Events.setInventoryStartEvent(true);
                rfidReader.Events.setInventoryStopEvent(true);

                // Config antenne, etc...
                Antennas antennas = rfidReader.Config.Antennas;
                antennas.setAntennaRfConfig(1, antennas.getAntennaRfConfig(1));

                rfidReader.Actions.Inventory.perform();

                Log.i(TAG, "RFID scanner initialized and inventory started");
            } else {
                Log.e(TAG, "No RFID readers available");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing scanner", e);
        }
    }

    public void closeScanner() {
        try {
            if (rfidReader != null && rfidReader.isConnected()) {
                rfidReader.Actions.Inventory.stop();
                rfidReader.Events.removeEventsListener(this);
                rfidReader.disconnect();
                rfidReader = null;
            }
            if (readers != null) {
                readers.Dispose();
                readers = null;
            }
            Log.i(TAG, "RFID scanner closed");
        } catch (Exception e) {
            Log.e(TAG, "Error closing scanner", e);
        }
    }

    @Override
    public void eventReadNotify(RfidReadEvents events) {
        List<TagData> tags = (List<TagData>) events.getReadEventData().tagData;
        if (tags != null) {
            for (TagData tag : tags) {
                String epc = tag.getTagID();
                if (listener != null) {
                    listener.onTagRead(epc);
                }
            }
        }
    }

    @Override
    public void eventStatusNotify(RfidStatusEvents events) {
        // Gérer les changements d’état du lecteur si besoin
    }

    public interface RFIDListener {
        void onTagRead(String epc);
    }
}
