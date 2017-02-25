package com.example.faizan.crunchtime;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.SystemUpdatePolicy;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationIssuer implements Runnable {

    Context context;
    Notification.Builder notification;

    public NotificationIssuer(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Log.d("NIrun", "Top of run");
        JSONArray closeNBAGames = new JSONArray();
        Integer secondsToSleep = null;
        TeamDBManager teamDBManager = new TeamDBManager(context);
        while(true){
            Log.d("NIrunW", "Top of While");

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

                        issueNotification(aCloseGame);

                    } else if (secondsToSleep == null || aCloseGame.getInt("ni") < secondsToSleep) {
                        secondsToSleep = aCloseGame.getInt("ni");
                    }
                } catch (JSONException e){
                    Log.d("NotifManrun", e.getMessage());
                }
            }
            try {
                Log.d("NIRunW", "Current Thread: " + Thread.currentThread().getName());
                Log.d("NIRunW", "secondsToSleep = " + secondsToSleep);
                if(secondsToSleep == null) secondsToSleep = 0;
                Thread.sleep(secondsToSleep * 1000);
            } catch (InterruptedException e){
                Log.d("NotifManrun", e.getMessage());
            }
        }
    }

    private void issueNotification(JSONObject aGame){
        notification = new Notification.Builder(context);
        notification.setSmallIcon(R.drawable.bballicon);
        notification.setAutoCancel(true);
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Crunch Time!");

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.setSound(alarmSound);

        String period = null;
        try {
            if(aGame.getInt("p") == 1) period = "first";
            else if(aGame.getInt("p") == 2) period = "second";
            else if(aGame.getInt("p") == 3) period = "third";
            else if(aGame.getInt("p") == 4) period = "forth";

            int scoreDiff = aGame.getInt("sd");
            if (scoreDiff == 0) {
                notification.setContentText(aGame.getString("vta") + " and " + aGame.getString("hta") +
                        " are tied up with " + aGame.getString("cl") + " left in the " + period + " quarter!");
            } else if (scoreDiff > 0) {
                notification.setContentText(aGame.getString("vta") + " is beating " + aGame.getString("hta") +
                        " by " + scoreDiff + " with " + aGame.getString("cl") + " left in the " + period + " quarter!");
            } else {
                scoreDiff = -scoreDiff;
                notification.setContentText(aGame.getString("hta") + " is beating " + aGame.getString("vta") +
                        " by " + scoreDiff + " with " + aGame.getString("cl") + " left in the " + period + " quarter!");
            }
        } catch(JSONException e){
            Log.d("iNJSONExep", e.getMessage());
        }

        Intent toNotif = new Intent(context, NotificationIssuer.class);
        PendingIntent pendingToNotif = PendingIntent.getActivity(context, 0, toNotif, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingToNotif);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, notification.build());
    }
}
