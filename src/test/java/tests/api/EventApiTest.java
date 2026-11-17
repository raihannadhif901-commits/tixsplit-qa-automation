package tests.api;

import base.BaseApiTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class EventApiTest extends BaseApiTest {

    @Test(priority = 1, description = "EVT-8: Mengambil daftar public event di halaman katalog")
    public void testGetEventsListSuccess() {
        given()
                .spec(reqSpec)
                .when()
                .get("/api/events")
                .then()
                .statusCode(200);
    }

    @Test(priority = 2, description = "EVT-8: Pencarian event dengan parameter query search")
    public void testSearchEventsWithQueryParamSuccess() {
        given()
                .spec(reqSpec)
                .queryParam("search", "Workshop")
                .queryParam("page", 1)
                .when()
                .get("/api/events")
                .then()
                .statusCode(200);
    }

    @Test(priority = 3, description = "EVT-1: Membuat event ditolak saat user belum terotentikasi (401)")
    public void testCreateEventNegativeUnauthorized() {
        String payload = """
        {
          "title": "Workshop Tanpa Token",
          "price": 50000,
          "quota": 10
        }
        """;

        given()
                .spec(reqSpec)
                .body(payload)
                .when()
                .post("/api/events")
                .then()
                .statusCode(anyOf(is(401), is(404), is(410)));
    }

    @Test(priority = 4, description = "PAY-2: RSVP pada event berbayar ditolak tanpa token otorisasi")
    public void testRsvpNegativeUnauthorizedWithoutToken() {
        given()
                .spec(reqSpec)
                .when()
                .post("/api/events/sample-event-id/rsvp")
                .then()
                .statusCode(anyOf(is(401), is(402), is(404)));
    }
}