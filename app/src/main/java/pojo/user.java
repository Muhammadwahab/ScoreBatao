package pojo;

/**
 * Created by abdull on 6/1/17.
 */

public class user {

    public String name;
    public String email;
    public String id;
    public String Numbers;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public user() {
    }

    public user(String email, String ID) {
        this.email = email;
        this.id=ID;

    }
}