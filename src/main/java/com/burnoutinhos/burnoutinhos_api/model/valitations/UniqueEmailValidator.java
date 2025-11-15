package com.burnoutinhos.burnoutinhos_api.model.valitations;

import com.burnoutinhos.burnoutinhos_api.repository.AppUserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueEmailValidator
    implements ConstraintValidator<UniqueEmail, String> {

    private final AppUserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }
        return userRepository.findByEmail(email).isEmpty();
    }
}
