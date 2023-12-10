package database.models;

import bots.platforms.Platform;
import jakarta.persistence.*;

@Entity
@Table(name = "auth", schema = "public")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String login, passhash, tgid, dsid;
    public Account() {
    }
    public Account(String login){
        this.login = login;
    }

    public Integer getId() {
        return id;
    }
    public String getLogin() {
        return login;
    }
    public String getPasshash() {
        return passhash;
    }
    public void setPasshash(String passhash) {
        this.passhash = passhash;
    }
    public String getTgid() {
        return tgid;
    }
    public void setTgid(String tgid) {
        this.tgid = tgid;
    }
    public String getDsid() {
        return dsid;
    }
    public void setDsid(String dsid) {
        this.dsid = dsid;
    }
}
