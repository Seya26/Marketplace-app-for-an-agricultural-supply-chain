package it.cs.unicam.ids.filiera.demo.controllers;

import it.cs.unicam.ids.filiera.demo.dtos.OrdineDTO;
import it.cs.unicam.ids.filiera.demo.dtos.OrdineMapper;
import it.cs.unicam.ids.filiera.demo.entity.Ordine;
import it.cs.unicam.ids.filiera.demo.entity.Prodotto;
import it.cs.unicam.ids.filiera.demo.model.Carrello;
import it.cs.unicam.ids.filiera.demo.model.RigaCarrello;
import it.cs.unicam.ids.filiera.demo.services.AcquistoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/acquisto")
@Validated
public class AcquistoController {

	private final AcquistoService acquistoService;

	public AcquistoController(AcquistoService acquistoService) {
		this.acquistoService = acquistoService;
	}


    // Aggiungi al carrello
	@PostMapping("/carrello/{id}")
	public ResponseEntity<String> richiestaAggiungiAlCarrello(
			@PathVariable Long id,
			@RequestParam(defaultValue = "1") int qty,
			HttpSession session) {
		String risposta = acquistoService.aggiungiAlCarrello(session, id, qty);
		return ResponseEntity.ok(risposta);
	}

	// Rimuovi dal carrello
	@DeleteMapping("/carrello/{id}")
	public ResponseEntity<String> richiestaRimuoviDalCarrello(
			@PathVariable Long id,
			HttpSession session) {
		String risposta = acquistoService.rimuoviDalCarrello(session, id);
		return ResponseEntity.ok(risposta);
	}

	// Visualizza carrello (righe)
	@GetMapping("/carrello")
	public ResponseEntity<Collection<RigaCarrello>> richiestaVisualizzaCarrello(HttpSession session) {
		Carrello carrello = acquistoService.getCarrello(session);
		return ResponseEntity.ok(carrello.getRighe());
	}

	// Svuota carrello
	@DeleteMapping("/carrello")
	public ResponseEntity<String> richiestaSvuotaCarrello(HttpSession session) {
		String risposta = acquistoService.svuotaCarrello(session);
		return ResponseEntity.ok(risposta);
	}

	// Visualizza totale
	@GetMapping("/totale")
	public ResponseEntity<Float> richiestaVisualizzaTotale(HttpSession session) {
		float totale = acquistoService.visualizzaTotale(session);
		return ResponseEntity.ok(totale);
	}

	// Acquista (conferma ordine)
	@PostMapping("/acquista")
	public ResponseEntity<String> richiestaAcquista(HttpSession session) {
		String risposta = acquistoService.acquista(session);
		return ResponseEntity.ok(risposta);
	}


	@GetMapping("/ordini/utente/{utenteId}")
	public List<OrdineDTO> getOrdiniUtente(@PathVariable Long utenteId) {
		return acquistoService.visualizzaOrdini(utenteId).stream()
				.map(OrdineMapper::toDTO)
				.toList();
	}

	@GetMapping("/ordini/{ordineId}")
	public OrdineDTO getOrdine(@PathVariable Long ordineId) {
		return OrdineMapper.toDTO(acquistoService.visualizzaOrdineSingolo(ordineId));
	}




}
