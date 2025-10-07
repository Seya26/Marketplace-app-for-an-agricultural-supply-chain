package it.cs.unicam.ids.filiera.demo.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegistrazioneDTO(
        @NotBlank(message = "Nome obbligatorio") String nome,
        @NotBlank(message = "Cognome obbligatorio") String cognome,
        @NotBlank(message = "Email obbligatorio") @Email(message = "Email non valida") String email,
        @NotBlank(message = "Password obbligatoria") String password,
        @NotBlank(message = "Ruolo utente obbligatorio")
        @Pattern(regexp = "ACQUIRENTE|PRODUTTORE|TRASFORMATORE|DISTRIBUTORE|ANIMATORE",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "Tipo deve essere uno tra: ACQUIRENTE, PRODUTTORE, TRASFORMATORE, DISTRIBUTORE, ANIMATORE")
        String tipo,

        String codiceFiscale    // Obbligatorio solo per venditori
) {}
