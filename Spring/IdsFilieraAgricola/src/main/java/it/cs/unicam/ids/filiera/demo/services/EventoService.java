package it.cs.unicam.ids.filiera.demo.services;

import it.cs.unicam.ids.filiera.demo.dtos.UtenteDTO;
import it.cs.unicam.ids.filiera.demo.dtos.UtenteMapper;
import it.cs.unicam.ids.filiera.demo.dtos.eventoDto.EventoDTO;
import it.cs.unicam.ids.filiera.demo.dtos.eventoDto.EventoMapper;
import it.cs.unicam.ids.filiera.demo.entity.Acquirente;
import it.cs.unicam.ids.filiera.demo.entity.Animatore;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Evento;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.exceptions.ForbiddenException;
import it.cs.unicam.ids.filiera.demo.repositories.EventoRepository;
import it.cs.unicam.ids.filiera.demo.repositories.InvitoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventoService {

    private final EventoRepository eventoRepository;
    private final InvitoRepository invitoRepository;

    public EventoService(EventoRepository eventoRepository, InvitoRepository invitoRepository) {
        this.eventoRepository = eventoRepository;
        this.invitoRepository = invitoRepository;
    }

    /*
        METODI DI LETTURA
    */
    @Transactional(readOnly = true)
    public List<EventoDTO> visualizzaTuttiEventi() {
        return eventoRepository.findAll()
                .stream()
                .map(EventoMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoDTO visualizzaEvento(Long id) {
        Evento e = eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento con id " + id + " non trovato"));
        return EventoMapper.toDto(e);
    }

    /** Eventi creati dall’animatore (solo ANIMATORE) */
    @Transactional(readOnly = true)
    public List<EventoDTO> visualizzaMieiEventi(UtenteVerificato animatore) {
        controllaAnimatore(animatore);
        List<Evento> eventi = eventoRepository.findByCreatoreId(animatore.getId());
        if (eventi.isEmpty()) {
            throw new IllegalStateException("L'animatore non ha creato eventi");
        }
        return eventi.stream().map(EventoMapper::toDto).toList();
    }

    /** Partecipanti di un evento (solo creatore) */
    @Transactional(readOnly = true)
    public List<UtenteDTO> visualizzaPartecipantiEvento(UtenteVerificato animatore, Long eventoId) {
        controllaAnimatore(animatore);
        Evento e = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento con id " + eventoId + " non trovato"));
        controllaCreatore(animatore, e);
        return e.getPartecipanti().stream().map(UtenteMapper::toDto).toList();
    }

    /** Eventi a cui l’utente è iscritto */
    @Transactional(readOnly = true)
    public List<EventoDTO> visualizzaMieiEventiIscritto(UtenteVerificato user) {
        List<Evento> eventi = eventoRepository.findByPartecipantiId(user.getId());
        return eventi.stream().map(EventoMapper::toDto).toList();
    }

    /*
        METODI DI SCRITTURA
    */
    public EventoDTO creaEvento(UtenteVerificato animatore, EventoDTO evento) {
        controllaAnimatore(animatore);
        Evento e = EventoMapper.toEntity(evento);
        e.setCreatore(animatore);
        e = eventoRepository.save(e);
        return EventoMapper.toDto(e);
    }

    public EventoDTO modificaEvento(UtenteVerificato animatore, Long id, EventoDTO evento) {
        controllaAnimatore(animatore);
        Evento e = eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento con id " + id + " non trovato"));
        controllaCreatore(animatore, e);
        EventoMapper.applicaModifica(e, evento);
        e = eventoRepository.save(e);
        return EventoMapper.toDto(e);
    }

    public boolean eliminaEvento(UtenteVerificato animatore, Long id) {
        controllaAnimatore(animatore);
        Evento e = eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento con id " + id + " non trovato"));
        controllaCreatore(animatore, e);
        e.cancella(); // <-- notifica partecipanti se data inizio futura
        invitoRepository.deleteByEventoId(e.getId()); // elimina inviti collegati
        eventoRepository.delete(e);
        return true;
    }

    /** Iscrizione (solo ACQUIRENTE) con capienza e doppioni gestiti dal dominio */
    public EventoDTO iscriviUtenteEvento(UtenteVerificato user, Long eventoId) {
        if (!(user instanceof Acquirente))
            throw new IllegalStateException("solo utenti Acquirenti possono iscriversi a eventi");

        Evento e = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento con id " + eventoId + " non trovato"));

        if (e.contienePartecipante(user)) {
            throw new IllegalStateException("Utente già iscritto all'evento");
        }

        e.aggiungiPartecipanteIscrizione(user); // controlla capienza internamente
        e = eventoRepository.save(e);
        return EventoMapper.toDto(e);
    }

    /** Disiscrizione (solo ACQUIRENTE) */
    public EventoDTO annullaIscrizione(UtenteVerificato user, Long eventoId) {
        if (!(user instanceof Acquirente))
            throw new IllegalStateException("solo utenti Acquirenti possono disiscriversi da eventi");

        Evento e = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento con id " + eventoId + " non trovato"));

        if (!e.contienePartecipante(user))
            throw new IllegalStateException("Utente non iscritto all'evento");

        e.rimuoviPartecipante(user);
        e = eventoRepository.save(e);
        return EventoMapper.toDto(e);
    }

    /*
        UTILITY (regole di dominio)
    */
    private void controllaAnimatore(UtenteVerificato user) {
        if (!(user instanceof Animatore)) {
            throw new ForbiddenException("Operazione consentita solo ad ANIMATORE");
        }
    }

    private void controllaCreatore(UtenteVerificato user, Evento e) {
        if (e.getCreatore() == null || !e.getCreatore().getId().equals(user.getId())) {
            throw new ForbiddenException("Operazione consentita solo al creatore dell'evento");
        }
    }
}
