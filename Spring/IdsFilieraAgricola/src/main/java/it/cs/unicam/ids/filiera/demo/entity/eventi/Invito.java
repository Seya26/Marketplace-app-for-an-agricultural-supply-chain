package it.cs.unicam.ids.filiera.demo.entity.eventi;

import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.observer.AnimatoreObserver;
import it.cs.unicam.ids.filiera.demo.observer.Notifica;
import it.cs.unicam.ids.filiera.demo.observer.Observer;
import it.cs.unicam.ids.filiera.demo.observer.VenditoreObserver;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
public class Invito implements Notifica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    @Setter
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invitato_id", nullable = false)
    @Setter
    private UtenteVerificato invitato;

    @Setter
    private String messaggio;

    @Enumerated(EnumType.STRING)
    private InvitoStato stato;

    @Transient
    private List<Observer> observers;

    protected Invito() {
    }

    public Invito(Evento evento, UtenteVerificato invitato, String messaggio) {
        this.evento = Objects.requireNonNull(evento);
        this.invitato = Objects.requireNonNull(invitato);
        this.messaggio = messaggio;
        this.stato = InvitoStato.IN_ATTESA;

        this.observers = new ArrayList<>();
        this.sub(new VenditoreObserver());
        notifyObservers("Sei stato invitato a partecipare all' evento: " + evento.getTitolo());
    }

    public void setStato(InvitoStato nuovoStato) {
        this.stato = nuovoStato;

        switch (nuovoStato) {
            case ACCETTATO -> notifyObservers("L'invito all'evento '" + evento.getTitolo()
                    + " è stato ACCETTATO da " + invitato.getNome() + invitato.getCognome());
            case RIFIUTATO -> notifyObservers("L'invito all'evento '" + evento.getTitolo()
                    + " è stato RIFIUTATO da " + invitato.getNome() + invitato.getCognome());
            default -> { /* Non fare nulla */ }
        }
    }

    @Override
    public void sub(Observer o) {
        observers.add(o);
    }

    @Override
    public void unsub(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer o : observers) {
            o.aggiorna(this, message);
        }
    }

    @PostLoad
    private void initObservers() {
        this.observers = new ArrayList<>();
        this.sub(new VenditoreObserver());
        this.sub(new AnimatoreObserver());
    }
}
