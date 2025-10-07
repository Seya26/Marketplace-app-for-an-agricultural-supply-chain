package it.cs.unicam.ids.filiera.demo.model;

import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;

import java.io.Serializable;

public class Sessione implements Serializable {

    private UtenteVerificato utente;
    private Carrello carrello = Carrello.newCarrello();

    public Sessione() {}

    public Sessione(UtenteVerificato utente) {
        this.utente = utente;
        this.carrello = Carrello.newCarrello();
    }

    public void aggiornaCarrelloSvuota() {
        if (carrello != null) carrello.svuota();
    }

    public String mostraContenuto() {
        return (carrello == null) ? "Carrello vuoto" : carrello.toString();
    }

    public Carrello getCarrello() {
        if (carrello == null) carrello = Carrello.newCarrello();
        return carrello;
    }

    public void setCarrello(Carrello carrello) { this.carrello = carrello; }

    public UtenteVerificato getUtente() { return utente; }
    public void setUtente(UtenteVerificato utente) { this.utente = utente; }
}
