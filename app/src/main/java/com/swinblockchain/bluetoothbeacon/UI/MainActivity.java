package com.swinblockchain.bluetoothbeacon.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.swinblockchain.bluetoothbeacon.Controller.BluetoothManager;
import com.swinblockchain.bluetoothbeacon.Controller.Console;
import com.swinblockchain.bluetoothbeacon.R;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Console console;
    TextView producerName;

    public static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final String NAME = "ProductChain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console = new Console((TextView) findViewById(R.id.consoleWindow));
        producerName = (TextView) findViewById(R.id.producerName);
        console.loadKeyFiles();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("producerName")) {
                console.setProducer(console.findProducer(extras.getString("producerName")));
                producerName.setText("Producer: " + extras.getString("producerName"));
            }
        }

        producerName.setText("Producer: " + console.getProducer().getName());

        //byte[] signature = console.signMessage(console.getProducerList().get(0).getPrivKey(), console.getProducerList().get(0).getPubKey());

        BluetoothManager bt = new BluetoothManager(console, NAME, MY_UUID);
        bt.startBluetoothServer();
    }

    public void updateConsole(final String string) {
        runOnUiThread(new Runnable() {
            public void run() {
                console.writeToConsole(string);
            }
        });
    }

    public void startAdmin(View view) {
        Intent i = new Intent(MainActivity.this, AdminActivity.class);
        i.putStringArrayListExtra("producerList", console.getProducerStringList());
        startActivity(i);
    }

}
