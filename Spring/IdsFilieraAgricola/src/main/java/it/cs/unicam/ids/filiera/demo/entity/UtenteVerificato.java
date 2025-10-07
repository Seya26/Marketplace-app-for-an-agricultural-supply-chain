package it.cs.unicam.ids.filiera.demo.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.cs.unicam.ids.filiera.demo.factory.Ruolo;
import it.cs.unicam.ids.filiera.demo.observer.Observer;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ruolo", discriminatorType = DiscriminatorType.STRING)
public abstract class UtenteVerificato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cognome;
    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "utente_notifiche",
            joinColumns = @JoinColumn(name = "utente_id")
    )
    @Column(name = "notifica")
    private List<String> notifiche = new ArrayList<>();

    @Column(name = "ruolo", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Ruolo ruolo;

    @Column(name = "verificato")
    private boolean verificato;

    protected UtenteVerificato() {}

    public UtenteVerificato(String nome, String cognome, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.verificato = false;
    }

    public UtenteVerificato(String nome, String cognome, String email, String password, String codiceFiscale) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.verificato = false;
    }


    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Ruolo getRuolo() { return ruolo; }
    public List<String> getNotifiche(){ return this.notifiche; }
    public boolean isVerificato() { return verificato; }
    public void setVerificato(boolean verificato) {
        if (verificato) {
            this.notifiche.add("Il tuo account è stato verificato in data: " + java.time.LocalDateTime.now());
        }
        this.verificato = verificato;
    }
}
