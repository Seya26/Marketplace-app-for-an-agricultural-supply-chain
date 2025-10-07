package it.cs.unicam.ids.filiera.demo.factory;

import it.cs.unicam.ids.filiera.demo.entity.Acquirente;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;

public class FactoryAcquirente implements CreaUtente {
    @Override
    public UtenteVerificato crea(String nome, String cognome, String email, String password, String codiceFiscale) {
        return new Acquirente(nome, cognome, email, password);
    }
}
