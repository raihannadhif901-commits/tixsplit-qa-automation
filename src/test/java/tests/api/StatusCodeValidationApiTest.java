package tests.api;

import base.BaseApiTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class StatusCodeValidationApiTest extends BaseApiTest {

    // ==========================================================
    // 1. STATUS 400: BAD REQUEST (Payload Kosong Tanpa Field)
    // ==========================================================
    @Test(priority = 1, description = "AUTH-1: Register dengan body kosong harus return 400 Bad Request")
    public void testStatus400_BadRequest_EmptyBody() {
        String emptyPayload = "{}";

        given()
                .spec(reqSpec)
                .body(emptyPayload)
                .when()
                .post("/api/auth/register")
                .then()
                // Server wajib menolak jika nama/email/password tidak dikirim
                .statusCode(anyOf(is(400), is(422)));
    }

    // ==========================================================
    // 2. STATUS 401: UNAUTHORIZED (Akses Endpoint Private Tanpa Token)
    // ==========================================================
    @Test(priority = 2, description = "AUTH-2: Akses RSVP tanpa token login harus return 401 Unauthorized")
    public void testStatus401_Unauthorized_NoToken() {
        given()
                .spec(reqSpec)
                // Sengaja TIDAK mengirim token Bearer
                .when()
                .post("/api/events/any-event-id/rsvp")
                .then()
                .statusCode(401);
    }

    // ==========================================================
    // 3. STATUS 404: NOT FOUND (ID Resource Tidak Ditemukan)
    // ==========================================================
    @Test(priority = 3, description = "SB-11: Mengambil Split Bill dengan ID palsu harus return 404 Not Found")
    public void testStatus404_NotFound_InvalidId() {
        given()
                .spec(reqSpec)
                .pathParam("id", "id-palsu-tidak-ada-99999")
                .when()
                .get("/api/tools/split-bill/{id}")
                .then()
                .statusCode(anyOf(is(404), is(400)));
    }

    // ==========================================================
    // 4. STATUS 405: METHOD NOT ALLOWED (Method HTTP Salah)
    // ==========================================================
    @Test(priority = 4, description = "AUTH-1: Mengirim method GET pada endpoint Register harus return 405")
    public void testStatus405_MethodNotAllowed() {
        given()
                .spec(reqSpec)
                .when()
                // Register hanya menerima POST, jika di-GET harus 405
                .get("/api/auth/register")
                .then()
                .statusCode(anyOf(is(405), is(404)));
    }

    // ==========================================================
    // 5. STATUS 409: CONFLICT (Daftar dengan Email Duplikat)
    // ==========================================================
    @Test(priority = 5, description = "AUTH-1: Register dengan email yang sama harus return 409 Conflict")
    public void testStatus409_Conflict_DuplicateEmail() {
        // Buat email unik berdasarkan timestamp agar selalu konsisten
        String duplicateEmail = "user_" + System.currentTimeMillis() + "@mail.com";
        String payload = String.format("""
        {
          "name": "User Duplikat",
          "email": "%s",
          "password": "Password123!"
        }
        """, duplicateEmail);

        // Langkah 1: Daftarkan pertama kali (Harus sukses 201)
        given()
                .spec(reqSpec)
                .body(payload)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201);

        // Langkah 2: Daftarkan lagi dengan email yang SAMA PERSIS (Harus Ditolak 409)
        given()
                .spec(reqSpec)
                .body(payload)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(409);
    }
}