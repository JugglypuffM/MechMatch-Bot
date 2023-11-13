package database;

import mainBot.User;

import java.util.List;

public class UserService {

    private final UserDao usersDao = new UserDao();

    public UserService() {
    }

    public UserObject findUser(int id) {
        return usersDao.findById(id);
    }

    public void saveUser(User user) {
        UserObject userObj = new UserObject(user);
        usersDao.save(userObj);
    }

    public void deleteUser(User user) {
        UserObject userObj = new UserObject(user);
        usersDao.delete(userObj);
    }

    public void updateUser(User user) {
        UserObject userObj = new UserObject(user);
        usersDao.update(userObj);
    }

    public List<UserObject> findAllUsers() {
        return usersDao.findAll();
    }


}