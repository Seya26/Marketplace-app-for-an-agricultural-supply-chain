package it.cs.unicam.ids.filiera.demo.controllers;

import it.cs.unicam.ids.filiera.demo.dtos.*;
import it.cs.unicam.ids.filiera.demo.dtos.eventoDto.EventoDTO;
import it.cs.unicam.ids.filiera.demo.entity.Ordine;
import it.cs.unicam.ids.filiera.demo.entity.Prodotto;
import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.model.Sessione;
import it.cs.unicam.ids.filiera.demo.services.EventoService;
import it.cs.unicam.ids.filiera.demo.services.GestionaleService;
import it.cs.unicam.ids.filiera.demo.services.UtenteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/utenti")
@Validated
public class UtenteController {

    private final UtenteService utenteService;
    private final EventoService eventoService;


    @Autowired
    public UtenteController(UtenteService utenteService, EventoService eventoService) {
        this.utenteService = utenteService;
        this.eventoService = eventoService;
    }

    /**
     * AUTENTICAZIONE (REGISTRAZIONE / LOGIN / LOGOUT)
     */

    @PostMapping("/registrazione")
    public ResponseEntity<UtenteDTO> richiestaRegistrazione(@Valid @RequestBody RegistrazioneDTO dto) {
        return ResponseEntity.ok(utenteService.registraUtente(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<UtenteDTO> login(@Valid @RequestBody LoginDTO dto, HttpSession httpSession) {
        Sessione s = utenteService.login(dto.email(), dto.password());
        httpSession.setAttribute(GestionaleService.SESSIONE_KEY, s);
        return ResponseEntity.ok(UtenteMapper.toDto(s.getUtente()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout effettuato con successo");
    }



    /**
     * LETTURA / GESTIONE UTENTI
     */

    @GetMapping("/{id}")
    public ResponseEntity<?> richiestaVisualizzaUtente(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(utenteService.visualizzaUtente(id));
    }

    @GetMapping()
    public ResponseEntity<List<UtenteDTO>> richiestaVisualizzaTuttiUtenti() {
        return ResponseEntity.ok(utenteService.visualizzaTuttiUtenti());
    }

    // Lista di venditori
    @GetMapping("/venditori")
    public ResponseEntity<List<UtenteDTO>> richiestaVisualizzaVenditori() {
        return ResponseEntity.ok(utenteService.visualizzaVenditori());
    }

    @GetMapping("/non-approvati")
    public ResponseEntity<List<UtenteDTO>> richiestaVisualizzaUtentiNonApprovati(){
        return ResponseEntity.ok(utenteService.visualizzaUtentiNonApprovati());
    }

    @PostMapping("/{scelta}/{id}")
    public ResponseEntity<UtenteDTO> richiestaApprovaUtente(@PathVariable Long id, @PathVariable @Pattern(regexp = "ACCETTA|RIFIUTA", flags = Pattern.Flag.CASE_INSENSITIVE) String scelta, HttpSession session){
        return ResponseEntity.ok(utenteService.approvaUtente(id, scelta, this.getUtenteCorrente(session)));
    }


    /**
     * PRODOTTI & ORDINI DELL'UTENTE
     */

    // Lista di prodotti posseduti da un utente
    @GetMapping("/{id}/prodotti")
    public ResponseEntity<List<Prodotto>> richiestaVisualizzaProdottiUtente(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(utenteService.prodottiPosseduti(id));
    }

    /**
     * Restituisce gli ordini dell'utente in sessione.
     */
    @GetMapping("/ordini")
    public ResponseEntity<List<Ordine>> richiestaVisualizzaOrdini(HttpSession session) {
        List<Ordine> ordini = utenteService.ordiniUtente(session);
        return ResponseEntity.ok(ordini);
    }


    /**
     * NOTIFICHE UTENTE
     */
    @GetMapping("/notifiche/visualizza")
    public ResponseEntity<List<String>> richiestaVisualizzaNotifiche(HttpSession httpSession) {
        List<String> notifiche = utenteService.visualizzaNotifiche(this.getUtenteCorrente(httpSession));
        return ResponseEntity.ok(notifiche);
    }

    @DeleteMapping("/notifiche/svuota")
    public ResponseEntity<String> richiestaSvuotaNotifiche(HttpSession httpSession) {
        return ResponseEntity.ok(utenteService.svuotaNotifiche(this.getUtenteCorrente(httpSession)));
    }


    /**
     * EVENTI: ISCRIZIONI DELL'UTENTE 
     */

    // Ritorna la lista di eventi a cui l'utente in sessione è iscritto
    @GetMapping("/eventi/mie-iscrizioni")
    public ResponseEntity<List<EventoDTO>> richiestaEventiMieiIscritti(HttpSession session) {
        UtenteVerificato u = getUtenteCorrente(session);
        return ResponseEntity.ok(eventoService.visualizzaMieiEventiIscritto(u));
    }


    /**
     * METODI TESTING
     */
    
    @PostMapping("/login-test")
    public ResponseEntity<String> loginFittizio(HttpSession session) {
        utenteService.loginFittizio(session);
        return ResponseEntity.ok("Login fittizio completato");
    }


    @PostMapping("/login-animatore-test")
    public ResponseEntity<String> loginAnimatoreFittizio(HttpSession session) {
        utenteService.loginAnimatoreFittizio(session);
        return ResponseEntity.ok("Login animatore fittizio completato");
    }

    @PostMapping("/login-venditore-test")
    public ResponseEntity<String> loginVenditoreFittizio(HttpSession session) {
        utenteService.loginVenditoreFittizio(session);
        return ResponseEntity.ok("Login venditore fittizio completato");
    }

    // per testare chi è l'utente in sessione
    @GetMapping("/whoami")
    public ResponseEntity<?> whoAmI(HttpSession session) {

        Sessione s = (Sessione) session.getAttribute(GestionaleService.SESSIONE_KEY);
        UtenteVerificato u = (s != null) ? s.getUtente() : null;


        if (u == null) u = (UtenteVerificato) session.getAttribute("utente");

        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nessun utente in sessione");
        }

        WhoAmIDTO dto = new WhoAmIDTO(
                u.getId(),
                u.getNome(),
                u.getCognome(),
                u.getEmail(),
                (u.getRuolo() != null ? u.getRuolo().name() : u.getClass().getSimpleName()),
                session.getId()
        );
        return ResponseEntity.ok(dto);
    }
    // per testare l'impersonificazione di un utente
    @PostMapping("/impersona/{id}")
    public ResponseEntity<Void> impersona(@PathVariable Long id, HttpSession session) {
        var u = utenteService.visualizzaUtente(id);
        Sessione s = (Sessione) session.getAttribute(GestionaleService.SESSIONE_KEY);
        if (s == null) s = new Sessione();
        s.setUtente(u);
        session.setAttribute(GestionaleService.SESSIONE_KEY, s);
        return ResponseEntity.noContent().build();
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
