package it.cs.unicam.ids.filiera.demo;

import it.cs.unicam.ids.filiera.demo.entity.*;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Evento;
import it.cs.unicam.ids.filiera.demo.repositories.EventoRepository;
import it.cs.unicam.ids.filiera.demo.repositories.ProdottoRepository;
import it.cs.unicam.ids.filiera.demo.repositories.UtenteRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Classe di configurazione per il caricamento iniziale dei dati di esempio nel database.
@Configuration
public class DataLoader {

    @Bean
    @Transactional
    public ApplicationRunner seedData(
            UtenteRepository utenteRepo,
            ProdottoRepository prodottoRepo,
            EventoRepository eventoRepo
    ) {

        return args -> {
            // utenti già verificati, pronti per login
            Gestore gestore = new Gestore("Giulia", "Bianchi", "gestore@example.com", "psw");
            gestore.setVerificato(true);

            Animatore animatore = new Animatore("Fabrizio", "Romano", "animatore@example.com", "psw");
            animatore.setVerificato(true);

            Produttore produttore = new Produttore("Nico", "Schira", "produttore@example.com", "psw", "PRDNCX80A01H501X");
            produttore.setVerificato(true);

            Trasformatore trasformatore = new Trasformatore("Luca", "Verdi", "trasformatore@example.com", "psw", "TRSVRD80A01H501X");
            trasformatore.setVerificato(true);

            Distributore distributore = new Distributore("Sara", "Rossi", "distributore@example.com", "psw", "DSRRSS80A01H501X");
            distributore.setVerificato(true);

            Acquirente acquirente = new Acquirente("Mario", "Rossi", "acquirente@example.com", "psw");
            acquirente.setVerificato(true);

            // utenti da verificare per il gestore
            Acquirente acquirenteDaVerificare = new Acquirente("Alfredo", "Pedullà", "alfredo@example.com", "psw");
            Produttore produttoreDaVerificare = new Produttore("Matteo", "Moretto", "matteo@example.com", "psw", "PRDMRT80A01H501X");

            utenteRepo.save(gestore);
            utenteRepo.save(animatore);
            utenteRepo.save(produttore);
            utenteRepo.save(trasformatore);
            utenteRepo.save(distributore);
            utenteRepo.save(acquirente);
            utenteRepo.save(acquirenteDaVerificare);
            utenteRepo.save(produttoreDaVerificare);

            // PRODOTTI BASE
            Prodotto mele = new Prodotto(
                    produttore.getId(),
                    "Mele Bio",
                    "Frutta",
                    new BigDecimal("2.50"),
                    LocalDate.now().plusMonths(1)
            );
            mele.setCreatore(produttore);
            mele.setQuantita(100);
            mele.setAttesa(false);
            mele.setConfermato(true);

            Prodotto latte = new Prodotto(
                    produttore.getId(),
                    "Latte Fresco",
                    "Latticini",
                    new BigDecimal("1.60"),
                    LocalDate.now().plusWeeks(2)
            );
            latte.setCreatore(produttore);
            latte.setQuantita(200);
            latte.setAttesa(false);
            latte.setConfermato(true);

            prodottoRepo.save(mele);
            prodottoRepo.save(latte);

            // PRODOTTO TRASFORMATO (da "Mele Bio")
            ProdottoTrasformato succoMele = new ProdottoTrasformato(
                    trasformatore.getId(),
                    "Succo di Mele",
                    "Bevande",
                    new BigDecimal("3.90"),
                    LocalDate.now().plusMonths(2),
                    mele.getId(),
                    "HACCP-1234",
                    "Pastorizzazione"
            );
            succoMele.setCreatore(trasformatore);
            succoMele.setQuantita(50);
            succoMele.setAttesa(false);
            succoMele.setConfermato(true);

            prodottoRepo.save(succoMele);

            // BUNDLE (Distributore), contiene Latte + Succo
            Bundle boxColazione = new Bundle(
                    distributore.getId(),
                    "Box Colazione",
                    "Cestini",
                    BigDecimal.ZERO,
                    LocalDate.now().plusMonths(1)
            );
            boxColazione.setCreatore(distributore);
            boxColazione.setAttesa(false);
            boxColazione.setConfermato(true);
            boxColazione.aggiungiItem(latte, 2);
            boxColazione.aggiungiItem(succoMele, 1);

            BigDecimal prezzoBundle = boxColazione.getItems().stream()
                    .map(i -> i.getProdotto().getPrezzo().multiply(BigDecimal.valueOf(i.getQuantita())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            boxColazione.setPrezzo(prezzoBundle);

            prodottoRepo.save(boxColazione);

            // EVENTI
            // Futuro (prenotabile)
            Evento degustazione = new Evento();
            degustazione.setTitolo("Degustazione Prodotti Locali");
            degustazione.setDescrizione("Incontro con produttori, assaggi e pairing.");
            degustazione.setLuogo("Sala Comunale");
            degustazione.setDataInizio(LocalDateTime.now().plusDays(7));
            degustazione.setDataFine(LocalDateTime.now().plusDays(7).plusHours(2));
            degustazione.setPostiDisponibili(2);
            degustazione.setCreatore(animatore);
            // iscrivo un acquirente
            degustazione.aggiungiPartecipanteInvito(acquirente);

            // Passato (terminato)
            Evento fieraPassata = new Evento();
            fieraPassata.setTitolo("Fiera Agricola d'Estate");
            fieraPassata.setDescrizione("Evento passato per test.");
            fieraPassata.setLuogo("Piazza Centrale");
            fieraPassata.setDataInizio(LocalDateTime.now().minusDays(20));
            fieraPassata.setDataFine(LocalDateTime.now().minusDays(20).plusHours(8));
            fieraPassata.setPostiDisponibili(100);
            fieraPassata.setCreatore(animatore);

            eventoRepo.save(degustazione);
            eventoRepo.save(fieraPassata);
        };
    }
}

