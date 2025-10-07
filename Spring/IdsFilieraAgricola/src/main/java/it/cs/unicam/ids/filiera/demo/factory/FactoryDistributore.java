package it.cs.unicam.ids.filiera.demo.factory;

import it.cs.unicam.ids.filiera.demo.entity.Distributore;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;

public class FactoryDistributore implements CreaUtente {
    @Override
    public UtenteVerificato crea(String nome, String cognome, String email, String password, String codiceFiscale) {
        return new Distributore(nome, cognome, email, password, codiceFiscale);
    }
}
