package database.main;

import bots.platforms.Platform;
import database.dao.AccountDAO;
import database.dao.ConnectionDAO;
import database.dao.UserDAO;
import database.hibernate.HibernateSessionFactory;
import database.models.Account;
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
    /**
     * Data Access Object class for accounts.
     */
    private final AccountDAO accountDAO = new AccountDAO(sessionFactory);
    @Override
    public void addUser(String id, String username, String platform){
        userDao.create(new User(id, username, platform));
    }
    @Override
    public User getUser(String id){
        return userDao.read(id);
    }
    @Override
    public void updateUser(User user){
        if (userDao.read(user.getId()) == null) return;
        userDao.update(user);
    }
    @Override
    public void deleteUser(String id){
        User user = userDao.read(id);
        if (user == null) return;
        userDao.delete(user);
    }
    @Override
    public void addConnection(String userID, String friendID, Boolean isLiked){
        connectionDAO.create(new Connection(userID, friendID, isLiked));
    }
    @Override
    public Connection getConnection(int id){
        return connectionDAO.read(id);
    }
    public void updateConnection(Connection connection){
        if (connectionDAO.read(connection.getId()) == null) return;
        connectionDAO.update(connection);
    }
    @Override
    public void deleteConnection(int id){
        Connection connection = connectionDAO.read(id);
        if (connection == null) return;
        connectionDAO.delete(connection);
    }
    @Override
    public List<String> getAllConnectedUserIds(String id){
        List<String> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getConnectionsWith(id)){
            connections.add(connection.getFriendID());
        }
        return connections;
    }
    @Override
    public List<Integer> getAllConnectionsWith(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getConnectionsWith(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    @Override
    public List<Integer> getPendingOf(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getPendingOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    @Override
    public List<Integer> getLikesOf(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getLikesOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    @Override
    public List<Integer> getDislikesOf(String id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getDislikesOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    @Override
    public void deleteAllConnectionsWith(String id){
        for (Connection connection: connectionDAO.getConnectionsWith(id)){
            connectionDAO.delete(connection);
        }
    }
    @Override
    public void addAccount(String login) {
        accountDAO.create(new Account(login));
    }
    @Override
    public Account getAccount(Integer id) {
        return accountDAO.read(id);
    }
    @Override
    public void updateAccount(Account account) {
        if (accountDAO.read(account.getId()) == null) return;
        accountDAO.update(account);
    }
    @Override
    public void deleteAccount(Integer id) {
        Account account = accountDAO.read(id);
        if (account == null) return;
        accountDAO.delete(account);
    }

    @Override
    public Account getAccountWithPlatformId(String id, Platform platform) {
        return accountDAO.getAccountWithPlatformId(id, platform);
    }

    @Override
    public List<String> getFilledProfilesList(String id){
        List<String> tmpList = new ArrayList<>();
        for (User user : userDao.getProfileFilledUsers()){
            tmpList.add(user.getId());
        }
        tmpList.remove(id);
        return tmpList;
    }
    @Override
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
    @Override
    public void addToFPL(String id) {
        User user = getUser(id);
        user.setProfileFilled(true);
        updateUser(user);
    }
    @Override
    public void deleteFromFPL(String id) {
        User user = getUser(id);
        user.setProfileFilled(false);
        updateUser(user);
    }
}
