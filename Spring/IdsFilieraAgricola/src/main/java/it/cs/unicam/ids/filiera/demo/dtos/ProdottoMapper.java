package it.cs.unicam.ids.filiera.demo.dtos;

import it.cs.unicam.ids.filiera.demo.entity.Bundle;
import it.cs.unicam.ids.filiera.demo.entity.Prodotto;
import it.cs.unicam.ids.filiera.demo.entity.ProdottoTrasformato;
import org.hibernate.Hibernate;

import java.util.List;
import it.cs.unicam.ids.filiera.demo.entity.BundleItem;

public class ProdottoMapper {

    public static ProdottoDTO inDTO(Prodotto p) {
        if (p == null) return null;

        String tipo = "BASE";
        Long prodottoBaseId = null;
        String certificato = null;
        String metodo = null;
        List<Long> componenti = null;

        if (p instanceof ProdottoTrasformato pt) {
            tipo = "TRASFORMATO";
            prodottoBaseId = pt.getProdottoBaseId();
            certificato = pt.getCertificato();
            metodo = pt.getMetodoTrasformazione();
        } else if (p instanceof Bundle pb) {
            tipo = "BUNDLE";

            if (Hibernate.isInitialized(pb.getItems()) && pb.getItems() != null) {
                componenti = pb.getItems().stream()
                        .flatMap(item -> java.util.Collections.nCopies(item.getQuantita(), item.getProdotto().getId()).stream())
                        .toList();
            } else {
                componenti = List.of();
            }
        }

        return new ProdottoDTO(
                p.getId(),
                tipo,
                p.getVenditoreId(),
                p.getNome(),
                p.getCategoria(),
                p.getPrezzo(),
                p.getDataScadenza(),
                prodottoBaseId,
                certificato,
                metodo,
                componenti,
                p.getQuantita()
        );
    }

    public static Prodotto inEntity(ProdottoDTO dto) {
        if (dto == null) return null;

        String tipo = dto.tipo() == null ? "BASE" : dto.tipo().toUpperCase();

        Prodotto prodotto = switch (tipo) {
            case "TRASFORMATO" -> new ProdottoTrasformato(
                    dto.venditoreId(),
                    dto.nome(),
                    dto.categoria(),
                    dto.prezzo(),
                    dto.dataScadenza(),
                    dto.prodottoBaseId(),
                    dto.certificato(),
                    dto.metodoTrasformazione()
            );
            case "BUNDLE" -> new Bundle(
                    dto.venditoreId(),
                    dto.nome(),
                    dto.categoria(),
                    dto.prezzo(),
                    dto.dataScadenza()
            );
            default -> new Prodotto(
                    dto.venditoreId(),
                    dto.nome(),
                    dto.categoria(),
                    dto.prezzo(),
                    dto.dataScadenza()
            );
        };

        if (dto.quantita() != null) {
            prodotto.setQuantita(dto.quantita());
        }

        return prodotto;
    }

}
