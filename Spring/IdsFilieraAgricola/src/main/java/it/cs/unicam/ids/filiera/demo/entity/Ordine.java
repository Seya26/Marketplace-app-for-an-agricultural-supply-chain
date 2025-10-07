package it.cs.unicam.ids.filiera.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordini")
public class Ordine {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private UtenteVerificato acquirente;

	private BigDecimal totale;
	private LocalDateTime creatoIl = LocalDateTime.now();
	@OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RigaOrdine> righe = new ArrayList<>();


	protected Ordine() {}

	public Ordine(UtenteVerificato acquirente, BigDecimal totale) {
		this.acquirente = acquirente;
		this.totale = totale;
	}

	public Long getId() { return id; }
	public UtenteVerificato getAcquirente() { return acquirente; }
	public BigDecimal getTotale() { return totale; }
	public LocalDateTime getCreatoIl() { return creatoIl; }

	@Override
	public String toString() {
		return "Ordine{id=%d, utente=%s %s, totale=%s, creatoIl=%s}"
				.formatted(id,
						acquirente != null ? acquirente.getNome() : "?",
						acquirente != null ? acquirente.getCognome() : "?",
						totale, creatoIl);
	}


	public List<RigaOrdine> getRighe() {
		return righe;
	}


	public void aggiungiRiga(Prodotto prodotto, int quantita) {
		this.righe.add(new RigaOrdine(this, prodotto, quantita));
	}



}

