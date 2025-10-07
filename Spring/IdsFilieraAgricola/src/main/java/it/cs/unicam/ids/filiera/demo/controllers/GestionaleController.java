package it.cs.unicam.ids.filiera.demo.controllers;

import it.cs.unicam.ids.filiera.demo.entity.Ordine;
import it.cs.unicam.ids.filiera.demo.model.Carrello;
import it.cs.unicam.ids.filiera.demo.services.GestionaleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/gestionale")
@Validated
public class GestionaleController {

	private final GestionaleService gestionaleService;

	public GestionaleController(GestionaleService gestionaleService) {
		this.gestionaleService = gestionaleService;
	}

	// (Opzionale) inizializza la sessione e restituisce l'ID
	@PostMapping("/sessione")
	public ResponseEntity<String> creaSessione(HttpSession session) {
		gestionaleService.newSessione(session);
		return ResponseEntity.ok(session.getId());
	}

	// Aggiunge un prodotto al carrello (qty default = 1)
	@PostMapping("/carrello/{prodottoId}")
	public ResponseEntity<Carrello> aggiungiAlCarrello(@PathVariable Long prodottoId,
													   @RequestParam(defaultValue = "1") int qty,
													   HttpSession session) {
		Carrello carrello = gestionaleService.aggiungiAlCarrello(session, prodottoId, qty);
		return ResponseEntity.ok(carrello);
	}

	// Mostra il contenuto del carrello
	@GetMapping("/carrello")
	public ResponseEntity<String> richiestaContenutoCarrello(HttpSession session) {
		return ResponseEntity.ok(gestionaleService.mostraContenutoCarrello(session));
	}

	// Svuota il carrello
	@DeleteMapping("/carrello")
	public ResponseEntity<Void> svuotaCarrello(HttpSession session) {
		gestionaleService.aggiornaCarrello(session);
		return ResponseEntity.noContent().build();
	}

	// Conclude l'ordine
	@PostMapping("/ordini")
	public ResponseEntity<Ordine> richiestaAggiungiOrdine(HttpSession session) {
		Ordine ordine = gestionaleService.aggiungiOrdine(session);
		return ResponseEntity.created(URI.create("/marketplace/ordini/" + ordine.getId()))
				.body(ordine);
	}


	@GetMapping("/ping")
	public String ping() {
		return "OK - Gestionale attivo!";
	}




}
