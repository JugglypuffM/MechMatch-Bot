package database.main;

import bots.platforms.Platform;
import database.models.Account;
import database.models.Connection;
import database.models.Profile;
import database.models.User;

import java.util.List;

public interface Database {
    void addUser(Integer id, String username, String platform);
    /**
     * Get user from Database.
     * Checks if user exists.
     * @param id string representation of user id
     * @return {@link User} if exists, null if not
     */
    User getUser(Integer id);
    void updateUser(User user);
    /**
     * Delete user from Database.
     * Checks if user exists.
     * @param id string representation of user id
     */
    void deleteUser(Integer id);



    void addConnection(Integer userID, Integer friendID, Boolean isLiked);
    /**
     * Get connection from Database.
     * Checks if connection exists.
     * @param id string representation of user id
     * @return {@link Connection} if exists, null if not
     */
    Connection getConnection(int id);
    void updateConnection(Connection connection);
    /**
     * Delete user from Database.
     * Checks if user exists.
     * @param id string representation of user id
     */
    void deleteConnection(int id);
    /**
     * Get list with id's of users, who have connection with given user
     *
     * @param id string representation of user id
     * @return list of id's
     */
    List<Integer> getAllConnectedUserIds(Integer id);
    List<Integer> getAllConnectionsWith(Integer id);
    /**
     * Get connections with given user, which were not set to like or dislike
     * @param id string representation of user id
     * @return list with integer id's of connections
     */
    List<Integer> getPendingOf(Integer id);
    /**
     * Get connections with given user, which were set to like
     * @param id string representation of user id
     * @return list with integer id's of connections
     */
    List<Integer> getLikesOf(Integer id);
    /**
     * Get connections with given user, which were set to dislike
     * @param id string representation of user id
     * @return list with integer id's of connections
     */
    List<Integer> getDislikesOf(Integer id);
    void deleteAllConnectionsWith(Integer id);


    void addAccount(String login);
    Account getAccount(Integer id);
    void updateAccount(Account account);
    void deleteAccount(Integer id);
    void addProfile(Integer id);
    Profile getProfile(Integer id);
    void updateProfile(Profile profile);
    void deleteProfile(Integer id);
    Account getAccountWithPlatformId(String platformId, Platform platform);
    /**
     * Get filledProfilesList.
     * Checks if it is initialized and initializes if not.
     * Excludes given id from returned list.
     *
     * @param id string representation of user id
     * @return list with id's
     */
    List<Integer> getFilledProfilesList(Integer id);
    /**
     * Collecting all user data in a string
     * @param id string presentation of user id
     * @return formatted user profile data
     */
    String profileData(Integer id);

    void addToFPL(Integer id);
    void deleteFromFPL(Integer id);
}

