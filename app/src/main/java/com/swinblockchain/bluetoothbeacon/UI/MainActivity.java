package com.swinblockchain.bluetoothbeacon.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.swinblockchain.bluetoothbeacon.Controller.Console;
import com.swinblockchain.bluetoothbeacon.R;

public class MainActivity extends AppCompatActivity {

    Console console;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console = new Console((TextView) findViewById(R.id.consoleWindow));

        //manageProducers();
        console.loadKeyFiles();

        byte[] signature = console.signMessage(console.getProducerList().get(0).getPrivKey(), console.getProducerList().get(0).getPubKey());
    }

    public void startAdmin(View view) {
        Intent i = new Intent(MainActivity.this, AdminActivity.class);
        //i.putStringArrayListExtra("producerNameList", producerNameList);
        startActivity(i);
    }

}
