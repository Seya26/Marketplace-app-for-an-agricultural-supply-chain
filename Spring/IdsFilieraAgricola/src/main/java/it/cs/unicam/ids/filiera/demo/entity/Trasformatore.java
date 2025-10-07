package it.cs.unicam.ids.filiera.demo.entity;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("TRASFORMATORE")
public class Trasformatore extends Venditore {

	protected Trasformatore() {
	}

	public Trasformatore(String nome, String cognome, String email, String password, String codiceFiscale) {
		super(nome, cognome, email, password,codiceFiscale);
	}

	@Override
	public Prodotto creaProdotto(String nomeProdotto, String categoria, BigDecimal prezzo) {
		return null;
	}
}
