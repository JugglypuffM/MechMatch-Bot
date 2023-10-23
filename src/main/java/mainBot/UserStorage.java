package mainBot;

import java.util.HashMap;
import java.util.Map;

/**
 * Class crated to store users in memory using {@link UserStorage#userList} dictionary.
 */
public class UserStorage {
    private final Map<String, User> userList = new HashMap<>();
    public Map<String, User> getUserList() {
        return userList;
    }

    /**
     * Initializes new user and puts him into {@link UserStorage#userList} dictionary by his id as a key.
     * @param id string representation of user id
     */
    public void addUser(String id){
        User user = new User(id);
        userList.put(id, user);
    }

    /**
     * Checks if user with given id exists in {@link UserStorage#userList}, adds with {@link UserStorage#addUser} if not and returns this user.
     * @param id string representation of user id
     * @return new or existing instance of {@link User} from {@link UserStorage#userList}
     */
    public User getUser(String id){
        if (!userList.containsKey(id)){
            return null;
        }
        return userList.get(id);
    }
}
