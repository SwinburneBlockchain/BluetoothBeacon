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

import static com.swinblockchain.bluetoothbeacon.R.id.producerName;

public class AdminActivity extends AppCompatActivity {

    private ArrayList<String> producerNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        producerNameList = getIntent().getExtras().getStringArrayList("producerNameList");
        displayProducers();
    }

    private void displayProducers() {
        for (String  s : producerNameList) {
            createTableRow(s);
        }
    }

    private void createTableRow(final String s) {

        final TableLayout detailsTable = (TableLayout) findViewById(R.id.mainTable);
        final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.tablerow, null);

        TextView tv;
        tv = (TextView) tableRow.findViewById(R.id.informationCell);
        tv.setText(s);

        Button button = (Button) tableRow.findViewById(R.id.buttonSave);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain(s);
            }
        });
        detailsTable.addView(tableRow);
    }

    public void newProducerSave(View view) {
        backToMain(findViewById(R.id.producerName).toString());
    }

    private void backToMain(String producerName) {
        Intent i = new Intent(AdminActivity.this, MainActivity.class);
        i.putExtra("producerName", producerName);
        startActivity(i);
    }
}