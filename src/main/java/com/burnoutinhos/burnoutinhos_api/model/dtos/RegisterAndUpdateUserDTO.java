package com.burnoutinhos.burnoutinhos_api.model.dtos;

import com.burnoutinhos.burnoutinhos_api.model.enums.LanguagePreference;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAndUpdateUserDTO {

    @Size(min = 3)
    private String name;

    @Email
    private String email;

    @Nullable
    private String password;

    @Nullable
    private LanguagePreference language = LanguagePreference.PTBR;

    @Nullable
    private String profileImage;
}
