package it.cs.unicam.ids.filiera.demo.dtos;

public record WhoAmIDTO(
        Long id,
        String nome,
        String cognome,
        String email,
        String ruolo,
        String sessionId
) {}