package it.cs.unicam.ids.filiera.demo.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CURATORE")
public class Curatore extends UtenteVerificato{


    protected Curatore() {}

    public Curatore(String nome, String cognome, String email, String password){
        super(nome,cognome,email,password);
    }
}
