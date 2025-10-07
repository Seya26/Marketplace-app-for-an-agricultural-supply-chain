package it.cs.unicam.ids.filiera.demo.dtos.eventoDto;

import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Evento;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Invito;

import static it.cs.unicam.ids.filiera.demo.factory.FactoryUtente.ruoloOf;

public class InvitoMapper {
    public static InvitoDTO toDTO(Invito inv) {
        Evento e = inv.getEvento();
        UtenteVerificato u = inv.getInvitato();

        return new InvitoDTO(
                inv.getId(),
                e.getId(),
                e.getTitolo(),
                u.getId(),
                u.getNome(),
                u.getCognome(),
                ruoloOf(u),
                inv.getStato(),
                inv.getMessaggio()
        );
    }


    public static Invito toEntity(InvitoDTO dto, Evento evento, UtenteVerificato invitato) {
        throw new UnsupportedOperationException("Not implemented yet");
    }




}
