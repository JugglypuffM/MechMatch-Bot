package mainBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserStorageTests {
    /**
     * Test of user addition
     */
    @Test
    public void addUserTest(){
        UserStorage storage = new UserStorage();
        Assertions.assertFalse(storage.getUserDict().containsKey("0"));
        storage.addUser("0");
        Assertions.assertTrue(storage.getUserDict().containsKey("0"));
    }

    /**
     * Test of user extraction
     */
    @Test
    public void getUserTest(){
        UserStorage storage = new UserStorage();
        storage.addUser("0");
        //Does return exact same user
        storage.getUserDict().get("0").setAge(12);
        User A = storage.getUserDict().get("0");
        Assertions.assertEquals(storage.getUser("0").getAge(), A.getAge());
        //Does return null if non-existing id given
        Assertions.assertNull(storage.getUser("1"));
    }
}
