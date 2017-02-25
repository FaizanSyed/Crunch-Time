package com.example.faizan.crunchtime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TeamDBManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "interTeams.db";
    public static final String TABLE_INTERTEAMS = "interTeams";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TEAMACRO = "teamAcro";
    public static final String COLUMN_PERIOD = "period";
    public static final String COLUMN_TEAMNAME = "teamName";
    public static final String COLUMN_TIMEREMAINING = "timeReamaining";
    public static final String COLUMN_SCOREDIFF = "scoreDiff";
    public static final String COLUMN_SENTNOTIF = "sentNotif";


    public TeamDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String firstQuery = "CREATE TABLE " + TABLE_INTERTEAMS + "( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TEAMACRO + " TEXT," +
                COLUMN_TEAMNAME + " TEXT," +
                COLUMN_PERIOD + " INTEGER," +
                COLUMN_TIMEREMAINING + " INTEGER," +
                COLUMN_SCOREDIFF + " INTEGER," +
                COLUMN_SENTNOTIF + " INTEGER " +
                ");";
        db.execSQL(firstQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERTEAMS);
        onCreate(db);
    }

    public void addTeam(Team team){
        if(exists(team.get_teamAcro())){
            deleteTeam(team.get_teamAcro());
        }
        ContentValues teamInfo = new ContentValues();
        teamInfo.put(COLUMN_TEAMACRO, team.get_teamAcro());
        teamInfo.put(COLUMN_TEAMNAME, team.get_teamName());
        teamInfo.put(COLUMN_PERIOD, team.get_period());
        teamInfo.put(COLUMN_TIMEREMAINING, team.get_timeRemaining());
        teamInfo.put(COLUMN_SCOREDIFF, team.get_scoreDiff());
        teamInfo.put(COLUMN_SENTNOTIF, team.get_sentNotif());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_INTERTEAMS, null, teamInfo);
        db.close();
    }

    public void deleteTeam(String teamAcro){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_INTERTEAMS + " WHERE " + COLUMN_TEAMACRO + "=\"" + teamAcro + "\";");
        db.close();
    }

    public boolean exists(String teamAcro){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_INTERTEAMS + " WHERE " + COLUMN_TEAMACRO + "=\"" + teamAcro + "\";";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public String[] teamNamesToArray(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INTERTEAMS, null);
        String[] teamNames = new String[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()) {
            String teamName = cursor.getString(cursor.getColumnIndex(COLUMN_TEAMNAME));
            teamNames[i]=teamName;
            i++;
        }
        cursor.close();
        db.close();
        return teamNames;
    }

    public String[] teamAcrosToArray(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INTERTEAMS, null);
        String[] teamAcros = new String[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()) {
            String teamAcro = cursor.getString(cursor.getColumnIndex(COLUMN_TEAMACRO));
            teamAcros[i]=teamAcro;
            i++;
        }
        cursor.close();
        db.close();
        return teamAcros;
    }

    public int getSentNotif(String teamAcro){
        if(!exists(teamAcro)) return 0;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_INTERTEAMS + " WHERE " + COLUMN_TEAMACRO + "=\"" + teamAcro + "\";";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int sentNotif = cursor.getInt(cursor.getColumnIndex(COLUMN_SENTNOTIF));
        cursor.close();
        return sentNotif;
    }

    public void setSentNotif(String teamAcro){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SENTNOTIF, 1);
        String[] whereArgs = {teamAcro};
        db.update(TABLE_INTERTEAMS, contentValues, COLUMN_TEAMACRO+" =? ", whereArgs);
    }

    public void clearSentNotif() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SENTNOTIF, 0);
        db.update(TABLE_INTERTEAMS, contentValues, null, null);
    }

    public int getScoreDiff(String teamAcro){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_INTERTEAMS + " WHERE " + COLUMN_TEAMACRO + "=\"" + teamAcro + "\";";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int scoreDiff = cursor.getInt(cursor.getColumnIndex(COLUMN_SCOREDIFF));
        cursor.close();
        return scoreDiff;
    }

    public int getSecRemaining(String teamAcro){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_INTERTEAMS + " WHERE " + COLUMN_TEAMACRO + "=\"" + teamAcro + "\";";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int periodsRemaining = (NBAConsts.periodsInGame.length) - (cursor.getInt(cursor.getColumnIndex(COLUMN_PERIOD)));
        int secsRemainingInPeriod = cursor.getInt(cursor.getColumnIndex(COLUMN_TIMEREMAINING));
        return (periodsRemaining*12*60)+secsRemainingInPeriod;
    }

    public List<Team> getAllContacts() {
        List<Team> teamList = new ArrayList<Team>();
        String query = "SELECT  * FROM " + TABLE_INTERTEAMS;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Team team = new Team();
                team.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID))));
                team.set_teamAcro(cursor.getString(cursor.getColumnIndex(COLUMN_TEAMACRO)));
                team.set_teamName(cursor.getString(cursor.getColumnIndex(COLUMN_TEAMNAME)));
                team.set_period(cursor.getInt(cursor.getColumnIndex(COLUMN_PERIOD)));
                team.set_timeRemaining(cursor.getInt(cursor.getColumnIndex(COLUMN_TIMEREMAINING)));
                team.set_scoreDiff(cursor.getInt(cursor.getColumnIndex(COLUMN_SCOREDIFF)));
                team.set_sentNotif(cursor.getInt(cursor.getColumnIndex(COLUMN_SENTNOTIF)));

                teamList.add(team);
            } while (cursor.moveToNext());
        }

        return teamList;
    }

    public String TeamDBtoString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_INTERTEAMS + " WHERE 1";

        Log.d("TeamDBtoS", "Called");

        //Cursor point to a location in your result
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your result
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_TEAMACRO)) != null){
                dbString += c.getString(c.getColumnIndex(COLUMN_TEAMACRO));
                dbString += " ";
            }
            if(c.getString(c.getColumnIndex(COLUMN_PERIOD)) != null){
                dbString += c.getString(c.getColumnIndex(COLUMN_PERIOD));
                dbString += " ";
            }
            if(c.getString(c.getColumnIndex(COLUMN_TIMEREMAINING)) != null){
                dbString += c.getString(c.getColumnIndex(COLUMN_TIMEREMAINING));
                dbString += " ";
            }
            if(c.getString(c.getColumnIndex(COLUMN_SCOREDIFF)) != null){
                dbString += c.getString(c.getColumnIndex(COLUMN_SCOREDIFF));
                dbString += " ";
            }
            if(c.getString(c.getColumnIndex(COLUMN_TEAMNAME)) != null){
                dbString += c.getString(c.getColumnIndex(COLUMN_TEAMNAME));
                dbString += " ";
            }
            if(c.getString(c.getColumnIndex(COLUMN_SENTNOTIF)) != null){
                dbString += c.getString(c.getColumnIndex(COLUMN_SENTNOTIF));
                dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();;
        return dbString;
    }
}
