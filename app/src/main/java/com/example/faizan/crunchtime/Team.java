package com.example.faizan.crunchtime;



public class Team {

    private int _id;
    private String _teamAcro;
    private int _period;
    private int _timeRemaining;
    private int _scoreDiff;
    private String _teamName;
    private int _sentNotif;

    public Team(){}

    public Team(String _teamAcro, String _teamName, int _period, int _timeRemaining, int _scoreDiff, int _sentNotif) {
        this._period = _period;
        this._scoreDiff = _scoreDiff;
        this._teamAcro = _teamAcro;
        this._timeRemaining = _timeRemaining;
        this._teamName = _teamName;
        this._sentNotif = _sentNotif;
    }

    public int get_id() {
        return _id;
    }

    public int get_period() {
        return _period;
    }

    public void set_period(int _period) {
        this._period = _period;
    }

    public int get_scoreDiff() {
        return _scoreDiff;
    }

    public void set_scoreDiff(int _scoreDiff) {
        this._scoreDiff = _scoreDiff;
    }

    public String get_teamAcro() {
        return _teamAcro;
    }

    public void set_teamAcro(String _teamAcro) {
        this._teamAcro = _teamAcro;
    }

    public int get_timeRemaining() {
        return _timeRemaining;
    }

    public void set_timeRemaining(int _timeRemaining) {
        this._timeRemaining = _timeRemaining;
    }

    public String get_teamName() {
        return _teamName;
    }

    public void set_teamName(String _teamName) {
        this._teamName = _teamName;
    }

    public int get_sentNotif() {
        return _sentNotif;
    }

    public void set_sentNotif(int _sentNotif) {
        this._sentNotif = _sentNotif;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
