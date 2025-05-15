package tests.swaggertests;

import assertions.AssertableResponse;
import assertions.Condition;
import assertions.Conditions;
import assertions.conditions.GenericAssertableResponse;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.swagger.FullUser;
import models.swagger.Info;
import models.swagger.JwtAuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static assertions.Conditions.hasMessage;
import static assertions.Conditions.hasStatusCode;
import static io.restassured.RestAssured.given;

public class UserTests {

    private static Random random;

    @BeforeAll
    public static void setUp(){
        RestAssured.baseURI = "http://85.192.34.140:8080/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        random = new Random();
    }

    @Test
    public void positiveRegisterTest(){
        int randomNumber = Math.abs(random.nextInt());
        FullUser user = FullUser.builder()
                .login("loginTestUser" + randomNumber)
                .pass("passTestUser")
                .build();

        Info info = given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then().statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("User created", info.getMessage());
    }

    @Test
    public void negativeRegisterLoginExistTest(){
        int randomNumber = Math.abs(random.nextInt());
        FullUser user = FullUser.builder()
                .login("loginTestUser" + randomNumber)
                .pass("passTestUser")
                .build();

        Info info = given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then().statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("User created", info.getMessage());

        Info errorInfo = given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then().statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("Login already exist", errorInfo.getMessage());
    }

    @Test
    public void negativeRegisterNoPasswordTest(){
        int randomNumber = Math.abs(random.nextInt());
        FullUser user = FullUser.builder()
                .login("loginTestUser" + randomNumber)
                .build();

        Info errorInfo = given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then().statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);

        new AssertableResponse(given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then()).should(hasMessage("Missing login or password"))
                .should(hasStatusCode(400));

        new GenericAssertableResponse<Info>(given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then(), new TypeRef<Info>() {})
                .should(hasMessage("Missing login or password"))
                .should(hasStatusCode(400));

        Assertions.assertEquals("Missing login or password", errorInfo.getMessage());
    }

    @Test
    public void positiveAdminAuthTest(){
        JwtAuthData authData = new JwtAuthData("admin", "admin");

        String token = given().contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
    }

    @Test
    public void positiveNewUserAuthTest(){
        int randomNumber = Math.abs(random.nextInt());
        FullUser user = FullUser.builder()
                .login("loginTestUser" + randomNumber)
                .pass("passTestUser")
                .build();
        Info info = given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then().statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);
        Assertions.assertEquals("User created", info.getMessage());



        JwtAuthData authData = new JwtAuthData(user.getLogin(), user.getPass());

        String token = given().contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
    }

    @Test
    public void negativeAuthTest(){
        JwtAuthData authData = new JwtAuthData("skdhfshdfshd;fhsdf564657", "jsshdfjshgdjfhsglsdghfsdf6547");

        given().contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then().statusCode(401);
    }

    @Test
    public void positiveUserGetInfoTest(){
        JwtAuthData authData = new JwtAuthData("admin", "admin");

        String token = given().contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("token");
        Assertions.assertNotNull(token);

        given().auth().oauth2(token)
                .get("/api/user")
                .then().statusCode(200);
    }

    @Test
    public void negativeGetUserInfoInvalidJwtTest(){
        given().auth().oauth2("some values")
                .get("/api/user")
                .then().statusCode(401);
    }

    @Test
    public void negativeGetUserInfoWithoutJwtTest(){
        given()
                .get("/api/user")
                .then().statusCode(401);
    }

    @Test
    public void positiveUpdatePasswordTest(){
        int randomNumber = Math.abs(random.nextInt());
        FullUser user = FullUser.builder()
                .login("loginTestUser" + randomNumber)
                .pass("passTestUser")
                .build();
        Info info = given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then().statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);
        Assertions.assertEquals("User created", info.getMessage());

        JwtAuthData authData = new JwtAuthData(user.getLogin(), user.getPass());

        String token = given().contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);

        Map<String, String> password = new HashMap<>();
        String updatedPassword = "newPassword";
        password.put("password", updatedPassword);

        Info infoUpdPassword = given().contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(password)
                .put("/api/user")
                .then().extract().jsonPath().getObject("info", Info.class);
        Assertions.assertEquals("User password successfully changed", infoUpdPassword.getMessage());

        authData.setPassword(updatedPassword);
        token = given().contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("token");

        FullUser updUser = given().auth().oauth2(token)
                .get("/api/user")
                .then().statusCode(200)
                .extract().as(FullUser.class);

        Assertions.assertNotEquals(user.getPass(), updUser.getPass());
    }

    @Test
    public void negativeUpdateAdminPasswordTest(){
        JwtAuthData authData = new JwtAuthData("admin", "admin");
        String token = given().contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("token");
        Assertions.assertNotNull(token);

        Map<String, String> password = new HashMap<>();
        String updatedPassword = "newPassword";
        password.put("password", updatedPassword);

        Info infoUpdPassword = given().contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(password)
                .put("/api/user")
                .then().statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);
        Assertions.assertEquals("Cant update base users", infoUpdPassword.getMessage());
    }

    @Test
    public void negativeDeleteAdminTest(){
        JwtAuthData authData = new JwtAuthData("admin", "admin");
        String token = given().contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("token");

        Info infoDeleteAdmin = given()
                .auth().oauth2(token)
                .delete("/api/user")
                .then().statusCode(400)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("Cant delete base users", infoDeleteAdmin.getMessage());
    }

    @Test
    public void positiveDeleteUserTest(){
        int randomNumber = Math.abs(random.nextInt());
        FullUser user = FullUser.builder()
                .login("loginTestUser" + randomNumber)
                .pass("passTestUser")
                .build();
        Info info = given().contentType(ContentType.JSON)
                .body(user)
                .post("/api/signup")
                .then().statusCode(201)
                .extract().jsonPath().getObject("info", Info.class);
        Assertions.assertEquals("User created", info.getMessage());

        JwtAuthData authData = new JwtAuthData(user.getLogin(), user.getPass());
        String token = given().contentType(ContentType.JSON)
                .body(authData)
                .post("/api/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("token");
        Assertions.assertNotNull(token);

        Info infoDeleteUser = given()
                .auth().oauth2(token)
                .delete("/api/user")
                .then().statusCode(200)
                .extract().jsonPath().getObject("info", Info.class);

        Assertions.assertEquals("User successfully deleted", infoDeleteUser.getMessage());
    }

    @Test
    public void positiveGetUserLoginTest(){
        List<String> users = given().get("/api/users")
                .then().statusCode(200)
                .extract().as(new TypeRef<List<String>>() {});

        Assertions.assertTrue(users.size() >= 3);
    }


}
