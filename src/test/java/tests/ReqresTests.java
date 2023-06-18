package tests;

import models.CreateUserBodyModel;
import models.CreateUserResponseModel;
import models.UserResponseModel;
import models.UsersResponseModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.ReqresSpecs.*;

public class ReqresTests {

    String userName = "Evans Smith";
    String userJob = "manager";

    @Test
    void getUserByIdResponseBodyValueTest() {
        int userId = 2;

        UserResponseModel userResponse = step("Perform request to get user by id", () ->
                given(requestSpec)
                        .when()
                        .get("/users/" + userId)
                        .then()
                        .spec(responseCode200Spec)
                        .extract().as(UserResponseModel.class));
        step("Check user id in response", () ->
                assertEquals(userId, userResponse.getUser().getId()));
    }

    @Test
    void getUserByNotExistedIdStatusCodeTest() {
        step("Perform GET user request with non existent id and check status code", () ->
                given(requestSpec)
                        .when()
                        .get("/users/23")
                        .then()
                        .spec(responseCode404Spec));
    }

    @Test
    void getUsersListResponseBodyIdsTest() {
        int pageId = 2;
        List<Integer> expectedIds = List.of(7, 8, 9, 10, 11, 12);

        UsersResponseModel usersResponse = step("Perform request to get list of users on the specified page", () ->
                given(requestSpec)
                        .when()
                        .get("/users?page=" + pageId)
                        .then()
                        .spec(responseCode200Spec)
                        .extract().as(UsersResponseModel.class));

        step("Check page number in response", () ->
                assertEquals(pageId, usersResponse.getPage()));

        step("Check user ids in response", () -> {
            ArrayList<Integer> responseIds = new ArrayList<>();
            usersResponse.getUsers().forEach((k) -> responseIds.add(k.getId()));
            assertEquals(expectedIds, responseIds);
        });
    }

    @Test
    void postUserResponseBodyValuesTest() {
        CreateUserBodyModel requestBody = new CreateUserBodyModel();
        requestBody.setName(userName);
        requestBody.setJob(userJob);

        CreateUserResponseModel createUserResponse = step("Perform request to create user", () ->
                given(requestSpec)
                        .body(requestBody)
                        .when()
                        .post("/users")
                        .then()
                        .spec(responseCode201Spec)
                        .extract().as(CreateUserResponseModel.class));

        step("Check user data in response", () -> {
            assertEquals(userName, createUserResponse.getName());
            assertEquals(userJob, createUserResponse.getJob());
        });
    }

    @Test
    void getResourceByIdResponseJsonSchemaTest() {
        step("Perform GET resource by id request and check  response body for conformance to scheme", () ->
                given(requestSpec)
                        .when()
                        .get("/unknown/2")
                        .then()
                        .spec(responseCode200Spec)
                        .body(matchesJsonSchemaInClasspath("schemes/single-resource-byid-response-scheme.json")));
    }
}