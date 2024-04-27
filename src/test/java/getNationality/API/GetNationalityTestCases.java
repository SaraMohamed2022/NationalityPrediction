package getNationality.API;

import io.restassured.builder.*;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;
import serilization.NationalityPredectionResponse;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static getNationality.API.CommonMethods.*;
import static getNationality.API.CommonMethods.faker;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import io.restassured.module.jsv.JsonSchemaValidator;

public class GetNationalityTestCases {

    static RequestSpecification requestSpecification;
    static ResponseSpecBuilder responseSpecBuilder;

    @BeforeTest
    public void beforeTest() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://api.nationalize.io/")
                .build();

        responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(anyOf(is(200), is(201)));
        responseSpecBuilder.expectBody(JsonSchemaValidator.matchesJsonSchemaInClasspath("GetNationalityAPISchema.json")); // Validate response body against JSON schema
    }

    @Test
    public static void positiveTest_OneLastName() {
        String testedName = String.valueOf(faker.name().lastName());
        NationalityPredectionResponse predictedNationalities = given()
                .spec(requestSpecification)
                .when().queryParam("name", testedName)
                .get()
                .then()
                .log().all()
                .assertThat().spec(responseSpecBuilder.build())
                .extract().response().as(NationalityPredectionResponse.class);

        assertEquals(predictedNationalities.getName(), testedName);
        assertTrue(predictedNationalities.getCountry().size() < 6);
    }


    @Test
    public static void positiveTestName_with_diacritics() throws UnsupportedEncodingException {

        NationalityPredectionResponse nameWithoutDiacriticsResponse = given()
                .spec(requestSpecification)
                .when().queryParam("name", "johnson")
                .get()
                .then()
                .log().all()
                .assertThat().spec(responseSpecBuilder.build())
                .extract().response().as(NationalityPredectionResponse.class);

        NationalityPredectionResponse nameWithDiacriticsResponse = given()
                .spec(requestSpecification)
                .when().queryParam("name", "jöhnśon")
                .get()
                .then()
                .log().all()
                .assertThat().spec(responseSpecBuilder.build())
                .extract().response().as(NationalityPredectionResponse.class);
        assertEquals(nameWithoutDiacriticsResponse.getCountry().toString(), nameWithDiacriticsResponse.getCountry().toString());
    }

    @Test
    /*
        Defect detected
        The documentation mentioned that ( It's recommended to always use a last name if you have it available. If not, the API will attempt to parse the input as a full name and pick out the first name.)
        while by the upcoming scenario it detects that parsing a full name is not as sending only last name
     */
    public static void positiveTest_FirstAndLastName() {

        /* Test Scenario
        call the API with the full name >> get the response
        call the API with only the first name >> get the response
        compare the two responses >> should match each others
        */
        NationalityPredectionResponse fullNameResponse = given()
                .spec(requestSpecification)
                .when().queryParam("name", "John Jacklen")
                .get()
                .then()
                .log().all()
                .assertThat().spec(responseSpecBuilder.build())
                .extract().response().as(NationalityPredectionResponse.class);

        NationalityPredectionResponse lastNameResponse = given()
                .spec(requestSpecification)
                .when().queryParam("name", "Jacklen")
                .get()
                .then()
                .log().all()
                .assertThat().spec(responseSpecBuilder.build())
                .extract().response().as(NationalityPredectionResponse.class);
        assertEquals(fullNameResponse.getCountry().toString(), lastNameResponse.getCountry().toString());

    }

    @Test
    public static void positiveTest_5_NamesPerRequest() {

        List<String> names = Arrays.asList(namesTestData(5));
        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://api.nationalize.io/")
                .build();

        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(anyOf(is(200), is(201)));
        responseSpecBuilder.expectBody(JsonSchemaValidator.matchesJsonSchemaInClasspath("GetNationalityAPISchema.json")); // Validate response body against JSON schema

        List<NationalityPredectionResponse> predictedNationalityList = given()
                .spec(requestSpecification)
                .when().queryParam("name[]", names)
                .get()
                .then().spec(responseSpecBuilder.build())
                .extract().response().jsonPath().getList(".", NationalityPredectionResponse.class);
        assertTrue(allNamesProcessed(predictedNationalityList, names));
        assertTrue(numberOfNationalitiesPredicted(predictedNationalityList));
    }

    @Test
    public static void positiveTest_10_NamesPerRequest() {
        List<String> names = Arrays.asList(namesTestData(10));

        List<NationalityPredectionResponse> predictedNationalityList = given()
                .spec(requestSpecification)
                .when().queryParam("name[]", names)
                .get()
                .then().log().all()
                .spec(responseSpecBuilder.build())
                .extract().response().jsonPath().getList(".", NationalityPredectionResponse.class);
        assertTrue(allNamesProcessed(predictedNationalityList, names));
        assertTrue(numberOfNationalitiesPredicted(predictedNationalityList));
    }
}

