package database;

import database.models.Connection;
import database.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class crated to store users in memory using {@link DatabaseMock#userDict} dictionary.
 */
public class DatabaseMock implements Database {
    /**
     * Dictionary of users, where user id is key and the instance of {@link User} is value
     */
    private final Map<String, User> userDict = new HashMap<>();
    /**
     * Dictionary of users, where user id is key and the instance of {@link User} is value
     */
    private final Map<Integer, Connection> connectionDict = new HashMap<>();
    private Integer lastConnectionID = 0;
    /**
     * List of users with fully filled profiles id's
     */
    private final List<String> filledProfilesList = new ArrayList<>();

    public void addUser(String id, String username) {
        User user = new User(id, username);
        userDict.put(id, user);
    }

    public User getUser(String id) {
        if (!userDict.containsKey(id)){
            return null;
        }
        return userDict.get(id);
    }

    public void updateUser(User user) {
        if (!userDict.containsKey(user.getId())) return;
        userDict.put(user.getId(), user);
    }

    public void deleteUser(String id) {
        userDict.remove(id);
    }

    public void addConnection(String userID, String friendID, Boolean isLiked) {
        connectionDict.put(lastConnectionID, new Connection(lastConnectionID, userID, friendID, isLiked));
        lastConnectionID++;
    }

    public Connection getConnection(int id) {
        if (!connectionDict.containsKey(id)){
            return null;
        }
        return connectionDict.get(id);
    }

    public void updateConnection(Connection connection) {
        if (!connectionDict.containsKey(connection.getId())) return;
        connectionDict.put(connection.getId(), connection);
    }

    public void deleteConnection(int id) {
        connectionDict.remove(id);
    }

    public List<String> getAllConnectedUserIds(String id) {
        List<String> result = new ArrayList<>();
        for (Connection connection: connectionDict.values()){
            if (connection.getUserID().equals(id)){
                result.add(connection.getFriendID());
            }
        }
        return result;
    }

    public List<Integer> getAllConnectionsWith(String id) {
        List<Integer> result = new ArrayList<>();
        for (Connection connection: connectionDict.values()){
            if (connection.getUserID().equals(id)){
                result.add(connection.getId());
            }
        }
        return result;
    }

    public List<Integer> getPendingOf(String id) {
        List<Integer> result = new ArrayList<>();
        for (Connection connection: connectionDict.values()){
            if (connection.getUserID().equals(id) && (connection.getLiked() == null)){
                result.add(connection.getId());
            }
        }
        return result;
    }

    public List<Integer> getLikesOf(String id) {
        List<Integer> result = new ArrayList<>();
        for (Connection connection: connectionDict.values()){
            if (connection.getLiked() == null){
                continue;
            }
            if (connection.getUserID().equals(id) && (connection.getLiked())){
                result.add(connection.getId());
            }
        }
        return result;
    }

    public List<Integer> getDislikesOf(String id) {
        List<Integer> result = new ArrayList<>();
        for (Connection connection: connectionDict.values()){
            if (connection.getLiked() == null){
                continue;
            }
            if (connection.getUserID().equals(id) && (!connection.getLiked())){
                result.add(connection.getId());
            }
        }
        return result;
    }

    public void deleteAllConnectionsWith(String id) {
        for (Integer i: getAllConnectionsWith(id)){
            connectionDict.remove(i);
        }
    }

    public List<String> getFilledProfilesList(String id) {
        List<String> tmpList = new ArrayList<>(filledProfilesList);
        tmpList.remove(id);
        return tmpList;
    }

    public String profileData(String id) {
        User user = getUser(id);
        return "Имя: " + user.getName() +
                "\nВозраст: " + user.getAge() +
                "\nПол: " + user.getSex() +
                "\nГород: " + user.getCity() +
                "\nИнформация о себе: " + user.getInformation() +
                "\nДиапазон возраста собеседника: " + user.getMinExpectedAge() + " - " + user.getMaxExpectedAge() +
                "\nПол собеседника: " + user.getExpectedSex() +
                "\nГород собеседника: " + user.getExpectedCity();
    }

    public void eraseProfileData(String id) {
        userDict.get(id).setName(null);
        userDict.get(id).setAge("0");
        userDict.get(id).setSex("");
        userDict.get(id).setCity(null);
        userDict.get(id).setInformation(null);
        userDict.get(id).setMinExpectedAge("0");
        userDict.get(id).setMaxExpectedAge("999");
        userDict.get(id).setExpectedSex("");
        userDict.get(id).setExpectedCity(null);
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