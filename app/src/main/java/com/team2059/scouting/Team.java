/**
 * implemented in Master Menu
 * one team object per team
 * class methods are used to display
 * attributes of team in
 * a table layout
 *
 * @author Anupam
 */

package com.team2059.scouting;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Team implements Parcelable {

    private String teamNumber;
    private String teamName;
    private String byteMapArr;

    Team(String name, String number, String byteMapArr){
        teamName = name;
        teamNumber = number;
        this.byteMapArr = byteMapArr;
    }


    protected Team(Parcel in) {
        teamNumber = in.readString();
        teamName = in.readString();
        byteMapArr = in.readString();
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    public String getTeamNumber()
    {
        return teamNumber;
    }
    public String getTeamName()
    {
        return teamName;
    }
    public String getByteMapArr(){return byteMapArr;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(teamNumber);
        dest.writeString(teamName);
        dest.writeString(byteMapArr);
    }
}
