package it.cs.unicam.ids.filiera.demo.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ANIMATORE")
public class Animatore extends UtenteVerificato {
    protected Animatore() {}
    public Animatore(String nome, String cognome, String email, String password) {
        super(nome, cognome, email, password);
    }
}

