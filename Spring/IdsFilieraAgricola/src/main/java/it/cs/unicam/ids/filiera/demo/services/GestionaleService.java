package it.cs.unicam.ids.filiera.demo.services;

import it.cs.unicam.ids.filiera.demo.entity.Acquirente;
import it.cs.unicam.ids.filiera.demo.entity.Ordine;
import it.cs.unicam.ids.filiera.demo.entity.Prodotto;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.model.Carrello;
import it.cs.unicam.ids.filiera.demo.model.Sessione;
import it.cs.unicam.ids.filiera.demo.repositories.OrdineRepository;
import it.cs.unicam.ids.filiera.demo.repositories.ProdottoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GestionaleService {

	public static final String SESSIONE_KEY = "SESSIONE";

	private final OrdineRepository ordineRepository;
	private final ProdottoRepository prodottoRepository;

	public GestionaleService(OrdineRepository ordineRepository,
							 ProdottoRepository prodottoRepository) {
		this.ordineRepository = ordineRepository;
		this.prodottoRepository = prodottoRepository;
	}

	private Sessione getOrCreate(HttpSession httpSession) {
		Sessione s = (Sessione) httpSession.getAttribute(SESSIONE_KEY);
		if (s == null) {
			s = new Sessione(); // utente potrai impostarlo altrove (login)
			httpSession.setAttribute(SESSIONE_KEY, s);
		}
		return s;
	}


	/** Inizializza/ritorna la Sessione applicativa */
	public Sessione newSessione(HttpSession httpSession) {
		return getOrCreate(httpSession);
	}

	/** Ritorna il contenuto del carrello in formato stringa */
	@Transactional(readOnly = true)
	public String mostraContenutoCarrello(HttpSession httpSession) {
		return getOrCreate(httpSession).mostraContenuto();
	}

	/** Aggiunge un prodotto al carrello cercandolo per id (usato dal Controller) */
	public Carrello aggiungiAlCarrello(HttpSession session, Long prodottoId, int qty) {
		Prodotto p = prodottoRepository.findById(prodottoId)
				.orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato: " + prodottoId));
		return aggiungiAlCarrello(session, p, qty);
	}

	/** Aggiunge un prodotto già caricato al carrello (riusata internamente) */
	public Carrello aggiungiAlCarrello(HttpSession session, Prodotto p, int qty) {
		if (p == null) throw new IllegalArgumentException("Prodotto nullo");
		Sessione s = getOrCreate(session);
		int q = Math.max(1, qty);
		s.getCarrello().aggiungi(p, (long) q);
		session.setAttribute(SESSIONE_KEY, s);
		return s.getCarrello();
	}

	/** Svuota il carrello e restituisce lo stato aggiornato */
	public Carrello aggiornaCarrello(HttpSession httpSession) {
		Sessione s = getOrCreate(httpSession);
		s.aggiornaCarrelloSvuota();
		httpSession.setAttribute(SESSIONE_KEY, s);
		return s.getCarrello();
	}

	/** Crea e persiste un ordine dal carrello; poi svuota il carrello */
	@Transactional
	public Ordine aggiungiOrdine(HttpSession httpSession) {
		Sessione s = (Sessione) httpSession.getAttribute(SESSIONE_KEY);
		if (s == null) throw new IllegalStateException("Sessione non inizializzata");

		Carrello carrello = s.getCarrello();
		if (carrello == null || carrello.isVuoto())
			throw new IllegalStateException("Carrello vuoto");

		UtenteVerificato utente = s.getUtente();
		if (utente == null)
			throw new IllegalStateException("Utente non presente in sessione");

		// Crea l'ordine
		Ordine ordine = new Ordine(utente, carrello.getTotale());

		for (var riga : carrello.getRighe()) {
			Prodotto p = prodottoRepository.findById(riga.getProdottoId())
					.orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

			int richiesta = Math.toIntExact(riga.getQuantita());

			// Verifica disponibilità
			if (p.getQuantita() < richiesta) {
				throw new IllegalStateException("Quantità insufficiente per il prodotto '%s'. Richiesti: %d, Disponibili: %d"
						.formatted(p.getNome(), richiesta, p.getQuantita()));
			}

			// Scala la quantità
			p.decrementaQuantita(richiesta);
			prodottoRepository.save(p);

			// Aggiungi riga all'ordine
			ordine.aggiungiRiga(p, richiesta);
		}

		ordine = ordineRepository.save(ordine);

		// Svuota il carrello
		s.aggiornaCarrelloSvuota();
		httpSession.setAttribute(SESSIONE_KEY, s);

		return ordine;
	}


	/** Alias void per compatibilità col tuo design */
	public void newOrdine(HttpSession httpSession) {
		aggiungiOrdine(httpSession);
	}

	public Carrello getCarrello(HttpSession session) {
		return getOrCreate(session).getCarrello();
	}

	public void loginFittizio(HttpSession session) {
		UtenteVerificato utenteFinto = new Acquirente("Mario", "Rossi", "mario@example.com", "password");
		session.setAttribute("utente", utenteFinto);

		Sessione s = getOrCreate(session);
		s.setUtente(utenteFinto);
		session.setAttribute(SESSIONE_KEY, s);
	}

	@Transactional(readOnly = true)
	public List<Ordine> getOrdiniPerUtente(Long utenteId) {
		return ordineRepository.findByAcquirenteId(utenteId);
	}

	@Transactional(readOnly = true)
	public Ordine getOrdineSingolo(Long ordineId) {
		return ordineRepository.findById(ordineId)
				.orElseThrow(() -> new IllegalArgumentException("Ordine non trovato"));
	}


}

