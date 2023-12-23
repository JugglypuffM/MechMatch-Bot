package database.main;

import bots.platforms.Platform;
import database.dao.AccountDAO;
import database.dao.ConnectionDAO;
import database.dao.ProfileDAO;
import database.dao.ClientDAO;
import database.hibernate.HibernateSessionFactory;
import database.entities.Account;
import database.entities.Connection;
import database.entities.Profile;
import database.entities.Client;

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
    private final ClientDAO clientDao = new ClientDAO(sessionFactory);
    /**
     * Data Access Object class for connections.
     */
    private final ConnectionDAO connectionDAO = new ConnectionDAO(sessionFactory);
    /**
     * Data Access Object class for accounts.
     */
    private final AccountDAO accountDAO = new AccountDAO(sessionFactory);
    /**
     * Data Access Object class for profile.
     */
    private final ProfileDAO profileDAO = new ProfileDAO(sessionFactory);
    @Override
    public void addClient(String id, String platform){
        clientDao.create(new Client(id, platform));
    }
    @Override
    public Client getClient(String platformId){
        return clientDao.read(platformId);
    }
    @Override
    public void updateClient(Client client){
        if (clientDao.read(client.getPlatformId()) == null) return;
        clientDao.update(client);
    }
    @Override
    public void deleteClient(String id){
        Client client = clientDao.read(id);
        if (client == null) return;
        clientDao.delete(client);
    }
    @Override
    public void addConnection(Integer userID, Integer friendID, Boolean isLiked){
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
    public List<Integer> getAllConnectedUserIds(Integer id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getConnectionsWith(id)){
            connections.add(connection.getFriendID());
        }
        return connections;
    }

    @Override
    public List<Integer> getAllDeletedWith(Integer id) {
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getDeletedWith(id)){
            connections.add(connection.getId());
        }
        return connections;
    }

    @Override
    public List<Integer> getAllConnectionsWith(Integer id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getConnectionsWith(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    @Override
    public List<Integer> getPendingOf(Integer id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getPendingOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    @Override
    public List<Integer> getLikesOf(Integer id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getLikesOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    @Override
    public List<Integer> getDislikesOf(Integer id){
        List<Integer> connections = new ArrayList<>();
        for (Connection connection: connectionDAO.getDislikesOf(id)){
            connections.add(connection.getId());
        }
        return connections;
    }
    @Override
    public void deleteAllConnectionsWith(Integer id){
        for (Connection connection: connectionDAO.getConnectionsOf(id)){
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
    public Account getAccountWithPlatformId(String platformId, Platform platform) {
        return accountDAO.getAccountWithPlatformId(platformId, platform);
    }

    @Override
    public Account getAccountWithLogin(String login) {
        return accountDAO.getAccountWithLogin(login);
    }


    @Override
    public void addProfile(Integer id) {
        profileDAO.create(new Profile(id));
    }

    @Override
    public Profile getProfile(Integer id) {
        return profileDAO.read(id);
    }

    @Override
    public void updateProfile(Profile profile) {
        if (profileDAO.read(profile.getId()) == null) return;
        profileDAO.update(profile);
    }

    @Override
    public void deleteProfile(Integer id) {
        Profile profile = profileDAO.read(id);
        if (profile == null) return;
        profileDAO.delete(profile);
    }

    @Override
    public List<Integer> getFilledProfilesList(Integer id){
        List<Integer> tmpList = new ArrayList<>();
        for (Profile profile : profileDAO.getFilledProfiles()){
            tmpList.add(profile.getId());
        }
        tmpList.remove(id);
        return tmpList;
    }
    @Override
    public String profileData(Integer id){
        Profile profile = getProfile(id);
        return "Имя: " + profile.getName() +
                "\nВозраст: " + profile.getAge() +
                "\nПол: " + profile.getSex() +
                "\nГород: " + profile.getCity() +
                "\nИнформация о себе: " + profile.getInformation() +
                "\nДиапазон возраста собеседника: " + profile.getMinExpectedAge() + " - " + profile.getMaxExpectedAge() +
                "\nПол собеседника: " + profile.getExpectedSex() +
                "\nГород собеседника: " + profile.getExpectedCity();
    }
    @Override
    public void addToFPL(Integer id) {
        Profile profile = getProfile(id);
        profile.setProfileFilled(true);
        updateProfile(profile);
    }
    @Override
    public void deleteFromFPL(Integer id) {
        Profile profile = getProfile(id);
        profile.setProfileFilled(false);
        updateProfile(profile);
    }
}
