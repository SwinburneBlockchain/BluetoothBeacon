package com.swinblockchain.bluetoothbeacon;

import android.os.Parcel;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by John on 12/10/2017.
 */

public class Producer {

    String name;
    PublicKey pubKey;
    PrivateKey privKey;

    public Producer(String name, PublicKey pubKey, PrivateKey privKey) {
        this.name = name;
        this.pubKey = pubKey;
        this.privKey = privKey;
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
