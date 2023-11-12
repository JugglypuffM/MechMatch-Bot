package mainBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class crated to store users in memory using {@link UserStorage#userDict} dictionary.
 */
public class UserStorage {
    /**
     * Dictionary of users, where user id is key and the instance of {@link User} is value
     */
    private final Map<String, User> userDict = new HashMap<>();
    /**
     * List of users with fully filled profiles id's
     */
    private final List<String> filledProfilesList = new ArrayList<>();
    public Map<String, User> getUserDict() {
        return userDict;
    }
    /**
     * Initializes new user and puts him into {@link UserStorage#userDict} dictionary by his id as a key.
     * @param id string representation of user id
     */
    public void addUser(String id, String username){
        User user = new User(id, username);
        userDict.put(id, user);
    }
    /**
     * Checks if user with given id exists in {@link UserStorage#userDict}, adds with {@link UserStorage#addUser} if not and returns this user.
     * @param id string representation of user id
     * @return new or existing instance of {@link User} from {@link UserStorage#userDict}
     */
    public User getUser(String id){
        if (!userDict.containsKey(id)){
            return null;
        }
        return userDict.get(id);
    }
    /**
     * Getter for list of all filled profiles id's
     * @param id string representation of user id
     * @return list of all users with filled profile id's, except given
     */
    public List<String> getFPL(String id){
        List<String> tmpList = new ArrayList<>(filledProfilesList);
        tmpList.remove(id);
        return tmpList;
    }

    /**
     * Add given user id to filled profiles list
     * @param id string representation of user id
     */
    public void addToFPL(String id){
        filledProfilesList.add(id);
    }

    /**
     * Delete given user id from filled profiles list
     * @param id string representation of user id
     */
    public void deleteFromFPL(String id){
        filledProfilesList.remove(id);
    }
}
