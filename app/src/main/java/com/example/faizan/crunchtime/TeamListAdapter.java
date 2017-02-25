package com.example.faizan.crunchtime;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

class TeamListAdapter extends ArrayAdapter<Team> {

    Context context;

    public TeamListAdapter(Context context, List<Team> teams) {
        super(context, R.layout.team_row ,teams);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater teamInflator = LayoutInflater.from(getContext());
        View teamView = teamInflator.inflate(R.layout.team_row, parent, false);

        Team aTeam = getItem(position);
        TextView teamName = (TextView) teamView.findViewById(R.id.teamName);
        TextView timeLeft = (TextView) teamView.findViewById(R.id.timeLeft);
        TextView period = (TextView) teamView.findViewById(R.id.period);
        TextView scoreDiff = (TextView) teamView.findViewById(R.id.scoreDiff);
        RelativeLayout teamBGLayout = (RelativeLayout) teamView.findViewById(R.id.teamBackgroundLayout);

        teamName.setText(aTeam.get_teamName());
        teamName.setTypeface(null, Typeface.BOLD);

        int minsLeft = aTeam.get_timeRemaining() / 60;
        int secsLeft = aTeam.get_timeRemaining() % 60;
        String formattedMinsLeft = String.format("%02d", minsLeft);
        String formattedSecsLeft = String.format("%02d", secsLeft);
        timeLeft.setText(formattedMinsLeft + ":" + formattedSecsLeft);

        if(aTeam.get_period() == 1) period.setText("1st quarter");
        else if(aTeam.get_period() == 2) period.setText("2nd quarter");
        else if(aTeam.get_period() == 3) period.setText("3rd quarter");
        else if(aTeam.get_period() == 4) period.setText("4th quarter");

        scoreDiff.setText(Integer.toString(aTeam.get_scoreDiff()) + " pts");

        int colorResId = teamView.getResources().getColor(teamView.getResources().getIdentifier(aTeam.get_teamAcro(), "color", context.getPackageName()));
        Log.d("TLAgV", "colorRedId = " + Integer.toString(colorResId, 16));
        teamBGLayout.setBackgroundColor(colorResId);

        teamName.setTextColor(context.getResources().getColor(R.color.offWhite));
        timeLeft.setTextColor(context.getResources().getColor(R.color.offWhite));
        period.setTextColor(context.getResources().getColor(R.color.offWhite));
        scoreDiff.setTextColor(context.getResources().getColor(R.color.offWhite));


        return teamView;
    }
}
