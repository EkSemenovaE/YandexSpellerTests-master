import beans.YandexSpellerAnswer;
import core.YandexSpellerCheckTextApi;
import core.constants.Options;
import core.constants.TestText;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static core.YandexSpellerCheckTextApi.successResponse;
import static core.constants.Options.*;
import static core.constants.YandexSpellerConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * Created by yulia-atlasova@epam.com.
 */
public class TestYandexSpellerJSON {

    // simple usage of RestAssured library: direct request call and response validations in test.
    @Test
    public void simpleSpellerApiCall() {
        RestAssured
                .given()
                .queryParam(PARAM_TEXT, TestText.MOTHER.wrongVer())
                .params(PARAM_LANG, Language.EN,
                        "CustomParameter", "valueOfParam")
                .accept(ContentType.JSON)
                .auth().basic("abcName", "abcPassword")
                .header("custom header1", "header1.value")
                .and()
                .body("some body payroll")
                .log().everything()
                .get(YandexSpellerCheckTextApi.YANDEX_SPELLER_API_URI)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(allOf(
                        stringContainsInOrder(Arrays.asList(TestText.MOTHER.wrongVer(),
                                TestText.MOTHER.corrVer())),
                        containsString("\"code\":1")))
                .contentType(ContentType.JSON)
                .time(lessThan(20000L)); // Milliseconds
    }

    // different http methods calls
    @Test
    public void spellerApiCallsWithDifferentMethods() {
        //GET //POST //HEAD //OPTIONS
        RestAssured
                .given()
                .param(PARAM_TEXT, TestText.BROTHER.wrongVer())
                .log().everything()
                .patch (YandexSpellerCheckTextApi.YANDEX_SPELLER_API_URI)
                .prettyPeek();

        //DELETE  //PUT  //PATCH
        RestAssured
                .given()
                .param(PARAM_TEXT, TestText.BROTHER.wrongVer())
                .log()
                .everything()
                .delete(YandexSpellerCheckTextApi.YANDEX_SPELLER_API_URI).prettyPeek()
                .then()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .statusLine("HTTP/1.1 405 Method not allowed");
    }


    // use base request and response specifications to form request and validate response.
    @Test
    public void useBaseRequestAndResponseSpecifications() {
        RestAssured
                .given(YandexSpellerCheckTextApi.baseRequestConfiguration())
                .param(PARAM_TEXT, TestText.BROTHER.wrongVer())
                .log().everything()
                .get().prettyPeek()
                .then().specification(successResponse());
    }

    @Test
    public void reachBuilderUsage(){
        YandexSpellerCheckTextApi.with()
                .language(Language.UK)
                .options(computeOptions(IGNORE_DIGITS, IGNORE_URLS, IGNORE_CAPITALIZATION, FIND_REPEAT_WORDS))
                .text(TestText.UK_WORD.wrongVer())
                .callApi()
                .then().specification(successResponse());
    }


    //validate an object we've got in API response
    @Test
    public void validateSpellerAnswerAsAnObject() {
        List<YandexSpellerAnswer> answers =
                YandexSpellerCheckTextApi.getYandexSpellerAnswers(
                        YandexSpellerCheckTextApi .with() .text("motherr fatherr," + TestText.BROTHER.wrongVer()).callApi());
        assertThat("expected number of answers is wrong.", answers.size(), equalTo(3));
        assertThat(answers.get(0).word, equalTo("motherr"));
        assertThat(answers.get(1).word, equalTo("fatherr"));
        assertThat(answers.get(0).s.get(0), equalTo("mother"));
        assertThat(answers.get(1).s.get(0), equalTo("father"));
        assertThat(answers.get(2).s.get(0), equalTo(TestText.BROTHER.corrVer()));
    }


    @Test
    public void optionsValueIgnoreDigits(){
        List<YandexSpellerAnswer> answers =
                YandexSpellerCheckTextApi.getYandexSpellerAnswers(
                        YandexSpellerCheckTextApi.with().
                                text(TestText.EN_WITH_DIGITS.wrongVer())
                                .options(IGNORE_DIGITS.getCode())
                                .callApi());
        assertThat("expected number of answers is wrong.", answers.size(), equalTo(0));
    }

    @Test
    public void optionsIgnoreWrongCapital(){
        List<YandexSpellerAnswer> answers =
                YandexSpellerCheckTextApi.getYandexSpellerAnswers(
                        YandexSpellerCheckTextApi.with().
                                text(TestText.RU_WRONG_CAPITAL.wrongVer())
                                .options(Options.IGNORE_DIGITS.getCode())
                                .callApi());
        assertThat("expected number of answers is wrong.", answers.size(), equalTo(0));
    }

    @Test//(description = "Check English text with Ru language parameter")
    public void checkRusTextWithEngLetters() {
        List<YandexSpellerAnswer> answers =
                YandexSpellerCheckTextApi.getYandexSpellerAnswers(
                        YandexSpellerCheckTextApi.with()
                .text(TestText.MOTHER.wrongVer() + ", " + TestText.RU_WORD.wrongVer())
                .language(Language.RU)
                .callApi()
                                .then()
                                .specification(successResponse())
                                .extract().response());

        // Check that answers size is 1
        assertThat(answers, hasSize(1));
        assertThat(answers.get(0).toString(), containsString(TestText.RU_WORD.corrVer()));

    }
}
