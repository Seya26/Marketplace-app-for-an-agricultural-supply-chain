package it.cs.unicam.ids.filiera.demo.controllers;

import it.cs.unicam.ids.filiera.demo.dtos.eventoDto.InvitoDTO;
import it.cs.unicam.ids.filiera.demo.dtos.eventoDto.RichiestaInvitoDTO;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.model.Sessione;
import it.cs.unicam.ids.filiera.demo.services.GestionaleService;
import it.cs.unicam.ids.filiera.demo.services.InvitoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping // nessun base path, voglio che le rotte siano definite nel metodo per utilizzare /utenti o /eventi
@Validated
public class InvitoController {

    private final InvitoService invitoService;

    public InvitoController(InvitoService invitoService) {
        this.invitoService = invitoService;
    }

    private UtenteVerificato current(HttpSession httpSession) {
        Sessione s = (Sessione) httpSession.getAttribute(GestionaleService.SESSIONE_KEY);
        if (s == null || s.getUtente() == null) {
            throw new it.cs.unicam.ids.filiera.demo.exceptions.UnauthorizedException("Utente non autenticato");
        }
        return s.getUtente();
    }

    // Crea inviti per un evento (solo creatore)
    // POST /eventi/{eventoId}/inviti
    @PostMapping("/eventi/{eventoId}/inviti")
    public ResponseEntity<List<InvitoDTO>> richiestaCreaInvito(@PathVariable("eventoId")  @Positive Long eventoId,
                                                               @Valid @RequestBody RichiestaInvitoDTO dto,
                                                               HttpSession session) {
        UtenteVerificato u = current(session);
        return ResponseEntity.ok(invitoService.creaInvito(u, eventoId, dto));
    }

    @DeleteMapping("/eventi/invito/elimina/{invitoId}")
    public ResponseEntity<String> richiestaEliminaInvito(@PathVariable @Positive Long invitoId, HttpSession session){
        UtenteVerificato u = this.current(session);
        return ResponseEntity.ok(invitoService.eliminaInvito(u, invitoId));
    }


    /** Lista tutti inviti di un evento (solo creatore) */
    @GetMapping("/eventi/{eventoId}/inviti")
    public ResponseEntity<List<InvitoDTO>> richiestaVisualizzaInvitiEvento(@PathVariable("eventoId") @Positive Long eventoId,
                                                                           HttpSession session) {
        UtenteVerificato u = current(session);
        return ResponseEntity.ok(invitoService.visualizzaTuttiInviti(u, eventoId));
    }


    // Lista inviti ricevuti da un utente (utente in sessione)
    // GET /utenti/inviti/ricevuti
    @GetMapping("inviti/ricevuti")
    public ResponseEntity<List<InvitoDTO>> richiestaVisualizzaInvitiRicevuti(HttpSession session) {
        UtenteVerificato u = current(session);
        return ResponseEntity.ok(invitoService.getInvitiRicevuti(u));
    }

    /** RISPOSTA invito (ACCETTA|RIFIUTA) – solo invitato */
    @PostMapping("/inviti/{invitoId}/{azione}")
    public ResponseEntity<InvitoDTO> richiestaRispondiInvito(@PathVariable @Positive Long invitoId,
                                                             @PathVariable
                                                             @Pattern(regexp = "ACCETTA|RIFIUTA", flags = Pattern.Flag.CASE_INSENSITIVE)
                                                             String azione,
                                                             HttpSession session) {
        UtenteVerificato u = current(session);
        return ResponseEntity.ok(invitoService.rispondiInvito(u, invitoId, azione));
    }


}
