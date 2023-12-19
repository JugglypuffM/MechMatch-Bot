package database.entities;

import bots.platforms.Platform;
import jakarta.persistence.*;
import logic.states.GlobalState;
import logic.states.LocalState;
import logic.states.StateFSM;

/**
 * Contains all data required for user account identification.
 * Has automatically generated internal id.
 */
@Entity
@Table(name = "accounts", schema = "public")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String login, passhash, tgid, tgusername, dsid, dsusername;
    private String globalState;
    private String localState;
    private Integer suggestedFriendID;
    private String profilesList;
    private Integer profilesPage;
    public Account() {}
    public Account(String login){
        this.login = login;
        this.globalState = "COMMAND";
        this.localState = "START";
    }
    public Account(Integer id, String login){
        this.id = id;
        this.login = login;
        this.globalState = "COMMAND";
        this.localState = "START";
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
    public GlobalState getGlobalState(){
        StateFSM stateFSM = new StateFSM();
        return stateFSM.getGlobalStateMap().get(this.globalState);
    }
    public void setGlobalState(GlobalState m_globalState){
        this.globalState = m_globalState.stringRepresentation();
    }
    public LocalState getLocalState(){
        StateFSM stateFSM = new StateFSM();
        return stateFSM.getLocalStateMap().get(this.localState);
    }
    public void setLocalState(LocalState m_localState){
        this.localState = m_localState.stringRepresentation();
    }
    public Integer getSuggestedFriendID() {
        return suggestedFriendID;
    }
    public void setSuggestedFriendID(Integer suggestedFriend) {
        this.suggestedFriendID = suggestedFriend;
    }
    public String getProfilesList() {
        return profilesList;
    }
    public void setProfilesList(String profilesList) {
        this.profilesList = profilesList;
    }
    public int getProfilesPage() {
        return profilesPage;
    }
    public void setProfilesPage(Integer profilesPage) {
        this.profilesPage = profilesPage;
    }

    /**
     * Setter for every platform id
     * @param platformId user id in certain platform
     * @param platform platform, with which given id will be associated
     */
    public void setPlatformId(String platformId, Platform platform){
        switch (platform){
            case TELEGRAM -> tgid = platformId;
            case DISCORD -> dsid = platformId;
        }
    }
    /**
     * Setter for every platform username
     * @param username username in certain platform
     * @param platform platform, with which given username will be associated
     */
    public void setPlatformUsername(String username, Platform platform){
        switch (platform){
            case TELEGRAM -> tgusername = username;
            case DISCORD -> dsusername = username;
        }
    }
    /**
     * Getter for every platform username
     * @param platform platform, with which given username is associated
     * @return username in certain platform
     */
    public String getPlatformUsername(Platform platform){
        switch (platform){
            case TELEGRAM -> {return tgusername;}
            case DISCORD -> {return dsusername;}
        }
        return null;
    }
    /**
     * Getter for every platform id
     * @param platform platform, with which given id is associated
     * @return user id in certain platform
     */
    public String getPlatformId(Platform platform){
        switch (platform){
            case TELEGRAM -> {return tgid;}
            case DISCORD -> {return dsid;}
        }
        return null;
    }
}
