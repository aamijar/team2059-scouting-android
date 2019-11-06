/**
 * Implemented in Master Menu
 * Match objects are created for every
 * file read from app
 *
 * @author Anupam
 */

package com.team2059.scouting;

public class Match
{
    private String teamName;
    private int matchNumber;
    private int pointsScored;
    private double rankPoints;
    private boolean matchResult;


    public Match(String name, int number, int points, double rankpts, boolean result)
    {
        teamName = name;
        matchNumber = number;
        pointsScored = points;
        rankPoints = rankpts;
        matchResult = result;
    }

    public String getTeamName()
    {
        return teamName;
    }
    public int getMatchNumber()
    {
        return matchNumber;
    }
    public int getMatchPoints()
    {
        return pointsScored;
    }
    public double getRankPoints()
    {
        return rankPoints;
    }

    public boolean getMatchResult()
    {
        return matchResult;
    }
}
