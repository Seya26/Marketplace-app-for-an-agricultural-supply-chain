package it.cs.unicam.ids.filiera.demo.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("DISTRIBUTORE")
public class Distributore extends Venditore {

	protected Distributore() {
	}
	public Distributore(String nome, String cognome, String email, String password, String codiceficale) {
		super(nome, cognome, email, password, codiceficale);
	}

	@Override
	public Prodotto creaProdotto(String nomeProdotto, String categoria, BigDecimal prezzo) {
		return null;
	}


}
