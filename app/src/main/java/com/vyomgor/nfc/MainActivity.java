package com.vyomgor.nfc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    IntentFilter[] readFilters;
    android.app.PendingIntent pendingIntent;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView) findViewById(R.id.text);

        try {
            intent = new Intent(this, getClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            pendingIntent  = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            IntentFilter vyomFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            vyomFilter.addDataScheme("http");
            vyomFilter.addDataAuthority("google.com", null);
            IntentFilter textFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED, "text/plain");
            readFilters = new IntentFilter[]{vyomFilter, textFilter};

        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }


        processNFC(getIntent());
    }

    private void enableRead(){
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, readFilters, null);
    }
    private void disableRead(){
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableRead();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableRead();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processNFC(intent);
    }

    private void processNFC(Intent intent) {
        Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        textView.setText(".");
        if (messages != null) {
            for (Parcelable message:messages) {
                NdefMessage ndefMessage = (NdefMessage) message;
                for (NdefRecord record : ndefMessage.getRecords()) {
                  switch(record.getTnf()){
                        case NdefRecord.TNF_WELL_KNOWN:
                            textView.append("WELL KNOWN: ");
                            if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                                textView.append("TEXT: ");
                                textView.append(new String(record.getPayload()));
                                textView.append("\n");
                            } else if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
                                textView.append("URI: ");
                                textView.append(new String(record.getPayload()));
                                textView.append("\n");
                            }
                    }
                }
            }
        }
    }
}
