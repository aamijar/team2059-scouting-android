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
    private int teamPoints;
    private int alliancePoints;
    private double rankPoints;
    private boolean matchResult;


    public Match(String name, int number, int points, int allpoints, double rankpts, boolean result)
    {
        teamName = name;
        matchNumber = number;
        teamPoints = points;
        alliancePoints = allpoints;
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
    public int getTeamPoints()
    {
        return teamPoints;
    }
    public int getAlliancePoints()
    {
        return alliancePoints;
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
