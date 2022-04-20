package com.example.springexam.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDTO {
    @NotBlank(message = "Name required")
    private String userName;

    @NotBlank(message = "Enter password correctly")
    private String password;
}
