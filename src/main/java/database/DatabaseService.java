package database;

import database.models.Connection;
import database.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access layer class.
 * Responsible for working with Database.
 */
public class DatabaseService implements Database {
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
    public User getUser(String id){
        return dao.getUser(id);
    }
    public void updateUser(User user){
        if (getUser(user.getId()) == null){
            return;
        }
        dao.updateUser(user);
    }
    public void deleteUser(String id){
        User user = dao.getUser(id);
        if (user == null){
            return;
        }
        dao.deleteUser(user);
    }
    public void addConnection(String userID, String friendID, Boolean isLiked){
        dao.createConnection(new Connection(userID, friendID, isLiked));
    }
    public Connection getConnection(int id){
        return dao.getConnection(id);
    }
    public void updateConnection(Connection connection){
        if (getConnection(connection.getId()) == null){
            return;
        }
        dao.updateConnection(connection);
    }
    public void deleteConnection(int id){
        Connection connection = dao.getConnection(id);
        if (connection == null){
            return;
        }
        dao.deleteConnection(connection);
    }
    public List<String> getAllConnectedUserIds(String id){
        List<String> connections = new ArrayList<>();
        for (Connection connection: dao.getConnectionsWith(id)){
            connections.add(connection.getFriendID());
        }
        return connections;
    }
    public List<Integer> getAllConnectionsWith(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: dao.getConnectionsWith(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    public List<Integer> getPendingOf(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: dao.getPendingOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    public List<Integer> getLikesOf(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: dao.getLikesOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    public List<Integer> getDislikesOf(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: dao.getDislikesOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    public void deleteAllConnectionsWith(String id){
        for (Connection connection: dao.getConnectionsWith(id)){
            dao.deleteConnection(connection);
        }
    }
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
    public String profileData(String id){
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
    public void eraseProfileData(String id){
        User user = getUser(id);
        user.setName(null);
        user.setAge("0");
        user.setSex("");
        user.setCity(null);
        user.setInformation(null);
        user.setMinExpectedAge("0");
        user.setMaxExpectedAge("999");
        user.setExpectedSex("");
        user.setExpectedCity(null);
    }

    public void addToFPL(String id){
        filledProfilesList.add(id);
    }
    public void deleteFromFPL(String id){
        filledProfilesList.remove(id);
    }
}
