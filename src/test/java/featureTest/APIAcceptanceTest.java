package featureTest;

import com.hodorgeek.mt.MoneyTransferApplication;
import com.hodorgeek.mt.dao.GenericDaoIntegrationTest;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import spark.Spark;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/resources/features"})
public class APIAcceptanceTest extends GenericDaoIntegrationTest {

    @BeforeClass
    public static void start() {
        GenericDaoIntegrationTest.init();

        String[] args = {};
        MoneyTransferApplication.main(args);
    }

    @AfterClass
    public static void stop() {
        Spark.stop();
        GenericDaoIntegrationTest.tearDown();
    }
}
