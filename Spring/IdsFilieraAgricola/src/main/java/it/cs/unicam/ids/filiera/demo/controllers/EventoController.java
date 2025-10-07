package it.cs.unicam.ids.filiera.demo.controllers;

import it.cs.unicam.ids.filiera.demo.dtos.UtenteDTO;
import it.cs.unicam.ids.filiera.demo.dtos.eventoDto.EventoDTO;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.model.Sessione;
import it.cs.unicam.ids.filiera.demo.services.EventoService;
import it.cs.unicam.ids.filiera.demo.services.GestionaleService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventi")
@Validated
public class EventoController {
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }


    /*
       VISUALIZZAZIONE EVENTI
     */

    @GetMapping()
    public ResponseEntity<List<EventoDTO>> richiestaVisualizzaTuttiEventi() {
        return ResponseEntity.ok(eventoService.visualizzaTuttiEventi());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> richiestaVisualizzaEvento(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(eventoService.visualizzaEvento(id));
    }

    @GetMapping("/miei")
    public ResponseEntity<List<EventoDTO>> richiestaVisualizzaMieiEventi(HttpSession session) {
        UtenteVerificato u = getUtenteCorrente(session);
        return ResponseEntity.ok(eventoService.visualizzaMieiEventi(u));
    }

    @GetMapping("/{id}/partecipanti")
    public ResponseEntity<List<UtenteDTO>> richiestaVisualizzaPartecipantiEvento(@PathVariable @Positive Long id, HttpSession session) {
        UtenteVerificato u = getUtenteCorrente(session);
        return ResponseEntity.ok(eventoService.visualizzaPartecipantiEvento(u, id));
    }


    /*
       CREAZIONE E GESTIONE EVENTI
     */

    @PostMapping("/crea")
    public ResponseEntity<EventoDTO> richiestaCreaEvento(@Valid @RequestBody EventoDTO eventoDTO, HttpSession session) {
        UtenteVerificato u = getUtenteCorrente(session);
        return ResponseEntity.ok(eventoService.creaEvento(u, eventoDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EventoDTO> richiestaModificaEvento(@PathVariable @Positive Long id,
                                                             @Valid @RequestBody EventoDTO eventoDTO,
                                                             HttpSession session) {
        UtenteVerificato u = getUtenteCorrente(session);
        return ResponseEntity.ok(eventoService.modificaEvento(u, id, eventoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> richiestaEliminaEvento(@PathVariable @Positive Long id, HttpSession session) {
        UtenteVerificato u = getUtenteCorrente(session);
        return ResponseEntity.ok(eventoService.eliminaEvento(u, id));
    }


    /*
       ISCRIZIONE AGLI EVENTI
     */

    @PostMapping("/{id}/iscrivi")
    public ResponseEntity<EventoDTO> richiestaIscriviUtenteEvento(@PathVariable @Positive Long id, HttpSession session) {
        UtenteVerificato u = getUtenteCorrente(session);
        return ResponseEntity.ok(eventoService.iscriviUtenteEvento(u, id));
    }

    @PostMapping("/{id}/disiscrivi")
    public ResponseEntity<EventoDTO> richiestaAnnullaIscrizione(@PathVariable @Positive Long id, HttpSession session) {
        UtenteVerificato u = getUtenteCorrente(session);
        return ResponseEntity.ok(eventoService.annullaIscrizione(u, id));
    }


    /*
     * UTILITY
     */
    private UtenteVerificato getUtenteCorrente(HttpSession httpSession) {
        Sessione s = (Sessione) httpSession.getAttribute(GestionaleService.SESSIONE_KEY);
        if (s == null || s.getUtente() == null) {
            throw new it.cs.unicam.ids.filiera.demo.exceptions.UnauthorizedException("Utente non autenticato");
        }
        return s.getUtente();
    }


}



