package com.swinblockchain.bluetoothbeacon.UI;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.swinblockchain.bluetoothbeacon.Controller.Console;
import com.swinblockchain.bluetoothbeacon.Model.Producer;
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

/**
 * The main activity that is presented to the user
 *
 * @author John Humphrys
 */
public class MainActivity extends AppCompatActivity {

    Console console;
    TextView producerName;
    Producer currProducer;
    Thread runningThread;
    TextView consoleWindow;

    BluetoothAdapter mBluetoothAdapter = null;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final String NAME = "ProductChain";

    /**
     * Called when created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console = new Console();
        consoleWindow = (TextView) findViewById(R.id.consoleWindow);
        producerName = (TextView) findViewById(R.id.producerName);
        console.loadKeyFiles();

        // Check if started up or coming from admin activity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("producerName")) {
                console.setProducer(console.findProducer(extras.getString("producerName")));
                producerName.setText(console.getProducer().getName());
            }
        }

        producerName.setText("Producer: " + console.getProducer().getName());

        startBluetoothServer();
    }

    /**
     * Starts the admin activity
     *
     * @param view
     */
    public void startAdmin(View view) {
        Intent i = new Intent(MainActivity.this, AdminActivity.class);
        i.putStringArrayListExtra("producerList", console.getProducerStringList());
        seeYaMate(runningThread);
        startActivity(i);
    }

    /**
     * Starts the bluetooth server
     */
    public void startBluetoothServer() {
        // Setup the bluetooth adapter.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            output("Bluetooth not supported");
            return;
        }
        // Make sure bluetooth is enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);

        } else {
            output("Starting server");
            startServer();
        }
    }

    /**
     * When the activity returns, onCreate is called again
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        //startBluetoothServer();
    }

    /**
     * The handler method assists in displaying information to the console in the UI thread
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {

                if (msg.getData().getString("msg").equals("")) {
                    consoleWindow.setText("");
                } else {
                    consoleWindow.setText(consoleWindow.getText() + sdf.format(System.currentTimeMillis()) + " " + msg.getData().getString("msg") + "\n");
                }
                System.out.println(msg.getData().getString("msg"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

    });

    /**
     * Passes information to the handler
     *
     * @param str The string to pass to the handler
     */
    public void output(String str) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    /**
     * Starts the bluetooth thread
     */
    public void startServer() {
        runningThread = new Thread(new AcceptThread());
        output("Creating thread");
        runningThread.start();
    }

    /**
     * seeYaMate terminates a running thread
     *
     * @param thread The thread to say goodbye to
     */
    private void seeYaMate(Thread thread) {
        thread.interrupt();
        thread = null;
    }

    /**
     * The Accept Thread accepts bluetooth requests for proof of location
     */
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

        /**
         * The run thread opens a socket and waits for a connection
         */
        public void run() {
            while (true) {
                output("Bluetooth enabled, listening and responding to requests");

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
                        String hashOfData = new String(Hex.encodeHex(DigestUtils.sha256(sign + "," + console.getProducer().getPubKeyPEMString() + "," + console.getTimestamp())));

                        output("Sending response");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println(sign + "," + console.getProducer().getPubKeyPEMString() + "," + console.getTimestamp() + "," + hashOfData);

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

        /**
         * Stops the thread from running
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                output("close() of connect socket failed: " + e.getMessage());
            }
        }
    }


}
