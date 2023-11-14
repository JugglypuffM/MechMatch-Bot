package database;

import jakarta.persistence.*;

@Entity
@Table(name = "matches", schema = "public")
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userID, friendID;
    public Connection(){}
    public Connection(String userID, String friendID) {
        this.userID = userID;
        this.friendID = friendID;
    }

    public int getId() {
        return id;
    }
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getFriendID() {
        return friendID;
    }
    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }
}
