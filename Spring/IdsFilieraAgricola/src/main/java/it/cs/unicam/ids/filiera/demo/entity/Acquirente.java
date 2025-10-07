package it.cs.unicam.ids.filiera.demo.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ACQUIRENTE")
public class Acquirente extends UtenteVerificato {

    protected Acquirente() {}

    public Acquirente(String nome, String cognome, String email, String password) {
        super(nome, cognome, email, password);
    }

}