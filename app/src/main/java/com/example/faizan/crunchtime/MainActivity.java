package com.example.faizan.crunchtime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    TextView testView;
    Button addButton;
    Button removeButton;
    TeamDBManager teamDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testView = (TextView) findViewById(R.id.testView);
        addButton = (Button) findViewById(R.id.addButton);
        removeButton = (Button) findViewById(R.id.removeButton);
        teamDBManager = new TeamDBManager(MainActivity.this);

        JSONArray gamesTest = NBAGames.getInterCloseGames(MainActivity.this);

        Log.d("gamesTestVal", gamesTest.toString());

        printDatabase();

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
    }

    public void printDatabase(){
        String dbString = teamDBManager.TeamDBtoString();
        testView.setText(dbString);
    }
}
