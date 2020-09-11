import core.YandexSpellerSOAP;
import core.constants.Options;
import core.constants.SoapAction;
import core.constants.TestText;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;

import static core.constants.YandexSpellerConstants.*;

/**
 * Created by yulia_atlasova@epam.com.
 * try to test SOAP via RestAssured
 */
public class TestYandexSpellerSOAP {

    @Test
    public void simpleCall(){
                YandexSpellerSOAP
                        .with().texts(TestText.BROTHER.wrongVer())
                        .callSOAP()
                        .then()
                        .body(Matchers.stringContainsInOrder
                                (Arrays.asList(TestText.BROTHER.wrongVer(),
                                        TestText.BROTHER.corrVer())));
    }

    @Test
    public void useRequestBuilderToChangeParams(){
        YandexSpellerSOAP.with()
                .language(Language.EN)
                .texts(TestText.MOTHER.wrongVer(), TestText.BROTHER.corrVer())
                .options(Options.computeOptions(Options.IGNORE_DIGITS, Options.FIND_REPEAT_WORDS))
                .action(SoapAction.CHECK_TEXTS)
                .callSOAP()
                .then()
                .body(Matchers.stringContainsInOrder
                        (Arrays.asList(TestText.MOTHER.wrongVer(),
                                TestText.MOTHER.corrVer())),
                        Matchers.not(Matchers.containsString(TestText.BROTHER.corrVer())));

    }
}
