package com.chanas.identity_service.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.chanas.identity_service.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUdateRequest {
    //    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;
    String firstname;
    String lastname;

    @DobConstraint(message = "INVALID_DOB", min = 18)
    LocalDate dob;

    List<String> roles;
}
