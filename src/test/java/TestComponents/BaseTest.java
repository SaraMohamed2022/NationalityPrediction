package TestComponents;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeTest;

public class BaseTest {
    static RequestSpecification requestSpecification;

    @BeforeTest
    public void beforeTest() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://api.nationalize.io/")
                .build();
    }
}
