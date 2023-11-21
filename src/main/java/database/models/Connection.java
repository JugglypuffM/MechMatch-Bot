package database.models;

import jakarta.persistence.*;

@Entity
@Table(name = "matches", schema = "public")
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userID, friendID;
    private Boolean isLiked;
    public Connection(){}
    public Connection(Integer id, String userID, String friendID, Boolean isLiked) {
        this.id = id;
        this.userID = userID;
        this.friendID = friendID;
        this.isLiked = isLiked;
    }
    public Connection(String userID, String friendID, Boolean isLiked) {
        this.userID = userID;
        this.friendID = friendID;
        this.isLiked = isLiked;
    }

    public int getId() {
        return id;
    }
    public String getUserID() {
        return userID;
    }
    public String getFriendID() {
        return friendID;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean m_isLiked) {
        this.isLiked = m_isLiked;
    }
}
