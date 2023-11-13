package mainBot;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserTests {
    /**
     * Test of user initialization
     */
    @Test
    public void initUserTest(){
        User user = new User("0", "stats");
        Assertions.assertEquals("0", user.getId());
        Assertions.assertEquals("stats", user.getUsername());
        Assertions.assertEquals(user.getGlobalState(), GlobalState.COMMAND);
        Assertions.assertEquals(user.getLocalState(), LocalState.START);
        Assertions.assertEquals(0, user.getMinExpectedAge());
        Assertions.assertEquals(999, user.getMaxExpectedAge());
    }
    /**
     * Test of sex verification.
     * Tests if there are only two acceptable options.
     */
    @Test
    public void sexVerificationTest(){
        User user = new User("0", "stas");
        String lastSex = user.getSex();
        Assertions.assertFalse(user.setSex("asdfasdf"));
        Assertions.assertEquals(lastSex, user.getSex());
        Assertions.assertTrue(user.setSex("Парень"));
        Assertions.assertEquals("парень", user.getSex());
        Assertions.assertTrue(user.setSex("Девушка"));
        Assertions.assertEquals("девушка", user.getSex());
        lastSex = user.getSex();
        Assertions.assertFalse(user.setSex("asdfasdf"));
        Assertions.assertEquals(lastSex, user.getSex());
    }

    /**
     * Test of age verification.
     * Tests if only a range from 15 to 119 is accepted.
     */
    @Test
    public void ageVerificationTest(){
        User user = new User("0", "stas");
        int lastAge = user.getAge();
        Assertions.assertFalse(user.setAge("14"));
        Assertions.assertFalse(user.setAge("-23"));
        Assertions.assertFalse(user.setAge("120"));
        Assertions.assertEquals(lastAge, user.getAge());
        Assertions.assertTrue(user.setAge("15"));
        Assertions.assertEquals(15, user.getAge());
        Assertions.assertTrue(user.setAge("45"));
        Assertions.assertEquals(45, user.getAge());
        Assertions.assertTrue(user.setAge("119"));
        Assertions.assertEquals(119, user.getAge());
        lastAge = user.getAge();
        Assertions.assertFalse(user.setAge("140"));
        Assertions.assertEquals(lastAge, user.getAge());
    }
}
