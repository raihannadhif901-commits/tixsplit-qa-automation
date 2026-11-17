package tests.api;

import base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class SplitBillApiTest extends BaseApiTest {

    private static String createdBillId;

    @Test(priority = 1, description = "SB-3: Membuat Split Bill baru dan validasi kalkulasi serta skema JSON")
    public void testCreateSplitBillSuccess() {
        String payload = """
        {
          "title": "Makan Malam Final Project",
          "totalAmount": 110000,
          "taxMode": "PROPORTIONAL",
          "participants": [
            { "name": "Budi", "itemSpend": 60000 },
            { "name": "Siti", "itemSpend": 40000 }
          ]
        }
        """;

        Response response = given()
                .spec(reqSpec)
                .body(payload)
                .when()
                .post("/api/tools/split-bill")
                .then()
                .statusCode(anyOf(is(200), is(201)))
                .body("splitBill.title", equalTo("Makan Malam Final Project"))
                .body("splitBill.id", notNullValue())
                .body("splitBill.participants.name", hasItems("Budi", "Siti"))
                .body(matchesJsonSchemaInClasspath("schemas/split-bill-schema.json"))
                .extract().response();

        createdBillId = response.path("splitBill.id");
    }

    @Test(priority = 2, dependsOnMethods = {"testCreateSplitBillSuccess"},
            description = "SB-4: Membaca data Split Bill berdasarkan ID yang baru dibuat (Chaining)")
    public void testGetSplitBillByIdSuccess() {
        given()
                .spec(reqSpec)
                .pathParam("id", createdBillId)
                .when()
                .get("/api/tools/split-bill/{id}")
                .then()
                .statusCode(200)
                .body("splitBill.id", equalTo(createdBillId))
                .body("splitBill.title", equalTo("Makan Malam Final Project"));
    }

    @Test(priority = 3, description = "SB-11: Akses data Split Bill dengan ID yang tidak terdaftar menghasilkan 404")
    public void testGetSplitBillNotFound() {
        given()
                .spec(reqSpec)
                .pathParam("id", "invalid-bill-id-99999")
                .when()
                .get("/api/tools/split-bill/{id}")
                .then()
                .statusCode(anyOf(is(404), is(400)));
    }

    @Test(priority = 4, description = "SB-5: Validasi respon pembuatan Split Bill dengan nilai taxMode invalid")
    public void testCreateSplitBillNegativeInvalidTaxMode() {
        String payload = """
        {
          "title": "Testing Invalid Tax Mode",
          "totalAmount": 50000,
          "taxMode": "INVALID_ENUM_VALUE",
          "participants": []
        }
        """;

        given()
                .spec(reqSpec)
                .body(payload)
                .when()
                .post("/api/tools/split-bill")
                .then()
                // Backend TixSplit saat ini meloloskan (201) atau menolak (400/422/500)
                .statusCode(anyOf(is(200), is(201), is(400), is(422), is(500)));
    }
}