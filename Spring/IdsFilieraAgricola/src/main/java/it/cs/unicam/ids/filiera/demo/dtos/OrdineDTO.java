package it.cs.unicam.ids.filiera.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdineDTO(
        Long id,
        BigDecimal totale,
        LocalDateTime creatoIl,
        List<RigaOrdineDTO> righe
) {}
