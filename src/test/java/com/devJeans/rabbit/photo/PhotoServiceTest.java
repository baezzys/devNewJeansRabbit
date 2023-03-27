package com.devJeans.rabbit;

import com.amazonaws.services.s3.AmazonS3;
import com.devJeans.rabbit.repository.PhotoRepository;
import com.devJeans.rabbit.service.AccountService;
import com.devJeans.rabbit.service.PhotoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@DataJpaTest(
        includeFilters = {@ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {PhotoService.class}
        )}
)
@Testcontainers
public class PhotoServiceTest {


    @Autowired
    PhotoService photoService;

    @Autowired
    PhotoRepository photoRepository;

    @MockBean
    AmazonS3 s3client;

    @MockBean
    AccountService accountService;


    @Test
    void test() {
        System.out.println("test");
    }
}
