package com.swinblockchain.bluetoothbeacon;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.Parcel;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by John on 12/10/2017.
 */

public class Producer {

    String name;
    PublicKey pubKey;
    PrivateKey privKey;

    private final static String FILENAME = "producers.csv";

    public Producer(String name, PublicKey pubKey, PrivateKey privKey) {
        this.name = name;
        this.pubKey = pubKey;
        this.privKey = privKey;
    }

    public Producer(String name) {
        this.name = name;
    }



    public static void save(ArrayList<Location> locations) {
        Context context = App.getContext();
        try {

            FileOutputStream output = context.openFileOutput(FILENAME, Activity.MODE_PRIVATE); // MODE_APPEND

            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(output));

            List<String[]> lines = new ArrayList<>();

            for (Location l: locations) {
                String entry[] = new String[4];
                //entry[0] = l.getName();
                entry[1] = String.valueOf(l.getLatitude());
                entry[2] = String.valueOf(l.getLongitude());
                //entry[3] = l.getTimeZone().getID();
                lines.add(entry);
            }

            csvWriter.writeAll(lines);
            csvWriter.close();
            output.close();

        }catch(FileNotFoundException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PublicKey pubKey) {
        this.pubKey = pubKey;
    }

    public PrivateKey getPrivKey() {
        return privKey;
    }

    public void setPrivKey(PrivateKey privKey) {
        this.privKey = privKey;
    }
}
