package database.main;

import database.models.Connection;
import database.models.User;

import java.util.List;

public interface Database {
    void addUser(String id, String username);
    /**
     * Get user from Database.
     * Checks if user exists.
     * @param id string representation of user id
     * @return {@link User} if exists, null if not
     */
    User getUser(String id);
    void updateUser(User user);
    /**
     * Delete user from Database.
     * Checks if user exists.
     * @param id string representation of user id
     */
    void deleteUser(String id);
    void addConnection(String userID, String friendID, Boolean isLiked);
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
     * @param id string representation of user id
     * @return list of id's
     */
    List<String> getAllConnectedUserIds(String id);
    List<Integer> getAllConnectionsWith(String id);
    /**
     * Get connections with given user, which were not set to like or dislike
     * @param id string representation of user id
     * @return list with integer id's of connections
     */
    List<Integer> getPendingOf(String id);
    /**
     * Get connections with given user, which were set to like
     * @param id string representation of user id
     * @return list with integer id's of connections
     */
    List<Integer> getLikesOf(String id);
    /**
     * Get connections with given user, which were set to dislike
     * @param id string representation of user id
     * @return list with integer id's of connections
     */
    List<Integer> getDislikesOf(String id);
    void deleteAllConnectionsWith(String id);
    /**
     * Get filledProfilesList.
     * Checks if it is initialized and initializes if not.
     * Excludes given id from returned list.
     * @param id string representation of user id
     * @return list with id's
     */
    List<String> getFilledProfilesList(String id);
    /**
     * Collecting all user data in a string
     * @param id string presentation of user id
     * @return formatted user profile data
     */
    String profileData(String id);
    /**
     * Erase all profile data to fill it again
     * @param id string representation of user id
     */
    void eraseProfileData(String id);

    void addToFPL(String id);
    void deleteFromFPL(String id);
}

