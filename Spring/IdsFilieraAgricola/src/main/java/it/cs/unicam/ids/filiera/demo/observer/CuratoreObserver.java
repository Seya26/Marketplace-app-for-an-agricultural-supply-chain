package it.cs.unicam.ids.filiera.demo.observer;

import it.cs.unicam.ids.filiera.demo.entity.Prodotto;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.repositories.UtenteRepository;
import org.springframework.stereotype.Component;


public class CuratoreObserver implements Observer{

    private UtenteRepository utenteRepository;

    @Override
    public void aggiorna(Notifica notifica, String messaggio) {
        if(notifica instanceof Prodotto prodotto){
            for(UtenteVerificato u : utenteRepository.findAllCuratori()){
                u.getNotifiche().add(messaggio);
            }
        }
    }
}


