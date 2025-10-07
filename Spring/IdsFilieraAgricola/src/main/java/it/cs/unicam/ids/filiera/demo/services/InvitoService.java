package it.cs.unicam.ids.filiera.demo.services;

import it.cs.unicam.ids.filiera.demo.dtos.eventoDto.InvitoDTO;
import it.cs.unicam.ids.filiera.demo.dtos.eventoDto.InvitoMapper;
import it.cs.unicam.ids.filiera.demo.dtos.eventoDto.RichiestaInvitoDTO;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.entity.Venditore;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Evento;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Invito;
import it.cs.unicam.ids.filiera.demo.entity.eventi.InvitoStato;
import it.cs.unicam.ids.filiera.demo.exceptions.ForbiddenException;
import it.cs.unicam.ids.filiera.demo.repositories.EventoRepository;
import it.cs.unicam.ids.filiera.demo.repositories.InvitoRepository;
import it.cs.unicam.ids.filiera.demo.repositories.UtenteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class InvitoService {

    private final InvitoRepository invitoRepo;
    private final EventoRepository eventoRepo;
    private final UtenteRepository utenteRepo;


    public List<InvitoDTO> creaInvito(UtenteVerificato utente, Long eventoId, RichiestaInvitoDTO dto) {
        // 1) Carico l'evento dal DB o lancio eccezione se non esiste
        Evento evento = eventoRepo.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato"));

        // 2) Autorizzazione: solo il creatore dell'evento può inviare inviti
        requireCreator(utente, evento);

        // impossibile invitare se l'evento è terminato
        ensureEventoNonTerminato(evento);

        // 4) Per ogni id utente nella richiesta, controllo che sia Venditore, evito duplicati e salvo invito
        List<InvitoDTO> risultati = new ArrayList<>();

        for (Long invId : dto.idUtentiList()) {
            // 1. Carico l'utente invitato dal DB o lancio eccezione se non esiste
            UtenteVerificato invitato = utenteRepo.findById(invId)
                    .orElseThrow(() -> new IllegalArgumentException("Utente con id " + invId + " non trovato"));

            // 2. Controllo che l'utente sia un Venditore
            if (!(invitato instanceof Venditore)) {
                throw new IllegalArgumentException("L'utente con id " + invId + " non è un Venditore");
            }

            // 3. Controllo che non esista già un invito per questo utente e evento
            var esistente = invitoRepo.findByEventoIdAndInvitatoId(eventoId, invId);
            if (esistente.isPresent()) {
                risultati.add(InvitoMapper.toDTO(esistente.get()));
                continue;
            }
            // 4. Creo e salvo il nuovo invito (in attesa by default)
            Invito inv = new Invito(evento, invitato, dto.messaggio());
            inv = invitoRepo.save(inv);
            risultati.add(InvitoMapper.toDTO(inv));
        }
        return risultati;
    }

    public String eliminaInvito(UtenteVerificato utente, Long id) {
        var invito = invitoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invito non trovato"));

        // Autorizzazione: solo il creatore dell'evento può eliminare gli inviti
        requireCreator(utente, invito.getEvento());

        invitoRepo.delete(invito);
        return "Invito : " + invito.toString() + " eliminato";
    }

    /**
     * Risponde a un invito (azione = accetta o rifiuta) - solo invitato
     */
    public InvitoDTO rispondiInvito(UtenteVerificato utente, Long invitoId, String azione) {

        var invito = invitoRepo.findById(invitoId)
                .orElseThrow(() -> new IllegalArgumentException("Invito non trovato"));

        // autorizzazione: solo l'invitato può rispondere
        if (!Objects.equals(invito.getInvitato().getId(), utente.getId())) {
            throw new ForbiddenException("Operazione consentita solo all'invitato");
        }

        // stato: posso rispondere solo se l'invito è in attesa
        if (invito.getStato() != InvitoStato.IN_ATTESA) {
            throw new IllegalStateException("Impossibile rispondere: invito già " + invito.getStato());
        }

        // impossibile rispondere se l'evento è terminato
        var evento2 = invito.getEvento();
        ensureEventoNonTerminato(evento2);

        // azione: accetta o rifiuta
        azione = azione.toUpperCase();
        switch (azione) {
            case "ACCETTA" -> {
                var evento = invito.getEvento();
                evento.aggiungiPartecipanteInvito(utente);
                invito.setStato(InvitoStato.ACCETTATO);
            }
            case "RIFIUTA" -> {
                invito.setStato(InvitoStato.RIFIUTATO);
            }
        }
        return InvitoMapper.toDTO(invitoRepo.save(invito));
    }


    /**
     * Recupera gli inviti ricevuti da un utente specifico.
     */
    public List<InvitoDTO> getInvitiRicevuti(UtenteVerificato invitato) {
        return invitoRepo.findByInvitatoId(invitato.getId())
                .stream()
                .map(InvitoMapper::toDTO)
                .toList();
    }


    /**
     * Lista tutti inviti di un evento (solo creatore)
     */
    public List<InvitoDTO> visualizzaTuttiInviti(UtenteVerificato creatore, Long eventoId) {
        Evento evento = eventoRepo.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato"));

        requireCreator(creatore, evento);

        return invitoRepo.findByEventoId(eventoId)
                .stream()
                .map(InvitoMapper::toDTO)
                .toList();

    }


    private void requireCreator(UtenteVerificato utente, Evento evento) {
        if (evento.getCreatore() == null || !evento.getCreatore().getId().equals(utente.getId())) {
            throw new ForbiddenException("Operazione consentita solo al creatore dell'evento");
        }
    }

    private void ensureEventoNonTerminato(Evento e) {
        LocalDateTime now = LocalDateTime.now();
        if (e.getDataFine() != null && e.getDataFine().isBefore(now)) {
            String when = e.getDataFine().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            throw new IllegalStateException("Evento terminato il " + when);
        }
    }

}
