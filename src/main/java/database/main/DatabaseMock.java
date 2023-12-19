package database.main;

import bots.platforms.Platform;
import database.entities.Connection;
import database.entities.Account;
import database.entities.Profile;
import database.entities.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class crated to mock database service dictionary.
 */
public class DatabaseMock implements Database {
    /**
     * Dictionary of users, where user id is key and the instance of {@link Client} is value
     */
    private final Map<String, Client> clientDict = new HashMap<>();
    /**
     * Dictionary of connections, where connection id is key and the instance of {@link Connection} is value
     */
    private final Map<Integer, Connection> connectionDict = new HashMap<>();
    /**
     * Dictionary of accounts, where account id is key and the instance of {@link Account} is value
     */
    private final Map<Integer, Account> accountDict = new HashMap<>();
    /**
     * Dictionary of profiles, where profile id is key and the instance of {@link Profile} is value
     */
    private final Map<Integer, Profile> profileDict = new HashMap<>();
    /**
     * Auto incrementing on every new connection id
     */
    private Integer lastConnectionID = 0;
    /**
     * Auto incrementing on every new account id
     */
    private Integer lastAcountID = 0;
    /**
     * List of users with fully filled profiles id's
     */
    private final List<Integer> filledProfilesList = new ArrayList<>();

    @Override
    public void addClient(String id, String platform) {
        Client client = new Client(id, platform);
        clientDict.put(id, client);
    }

    @Override
    public Client getClient(String platformId) {
        if (!clientDict.containsKey(platformId)){
            return null;
        }
        return clientDict.get(platformId);
    }

    @Override
    public void updateClient(Client client) {
        if (!clientDict.containsKey(client.getPlatformId())) return;
        clientDict.put(client.getPlatformId(), client);
    }

    @Override
    public void deleteClient(String id) {
        clientDict.remove(id);
    }

    @Override
    public void addConnection(Integer userID, Integer friendID, Boolean isLiked) {
        connectionDict.put(lastConnectionID, new Connection(lastConnectionID, userID, friendID, isLiked));
        lastConnectionID++;
    }

    @Override
    public Connection getConnection(int id) {
        if (!connectionDict.containsKey(id)){
            return null;
        }
        return connectionDict.get(id);
    }

    @Override
    public void updateConnection(Connection connection) {
        if (!connectionDict.containsKey(connection.getId())) return;
        connectionDict.put(connection.getId(), connection);
    }

    @Override
    public void deleteConnection(int id) {
        connectionDict.remove(id);
    }

    @Override
    public List<Integer> getAllConnectedUserIds(Integer id) {
        List<Integer> result = new ArrayList<>();
        for (Connection connection: connectionDict.values()){
            if (connection.getUserID().equals(id)){
                result.add(connection.getFriendID());
            }
        }
        return result;
    }

    @Override
    public List<Integer> getAllConnectionsWith(Integer id) {
        List<Integer> result = new ArrayList<>();
        for (Connection connection: connectionDict.values()){
            if (connection.getUserID().equals(id)){
                result.add(connection.getId());
            }
        }
        return result;
    }

    public List<Integer> getAllConnectionsOf(Integer id) {
        List<Integer> result = new ArrayList<>();
        for (Connection connection: connectionDict.values()){
            if (connection.getUserID().equals(id) || connection.getFriendID().equals(id)){
                result.add(connection.getId());
            }
        }
        return result;
    }

    @Override
    public List<Integer> getPendingOf(Integer id) {
        List<Integer> result = new ArrayList<>();
        for (Connection connection: connectionDict.values()){
            if (connection.getUserID().equals(id) && (connection.getLiked() == null)){
                result.add(connection.getId());
            }
        }
        return result;
    }

    @Override
    public List<Integer> getLikesOf(Integer id) {
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

    @Override
    public List<Integer> getDislikesOf(Integer id) {
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

    @Override
    public void deleteAllConnectionsWith(Integer id) {
        for (Integer i: getAllConnectionsOf(id)){
            connectionDict.remove(i);
        }
    }

    @Override
    public void addAccount(String login) {
        accountDict.put(lastAcountID, new Account(lastAcountID, login));
        lastAcountID++;
    }

    @Override
    public Account getAccount(Integer id) {
        if (!accountDict.containsKey(id)) return null;
        return accountDict.get(id);
    }

    @Override
    public void updateAccount(Account account) {
        if (!accountDict.containsKey(account.getId())) return;
        accountDict.put(account.getId(), account);
    }

    @Override
    public void deleteAccount(Integer id) {
        if (!accountDict.containsKey(id)) return;
        accountDict.remove(id);
    }

    @Override
    public void addProfile(Integer id) {
        profileDict.put(id, new Profile(id));
    }

    @Override
    public Profile getProfile(Integer id) {
        return profileDict.get(id);
    }

    @Override
    public void updateProfile(Profile profile) {
        if (!profileDict.containsKey(profile.getId())) return;
        profileDict.put(profile.getId(), profile);
    }

    @Override
    public void deleteProfile(Integer id) {
        if (!profileDict.containsKey(id)) return;
        profileDict.remove(id);
    }

    @Override
    public Account getAccountWithPlatformId(String platformId, Platform platform) {
        for (Account account: accountDict.values()){
            if (account.getPlatformId(platform).equals(platformId)) return account;
        }
        return null;
    }

    @Override
    public Account getAccountWithLogin(String login) {
        for (Account account: accountDict.values()){
            if (account.getLogin().equals(login)) return account;
        }
        return null;
    }

    @Override
    public List<Integer> getFilledProfilesList(Integer id) {
        List<Integer> tmpList = new ArrayList<>(filledProfilesList);
        tmpList.remove(id);
        return tmpList;
    }

    @Override
    public String profileData(Integer id) {
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

    /**
     * Add given user id to filled profiles list
     * @param id string representation of user id
     */
    @Override
    public void addToFPL(Integer id){
        filledProfilesList.add(id);
    }

    /**
     * Delete given user id from filled profiles list
     * @param id string representation of user id
     */
    @Override
    public void deleteFromFPL(Integer id){
        filledProfilesList.remove(id);
    }
}