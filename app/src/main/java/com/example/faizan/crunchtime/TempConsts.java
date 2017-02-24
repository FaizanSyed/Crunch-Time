package com.example.faizan.crunchtime;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class TempConsts {
    private static JSONObject jObj;
    private static String string1 = "{\"gs\":{\"mid\":1487557521769,\"gdte\":\"2017-02-19\",\"next\":\"http://data.nba.com/data/v2015/json/mobile_teams/nba/2016/scores/00_todays_scores.json\",\"g\":[{\"gid\":\"0031600001\",\"gcode\":\"20170219/ESTWST\",\"p\":2,\"st\":2,\"stt\":\"Halftime\",\"cl\":\"00:00.0\",\"seq\":1,\"lm\":{\"gdte\":\"\",\"gres\":\"\",\"seri\":\"\",\"gid\":\"\"},\"v\":{\"ta\":\"EST\",\"q1\":53,\"s\":92,\"q2\":39,\"q3\":0,\"q4\":0,\"ot1\":0,\"ot2\":0,\"ot3\":0,\"ot4\":0,\"ot5\":0,\"ot6\":0,\"ot7\":0,\"ot8\":0,\"ot9\":0,\"ot10\":0,\"tn\":\"East\",\"tc\":\"East NBA All Stars\",\"tid\":1610616833},\"h\":{\"ta\":\"WST\",\"q1\":48,\"s\":97,\"q2\":49,\"q3\":0,\"q4\":0,\"ot1\":0,\"ot2\":0,\"ot3\":0,\"ot4\":0,\"ot5\":0,\"ot6\":0,\"ot7\":0,\"ot8\":0,\"ot9\":0,\"ot10\":0,\"tn\":\"West\",\"tc\":\"West NBA All Stars\",\"tid\":1610616834}}]}}";
    private static String string2 = "{\"gs\":{\"mid\":1487562203579,\"gdte\":\"2017-02-19\",\"next\":\"http://data.nba.com/data/v2015/json/mobile_teams/nba/2016/scores/00_todays_scores.json\",\"g\":[{\"gid\":\"0031600001\",\"gcode\":\"20170219/ESTWST\",\"p\":4,\"st\":2,\"stt\":\"4th Qtr\",\"cl\":\"01:15\",\"seq\":1,\"lm\":{\"gdte\":\"\",\"gres\":\"\",\"seri\":\"\",\"gid\":\"\"},\"v\":{\"ta\":\"EST\",\"q1\":53,\"s\":180,\"q2\":39,\"q3\":47,\"q4\":41,\"ot1\":0,\"ot2\":0,\"ot3\":0,\"ot4\":0,\"ot5\":0,\"ot6\":0,\"ot7\":0,\"ot8\":0,\"ot9\":0,\"ot10\":0,\"tn\":\"East\",\"tc\":\"East NBA All Stars\",\"tid\":1610616833},\"h\":{\"ta\":\"WST\",\"q1\":48,\"s\":190,\"q2\":49,\"q3\":47,\"q4\":46,\"ot1\":0,\"ot2\":0,\"ot3\":0,\"ot4\":0,\"ot5\":0,\"ot6\":0,\"ot7\":0,\"ot8\":0,\"ot9\":0,\"ot10\":0,\"tn\":\"West\",\"tc\":\"West NBA All Stars\",\"tid\":1610616834}}]}}";

    public static JSONObject getjObj() {
        try{
            jObj = new JSONObject(string2);
        }catch (JSONException e){
            Log.d("JSONExep", e.getMessage());
        }
        return jObj;
    }
}
