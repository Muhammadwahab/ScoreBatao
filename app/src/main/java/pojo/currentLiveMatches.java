package pojo;

/**
 * Created by abdull on 3/23/17.
 */

public class currentLiveMatches {
    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTeamOne() {
        return teamOne;
    }

    public void setTeamOne(String teamOne) {
        this.teamOne = teamOne;
    }

    public String getTeamTwo() {
        return teamTwo;
    }

    public void setTeamTwo(String teamTwo) {
        this.teamTwo = teamTwo;
    }

    public long getUnique_ID() {
        return unique_ID;
    }

    public void setUnique_ID(long unique_ID) {
        this.unique_ID = unique_ID;
    }

    private String name,Date,teamOne,teamTwo;
    boolean MatchStart;

    public boolean isMatchStart() {
        return MatchStart;
    }

    public void setMatchStart(boolean matchStart) {
        MatchStart = matchStart;
    }

    private long unique_ID;

    public currentLiveMatches() {
        this.name = "wahab";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
