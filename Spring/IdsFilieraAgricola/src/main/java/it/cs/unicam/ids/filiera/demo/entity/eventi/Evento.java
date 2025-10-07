package it.cs.unicam.ids.filiera.demo.entity.eventi;

import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.observer.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity

@Table(name = "eventi")
public class Evento implements Notifica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titolo;

    @Column(length = 4000)
    private String descrizione;

    @Column(nullable = false)
    private String luogo;

    @Column(name = "data_inizio", nullable = false)
    private LocalDateTime dataInizio;

    @Column(name = "data_fine", nullable = false)
    private LocalDateTime dataFine;

    // capienza massima. Se <=0 posti illimitati
    @Column(name = "capienza_max")
    private int capienzaMax;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creatore_id", nullable = false)
    private UtenteVerificato creatore;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "evento_partecipanti",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "utente_id")
    )
    private List<UtenteVerificato> partecipanti = new ArrayList<>();

    @Transient // non persistente
    private List<Observer> observers = new ArrayList<>();

    // Costruttore richiesto da JPA
    public Evento() {
    }

    //true se evento a posti illimitati
    @Transient // non persistente
    public boolean isIllimitato() {
        return capienzaMax <= 0;
    }

    @PostLoad
    private void initObservers() {
        this.observers = new ArrayList<>();
        this.sub(new VenditoreObserver());
        this.sub(new AcquirenteObserver());
        this.sub(new AnimatoreObserver());
    }

    // Aggiunge partecipante tramite iscrizione per notificare il creatore
    public void aggiungiPartecipanteIscrizione(UtenteVerificato u) {
        this.aggiungiPartecipante(u);
        // Notifica al creatore dell'evento
        notifyObservers("Nuova iscrizione all'evento: " + this.titolo + " da parte di " + u.getNome() + " " + u.getCognome());
        controllaSePienoENotifica();
    }

    // Aggiunge partecipante tramite Invito, la notifica avviene dall'Invito
    public void aggiungiPartecipanteInvito(UtenteVerificato u) {
        this.aggiungiPartecipante(u);
        controllaSePienoENotifica();
    }

    public void rimuoviPartecipante(UtenteVerificato u) {
        partecipanti.removeIf(p -> p.getId().equals(u.getId()));
    }

    public boolean contienePartecipante(UtenteVerificato u) {
        return partecipanti.stream().anyMatch(p -> p.getId().equals(u.getId()));
    }


    public int getPostiRimasti() {
        return isIllimitato() ? Integer.MAX_VALUE
                : Math.max(0, capienzaMax - partecipanti.size());
    }

    public void setPostiDisponibili(int nuovi) {
        if (nuovi <= 0) {
            this.capienzaMax = -1;
            return; // illimitato
        }
        if (nuovi < partecipanti.size()) {
            throw new IllegalStateException(
                    "Capienza (" + nuovi + ") < partecipanti già iscritti (" + partecipanti.size() + ")"
            );
        }
        this.capienzaMax = nuovi;
    }

    public void cancella() {
        // notifica solo se data inizio futura
        if (dataInizio.isAfter(LocalDateTime.now()))
            notifyObservers("L'evento " + this.titolo + " è stato cancellato.");

    }

    private void aggiungiPartecipante(UtenteVerificato u) {
         if (u == null)
            throw new IllegalStateException("Utente nullo");

        // non si può aggiungere partecipante già iscritto
        if (contienePartecipante(u)) {
            return;
        }
        // non si può aggiungere partecipante se evento pieno
        if (!isIllimitato() && partecipanti.size() >= capienzaMax) {
            throw new IllegalStateException("Evento pieno, impossibile aggiungere partecipante");
        }
        // non si può aggiungere partecipante se evento già iniziato o concluso
        if (dataFine.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Evento già concluso, impossibile aggiungere partecipante");
        }
        partecipanti.add(u);
    }

    private void controllaSePienoENotifica() {
        if (!isIllimitato() && partecipanti.size() >= capienzaMax) {
            notifyObservers("L'evento " + this.titolo + " non ha più posti disponibili.");
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
}

