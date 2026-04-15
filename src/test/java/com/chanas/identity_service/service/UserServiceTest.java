package com.chanas.identity_service.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.chanas.identity_service.dto.request.UserCreationRequest;
import com.chanas.identity_service.dto.response.UserResponse;
import com.chanas.identity_service.entity.Role;
import com.chanas.identity_service.entity.User;
import com.chanas.identity_service.exception.AppException;
import com.chanas.identity_service.repository.RoleRepository;
import com.chanas.identity_service.repository.UserRepository;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RoleRepository roleRepository;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob;
    private List<Role> mockRoles;

    @BeforeEach
    void initData() {
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
        user = User.builder()
                .id("aebe2fc6-27a2-43eb-aa46-da9c9ba70d68")
                .username("John")
                .firstname("John")
                .lastname("Doe")
                .dob(dob)
                .build();

        Role role = Role.builder().name("USER").description("User role").build();
        mockRoles = List.of(role);
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        when(roleRepository.findAllById(any())).thenReturn(mockRoles);

        // WHEN
        var response = userService.createUser(request);

        // THEN
        Assertions.assertThat(response.getId()).isEqualTo("aebe2fc6-27a2-43eb-aa46-da9c9ba70d68");
        Assertions.assertThat(response.getUsername()).isEqualTo("John");
    }

    @Test
    void createUser_userExisted_fail() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));

        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
    }

    @Test
    @WithMockUser(username = "John")
    void getMyInfo_valid_success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        var response = userService.getMyInfo();

        Assertions.assertThat(response.getUsername()).isEqualTo("John");
        Assertions.assertThat(response.getId()).isEqualTo("aebe2fc6-27a2-43eb-aa46-da9c9ba70d68");
    }

    @Test
    @WithMockUser(username = "John")
    void getMyInfo_userNotFound_error() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.ofNullable(null));

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.getMyInfo());

        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1005);
    }
}
