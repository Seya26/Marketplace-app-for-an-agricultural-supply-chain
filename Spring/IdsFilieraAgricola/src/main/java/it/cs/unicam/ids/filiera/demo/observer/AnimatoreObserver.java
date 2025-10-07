package it.cs.unicam.ids.filiera.demo.observer;

import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Evento;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Invito;

public class AnimatoreObserver implements Observer{

    @Override
    public void aggiorna(Notifica notifica, String messaggio) {
        UtenteVerificato animatore;

        if (notifica instanceof Invito invito) {
            animatore = invito.getEvento().getCreatore();
            animatore.getNotifiche().add(messaggio);
        }

        if (notifica instanceof Evento evento) {
            animatore = evento.getCreatore();
            animatore.getNotifiche().add(messaggio);
        }

    }}
