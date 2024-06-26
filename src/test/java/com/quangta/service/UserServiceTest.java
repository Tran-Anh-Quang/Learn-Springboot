package com.quangta.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import com.quangta.dto.request.UserCreationRequest;
import com.quangta.dto.response.UserResponse;
import com.quangta.entity.User;
import com.quangta.exception.AppException;
import com.quangta.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean // mock to user repository
    private UserRepository userRepository;

    private UserCreationRequest request;
    private UserResponse response;
    private User user;

    @BeforeEach
    public void initData() {
        LocalDate dob = LocalDate.of(2000, 1, 1);

        request = UserCreationRequest.builder()
                .username("test02")
                .firstName("quang")
                .lastName("tran")
                .email("test02@gmail.com")
                .password("Quang09122002@")
                .dob(dob)
                .phoneNumber("0563016466")
                .build();

        response = UserResponse.builder()
                .id("42e2-bae5-9ea7c0f1c4d4")
                .username("test02")
                .firstName("quang")
                .lastName("tran")
                .email("test02@gmail.com")
                .dob(dob)
                .phoneNumber("0563016466")
                .build();

        user = User.builder()
                .id("42e2-bae5-9ea7c0f1c4d4")
                .username("test02")
                .firstName("quang")
                .lastName("tran")
                .email("test02@gmail.com")
                .dob(dob)
                .phoneNumber("0563016466")
                .build();
    }

    @Test
    public void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        // WHEN
        var response = userService.createUser(request);

        // THEN
        Assertions.assertThat(response.getId()).isEqualTo("42e2-bae5-9ea7c0f1c4d4");
        Assertions.assertThat(response.getPhoneNumber()).isEqualTo("0563016466");
    }

    @Test
    public void createUser_invalidRequest_fail() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));

        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1001);
    }

    @Test
    @WithMockUser(
            username = "test02",
            roles = {"USER"})
    public void getProfile_validRequest_success() {
        // GIVEN
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        // WHEN
        var response = userService.getProfile();

        // THEN
        Assertions.assertThat(response.getUsername()).isEqualTo("test02");
        Assertions.assertThat(response.getId()).isEqualTo("42e2-bae5-9ea7c0f1c4d4");
    }

    @Test
    @WithMockUser(
            username = "test02",
            roles = {"USER"})
    public void getProfile_invalidRequest_fail() {
        // GIVEN
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.ofNullable(null));

        // WHEN
        var exception = assertThrows(AppException.class, () -> userService.getProfile());

        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1005);
    }
}
