package it.cs.unicam.ids.filiera.demo.observer;

import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Evento;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Invito;
import it.cs.unicam.ids.filiera.demo.factory.Ruolo;
import it.cs.unicam.ids.filiera.demo.entity.Prodotto;

public class VenditoreObserver implements Observer {

    @Override
    public void aggiorna(Notifica notifica, String messaggio) {
        if (notifica instanceof Invito invito) {
            UtenteVerificato invitato = invito.getInvitato();
            invitato.getNotifiche().add(messaggio);
        }

        if (notifica instanceof Evento evento) {
            for (UtenteVerificato partecipante : evento.getPartecipanti()) {
                if (partecipante.getRuolo() == Ruolo.PRODUTTORE || partecipante.getRuolo() == Ruolo.DISTRIBUTORE || partecipante.getRuolo() == Ruolo.TRASFORMATORE) {
                    partecipante.getNotifiche().add(messaggio);
                }
            }
        }

        if(notifica instanceof Prodotto prodotto){
            UtenteVerificato utente = prodotto.getCreatore();
            utente.getNotifiche().add(messaggio);
        }
    }
}



