package it.cs.unicam.ids.filiera.demo.dtos;

import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;

public class UtenteMapper {
    public static UtenteDTO toDto(UtenteVerificato u) {
        if (u == null) return null;
        String ruoloStr = (u.getRuolo() != null)
                ? u.getRuolo().name()
                : u.getClass().getSimpleName();
        return new UtenteDTO(
                u.getId(),
                u.getNome(),
                u.getCognome(),
                u.getEmail(),
                ruoloStr,
                u.isVerificato() ? "APPROVATO" : "DA APPROVARE"
        );
    }
}

