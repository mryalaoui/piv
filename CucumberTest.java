package com.bnpparibas.dsibddf.fis.pvi.cucumber;

import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.ConfigurationParameter;
import io.cucumber.junit.platform.engine.Constants;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = Constants.GLUE_PROPERTY_NAME,
        value = "com.bnpparibas.dsibddf.fis.pvi.cucumber"
)
@ConfigurationParameter(
        key = Constants.PLUGIN_PROPERTY_NAME,
        value = """
                pretty,
                html:target/cucumber-reports/pvi-report.html,
                json:target/cucumber-reports/pvi-report.json,
                junit:target/cucumber-reports/pvi-report.xml
                """
)
public class CucumberTest {
}
