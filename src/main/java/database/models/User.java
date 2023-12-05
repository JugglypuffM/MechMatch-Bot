package database.models;

import bots.platforms.Platform;
import bots.platforms.PlatformFSM;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import logic.states.GlobalState;
import logic.states.LocalState;
import logic.states.StateFSM;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
    private String platform;
    public User(){}
    public User(String m_id, String m_username, String m_platform){
        this.id = m_id;
        this.username = m_username;
        this.platform = m_platform;
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
    public Boolean setSex(String m_sex) {
        if (m_sex.equalsIgnoreCase("парень") ||
                m_sex.equalsIgnoreCase("девушка")){
            this.sex = m_sex.toLowerCase();
            if (!validateSex(name, sex)){
                return null;
            }
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
    public Boolean setExpectedSex(String m_expectedSex) {
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
        if ((n_age >= 14) && (n_age <= 120) && (n_age <= maxExpectedAge)){
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
        if ((n_age >= 14) && (n_age <= 120) && (n_age >= minExpectedAge)){
            this. maxExpectedAge = n_age;
            return true;
        }
        return false;
    }
    public GlobalState getGlobalState(){
        StateFSM stateFSM = new StateFSM();
        return stateFSM.getGlobalStateMap().get(this.globalState);
    }
    public void setGlobalState(GlobalState m_globalState){
        this.globalState = m_globalState.toString();
    }
    public LocalState getLocalState(){
        StateFSM stateFSM = new StateFSM();
        return stateFSM.getLocalStateMap().get(this.localState);
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
    public void setPlatform(Platform platform) {
        this.platform = platform.toString();
    }
    public Platform getPlatform() {
        PlatformFSM platformFSM = new PlatformFSM();
        return platformFSM.getPlatformMap().get(this.platform);
    }


    /**
     * Method to unify field filling.
     * Uses different setters depending on current {@link User#localState}.
     * @param value user's message
     * @return true if field was filled successfully and false if not
     */
    public Boolean setField(String value){
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

    public boolean validateSex(String name, String sex){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/fio");
        httppost.setHeader("Content-Type", "application/json");
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Authorization", "Token 992551514f0c3981d2d3fff1201d86b093612eeb");
        httppost.setEntity(new StringEntity("{ \"query\": \"" + name + "\",\n" +
                "\"parts\": [\"NAME\"] }", StandardCharsets.UTF_8));
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpEntity entity = response.getEntity();
        String result = "none";
        if (entity != null) {
            try (InputStream inputStream = entity.getContent()) {
                result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        JSONObject json = null;
        try {
            json = (JSONObject) JSONValue.parseWithException(result);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        JSONArray array = (JSONArray) json.get("suggestions");
        Map<String, String> genderDict = new HashMap<>();
        genderDict.put("MALE", "девушка");
        genderDict.put("FEMALE", "парень");
        genderDict.put("UNKNOWN", "");
        for (Object value: array){
            JSONObject data = (JSONObject) ((JSONObject) value).get("data");
            if (data.get("name").equals(name) && (genderDict.get(((String) data.get("gender"))).equals(sex))){
                return false;
            }
        }
        return true;
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