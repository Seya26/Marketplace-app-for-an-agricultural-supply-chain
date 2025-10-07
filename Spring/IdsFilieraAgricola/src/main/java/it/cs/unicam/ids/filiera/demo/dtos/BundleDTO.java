package it.cs.unicam.ids.filiera.demo.dtos;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BundleDTO(
        Long venditoreId,
        String nome,
        String categoria,
        BigDecimal prezzo,
        LocalDate dataScadenza,
        List<BundleItemDTO> componenti,
        Integer quantita
) {}

