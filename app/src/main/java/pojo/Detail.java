package pojo;

/**
 * Created by abdull on 6/29/17.
 */

public class Detail {
    String name,update,status,request;

    public Detail(String name, String update, String status, String request) {
        this.name = name;
        this.update = update;
        this.status = status;
        this.request = request;
    }
    public Detail(String name, String update, String status) {
        this.name = name;
        this.update = update;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
