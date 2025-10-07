package it.cs.unicam.ids.filiera.demo.dtos.eventoDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RichiestaInvitoDTO (
        @NotEmpty(message = "Devi specificare almeno un utente") List<Long> idUtentiList,
        @Size(max = 500, message = "Messaggio troppo lungo") String messaggio) {

}
