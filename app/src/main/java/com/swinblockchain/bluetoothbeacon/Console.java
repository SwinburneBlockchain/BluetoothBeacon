package com.swinblockchain.bluetoothbeacon;

import android.content.res.AssetManager;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import static android.R.attr.key;
import static android.R.attr.path;
import static android.R.id.message;
import static android.provider.Telephony.Mms.Part.FILENAME;

/**
 * Created by John on 12/10/2017.
 */

public class Console {

    private Producer producer;
    TextView consoleWindow;

    ArrayList<Producer> producerList = new ArrayList<>();

    public Console(TextView consoleWindow) {
        this.consoleWindow = consoleWindow;
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

    private Producer findProducer(String producerName) {
        for (Producer p : producerList) {
            if (p.getName().equals(producerName)) {
                return p;
            }
        }
        return null;
    }

    public void loadKeyFiles() {
        AssetManager assetManager = App.getContext().getAssets();
        InputStream input;

        try {
            String[] list = assetManager.list("");

            for (String keyFile : list) {
                if (keyFile.contains(".")) {
                    String[] keyFileArr = keyFile.split("\\.");
                    if (keyFileArr[1].equals("key") || keyFileArr[1].equals("pub")) {
                        if (findProducer(keyFileArr[0]) == null) {
                            Producer newProd = new Producer(keyFileArr[0]);
                            producerList.add(newProd);

                    /* Read all bytes from the private key file */
                            input = assetManager.open(keyFile);
                            byte[] bytes = new byte[input.available()];
                            input.read(bytes);
                            input.close();

                            try {
                        /* Generate key. */

                                if (keyFileArr[1].equals("key")) {
                                    PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
                                    KeyFactory kf = KeyFactory.getInstance("RSA");
                                    PrivateKey pvt = kf.generatePrivate(ks);
                                    newProd.setPrivKey(pvt);

                                } else if (keyFileArr[1].equals("pub")) {
                                    X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
                                    KeyFactory kf = KeyFactory.getInstance("RSA");
                                    PublicKey pub = kf.generatePublic(ks);
                                    newProd.setPubKey(pub);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                System.out.println();
            }
        } catch (
                IOException e)

        {
            e.printStackTrace();
        }
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public void log(String message) {
        consoleWindow.append(message + "\n");
    }
}
