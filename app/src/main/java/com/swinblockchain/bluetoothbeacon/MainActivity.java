package com.swinblockchain.bluetoothbeacon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import static com.swinblockchain.bluetoothbeacon.R.id.producerName;

public class MainActivity extends AppCompatActivity {

    Console console;
    ArrayList<Producer> producerList;
    ArrayList<String> producerNameList;

    private boolean firstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console = new Console((TextView) findViewById(R.id.consoleWindow));

        if (firstRun) {
            loadProducers();
            createProducerNameList();
            firstRun = false;
        } else {
            String newProducerName = getIntent().getExtras().getString("producerName");

            if (producerNameList.contains(newProducerName)) {
                console.setProducer(findProducer(newProducerName));
            } else {
                Producer newProducer = console.generateKeyPair(newProducerName);
                producerList.add(newProducer);
                producerNameList.add(newProducerName);
                console.setProducer(newProducer);
            }
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

    private void loadProducers() {
        console.log("Loading Producers");
        // TODO load producers
        producerList = new ArrayList<>();
        producerNameList = new ArrayList<>();

        for (Producer p : producerList) {
            producerNameList.add(p.getName());
        }

        console.log("Producers successfully loaded");
    }

    private void createProducerNameList() {
        for (Producer p : producerList) {
            producerNameList.add(p.getName());
        }
    }

    public void startAdmin(View view) {
        Intent i = new Intent(MainActivity.this, AdminActivity.class);
        i.putStringArrayListExtra("producerNameList", producerNameList);
        startActivity(i);
    }

}
