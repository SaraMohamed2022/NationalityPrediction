package getNationality.API;

import net.datafaker.Faker;
import serilization.NationalityPredectionResponse;

import java.util.List;

public class CommonMethods {
    static Faker faker = new Faker();

    public static String[] namesTestData(int numberOfNames) {
        String[] generatedNames = new String[numberOfNames];
        for (int i = 0; i < numberOfNames; i++)
            generatedNames[i] = String.valueOf(faker.name().lastName());
        return generatedNames;
    }

    public static boolean numberOfNationalitiesPredicted(List<NationalityPredectionResponse> predictedNationalityList) {
        boolean checkPassed = true;
        for (NationalityPredectionResponse perName : predictedNationalityList) {
            //asserting that each name has maximum 5 list of predicted nationalities
            checkPassed = checkPassed && (perName.getCountry().size() < 6);
        }
        return checkPassed;
    }


    public static boolean allNamesProcessed(List<NationalityPredectionResponse> predictedNationalityList, List<String> testedNames) {
        int requestedName = 0;
        boolean allNamesProcessed = true;
        for (NationalityPredectionResponse perName : predictedNationalityList) {    // asserting that each requested name is processed successfully
            allNamesProcessed = allNamesProcessed && (perName.getName().equals(testedNames.get(requestedName)));
            requestedName++;
        }
        return allNamesProcessed;
    }
}

