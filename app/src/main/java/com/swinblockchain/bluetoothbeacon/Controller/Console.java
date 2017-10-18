package com.swinblockchain.bluetoothbeacon.Controller;

import android.content.res.AssetManager;

import android.widget.TextView;

import com.swinblockchain.bluetoothbeacon.App;
import com.swinblockchain.bluetoothbeacon.Model.Producer;

import java.io.BufferedReader;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

/**
 * The Console class manages all keys in the system
 *
 * @author John Humphrys
 */
public class Console {

    private Producer producer;
    String timestamp = String.valueOf(System.currentTimeMillis());;
    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    ArrayList<Producer> producerList = new ArrayList<>();
    ArrayList<String> producerStringList = new ArrayList<>();

    public Console() {
    }

    /**
     * Signs a message using the producers private key
     *
     * @param p The producer to use
     * @return The string containing the sign
     */
    public String signMessage(Producer p) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            byte[] data = timestamp.getBytes("UTF8");

            sig.initSign(p.getPrivKeyDER());
            sig.update(data);

            byte[] signatureBytes = sig.sign();

            sig.initVerify(p.getPubKeyDER());
            sig.update(data);

            System.out.println(sig.verify(signatureBytes));
            return bytesToHex(signatureBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads all the key files from assets
     */
    public void loadKeyFiles() {
        AssetManager assetManager = App.getContext().getAssets();
        InputStream input;
        Producer prod;

        try {
            String[] list = assetManager.list("");

            // Check if is a valid key file
            for (String keyFile : list) {
                if (keyFile.contains(".")) {
                    String[] keyFileArr = keyFile.split("\\.");
                    if (keyFileArr.length == 3) {
                        if (keyFileArr[2].equals("der") || keyFileArr[2].equals("pem")) {
                            if (findProducer(keyFileArr[0]) == null) {
                                prod = new Producer(keyFileArr[0]);
                                producerList.add(prod);
                                producerStringList.add(keyFileArr[0]);
                            } else {
                                prod = findProducer(keyFileArr[0]);
                            }

                            // Store all types of keys in producer
                            try {
                                if (keyFileArr[2].equals("pem")) {

                                    input = assetManager.open(keyFile);
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                                    String temp = reader.readLine();
                                    StringBuilder fullKey = new StringBuilder("");
                                    while (temp != null) {
                                        try {
                                            // Skip commented lines in csv
                                            if (!temp.substring(0, 2).equals("//")) {
                                                fullKey.append(temp);
                                            }
                                        } catch (Exception e) {

                                        }
                                        temp = reader.readLine();
                                    }
                                    reader.close();

                                    if (keyFileArr[1].equals("private")) {
                                        prod.setPrivKeyPEMString(fullKey.toString());
                                    } else if (keyFileArr[1].equals("public"))
                                        prod.setPubKeyPEMString(fullKey.toString());
                                }
                                if (keyFileArr[2].equals("der")) {
                                    input = assetManager.open(keyFile);
                                    byte[] bytes = new byte[input.available()];
                                    input.read(bytes);
                                    input.close();

                                    if (keyFileArr[1].equals("private")) {
                                        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
                                        KeyFactory kf = KeyFactory.getInstance("RSA");
                                        PrivateKey pvt = kf.generatePrivate(ks);
                                        prod.setPrivKeyDER(pvt);

                                    } else if (keyFileArr[1].equals("public")) {
                                        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
                                        KeyFactory kf = KeyFactory.getInstance("RSA");
                                        PublicKey pub = kf.generatePublic(ks);
                                        prod.setPubKeyDER(pub);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (producerList.size() != 0) {
            producer = producerList.get(0); // Default producer
        }
    }

    /**
     * Finds a producer in the producer list
     *
     * @param producerName The producer name to look for
     * @return The found producer
     */
    public Producer findProducer(String producerName) {
        for (Producer p : producerList) {
            if (producerName.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Converts bytes into hex
     *
     * @param bytes The bytes to convert
     * @return The hex as a string
     */
    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public ArrayList<Producer> getProducerList() {
        return producerList;
    }

    public void setProducerList(ArrayList<Producer> producerList) {
        this.producerList = producerList;
    }

    public ArrayList<String> getProducerStringList() {
        return producerStringList;
    }

    public void setProducerStringList(ArrayList<String> producerStringList) {
        this.producerStringList = producerStringList;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
