package it.cs.unicam.ids.filiera.demo.dtos.eventoDto;

import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import it.cs.unicam.ids.filiera.demo.entity.eventi.Evento;

import java.util.List;

public class EventoMapper {

    public static EventoDTO toDto(Evento e) {
        if (e == null) return null;

        boolean illimitato = (e.getCapienzaMax() <= 0);

        Integer capienzaMax = illimitato
                ? null
                : e.getCapienzaMax();

        List<UtenteVerificato> partecipanti = e.getPartecipanti();
        int partecipantiCount = (partecipanti == null)
                ? 0
                : partecipanti.size();

        List<Long> partecipantiIds = (partecipanti == null)
                ? List.of()
                : partecipanti.stream()
                .map(UtenteVerificato::getId)
                .toList();

        Long creatoreId = (e.getCreatore() != null)
                ? e.getCreatore().getId()
                : null;

        // posti rimanenti: null se illimitato
        Integer postiRimanenti = illimitato
                ? null
                : Math.max(0, e.getCapienzaMax() - partecipantiCount);

        return new EventoDTO(
                e.getId(),
                e.getTitolo(),
                e.getDescrizione(),
                e.getLuogo(),
                e.getDataInizio(),
                e.getDataFine(),
                illimitato,
                capienzaMax,       // null se illimitato
                postiRimanenti,
                partecipantiCount,
                creatoreId,
                partecipantiIds
        );
    }

    public static Evento toEntity(EventoDTO dto) {
        if (dto == null) return null;
        Evento e = new Evento();
        applicaModifica(e, dto);
        return e;
    }

    /** update dei campi modificabili (creatore NON viene toccato) */
    public static void applicaModifica(Evento e, EventoDTO dto) {
        if (e == null || dto == null) return;

        e.setTitolo(dto.titolo());
        e.setDescrizione(dto.descrizione());
        e.setLuogo(dto.luogo());
        e.setDataInizio(dto.dataInizio());
        e.setDataFine(dto.dataFine());

        if (dto.illimitato()) {
            e.setCapienzaMax(-1);
        } else {
            Integer x = dto.capienzaMax();
            if (x == null || x <= 0) {
                throw new IllegalArgumentException("Se l'evento ha posti limitati, capienza massima deve essere > 0");
            }
            e.setCapienzaMax(x);
        }
    }
}
