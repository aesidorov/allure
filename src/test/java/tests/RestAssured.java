package tests;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

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
}
