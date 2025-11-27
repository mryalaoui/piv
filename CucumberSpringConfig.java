package com.bnpparibas.dsibddf.fis.pvi.cucumber;

import com.bnpparibas.dsibddf.fis.pvi.PviApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(
        classes = PviApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
public class CucumberSpringConfig {
    // Pas de code nécessaire : cette classe sert juste à lier Cucumber ↔ Spring
}
