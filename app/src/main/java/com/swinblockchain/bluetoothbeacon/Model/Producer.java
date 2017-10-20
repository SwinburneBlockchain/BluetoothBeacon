package com.swinblockchain.bluetoothbeacon.Model;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
/*
  Swinburne Capstone Project - ICT90004
  Aidan Beale & John Humphrys
  https://github.com/SwinburneBlockchain
*/
  
/**
 * The producer object contains all information about a Producer
 *
 * @author John Humphrys
 */
public class Producer {

    String name;
    PublicKey pubKeyDER;
    PrivateKey privKeyDER;
    String pubKeyPEMString;
    String privKeyPEMString;

    public Producer(String name, PublicKey pubKeyDER, PrivateKey privKeyDER, String pubKeyPEMString, String privKeyPEMString) {
        this.name = name;
        this.pubKeyDER = pubKeyDER;
        this.privKeyDER = privKeyDER;
        this.pubKeyPEMString = pubKeyPEMString;
        this.privKeyPEMString = privKeyPEMString;
    }

    public Producer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PublicKey getPubKeyDER() {
        return pubKeyDER;
    }

    public void setPubKeyDER(PublicKey pubKeyDER) {
        this.pubKeyDER = pubKeyDER;
    }

    public PrivateKey getPrivKeyDER() {
        return privKeyDER;
    }

    public void setPrivKeyDER(PrivateKey privKeyDER) {
        this.privKeyDER = privKeyDER;
    }

    public String getPubKeyPEMString() {
        return pubKeyPEMString;
    }

    public void setPubKeyPEMString(String pubKeyPEMString) {
        this.pubKeyPEMString = pubKeyPEMString;
    }

    public String getPrivKeyPEMString() {
        return privKeyPEMString;
    }

    public void setPrivKeyPEMString(String privKeyPEMString) {
        this.privKeyPEMString = privKeyPEMString;
    }
}
