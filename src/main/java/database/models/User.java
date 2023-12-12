package database.models;

import bots.platforms.Platform;
import bots.platforms.PlatformFSM;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import logic.states.GlobalState;
import logic.states.LocalState;
import logic.states.StateFSM;


/**
 * Main user class.
 * Contains all data fields required for matching.
 * {@link User#id} is unique for every user.
 */
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    private Integer id;
    private String username;
    private String globalState;
    private String localState;
    private Integer suggestedFriendID;
    private String profilesList;
    private Integer profilesPage;
    private String platform;
    public User(){}
    public User(Integer m_id, String m_username, String m_platform){
        this.id = m_id;
        this.username = m_username;
        this.platform = m_platform;
        this.globalState = "COMMAND";
        this.localState = "START";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    public Integer getId(){
        return id;
    }
    public String getUsername() {
        return username;
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
    public void setPlatform(Platform platform) {
        this.platform = platform.stringRepresentation();
    }
    public Platform getPlatform() {
        PlatformFSM platformFSM = new PlatformFSM();
        return platformFSM.getPlatformMap().get(this.platform);
    }
}