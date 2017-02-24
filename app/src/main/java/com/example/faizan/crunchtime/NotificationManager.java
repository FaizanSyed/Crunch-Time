package com.example.faizan.crunchtime;


import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationManager implements Runnable {

    Context context;

    public NotificationManager(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        JSONArray closeNBAGames = new JSONArray();
        Integer secondsToSleep = null;
        TeamDBManager teamDBManager = new TeamDBManager(context);
        while(true){
            closeNBAGames = NBAGames.getInterCloseGames(context);
            int numOfCloseGames = closeNBAGames.length();

            // If there are no more possible close games today,
            // wait until tomorrow at 9am EST (When endpoint is updated)
            if(numOfCloseGames == 0){
                teamDBManager.clearSentNotif();

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                String currTime = df.format(c.getTime());
                int currESTHour = Integer.parseInt(currTime.substring(0, currTime.indexOf(':')));
                int currESTMin = Integer.parseInt(currTime.substring(currTime.indexOf(':')+1, currTime.indexOf(':')+3));

                int hoursUntil9am;
                int minsUntil9am;
                int secsUntil9am;

                if(currESTHour < 9){
                    hoursUntil9am = 9 - currESTHour;
                } else {
                    hoursUntil9am = ((24-currESTHour)+9);
                }
                minsUntil9am = (hoursUntil9am*60)-currESTMin;
                secsUntil9am = minsUntil9am*60;

                secondsToSleep = secsUntil9am;
            }

            // Loop through all trimmed interesting games and look for ones to send a notif for
            // If it is not ready for a notif set the time to sleep for the lowest time
            for(int i = 0; i < numOfCloseGames; i++){
                try {
                    JSONObject aCloseGame = closeNBAGames.getJSONObject(i);

                    if (aCloseGame.getInt("ni") == 0) {


                        //Send Notification Here


                    } else if (secondsToSleep == null || aCloseGame.getInt("ni") < secondsToSleep) {
                        secondsToSleep = aCloseGame.getInt("ni");
                    }
                } catch (JSONException e){
                    Log.d("NotifManrun", e.getMessage());
                }
            }
            try {
                Thread.sleep(secondsToSleep * 1000);
            } catch (InterruptedException e){
                Log.d("NotifManrun", e.getMessage());
            }
        }
    }
}
