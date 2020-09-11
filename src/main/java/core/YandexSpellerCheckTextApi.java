package core;

import beans.YandexSpellerAnswer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.constants.YandexSpellerConstants;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.lessThan;

/**
 * Created by yulia_atlasova@epam.com
 * Describes Yandex Speller REST API
 */
public class YandexSpellerCheckTextApi {


    //builder pattern
    private YandexSpellerCheckTextApi() {}

    public static final String YANDEX_SPELLER_API_URI =
            "https://speller.yandex.net/services/spellservice.json/checkText";

    private HashMap<String, String> params = new HashMap<String, String>();

    public static class ApiBuilder {
        YandexSpellerCheckTextApi spellerApi;

        private ApiBuilder(YandexSpellerCheckTextApi gcApi) {
            spellerApi = gcApi;
        }

        public ApiBuilder text(String text) {
            spellerApi.params.put(YandexSpellerConstants.PARAM_TEXT, text);
            return this;
        }

        public ApiBuilder options(String options) {
            spellerApi.params.put(YandexSpellerConstants.PARAM_OPTIONS, options);
            return this;
        }

        public ApiBuilder language(YandexSpellerConstants.Language language) {
            spellerApi.params.put(YandexSpellerConstants.PARAM_LANG, language.languageCode);
            return this;
        }

        public Response callApi() {
            return RestAssured.with()
                    .queryParams(spellerApi.params)
                    .log().all()
                    .get(YANDEX_SPELLER_API_URI).prettyPeek();
        }
    }

    public static ApiBuilder with() {
        YandexSpellerCheckTextApi api = new YandexSpellerCheckTextApi();
        return new ApiBuilder(api);
    }

    //get ready Speller answers list form api response
    public static List<YandexSpellerAnswer> getYandexSpellerAnswers(Response response){
        return new Gson().fromJson( response.asString().trim(), new TypeToken<List<YandexSpellerAnswer>>() {}.getType());
    }

    //set base request and response specifications tu use in tests
    public static ResponseSpecification successResponse(){
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectHeader("Connection", "keep-alive")
                .expectResponseTime(lessThan(20000L))
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static RequestSpecification baseRequestConfiguration(){
        return new RequestSpecBuilder()
                .setAccept(ContentType.XML)
                .setRelaxedHTTPSValidation()

                .addHeader("custom header343434", "header343434342.value")
                .addQueryParam("requestID", new Random().nextLong())
                .setBaseUri(YANDEX_SPELLER_API_URI)
                .build();
    }
}
