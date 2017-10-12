package com.swinblockchain.bluetoothbeacon;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;

/**
 * Created by John on 12/10/2017.
 */

public class Console {

    private Producer producer;
    TextView consoleWindow;

    public Console(TextView consoleWindow) {
        this.consoleWindow = consoleWindow;
    }

    public Producer generateKeyPair(String producerName) {

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);

            KeyPair pair = keyGen.generateKeyPair();

            Producer newProducer = new Producer(producerName, pair.getPublic(), pair.getPrivate());

            return newProducer;
        } catch (Exception e) {

            return null;
        }
    }

    public byte[] signMessage(String message) {

        try {
            Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
            dsa.initSign(producer.getPrivKey());

            dsa.update(message.getBytes());

            byte[] realSig = dsa.sign();

            return realSig;
        } catch (Exception e) {
            return null;
        }
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public void setConsoleWindow(TextView consoleWindow) {
        this.consoleWindow = consoleWindow;
    }
}
