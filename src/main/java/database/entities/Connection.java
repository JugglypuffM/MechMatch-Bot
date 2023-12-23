package database.entities;

import jakarta.persistence.*;

/**
 * Contains all data of relations between users.
 * Has automatically generated id.
 */
@Entity
@Table(name = "matches", schema = "public")
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userID, friendID;
    private Boolean isLiked;
    private boolean deleted;
    public Connection(){}
    public Connection(Integer id, Integer userID, Integer friendID, Boolean isLiked) {
        this.id = id;
        this.userID = userID;
        this.friendID = friendID;
        this.isLiked = isLiked;
        this.deleted = false;
    }
    public Connection(Integer userID, Integer friendID, Boolean isLiked) {
        this.userID = userID;
        this.friendID = friendID;
        this.isLiked = isLiked;
        this.deleted = false;
    }

    public int getId() {
        return id;
    }
    public Integer getUserID() {
        return userID;
    }
    public Integer getFriendID() {
        return friendID;
    }
    public Boolean getLiked() {
        return isLiked;
    }
    public void setIsLiked(Boolean m_isLiked) {
        this.isLiked = m_isLiked;
    }
    public Boolean getDeleted() {
        return deleted;
    }
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
