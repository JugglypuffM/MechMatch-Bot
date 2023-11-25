package database.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import mainBot.states.GlobalState;
import mainBot.states.LocalState;
import mainBot.states.StateFSM;

/**
 * Main user class.
 * Contains all data fields required for matching.
 * {@link User#id} is unique for every user.
 * {@link User#sex} and {@link User#expectedSex} are boolean, where false is male and true is female.
 */
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    private String id;
    private String username;
    private String name, city, expectedCity, sex, expectedSex, information, photoID;
    private String globalState;
    private String localState;
    private int age, minExpectedAge, maxExpectedAge;
    private boolean profileFilled;
    private String suggestedFriendID;
    private String profilesList;
    private Integer profilesPage;
    public User(){}
    public User(String m_id, String m_username){
        this.id = m_id;
        this.username = m_username;
        this.globalState = "COMMAND";
        this.localState = "START";
        this.minExpectedAge = 0;
        this.maxExpectedAge = 999;
        this.profileFilled = false;
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
    public String getId(){
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getName() {
        return name;
    }
    public void setName(String m_name) {
        this.name = m_name;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String m_city) {
        this.city = m_city;
    }
    public String getExpectedCity() {
        return expectedCity;
    }
    public void setExpectedCity(String m_expectedCity) {
        this.expectedCity = m_expectedCity;
    }
    public String getSex() {
        return sex;
    }
    /**
     * Checks if given string is correct and fills {@link User#sex}.
     * @param m_sex string, which should be equal to "Парень" if sex is male or to "Девушка" if sex female.
     * @return true if {@link User#sex} filled successfully and false if not.
     */
    public boolean setSex(String m_sex) {
        if (m_sex.equalsIgnoreCase("парень") ||
                m_sex.equalsIgnoreCase("девушка")){
            this.sex = m_sex.toLowerCase();
            return true;
        }
        return false;
    }
    public String getExpectedSex() {
        return expectedSex;
    }
    /**
     * Checks if given string is correct and fills {@link User#expectedSex}.
     * @param m_expectedSex string, which should be equal to "Парень" if sex is male or to "Девушка" if sex female.
     * @return true if {@link User#expectedSex} filled successfully and false if not.
     */
    public boolean setExpectedSex(String m_expectedSex) {
        if (m_expectedSex.equalsIgnoreCase("парень") ||
                m_expectedSex.equalsIgnoreCase("девушка") ||
                m_expectedSex.equalsIgnoreCase("без разницы")){
            this.expectedSex = m_expectedSex.toLowerCase();
            return true;
        }
        return false;
    }
    public int getAge(){
        return age;
    }
    /**
     * Checks if given integer is correct and fills {@link User#age}.
     * @param m_age integer, which should be between 14 and 120.
     * @return true if {@link User#age} filled successfully and false if not.
     */
    public boolean setAge(String  m_age) {
        int n_age;
        try {
            n_age = Integer.parseInt(m_age);
        }catch (NumberFormatException e){
            return false;
        }
        if ((n_age >= 14) && (n_age <= 120)){
            this.age = n_age;
            return true;
        }
        return false;
    }
    public int getMinExpectedAge(){
        return minExpectedAge;
    }
    /**
     * Checks if given integer is correct and fills {@link User#minExpectedAge}.
     * @param m_minExpectedAge integer, which should be between 14 and 120.
     * @return true if {@link User#minExpectedAge} filled successfully and false if not.
     */
    public boolean setMinExpectedAge(String  m_minExpectedAge) {
        int n_age;
        try {
            n_age = Integer.parseInt(m_minExpectedAge);
        }catch (NumberFormatException e){
            return false;
        }
        if ((n_age >= 14) && (n_age <= 120)){
            this. minExpectedAge = n_age;
            return true;
        }
        return false;
    }
    public int getMaxExpectedAge(){
        return maxExpectedAge;
    }
    /**
     * Checks if given integer is correct and fills {@link User#maxExpectedAge}.
     * @param m_maxExpectedAge integer, which should be between 14 and 120.
     * @return true if {@link User#maxExpectedAge} filled successfully and false if not.
     */
    public boolean setMaxExpectedAge(String  m_maxExpectedAge) {
        int n_age;
        try {
            n_age = Integer.parseInt(m_maxExpectedAge);
        }catch (NumberFormatException e){
            return false;
        }
        if ((n_age >= 14) && (n_age <= 120)){
            this. maxExpectedAge = n_age;
            return true;
        }
        return false;
    }
    public GlobalState getGlobalState(){
        StateFSM util = new StateFSM();
        return util.getGlobalStateMap().get(this.globalState);
    }
    public void setGlobalState(GlobalState m_globalState){
        this.globalState = m_globalState.toString();
    }
    public LocalState getLocalState(){
        StateFSM util = new StateFSM();
        return util.getLocalStateMap().get(this.localState);
    }
    public void setLocalState(LocalState m_localState){
        this.localState = m_localState.toString();
    }
    public String getInformation(){
        return information;
    }
    public void setInformation(String m_information){
        this.information = m_information;
    }
    public String getPhotoID(){
        return photoID;
    }
    public void setPhotoID(String m_pictureID){
        this.photoID = m_pictureID;
    }
    public boolean isProfileFilled() {
        return profileFilled;
    }
    public void setProfileFilled(boolean profileFilled) {
        this.profileFilled = profileFilled;
    }
    public String getSuggestedFriendID() {
        return suggestedFriendID;
    }
    public void setSuggestedFriendID(String suggestedFriend) {
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
     * Method to unify field filling.
     * Uses different setters depending on current {@link User#localState}.
     * @param value user's message
     * @return true if field was filled successfully and false if not
     */
    public boolean setField(String value){
        switch (this.getLocalState()){
            case NAME:
                this.setName(value);
                return true;
            case AGE:
                return this.setAge(value);
            case SEX:
                return this.setSex(value);
            case CITY:
                this.setCity(value);
                return true;
            case ABOUT:
                this.setInformation(value);
                return true;
            case EAGEMIN:
                return this.setMinExpectedAge(value);
            case EAGEMAX:
                return this.setMaxExpectedAge(value);
            case ESEX:
                return this.setExpectedSex(value);
            case ECITY:
                this.setExpectedCity(value);
                return true;
            case PHOTO:
                return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", expectedCity='" + expectedCity + '\'' +
                ", sex='" + sex + '\'' +
                ", expectedSex='" + expectedSex + '\'' +
                ", information='" + information + '\'' +
                ", photoID='" + photoID + '\'' +
                ", globalState=" + globalState +
                ", localState=" + localState +
                ", age=" + age +
                ", minExpectedAge=" + minExpectedAge +
                ", maxExpectedAge=" + maxExpectedAge +
                '}';
    }
}