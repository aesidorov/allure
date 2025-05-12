package tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tests.users.Address;
import tests.users.Geolocation;
import tests.users.Name;
import tests.users.StructResponseUser;

import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RestAssured{

    @Test
    public void getAllisers(){
        given().get("https://fakestoreapi.com/users")
                .then()
                .log().all().statusCode(000);
    }

    @Test
    public void getUserTest(){
        int userId = 5;
//        String path = "https://fakestoreapi.com/users";
        given().pathParam("userId", userId).get("https://fakestoreapi.com/users/{userId}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("address.zipcode", matchesPattern("\\d{5}-\\d{4}"));
    }

    @Test
    public void getUsersLimit(){
        int limit = 3;
        given().queryParam("limit", limit)
                .get("https://fakestoreapi.com/users")
                .then()
                .log().all()
                .statusCode(200)
                .body("", hasSize(limit));
    }

    public void getAllUsersSortDescTest(){
        String sortType = "desc";
        Response sortedResponse = given().queryParam("sort", sortType)
                .get("https://fakestoreapi.com/users")
                .then().log().all()
                .extract().response();

        Response notSortedResponse = given().get("https://fakestoreapi.com/users")
                .then().log().all()
                .extract().response();

        List<Integer> sortResponseIds = sortedResponse.jsonPath().getList("id");
        List<Integer> notSortResponseIds = notSortedResponse.jsonPath().getList("id");

        Assertions.assertEquals(sortResponseIds, notSortResponseIds);

        List<Integer> sortedByCode = notSortResponseIds
                .stream().
                sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        Assertions.assertEquals(sortResponseIds, sortedByCode);
    }

    @Test
    public void addUserTest(){
        Name name = new Name();
        Name.builder().lastname("Smolov").firstname("Fedor");
        Geolocation geolocation = new Geolocation("315,65", "95.1");
        Address adress = Address.builder()
                .city("Kasimov")
                .street("Bolshakova")
                .number(55)
                .zipcode("123-123")
                .geolocation(geolocation).build();

        StructResponseUser bodyRequest = StructResponseUser.builder()
                .name(name)
                .email("fgfgdfg")
                .phone("93999999")
                .address(adress)
                .password("21354")
                .username("skdfksjdsdhkfj")
                .build();

        given().body(bodyRequest)
                .post("https://fakestoreapi.com/users")
                .then().log().all()
                .statusCode(200)
                .body("id", notNullValue());
    }

    private StructResponseUser getUserId(){
        Name name = new Name();
        Name.builder().lastname("Smolov").firstname("Fedor");
        Geolocation geolocation = new Geolocation("315,65", "95.1");
        Address adress = Address.builder()
                .city("Kasimov")
                .street("Bolshakova")
                .number(55)
                .zipcode("123-123")
                .geolocation(geolocation).build();

        return StructResponseUser.builder()
                .name(name)
                .email("fgfgdfg")
                .phone("93999999")
                .address(adress)
                .password("21354")
                .username("skdfksjdsdhkfj")
                .build();
    }

    @Test
    public void updateUserTest(){
        StructResponseUser user = getUserId();
        String oldPassword = user.getPassword();

        user.setPassword("new");
        given().body(user)
                .put("https://fakestoreapi.com/users/1")
                .then().log().all()
                .body("password", not(equalTo(oldPassword)));
    }

    @Test
    public void deleteUserTest(){
        given().delete("https://fakestoreapi.com/users/1")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    public void authUserTest(){
        Map<String, String> userAuth = new HashMap<>();
        userAuth.put("username", "johnd");
        userAuth.put("password", "m38rmF$");
        given()
                .contentType(ContentType.JSON)
                .body(userAuth)
                .post("https://fakestoreapi.com/auth/login")
                .then().log().all()
                .statusCode(200)
                .body("token", notNullValue());
    }

}
