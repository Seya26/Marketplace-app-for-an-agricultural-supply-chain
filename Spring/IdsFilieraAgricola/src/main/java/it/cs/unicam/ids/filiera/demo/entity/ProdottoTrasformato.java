package it.cs.unicam.ids.filiera.demo.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@DiscriminatorValue("TRASFORMATO")
public class ProdottoTrasformato extends Prodotto {


	private Long prodottoBaseId;

	private String certificato;
	private String metodoTrasformazione;

	// costruttore JPA
	protected ProdottoTrasformato() {

	}

	public ProdottoTrasformato(Long venditoreId,
							   String nome,
							   String categoria,
							   BigDecimal prezzo,
							   LocalDate dataScadenza,
							   Long prodottoBaseId,
							   String certificato,
							   String metodoTrasformazione) {
		super(venditoreId, nome, categoria, prezzo, dataScadenza);
		this.prodottoBaseId = prodottoBaseId;
		this.certificato = certificato;
		this.metodoTrasformazione = metodoTrasformazione;
	}

	public Long getProdottoBaseId() {
		return prodottoBaseId;
	}

	public void setProdottoBaseId(Long prodottoBaseId) {
		this.prodottoBaseId = prodottoBaseId;
	}

	public String getCertificato() {
		return certificato;
	}

	public void setCertificato(String certificato) {
		this.certificato = certificato;
	}

	public String getMetodoTrasformazione() {
		return metodoTrasformazione;
	}

	public void setMetodoTrasformazione(String metodoTrasformazione) {
		this.metodoTrasformazione = metodoTrasformazione;
	}


}