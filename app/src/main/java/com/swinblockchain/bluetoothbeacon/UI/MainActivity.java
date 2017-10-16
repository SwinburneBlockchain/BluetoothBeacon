package com.swinblockchain.bluetoothbeacon.UI;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.swinblockchain.bluetoothbeacon.Controller.Console;
import com.swinblockchain.bluetoothbeacon.R;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Console console;
    TextView producerName;
    BluetoothManager bt;

    BluetoothAdapter mBluetoothAdapter = null;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

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

        startBluetoothServer();
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

    public void startBluetoothServer() {
        //setup the bluetooth adapter.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            output("Bluetooth not supported");
            return;
        }
        //make sure bluetooth is enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);

        } else {
            startServer();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        startServer();
    }



    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            console.writeToConsole(sdf.format(System.currentTimeMillis()) + " " + msg.getData().getString("msg"));
            return true;
        }

    });

    public void output(String str) {
        //handler junk, because thread can't update screen!
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    public void startServer() {
        new Thread(new AcceptThread()).start();
    }

    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            // Create a new listening server socket
            try {
                output("Starting server for " + console.getProducer().getName() + "\n");
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                output("Failed to start server\n");
            }
            mmServerSocket = tmp;
        }

        public void run() {
            while (true) {
                output("Bluetooth enabled, listening for connection");

                BluetoothSocket socket = null;
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    output("Failed to accept connection");
                }

                // If a connection was accepted
                if (socket != null) {
                    output("Connection established with " + socket.getRemoteDevice().getAddress());
                    try {
                        output("Attempting to read request");
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String str = in.readLine();
                        output("Received request for proof of location");

                        output("Signing message and timestamp");
                        String sign = console.signMessage(console.getProducer());
                        String timestamp = String.valueOf(System.currentTimeMillis());
                        String hashOfData = new String(Hex.encodeHex(DigestUtils.sha256(sign + "," + console.getProducer().getPubKeyPEMString() + "," + timestamp)));

                        output("Sending response");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println(sign + "," + console.getProducer().getPubKeyPEMString() + "," + timestamp + "," + hashOfData);

                        out.flush();
                        output("Response sent");

                        output("Finished transaction, closing connection");
                    } catch (Exception e) {
                        output("Error occured sending/receiving");

                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            output("Unable to close socket" + e.getMessage());
                        }
                    }
                } else {
                    output("Made connection, but socket is null");
                }

                output("Server closing\n");
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                output("close() of connect socket failed: " + e.getMessage());
            }
        }
    }

}
