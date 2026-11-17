package tests.api;

import base.BaseApiTest;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthApiTest extends BaseApiTest {

    @Test(priority = 1, description = "AUTH-1: Registrasi user baru dengan payload valid")
    public void testRegisterSuccess() {
        String uniqueEmail = "user_" + System.currentTimeMillis() + "@mail.com";
        String payload = String.format("""
        {
          "name": "QA Student",
          "email": "%s",
          "password": "Password123!"
        }
        """, uniqueEmail);

        given()
                .spec(reqSpec)
                .body(payload)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .body("message", anyOf(nullValue(), notNullValue()));
    }

    @Test(priority = 2, description = "AUTH-1: Registrasi gagal saat payload kosong (Validasi Error)")
    public void testRegisterNegativeEmptyPayload() {
        given()
                .spec(reqSpec)
                .body("{}")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(anyOf(is(400), is(422)));
    }

    @Test(priority = 3, description = "AUTH-1: Registrasi ditolak saat email sudah terdaftar (Conflict)")
    public void testRegisterNegativeDuplicateEmail() {
        String duplicateEmail = "dup_" + System.currentTimeMillis() + "@mail.com";
        String payload = String.format("""
        {
          "name": "User Duplicate",
          "email": "%s",
          "password": "Password123!"
        }
        """, duplicateEmail);

        // Register pertama
        given().spec(reqSpec).body(payload).when().post("/api/auth/register").then().statusCode(201);

        // Register kedua dengan email yang persis sama
        given().spec(reqSpec).body(payload).when().post("/api/auth/register").then().statusCode(409);
    }

    @Test(priority = 4, description = "AUTH-1: Endpoint registrasi menolak HTTP Method GET")
    public void testRegisterNegativeMethodNotAllowed() {
        given()
                .spec(reqSpec)
                .when()
                .get("/api/auth/register")
                .then()
                .statusCode(anyOf(is(405), is(404)));
    }

    @Test(priority = 5, description = "AUTH-2: Login gagal saat password salah")
    public void testLoginNegativeInvalidCredentials() {
        String payload = """
        {
          "email": "user@mail.com",
          "password": "WrongPassword999!"
        }
        """;

        given()
                .spec(reqSpec)
                .body(payload)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(anyOf(is(401), is(400)));
    }
}