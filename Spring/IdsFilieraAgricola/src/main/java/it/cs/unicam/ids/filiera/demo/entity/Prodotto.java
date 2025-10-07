package it.cs.unicam.ids.filiera.demo.entity;

import it.cs.unicam.ids.filiera.demo.observer.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "prodotti")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("BASE")
public class Prodotto implements Notifica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "venditore_id")
	private Long venditoreId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "creatore_id", nullable = false)
	private UtenteVerificato creatore;

	private String nome;
	private String categoria;

	@Column(precision = 12, scale = 2)
	private BigDecimal prezzo;

	@Column(nullable = false)
	private boolean confermato = false;

	@Column(name = "data_scadenza")
	private LocalDate dataScadenza;

	@Column(name = "attesa")
	private boolean attesa = true;

	@Column(nullable = false)
	private int quantita = 0;

	@Transient
	private List<Observer> observers = new ArrayList<>();

	public Prodotto() {}

	public Prodotto(Long venditoreId, String nome, String categoria, BigDecimal prezzo, LocalDate dataScadenza) {
		this.venditoreId = venditoreId;
		this.nome = nome;
		this.categoria = categoria;
		this.prezzo = prezzo;
		this.dataScadenza = dataScadenza;
		this.attesa = true;
		this.confermato = false;
		this.quantita = 0;
		this.notifyObservers("Il nuovo prodotto " + this.getNome() + "è stato creato in attesa di validazione");
	}



	public Long getId() {
		return id;
	}

	public Long getVenditoreId() {
		return venditoreId;
	}

	public void setVenditoreId(Long venditoreId) {
		this.venditoreId = venditoreId;
	}

	public UtenteVerificato getCreatore() {
		return creatore;
	}

	public void setCreatore(UtenteVerificato creatore) {
		this.creatore = creatore;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public BigDecimal getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(BigDecimal prezzo) {
		this.prezzo = prezzo;
	}

	public LocalDate getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(LocalDate dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public boolean isAttesa() {
		return attesa;
	}

	public void setAttesa(boolean attesa) {
		this.attesa = attesa;
	}

	public boolean isConfermato() {
		return confermato;
	}

	public void setConfermato(boolean confermato) {
		this.confermato = confermato;
		notifyObservers("Il prodotto " + this.getNome() + "è stato confermato");
	}

	public int getQuantita() {
		return quantita;
	}

	public void setQuantita(int quantita) {
		this.quantita = quantita;
	}

	public void incrementaQuantita(int delta) {
		if (delta < 0) throw new IllegalArgumentException("Delta negativo non valido.");
		this.quantita += delta;
	}

	public void decrementaQuantita(int delta) {
		if (delta < 0) throw new IllegalArgumentException("Delta negativo non valido.");
		if (this.quantita < delta) {
			throw new IllegalStateException("Quantità insufficiente per il prodotto con ID: " + this.id);
		}
		this.quantita -= delta;
	}

	// Observer pattern
	@Override
	public void sub(Observer o) {
		observers.add(o);
	}

	@Override
	public void unsub(Observer o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers(String message) {
		for (Observer o : observers) {
			o.aggiorna(this, message);
		}
	}
	// Equals & hashCode
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Prodotto other)) return false;
		return id != null && id.equals(other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClass(), id);
	}
}



