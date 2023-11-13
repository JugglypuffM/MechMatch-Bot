package database;

import mainBot.User;

import java.util.ArrayList;
import java.util.List;

/**
 * TO DESCRIBE
 */
public class UserService {
    /**
     * TO DESCRIBE
     */
    private final UserDao dao = new UserDao();
    private final List<String> otherProfilesList = new ArrayList<>();
    /**
     * TO DESCRIBE
     * @param id string representation of user id
     */
    public void addUser(String id){
       dao.createUser(new User(id));
    }
    /**
     * TO DESCRIBE
     * @param id string representation of user id
     * @return TO DESCRIBE
     */
    public User getUser(String id){
        try {
            return dao.getUser(id);
        }catch (NullPointerException e){
            return null;
        }
    }
    public void updateUser(User user){
        dao.update(user);
    }
    public List<String> getOtherProfilesList(String id){
        if (otherProfilesList.isEmpty()){
            List<User> users = dao.getProfileFilledUsers();
            for (User user : users){
                otherProfilesList.add(user.getId());
            }
        }
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
