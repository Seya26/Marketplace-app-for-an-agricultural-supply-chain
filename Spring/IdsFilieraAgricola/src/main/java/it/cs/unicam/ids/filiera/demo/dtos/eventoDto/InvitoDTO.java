package it.cs.unicam.ids.filiera.demo.dtos.eventoDto;

import it.cs.unicam.ids.filiera.demo.entity.eventi.InvitoStato;

public record InvitoDTO(
        Long id,
        Long eventoId,
        String eventoTitolo,
        Long invitatoId,
        String invitatoNome,
        String invitatoCognome,
        String invitatoRuolo,
        InvitoStato stato,
        String messaggio
) {
}
