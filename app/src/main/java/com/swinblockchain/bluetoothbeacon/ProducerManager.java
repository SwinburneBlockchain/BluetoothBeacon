package com.swinblockchain.bluetoothbeacon;

import java.util.ArrayList;

/**
 * Created by John on 13/10/2017.
 */

public class ProducerManager {
    ArrayList<Producer> producerList;

    public void loadData() {
        // if file exists read it
        producerList = new ArrayList<>();
    }

    

    public ArrayList<Producer> getProducerList() {
        return producerList;
    }

    public void setProducerList(ArrayList<Producer> producerList) {
        this.producerList = producerList;
    }
}
