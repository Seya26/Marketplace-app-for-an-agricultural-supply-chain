package it.cs.unicam.ids.filiera.demo.services;


import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.repositories.UtenteRepository;
import it.cs.unicam.ids.filiera.demo.dtos.*;
import it.cs.unicam.ids.filiera.demo.entity.Bundle;
import it.cs.unicam.ids.filiera.demo.entity.BundleItem;
import it.cs.unicam.ids.filiera.demo.entity.Prodotto;
import it.cs.unicam.ids.filiera.demo.entity.ProdottoTrasformato;
import it.cs.unicam.ids.filiera.demo.repositories.ProdottoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;
    private final UtenteRepository utenteRepository;

    public ProdottoService(ProdottoRepository prodottoRepository,
                           UtenteRepository utenteRepository) {
        this.prodottoRepository = prodottoRepository;
        this.utenteRepository = utenteRepository;
    }


    public String newProdotto(ProdottoDTO dtoProd) {
        if (dtoProd.venditoreId() == null) {
            throw new IllegalArgumentException("Il campo venditoreId è obbligatorio");
        }

        boolean campiTrasf = dtoProd.prodottoBaseId() != null
                || (dtoProd.certificato() != null && !dtoProd.certificato().isBlank())
                || (dtoProd.metodoTrasformazione() != null && !dtoProd.metodoTrasformazione().isBlank());

        boolean tipoNonBase = dtoProd.tipo() != null
                && !dtoProd.tipo().equalsIgnoreCase("BASE");

        if (campiTrasf || tipoNonBase) {
            throw new IllegalArgumentException("Per prodotti trasformati/bundle usa le rotte dedicate.");
        }

        UtenteVerificato creatore = utenteRepository.findById(dtoProd.venditoreId())
                .orElseThrow(() -> new IllegalArgumentException("Utente creatore non trovato con ID: " + dtoProd.venditoreId()));

        Prodotto prodotto = ProdottoMapper.inEntity(dtoProd);
        prodotto.setAttesa(true);
        prodotto.setCreatore(creatore);
        prodottoRepository.save(prodotto);

        return "Prodotto creato con ID: " + prodotto.getId();
    }


    public Prodotto salvaProdotto(Prodotto prodotto) {
        return prodottoRepository.save(prodotto);
    }

    public Prodotto rimuoviProdotto(int id) {
        Prodotto prodotto = visualizzaProdotto(id);
        prodottoRepository.delete(prodotto);
        return prodotto;
    }

    public Prodotto visualizzaProdotto(int id) {
        return prodottoRepository.findById((long) id)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato con ID: " + id));
    }

    public List<Prodotto> visualizzaTuttiProdotti() {
        return prodottoRepository.findAll();
    }

    public Prodotto aggiornaProdotto(int id, String nome, BigDecimal prezzo) {
        Prodotto prodotto = visualizzaProdotto(id);
        prodotto.setNome(nome);
        prodotto.setPrezzo(prezzo);
        return prodottoRepository.save(prodotto);
    }

    public List<Prodotto> visualizzaProdottiUtente(int id) {
        return prodottoRepository.findAll().stream()
                .filter(p -> p.getVenditoreId() != null && p.getVenditoreId().equals((long) id))
                .toList();
    }

    public Prodotto newProdottoTrasformato(ProdottoTrasformatoDTO dto) {
        if (dto.venditoreId() == null || dto.prodottoBaseId() == null || dto.quantita() == null || dto.quantita() <= 0) {
            throw new IllegalArgumentException("Dati obbligatori mancanti o quantità non valida.");
        }


        UtenteVerificato creatore = utenteRepository.findById(dto.venditoreId())
                .orElseThrow(() -> new IllegalArgumentException("Creatore non trovato con ID: " + dto.venditoreId()));


        Prodotto base = prodottoRepository.findById(dto.prodottoBaseId())
                .orElseThrow(() -> new IllegalArgumentException("Prodotto base non trovato."));

        if (base instanceof ProdottoTrasformato || base instanceof Bundle) {
            throw new IllegalArgumentException("Il prodotto base deve essere di tipo BASE.");
        }

        if (base.getQuantita() < dto.quantita()) {
            throw new IllegalStateException("Quantità insufficiente del prodotto base con ID: " + base.getId());
        }


        base.decrementaQuantita(dto.quantita());
        prodottoRepository.save(base);


        ProdottoTrasformato pt = new ProdottoTrasformato(
                dto.venditoreId(),
                dto.nome(),
                dto.categoria(),
                dto.prezzo(),
                dto.dataScadenza(),
                dto.prodottoBaseId(),
                dto.certificato(),
                dto.metodoTrasformazione()
        );

        pt.setAttesa(true);
        pt.setQuantita(dto.quantita());
        pt.setCreatore(creatore);

        return prodottoRepository.save(pt);
    }


    @Transactional
    public Prodotto aggiungiProdottoBundle(Long bundleId, Long prodottoId) {
        Bundle bundle = prodottoRepository.findBundleWithProdotti(bundleId)
                .orElseThrow(() -> new IllegalArgumentException("Bundle non trovato"));

        if (!bundle.isAttesa()) {
            throw new IllegalStateException("Bundle confermato: non modificabile.");
        }

        Prodotto prodotto = prodottoRepository.findById(prodottoId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

        if (prodotto instanceof Bundle) {
            throw new IllegalArgumentException("Non è possibile inserire un bundle dentro un altro bundle.");
        }

        Optional<BundleItem> esistente = bundle.getItems().stream()
                .filter(item -> item.getProdotto().getId().equals(prodottoId))
                .findFirst();

        if (esistente.isPresent()) {
            esistente.get().incrementaQuantita();
        } else {
            bundle.aggiungiItem(prodotto, 1);
        }

        aggiornaPrezzoBundle(bundle);

        return prodottoRepository.save(bundle);
    }



    @Transactional
    public Prodotto rimuoviProdottoDaBundle(Long bundleId, Long prodottoId) {
        Bundle bundle = prodottoRepository.findBundleWithProdotti(bundleId)
                .orElseThrow(() -> new IllegalArgumentException("Bundle non trovato"));

        if (!bundle.isAttesa()) {
            throw new IllegalStateException("Bundle confermato: non modificabile.");
        }

        BundleItem item = bundle.getItems().stream()
                .filter(i -> i.getProdotto().getId().equals(prodottoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non presente nel bundle"));

        if (item.getQuantita() > 1) {
            item.setQuantita(item.getQuantita() - 1);
        } else {
            bundle.getItems().remove(item);
        }

        aggiornaPrezzoBundle(bundle);

        return prodottoRepository.save(bundle);
    }

    public List<Prodotto> getProdotti(Long idVenditore) {
        return prodottoRepository.findAll().stream()
                .filter(p -> p.getVenditoreId() != null && p.getVenditoreId().equals(idVenditore))
                .toList();
    }


    public Prodotto newBundle(BundleDTO dto) {
        if (dto.venditoreId() == null) {
            throw new IllegalArgumentException("Il venditoreId è obbligatorio.");
        }


        UtenteVerificato creatore = utenteRepository.findById(dto.venditoreId())
                .orElseThrow(() -> new IllegalArgumentException("Creatore non trovato con ID: " + dto.venditoreId()));


        Optional<Bundle> esistente = prodottoRepository.findBundleInAttesaByVenditoreId(dto.venditoreId());
        if (esistente.isPresent()) {
            throw new IllegalStateException("Hai già un bundle in attesa. Confermalo prima di crearne un altro.");
        }


        Bundle nuovo = new Bundle(
                dto.venditoreId(),
                dto.nome(),
                dto.categoria(),
                dto.prezzo(),
                dto.dataScadenza()
        );

        nuovo.setAttesa(true);
        nuovo.setCreatore(creatore);

        return prodottoRepository.save(nuovo);
    }




    private void aggiornaPrezzoBundle(Bundle bundle) {
        BigDecimal totale = bundle.getItems().stream()
                .map(item -> item.getProdotto().getPrezzo().multiply(BigDecimal.valueOf(item.getQuantita())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        bundle.setPrezzo(totale);
    }


    @Transactional
    public Prodotto confermaBundle(Long bundleId, int quantitaFinale, UtenteVerificato utente) {

        Bundle bundle = prodottoRepository.findBundleWithProdotti(bundleId)
                .orElseThrow(() -> new IllegalArgumentException("Bundle non trovato"));

        if (bundle.isConfermato()) {
            throw new IllegalStateException("Il bundle è già stato confermato.");
        }

        if (bundle.getItems() == null || bundle.getItems().isEmpty()) {
            throw new IllegalStateException("Impossibile confermare: il bundle è vuoto.");
        }

        for (BundleItem item : bundle.getItems()) {
            Prodotto prodotto = item.getProdotto();
            int richiestaTotale = item.getQuantita() * quantitaFinale;

            if (prodotto.getQuantita() < richiestaTotale) {
                throw new IllegalStateException("Quantità insufficiente per il prodotto '%s' (ID: %d). Richiesti: %d, Disponibili: %d"
                        .formatted(prodotto.getNome(), prodotto.getId(), richiestaTotale, prodotto.getQuantita()));
            }
        }

        for (BundleItem item : bundle.getItems()) {
            Prodotto prodotto = item.getProdotto();
            int richiestaTotale = item.getQuantita() * quantitaFinale;
            prodotto.decrementaQuantita(richiestaTotale);
            prodottoRepository.save(prodotto);
        }

        bundle.setQuantita(quantitaFinale);
        aggiornaPrezzoBundle(bundle);
        bundle.setConfermato(true);
        bundle.setAttesa(false);

        if (bundle.getCreatore() == null)
            bundle.setCreatore(utente);
        if (bundle.getVenditoreId() == null)
            bundle.setVenditoreId(utente.getId());

        return prodottoRepository.save(bundle);
    }





    public Prodotto setStato(int id, boolean attesa) {
        Prodotto prodotto = visualizzaProdotto(id);
        prodotto.setAttesa(attesa);
        return prodottoRepository.save(prodotto);
    }

    public List<Prodotto> getProdottiInAttesa() {
        return prodottoRepository.findAll().stream()
                .filter(Prodotto::isAttesa)
                .toList();
    }

    public Prodotto approvaProdotto(int id) {
        Prodotto prodotto = visualizzaProdotto(id);
        prodotto.setAttesa(false);
        return prodottoRepository.save(prodotto);
    }

    public Prodotto rifiutaProdotto(int id) {
        Prodotto prodotto = visualizzaProdotto(id);
        prodottoRepository.delete(prodotto);
        return prodotto;
    }

    public List<ProdottoDTO> visualizzaTuttiProdottiDTO() {
        List<Prodotto> prodottiNormali = prodottoRepository.findAll().stream()
                .filter(p -> !(p instanceof Bundle))
                .toList();
        List<Bundle> bundles = prodottoRepository.findTuttiIBundleConProdotti();

        List<ProdottoDTO> result = new ArrayList<>();
        prodottiNormali.forEach(p -> result.add(ProdottoMapper.inDTO(p)));
        bundles.forEach(b -> result.add(ProdottoMapper.inDTO(b)));
        return result;
    }

    public List<ProdottoDTO> visualizzaProdottiApprovatiDTO() {
        List<Prodotto> prodottiNormali = prodottoRepository.findByAttesaFalse().stream()
                .filter(p -> !(p instanceof Bundle))
                .toList();
        List<Bundle> bundles = prodottoRepository.findBundleConfermatiConProdotti();

        List<ProdottoDTO> result = new ArrayList<>();
        prodottiNormali.forEach(p -> result.add(ProdottoMapper.inDTO(p)));
        bundles.forEach(b -> result.add(ProdottoMapper.inDTO(b)));
        return result;
    }


    @Transactional(readOnly = true)
    public Bundle visualizzaBundleConItems(Long id) {
        return prodottoRepository.findBundleWithItems(id)
                .orElseThrow(() -> new IllegalArgumentException("Bundle non trovato con ID: " + id));
    }

    public String toStringProdotto(Prodotto prod) {
        String base = "Prodotto{id=%d, tipo=%s, nome='%s', categoria='%s', prezzo=%.2f, scadenza=%s, attesa=%s}"
                .formatted(
                        prod.getId(),
                        prod.getClass().getSimpleName(),
                        prod.getNome(),
                        prod.getCategoria(),
                        prod.getPrezzo(),
                        prod.getDataScadenza(),
                        prod.isAttesa()
                );

        if (prod instanceof ProdottoTrasformato pt) {
            return base + " [baseId=%d, certificato='%s', metodo='%s']"
                    .formatted(pt.getProdottoBaseId(), pt.getCertificato(), pt.getMetodoTrasformazione());
        }

        if (prod instanceof Bundle bundle) {
            return base + " [componenti=%s]"
                    .formatted(bundle.getItems().stream()
                            .map(item -> "(id=%d x%d)".formatted(item.getProdotto().getId(), item.getQuantita()))
                            .toList());
        }

        return base;
    }





}
