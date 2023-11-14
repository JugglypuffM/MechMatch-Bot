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
    private final DAO dao = new DAO();
    /**
     * TO DESCRIBE
     */
    private final List<String> filledProfilesList = new ArrayList<>();
    /**
     * TO DESCRIBE
     * @param id string representation of user id
     */
    public void addUser(String id, String username){
       dao.createUser(new User(id, username));
    }
    /**
     * TO DESCRIBE
     * @param id string representation of user id
     * @return TO DESCRIBE
     */
    public User getUser(String id){
        User user;
        try {
            user = dao.getUser(id);
        }catch (NullPointerException e){
            return null;
        }
        return user;
    }
    /**
     * TO DESCRIBE
     * @param user
     */
    public void updateUser(User user){
        dao.updateUser(user);
    }
    public void addConnection(String userID, String friendID){
        Connection connection = new Connection(userID, friendID);
        dao.createConnection(connection);
    }
    public List<String> getConnectionsWith(String userID){
        List<String> connections = new ArrayList<>();
        for (Connection connection: dao.getConnectionsWith(userID)){
            connections.add(connection.getFriendID());
        }
        return connections;
    }

    /**
     * TO DESCRIBE
     * @param id
     * @return
     */
    public List<String> getFilledProfilesList(String id){
        if (filledProfilesList.isEmpty()){
            List<User> users = dao.getProfileFilledUsers();
            for (User user : users){
                filledProfilesList.add(user.getId());
            }
        }
        List<String> tmpList = new ArrayList<>(filledProfilesList);
        tmpList.remove(id);
        return tmpList;
    }
    public void addToFPL(String id){
        filledProfilesList.add(id);
    }
    public void deleteFromFPL(String id){
        filledProfilesList.remove(id);
    }
}
