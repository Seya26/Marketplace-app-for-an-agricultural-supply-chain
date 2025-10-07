package it.cs.unicam.ids.filiera.demo.factory;

import it.cs.unicam.ids.filiera.demo.entity.Animatore;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;

public class FactoryAnimatore implements CreaUtente {
    @Override
    public UtenteVerificato crea(String nome, String cognome, String email, String password, String codiceFiscale) {
        return new Animatore(nome, cognome, email, password);
    }
}
