package it.cs.unicam.ids.filiera.demo.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProdottoDTO(
        Long id,
        String tipo,              // "BASE" | "TRASFORMATO" | "BUNDLE"
        Long venditoreId,
        String nome,
        String categoria,
        BigDecimal prezzo,
        LocalDate dataScadenza,
        // campi solo per TRASFORMATO:
        Long prodottoBaseId,
        String certificato,
        String metodoTrasformazione,
        // campi solo per BUNDLE:
        List<Long> componenti,    // IDs dei prodotti inclusi, senza quantità
        Integer quantita
) {}
