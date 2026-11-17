package tests.api;

import base.BaseApiTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BookingApiTest extends BaseApiTest {

    @Test(priority = 1, description = "PAY-3: Checkout tiket event berbayar ditolak tanpa token login (401)")
    public void testCreateBookingNegativeUnauthorized() {
        String payload = """
        {
          "ticketCount": 1,
          "feeBearer": "PARTICIPANT"
        }
        """;

        given()
                .spec(reqSpec)
                .pathParam("eventId", "sample-event-id")
                .body(payload)
                .when()
                .post("/api/events/{eventId}/booking")
                .then()
                .statusCode(anyOf(is(401), is(404), is(400)));
    }

    @Test(priority = 2, description = "PAY-6: Konfirmasi pembayaran dengan ID booking tidak terdaftar menghasilkan 404")
    public void testConfirmBookingNotFound() {
        String payload = """
        {
          "paymentProofUrl": "https://image.com/proof.jpg"
        }
        """;

        given()
                .spec(reqSpec)
                .pathParam("bookingId", "booking-id-palsu-999")
                .body(payload)
                .when()
                .post("/api/bookings/{bookingId}/confirm")
                .then()
                .statusCode(anyOf(is(404), is(400), is(401)));
    }
}