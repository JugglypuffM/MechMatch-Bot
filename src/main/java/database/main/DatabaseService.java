package database.main;

import database.dao.ConnectionDAO;
import database.dao.UserDAO;
import database.hibernate.HibernateSessionFactory;
import database.models.Connection;
import database.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access layer class.
 * Responsible for working with Database.
 */
public class DatabaseService implements Database {
    private final HibernateSessionFactory sessionFactory = new HibernateSessionFactory();
    /**
     * Data Access Object class for users.
     */
    private final UserDAO userDao = new UserDAO(sessionFactory);
    /**
     * Data Access Object class for connections.
     */
    private final ConnectionDAO connectionDAO = new ConnectionDAO(sessionFactory);
    public void addUser(String id, String username){
        userDao.create(new User(id, username));
    }
    public User getUser(String id){
        return userDao.read(id);
    }
    public void updateUser(User user){
        if (getUser(user.getId()) == null){
            return;
        }
        userDao.update(user);
    }
    public void deleteUser(String id){
        User user = userDao.read(id);
        if (user == null){
            return;
        }
        userDao.delete(user);
    }
    public void addConnection(String userID, String friendID, Boolean isLiked){
        connectionDAO.create(new Connection(userID, friendID, isLiked));
    }
    public Connection getConnection(int id){
        return connectionDAO.read(id);
    }
    public void updateConnection(Connection connection){
        if (getConnection(connection.getId()) == null){
            return;
        }
        connectionDAO.update(connection);
    }
    public void deleteConnection(int id){
        Connection connection = connectionDAO.read(id);
        if (connection == null){
            return;
        }
        connectionDAO.delete(connection);
    }
    public List<String> getAllConnectedUserIds(String id){
        List<String> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getConnectionsWith(id)){
            connections.add(connection.getFriendID());
        }
        return connections;
    }
    public List<Integer> getAllConnectionsWith(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getConnectionsWith(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    public List<Integer> getPendingOf(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getPendingOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    public List<Integer> getLikesOf(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getLikesOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    public List<Integer> getDislikesOf(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getDislikesOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    public void deleteAllConnectionsWith(String id){
        for (Connection connection: connectionDAO.getConnectionsWith(id)){
            connectionDAO.delete(connection);
        }
    }
    public List<String> getFilledProfilesList(String id){
        List<String> tmpList = new ArrayList<>();
        for (User user : userDao.getProfileFilledUsers()){
            tmpList.add(user.getId());
        }
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

    public void addToFPL(String id) {
        User user = getUser(id);
        user.setProfileFilled(true);
        updateUser(user);
    }

    public void deleteFromFPL(String id) {
        User user = getUser(id);
        user.setProfileFilled(false);
        updateUser(user);
    }
}
