package com.chanas.identity_service.controller;

import com.chanas.identity_service.dto.request.UserCreationRequest;
import com.chanas.identity_service.dto.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerIntegrationTest {
    @Container
    static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:latest");

//    @DynamicPropertySource
//    static void configureDatasource(DynamicPropertyRegistry registry){
//        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
//        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
//        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
//        registry.add("spring.datasource.driver-class-name", ()-> "com.mysql.cj.jdbc.Driver");
//        registry.add("spring.jpa.hibernate.ddl-auto", ()-> "update");
//    }

    @Autowired
    private MockMvc mockMvc;
    private UserCreationRequest request;
    private UserResponse userResponse;
    private LocalDate dob;

    @BeforeEach
    void initData(){
        dob = LocalDate.of(1990, 1, 1);

        request = UserCreationRequest.builder()
                .username("John")
                .firstname("John")
                .lastname("Doe")
                .password("12345678")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("aebe2fc6-27a2-43eb-aa46-da9c9ba70d68")
                .username("John")
                .firstname("John")
                .lastname("Doe")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        //GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        //WHEN, THEN
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/users")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(content))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
//                .andExpect(MockMvcResultMatchers.jsonPath("result.id").exists())
//                .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("John"))
//                .andExpect(MockMvcResultMatchers.jsonPath("result.firstname").value("John"));
    }


}
