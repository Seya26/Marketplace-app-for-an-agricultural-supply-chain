package it.cs.unicam.ids.filiera.demo.observer;

import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Evento;
import it.cs.unicam.ids.filiera.demo.factory.Ruolo;

public class AcquirenteObserver implements Observer {

    @Override
    public void aggiorna(Notifica notifica, String messaggio) {
        if (notifica instanceof Evento evento) {
            for (UtenteVerificato acquirentePartecipante : evento.getPartecipanti()) {
                if (acquirentePartecipante.getRuolo() == Ruolo.ACQUIRENTE) {
                    acquirentePartecipante.getNotifiche().add(messaggio);
                }
            }
        }
    }
}
