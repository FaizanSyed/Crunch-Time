package com.example.faizan.crunchtime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button addButton;
    private Button removeButton;
    private TeamDBManager teamDBManager;
    private ListView teamListView;
    private UIManager uiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = (Button) findViewById(R.id.addButton);
        removeButton = (Button) findViewById(R.id.removeButton);
        teamListView = (ListView) findViewById(R.id.teamListView);
        uiManager = new UIManager();
        teamDBManager = new TeamDBManager(MainActivity.this);

        startNBAService();
        updateListView();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiManager.showAddDialog(MainActivity.this);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiManager.showRemoveDialog(MainActivity.this);
            }
        });
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
