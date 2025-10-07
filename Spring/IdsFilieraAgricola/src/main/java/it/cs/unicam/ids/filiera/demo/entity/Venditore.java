package it.cs.unicam.ids.filiera.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ruolo", discriminatorType = DiscriminatorType.STRING)
public abstract class Venditore extends UtenteVerificato {

	private String codiceFiscale;
	private boolean attesa = true;


	protected Venditore() {
		super(); // richiesto da JPA
	}

	public Venditore(String nome, String cognome, String email, String password, String codiceFiscale) {
		super(nome, cognome, email, password, codiceFiscale);
		this.codiceFiscale = codiceFiscale;
	}

	/**
	 * Ogni sottoclasse decide come crea il proprio prodotto base.
	 * Ad esempio, un produttore può creare un prodotto base, un trasformatore un trasformato, ecc.
	 */
	public abstract Prodotto creaProdotto(String nomeProdotto, String categoria, BigDecimal prezzo);

	// Getters & Setters

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public boolean isAttesa() {
		return attesa;
	}

	public void setAttesa(boolean attesa) {
		this.attesa = attesa;
	}
}
