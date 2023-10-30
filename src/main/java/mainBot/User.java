package mainBot;

/**
 * Main user class. Contains all data fields required for matching.<p>
 * {@link User#id} is unique for every user.<p>
 * {@link User#sex} and {@link User#expectedSex} are boolean, where false is male and true is female.
 */
public class User{
    private final String id;
    private String name, city, expectedCity, sex, expectedSex, globalState, localState, information;
    private int age, minExpectedAge, maxExpectedAge;
    public User(String m_id){
        this.id = m_id;
        this.globalState = "default";
        this.localState = "";
        minExpectedAge = 0;
        maxExpectedAge = 999;
    }
    public String getId(){
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
    public boolean setAge(int m_age) {
        if ((m_age > 14) && (m_age < 120)){
            this.age = m_age;
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
    public boolean setMinExpectedAge(int m_minExpectedAge) {
        if ((m_minExpectedAge > 14) && (m_minExpectedAge < 120) && (maxExpectedAge >= m_minExpectedAge)){
            this.minExpectedAge = m_minExpectedAge;
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
    public boolean setMaxExpectedAge(int m_maxExpectedAge) {
        if ((m_maxExpectedAge > 14) && (m_maxExpectedAge < 120) && (m_maxExpectedAge >= minExpectedAge)){
            this.maxExpectedAge = m_maxExpectedAge;
            return true;
        }
        return false;
    }
    public String getGlobalState(){
        return globalState;
    }
    public void setGlobalState(String m_globalState){
        this.globalState = m_globalState;
    }
    public String getLocalState(){
        return localState;
    }
    public void setLocalState(String m_localState){
        this.localState = m_localState;
    }
    public String getInformation(){
        return information;
    }
    public void setInformation(String m_information){
        this.information = m_information;
    }
}
