package com.burnoutinhos.burnoutinhos_api.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @Email
    @NotEmpty
    private String email;

    @Size(min = 8)
    @NotEmpty
    private String password;
}
