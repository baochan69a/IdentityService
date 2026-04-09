package com.chanas.identity_service.dto.request;

import com.chanas.identity_service.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

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
