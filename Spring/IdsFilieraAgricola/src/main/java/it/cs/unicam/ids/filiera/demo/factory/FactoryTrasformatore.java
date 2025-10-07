package it.cs.unicam.ids.filiera.demo.factory;

import it.cs.unicam.ids.filiera.demo.entity.Trasformatore;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;


public class FactoryTrasformatore implements CreaUtente {
    @Override
    public UtenteVerificato crea(String nome, String cognome, String email, String password, String codiceFiscale) {
        return new Trasformatore(nome, cognome, email, password, codiceFiscale);
    }
}
