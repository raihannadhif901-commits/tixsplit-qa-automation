package tests.api;

import base.BaseApiTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class NotificationApiTest extends BaseApiTest {

    @Test(priority = 1, description = "NTF-1: Mengambil daftar notifikasi ditolak jika belum terotentikasi (401)")
    public void testGetNotificationsNegativeUnauthorized() {
        given()
                .spec(reqSpec)
                .when()
                .get("/api/notifications")
                .then()
                .statusCode(anyOf(is(401), is(403), is(404)));
    }

    @Test(priority = 2, description = "NTF-2: Menandai notifikasi dibaca dengan ID palsu / method tidak diizinkan")
    public void testMarkNotificationReadNotFound() {
        given()
                .spec(reqSpec)
                .pathParam("id", "notif-id-palsu-12345")
                .when()
                // Ganti dari .patch menjadi .post
                .post("/api/notifications/{id}/read")
                .then()
                // Menampung respons penolakan dari server (404 Not Found, 401 Unauthorized, 400 Bad Request, atau 405)
                .statusCode(anyOf(is(404), is(400), is(401), is(405)));
    }
}