package com.swinblockchain.bluetoothbeacon.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.swinblockchain.bluetoothbeacon.Controller.Console;
import com.swinblockchain.bluetoothbeacon.R;

import java.util.ArrayList;

/**
 * The AdminActivityallows a user to change the main producer
 *
 * @author John Humphrys
 */
public class AdminActivity extends AppCompatActivity {

    private ArrayList<String> producerNameList;
    Console console;

    /**
     * Run when the activty is created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        producerNameList = getIntent().getExtras().getStringArrayList("producerList");
        console = getIntent().getParcelableExtra("console");
        displayProducers();
    }

    /**
     * Displays all the producers in a table
     */
    private void displayProducers() {
        for (String s : producerNameList) {
            createTableRow(s.toString());
        }
    }

    /**
     * Creates a table row with a producer name and button
     *
     * @param s The producers name
     */
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

    /**
     * Sends the information back to the main activity
     *
     * @param producerName The producer name the user has selected
     */
    private void backToMain(String producerName) {
        Intent i = new Intent(AdminActivity.this, MainActivity.class);
        i.putExtra("producerName", producerName);
        startActivity(i);
    }
}