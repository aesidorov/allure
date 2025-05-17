package tests.swaggertests;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import models.swagger.FullUser;
import models.swagger.Info;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.util.List;

import static assertions.Conditions.hasMessage;
import static assertions.Conditions.hasStatusCode;
import static service.RandomTestData.*;

public class UserNewTests {

    private static UserService userService;
    private FullUser user;

    @BeforeEach
    public void initTestUser(){
        user = getRandomUser();
    }

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080/api";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        userService = new UserService();
    }


    @Test
    public void positiveRegisterTest() {
        userService.register(user)
                .should(hasStatusCode(201))
                .should(hasMessage("User created"));
    }

    @Test
    public void positiveRegisterWithGamesTest() {
        Response response = userService.register(user)
                //.should(hasStatusCode(201))
                //.should(hasMessage("User created"));
                .asResponse();

        Info info = response.jsonPath().getObject("info", Info.class);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response
                .statusCode()).as("Статус код не совпадает")
                .isEqualTo(201);

        softAssertions.assertThat(info.getMessage()).as("Инфо не верная")
                .isEqualTo("User created");

        softAssertions.assertAll();
    }

    @Test
    public void negativeRegisterLoginExistTest() {
        userService.register(user);
        userService.register(user)
                .should(hasStatusCode(400))
                .should(hasMessage("Login already exist"));
    }

    @Test
    public void negativeRegisterNoPasswordTest() {
        user.setPass(null);

        userService.register(user)
                .should(hasStatusCode(400))
                .should(hasMessage("Missing login or password"));
    }

    @Test
    public void positiveAdminAuthTest() {
        FullUser user = getAdminUser();
        String token = userService.auth(user)
                .should(hasStatusCode(200))
                .asJwt();
        Assertions.assertNotNull(token);
    }

    @Test
    public void positiveNewUserAuthTest() {
        userService.register(user);

        String token = userService.auth(user)
                .should(hasStatusCode(200))
                .asJwt();
        Assertions.assertNotNull(token);
    }

    @Test
    public void negativeAuthTest() {
        userService.auth(user).should(hasStatusCode(401));
    }

    @Test
    public void positiveUserGetInfoTest() {
        FullUser user = getAdminUser();
        String token = userService.auth(user).asJwt();
        userService.getUserInfo(token).should(hasStatusCode(200));

    }

    @Test
    public void negativeGetUserInfoInvalidJwtTest() {
        userService.getUserInfo("some jwt").should(hasStatusCode(401));
    }

    @Test
    public void negativeGetUserInfoWithoutJwtTest() {
        userService.getUserInfo().should(hasStatusCode(401));
    }

    @Test
    public void positiveUpdatePasswordTest() {
        String oldPass = user.getPass();
        userService.register(user);

        String token = userService.auth(user).asJwt();

        String updatedPassword = "some Pass";

        userService.updatePass(updatedPassword, token)
                .should(hasStatusCode(200))
                .should(hasMessage("User password successfully changed"));

        user.setPass(updatedPassword);
        token = userService.auth(user).asJwt();

        FullUser updUser = userService.getUserInfo(token).as(FullUser.class);

        Assertions.assertNotEquals(oldPass, updUser.getPass());
    }

    @Test
    public void negativeUpdateAdminPasswordTest() {
        FullUser user = getAdminUser();
        String token = userService.auth(user).asJwt();

        String updatedPassword = "newSome Pass";

        userService.updatePass(updatedPassword, token)
                .should(hasStatusCode(400))
                .should(hasMessage("Cant update base users"));
    }

    @Test
    public void negativeDeleteAdminTest() {
        FullUser user = getAdminUser();
        String token = userService.auth(user).asJwt();

        userService.deleteUser(token)
                .should(hasStatusCode(400))
                .should(hasMessage("Cant delete base users"));
    }

    @Test
    public void positiveDeleteUserTest() {
        userService.register(user);

        String token = userService.auth(user).asJwt();

        userService.deleteUser(token)
                .should(hasStatusCode(200))
                .should(hasMessage("User successfully deleted"));
    }

    @Test
    public void positiveGetUserLoginTest() {
        List<String> users = userService.getAllUsers().asList(String.class);
        Assertions.assertTrue(users.size() >= 3);
    }
}
