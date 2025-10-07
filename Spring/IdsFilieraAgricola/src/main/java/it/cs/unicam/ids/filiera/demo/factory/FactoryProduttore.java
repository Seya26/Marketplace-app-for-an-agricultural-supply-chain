package it.cs.unicam.ids.filiera.demo.factory;

import it.cs.unicam.ids.filiera.demo.entity.Produttore;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;

public class FactoryProduttore implements CreaUtente {
    @Override
    public UtenteVerificato crea(String nome, String cognome, String email, String password, String codiceFiscale) {
        return new Produttore(nome, cognome, email, password, codiceFiscale);
    }
}
