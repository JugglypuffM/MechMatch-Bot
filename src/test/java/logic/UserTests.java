package logic;

import database.models.User;
import logic.states.GlobalState;
import logic.states.LocalState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserTests {
    /**
     * Test of user initialization
     */
    @Test
    public void initUserTest(){
        User user = new User(0, "stas", "TELEGRAM");
        Assertions.assertEquals(0, user.getId());
        Assertions.assertEquals(user.getGlobalState(), GlobalState.COMMAND);
        Assertions.assertEquals(user.getLocalState(), LocalState.START);
    }

}
