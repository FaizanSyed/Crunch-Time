package com.example.faizan.crunchtime;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class NBAGames {
    private static JSONObject infoForAllGames;
    private static JSONArray allGamesArr;
    private static JSONArray allTrimGamesArr;

    public static JSONArray getInterCloseGames(Context context){
        // Get current NBA scores
        getScores();

        Log.d("GICG", "infoForAllGames = " + infoForAllGames.toString());
        Log.d("GICG", "allTrimGamesArr = " + allTrimGamesArr.toString());

        TeamDBManager teamDBManager = new TeamDBManager(context);
        JSONArray trimGamesForNotif = new JSONArray();
        int numOfGames = allGamesArr.length();

        Log.d("GICG", "numOfGames = " + numOfGames);

        for(int i = 0; i < numOfGames; i++){

            try {
                JSONObject aTrimGame = allTrimGamesArr.getJSONObject(i);

                Log.d("GICGfor", "hta = " + aTrimGame.getString("hta"));
                Log.d("GICGfor", "vta = " + aTrimGame.getString("vta"));

                // Only games that the user it interested in and games that the user has not yet been notified about
                if((teamDBManager.exists(aTrimGame.getString("vta")) || teamDBManager.exists(aTrimGame.getString("hta"))) &&
                        ((teamDBManager.getSentNotif(aTrimGame.getString("vta")) == 0) &&
                                (teamDBManager.getSentNotif(aTrimGame.getString("hta")) == 0))){

                    String interTeamAcro;
                    String exta;

                    if(teamDBManager.exists(aTrimGame.getString("vta"))){
                        interTeamAcro = aTrimGame.getString("vta");
                        exta = "vta";
                    } else {
                        interTeamAcro = aTrimGame.getString("hta");
                        exta = "hta";
                    }

                    // If the game is in the future
                    if(aTrimGame.getString("cl") == "null" || aTrimGame.getInt("p") == 0){
                        // Get start time of game
                        String startTime = aTrimGame.getString("stt");
                        Log.d("GICGforif", "startTime = " + startTime);

                        // Get hour and min of the game
                        int hourOfGame = Integer.parseInt(startTime.substring(0, startTime.indexOf(':')));
                        int minOfGame = Integer.parseInt(startTime.substring(startTime.indexOf(':')+1, startTime.indexOf(':')+3));
                        // Convert hour to 24 hour
                        if((startTime.substring(startTime.indexOf(' ')+1, startTime.indexOf(' ')+3)) != "am"){
                            hourOfGame = hourOfGame+12;
                        }
                        Log.d("GICGforif", "GameTime = " + hourOfGame + ":" + minOfGame);

                        // Get current EST
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                        String currTime = df.format(c.getTime());
                        int currESTHour = Integer.parseInt(currTime.substring(0, currTime.indexOf(':')));
                        int currESTMin = Integer.parseInt(currTime.substring(currTime.indexOf(':')+1, currTime.indexOf(':')+3));
                        Log.d("GICGforif", "CurrentTime = " + currTime + " CurrentHour = " + currESTHour + " CurrentMin = " + currESTMin);

                        int hoursUntilGame = hourOfGame - currESTHour;
                        int minsUntilGame = minOfGame - currESTMin;

                        int secondsUntilGameStart = ((hoursUntilGame*60)+minsUntilGame+1)*60;

                        if(secondsUntilGameStart <= 60){
                            aTrimGame.put("ni", 60);
                            trimGamesForNotif.put(aTrimGame);
                        } else {
                            aTrimGame.put("ni", secondsUntilGameStart);
                            trimGamesForNotif.put(aTrimGame);
                        }

                        Log.d("GICGforif", aTrimGame.getString("vta") + " secondsUntilGameStart =  " + secondsUntilGameStart);

                        continue;

                    }

                    int periodsLeftInGame = NBAConsts.periodsInGame.length - aTrimGame.getInt("p");
                    Log.d("GICGDuringGame", "periodsLeftInGame = " + periodsLeftInGame);
                    int minsLeftInPeriod = Integer.parseInt(aTrimGame.getString("cl").substring(0,2));
                    Log.d("GICGDuringGame","minsLeftInPeriod = " + minsLeftInPeriod);
                    int secsLeftInMin = Integer.parseInt(aTrimGame.getString("cl").substring(3,5));
                    Log.d("GICGDuringGame", "secsLeftInMin = " + secsLeftInMin);
                    int secsLeftInGame = (periodsLeftInGame*12*60)+(minsLeftInPeriod*60)+secsLeftInMin;
                    Log.d("GICGDuringGame", "secsLeftInGame = " + secsLeftInGame);
                    int absScoreDiff = aTrimGame.getInt("sd") < 0 ? -aTrimGame.getInt("sd") : aTrimGame.getInt("sd");

                    int reqSecsLeftInGame = teamDBManager.getSecRemaining(aTrimGame.getString(exta));
                    int reqScoreDiff = teamDBManager.getScoreDiff(interTeamAcro);
                    Log.d("GICGDuringGame", "reqSecsLiftInGame = " + reqSecsLeftInGame);
                    Log.d("GICGDuringGame", "reqScoreDiff = " + reqScoreDiff);

                    // If game is over, continue
                    if(secsLeftInGame == 0){
                        continue;
                    }
                    // If the time left in the game and score diff is less than required
                    else if(secsLeftInGame <= reqSecsLeftInGame && reqScoreDiff >= absScoreDiff){
                        aTrimGame.put("ni", 0);
                        trimGamesForNotif.put(aTrimGame);
                        teamDBManager.setSentNotif(aTrimGame.getString(interTeamAcro));
                    }
                    // If difference in time is less than a minute, wait 1 minute in between requests
                    else if(secsLeftInGame - reqSecsLeftInGame <= 60){
                        aTrimGame.put("ni", 60);
                        trimGamesForNotif.put(aTrimGame);
                    }
                    // If the difference is more than a minute, wait that amount of time
                    else if(secsLeftInGame- reqSecsLeftInGame > 60){
                        aTrimGame.put("ni", secsLeftInGame - reqSecsLeftInGame);
                        trimGamesForNotif.put(aTrimGame);
                    }

                }
            } catch(JSONException e){
                Log.d("getInterCloseGamesExep", e.getMessage());
                return null;
            }
        }
        return trimGamesForNotif;
    }

    private static void getScores(){
        allTrimGamesArr = new JSONArray();
        try {
            infoForAllGames = new RequestNBAScores().execute().get();
        } catch(ExecutionException e){
            Log.d("getScoresExecExep", e.getMessage());
        } catch (InterruptedException e){
            Log.d("getScoresInterExep", e.getMessage());
        }

        try {
            //allGamesArr = TempConsts.getjObj().getJSONObject("gs").getJSONArray("g");
            Log.d("getScores", "infoForAllGames = " + infoForAllGames.toString());
            allGamesArr = infoForAllGames.getJSONObject("gs").getJSONArray("g");
        }catch (JSONException e) {
            Log.d("GetNBAScoresJSONExep", e.getMessage());
            return;
        }

        int numOfGames = allGamesArr.length();
        for(int i = 0; i < numOfGames; i++){
            try {
                JSONObject aGame = allGamesArr.getJSONObject(i);
                JSONObject aTrimGame = new JSONObject();

                int period;
                String clock = aGame.getString("cl");
                if(clock == "null"){
                    period = 0;
                } else {
                    period = aGame.getInt("p");
                }
                String vTeamAcro = aGame.getJSONObject("v").getString("ta");
                String hTeamAcro = aGame.getJSONObject("h").getString("ta");
                int scoreDiff = (aGame.getJSONObject("v").getInt("s") - aGame.getJSONObject("h").getInt("s"));
                String vTeamName = aGame.getJSONObject("v").getString("tn");
                String hTeamName = aGame.getJSONObject("h").getString("tn");
                String startTime = aGame.getString("stt");

                aTrimGame.put("p", period);
                aTrimGame.put("cl", clock);
                aTrimGame.put("vta", vTeamAcro);
                aTrimGame.put("hta", hTeamAcro);
                aTrimGame.put("vtn", vTeamName);
                aTrimGame.put("htn", hTeamName);
                aTrimGame.put("sd", scoreDiff);
                aTrimGame.put("stt", startTime);

                allTrimGamesArr.put(aTrimGame);
            }catch (JSONException e){
                Log.d("GetNBAScoresJSONExep", e.getMessage());
                return;
            }
        }
    }

    private static class RequestNBAScores extends AsyncTask<Void, Void, JSONObject>{
        StringBuilder stringBuilder;
        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                InputStream response = new URL(NBAConsts.nbaScoresEndpoint).openStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response));
                stringBuilder = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line+"n");
                }
                response.close();
                return new JSONObject(stringBuilder.toString());
            } catch (JSONException e){
                e.printStackTrace();
                Log.d("dIBJSONExcep", e.getMessage());
            } catch (IOException e){
                e.printStackTrace();
                Log.d("dIBIOExcep", e.getMessage());
            }
            return null;
        }
    }
}
