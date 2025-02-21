package com.secure.shopbackend.dtos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    private Long userId;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
//    @Pattern(regexp = "^[0-9]{3}[0-9]{4}[0-9]{4}", message = "전화번호 형식이 잘못되었습니다!")
    private String phone;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;
}
