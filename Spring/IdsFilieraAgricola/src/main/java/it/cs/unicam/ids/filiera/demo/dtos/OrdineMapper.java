package it.cs.unicam.ids.filiera.demo.dtos;

import it.cs.unicam.ids.filiera.demo.entity.Ordine;

import java.util.List;

public class OrdineMapper {
    public static OrdineDTO toDTO(Ordine ordine) {
        List<RigaOrdineDTO> righeDTO = ordine.getRighe().stream()
                .map(riga -> new RigaOrdineDTO(
                        riga.getProdotto().getId(),
                        riga.getProdotto().getNome(),
                        riga.getQuantita()))
                .toList();

        return new OrdineDTO(
                ordine.getId(),
                ordine.getTotale(),
                ordine.getCreatoIl(),
                righeDTO
        );
    }
}
