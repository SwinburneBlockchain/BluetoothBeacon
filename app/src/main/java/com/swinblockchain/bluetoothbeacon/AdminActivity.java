package com.swinblockchain.bluetoothbeacon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private ArrayList<Producer> producerList;
    Console console;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

         console = getIntent().getParcelableExtra("console");
        displayProducers();
    }

    private void displayProducers() {
        for (Producer p : console.getProducerList()) {
            createTableRow(p);
        }
    }

    private void createTableRow(final Producer p) {

        final TableLayout detailsTable = (TableLayout) findViewById(R.id.mainTable);
        final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.tablerow, null);

        TextView tv;
        tv = (TextView) tableRow.findViewById(R.id.informationCell);
        tv.setText(p.getName() + "\nPublic Key:" + p.getPubKey());

        Button button = (Button) tableRow.findViewById(R.id.buttonSave);

        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                console.setProducer(p);
                backToMain();
            }
        });
        detailsTable.addView(tableRow);
    }

    public void newProducerSave(View view) {
        Producer newProducer = console.generateKeyPair(findViewById(R.id.producerName).toString());
        console.producerList.add(newProducer);
        console.setProducer(newProducer);
        backToMain();
    }

    private void backToMain() {
        Intent i = new Intent(AdminActivity.this, MainActivity.class);
        startActivity(i);
    }
}