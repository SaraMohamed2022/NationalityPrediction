package getNationality.API;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import net.datafaker.Faker;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;

public class GetNationalityPropabilityNegativeTestCases {

    static RequestSpecification requestSpecification;
    static ResponseSpecification responseSpecBuilder;
    static Faker faker = new Faker();
    public static Properties property;

    @BeforeTest
    public void beforeTest() throws IOException {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://api.nationalize.io/")
                .build();

        property = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "//src//main//java//PropertiesFile.properties");
        property.load(fis);

        responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(anyOf(is(401), is(402), is(422), is(429))).build();
    }

    @Test
    // Defect >> Request exceed the maximum number of names should be processed as per request
    // Therefore the expectation as per the documentation should be
    // 429
    //Too many requests
    //{ "error": "Request limit reached" }
    //While the actual response is
    // 422
    //{"error": "Invalid 'name' parameter"}
    public static void exceeding_15_NamesPerRequest() {
        List<String> names = Arrays.asList(CommonMethods.namesTestData(15));
        String predictedNationalityResponse = given()
                .spec(requestSpecification)
                .when().queryParam("name[]", names)
                .get()
                .then()
                .log().all()
                .assertThat().statusCode(429)
                .extract().response().asString();
        assertEquals(new JsonPath(predictedNationalityResponse).get("error").toString(), "Request limit reached");
    }

    @Test
    public static void unprocessable_Content_MissingName() {
        String predictedNationalityResponse = given()
                .spec(requestSpecification)
                .when()
                .get()
                .then()
                .log().all()
                .assertThat().statusCode(422)
                .extract().response().asString();
        assertEquals(new JsonPath(predictedNationalityResponse).get("error").toString(), "Missing 'name' parameter");
    }

    // Need a business owner communication to clarify what possible values for name to be invalid ?
    @Test
    public static void unprocessable_Content_InvalidName() {

        String predictedNationalityResponse = given()
                .spec(requestSpecification)
                .when().queryParam("name", "")
                .get()
                .then()
                .log().all()
                .assertThat().statusCode(422)
                .extract().response().asString();
        assertEquals(new JsonPath(predictedNationalityResponse).get("error").toString(), "Invalid 'name' parameter");
    }

    @Test
    public static void unauthorized() {
        String postPlaceResponse = given()
                .spec(requestSpecification)
                .when().queryParam("name", "Jason")
                .queryParam("apikey", property.getProperty("unauthorizedApiKey"))
                .get()
                .then()
                .log().all()
                .assertThat().statusCode(401)
                .extract().response().asString();
        assertEquals(new JsonPath(postPlaceResponse).get("error").toString(), "Invalid API key");
    }

    @Test
    public static void exceeding_requestsLimit_101_NamesPerRequest() {
        for (int requests = 1; requests < 101; requests++) {
            given()
                    .spec(requestSpecification)
                    .when().queryParam("name", String.valueOf(faker.name().lastName()))
                    .queryParam("apikey", property.getProperty("newApiKey"))
                    .get()
                    .then();
        }
        String postPlaceResponse = given()
                .spec(requestSpecification)
                .when().queryParam("name", String.valueOf(faker.name().lastName()))
                .queryParam("apikey", property.getProperty("newApiKey"))
                .get()
                .then()
                .assertThat().statusCode(402)
                .extract().response().asString();
        assertEquals(new JsonPath(postPlaceResponse).get("error").toString(), "No active subscription");
    }
}

