package it.cs.unicam.ids.filiera.demo.factory;

import it.cs.unicam.ids.filiera.demo.entity.*;

import java.util.Map;

public final class FactoryUtente {

    private static final Map<Ruolo, CreaUtente> factoryMap = Map.of(
            Ruolo.PRODUTTORE, new FactoryProduttore(),
            Ruolo.TRASFORMATORE, new FactoryTrasformatore(),
            Ruolo.DISTRIBUTORE, new FactoryDistributore(),
            Ruolo.ACQUIRENTE, new FactoryAcquirente(),
            Ruolo.ANIMATORE, new FactoryAnimatore()
    );

    private FactoryUtente() {} // utility class

    public static UtenteVerificato createUser(Ruolo ruolo, String nome, String cognome, String email, String password, String codiceFiscale) {
        CreaUtente factory = factoryMap.get(ruolo);
        if (factory == null)
            throw new IllegalArgumentException("Ruolo non supportato: " + ruolo);
        return factory.crea(nome, cognome, email, password, codiceFiscale);
    }

    public static String ruoloOf(UtenteVerificato u) {
        if (u.getRuolo() != null) return u.getRuolo().name();
        if (u instanceof Produttore) return "PRODUTTORE";
        if (u instanceof Trasformatore) return "TRASFORMATORE";
        if (u instanceof Distributore) return "DISTRIBUTORE";
        if (u instanceof Animatore) return "ANIMATORE";
        if (u instanceof Acquirente) return "ACQUIRENTE";
        return null;
    }
}
