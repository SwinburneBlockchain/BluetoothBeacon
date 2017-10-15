package com.swinblockchain.bluetoothbeacon.Controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.UUID;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


/**
 * Created by John on 13/10/2017.
 */

public class BluetoothManager {

    BluetoothAdapter mBluetoothAdapter = null;
    public static Console myConsole;
    String NAME;
    UUID MY_UUID;
    Message msg = Message.obtain();

    public BluetoothManager(Console console, String NAME, UUID MY_UUID) {
        this.myConsole = console;
        this.NAME = NAME;
        this.MY_UUID = MY_UUID;
    }

    public void startBluetoothServer() {
        //setup the bluetooth adapter.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        startServer(myConsole);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            myConsole.writeToConsole(msg.getData().getString("msg"));
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


    private void startServer(Console myConsole) {
        new Thread(new AcceptThread(myConsole)).start();
    }

    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private Console console;

        public AcceptThread(Console myConsole) {
            BluetoothServerSocket tmp = null;
            this.console = myConsole;
            // Create a new listening server socket
            try {
                console.getConsoleWindow().append("Starting server for " + console.getProducer().getName() + "\n");
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                console.getConsoleWindow().append("Failed to start server\n");
            }
            mmServerSocket = tmp;
        }

        public void run() {
            while (true) {
                output("Listening for connection");

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
                        //Generate sig //TODO

                        output("Sending response");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        socket.getOutputStream().write(console.signMessage(console.getProducer()));

                        //output("Hi from Bluetooth Demo Server"); //Message to
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
