package it.cs.unicam.ids.filiera.demo.controllers;

import it.cs.unicam.ids.filiera.demo.dtos.BundleDTO;
import it.cs.unicam.ids.filiera.demo.dtos.ProdottoDTO;
import it.cs.unicam.ids.filiera.demo.dtos.ProdottoTrasformatoDTO;
import it.cs.unicam.ids.filiera.demo.entity.Bundle;
import it.cs.unicam.ids.filiera.demo.entity.Prodotto;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.model.Sessione;
import it.cs.unicam.ids.filiera.demo.services.GestionaleService;
import it.cs.unicam.ids.filiera.demo.services.ProdottoService;
import it.cs.unicam.ids.filiera.demo.dtos.ProdottoMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prodotti")
public class ProdottoController {

    @Autowired
    private ProdottoService prodottoService;

    @PostMapping
    public ResponseEntity<String> creaProdotto(@RequestBody ProdottoDTO dto) {
        String result = prodottoService.newProdotto(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<ProdottoDTO>> getTuttiProdotti() {
        return ResponseEntity.ok(
                prodottoService.visualizzaTuttiProdottiDTO()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProdottoDTO> getProdotto(@PathVariable int id) {
        return ResponseEntity.ok(
                ProdottoMapper.inDTO(prodottoService.visualizzaProdotto(id))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProdottoDTO> deleteProdotto(@PathVariable int id) {
        return ResponseEntity.ok(
                ProdottoMapper.inDTO(prodottoService.rimuoviProdotto(id))
        );
    }

    @GetMapping("/in-attesa")
    public ResponseEntity<List<ProdottoDTO>> getProdottiInAttesa() {
        return ResponseEntity.ok(
                prodottoService.getProdottiInAttesa().stream()
                        .map(ProdottoMapper::inDTO)
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{id}/approva")
    public ResponseEntity<ProdottoDTO> approvaProdotto(@PathVariable int id) {
        return ResponseEntity.ok(
                ProdottoMapper.inDTO(prodottoService.approvaProdotto(id))
        );
    }

    @DeleteMapping("/{id}/rifiuta")
    public ResponseEntity<ProdottoDTO> rifiutaProdotto(@PathVariable int id) {
        return ResponseEntity.ok(
                ProdottoMapper.inDTO(prodottoService.rifiutaProdotto(id))
        );
    }


    @GetMapping("/approvati")
    public ResponseEntity<List<ProdottoDTO>> getProdottiApprovati() {
        return ResponseEntity.ok(prodottoService.visualizzaProdottiApprovatiDTO());
    }






    @PutMapping("/{id}")
    public ResponseEntity<ProdottoDTO> aggiornaNomePrezzo(
            @PathVariable int id,
            @RequestParam String nome,
            @RequestParam BigDecimal prezzo) {
        return ResponseEntity.ok(
                ProdottoMapper.inDTO(prodottoService.aggiornaProdotto(id, nome, prezzo))
        );
    }

    @GetMapping("/venditore/{id}")
    public ResponseEntity<List<ProdottoDTO>> getProdottiPerVenditore(@PathVariable Long id) {
        return ResponseEntity.ok(
                prodottoService.getProdotti(id).stream()
                        .map(ProdottoMapper::inDTO)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("/trasformato")
    public ResponseEntity<ProdottoDTO> creaProdottoTrasformato(@RequestBody ProdottoTrasformatoDTO dto) {
        try {
            return ResponseEntity.ok(
                    ProdottoMapper.inDTO(prodottoService.newProdottoTrasformato(dto))
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/bundle")
    public ResponseEntity<ProdottoDTO> creaBundle(@RequestBody BundleDTO dto) {
        return ResponseEntity.ok(
                ProdottoMapper.inDTO(prodottoService.newBundle(dto))
        );
    }

    @PatchMapping("/bundle/{bundleId}/aggiungi/{prodottoId}")
    public ResponseEntity<ProdottoDTO> aggiungiProdottoABundle(
            @PathVariable Long bundleId,
            @PathVariable Long prodottoId) {
        return ResponseEntity.ok(
                ProdottoMapper.inDTO(prodottoService.aggiungiProdottoBundle(bundleId, prodottoId))
        );
    }

    @DeleteMapping("/bundle/{bundleId}/rimuovi/{prodottoId}")
    public ResponseEntity<ProdottoDTO> rimuoviProdottoDaBundle(
            @PathVariable Long bundleId,
            @PathVariable Long prodottoId) {
        return ResponseEntity.ok(
                ProdottoMapper.inDTO(prodottoService.rimuoviProdottoDaBundle(bundleId, prodottoId))
        );
    }

    @PostMapping("/bundle/{id}/conferma")
    public ResponseEntity<ProdottoDTO> confermaBundle(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int quantita,
            HttpSession session
    ) {

        Sessione s = (Sessione) session.getAttribute(GestionaleService.SESSIONE_KEY);
        if (s == null || s.getUtente() == null)
            throw new RuntimeException("Utente non autenticato");

        UtenteVerificato utente = s.getUtente();


        Prodotto prodotto = prodottoService.confermaBundle(id, quantita, utente);

        return ResponseEntity.ok(ProdottoMapper.inDTO(prodotto));
    }



    @GetMapping("/bundle/{id}/componenti")
    public ResponseEntity<List<String>> getComponenti(@PathVariable Long id) {
        Bundle b = prodottoService.visualizzaBundleConItems(id);

        List<String> dettagli = b.getItems().stream()
                .map(i -> "Prodotto ID %d x%d".formatted(i.getProdotto().getId(), i.getQuantita()))
                .toList();

        return ResponseEntity.ok(dettagli);
    }



}
