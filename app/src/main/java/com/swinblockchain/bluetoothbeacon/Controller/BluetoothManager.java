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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.swinblockchain.bluetoothbeacon.UI.MainActivity;

/**
 * Created by John on 13/10/2017.
 */

public class BluetoothManager {

    BluetoothAdapter mBluetoothAdapter = null;
    Console console;
    String NAME;
    UUID MY_UUID;

    public BluetoothManager(Console console, String NAME, UUID MY_UUID){
        this.console = console;
        this.NAME = NAME;
        this.MY_UUID = MY_UUID;
    }

    public void startBluetoothServer() {
        //setup the bluetooth adapter.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
//TODO
        }

        startServer();
    }

    private void startServer() {
        new Thread(new AcceptThread()).start();
    }

    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            // Create a new listening server socket
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                mkmsg("Failed to start server\n");
            }
            mmServerSocket = tmp;
        }

        public void mkmsg(String str) {
            //handler junk, because thread can't update screen!
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("msg", str);
            msg.setData(b);
            System.out.println("SYSOUT:" + str);
        }

        public void run() {
            mkmsg("waiting on accept");
            BluetoothSocket socket = null;
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                mkmsg("Failed to accept\n");
            }

            // If a connection was accepted
            if (socket != null) {
                mkmsg("Connection made\n");
                mkmsg("Remote device address: " + socket.getRemoteDevice().getAddress() + "\n");
                //Note this is copied from the TCPdemo code.
                try {
                    mkmsg("Attempting to receive a message ...\n");
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = in.readLine();
                    mkmsg("received a message:\n" + str + "\n");

                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    mkmsg("Attempting to send message ...\n");

                    socket.getOutputStream().write(console.signMessage(console.getProducer()));

                    out.println("Hi from Bluetooth Demo Server");
                    out.flush();
                    mkmsg("Message sent...\n");

                    mkmsg("We are done, closing connection\n");
                } catch (Exception e) {
                    mkmsg("Error happened sending/receiving\n");

                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        mkmsg("Unable to close socket" + e.getMessage() + "\n");
                    }
                }
            } else {
                mkmsg("Made connection, but socket is null\n");
            }
            mkmsg("Server ending \n");
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                mkmsg( "close() of connect socket failed: "+e.getMessage() +"\n");
            }
        }
    }
}
