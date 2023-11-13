package mainBot;

import database.UserObject;
import database.UserService;
import org.junit.jupiter.api.Test;

public class UserServiceTests {
    @Test
    public void crudTest(){
        UserService service = new UserService();
        User user = new User("0", "stats", "ekb", "ekb", "парень", "девушка", "prosto crud", "photoID", GlobalState.COMMAND, LocalState.START, 19, 118,119);
        UserObject userObject = new UserObject(user);
        service.saveUser(user);
    }
}
