package com.example.faizan.crunchtime;

import android.app.*;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView testView;
    Button addButton;
    Button removeButton;
    Button refreshButton;
    TeamDBManager teamDBManager;
    ListView teamListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testView = (TextView) findViewById(R.id.testView);
        addButton = (Button) findViewById(R.id.addButton);
        removeButton = (Button) findViewById(R.id.removeButton);
        refreshButton = (Button) findViewById(R.id.refreshButton);
        teamListView = (ListView) findViewById(R.id.teamListView);
        teamDBManager = new TeamDBManager(MainActivity.this);

        startNBAService();

        updateListView();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIManager.showAddDialog(MainActivity.this);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIManager.showRemoveDialog(MainActivity.this);
            }
        });

        // This is a temporary workaround
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //printDatabase();
                updateListView();
                startNBAService();
            }
        });
    }

    public void printDatabase(){
        String dbString = teamDBManager.TeamDBtoString();
        testView.setText(dbString);
    }

    public void startNBAService(){
        Intent toNBAService = new Intent(MainActivity.this, NBAService.class);
        startService(toNBAService);
    }

    public void updateListView(){
        List<Team> teams = teamDBManager.getAllContacts();
        ListAdapter teamListAdapter = new TeamListAdapter(MainActivity.this, teams);
        teamListView.setAdapter(teamListAdapter);
    }
}
