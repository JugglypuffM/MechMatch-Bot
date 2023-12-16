package database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
 * Contains all user profile data.
 * Is always related to one account.
 * Its id is an id of related account.
 */
@Entity
@Table(name = "profiles", schema = "public")
public class Profile {
    @Id
    private Integer id;
    private String name, city, expectedCity, sex, expectedSex, information, photoID;
    private int age, minExpectedAge, maxExpectedAge;
    private boolean profileFilled;
    public Profile(){}
    public Profile(Integer id){
        this.id = id;
        this.minExpectedAge = 0;
        this.maxExpectedAge = 999;
        this.profileFilled = false;
    }
    public Integer getId(){
        return id;
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
     * Checks if given string is correct and fills {@link Profile#sex}.
     * @param m_sex string, which should be equal to "Парень" if sex is male or to "Девушка" if sex female.
     * @return true if {@link Profile#sex} filled successfully and false if not.
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
     * Checks if given string is correct and fills {@link Profile#expectedSex}.
     * @param m_expectedSex string, which should be equal to "Парень" if sex is male or to "Девушка" if sex female.
     * @return true if {@link Profile#expectedSex} filled successfully and false if not.
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
     * Checks if given integer is correct and fills {@link Profile#age}.
     * @param m_age integer, which should be between 14 and 120.
     * @return true if {@link Profile#age} filled successfully and false if not.
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
     * Checks if given integer is correct and fills {@link Profile#minExpectedAge}.
     * @param m_minExpectedAge integer, which should be between 14 and 120.
     * @return true if {@link Profile#minExpectedAge} filled successfully and false if not.
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
     * Checks if given integer is correct and fills {@link Profile#maxExpectedAge}.
     * @param m_maxExpectedAge integer, which should be between 14 and 120.
     * @return true if {@link Profile#maxExpectedAge} filled successfully and false if not.
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
}
