package it.cs.unicam.ids.filiera.demo.entity;

import it.cs.unicam.ids.filiera.demo.entity.*;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PRODUTTORE")
public class Produttore extends Venditore {

	protected Produttore() {
	}

	@Override
	public Prodotto creaProdotto(String nomeProdotto, String categoria, BigDecimal prezzo) {
		return null;
	}

	public Produttore(String nome, String cognome, String email, String password, String codiceFiscale) {
		super(nome, cognome, email, password, codiceFiscale);
	}
}
