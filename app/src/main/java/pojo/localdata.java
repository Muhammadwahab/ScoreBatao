package pojo;

/**
 * Created by abdull on 6/30/17.
 */

public class localdata {

    String matchID,status,phonenumber,email,request,matchStatus,update,name;

    public localdata(String matchID, String status, String phonenumber, String email, String request, String matchStatus, String update, String name) {
        this.matchID = matchID;
        this.status = status;
        this.phonenumber = phonenumber;
        this.email = email;
        this.request = request;
        this.matchStatus = matchStatus;
        this.update = update;
        this.name = name;
    }

    public localdata() {
    }

    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
