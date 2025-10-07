package it.cs.unicam.ids.filiera.demo.services;


import it.cs.unicam.ids.filiera.demo.entity.Ordine;
import it.cs.unicam.ids.filiera.demo.model.Carrello;
import it.cs.unicam.ids.filiera.demo.model.RigaCarrello;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class AcquistoService {

	@Autowired
	private GestionaleService gestionaleService;

	public String aggiungiAlCarrello(HttpSession session, Long prodottoId, int qty) {
		gestionaleService.aggiungiAlCarrello(session, prodottoId, qty);
		return "Prodotto aggiunto al carrello";
	}

	public String rimuoviDalCarrello(HttpSession session, Long prodottoId) {
		Carrello carrello = getCarrello(session);

		Iterator<RigaCarrello> iterator = carrello.getRighe().iterator();
		while (iterator.hasNext()) {
			RigaCarrello riga = iterator.next();

			if (riga.getProdottoId().equals(prodottoId)) {
				Long q = riga.getQuantita();

				if (q > 1) {
					riga.setQuantita(q - 1);
				} else {
					iterator.remove(); // ✅ più sicuro di remove() sulla lista
				}

				return "Quantità aggiornata/ridotta nel carrello.";
			}
		}

		return "Prodotto non trovato nel carrello.";
	}

	public String svuotaCarrello(HttpSession session) {
		gestionaleService.aggiornaCarrello(session);
		return "Carrello svuotato";
	}

	public String mostraContenutoCarrello(HttpSession session) {
		return gestionaleService.mostraContenutoCarrello(session);
	}

	public float visualizzaTotale(HttpSession session) {
		return gestionaleService.getCarrello(session).getTotale().floatValue();
	}

	public String acquista(HttpSession session) {
		try {
			gestionaleService.aggiungiOrdine(session);
			return "Ordine effettuato con successo";
		} catch (Exception e) {
			e.printStackTrace(); // Log in console
			return "Errore durante l'acquisto: " + e.getMessage();
		}
	}


	public Carrello getCarrello(HttpSession session) {
		return gestionaleService.getCarrello(session);
	}

	public List<Ordine> visualizzaOrdini(Long utenteId) {
		return gestionaleService.getOrdiniPerUtente(utenteId);
	}

	public Ordine visualizzaOrdineSingolo(Long ordineId) {
		return gestionaleService.getOrdineSingolo(ordineId);
	}

}
