package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tests.users.Address;
import tests.users.Geolocation;
import tests.users.Name;
import tests.users.StructResponseUser;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class refactoredTests {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://fakestoreapi.com";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(),
        new AllureRestAssured());
    }

    @Test
    public void getAllisers() {
        given().get("/users")
                .then()
                .statusCode(200);
    }


    public StructResponseUser getUserTest() {
        int userId = 5;
        StructResponseUser response = given().
                pathParam("userId", userId).get("/users/{userId}")
                .then()
                .statusCode(200)
                .extract().as(StructResponseUser.class);

        Assertions.assertEquals(userId, response.getId());
        Assertions.assertTrue(response.getAddress().getZipcode().matches("\\d{5}-\\d{4}"));
        return response;
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 20})
    public void getUsersLimit(int limitSize) {
        List<StructResponseUser> users = given().queryParam("limit", limitSize)
                .get("/users")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<List<StructResponseUser>>() {
                });


        Assertions.assertEquals(limitSize, users.size());
    }

    @Test
    public void getAllUsersSortDescTest() {
        String sortType = "desc";
        List<StructResponseUser> usersSorted = given()
                .queryParam("sort", sortType)
                .get("/users")
                .then()
                .extract().as(new TypeRef<List<StructResponseUser>>() {
                });

        List<StructResponseUser> usersNotSorted = given()
                .get("/users")
                .then()
                .extract().as(new TypeRef<List<StructResponseUser>>() {
                });

        List<Integer> sortResponseIds = usersSorted.stream()
                .map(StructResponseUser::getId)
                .collect(Collectors.toList());

        List<Integer> sortByCode = usersNotSorted.stream()
                .map(StructResponseUser::getId)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        Assertions.assertNotEquals(usersSorted, usersNotSorted);
        Assertions.assertEquals(sortResponseIds, sortByCode);
    }

    @Test
    public void addUserTest() {
        StructResponseUser user = getUserTest();

        Integer userID = given().body(user)
                .post("/users")
                .then()
                .statusCode(200)
                .extract().jsonPath().getInt("id");

        Assertions.assertNotNull(userID);
    }

    @Test
    public void updateUserTest() {
        StructResponseUser user = getUserId();
        String oldPassword = user.getPassword();
        user.setPassword("new");

        StructResponseUser updatedUser = given()
                .body(user)
                .put("/users/1")
                .then()
                .extract().as(StructResponseUser.class);

        Assertions.assertNotEquals(oldPassword, updatedUser.getPassword());
    }

    private StructResponseUser getUserId() {
        Random random = new Random();
        Name name = new Name();
        Name.builder().lastname("Smolov").firstname("Fedor");
        Geolocation geolocation = new Geolocation("315,65", "95.1");
        Address adress = Address.builder()
                .city("Kasimov")
                .street("Bolshakova")
                .number(random.nextInt(10))
                .zipcode("123-123")
                .geolocation(geolocation).build();

        return StructResponseUser.builder()
                .name(name)
                .email("fgfgdfg")
                .phone(String.valueOf(random.nextInt(10)))
                .address(adress)
                .password(random.toString())
                .username(random.toString())
                .build();
    }

    @Test
    public void authUserTest() {
        Map<String, String> userAuth = new HashMap<>();
        userAuth.put("username", "johnd");
        userAuth.put("password", "m38rmF$");
        String token = given()
                .contentType(ContentType.JSON)
                .body(userAuth)
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
    }


}
