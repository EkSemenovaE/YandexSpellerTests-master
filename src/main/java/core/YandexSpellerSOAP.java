package core;

import core.constants.SoapAction;
import core.constants.TestText;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Arrays;
import java.util.HashMap;

import static core.constants.YandexSpellerConstants.*;

/**
 * Created by yulia_atlasova@epam.com.
 * Describes Yandex Speller SOAP request.
 */
public class YandexSpellerSOAP {

    static RequestSpecification spellerSOAPreqSpec = new RequestSpecBuilder()
            .addHeader("Accept-Encoding", "gzip,deflate")
            .setContentType("text/xml;charset=UTF-8")
            .addHeader("Host", "speller.yandex.net")
            .setBaseUri("http://speller.yandex.net/services/spellservice")
            .build();

    //builder pattern
    private YandexSpellerSOAP(){}

    private HashMap<String, String> params = new HashMap<>();
    private SoapAction action = SoapAction.CHECK_TEXT;

    public static class SOAPBuilder {
        YandexSpellerSOAP soapReq;

        private SOAPBuilder(YandexSpellerSOAP soap) {
            this.soapReq = soap;
        }

        public YandexSpellerSOAP.SOAPBuilder action(SoapAction action){
            soapReq.action = action;
            return this;
        }

        public YandexSpellerSOAP.SOAPBuilder texts(String... text) {
            soapReq.params.put(PARAM_TEXT, Arrays.asList(text).toString());
            return this;
        }

        public YandexSpellerSOAP.SOAPBuilder options(String options) {
            soapReq.params.put(PARAM_OPTIONS, options);
            return this;
        }

        public YandexSpellerSOAP.SOAPBuilder language(Language language) {
            soapReq.params.put(PARAM_LANG, language.languageCode);
            return this;
        }

        public Response callSOAP() {
            String soapBody="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:spel=\"http://speller.yandex.net/services/spellservice\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <spel:" + soapReq.action.getReqName() + " lang=" + QUOTES +
                    (soapReq.params.getOrDefault(PARAM_LANG, "en")) + QUOTES
                    +  " options=" + QUOTES + (soapReq.params.getOrDefault(
                            PARAM_OPTIONS, "0"))+ QUOTES
                    + " format=\"\">\n" +
                    "         <spel:text>"+ (soapReq.params.getOrDefault(
                            PARAM_TEXT, TestText.BROTHER.wrongVer())) + "</spel:text>\n" +
                    "      </spel:"+ soapReq.action.getReqName() + ">\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";


            return RestAssured.with()
                    .spec(spellerSOAPreqSpec)
                    .header("SOAPAction", "http://speller.yandex.net/services/spellservice/" + soapReq.action.getMethod())
                    .body(soapBody)
                    .log().all().with()
                    .post().prettyPeek();
        }
    }


    public static SOAPBuilder with() {
        core.YandexSpellerSOAP soap = new YandexSpellerSOAP();
        return new SOAPBuilder(soap);
    }
}
