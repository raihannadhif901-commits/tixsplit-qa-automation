package base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;

public class BaseApiTest {
    protected RequestSpecification reqSpec;
    protected String authToken = "";

    @BeforeClass
    public void setup() {
        // 1. Membaca baseUrl dinamis dari config.properties
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // 2. Set default header JSON untuk seluruh request API
        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }
}