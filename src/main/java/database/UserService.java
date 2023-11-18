package database;

import database.models.Connection;
import database.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access layer class.
 * Responsible for working with database.
 */
public class UserService {
    /**
     * Data Access Object class.
     */
    private final DAO dao = new DAO();
    /**
     * List with id's of users with filled profiles.
     * Stored in memory for speed purposes.
     */
    private final List<String> filledProfilesList = new ArrayList<>();
    public void addUser(String id, String username){
       dao.createUser(new User(id, username));
    }
    /**
     * Get user from database.
     * Checks if user exists.
     * @param id string representation of user id
     * @return {@link User} if exists, null if not
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
    public void updateUser(User user){
        if (dao.getUser(user.getId()) == null){
            return;
        }
        dao.updateUser(user);
    }
    /**
     * Delete user from database.
     * Checks if user exists.
     * @param id string representation of user id
     */
    public void deleteUser(String id){
        User user;
        try {
            user = dao.getUser(id);
        }catch (NullPointerException e){
            return;
        }
        dao.deleteUser(user);
    }
    public void addConnection(String userID, String friendID){
        Connection connection = new Connection(userID, friendID);
        dao.createConnection(connection);
    }

    /**
     * Get list with id's of users, who have connection with given user
     * @param id string representation of user id
     * @return list of id's
     */
    public List<String> getConnectionsWith(String id){
        List<String> connections = new ArrayList<>();
        for (Connection connection: dao.getConnectionsWith(id)){
            connections.add(connection.getFriendID());
        }
        return connections;
    }
    public void deleteConnectionsWith(String id){
        for (Connection connection: dao.getConnectionsWith(id)){
            dao.deleteConnection(connection);
        }
    }
    /**
     * Get {@link UserService#filledProfilesList}.
     * Checks if it is initialized and initializes if not.
     * Excludes given id from returned list.
     * @param id string representation of user id
     * @return list with id's
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
