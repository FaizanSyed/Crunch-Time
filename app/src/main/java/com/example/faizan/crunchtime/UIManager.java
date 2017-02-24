package com.example.faizan.crunchtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class UIManager {

    private static ArrayList<String> selectedTeamNames;
    private static ArrayList<String> selectedTeamAcros;


    public static void showAddDialog(Context context){
        selectedTeamNames = new ArrayList();
        selectedTeamAcros = new ArrayList();
        showTeamDialog(context);
    }

    private static void showTeamDialog(final Context context){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Select Teams to Add")
                .setMultiChoiceItems(NBAConsts.allTeamNames, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            selectedTeamNames.add(NBAConsts.allTeamNames[indexSelected]);
                            selectedTeamAcros.add(NBAConsts.allTeamAcros[indexSelected]);
                        } else if (!isChecked) {
                            selectedTeamNames.remove(NBAConsts.allTeamNames[indexSelected]);
                            selectedTeamAcros.remove(NBAConsts.allTeamAcros[indexSelected]);
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(!selectedTeamNames.isEmpty()) {
                            showTimeDialog(context);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create();
        dialog.show();
    }

    private static void showTimeDialog(final Context context){
        View timeView = View.inflate(context, R.layout.time_layout, null);
        final Spinner periodSpinner = (Spinner) timeView.findViewById(R.id.periodSpinner);
        final Spinner secLeftSpinner = (Spinner) timeView.findViewById(R.id.secLeftSpinner);
        final Spinner minLeftSpinner = (Spinner) timeView.findViewById(R.id.minLeftSpinner);
        final Spinner scoreDiffSpinner = (Spinner) timeView.findViewById(R.id.scoreDiffSpinner);

        final ArrayAdapter<Integer> periodAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, NBAConsts.periodsInGame);
        final ArrayAdapter<Integer> secLeftAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, NBAConsts.secsInMin);
        final ArrayAdapter<Integer> minLeftAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, NBAConsts.minsInPeriod);
        final ArrayAdapter<Integer> scoreDiffAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, NBAConsts.scoreDiffs);

        periodSpinner.setAdapter(periodAdapter);
        secLeftSpinner.setAdapter(secLeftAdapter);
        minLeftSpinner.setAdapter(minLeftAdapter);
        scoreDiffSpinner.setAdapter(scoreDiffAdapter);

        //PRINT TEST HERE
        Log.d("ArrayListTest", Arrays.toString(selectedTeamNames.toArray()));
        Log.d("ArrayListTest", Arrays.toString(selectedTeamAcros.toArray()));

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("How much time left?")
                .setView(timeView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addToDB(context,
                                ((int) periodSpinner.getSelectedItem()),
                                ((int) minLeftSpinner.getSelectedItem()),
                                ((int) secLeftSpinner.getSelectedItem()),
                                ((int) scoreDiffSpinner.getSelectedItem()));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
    }

    private static void addToDB(Context context, int period, int minLeft, int secLeft, int scoreDiff){
        int numSelectedTeams = selectedTeamNames.size();
        TeamDBManager teamDBManager = new TeamDBManager(context);
        int timeRemaining = (60*minLeft)+secLeft;

        for(int i = 0; i < numSelectedTeams; i++){
            teamDBManager.addTeam(new Team(selectedTeamAcros.get(i), selectedTeamNames.get(i), period, timeRemaining, scoreDiff, 0));
        }

    }




    public static void showRemoveDialog(final Context context){
        selectedTeamNames = new ArrayList();
        selectedTeamAcros = new ArrayList();
        TeamDBManager teamDBManager = new TeamDBManager(context);
        final String[] allAddedTeamNames = teamDBManager.teamNamesToArray();
        final String[] allAddedTeamAcros = teamDBManager.teamAcrosToArray();

        if(allAddedTeamAcros.length == 0){
            Toast.makeText(context, "No teams added!", Toast.LENGTH_SHORT);
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Select Teams to Remove")
                .setMultiChoiceItems(allAddedTeamNames, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            selectedTeamNames.add(allAddedTeamNames[indexSelected]);
                            selectedTeamAcros.add(allAddedTeamAcros[indexSelected]);
                        } else if (!isChecked) {
                            selectedTeamNames.remove(allAddedTeamNames[indexSelected]);
                            selectedTeamAcros.remove(allAddedTeamAcros[indexSelected]);
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(!selectedTeamNames.isEmpty()) {
                            removeFromDB(context);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create();
        dialog.show();
    }

    public static void removeFromDB(Context context){
        int numTeamsToRemove = selectedTeamAcros.size();
        TeamDBManager teamDBManager = new TeamDBManager(context);

        for(int i = 0; i < numTeamsToRemove; i++){
            teamDBManager.deleteTeam(selectedTeamAcros.get(i));
        }

    }
}
