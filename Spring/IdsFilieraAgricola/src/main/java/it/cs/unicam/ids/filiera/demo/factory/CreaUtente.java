package it.cs.unicam.ids.filiera.demo.factory;

import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;

public interface CreaUtente {
    UtenteVerificato crea(String nome, String cognome, String email, String password, String codiceFiscale);
}
