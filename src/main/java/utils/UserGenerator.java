package utils;

import model.User;
import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {

    public static User getRandom() {
        String email = RandomStringUtils.randomAlphanumeric(8).toLowerCase() + "@local.net";
        String password = RandomStringUtils.randomAlphanumeric(8);
        String name = RandomStringUtils.randomAlphabetic(6);
        return new User(email, password, name);
    }
}
