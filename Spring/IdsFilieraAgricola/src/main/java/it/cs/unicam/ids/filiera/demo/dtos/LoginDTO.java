package it.cs.unicam.ids.filiera.demo.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "Email non può essere vuota")
        @Email(message = "Email non valida")
        String email,
        @NotBlank(message = "Password non può essere vuota")
        String password
) {
}
