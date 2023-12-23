package database.main;

import bots.platforms.Platform;
import database.entities.Account;
import database.entities.Connection;
import database.entities.Profile;
import database.entities.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface Database {
    void addClient(String id, String platform);
    /**
     * Get user from Database.
     * Checks if user exists.
     * @param platformId string representation of user platformId
     * @return {@link Client} if exists, null if not
     */
    Client getClient(String platformId);
    void updateClient(Client client);
    /**
     * Delete user from Database.
     * Checks if user exists.
     * @param id string representation of user id
     */
    void deleteClient(String id);



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
    List<Integer> getAllDeletedWith(Integer id);
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
    Account getAccountWithPlatformId(String platformId, Platform platform);
    Account getAccountWithLogin(String login);


    void addProfile(Integer id);
    Profile getProfile(Integer id);
    void updateProfile(Profile profile);
    void deleteProfile(Integer id);
    /**
     * Method to unify field filling.
     * Uses different setters depending on current local state.
     * @param value user's message
     * @return true if field was filled successfully and false if not
     */
    default Boolean setField(Profile profile, String value){
        Boolean result;
        result = switch (getAccount(profile.getId()).getLocalState()) {
            case NAME -> {
                profile.setName(value);
                yield true;
            }
            case AGE -> profile.setAge(value);
            case SEX -> profile.setSex(value);
            case CITY -> {
                profile.setCity(value);
                yield true;
            }
            case ABOUT -> {
                profile.setInformation(value);
                yield true;
            }
            case EAGEMIN -> profile.setMinExpectedAge(value);
            case EAGEMAX -> profile.setMaxExpectedAge(value);
            case ESEX -> profile.setExpectedSex(value);
            case ECITY -> {
                profile.setExpectedCity(value);
                yield true;
            }
            default -> false;
        };
        return result;
    }
    default String getUserUsernames(Integer id){
        StringBuilder result = new StringBuilder("Вот имена этого пользователя на разных платформах:\n");
        Account user = getAccount(id);
        List<Platform> platforms = new ArrayList<>();
        platforms.add(Platform.TELEGRAM);
        platforms.add(Platform.DISCORD);
        for (Platform platform: platforms){
            if (user.getPlatformUsername(platform) != null){
                if (platform == Platform.TELEGRAM){
                    result.append(platform.stringRepresentation()).append(" - @").
                            append(user.getPlatformUsername(platform).replace("_", "\\_")).append("\n");
                }else {
                    result.append(platform.stringRepresentation()).append(" - ").
                            append(user.getPlatformUsername(platform)).append("\n");
                }
            }
        }
        return result.toString();
    }
    default Integer getNewFriendId(Integer id){
        Account friend, user = getAccount(id);
        Profile friendProfile, profile = getProfile(id);
        List<Integer> fpl = getFilledProfilesList(user.getId());
        int tmpNum = 0;
        while (tmpNum < fpl.size()) {
            friend = getAccount(fpl.get(tmpNum));
            friendProfile = getProfile(fpl.get(tmpNum));
            boolean userSexMatch = (profile.getExpectedSex().equalsIgnoreCase("без разницы")) || (friendProfile.getSex().equals(profile.getExpectedSex()));
            boolean friendSexMatch = (friendProfile.getExpectedSex().equalsIgnoreCase("без разницы")) || (profile.getSex().equals(friendProfile.getExpectedSex()));
            boolean userCityMatch = (profile.getExpectedCity().equalsIgnoreCase("любой")) || (friendProfile.getCity().equalsIgnoreCase(profile.getExpectedCity()));
            boolean friendCityMatch = (friendProfile.getExpectedCity().equalsIgnoreCase("любой")) || (profile.getCity().equalsIgnoreCase(friendProfile.getExpectedCity()));
            boolean userAgeMatch = (friendProfile.getAge() <= profile.getMaxExpectedAge()) && (friendProfile.getAge() >= profile.getMinExpectedAge());
            boolean friendAgeMatch = (profile.getAge() <= friendProfile.getMaxExpectedAge()) && (profile.getAge() >= friendProfile.getMinExpectedAge());
            List<Integer> friendDislikes = new ArrayList<>();
            for (Integer i: getDislikesOf(friend.getId())){
                friendDislikes.add(getConnection(i).getFriendID());
            }
            if (userSexMatch && userCityMatch && userAgeMatch &&
                    friendSexMatch && friendCityMatch && friendAgeMatch &&
                    !getAllConnectedUserIds(user.getId()).contains(friend.getId()) &&
                    !friendDislikes.contains(user.getId()) &&
                    !(Objects.equals(friend.getSuggestedFriendID(), user.getId()))) {
                return friend.getId();
            }
            tmpNum++;
        }
        return -1;
    }
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

