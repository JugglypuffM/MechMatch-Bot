package mainBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class crated to store users in memory using {@link UserStorage#userDict} dictionary.
 */
public class UserStorage {
    private final Map<String, User> userDict = new HashMap<>();
    private final List<String> otherProfilesList = new ArrayList<>();
    public Map<String, User> getUserDict() {
        return userDict;
    }
    /**
     * Initializes new user and puts him into {@link UserStorage#userDict} dictionary by his id as a key.
     * @param id string representation of user id
     */
    public void addUser(String id){
        User user = new User(id);
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
    public List<String> getOtherProfilesList(String id){
        List<String> tmpList = new ArrayList<>(otherProfilesList);
        tmpList.remove(id);
        return tmpList;
    }
    public void addToOPL(String id){
        otherProfilesList.add(id);
    }
    public void deleteFromOPL(String id){
        otherProfilesList.remove(id);
    }
}
