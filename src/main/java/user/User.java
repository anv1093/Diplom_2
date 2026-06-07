package user;

import io.qameta.allure.Step;

import java.util.concurrent.ThreadLocalRandom;

public class User {

    private String email;
    private String password;
    private String name;

    public User(){    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Step("Создание рандомного пользователя")
    public static User random() {
        var random = ThreadLocalRandom.current();
        String randomEmail = "andy" + random.nextInt() + "@testya.ru";
        String randomPassword = "P@ssw0rd" + random.nextInt(100, 999);
        String randomName = "andyName" + random.nextInt();

        return new User(randomEmail, randomPassword, randomName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
