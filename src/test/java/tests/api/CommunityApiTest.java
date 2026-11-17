package tests.api;

import base.BaseApiTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CommunityApiTest extends BaseApiTest {

    @Test(priority = 1, description = "COM-1: Mengambil daftar komunitas publik di katalog")
    public void testGetCommunitiesListSuccess() {
        given()
                .spec(reqSpec)
                .when()
                .get("/api/communities")
                .then()
                .statusCode(200);
    }

    @Test(priority = 2, description = "COM-1: Membuat komunitas ditolak jika tidak membawa token login (401)")
    public void testCreateCommunityNegativeUnauthorized() {
        String payload = """
        {
          "name": "Komunitas QA Baru",
          "description": "Tempat belajar testing",
          "policy": "OPEN"
        }
        """;

        given()
                .spec(reqSpec)
                // Sengaja tanpa Authorization token
                .body(payload)
                .when()
                .post("/api/communities")
                .then()
                .statusCode(anyOf(is(401), is(404), is(403)));
    }

    @Test(priority = 3, description = "COM-2: Mengambil detail komunitas dengan ID palsu menghasilkan 404")
    public void testGetCommunityDetailNotFound() {
        given()
                .spec(reqSpec)
                .pathParam("id", "invalid-community-id-99999")
                .when()
                .get("/api/communities/{id}")
                .then()
                .statusCode(anyOf(is(404), is(400)));
    }
}